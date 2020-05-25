package cz.cuni.mff.aspect.evolution.levels.evaluators

import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import kotlin.math.abs
import kotlin.math.max

/**
 * Levels evaluator returning difference of expected win ratio and actual win ratio.
 *
 * The value returned is normalized to interval [0, 1] and inverted, so that if the expected ratio is the same as actual,
 * it returns 1. The return value can be also scaled.
 *
 * @param expectedWinRatio the expected win ratio (e.g. 0.5 for 50% wins and 50% loses).
 * @param scale the resulting value is multiplied by scale.
 */
class WinRatio(private val expectedWinRatio: Float = 0.75f, private val scale: Float = 50000f) : LevelsEvaluator<Float> {

    override fun invoke(
        levels: List<MarioLevel>,
        gameStatistics: List<GameStatistics>
    ): Float {
        val wonCount = gameStatistics.sumBy { if (it.levelFinished) 1 else 0 }
        val actualWinRatio = wonCount.toFloat() / gameStatistics.size
        val ratioDifference = abs(this.expectedWinRatio - actualWinRatio)

        return this.normalizeAndInvertDifference(ratioDifference) * this.scale
    }

    private fun normalizeAndInvertDifference(ratioDifference: Float): Float {
        val maxDifference: Float = max(this.expectedWinRatio, 1f - this.expectedWinRatio)
        val normalized = ratioDifference * (1 / maxDifference)

        return 1f - normalized
    }

}