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
 *
 * @param LevelGeneratorType level generator type
 */
class Coevolution<LevelGeneratorType: LevelGenerator>  {

    /**
     * Starts the coevolution using given settings.
     *
     * @param coevolutionSettings coevolution settings.
     * @see CoevolutionSettings for mor info about coevolution settings.
     */
    fun startEvolution(coevolutionSettings: CoevolutionSettings<LevelGeneratorType>): CoevolutionResult {
        return this.evolve(coevolutionSettings, 0)
    }

    /**
     * Continues the coevolution using data from given path. This is useful if the coevolution happens to crash
     * for any reason.
     *
     * @param coevolutionSettings coevolution settings with which the coevolution was started.
     */
    fun continueCoevolution(coevolutionSettings: CoevolutionSettings<LevelGeneratorType>): CoevolutionResult {
        val coevolutionState = CoevolutionStorage.loadState(coevolutionSettings)
        println("Restarting coevolution from generation: ${coevolutionState.lastFinishedGeneration}")

        val updatedCoevolutionSettings = coevolutionSettings.copy(
            initialController = coevolutionState.latestController,
            initialLevelGenerator = coevolutionState.latestGenerator
        )

        val generatorsLastPopulation = coevolutionState.latestGeneratorsPopulation

        updatedCoevolutionSettings.controllerEvolution.chart = coevolutionState.controllerEvolutionChart
        updatedCoevolutionSettings.generatorEvolution.chart = coevolutionState.generatorEvolutionChart

        return this.evolve(updatedCoevolutionSettings, coevolutionState.lastFinishedGeneration, generatorsLastPopulation, coevolutionState.coevolutionTimer)
    }

    private fun evolve(
        coevolutionSettings: CoevolutionSettings<LevelGeneratorType>,
        startGenerationIndex: Int = 0,
        initialGeneratorPopulation: List<LevelGeneratorType> = listOf(),
        coevolutionTimer: CoevolutionTimer = CoevolutionTimer()
    ): CoevolutionResult {
        var latestController: MarioController = coevolutionSettings.initialController
        var latestGenerator: LevelGenerator = coevolutionSettings.initialLevelGenerator
        var latestGeneratorPopulation: List<LevelGeneratorType> = initialGeneratorPopulation

        val generatorsHistory: SlidingWindow<LevelGenerator> = this.createGeneratorsHistory(coevolutionSettings, startGenerationIndex)
        val startTime = System.currentTimeMillis()

        for (generationIndex in (startGenerationIndex until coevolutionSettings.generations)) {
            println(" -- COEVOLUTION GENERATION ${generationIndex + 1} -- ")

            println("(${this.timeString(System.currentTimeMillis() - startTime)}) controller evo")
            coevolutionTimer.startControllerEvolution()
            latestController = coevolutionSettings.controllerEvolution.continueEvolution(latestController, generatorsHistory.getAll())
            coevolutionTimer.stopControllerEvolution()

            println("(${this.timeString(System.currentTimeMillis() - startTime)}) level generator evo")
            val agentFactory = { MarioAgent(DeepCopy.copy(latestController)) }
            coevolutionTimer.startGeneratorsEvolution()
            val lgEvolutionResult = if (startGenerationIndex == 0)
                coevolutionSettings.generatorEvolution.evolve(agentFactory)
            else
                coevolutionSettings.generatorEvolution.continueEvolution(agentFactory, latestGeneratorPopulation)
            coevolutionTimer.stopGeneratorsEvolution()
            latestGenerator = lgEvolutionResult.bestLevelGenerator
            latestGeneratorPopulation = lgEvolutionResult.lastPopulation

            generatorsHistory.push(latestGenerator)
            CoevolutionStorage.storeState(coevolutionSettings, generationIndex + 1, latestController,
                latestGenerator, latestGeneratorPopulation, coevolutionTimer)
        }

        println(" -- COEVOLUTION FINISHED --")
        println("Total coevolution time: ${timeString(coevolutionTimer.totalControllerEvolutionTime + coevolutionTimer.totalGeneratorsEvolutionTime)}")
        println("Total controller evolution time: ${timeString(coevolutionTimer.totalControllerEvolutionTime)}")
        println("Total generators evolution time: ${timeString(coevolutionTimer.totalGeneratorsEvolutionTime)}")

        return CoevolutionResult(latestController, latestGenerator)
    }

    private fun createGeneratorsHistory(coevolutionSettings: CoevolutionSettings<LevelGeneratorType>, alreadyCreatedGeneratorsCount: Int): SlidingWindow<LevelGenerator> {
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
