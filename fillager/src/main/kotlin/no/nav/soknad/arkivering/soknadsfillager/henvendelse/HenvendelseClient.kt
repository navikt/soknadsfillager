package no.nav.soknad.arkivering.soknadsfillager.henvendelse

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import no.nav.soknad.arkivering.soknadsfillager.config.AppConfiguration
import no.nav.soknad.arkivering.soknadsfillager.dto.FilElementDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Mono
import reactor.netty.Connection
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.TcpClient
import java.util.function.Consumer

@Service
class HenvendelseClient(private val appConfig: AppConfiguration) : HenvendelseInterface {

	private val logger = LoggerFactory.getLogger(javaClass)
	private val config = appConfig.restConfig
	private val webClient = defaultWebClient()

	override fun deleteFile(uuid: String): Boolean {
		webClient.delete().uri("/$uuid")
			.retrieve()
			.onStatus({ obj: HttpStatus -> obj.is4xxClientError }) {
				logger.warn("Fikk 4xx feil ved forsøk på å slette uuid=$uuid")
				Mono.error(RuntimeException("4xx"))
			}
			.onStatus({ obj: HttpStatus -> obj.is5xxServerError }) {
				logger.warn("Fikk 5xx feil ved forsøk på å slette uuid=$uuid")
				Mono.error(RuntimeException("5xx"))
			}
			.bodyToMono(Map::class.java)
			.block()

		return true
	}

	override fun fetchFile(uuid: String): FilElementDto? {
		logger.info("Henter fil med $uuid fra henvendelse")
		return webClient.get().uri("/hent/$uuid")
			.retrieve()
			.onStatus({ obj: HttpStatus -> obj.is4xxClientError }) { response ->
				logger.warn("Fikk 4xx feil ved forsøk på å hente uuid=$uuid")
				val status = response.rawStatusCode()
				logger.info("status code= $status")
				Mono.error(RuntimeException("4xx"))
			}
			.onStatus({ obj: HttpStatus -> obj.is5xxServerError }) {
				logger.warn("Fikk 5xx feil ved forsøk på å hente uuid=$uuid")
				Mono.error(RuntimeException("5xx"))
			}
			.bodyToMono(FilElementDto::class.java)
			.block()
	}

	private fun defaultWebClient(): WebClient {
		val tcpClient = TcpClient.create()
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
			.doOnConnected { connection: Connection ->
				connection.addHandlerLast(ReadTimeoutHandler(2))
					.addHandlerLast(WriteTimeoutHandler(2))
			}

		val maxFileSize = appConfig.restConfig.henvendelseMaxFileSize
		val exchangeStrategies = ExchangeStrategies.builder()
			.codecs { configurer: ClientCodecConfigurer -> configurer.defaultCodecs().maxInMemorySize(maxFileSize) }.build()
		return WebClient.builder()
			.baseUrl(config.henvendelseUrl)
			.exchangeStrategies(exchangeStrategies)
			.clientConnector(ReactorClientHttpConnector(HttpClient.from(tcpClient)))
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.filter(ExchangeFilterFunctions.basicAuthentication(config.henvendelseUsername, config.henvendelsePassword))
			.filter(logRequest())
			.build()
	}

	private fun logRequest(): ExchangeFilterFunction {
		return ExchangeFilterFunction { clientRequest: ClientRequest, next: ExchangeFunction ->
			logger.info("Request: {} {}", clientRequest.method(), clientRequest.url())
			clientRequest.headers()
				.forEach { name: String?, values: List<String?> -> values.forEach(Consumer { value: String? -> logger.info("{}={}", name, if (name == "Authorization") "****" else value) }) }
			next.exchange(clientRequest)
		}
	}
}
