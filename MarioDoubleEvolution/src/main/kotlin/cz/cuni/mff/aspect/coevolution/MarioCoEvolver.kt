package cz.cuni.mff.aspect.coevolution

import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluator
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorEvolution
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.storage.ObjectStorage
import cz.cuni.mff.aspect.utils.DeepCopy
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser
import java.util.concurrent.TimeUnit

class MarioCoEvolver {

    fun evolve(controllerEvolution: ControllerEvolution, generatorEvolution: LevelGeneratorEvolution,
               initialController: MarioController, initialLevelGenerator: LevelGenerator,
               controllerFitness: MarioGameplayEvaluator<Float> = MarioGameplayEvaluators::distanceOnly,
               generations: Int = DEFAULT_GENERATIONS_NUMBER,
               storagePath: String
    ): CoevolutionResult {
        var currentController: MarioController = initialController
        var currentLevelGenerator: LevelGenerator = initialLevelGenerator

        var gameSimulator = GameSimulator()

        val startTime = System.currentTimeMillis()
        for (generation in (0 until generations)) {
            println(" -- COEVOLUTION GENERATION ${generation + 1} -- ")

            println("(${this.timeString(System.currentTimeMillis() - startTime)}) controller evo")
            currentController = controllerEvolution.continueEvolution(
                currentController,
                currentLevelGenerator,
                controllerFitness,
                MarioGameplayEvaluators::victoriesOnly
            )

//            gameSimulator.playMario(MarioAgent(DeepCopy.copy(currentController)), currentLevelGenerator.generate(), true)

            println("(${this.timeString(System.currentTimeMillis() - startTime)}) level generator evo")
            val agentFactory = { MarioAgent(DeepCopy.copy(currentController)) }
            currentLevelGenerator = generatorEvolution.evolve(agentFactory)

            ObjectStorage.store("$storagePath/ai_${generation + 1}.ai", currentController)
            ObjectStorage.store("$storagePath/lg_${generation + 1}.lg", currentLevelGenerator)
//            LevelVisualiser().display(currentLevelGenerator.generate())
        }

        return CoevolutionResult(currentController, currentLevelGenerator)
    }

    private fun timeString(currentTimeMillis: Long): String {
        return String.format("%02d min, %02d sec",
            TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis),
            TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis))
        )
    }

    companion object {
        private const val DEFAULT_GENERATIONS_NUMBER: Int = 1
    }
}