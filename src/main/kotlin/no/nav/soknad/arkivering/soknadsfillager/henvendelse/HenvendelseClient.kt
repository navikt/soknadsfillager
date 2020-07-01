package no.nav.soknad.arkivering.soknadsfillager.henvendelse

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import no.nav.soknad.arkivering.soknadsfillager.config.AppConfiguration
import org.apache.tomcat.util.codec.binary.Base64
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.Connection
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.TcpClient

@Service
class HenvendelseClient(private val appConfig: AppConfiguration) : HenvendelseInterface {

	private val logger = LoggerFactory.getLogger(javaClass)
	private val config = appConfig.restConfig
	private val webClient = defaultWebClient()

	override fun deleteFile(uuid: String): Boolean {
		val resultat = webClient.delete().uri("/$uuid")
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
		return webClient.get().uri("/hent/$uuid")
			.retrieve()
			.onStatus({ obj: HttpStatus -> obj.is4xxClientError }) { response ->
				logger.warn("Fikk 4xx feil ved forsøk på å hente uuid=$uuid")
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

		return WebClient.builder()
			.baseUrl(config.url)
			.clientConnector(ReactorClientHttpConnector(HttpClient.from(tcpClient)))
			.defaultHeaders({ createHeaders(config.username, config.sharedPassword) })
			.build()
	}

	private fun createHeaders(username: String, password: String): HttpHeaders {
		return object : HttpHeaders() {
			init {
				val auth = "$username:$password"
				val encodedAuth: ByteArray = Base64.encodeBase64(auth.toByteArray())
				val authHeader = "Basic " + String(encodedAuth)
				set("Authorization", authHeader)
				set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
			}
		}
	}

}
