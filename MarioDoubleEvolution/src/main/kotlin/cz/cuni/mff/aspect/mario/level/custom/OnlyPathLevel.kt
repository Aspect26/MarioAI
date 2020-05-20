package cz.cuni.mff.aspect.mario.level.custom

import cz.cuni.mff.aspect.evolution.levels.ge.grammar.ColumnHelpers
import cz.cuni.mff.aspect.mario.level.MarioLevel

/** Super Mario level containing only flat path with no obstacles. */
object OnlyPathLevel : MarioLevel {

    private const val LENGTH = 50

    override val tiles: Array<ByteArray> = Array(LENGTH) {
        ColumnHelpers.getPathColumn(10)
    }

    override val entities: Array<IntArray> = emptyArray()

    override fun toString(): String = "OnlyPathLevel"

}