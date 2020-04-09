package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel

class DistanceLinearityDifficultyCompressionEvaluator : PMPLevelEvaluator<Float> {

    override fun invoke(level: MarioLevel, levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics): Float {
        val distance = gameStatistic.finalMarioDistance

        val linearityFactor = LinearityEvaluator()(level, levelMetadata, gameStatistic)
        val difficultyFactor = DifficultyEvaluator()(level, levelMetadata, gameStatistic)
        val compressionFactor = CompressionEvaluator()(level, levelMetadata, gameStatistic)

        return distance * (2 + linearityFactor + difficultyFactor + compressionFactor)
    }

}