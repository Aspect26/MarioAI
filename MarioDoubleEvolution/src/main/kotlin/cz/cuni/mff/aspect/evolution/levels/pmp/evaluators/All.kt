package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.evaluators.compression.SmallPNGCompression
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.PMPLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.visualisation.level.LevelToImageConverter
import io.jenetics.Optimize
import kotlin.math.abs

/**
 * Probabilistic Multipass level generator evaluator returning difference of won/lost count, compressibility metric
 * and linearity metric.
 */
// TODO: better name...
class All : PMPLevelEvaluator<Float> {

    override fun invoke(
        levels: List<MarioLevel>,
        metadata: List<PMPLevelMetadata>,
        gameStatistics: List<GameStatistics>
    ): Float {
        val wonCount = gameStatistics.sumBy { if (it.levelFinished) 1 else 0 }
        val lostCount = gameStatistics.size - wonCount

        val maxDifference = gameStatistics.size
        val wonLostDifference = abs(wonCount - lostCount)
        val reversedWonLostDifference = maxDifference - wonLostDifference

        // TODO: konstanta vycucana z prsta
        val compressionFactor = List(levels.size) {
            val image = LevelToImageConverter.createMinified(levels[it], noAlpha=true)
            SmallPNGCompression.getSize(image)
        }.sum() / (levels.size * 1000f)

        val linearityFactor = List(levels.size) {
            LinearityEvaluator().evaluateOne(levels[it], metadata[it], gameStatistics[it])
        }.average().toFloat()

        return reversedWonLostDifference.toFloat() * (1 + compressionFactor + linearityFactor) * 1000
    }

    override val optimize: Optimize get() = Optimize.MAXIMUM

}