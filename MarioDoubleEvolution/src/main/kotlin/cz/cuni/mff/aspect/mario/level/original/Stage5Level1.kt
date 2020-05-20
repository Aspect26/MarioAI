package cz.cuni.mff.aspect.mario.level.original

import cz.cuni.mff.aspect.evolution.levels.ge.grammar.ColumnHelpers
import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.MarioLevel

/** Implementation of original Super Mario Bros game's Level 1 from Stage 5. */
object Stage5Level1 : MarioLevel {

    private const val BOTTOM_LEVEL = 15
    private const val FLOOR_LEVEL =  13
    private const val SECOND_LEVEL = 9
    private const val THIRD_LEVEL = 5
    private const val LEVEL_WIDTH = 207

    override fun toString(): String = "Stage5Level1"

    override val tiles: Array<ByteArray>
    override val entities: Array<IntArray>

    init {
        this.tiles = Array(LEVEL_WIDTH) {
            when (it) {
                44 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 3)
                45 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 3)

                49, 50 -> ColumnHelpers.getSpaceColumn()
                51 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 3)
                52 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 3)

                89 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 4)
                90, 91 -> ColumnHelpers.getColumnWithTwoBlocks(FLOOR_LEVEL, SECOND_LEVEL, Tiles.STONE, THIRD_LEVEL, Tiles.BRICK) // the second brick should have star
                92 -> ColumnHelpers.getColumnWithTwoBlocks(BOTTOM_LEVEL, SECOND_LEVEL, Tiles.STONE, THIRD_LEVEL, Tiles.BRICK)
                93 -> ColumnHelpers.getColumnWithBlock(BOTTOM_LEVEL, SECOND_LEVEL, Tiles.STONE)
                94, 95 -> ColumnHelpers.getSpaceColumn()

                111 -> ColumnHelpers.getBlasterBulletBillColumn(FLOOR_LEVEL, 2)

                114, 115 -> ColumnHelpers.getSpaceColumn()
                116 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 3)

                147 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 4)

                149, 150 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.BRICK)

                152, 153, 154 -> ColumnHelpers.getSpaceColumn()

                156 -> ColumnHelpers.getPipeStartWithBlockColumn(FLOOR_LEVEL, 2, SECOND_LEVEL, Tiles.STONE, 4)
                157 -> ColumnHelpers.getPipeEndWithBlockColumn(FLOOR_LEVEL, 2, SECOND_LEVEL, Tiles.STONE, 4)

                159 -> ColumnHelpers.getBlasterBulletBillColumn(FLOOR_LEVEL, 2)

                163 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 2)
                164 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 2)

                170 -> ColumnHelpers.getBlasterBulletBillColumn(FLOOR_LEVEL, 2)

                182 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 1)
                183 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 2)
                184 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 3)
                185 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 4)
                186 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 5)

                189, 190 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 6, 2)

                199 -> ColumnHelpers.getPrincessPeachColumn(FLOOR_LEVEL)

                else -> ColumnHelpers.getPathColumn(FLOOR_LEVEL)
            }
        }

        this.entities = Array(LEVEL_WIDTH) { IntArray(15) { 0 } }
        entities[16][FLOOR_LEVEL - 1] = Entities.Koopa.GREEN

        entities[19][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[20][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[21][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL

        entities[30][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[31][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[32][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL

        entities[41][FLOOR_LEVEL - 1] = Entities.Koopa.GREEN_WINGED
        entities[43][FLOOR_LEVEL - 1] = Entities.Koopa.GREEN_WINGED

        entities[44][FLOOR_LEVEL - 3] = Entities.Flower.NORMAL

        entities[51][FLOOR_LEVEL - 3] = Entities.Flower.NORMAL

        entities[61][FLOOR_LEVEL - 4] = Entities.Koopa.GREEN_WINGED

        entities[65][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[66][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[67][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL

        entities[76][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[77][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[78][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL

        entities[87][FLOOR_LEVEL - 2] = Entities.Koopa.GREEN_WINGED

        entities[103][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[104][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[105][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL

        entities[121][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[122][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[123][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL

        entities[127][FLOOR_LEVEL - 1] = Entities.Koopa.GREEN

        entities[135][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[136][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[137][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL

        entities[144][FLOOR_LEVEL - 1] = Entities.Koopa.GREEN
        entities[146][FLOOR_LEVEL - 1] = Entities.Koopa.GREEN

        entities[156][FLOOR_LEVEL - 6] = Entities.Flower.NORMAL

        entities[163][FLOOR_LEVEL - 2] = Entities.Flower.NORMAL

        entities[178][FLOOR_LEVEL - 1] = Entities.Koopa.GREEN_WINGED
        entities[182][FLOOR_LEVEL - 5] = Entities.Koopa.GREEN_WINGED
    }

}
