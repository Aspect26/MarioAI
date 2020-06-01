package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.evolution.levels.evaluators.WinRatioEvaluator
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import io.jenetics.Optimize

/**
 * Probabilistic Chunks level generator evaluator returning difference of actual win ratio and expected win ration.
 *
 * @param expectedWinRatio the expected win ratio (e.g. 0.5 for 50% wins and 50% loses).
 * @param scale the resulting value is multiplied by scale.
 * @see WinRatioEvaluator for more info.
 */
class WinRatioEvaluator(private val expectedWinRatio: Float = 0.75f, private val scale: Float = 50000f) : PCLevelEvaluator<Float> {

    override fun invoke(
        levels: List<MarioLevel>,
        levelsChunkMetadata: List<ChunksLevelMetadata>,
        gameStatistics: List<GameStatistics>
    ): Float {
        return WinRatioEvaluator(this.expectedWinRatio, this.scale)(levels, gameStatistics)
    }

    override val optimize: Optimize get() = Optimize.MAXIMUM

}