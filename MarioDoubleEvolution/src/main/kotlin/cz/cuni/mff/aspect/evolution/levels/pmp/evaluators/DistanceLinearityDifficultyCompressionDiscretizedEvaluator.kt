package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.utils.discretize

class DistanceLinearityDifficultyCompressionDiscretizedEvaluator : PMPLevelEvaluator<Float> {

    override fun invoke(level: MarioLevel, levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics): Float {
        val distance = gameStatistic.finalMarioDistance

        val linearityFactor = LinearityEvaluator()(level, levelMetadata, gameStatistic)
        val difficultyFactor = DifficultyEvaluator()(level, levelMetadata, gameStatistic)
        val compressionFactor = CompressionEvaluator()(level, levelMetadata, gameStatistic)

        val linearityDiscretized = discretize(linearityFactor, arrayOf(0.0f, 0.3f, 0.6f, 1.0f))
        val difficultyDiscretized = discretize(difficultyFactor, arrayOf(0.0f, 0.3f, 0.6f, 1.0f))
        val compressionDiscretized = discretize(compressionFactor, arrayOf(0.0f, 0.3f, 0.6f, 1.0f))

        return distance * (1 + linearityDiscretized + difficultyDiscretized + compressionDiscretized)
    }

}