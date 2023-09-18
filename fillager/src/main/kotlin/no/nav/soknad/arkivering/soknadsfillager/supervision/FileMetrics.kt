package no.nav.soknad.arkivering.soknadsfillager.supervision

import io.prometheus.client.*
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Configuration
class FileMetrics(private val registry: CollectorRegistry) {

	private val soknadNamespace = "soknadinnsending"
	private val app = "soknadsfillager"
	private val appLabel = "app"
	private val name = "antall_filer"
	private val label = "operation"
	private val help = "Number of files"
	private val errorName = "antall_feil"
	private val errorHelp = "Number of errors"
	private val summary = "fil_info"
	private val summaryHelp = "File size distribution"
	private val summaryLatency = "latency_file_operations"
	private val summaryLatencyHelp = "File size distribution"
	private val summaryHistogram = "fil_size_histogram"
	private val summaryHistogramHelp = "File size distribution"
	private val latencyHistogram = "file_latency_histogram"
	private val latencyHistogramHelp = "File latency distribution"
	private val filenumberGauge = "antall_filer_i_db_gauge"
	private val filenumberGaugeHelp = "Number of rows with documents in the database"
	private val databaseSizeGauge = "database_size"
	private val databaseSizeGaugeHelp = "Database size"

	private val filCounter = registerCounter(name, help, label)
	private val errorCounter = registerCounter(errorName, errorHelp, label)

	private val filSizeSummary = registerSummary(summary, summaryHelp, label)
	private val filLatencySummary = registerSummary(summaryLatency, summaryLatencyHelp, label)

	private val filSizeHistogram = registerSizeHistogram(summaryHistogram, summaryHistogramHelp, label)
	private val fileLatencyHistogram = registerLatencyHistogram(latencyHistogram, latencyHistogramHelp, label)

	private val filesInDb = registerGauge(filenumberGauge, filenumberGaugeHelp, label)
	private val databaseSize = registerGauge(databaseSizeGauge, databaseSizeGaugeHelp, label)

	private fun registerGauge(name: String, help: String, label: String): Gauge =
		Gauge
			.build()
			.namespace(soknadNamespace)
			.name(name)
			.help(help)
			.labelNames(label, appLabel)
			.register(registry)

	private fun registerCounter(name: String, help: String, label: String): Counter =
		Counter
			.build()
			.namespace(soknadNamespace)
			.name(name)
			.help(help)
			.labelNames(label, appLabel)
			.register(registry)

	private fun registerSummary(name: String, help: String, label: String): Summary =
		Summary
			.build()
			.namespace(soknadNamespace)
			.name(name)
			.help(help)
			.labelNames(label, appLabel)
			.quantile(0.5, 0.05)
			.quantile(0.9, 0.01)
			.quantile(0.99, 0.001)
			.register(registry)

	private fun registerSizeHistogram(name: String, help: String, label: String): Histogram =
		Histogram
			.build()
			.namespace(soknadNamespace)
			.name(name)
			.help(help)
			.labelNames(label, appLabel)
			.buckets(4.0, 16.0, 64.0, 256.0, 1024.0, 4096.0, 16384.0, 65536.0, 262144.0)
			.register(registry)

	private fun registerLatencyHistogram(name: String, help: String, label: String): Histogram =
		Histogram
			.build()
			.namespace(soknadNamespace)
			.name(name)
			.help(help)
			.labelNames(label, appLabel)
			.buckets(100.0, 200.0, 400.0, 1000.0, 2000.0, 4000.0, 15000.0, 30000.0)
			.register(registry)

	fun filCounterInc(operation: String) = filCounter.labels(operation, app).inc()
	fun filCounterGet(operation: String) = filCounter.labels(operation, app)?.get()

	fun filesInDbGaugeSet(number: Long) {
		filesInDb.labels("FIND", app).set(number.toDouble())
	}

	fun filesInDbGaugeGet() = filesInDb.labels("FIND", app)?.get()

	fun databaseSizeSet(number: Long) = databaseSize.labels("dbsize", app).set(number.toDouble())
	fun databaseSizeGet() = databaseSize.labels("dbsize", app)?.get()

	fun errorCounterInc(operation: String) {
		errorCounter.labels(operation, app).inc()
	}

	fun errorCounterGet(operation: String) = errorCounter.labels(operation, app)?.get()

	fun filSummarySetSize(operation: String, size: Double?) {
		if (size != null) filSizeSummary.labels(operation, app).observe(size / 1024)
	}

	fun filHistogramSetSize(operation: String, size: Double?) {
		if (size != null) filSizeHistogram.labels(operation, app).observe(size / 1024)
	}

	fun fileHistogramLatencyStart(operation: String): Histogram.Timer =
		fileLatencyHistogram.labels(operation, app).startTimer()

	fun fileHistogramLatencyEnd(timer: Histogram.Timer) {
		timer.observeDuration()
	}

	fun filSummaryLatencyStart(operation: String): Summary.Timer = filLatencySummary.labels(operation, app).startTimer()
	fun filSummaryLatencyEnd(start: Summary.Timer) {
		start.observeDuration()
	}

	fun filSummaryLatencyGet(operation: String): Summary.Child.Value = filLatencySummary.labels(operation, app).get()
}
