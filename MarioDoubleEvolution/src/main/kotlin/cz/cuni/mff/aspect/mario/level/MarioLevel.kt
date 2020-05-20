package cz.cuni.mff.aspect.mario.level

/** Interface for Super Mario game levels. */
interface MarioLevel {

    /** Represents 2D grid of tiles in the level. */
    val tiles: Array<ByteArray>

    /** Represents 2D grid of entities in the level. One tile can contain only one entity. */
    val entities: Array<IntArray>

    /** Constant specifying pixel width of one tile. */
    val pixelWidth: Int
        get() = tiles.size * 16

}
