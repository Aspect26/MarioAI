package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel

class ChunksDiversityEvaluator : PCLevelEvaluator<Float> {

    override operator fun invoke(level: MarioLevel, chunkMetadata: ChunksLevelMetadata, gameStatistic: GameStatistics): Float {
        val differentChunksUsed = chunkMetadata.chunks.map { it.chunk.name }.distinct().size
        return differentChunksUsed.toFloat() / PCLevelGenerator.DEFAULT_CHUNK_TYPES_COUNT
    }

}