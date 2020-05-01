package cz.cuni.mff.aspect.mario.level.original

import cz.cuni.mff.aspect.evolution.levels.pc.chunks.ColumnHelpers
import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.MarioLevel


object Stage1Level1 : MarioLevel {

    private const val BOTTOM_LEVEL = 15
    private const val FLOOR_LEVEL =  13
    private const val SECOND_LEVEL = 9
    private const val THIRD_LEVEL = 5
    private const val LEVEL_WIDTH = 212

    override val tiles: Array<ByteArray>
    override val entities: Array<IntArray>

    init {
        this.tiles = Array(LEVEL_WIDTH) {
            when (it) {
                17 -> ColumnHelpers.getSecretBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)

                21 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)
                22 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_POWERUP)
                23 -> ColumnHelpers.getBoxesAndSecretsColumn(FLOOR_LEVEL, SECOND_LEVEL, THIRD_LEVEL)
                24 -> ColumnHelpers.getSecretBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)
                25 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)

                29 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 2)
                30 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 2)

                39 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 3)
                40 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 3)

                47 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 4)
                48 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 4)

                58 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 4) // should be pipe down
                59 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 4)

                65 -> ColumnHelpers.getSecretBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL - 1)  // Should be green mroom and invisible

                70, 71 -> ColumnHelpers.getSpaceColumn()

                78 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)
                79 -> ColumnHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_POWERUP)
                80 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)
                81 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)
                82 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)
                83 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)
                84 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)
                85 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)
                86 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)

                87, 88 -> ColumnHelpers.getBoxesColumn(BOTTOM_LEVEL, THIRD_LEVEL)
                89 -> ColumnHelpers.getSpaceColumn()

                92, 93, 94 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)
                95 -> ColumnHelpers.getBoxesAndSecretsColumn(FLOOR_LEVEL, SECOND_LEVEL, THIRD_LEVEL) // on second level is multiple coins qm

                101 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)
                    102 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)  // should be star powerup

                107 -> ColumnHelpers.getSecretBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)
                110 -> ColumnHelpers.getColumnWithTwoBlocks(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_COIN, THIRD_LEVEL, Tiles.QM_WITH_POWERUP)
                113 -> ColumnHelpers.getSecretBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)

                119 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)

                122, 123, 124 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)

                129 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)
                130, 131 -> ColumnHelpers.getBoxesAndSecretsColumn(FLOOR_LEVEL, SECOND_LEVEL, THIRD_LEVEL)
                132 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)

                135 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 1)
                136 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 2)
                137 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 3)
                138 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 4)

                141 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 4)
                142 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 3)
                143 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 2)
                144 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 1)

                149 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 1)
                150 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 2)
                151 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 3)
                152 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 4)
                153 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 4)
                154, 155 -> ColumnHelpers.getSpaceColumn()
                156 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 4)
                157 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 3)
                158 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 2)
                159 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 1)

                164 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 2)  // should be pipe up
                165 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 2)

                169, 170 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)
                171 -> ColumnHelpers.getSecretBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)
                172 -> ColumnHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)

                180 -> ColumnHelpers.getPipeStartColumn(FLOOR_LEVEL, 2)
                181 -> ColumnHelpers.getPipeEndColumn(FLOOR_LEVEL, 2)

                182 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 1)
                183 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 2)
                184 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 3)
                185 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 4)
                186 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 5)
                187 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 6)
                188 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 7)
                189 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 8)
                190 -> ColumnHelpers.getStonesColumn(FLOOR_LEVEL, 8)

                199 -> ColumnHelpers.getPrincessPeachColumn(FLOOR_LEVEL)

                else -> ColumnHelpers.getPathColumn(FLOOR_LEVEL)
            }
        }

        this.entities = Array(LEVEL_WIDTH) { IntArray(15) { 0 } }
        entities[22][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[42][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[55][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[56][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[79][SECOND_LEVEL - 2] = Entities.Goomba.NORMAL
        entities[82][THIRD_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[96][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[98][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[107][FLOOR_LEVEL - 1] = Entities.Koopa.GREEN
        entities[124][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[125][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[127][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[128][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[174][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        entities[175][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
    }

}
