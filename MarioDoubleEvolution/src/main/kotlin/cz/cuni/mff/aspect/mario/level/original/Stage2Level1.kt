package cz.cuni.mff.aspect.mario.level.original

import cz.cuni.mff.aspect.evolution.levels.ge.grammar.ColumnHelpers
import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.MarioLevel


object Stage2Level1 : MarioLevel {

    private const val BOTTOM_LEVEL = 15
    private const val FLOOR_LEVEL =  13
    private const val SECOND_LEVEL = 9
    private const val THIRD_LEVEL = 5
    private const val LEVEL_WIDTH = 212

    override val tiles: Array<ByteArray>
    override val enemies: Array<Array<Int>>

    init {
        this.tiles = Array(LEVEL_WIDTH) {
            when (it) {
                15, 17 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.BRICK)
                16 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.BRICK_WITH_POWERUP)

                20, 21, 22, 23, 24 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, it - 19)

                28 -> ColumnHelpers.getColumnWithTwoBlocks(FLOOR_LEVEL, SECOND_LEVEL, Tiles.BRICK_WITH_COIN, THIRD_LEVEL, Tiles.BRICK) // The coin block should be hidden and the brick should have 1UP
                29, 30, 31 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, THIRD_LEVEL, Tiles.BRICK)

                34 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 4)
                35 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 2)

                46 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 4)
                47 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 4)

                53 -> ColumnHelpers.getColumnWithTwoBlocks(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_POWERUP, THIRD_LEVEL, Tiles.QM_WITH_COIN)
                54, 55, 56, 57 -> ColumnHelpers.getColumnWithTwoBlocks(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_COIN, THIRD_LEVEL, Tiles.QM_WITH_COIN)

                68 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.BRICK)
                69 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, THIRD_LEVEL, Tiles.BRICK) // should contain star
                70, 71, 72 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, THIRD_LEVEL, Tiles.BRICK)

                74 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 4)
                75 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 4)

                79, 80 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_COIN)
                81, 82 -> ColumnHelpers.getColumnWithTwoBlocks(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_COIN, THIRD_LEVEL, Tiles.BRICK)
                83 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, THIRD_LEVEL, Tiles.BRICK) // should contain ladder to the clouds
                84 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, THIRD_LEVEL, Tiles.BRICK)
                85 -> ColumnHelpers.getColumnWithTwoBlocks(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_COIN, THIRD_LEVEL, Tiles.BRICK)
                86, 87 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_COIN)

                92, 93, 94, 95 -> ColumnHelpers.getColumnWithBlock(BOTTOM_LEVEL, THIRD_LEVEL, Tiles.BRICK)

                103 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 4) // should be pipe down
                104 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 4)

                106, 107, 108 -> ColumnHelpers.getSpaceColumn()

                115 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 2) // should be pipe up
                116 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 2)

                122 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 4)
                123 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 4)

                125 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, THIRD_LEVEL, Tiles.BRICK_WITH_POWERUP)
                126 -> ColumnHelpers.getPipeStartWithBlockColumn(FLOOR_LEVEL, 3, THIRD_LEVEL, Tiles.BRICK)
                127 -> ColumnHelpers.getPipeEndWithBlockColumn(FLOOR_LEVEL, 3, THIRD_LEVEL, Tiles.BRICK)
                128 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, THIRD_LEVEL, Tiles.BRICK)

                130 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 5)
                131 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 5)

                139, 140, 141 -> ColumnHelpers.getSpaceColumn()

                152, 153 -> ColumnHelpers.getSpaceColumn()
                154 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 3)

                161 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.BRICK_WITH_COIN)

                164, 165, 166, 167, 168 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, THIRD_LEVEL, Tiles.BRICK)
                170 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_COIN)
                172 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, THIRD_LEVEL, Tiles.BRICK_WITH_POWERUP)

                176 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 3)
                177 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 3)

                185 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.BRICK)
                186 -> ColumnHelpers.getColumnWithTwoBlocks(FLOOR_LEVEL, SECOND_LEVEL, Tiles.BRICK, THIRD_LEVEL, Tiles.BRICK_WITH_COIN) // the coin brick should be invisible

                // 188 should be some piston
                // 190 and 191 should be stone wall with height 10

                200 -> ColumnHelpers.getPrincessPeachColumn(FLOOR_LEVEL)

                else -> ColumnHelpers.getPathColumn(FLOOR_LEVEL)
            }
        }

        this.enemies = Array(LEVEL_WIDTH) { Array(15) { 0 } }
        enemies[24][FLOOR_LEVEL - 6] = Entities.Goomba.NORMAL

        enemies[32][FLOOR_LEVEL - 1] = Entities.Koopa.GREEN
        enemies[33][FLOOR_LEVEL - 1] = Entities.Koopa.GREEN

        enemies[42][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[43][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL

        enemies[46][FLOOR_LEVEL - 4] = Entities.Flower.NORMAL

        enemies[55][SECOND_LEVEL - 1] = Entities.Koopa.GREEN

        enemies[59][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[61][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[66][SECOND_LEVEL - 1] = Entities.Koopa.GREEN
        enemies[68][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[70][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[71][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL

        enemies[74][FLOOR_LEVEL - 4] = Entities.Flower.NORMAL

        enemies[87][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[89][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[90][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL

        enemies[103][FLOOR_LEVEL - 5] = Entities.Goomba.NORMAL
        enemies[103][FLOOR_LEVEL - 4] = Entities.Flower.NORMAL

        enemies[115][FLOOR_LEVEL - 5] = Entities.Goomba.NORMAL
        enemies[115][FLOOR_LEVEL - 2] = Entities.Flower.NORMAL
        enemies[120][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[122][FLOOR_LEVEL - 4] = Entities.Flower.NORMAL
        enemies[130][FLOOR_LEVEL - 5] = Entities.Flower.NORMAL

        enemies[137][SECOND_LEVEL - 1] = Entities.Koopa.GREEN

        enemies[151][SECOND_LEVEL - 3] = Entities.Koopa.GREEN_WINGED

        enemies[162][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[164][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL

        enemies[169][FLOOR_LEVEL - 1] = Entities.Koopa.GREEN_WINGED
        enemies[171][FLOOR_LEVEL - 1] = Entities.Koopa.GREEN_WINGED

        enemies[176][FLOOR_LEVEL - 3] = Entities.Flower.NORMAL

        enemies[185][FLOOR_LEVEL - 1] = Entities.Koopa.GREEN

    }

}
