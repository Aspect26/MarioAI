package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.evolution.levels.evaluators.WinRatioEvaluator
import cz.cuni.mff.aspect.evolution.levels.evaluators.compression.SmallPNGCompression
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.visualisation.level.LevelToImageConverter
import io.jenetics.Optimize

/**
 * Probabilistic Chunks level generator evaluator returning difference of won/lost count, compressibility metric
 * and linearity metric.
 */
class Combined(expectedWinRatio: Float = 0.5f) : PCLevelEvaluator<Float> {

    private val winRatioEvaluator: WinRatioEvaluator = WinRatioEvaluator(expectedWinRatio, 1f)

    override fun invoke(
        levels: List<MarioLevel>,
        levelsChunkMetadata: List<ChunksLevelMetadata>,
        gameStatistics: List<GameStatistics>
    ): Float {
        val winRatioFactor = this.winRatioEvaluator(levels, gameStatistics)

        val compressionFactor = List(levels.size) {
            val image = LevelToImageConverter.createMinified(levels[it], noAlpha=true)
            SmallPNGCompression.getSize(image)
        }.sum() / 11400f

        val linearityFactor = List(levels.size) {
            LinearityEvaluator().evaluateOne(levels[it], levelsChunkMetadata[it], gameStatistics[it])
        }.average().toFloat()

        return winRatioFactor * (1 + compressionFactor + linearityFactor)
    }

    override val optimize: Optimize get() = Optimize.MAXIMUM

}