package no.nav.soknad.arkivering.soknadsfillager.supervision

import io.prometheus.client.Counter
import io.prometheus.client.Summary

object Metrics {

	private const val SOKNAD_NAMESPACE = "soknadinnsending"
	private const val SUB_SYSTEM = "soknadsfillager"
	private const val NAME = "antall_filer"
	private const val LABEL = "operation"
	private const val HELP = "Number of files"
	private const val ERROR_NAME = "antall_feil"
	private const val ERROR_HELP = "Number of errors"
	private const val SUMMARY = "fil_info"
	private const val SUMMARY_HELP = "File size distribution"
	private const val SUMMARY_LATENCY = "latency_file_operations"
	private const val SUMMARY_LATENCY_HELP = "File size distribution"

	private val filCounter = registerCounter(NAME, HELP, LABEL)
	private val errorCounter = registerCounter(ERROR_NAME, ERROR_HELP, LABEL)

	private val filSizeSummary = registerSummary(SUMMARY, SUMMARY_HELP, LABEL)
	private val filLatencySummary = registerSummary(SUMMARY_LATENCY, SUMMARY_LATENCY_HELP, LABEL)

	private fun registerCounter(name: String, help: String, label: String): Counter =
		Counter
			.build()
			.namespace(SOKNAD_NAMESPACE)
			.subsystem(SUB_SYSTEM)
			.name(name)
			.help(help)
			.labelNames(label)
			.register()

	private fun registerSummary(name: String, help: String, label: String): Summary =
		Summary
			.build()
			.namespace(SOKNAD_NAMESPACE)
			.subsystem(SUB_SYSTEM)
			.name(name)
			.help(help)
			.labelNames(label)
			.quantile(0.5, 0.05)
			.quantile(0.9, 0.01)
			.quantile(0.99, 0.001)
			.register()


	fun filCounterInc(operation: String) {
		filCounter.labels(operation).inc()
	}

	fun filCounterGet(operation: String) = filCounter.labels(operation).get()

	fun errorCounterInc(operation: String) {
		errorCounter.labels(operation).inc()
	}

	fun errorCounterGet(operation: String) = errorCounter.labels(operation).get()

	fun filSummarySetSize(operation: String, size: Double?) {
		if (size != null) filSizeSummary.labels(operation).observe(size)
	}

	fun filSummarySizeGet(operation: String): Summary.Child.Value = filSizeSummary.labels(operation).get()

	fun filSummaryLatencyStart(operation: String): Summary.Timer = filLatencySummary.labels(operation).startTimer()

	fun filSummaryLatencyEnd(start: Summary.Timer) {
		start.observeDuration()
	}

	fun filSummaryLatencyGet(operation: String): Summary.Child.Value = filLatencySummary.labels(operation).get()
}
