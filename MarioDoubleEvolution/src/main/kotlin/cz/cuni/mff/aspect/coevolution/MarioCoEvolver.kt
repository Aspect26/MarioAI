package cz.cuni.mff.aspect.coevolution

import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelEvolution
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.mario.level.custom.OnlyPathLevel

class MarioCoEvolver {

    fun evolve(controllerEvolution: ControllerEvolution, generator: LevelEvolution, generations: Int = DEFAULT_GENERATIONS_NUMBER): CoevolutionResult {
        lateinit var resultController: MarioController
        var resultLevels: Array<MarioLevel> = arrayOf(OnlyPathLevel)

        for (generation in (0 until generations)) {
            println("COEVOLUTION GENERATION ${generation + 1}")
            resultController = controllerEvolution.evolve(resultLevels, MarioGameplayEvaluators::distanceLeastActions, MarioGameplayEvaluators::victoriesOnly)
            resultLevels = generator.evolve { MarioAgent(resultController) }
        }

        return CoevolutionResult(resultController, resultLevels)
    }

    companion object {
        private const val DEFAULT_GENERATIONS_NUMBER: Int = 1
    }
}