package cz.cuni.mff.aspect.mario.level.original

import cz.cuni.mff.aspect.evolution.levels.ge.grammar.ColumnHelpers
import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.MarioLevel


object Stage4Level1 : MarioLevel {

    private const val BOTTOM_LEVEL = 15
    private const val FLOOR_LEVEL =  13
    private const val SECOND_LEVEL = 9
    private const val THIRD_LEVEL = 5
    private const val LEVEL_WIDTH = 230

    override val tiles: Array<ByteArray>
    override val enemies: Array<Array<Int>>

    init {
        this.tiles = Array(LEVEL_WIDTH) {
            when (it) {
                21 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 3)
                22 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 3)

                25 -> ColumnHelpers.getColumnWithTwoBlocks(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_POWERUP, THIRD_LEVEL, Tiles.QM_WITH_COIN)

                29, 30 -> ColumnHelpers.getSpaceColumn()

                38 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.COIN)
                39, 40 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL - 1, Tiles.COIN)
                41 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.COIN)

                61, 63 -> ColumnHelpers.getColumnWithTwoBlocks(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_COIN, THIRD_LEVEL, Tiles.QM_WITH_COIN)

                75, 76, 77, 78 -> ColumnHelpers.getSpaceColumn()

                87, 88, 89, 90 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_COIN)  // the 3rd should have hidden 1UP

                100 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 3)

                102, 103, 104, 105 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, THIRD_LEVEL + 1, Tiles.COIN)

                113 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 4)
                114 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 4)

                116, 117, 118, 119 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, THIRD_LEVEL + 1, Tiles.COIN)

                129 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 4)
                130 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 4)

                132, 133, 134, 135 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, THIRD_LEVEL + 1, Tiles.COIN)

                143, 144 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_COIN)
                145 -> ColumnHelpers.getColumnWithTwoBlocks(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_POWERUP, THIRD_LEVEL, Tiles.QM_WITH_COIN)
                146, 147 -> ColumnHelpers.getColumnWithTwoBlocks(BOTTOM_LEVEL, SECOND_LEVEL, Tiles.BRICK, THIRD_LEVEL, Tiles.QM_WITH_COIN)
                148 -> ColumnHelpers.getColumnWithTwoBlocks(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_COIN, THIRD_LEVEL, Tiles.QM_WITH_COIN)
                149, 150 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_COIN)

                160 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 2)
                161 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 2)

                171, 172, 173 -> ColumnHelpers.getSpaceColumn()
                177, 178 -> ColumnHelpers.getSpaceColumn()

                186 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 3)

                187, 188 -> ColumnHelpers.getSpaceColumn()

                205 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 1)
                206 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 2)
                207 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 3)
                208 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 4)
                209 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 5)
                210 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 6)
                211 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 7)
                212 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 8)
                213 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 8)

                217 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.BRICK_WITH_COIN)

                222 -> ColumnHelpers.getPrincessPeachColumn(FLOOR_LEVEL)

                else -> ColumnHelpers.getPathColumn(FLOOR_LEVEL)
            }
        }

        this.enemies = Array(LEVEL_WIDTH) { Array(15) { 0 } }
        enemies[21][FLOOR_LEVEL - 3] = Entities.Flower.NORMAL
        enemies[28][FLOOR_LEVEL - 1] = Entities.Spiky.NORMAL
        enemies[111][FLOOR_LEVEL - 1] = Entities.Spiky.NORMAL
        enemies[113][FLOOR_LEVEL - 4] = Entities.Flower.NORMAL
        enemies[129][FLOOR_LEVEL - 4] = Entities.Flower.NORMAL
        enemies[160][FLOOR_LEVEL - 2] = Entities.Flower.NORMAL
        enemies[191][FLOOR_LEVEL - 1] = Entities.Spiky.NORMAL
        enemies[193][FLOOR_LEVEL - 1] = Entities.Spiky.NORMAL
    }

}
