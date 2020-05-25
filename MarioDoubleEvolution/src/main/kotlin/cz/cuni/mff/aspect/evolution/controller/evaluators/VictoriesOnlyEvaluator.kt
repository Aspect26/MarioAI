package cz.cuni.mff.aspect.evolution.controller.evaluators

import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.utils.sumByFloat

/** Computes how many victories the agent has achieved on given levels, multiplied by 1000. */
class VictoriesOnlyEvaluator :
    MarioGameplayEvaluator {
    override fun invoke(gameStatistics: Array<GameStatistics>): Float {
        return gameStatistics.sumByFloat { if (it.levelFinished) 1.0f else 0.0f } * 1000
    }
}