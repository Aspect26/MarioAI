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
        val lgLastPopulation = CoevolutionStorage.loadLastLevelGeneratorsPopulation(coevolutionSettings)

        updatedCoevolutionSettings.controllerEvolution.chart = aiChart
        updatedCoevolutionSettings.generatorEvolution.chart = lgChart

        return this.evolve(updatedCoevolutionSettings, lastFinishedGenerationNumber, lgLastPopulation)
    }

    private fun evolve(
        coevolutionSettings: CoevolutionSettings<LevelGeneratorType>,
        startGenerationIndex: Int = 0,
        initialGeneratorPopulation: List<LevelGeneratorType> = listOf()
    ): CoevolutionResult {
        var latestController: MarioController = coevolutionSettings.initialController
        var latestGenerator: LevelGenerator = coevolutionSettings.initialLevelGenerator
        var latestGeneratorPopulation: List<LevelGeneratorType> = initialGeneratorPopulation

        val generatorsHistory: SlidingWindow<LevelGenerator> = this.createGeneratorsHistory(coevolutionSettings, startGenerationIndex)
        val startTime = System.currentTimeMillis()

        for (generationIndex in (startGenerationIndex until coevolutionSettings.generations)) {
            println(" -- COEVOLUTION GENERATION ${generationIndex + 1} -- ")

            println("(${this.timeString(System.currentTimeMillis() - startTime)}) controller evo")
            latestController = coevolutionSettings.controllerEvolution.continueEvolution(latestController, generatorsHistory.getAll())

            println("(${this.timeString(System.currentTimeMillis() - startTime)}) level generator evo")
            val agentFactory = { MarioAgent(DeepCopy.copy(latestController)) }
            val lgEvolutionResult = if (startGenerationIndex == 0)
                coevolutionSettings.generatorEvolution.evolve(agentFactory)
            else
                coevolutionSettings.generatorEvolution.continueEvolution(agentFactory, latestGeneratorPopulation)
            latestGenerator = lgEvolutionResult.bestLevelGenerator
            latestGeneratorPopulation = lgEvolutionResult.lastPopulation

            generatorsHistory.push(latestGenerator)
            CoevolutionStorage.storeState(coevolutionSettings, generationIndex + 1, latestController,
                latestGenerator, latestGeneratorPopulation)
        }

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
