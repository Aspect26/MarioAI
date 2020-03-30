package cz.cuni.mff.aspect.coevolution

import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluator
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.ProbabilisticChunksLevelGenerator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser
import java.util.concurrent.TimeUnit

class MarioCoEvolver {

    fun evolve(controllerEvolution: ControllerEvolution, generator: LevelGeneratorEvolution,
               controllerFitness: MarioGameplayEvaluator<Float> = MarioGameplayEvaluators::distanceOnly,
               generations: Int = DEFAULT_GENERATIONS_NUMBER): CoevolutionResult {
        lateinit var resultController: MarioController
        // TODO: what about this first generator
        var resultLevelGenerator: LevelGenerator = ProbabilisticChunksLevelGenerator()

        val startTime = System.currentTimeMillis()
        for (generation in (0 until generations)) {
            println(" -- COEVOLUTION GENERATION ${generation + 1} -- ")

            println("(${this.timeString(System.currentTimeMillis() - startTime)}) controller evo")
            val levels = Array(5) { resultLevelGenerator.generate() }
            resultController = controllerEvolution.evolve(levels, controllerFitness, MarioGameplayEvaluators::victoriesOnly)

            println("(${this.timeString(System.currentTimeMillis() - startTime)}) level generator evo")
            val agentFactory = { MarioAgent(resultController.copy()) }
            resultLevelGenerator = generator.evolve(agentFactory)

            LevelVisualiser().display(resultLevelGenerator.generate())
        }

        return CoevolutionResult(resultController, resultLevelGenerator)
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