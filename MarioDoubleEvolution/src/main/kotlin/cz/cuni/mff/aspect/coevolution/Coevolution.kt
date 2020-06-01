package cz.cuni.mff.aspect.coevolution

import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.storage.ObjectStorage
import cz.cuni.mff.aspect.utils.DeepCopy
import cz.cuni.mff.aspect.utils.SlidingWindow
import java.util.concurrent.TimeUnit

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
        val lastFinishedGenerationNumber = CoevolutionStorage.getLastStoredCoevolutionGenerationNumber(coevolutionSettings)
        println("Restarting coevolution from generation: $lastFinishedGenerationNumber")

        if (lastFinishedGenerationNumber == 0)
            return this.evolve(coevolutionSettings, 0)

        val updatedCoevolutionSettings = coevolutionSettings.copy(
            initialController = CoevolutionStorage.loadController(coevolutionSettings, lastFinishedGenerationNumber),
            initialLevelGenerator = CoevolutionStorage.loadLevelGenerator(coevolutionSettings, lastFinishedGenerationNumber)
        )

        val aiChart = CoevolutionStorage.loadControllerChart(coevolutionSettings)
        val lgChart = CoevolutionStorage.loadLevelGeneratorsChart(coevolutionSettings)

        updatedCoevolutionSettings.controllerEvolution.chart = aiChart
        updatedCoevolutionSettings.generatorEvolution.chart = lgChart

        return this.evolve(updatedCoevolutionSettings, lastFinishedGenerationNumber)
    }

    private fun evolve(coevolutionSettings: CoevolutionSettings, startGenerationIndex: Int = 0): CoevolutionResult {
        var currentController: MarioController = coevolutionSettings.initialController
        val generatorsHistory: SlidingWindow<LevelGenerator> = this.createGeneratorsHistory(coevolutionSettings, startGenerationIndex)
        var latestGenerator: LevelGenerator = coevolutionSettings.initialLevelGenerator

        val startTime = System.currentTimeMillis()
        for (generationIndex in (startGenerationIndex until coevolutionSettings.generations)) {
            println(" -- COEVOLUTION GENERATION ${generationIndex + 1} -- ")

            println("(${this.timeString(System.currentTimeMillis() - startTime)}) controller evo")
            currentController = coevolutionSettings.controllerEvolution.continueEvolution(
                currentController,
                generatorsHistory.getAll()
            )

            println("(${this.timeString(System.currentTimeMillis() - startTime)}) level generator evo")
            val agentFactory = { MarioAgent(DeepCopy.copy(currentController)) }
            latestGenerator = coevolutionSettings.generatorEvolution.evolve(agentFactory)

            generatorsHistory.push(latestGenerator)
            CoevolutionStorage.storeState(coevolutionSettings, generationIndex + 1, currentController, latestGenerator)
        }

        return CoevolutionResult(currentController, latestGenerator)
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
