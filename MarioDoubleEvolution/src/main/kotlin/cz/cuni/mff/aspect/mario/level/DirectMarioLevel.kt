package cz.cuni.mff.aspect.mario.level

import cz.cuni.mff.aspect.mario.Tiles

/**
 * Direct [MarioLevel] implementation. It receives both tiles and entities in the primary constructor
 * and uses these objects to implement the [MarioLevel] interface.
 */
class DirectMarioLevel(
    override val tiles: Array<ByteArray>,
    override val entities: Array<IntArray>,
    private val name: String? = null) : MarioLevel {

    companion object {
        fun createFromTilesArray(width: Int, height: Int, tilesArray: IntArray): DirectMarioLevel {
            val tiles = Array(height) { y ->
                ByteArray(width) { x ->
                    val index = x * height + y
                    when (tilesArray[index]) {
                        0 -> Tiles.NOTHING
                        1 -> Tiles.DIRT
                        else -> Tiles.NOTHING
                    }
                }
            }

            return DirectMarioLevel(tiles, emptyArray())
        }
    }

    override fun toString(): String = this.name ?: super.toString()
}