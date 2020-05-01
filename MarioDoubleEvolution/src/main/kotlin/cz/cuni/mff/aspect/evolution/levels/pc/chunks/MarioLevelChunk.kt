package cz.cuni.mff.aspect.evolution.levels.pc.chunks

import java.io.Serializable

abstract class MarioLevelChunk(val name: String) : Serializable {

    fun generate(level: Int): Array<ByteArray> =
        Array(this.length) {
            this.generateColumn(it, level)
        }

    abstract fun generateColumn(column: Int, level: Int): ByteArray

    abstract fun copySelf(): MarioLevelChunk

    abstract val length: Int

}