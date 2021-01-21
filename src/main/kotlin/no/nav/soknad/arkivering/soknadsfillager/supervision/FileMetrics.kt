package no.nav.soknad.arkivering.soknadsfillager.supervision

import io.prometheus.client.*
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
	private val LATENCY_HISTOGRAM = "file_latency_histogram"
	private val LATENCY_HISTOGRAM_HELP = "File latency distribution"
	private val FILENUMBER_COUNTER = "antall_filer_i_db"
	private val FILENUMBER_COUNTER_HELP = "Number of files in the database"

	private val filCounter = registerCounter(NAME, HELP, LABEL)
	private val errorCounter = registerCounter(ERROR_NAME, ERROR_HELP, LABEL)

	private val filSizeSummary = registerSummary(SUMMARY, SUMMARY_HELP, LABEL)
	private val filLatencySummary = registerSummary(SUMMARY_LATENCY, SUMMARY_LATENCY_HELP, LABEL)

	private val filSizeHistogram = registerSizeHistogram(SUMMARY_HISTOGRAM, SUMMARY_HISTOGRAM_HELP, LABEL)
	private val fileLatencyHistogram = registerLatencyHistogram(LATENCY_HISTOGRAM, LATENCY_HISTOGRAM_HELP, LABEL)

	private val filesInDb = registerGauge(FILENUMBER_COUNTER, FILENUMBER_COUNTER_HELP, LABEL)

	private fun registerGauge(name: String, help: String, label: String): Gauge =
		Gauge
			.build()
			.namespace(SOKNAD_NAMESPACE)
			.name(name)
			.help(help)
			.labelNames(label, APP_LABEL)
			.register(registry)

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

	private fun registerSizeHistogram(name: String, help: String, label: String): Histogram =
		Histogram
			.build()
			.namespace(SOKNAD_NAMESPACE)
			.name(name)
			.help(help)
			.labelNames(label, APP_LABEL)
			.buckets(4.0, 16.0, 64.0, 256.0, 1024.0, 4096.0, 16384.0, 65536.0, 262144.0)
			.register(registry)

	private fun registerLatencyHistogram(name: String, help: String, label: String): Histogram =
		Histogram
			.build()
			.namespace(SOKNAD_NAMESPACE)
			.name(name)
			.help(help)
			.labelNames(label, APP_LABEL)
			.buckets(100.0, 200.0, 400.0, 1000.0, 2000.0, 4000.0, 15000.0, 30000.0)
			.register(registry)

	fun filCounterInc(operation: String) = filCounter.labels(operation, APP).inc()
	fun filCounterGet(operation: String) = filCounter.labels(operation, APP)?.get()

	fun filesInDbCounterInc(number: Long?) = {if (number != null) filesInDb.labels("FIND", APP).set(number.toDouble())}
	fun filesInDbCounterGet() = filesInDb.labels("FIND", APP)?.get()

	fun errorCounterInc(operation: String) {
		errorCounter.labels(operation, APP).inc()
	}

	fun errorCounterGet(operation: String) = errorCounter.labels(operation, APP)?.get()

	fun filSummarySetSize(operation: String, size: Double?) {
		if (size != null) filSizeSummary.labels(operation, APP).observe(size/1024)
	}
	fun filSummarySizeGet(operation: String): Summary.Child.Value = filSizeSummary.labels(operation, APP).get()

	fun filHistogramSetSize(operation: String, size: Double?) {
		if (size != null) filSizeHistogram.labels(operation, APP).observe(size/1024)
	}
	fun filHistogramGetSize(operation: String): Histogram.Child.Value = filSizeHistogram.labels(operation, APP).get()

	fun fileHistogramLatencyStart(operation: String): Histogram.Timer =  fileLatencyHistogram.labels(operation, APP).startTimer()
	fun fileHistogramLatencyEnd(timer: Histogram.Timer) {timer.observeDuration()}
	fun filHistogramGetLatency(operation: String): Histogram.Child.Value = fileLatencyHistogram.labels(operation, APP).get()

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
		registry.unregister(fileLatencyHistogram)
	}
}
