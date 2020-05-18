package cz.cuni.mff.aspect.evolution.levels.pc.metadata

import cz.cuni.mff.aspect.evolution.levels.pc.chunks.MarioLevelChunk

/** Represents a chunk and its height. */
data class ChunkWithHeight(
    val chunk: MarioLevelChunk,
    val height: Int
)
