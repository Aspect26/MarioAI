package cz.cuni.mff.aspect.evolution.levels.chunks.metadata

import cz.cuni.mff.aspect.evolution.levels.chunks.chunks.MarioLevelChunk

/** Represents a chunk and its height. */
data class ChunkWithHeight(
    val chunk: MarioLevelChunk,
    val height: Int
)
