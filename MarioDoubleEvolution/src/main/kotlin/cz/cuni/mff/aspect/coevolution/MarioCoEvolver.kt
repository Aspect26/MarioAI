package cz.cuni.mff.aspect.coevolution

import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController

class MarioCoEvolver {

    fun evolve(controllerEvolution: ControllerEvolution, generator: LevelGeneratorEvolution, generations: Int = DEFAULT_GENERATIONS_NUMBER): CoevolutionResult {
        lateinit var resultController: MarioController
        var resultLevelGenerator: LevelGenerator = PMPLevelGenerator()

        for (generation in (0 until generations)) {
            println("COEVOLUTION GENERATION ${generation + 1}")
            val levels = Array(5) { resultLevelGenerator.generate() }

            resultController = controllerEvolution.evolve(levels, MarioGameplayEvaluators::distanceLeastActions, MarioGameplayEvaluators::victoriesOnly)
            resultLevelGenerator = generator.evolve { MarioAgent(resultController) }
        }

        return CoevolutionResult(resultController, resultLevelGenerator)
    }

    companion object {
        private const val DEFAULT_GENERATIONS_NUMBER: Int = 1
    }
}