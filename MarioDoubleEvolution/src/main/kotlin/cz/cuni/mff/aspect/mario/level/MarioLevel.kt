package cz.cuni.mff.aspect.mario.level


interface MarioLevel {

    val tiles: Array<ByteArray>
    val entities: Array<Array<Int>>

    val pixelWidth: Int
        get() = tiles.size * 16

}
