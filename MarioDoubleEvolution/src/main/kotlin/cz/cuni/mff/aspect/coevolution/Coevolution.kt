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
    fun evolve(controllerEvolution: ControllerEvolution,
               generatorEvolution: LevelGeneratorEvolution,
               initialController: MarioController,
               initialLevelGenerator: LevelGenerator,
               generations: Int = DEFAULT_GENERATIONS_NUMBER,
               repeatGeneratorsCount: Int = DEFAULT_REPEAT_GENERATORS_COUNT,
               storagePath: String
    ): CoevolutionResult {
        var currentController: MarioController = initialController
        val generatorsHistory: SlidingWindow<LevelGenerator> = SlidingWindow(repeatGeneratorsCount)
        generatorsHistory.push(initialLevelGenerator)
        var latestGenerator: LevelGenerator = initialLevelGenerator

        val startTime = System.currentTimeMillis()
        for (generation in (0 until generations)) {
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
    }
}
