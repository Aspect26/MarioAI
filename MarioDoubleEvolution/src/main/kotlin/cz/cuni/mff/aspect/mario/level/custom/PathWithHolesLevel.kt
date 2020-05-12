package cz.cuni.mff.aspect.mario.level.custom

import cz.cuni.mff.aspect.evolution.levels.ge.grammar.ColumnHelpers
import cz.cuni.mff.aspect.mario.level.MarioLevel

object PathWithHolesLevel : MarioLevel {

    private const val LENGTH = 50
    private val HOLES_AT = arrayOf(
        6, 7,
        12, 13, 14,
        20, 21, 22, 23,
        28, 29,
        32, 33,
        35, 36
    )

    override val tiles: Array<ByteArray> = Array(LENGTH) {
        if (it in HOLES_AT) {
            ColumnHelpers.getSpaceColumn()
        } else {
            ColumnHelpers.getPathColumn(10)
        }
    }

    override val entities: Array<IntArray> = emptyArray()

    override fun toString(): String = "PathWithHoles"

}