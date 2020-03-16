package cz.cuni.mff.aspect.evolution.levels

import cz.cuni.mff.aspect.extensions.sumByFloat
import cz.cuni.mff.aspect.mario.GameStatistics

typealias MarioLevelEvaluator<F> = (gameStatistics: List<GameStatistics>) -> F

object MarioLevelEvaluators {

    fun distanceOnly(statistics: List<GameStatistics>): Float {
        return statistics.sumByFloat { it.finalMarioDistance }
    }

    fun distanceActionsVictory(statistics: GameStatistics): Float {
        return statistics.finalMarioDistance + statistics.kills * 20 + if (statistics.levelFinished) 200f else 0f
    }

}