package no.nav.soknad.arkivering.soknadsfillager.supervision

import io.prometheus.client.CollectorRegistry
import io.prometheus.client.Counter
import io.prometheus.client.Histogram
import io.prometheus.client.Summary
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Configuration
class FileMetrics(private val registry: CollectorRegistry) {

	private val SOKNAD_NAMESPACE = "soknadinnsending"
	private val APP = "soknadsfillager"
	private val APP_LABEL = "app"
	private val NAME = "antall_filer"
	private val LABEL = "operation"
	private val HELP = "Number of files"
	private val ERROR_NAME = "antall_feil"
	private val ERROR_HELP = "Number of errors"
	private val SUMMARY = "fil_info"
	private val SUMMARY_HELP = "File size distribution"
	private val SUMMARY_LATENCY = "latency_file_operations"
	private val SUMMARY_LATENCY_HELP = "File size distribution"
	private val SUMMARY_HISTOGRAM = "fil_size_histogram"
	private val SUMMARY_HISTOGRAM_HELP = "File size distribution"

	private val filCounter = registerCounter(NAME, HELP, LABEL)
	private val errorCounter = registerCounter(ERROR_NAME, ERROR_HELP, LABEL)

	private val filSizeSummary = registerSummary(SUMMARY, SUMMARY_HELP, LABEL)
	private val filLatencySummary = registerSummary(SUMMARY_LATENCY, SUMMARY_LATENCY_HELP, LABEL)

	private val filSizeHistogram = registerHistogram(SUMMARY_HISTOGRAM, SUMMARY_HISTOGRAM_HELP, LABEL)

	private fun registerCounter(name: String, help: String, label: String): Counter =
		Counter
			.build()
			.namespace(SOKNAD_NAMESPACE)
			.name(name)
			.help(help)
			.labelNames(label, APP_LABEL)
			.register(registry)

	private fun registerSummary(name: String, help: String, label: String): Summary =
		Summary
			.build()
			.namespace(SOKNAD_NAMESPACE)
			.name(name)
			.help(help)
			.labelNames(label, APP_LABEL)
			.quantile(0.5, 0.05)
			.quantile(0.9, 0.01)
			.quantile(0.99, 0.001)
			.register(registry)

	private fun registerHistogram(name: String, help: String, label: String): Histogram =
		Histogram
			.build()
			.namespace(SOKNAD_NAMESPACE)
			.name(name)
			.help(help)
			.labelNames(label, APP_LABEL)
			.buckets(4096.0, 16384.0, 65536.0, 262144.0, 1048576.0, 4194304.0, 16777216.0, 67108864.0, 268435456.0)
			.register(registry)


	fun filCounterInc(operation: String) {
		filCounter.labels(operation, APP).inc()
	}

	fun filCounterGet(operation: String) = filCounter.labels(operation, APP)?.get()

	fun errorCounterInc(operation: String) {
		errorCounter.labels(operation, APP).inc()
	}

	fun errorCounterGet(operation: String) = errorCounter.labels(operation, APP)?.get()

	fun filSummarySetSize(operation: String, size: Double?) {
		if (size != null) filSizeSummary.labels(operation, APP).observe(size)
	}
	fun filSummarySizeGet(operation: String): Summary.Child.Value = filSizeSummary.labels(operation, APP).get()

	fun filHistogramSetSize(operation: String, size: Double?) {
		if (size != null) filSizeHistogram.labels(operation, APP).observe(size)
	}
	fun filHistogramGetSize(operation: String): Histogram.Child.Value = filSizeHistogram.labels(operation, APP).get()

	fun filSummaryLatencyStart(operation: String): Summary.Timer = filLatencySummary.labels(operation, APP).startTimer()
	fun filSummaryLatencyEnd(start: Summary.Timer) {
		start.observeDuration()
	}

	fun filSummaryLatencyGet(operation: String): Summary.Child.Value = filLatencySummary.labels(operation, APP).get()

	fun unregister() {
		registry.unregister(filCounter)
		registry.unregister(errorCounter)
		registry.unregister(filSizeSummary)
		registry.unregister(filLatencySummary)
		registry.unregister(filSizeHistogram)
	}
}
