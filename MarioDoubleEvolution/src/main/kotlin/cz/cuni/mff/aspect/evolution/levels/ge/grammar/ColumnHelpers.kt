package cz.cuni.mff.aspect.evolution.levels.ge.grammar

import cz.cuni.mff.aspect.mario.Tiles


object ColumnHelpers {

    private const val DEFAULT_HEIGHT: Int = 15

    fun getSpaceColumn(height: Int = this.DEFAULT_HEIGHT): ByteArray =
        ByteArray(height) { Tiles.NOTHING }

    fun getPathColumn(level: Int, height: Int = this.DEFAULT_HEIGHT): ByteArray = ByteArray(height) {
        when {
            it < level -> Tiles.NOTHING
            it > level -> Tiles.DIRT
            else -> Tiles.GRASS_TOP
        }
    }

    fun getPlatformColumn(level: Int, platformLevel: Int, platformBlockType: Byte): ByteArray = ByteArray(15) {
        when {
            it == platformLevel -> platformBlockType
            it == level -> Tiles.GRASS_TOP
            it > level -> Tiles.DIRT
            else -> Tiles.NOTHING
        }
    }

    fun getBoxesColumn(level: Int, boxesLevel: Int): ByteArray = ByteArray(15) {
        when {
            it == boxesLevel -> Tiles.BRICK
            it == level -> Tiles.GRASS_TOP
            it > level -> Tiles.DIRT
            else -> Tiles.NOTHING
        }
    }

    fun getSecretBoxesColumn(level: Int, secretsLevel: Int): ByteArray = ByteArray(15) {
        when {
            it == secretsLevel -> Tiles.QM_WITH_COIN
            it == level -> Tiles.GRASS_TOP
            it > level -> Tiles.DIRT
            else -> Tiles.NOTHING
        }
    }

    fun getSecretPowerUpColumn(level: Int, secretLevel: Int): ByteArray = ByteArray(15) {
        when {
            it == secretLevel -> Tiles.QM_WITH_POWERUP
            it == level -> Tiles.GRASS_TOP
            it > level -> Tiles.DIRT
            else -> Tiles.NOTHING
        }
    }

    fun getBoxesAndSecretsColumn(level: Int, boxesLevel: Int, secretsLevel: Int): ByteArray = ByteArray(15) {
        when {
            it == boxesLevel -> Tiles.BRICK
            it == secretsLevel -> Tiles.QM_WITH_COIN
            it == level -> Tiles.GRASS_TOP
            it > level -> Tiles.DIRT
            else -> Tiles.NOTHING
        }
    }

    fun getDoubleSecretsColumn(level: Int, firstSecretsLevel: Int, secondSecretsLevel: Int): ByteArray = ByteArray(15) {
        when {
            it == firstSecretsLevel -> Tiles.QM_WITH_COIN
            it == secondSecretsLevel -> Tiles.QM_WITH_COIN
            it == level -> Tiles.GRASS_TOP
            it > level -> Tiles.DIRT
            else -> Tiles.NOTHING
        }
    }

    fun getColumnWithBlock(floor: Int, blockLevel: Int, blockType: Byte): ByteArray = ByteArray(15) {
        when {
            it == blockLevel -> blockType
            it == floor -> Tiles.GRASS_TOP
            it > floor -> Tiles.DIRT
            else -> Tiles.NOTHING
        }
    }

    fun getColumnWithTwoBlocks(floor: Int, firstBlockLevel: Int, firstBlockType: Byte, secondBlockLevel: Int, secondBlockType: Byte): ByteArray = ByteArray(15) {
        when {
            it == secondBlockLevel -> secondBlockType
            it == firstBlockLevel -> firstBlockType
            it == floor -> Tiles.GRASS_TOP
            it > floor -> Tiles.DIRT
            else -> Tiles.NOTHING
        }
    }


    fun getPipeStartColumn(level: Int, height: Int): ByteArray = ByteArray(15) {
        when {
            it == level -> Tiles.GRASS_TOP
            it > level -> Tiles.DIRT
            it == level - height -> Tiles.PIPE_TOP_LEFT
            it < level && it > level - height -> Tiles.PIPE_MIDDLE_LEFT
            else -> Tiles.NOTHING
        }
    }

    fun getPipeStartWithBlockColumn(level: Int, height: Int, blockLevel: Int, blockType: Byte, lift: Int = 0): ByteArray = ByteArray(15) {
        when {
            it == blockLevel -> blockType
            it == level -> Tiles.GRASS_TOP
            it > level -> Tiles.DIRT
            it == level - height - lift -> Tiles.PIPE_TOP_LEFT
            it < level - lift && it > level - height - lift -> Tiles.PIPE_MIDDLE_LEFT
            else -> Tiles.NOTHING
        }
    }

    fun getPipeEndColumn(level: Int, height: Int): ByteArray = ByteArray(15) {
        when {
            it == level -> Tiles.GRASS_TOP
            it > level -> Tiles.DIRT
            it == level - height -> Tiles.PIPE_TOP_RIGHT
            it < level && it > level - height -> Tiles.PIPE_MIDDLE_RIGHT
            else -> Tiles.NOTHING
        }
    }

    fun getPipeEndWithBlockColumn(level: Int, height: Int, blockLevel: Int, blockType: Byte, lift: Int = 0): ByteArray = ByteArray(15) {
        when {
            it == blockLevel -> blockType
            it == level -> Tiles.GRASS_TOP
            it > level -> Tiles.DIRT
            it == level - height - lift -> Tiles.PIPE_TOP_RIGHT
            it < level - lift && it > level - height - lift -> Tiles.PIPE_MIDDLE_RIGHT
            else -> Tiles.NOTHING
        }
    }

    fun getStonesColumn(level: Int, height: Int, lift: Int = 0): ByteArray = ByteArray(15) {
        when {
            it == level -> Tiles.GRASS_TOP
            it > level -> Tiles.DIRT
            it < level - lift && it >= level - height - lift -> Tiles.STONE
            else -> Tiles.NOTHING
        }
    }

    fun getBlasterBulletBillColumn(level: Int, height: Int): ByteArray = ByteArray(15) {
        when {
            it == level -> Tiles.GRASS_TOP
            it > level -> Tiles.DIRT
            it == level - height -> Tiles.BULLET_BLASTER_TOP
            it == level - height + 1 -> Tiles.BULLET_BLASTER_MIDDLE
            it >= level - height + 2 && it < level -> Tiles.BULLET_BLASTER_BOTTOM
            else -> Tiles.NOTHING
        }
    }

    fun getPrincessPeachColumn(level: Int): ByteArray = ByteArray(15) {
        when {
            it == level - 1 -> Tiles.PEACH
            it == level -> Tiles.GRASS_TOP
            it > level -> Tiles.DIRT
            else -> Tiles.NOTHING
        }
    }

}
