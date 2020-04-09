package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.evolution.levels.evaluators.DifficultyEvaluator
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel

class DifficultyEvaluator : PCLevelEvaluator<Float> {

    override operator fun invoke(level: MarioLevel, chunkMetadata: ChunksLevelMetadata, gameStatistic: GameStatistics): Float {
        val levelLength = chunkMetadata.chunks.subList(1, chunkMetadata.chunks.size - 1).map { it.chunk.length }.sum()
        return (DifficultyEvaluator()(level, gameStatistic) / (levelLength)).coerceAtMost(1f)
    }

}