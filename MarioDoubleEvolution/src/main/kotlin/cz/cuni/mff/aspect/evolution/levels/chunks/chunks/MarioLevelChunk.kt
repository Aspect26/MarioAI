package cz.cuni.mff.aspect.evolution.levels.chunks.chunks

abstract class MarioLevelChunk {

    abstract fun generate(level: Int): Array<ByteArray>

    override fun toString(): String {
        return this::class.java.simpleName.toString()
    }

}