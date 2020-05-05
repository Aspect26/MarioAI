package cz.cuni.mff.aspect.coevolution

import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluator
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorEvolution
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.storage.ObjectStorage
import cz.cuni.mff.aspect.utils.DeepCopy
import cz.cuni.mff.aspect.utils.SlidingWindow
import java.util.concurrent.TimeUnit

/**
 *
 */
class MarioCoEvolution {

    fun evolve(controllerEvolution: ControllerEvolution, generatorEvolution: LevelGeneratorEvolution,
               initialController: MarioController, initialLevelGenerator: LevelGenerator,
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
        }

        return CoevolutionResult(currentController, latestGenerator)
    }

    private fun timeString(currentTimeMillis: Long): String {
        return String.format("%02d min, %02d sec",
            TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis),
            TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis))
        )
    }

    companion object {
        private const val DEFAULT_GENERATIONS_NUMBER: Int = 10
        private const val DEFAULT_REPEAT_GENERATORS_COUNT: Int = 5
    }
}
