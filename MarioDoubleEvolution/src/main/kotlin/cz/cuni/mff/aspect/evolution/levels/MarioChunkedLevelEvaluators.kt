package cz.cuni.mff.aspect.evolution.levels

import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel

typealias MarioLevelEvaluator<F> = (level: MarioLevel, gameStatistic: GameStatistics) -> F

object MarioLevelEvaluators {

    fun marioDistance(level: MarioLevel, gameStatistic: GameStatistics): Float =
        gameStatistic.finalMarioDistance

}
