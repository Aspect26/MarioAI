package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.utils.min

class DistanceLinearityDifficultyCompressionMinimumEvaluator : PMPLevelEvaluator<Float> {

    override fun invoke(level: MarioLevel, levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics): Float {
        val distance = gameStatistic.finalMarioDistance

        val linearityFactor = LinearityEvaluator()(level, levelMetadata, gameStatistic)
        val difficultyFactor = DifficultyEvaluator()(level, levelMetadata, gameStatistic)
        val compressionFactor = CompressionEvaluator()(level, levelMetadata, gameStatistic)

        val allFactors = listOf(linearityFactor, difficultyFactor, compressionFactor)
        val minFactor = min(allFactors)

        return distance * (1 + minFactor)
    }

}