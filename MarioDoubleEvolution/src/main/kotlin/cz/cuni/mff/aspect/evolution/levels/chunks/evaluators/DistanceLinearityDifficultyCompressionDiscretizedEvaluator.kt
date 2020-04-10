package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.utils.discretize

class DistanceLinearityDifficultyCompressionDiscretizedEvaluator : PCLevelEvaluator<Float> {

    override fun invoke(level: MarioLevel, chunkMetadata: ChunksLevelMetadata, gameStatistic: GameStatistics): Float {
        val distance = gameStatistic.finalMarioDistance

        val difficultyFactor = DifficultyEvaluator()(level, chunkMetadata, gameStatistic)
        val linearityFactor = LinearityEvaluator()(level, chunkMetadata, gameStatistic)
        val compressionFactor = HuffmanCompressionEvaluator()(level, chunkMetadata, gameStatistic)

        val linearityDiscretized = discretize(linearityFactor, arrayOf(0.0f, 0.3f, 0.6f, 1.0f))
        val difficultyDiscretized = discretize(difficultyFactor, arrayOf(0.0f, 0.3f, 0.6f, 1.0f))
        val compressionDiscretized = discretize(compressionFactor, arrayOf(0.0f, 0.3f, 0.6f, 1.0f))

        return distance * (1 + linearityDiscretized + difficultyDiscretized + compressionDiscretized)
    }

}
