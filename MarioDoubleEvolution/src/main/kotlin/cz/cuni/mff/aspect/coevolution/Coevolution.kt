package cz.cuni.mff.aspect.coevolution

import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorEvolution
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.storage.ObjectStorage
import cz.cuni.mff.aspect.utils.DeepCopy
import cz.cuni.mff.aspect.utils.SlidingWindow
import cz.cuni.mff.aspect.visualisation.charts.evolution.CoevolutionLineChart
import cz.cuni.mff.aspect.visualisation.charts.evolution.EvolutionLineChart
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * Implementation of coevolution of AI and level generators algorithm.
 */
class Coevolution {

    /**
     * Starts the coevolution using given settings.
     *
     * @param coevolutionSettings coevolution settings.
     * @see CoevolutionSettings for mor info about coevolution settings.
     */
    fun startEvolution(coevolutionSettings: CoevolutionSettings): CoevolutionResult {
        return this.evolve(coevolutionSettings, 0)
    }

    /**
     * Continues the coevolution using data from given path. This is useful if the coevolution happens to crash
     * for any reason.
     *
     * @param coevolutionSettings coevolution settings with which the coevolution was started.
     */
    fun continueCoevolution(coevolutionSettings: CoevolutionSettings): CoevolutionResult {
        val lastFinishedGenerationNumber = this.getLastFinishedGenerationNumber(coevolutionSettings.storagePath)
        println("Restarting coevolution from generation: $lastFinishedGenerationNumber")

        if (lastFinishedGenerationNumber == 0)
            return this.evolve(coevolutionSettings, 0)

        val updatedCoevolutionSettings = coevolutionSettings.copy(
            initialController = ObjectStorage.load("${coevolutionSettings.storagePath}/ai_$lastFinishedGenerationNumber.ai"),
            initialLevelGenerator = ObjectStorage.load("${coevolutionSettings.storagePath}/lg_$lastFinishedGenerationNumber.lg")
        )

        val aiChart = EvolutionLineChart.loadFromFile("${coevolutionSettings.storagePath}/ai.svg.dat")
        val lgChart = EvolutionLineChart.loadFromFile("${coevolutionSettings.storagePath}/lg.svg.dat")

        updatedCoevolutionSettings.controllerEvolution.chart = aiChart
        updatedCoevolutionSettings.generatorEvolution.chart = lgChart

        return this.evolve(updatedCoevolutionSettings, lastFinishedGenerationNumber)
    }

    private fun evolve(coevolutionSettings: CoevolutionSettings, startGenerationIndex: Int = 0): CoevolutionResult {
        var currentController: MarioController = coevolutionSettings.initialController
        val generatorsHistory: SlidingWindow<LevelGenerator> = this.createGeneratorsHistory(coevolutionSettings, startGenerationIndex)
        var latestGenerator: LevelGenerator = coevolutionSettings.initialLevelGenerator

        val startTime = System.currentTimeMillis()
        for (generation in (startGenerationIndex until coevolutionSettings.generations)) {
            println(" -- COEVOLUTION GENERATION ${generation + 1} -- ")

            println("(${this.timeString(System.currentTimeMillis() - startTime)}) controller evo")
            currentController = coevolutionSettings.controllerEvolution.continueEvolution(
                currentController,
                generatorsHistory.getAll()
            )

            println("(${this.timeString(System.currentTimeMillis() - startTime)}) level generator evo")
            val agentFactory = { MarioAgent(DeepCopy.copy(currentController)) }
            latestGenerator = coevolutionSettings.generatorEvolution.evolve(agentFactory)

            ObjectStorage.store("${coevolutionSettings.storagePath}/ai_${generation + 1}.ai", currentController)
            ObjectStorage.store("${coevolutionSettings.storagePath}/lg_${generation + 1}.lg", latestGenerator)

            generatorsHistory.push(latestGenerator)
            storeCharts(coevolutionSettings.controllerEvolution, coevolutionSettings.generatorEvolution, coevolutionSettings.storagePath)
        }

        return CoevolutionResult(currentController, latestGenerator)
    }

    private fun storeCharts(controllerEvolution: ControllerEvolution, levelGeneratorEvolution: LevelGeneratorEvolution, storagePath: String) {
        val controllerChart = controllerEvolution.chart
        val levelGeneratorChart = levelGeneratorEvolution.chart
        val coevolutionChart =
            CoevolutionLineChart(
                controllerChart,
                levelGeneratorChart,
                "Coevolution"
            )

        controllerChart.store("$storagePath/ai.svg")
        levelGeneratorChart.store("$storagePath/lg.svg")

        coevolutionChart.storeChart("$storagePath/coev.svg")
    }

    private fun getLastFinishedGenerationNumber(storagePath: String): Int {
        val storageDirectory = File(storagePath)
        if (!storageDirectory.exists()) throw IllegalArgumentException("Can't restore the given coevolution, because directory $storagePath does not exist")
        if (!storageDirectory.isDirectory) throw IllegalArgumentException("Can't restore the given coevolution, because file $storagePath is not a directory")

        val filesInDirectory = storageDirectory.listFiles() ?: throw IllegalArgumentException("Can't restore the given coevolution, because directory $storagePath is empty")

        val lgFilePattern = Pattern.compile("lg_([0-9]+)\\.lg")

        return filesInDirectory
            .filter { it.isFile }
            .map { it.name }
            .filter { lgFilePattern.matcher(it).matches() }
            .map { val matcher = lgFilePattern.matcher(it); matcher.matches(); matcher.group(1).toInt() }
            .max() ?: 0
    }

    private fun createGeneratorsHistory(coevolutionSettings: CoevolutionSettings, alreadyCreatedGeneratorsCount: Int): SlidingWindow<LevelGenerator> {
        val generators = SlidingWindow<LevelGenerator>(coevolutionSettings.repeatGeneratorsCount)
        generators.push(coevolutionSettings.initialLevelGenerator)

        repeat(alreadyCreatedGeneratorsCount) {
            val generator = ObjectStorage.load<LevelGenerator>("${coevolutionSettings.storagePath}/lg_${it + 1}.lg")
            generators.push(generator)
        }

        return generators
    }

    private fun timeString(currentTimeMillis: Long): String =
        String.format("%02d min, %02d sec",
            TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis),
            TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis))
        )
}
