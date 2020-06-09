package no.nav.soknad.arkivering.soknadsfillager.henvendelse

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import no.nav.soknad.arkivering.soknadsfillager.config.AppConfiguration
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.util.Base64Utils
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Mono
import reactor.netty.Connection
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.TcpClient
import java.util.function.Consumer
import kotlin.text.Charsets.UTF_8

@Service
class HenvendelseClient(private val appConfig: AppConfiguration): HenvendelseInterface {

	private val logger = LoggerFactory.getLogger(javaClass)
	private val config = appConfig.restConfig
	private val webClient = defaultWebClient()

	override fun deleteFile(uuid: String): Boolean {
		val resultat =  webClient.delete().uri("/$uuid")
			.retrieve()
			.onStatus({ obj: HttpStatus -> obj.is4xxClientError }) { response ->
				logger.warn("Fikk 4xx feil ved forsøk på å slette uuid=$uuid")
				Mono.error(RuntimeException("4xx"))
			}
			.onStatus({ obj: HttpStatus -> obj.is5xxServerError }) { response ->
				logger.warn("Fikk 5xx feil ved forsøk på å slette uuid=$uuid")
				Mono.error(RuntimeException("5xx"))
			}
			.bodyToMono(Map::class.java)
			.block()

		return true
	}

	override fun fetchFile(uuid: String): ByteArray? {
		logger.info("Henter fil med $uuid fra henvendelse")
		return webClient.get().uri("/hent/$uuid")
/*
			.header(HttpHeaders.AUTHORIZATION,"Basic " + Base64Utils
				.encodeToString((config.username + ":" + config.password).toByteArray(UTF_8)))
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
*/
			.retrieve()
			.onStatus({ obj: HttpStatus -> obj.is4xxClientError }) { response ->
				logger.warn("Fikk 4xx feil ved forsøk på å hente uuid=$uuid. " )
				val status = response.rawStatusCode()
				logger.info("status code= $status")
				Mono.error(RuntimeException("4xx"))
			}
			.onStatus({ obj: HttpStatus -> obj.is5xxServerError }) { response ->
				logger.warn("Fikk 5xx feil ved forsøk på å hente uuid=$uuid")
				Mono.error(RuntimeException("5xx"))
			}
			.bodyToMono(ByteArray::class.java)
			.block()
	}

	private fun defaultWebClient(): WebClient {
		val tcpClient = TcpClient.create()
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
			.doOnConnected { connection: Connection ->
				connection.addHandlerLast(ReadTimeoutHandler(2))
					.addHandlerLast(WriteTimeoutHandler(2))
			}

		//val headers = createHeaders(config.username, config.password)
		return WebClient.builder()
			.baseUrl(config.url)
			.clientConnector(ReactorClientHttpConnector(HttpClient.from(tcpClient)))
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.filter(ExchangeFilterFunctions.basicAuthentication(config.username, config.password))
			.filter(logRequest())
			.build()
	}

	private fun createHeaders(username: String, password: String): HttpHeaders {
		return object : HttpHeaders() {
			init {
				setBasicAuth(username, password)
				set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
			}
		}
	}

	private fun logRequest(): ExchangeFilterFunction {
		return ExchangeFilterFunction { clientRequest: ClientRequest, next: ExchangeFunction ->
			logger.info("Request: {} {}", clientRequest.method(), clientRequest.url())
			clientRequest.headers()
				.forEach { name: String?, values: List<String?> -> values.forEach(Consumer { value: String? -> logger.info("{}={}", name, value) }) }
			next.exchange(clientRequest)
		}
	}

}
