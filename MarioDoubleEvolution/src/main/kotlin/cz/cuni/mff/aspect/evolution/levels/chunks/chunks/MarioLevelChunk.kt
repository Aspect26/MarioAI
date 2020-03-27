package cz.cuni.mff.aspect.evolution.levels.chunks.chunks

import java.io.Serializable

abstract class MarioLevelChunk : Serializable {

    abstract fun generate(level: Int): Array<ByteArray>

    override fun toString(): String {
        return this::class.java.simpleName.toString()
    }

}