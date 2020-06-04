package cz.cuni.mff.aspect.coevolution

import cz.cuni.mff.aspect.storage.LocalTextFileStorage
import org.joda.time.DateTime

/** Utility class for computing total time spent on different evolutions during one coevolution. */
class CoevolutionTimer {

    private data class EvolutionRun(val startTime: DateTime, val endTime: DateTime)

    private val controllerEvolutionRuns: MutableList<EvolutionRun> = mutableListOf()
    private val generatorsEvolutionRuns: MutableList<EvolutionRun> = mutableListOf()
    private var lastControllerEvolutionStart: DateTime? = null
    private var lastGeneratorsEvolutionStart: DateTime? = null

    /** The total time spent on controller evolution. */
    val totalControllerEvolutionTime: Long get() =
        this.controllerEvolutionRuns.map { it.endTime.millis - it.startTime.millis }.sum() +
                if (this.lastControllerEvolutionStart != null) (DateTime.now().millis - this.lastControllerEvolutionStart!!.millis) else 0L

    /** The total time spent on generators evolution. */
    val totalGeneratorsEvolutionTime: Long get() {
        return this.generatorsEvolutionRuns.map { it.endTime.millis - it.startTime.millis }.sum() +
                if (this.lastGeneratorsEvolutionStart != null) (DateTime.now().millis - this.lastGeneratorsEvolutionStart!!.millis) else 0L
    }

    /** Starts controller evolution timer. */
    fun startControllerEvolution() {
        this.lastControllerEvolutionStart = DateTime.now()
    }

    /** Starts generators evolution timer. */
    fun startGeneratorsEvolution() {
        this.lastGeneratorsEvolutionStart = DateTime.now()
    }

    /**
     * Stops controller evolution timer.
     *
     * @exception [IllegalStateException] if no timer for controller evolution was started.
     */
    fun stopControllerEvolution() {
        val lastEvolutionStart = this.lastControllerEvolutionStart
            ?: throw IllegalStateException("Can't stop evolution timer since it wasn't started")

        this.lastControllerEvolutionStart = null

        val evolutionRun = EvolutionRun(lastEvolutionStart, DateTime.now())
        this.controllerEvolutionRuns.add(evolutionRun)
    }

    /**
     * Stops generators evolution timer.
     *
     * @exception [IllegalStateException] if no timer for generators evolution was started.
     */
    fun stopGeneratorsEvolution() {
        val lastEvolutionStart = this.lastGeneratorsEvolutionStart
            ?: throw IllegalStateException("Can't stop evolution timer since it wasn't started")

        this.lastGeneratorsEvolutionStart = null

        val evolutionRun = EvolutionRun(lastEvolutionStart, DateTime.now())
        this.generatorsEvolutionRuns.add(evolutionRun)
    }

    /**
     * Stores the current state of timers to a file. It can be later used to reload the timers using [loadFromFile].
     *
     * @param filePath path of the file where the data is to be stored.
     */
    fun store(filePath: String) {
        val controllerRunsData = this.controllerEvolutionRuns.joinToString(System.lineSeparator()) { serializeEvolutionRun(it) }
        val generatorsRunsData = this.generatorsEvolutionRuns.joinToString(System.lineSeparator()) { serializeEvolutionRun(it) }
        val serializedData = "$controllerRunsData${System.lineSeparator()}---${System.lineSeparator()}$generatorsRunsData"

        LocalTextFileStorage.storeData(filePath, serializedData)
    }

    companion object {

        /**
         * Loads timers state from given file if they were stored using [store] method.
         *
         * @param filePath path to the file where the data was stored.
         */
        fun loadFromFile(filePath: String): CoevolutionTimer {
            val rawData = LocalTextFileStorage.loadData(filePath).lines()
            var controllerDataFinished = false
            val coevolutionTimer = CoevolutionTimer()

            for (dataLine in rawData) {
                if (dataLine == "---") {
                    controllerDataFinished = true
                    continue
                } else if (controllerDataFinished) {
                    coevolutionTimer.generatorsEvolutionRuns.add(parseEvolutionRun(dataLine))
                } else {
                    coevolutionTimer.controllerEvolutionRuns.add(parseEvolutionRun(dataLine))
                }
            }

            return coevolutionTimer
        }

        private fun serializeEvolutionRun(evolutionRun: EvolutionRun): String =
            "${evolutionRun.startTime.millis}:${evolutionRun.endTime.millis}"

        private fun parseEvolutionRun(data: String): EvolutionRun {
            val parts = data.split(":")
            val startTimeMillis = parts[0].toLong()
            val endTimeMillis = parts[1].toLong()

            return EvolutionRun(DateTime(startTimeMillis), DateTime(endTimeMillis))
        }
    }
}