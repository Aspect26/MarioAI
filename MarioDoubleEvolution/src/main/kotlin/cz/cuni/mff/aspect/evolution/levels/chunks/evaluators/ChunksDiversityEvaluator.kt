package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import io.jenetics.Optimize

/** Probabilistic Chunks level generator evaluator returning how many different chunks are used. */
class ChunksDiversityEvaluator : SummingEvaluator() {

    override fun evaluateOne(level: MarioLevel, levelMetadata: ChunksLevelMetadata, gameStatistics: GameStatistics): Float {
        val differentChunksUsed = levelMetadata.chunks.map { it.chunk.name }.distinct().size
        return differentChunksUsed.toFloat() / PCLevelGenerator.DEFAULT_CHUNK_TYPES_COUNT
    }

    override val optimize: Optimize get() = Optimize.MAXIMUM

}