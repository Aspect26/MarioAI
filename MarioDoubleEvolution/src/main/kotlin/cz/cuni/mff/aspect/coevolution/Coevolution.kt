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
import java.util.concurrent.TimeUnit

/**
 * Implementation of coevolution of AI and level generators algorithm.
 */
class Coevolution {

    /**
     * Starts the coevolution.
     *
     * @param controllerEvolution the evolution algorithm for controller
     * @param generatorEvolution the evolution algorithm for level generator
     * @param initialController initial controller, which is being evolved by the algorithm
     * @param initialLevelGenerator initial level generator used in the first generation of the coevolution
     * @param generations number of coevolution generations
     * @param repeatGeneratorsCount number of level generators, on which the controller evolution should evaluate the controllers
     * @param storagePath path, where the results of the coevolution are to be stored
     */
    fun startEvolution(controllerEvolution: ControllerEvolution,
                       generatorEvolution: LevelGeneratorEvolution,
                       initialController: MarioController,
                       initialLevelGenerator: LevelGenerator,
                       generations: Int = DEFAULT_GENERATIONS_NUMBER,
                       repeatGeneratorsCount: Int = DEFAULT_REPEAT_GENERATORS_COUNT,
                       storagePath: String
    ): CoevolutionResult {
        return this.evolve(controllerEvolution, generatorEvolution, initialController, initialLevelGenerator,
            generations, repeatGeneratorsCount, storagePath, 0, true)
    }

    /**
     * Continues the coevolution using data from given path. This is useful if the coevolution happens to crash
     * for any reason.
     *
     * @param storagePath path to the coevolution data. This is the same as `path` parameter from [startEvolution] function.
     */
    fun continueCoevolution(storagePath: String): CoevolutionResult {
        val controllerEvolution = ObjectStorage.load<ControllerEvolution>("$storagePath/$CONTROLLER_EVOLUTION_FILE")
        val generatorEvolution = ObjectStorage.load<LevelGeneratorEvolution>("$storagePath/$GENERATOR_EVOLUTION_FILE")

        val lastFinishedGenerationIndex = if (ObjectStorage.exists("$storagePath/$LAST_FINISHED_GENERATION_FILE"))
            ObjectStorage.load("$storagePath/$LAST_FINISHED_GENERATION_FILE") else -1

        val initialController: MarioController = if (lastFinishedGenerationIndex == -1)
            ObjectStorage.load("$storagePath/$INITIAL_CONTROLLER_FILE") else
            ObjectStorage.load("$storagePath/ai_${lastFinishedGenerationIndex + 1}.ai")

        val initialLevelGenerator: LevelGenerator = if (lastFinishedGenerationIndex == -1)
            ObjectStorage.load("$storagePath/$INITIAL_GENERATOR_FILE") else
            ObjectStorage.load("$storagePath/lg_${lastFinishedGenerationIndex + 1}.lg")

        val generationsCount = ObjectStorage.load<Int>("$storagePath/$GENERATIONS_COUNT_FILE")
        val repeatGeneratorsCount = ObjectStorage.load<Int>("$storagePath/$REPEAT_GENERATORS_COUNT_FILE")

        return this.evolve(controllerEvolution, generatorEvolution, initialController, initialLevelGenerator,
            generationsCount, repeatGeneratorsCount, storagePath, lastFinishedGenerationIndex, false)
    }

    // TODO: refactor me
    private fun evolve(
        controllerEvolution: ControllerEvolution,
        generatorEvolution: LevelGeneratorEvolution,
        initialController: MarioController,
        initialLevelGenerator: LevelGenerator,
        generations: Int = DEFAULT_GENERATIONS_NUMBER,
        repeatGeneratorsCount: Int = DEFAULT_REPEAT_GENERATORS_COUNT,
        storagePath: String,
        firstGenerationIndex: Int = 0,
        storeInitial: Boolean = true
    ): CoevolutionResult {
        var currentController: MarioController = initialController
        val generatorsHistory: SlidingWindow<LevelGenerator> = SlidingWindow(repeatGeneratorsCount)
        generatorsHistory.push(initialLevelGenerator)
        var latestGenerator: LevelGenerator = initialLevelGenerator

        if (storeInitial) {
            ObjectStorage.store("$storagePath/$INITIAL_CONTROLLER_FILE", initialController)
            ObjectStorage.store("$storagePath/$INITIAL_GENERATOR_FILE", initialLevelGenerator)
            ObjectStorage.store("$storagePath/$GENERATIONS_COUNT_FILE", generations)
            ObjectStorage.store("$storagePath/$REPEAT_GENERATORS_COUNT_FILE", repeatGeneratorsCount)
            ObjectStorage.store("$storagePath/$LAST_FINISHED_GENERATION_FILE", 0)
        }

        val startTime = System.currentTimeMillis()
        for (generation in (firstGenerationIndex until generations)) {
            println(" -- COEVOLUTION GENERATION ${generation + 1} -- ")

            println("(${this.timeString(System.currentTimeMillis() - startTime)}) controller evo")
            currentController = controllerEvolution.continueEvolution(
                currentController,
                generatorsHistory.getAll()
            )

            println("(${this.timeString(System.currentTimeMillis() - startTime)}) level generator evo")
            val agentFactory = { MarioAgent(DeepCopy.copy(currentController)) }
            latestGenerator = generatorEvolution.evolve(agentFactory)

            ObjectStorage.store("$storagePath/ai_${generation + 1}.ai", currentController)
            ObjectStorage.store("$storagePath/lg_${generation + 1}.lg", latestGenerator)
            ObjectStorage.store("$storagePath/$CONTROLLER_EVOLUTION_FILE", controllerEvolution)
            ObjectStorage.store("$storagePath/$GENERATOR_EVOLUTION_FILE", generatorEvolution)
            ObjectStorage.store("$storagePath/$LAST_FINISHED_GENERATION_FILE", generation)

            generatorsHistory.push(latestGenerator)
            storeCharts(controllerEvolution, generatorEvolution, storagePath)
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

    private fun timeString(currentTimeMillis: Long): String =
        String.format("%02d min, %02d sec",
            TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis),
            TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis))
        )

    companion object {
        private const val DEFAULT_GENERATIONS_NUMBER: Int = 10
        private const val DEFAULT_REPEAT_GENERATORS_COUNT: Int = 5

        private const val CONTROLLER_EVOLUTION_FILE = "controllerEvolution.dat"
        private const val GENERATOR_EVOLUTION_FILE = "levelGeneratorEvolution.dat"
        private const val LAST_FINISHED_GENERATION_FILE = "lastFinishedGeneration.dat"
        private const val GENERATIONS_COUNT_FILE = "generationsCount.dat"
        private const val INITIAL_CONTROLLER_FILE = "initialController.dat"
        private const val INITIAL_GENERATOR_FILE = "initialGenerator.dat"
        private const val REPEAT_GENERATORS_COUNT_FILE = "repeatGeneratorsCount.dat"
    }
}
