package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.PMPLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.utils.min
import io.jenetics.Optimize

/**
 * Probabilistic Multipass level generator evaluator returning minimum of distance reached and difficulty, linearity
 * and compression metrics.
 */
class DistanceLinearityDifficultyCompressionMinimumEvaluator : SummingEvaluator() {

    override fun evaluateOne(level: MarioLevel, levelMetadata: PMPLevelMetadata, gameStatistics: GameStatistics): Float {
        val distance = gameStatistics.finalMarioDistance

        val linearityFactor = LinearityEvaluator().evaluateOne(level, levelMetadata, gameStatistics)
        val difficultyFactor = DifficultyEvaluator().evaluateOne(level, levelMetadata, gameStatistics)
        val compressionFactor = HuffmanCompressionEvaluator().evaluateOne(level, levelMetadata, gameStatistics)

        val allFactors = listOf(linearityFactor, difficultyFactor, compressionFactor)
        val minFactor = min(allFactors)

        return distance * (1 + minFactor)
    }

    override val optimize: Optimize get() = Optimize.MAXIMUM

}