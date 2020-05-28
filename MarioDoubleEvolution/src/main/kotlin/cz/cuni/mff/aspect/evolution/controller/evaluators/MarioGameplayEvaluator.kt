package cz.cuni.mff.aspect.evolution.controller.evaluators

import cz.cuni.mff.aspect.mario.GameStatistics
import java.io.Serializable

/** Interface for Super Mario agent gameplay evaluator. */
interface MarioGameplayEvaluator : Serializable {

    operator fun invoke(gameStatistics: Array<GameStatistics>): Float

}
