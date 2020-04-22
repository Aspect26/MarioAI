package cz.cuni.mff.aspect.storage

import cz.cuni.mff.aspect.mario.level.DirectMarioLevel
import cz.cuni.mff.aspect.mario.level.MarioLevel
import java.io.Serializable


object LevelStorage {

    private val storage: ObjectStorage = ObjectStorage

    fun storeLevel(filePath: String, level: MarioLevel) {
        val levelRepresentation = LevelRepresentation(level.tiles, level.entities)
        this.storage.store(filePath, levelRepresentation)
    }

    fun loadLevel(filePath: String): MarioLevel {
        val levelRepresentation = this.storage.load(filePath) as LevelRepresentation
        return DirectMarioLevel(levelRepresentation.tiles, levelRepresentation.enemies)
    }

    data class LevelRepresentation(val tiles: Array<ByteArray>, val enemies: Array<IntArray>): Serializable {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as LevelRepresentation

            if (!tiles.contentDeepEquals(other.tiles)) return false
            if (!enemies.contentDeepEquals(other.enemies)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = tiles.contentDeepHashCode()
            result = 31 * result + enemies.contentDeepHashCode()
            return result
        }
    }

}