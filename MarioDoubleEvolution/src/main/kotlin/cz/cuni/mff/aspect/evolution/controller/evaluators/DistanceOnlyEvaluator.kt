package cz.cuni.mff.aspect.evolution.controller.evaluators

import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.utils.sumByFloat

/** Computes sum of distances, which the agent was able to get to in the given levels. */
class DistanceOnlyEvaluator :
    MarioGameplayEvaluator {
    override fun invoke(gameStatistics: Array<GameStatistics>): Float {
        return gameStatistics.sumByFloat { it.finalMarioDistance }
    }
}