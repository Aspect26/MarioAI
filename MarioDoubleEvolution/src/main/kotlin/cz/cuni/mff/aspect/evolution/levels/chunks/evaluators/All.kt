package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.evolution.levels.evaluators.WinRatioEvaluator
import cz.cuni.mff.aspect.evolution.levels.evaluators.compression.ImageHuffmanCompression
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.visualisation.level.LevelToImageConverter
import io.jenetics.Optimize

/**
 * Probabilistic Chunks level generator evaluator returning difference of won/lost count, compressibility metric
 * and linearity metric.
 */
// TODO: better name...
class All(expectedWinRatio: Float = 0.5f) : PCLevelEvaluator<Float> {

    private val winRatioEvaluator: WinRatioEvaluator = WinRatioEvaluator(expectedWinRatio, 1f)

    override fun invoke(
        levels: List<MarioLevel>,
        levelsChunkMetadata: List<ChunksLevelMetadata>,
        gameStatistics: List<GameStatistics>
    ): Float {
        val winRatioFactor = this.winRatioEvaluator(levels, gameStatistics)

        // TODO: konstanta vycucana z prsta
        val compressionFactor = List(levels.size) {
            val image = LevelToImageConverter.createMinified(levels[it], noAlpha=true)
            ImageHuffmanCompression(2).getSize(image)
        }.sum() / 125180f

        val linearityFactor = List(levels.size) {
            LinearityEvaluator().evaluateOne(levels[it], levelsChunkMetadata[it], gameStatistics[it])
        }.average().toFloat()

        return winRatioFactor * (1 + compressionFactor + linearityFactor)
    }

    override val optimize: Optimize get() = Optimize.MAXIMUM

}