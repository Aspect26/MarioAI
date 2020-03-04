package cz.cuni.mff.aspect.mario.level.original

import cz.cuni.mff.aspect.evolution.levels.grammar.ChunkHelpers
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
    override val enemies: Array<Array<Int>>

    init {
        this.tiles = Array(LEVEL_WIDTH) {
            when (it) {
                17 -> ChunkHelpers.getSecretBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)

                21 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)
                22 -> ChunkHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_POWERUP)
                23 -> ChunkHelpers.getBoxesAndSecretsColumn(FLOOR_LEVEL, SECOND_LEVEL, THIRD_LEVEL)
                24 -> ChunkHelpers.getSecretBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)
                25 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)

                29 -> ChunkHelpers.getPipeStartColumn(FLOOR_LEVEL, 2)
                30 -> ChunkHelpers.getPipeEndColumn(FLOOR_LEVEL, 2)

                39 -> ChunkHelpers.getPipeStartColumn(FLOOR_LEVEL, 3)
                40 -> ChunkHelpers.getPipeEndColumn(FLOOR_LEVEL, 3)

                47 -> ChunkHelpers.getPipeStartColumn(FLOOR_LEVEL, 4)
                48 -> ChunkHelpers.getPipeEndColumn(FLOOR_LEVEL, 4)

                58 -> ChunkHelpers.getPipeStartColumn(FLOOR_LEVEL, 4) // should be pipe down
                59 -> ChunkHelpers.getPipeEndColumn(FLOOR_LEVEL, 4)

                65 -> ChunkHelpers.getSecretBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL - 1)  // Should be green mroom and invisible

                70, 71 -> ChunkHelpers.getSpaceColumn()

                78 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)
                79 -> ChunkHelpers.getColumnWithBlock(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_POWERUP)
                80 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)
                81 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)
                82 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)
                83 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)
                84 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)
                85 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)
                86 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)

                87, 88 -> ChunkHelpers.getBoxesColumn(BOTTOM_LEVEL, THIRD_LEVEL)
                89 -> ChunkHelpers.getSpaceColumn()

                92, 93, 94 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)
                95 -> ChunkHelpers.getBoxesAndSecretsColumn(FLOOR_LEVEL, SECOND_LEVEL, THIRD_LEVEL) // on second level is multiple coins qm

                101 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)
                    102 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)  // should be star powerup

                107 -> ChunkHelpers.getSecretBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)
                110 -> ChunkHelpers.getColumnWithTwoBlocks(FLOOR_LEVEL, SECOND_LEVEL, Tiles.QM_WITH_COIN, THIRD_LEVEL, Tiles.QM_WITH_POWERUP)
                113 -> ChunkHelpers.getSecretBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)

                119 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)

                122, 123, 124 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)

                129 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)
                130, 131 -> ChunkHelpers.getBoxesAndSecretsColumn(FLOOR_LEVEL, SECOND_LEVEL, THIRD_LEVEL)
                132 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, THIRD_LEVEL)

                135 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 1)
                136 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 2)
                137 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 3)
                138 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 4)

                141 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 4)
                142 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 3)
                143 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 2)
                144 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 1)

                149 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 1)
                150 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 2)
                151 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 3)
                152 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 4)
                153 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 4)
                154, 155 -> ChunkHelpers.getSpaceColumn()
                156 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 4)
                157 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 3)
                158 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 2)
                159 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 1)

                164 -> ChunkHelpers.getPipeStartColumn(FLOOR_LEVEL, 2)  // should be pipe up
                165 -> ChunkHelpers.getPipeEndColumn(FLOOR_LEVEL, 2)

                169, 170 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)
                171 -> ChunkHelpers.getSecretBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)
                172 -> ChunkHelpers.getBoxesColumn(FLOOR_LEVEL, SECOND_LEVEL)

                180 -> ChunkHelpers.getPipeStartColumn(FLOOR_LEVEL, 2)
                181 -> ChunkHelpers.getPipeEndColumn(FLOOR_LEVEL, 2)

                182 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 1)
                183 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 2)
                184 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 3)
                185 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 4)
                186 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 5)
                187 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 6)
                188 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 7)
                189 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 8)
                190 -> ChunkHelpers.getStonesColumn(FLOOR_LEVEL, 8)

                199 -> ChunkHelpers.getPrincessPeachColumn(FLOOR_LEVEL)

                else -> ChunkHelpers.getPathColumn(FLOOR_LEVEL)
            }
        }

        this.enemies = Array(LEVEL_WIDTH) { Array(15) { 0 } }
        enemies[22][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[42][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[55][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[56][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[79][SECOND_LEVEL - 2] = Entities.Goomba.NORMAL
        enemies[82][THIRD_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[96][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[98][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[107][FLOOR_LEVEL - 1] = Entities.Koopa.GREEN
        enemies[124][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[125][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[127][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[128][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[174][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
        enemies[175][FLOOR_LEVEL - 1] = Entities.Goomba.NORMAL
    }

}
