package cz.cuni.mff.aspect.evolution.levels.pc.chunks

import java.io.Serializable

/**
 * Abstract class representing a Super Mario level chunk.
 * @param name name of the chunk.
 */
abstract class MarioLevelChunk(val name: String) : Serializable {

    /**
     * Generates this chunk in the form of a 2D array of tiles.
     * @param level floor level.
     */
    fun generate(level: Int): Array<ByteArray> =
        Array(this.length) {
            this.generateColumn(it, level)
        }

    /**
     * Generates given column of this chunk.
     * @param column index of the chunk's column to be generated.
     * @param level floor level.
     */
    abstract fun generateColumn(column: Int, level: Int): ByteArray

    /** Copies itself. */
    abstract fun copySelf(): MarioLevelChunk

    /** Length (in tiles) of the level. */
    abstract val length: Int

}