package cz.cuni.mff.aspect.evolution.levels.chunks.metadata

import cz.cuni.mff.aspect.evolution.levels.chunks.chunks.MarioLevelChunk
import cz.cuni.mff.aspect.mario.level.DirectMarioLevel
import cz.cuni.mff.aspect.mario.level.MarioLevel


data class ChunkWithHeight(
    val chunk: MarioLevelChunk,
    val height: Int
)


data class ChunksLevelMetadata(
    val chunks: List<ChunkWithHeight>,
    val entities: Array<Array<Int>>
) {

    fun createLevel(): MarioLevel {
        val tiles: Array<ByteArray> = this.createTiles()
        return DirectMarioLevel(tiles, this.entities)
    }

    fun createTiles(): Array<ByteArray> {
        val tilesList: MutableList<ByteArray> = mutableListOf()

        for ((chunk, level) in this.chunks) {
            tilesList.addAll(chunk.generate(level))
        }

        return tilesList.toTypedArray()
    }

}
