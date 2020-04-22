package cz.cuni.mff.aspect.mario.level


interface MarioLevel {

    val tiles: Array<ByteArray>
    val entities: Array<IntArray>

    val pixelWidth: Int
        get() = tiles.size * 16

}
