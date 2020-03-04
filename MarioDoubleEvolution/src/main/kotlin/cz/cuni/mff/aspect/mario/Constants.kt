package cz.cuni.mff.aspect.mario

import ch.idsia.benchmark.mario.engine.sprites.Sprite

/**
 * Constants representing level tiles. These tiles can be found in /MarioAI4J/src/ch/idsia/benchmark/mario/engine/resources/mapsheet.png.
 * The tile number is then computed as column + row * 16 (column and row being 0-based)
 */
object Tiles {

    const val NOTHING: Byte = 0

    const val DIRT: Byte = (1 + 9 * 16).toByte()

    const val GRASS_TOP: Byte = (1 + 8 * 16).toByte()
    const val GRASS_LEFT: Byte = (0 + 9 * 16).toByte()
    const val GRASS_RIGHT: Byte = (2 + 9 * 16).toByte()
    const val GRASS_TOP_LEFT: Byte = (0 + 8 * 16).toByte()
    const val GRASS_TOP_RIGHT: Byte = (2 + 8 * 16).toByte()
    const val GRASS_CORNER_TOP_LEFT: Byte = (3 + 8 * 16).toByte()
    const val GRASS_CORNER_TOP_RIGHT: Byte = (3 + 9 * 16).toByte()
    const val GRASS_CORNER_BOTTOM_RIGHT: Byte = (3 + 10 * 16).toByte()
    const val GRASS_CORNER_BOTTOM_LEFT: Byte = (3 + 11 * 16).toByte()

    const val COIN: Byte = (0 + 2 * 16).toByte()

    const val QM_WITH_COIN: Byte = (5 + 1 * 16).toByte()
    const val QM_WITH_POWERUP: Byte = (6 + 1 * 16).toByte()

    const val EXPIRED_QM: Byte = (4 + 0 * 16).toByte()

    const val BRICK: Byte = (0 + 1 * 16).toByte()
    const val BRICK_WITH_COIN: Byte = (1 + 1 * 16).toByte()
    const val BRICK_WITH_POWERUP: Byte = (2 + 1 * 16).toByte()

    const val TEST_BLOCK = (7 + 1 * 16).toByte()

    const val PIPE_TOP_LEFT: Byte = (10 + 0 * 16).toByte()
    const val PIPE_TOP_RIGHT: Byte = (11 + 0 * 16).toByte()
    const val PIPE_MIDDLE_LEFT: Byte = (10 + 1 * 16).toByte()
    const val PIPE_MIDDLE_RIGHT: Byte = (11 + 1 * 16).toByte()

    const val BULLET_BLASTER_TOP: Byte = (14 + 0 * 16).toByte()
    const val BULLET_BLASTER_MIDDLE: Byte = (14 + 1 * 16).toByte()
    const val BULLET_BLASTER_BOTTOM: Byte = (14 + 2 * 16).toByte()

    const val STONE: Byte = (9 + 0 * 16).toByte()

    const val PEACH: Byte = (15 + 15 * 16).toByte()

}

/**
 * Wrapper constants representing all types of enemies in Mario.
 */
object Entities {

    const val NOTHING = 0

    object Goomba {
        const val NORMAL = Sprite.KIND_GOOMBA
        const val WINGED = Sprite.KIND_GOOMBA_WINGED
        const val WAVE = Sprite.KIND_WAVE_GOOMBA
    }

    object Koopa {
        const val GREEN = Sprite.KIND_GREEN_KOOPA
        const val GREEN_WINGED = Sprite.KIND_GREEN_KOOPA_WINGED
        const val RED = Sprite.KIND_RED_KOOPA
        const val RED_WINGED = Sprite.KIND_RED_KOOPA_WINGED
    }

    object Spiky {
        const val NORMAL = Sprite.KIND_SPIKY
        const val WINGED = Sprite.KIND_SPIKY_WINGED
    }

    object Flower {
        const val NORMAL = Sprite.KIND_ENEMY_FLOWER
    }

    object BulletBill {
        const val NORMAL = Sprite.KIND_BULLET_BILL
    }

    object Princess {
        const val NORMAL = Sprite.KIND_PRINCESS
    }

}

/**
 * Constants representing all Mario level types.
 */
object LevelTypes {

    const val DEFAULT: Int = 0
    const val CAVE: Int = 1
    const val CASTLE: Int = 2

}