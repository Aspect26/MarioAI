package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.utils.min

class DistanceLinearityDifficultyCompressionMinimumEvaluator : PCLevelEvaluator<Float> {

    override fun invoke(level: MarioLevel, chunkMetadata: ChunksLevelMetadata, gameStatistic: GameStatistics): Float {
        val distance = gameStatistic.finalMarioDistance

        val difficultyFactor = DifficultyEvaluator()(level, chunkMetadata, gameStatistic)
        val linearityFactor = LinearityEvaluator()(level, chunkMetadata, gameStatistic)
        val compressionFactor = CompressionEvaluator()(level, chunkMetadata, gameStatistic)

        val allFactors = listOf(linearityFactor, difficultyFactor, compressionFactor)
        val minFactor = min(allFactors)

        return distance * (1 + minFactor)
    }

}
