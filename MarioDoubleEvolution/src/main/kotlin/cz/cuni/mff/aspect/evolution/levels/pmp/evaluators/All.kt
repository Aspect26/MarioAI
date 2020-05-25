package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.evaluators.WinRatio
import cz.cuni.mff.aspect.evolution.levels.evaluators.compression.ImageHuffmanCompression
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.PMPLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.visualisation.level.LevelToImageConverter
import io.jenetics.Optimize

/**
 * Probabilistic Multipass level generator evaluator returning win ratio metric, compressibility metric
 * and linearity metric.
 */
// TODO: better name...
class All(expectedWinRatio: Float = 0.5f) : PMPLevelEvaluator<Float> {

    private val winRatioEvaluator: WinRatio = WinRatio(expectedWinRatio, 1f)

    override fun invoke(
        levels: List<MarioLevel>,
        metadata: List<PMPLevelMetadata>,
        gameStatistics: List<GameStatistics>
    ): Float {
        val winRatioFactor = this.winRatioEvaluator(levels, gameStatistics)

        // TODO: konstanta vycucana z prsta
        val compressionFactor = List(levels.size) {
            val image = LevelToImageConverter.createMinified(levels[it], noAlpha=true)
            ImageHuffmanCompression(2).getSize(image)
        }.sum() / 155073f

        val linearityFactor = List(levels.size) {
            LinearityEvaluator().evaluateOne(levels[it], metadata[it], gameStatistics[it])
        }.average().toFloat()

        return winRatioFactor * (1 + compressionFactor + linearityFactor) * 30000
    }

    override val optimize: Optimize get() = Optimize.MAXIMUM

}