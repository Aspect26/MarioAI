package cz.cuni.mff.aspect.evolution.levels.chunks.chunks

import java.io.Serializable

abstract class MarioLevelChunk(val name: String) : Serializable {

    abstract fun generate(level: Int): Array<ByteArray>

    abstract fun copySelf(): MarioLevelChunk

}