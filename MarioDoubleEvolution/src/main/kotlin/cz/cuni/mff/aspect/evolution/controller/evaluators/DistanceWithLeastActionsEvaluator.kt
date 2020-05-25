package cz.cuni.mff.aspect.evolution.controller.evaluators

import cz.cuni.mff.aspect.utils.sumByFloat
import cz.cuni.mff.aspect.mario.GameStatistics


/**
 * Computes sum of distances, which the agent was able to get to in given levels, and punishes the agent for
 * each action if has done.
 */
class DistanceWithLeastActionsEvaluator : MarioGameplayEvaluator {
    override fun invoke(gameStatistics: Array<GameStatistics>): Float {
        val sumFinalDistances: Float = gameStatistics.sumByFloat { it.finalMarioDistance }
        val sumJumps = gameStatistics.sumBy { it.jumps }
        val sumSpecials = gameStatistics.sumBy { it.specials }
        val levelsFinished = gameStatistics.sumBy { if (it.levelFinished) 1 else 0 }

        return sumFinalDistances - sumJumps * 20 - sumSpecials * 20 + levelsFinished * 200.0f
    }
}
