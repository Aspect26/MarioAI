package cz.cuni.mff.aspect.mario.level.custom

import cz.cuni.mff.aspect.evolution.levels.pc.chunks.ColumnHelpers
import cz.cuni.mff.aspect.mario.level.MarioLevel

object OnlyPathLevel : MarioLevel {

    private const val LENGTH = 50

    override val tiles: Array<ByteArray> = Array(LENGTH) {
        ColumnHelpers.getPathColumn(10)
    }

    override val entities: Array<IntArray> = emptyArray()
}