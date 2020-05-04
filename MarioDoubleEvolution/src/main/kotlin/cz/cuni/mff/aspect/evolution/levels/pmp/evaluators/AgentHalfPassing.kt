package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import io.jenetics.Optimize
import kotlin.math.abs

class AgentHalfPassing : PMPLevelGeneratorEvaluatorBase() {

    override fun evaluate(
        levels: List<MarioLevel>,
        metadata: List<MarioLevelMetadata>,
        gameStatistics: List<GameStatistics>
    ): Float {
        val wonCount = gameStatistics.sumBy { if (it.levelFinished) 1 else 0 }
        val lostCount = gameStatistics.size - wonCount

        val wonLostDifference = abs(wonCount - lostCount) * 1000
        return wonLostDifference.toFloat()
    }

    override val optimize: Optimize get() = Optimize.MINIMUM

}