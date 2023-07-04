package no.nav.soknad.arkivering.soknadsfillager

import io.prometheus.client.CollectorRegistry
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ActiveProfiles("test")
@SpringBootTest()
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
class ApplicationTest {
	@MockBean
	lateinit var collectorRegistry: CollectorRegistry

}
