package cz.cuni.mff.aspect.evolution.utils

import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.DirectMarioLevel
import cz.cuni.mff.aspect.mario.level.MarioLevel
import java.util.*

object LevelPostProcessor {

    private val random: Random = Random()

    private const val BUSHES_PROBABILITY = 0.05f
    private const val ENV_GRASS_PROBABILITY = 0.07f

    fun postProcess(level: MarioLevel, environment: Boolean = false): MarioLevel {
        val newLevel = this.copyLevel(level)

        for (x in newLevel.tiles.indices) {
            for (y in newLevel.tiles[x].indices) {
                val currentTile = newLevel.tiles[x][y]

                if (currentTile == Tiles.DIRT) this.processDirt(newLevel, x, y)
                else if (currentTile == Tiles.GRASS_TOP) this.processTopGrass(newLevel, x ,y)
            }
        }

        if (!environment) return newLevel

        for (x in newLevel.tiles.indices) {
            for (y in newLevel.tiles[x].indices) {
                this.tryAddEnvironment(newLevel, x, y)
            }
        }

        return newLevel
    }

    private fun processDirt(level: MarioLevel, x: Int, y: Int) {
        if (level.tiles.size - 1 > x) {
            when (level.tiles[x + 1][y]) {
                Tiles.NOTHING -> level.tiles[x][y] = Tiles.GRASS_RIGHT
                Tiles.GRASS_TOP -> level.tiles[x][y] = Tiles.GRASS_CORNER_TOP_RIGHT
            }
        }

        if (x > 0) {
            when (level.tiles[x - 1][y]) {
                Tiles.NOTHING -> level.tiles[x][y] = Tiles.GRASS_LEFT
                Tiles.GRASS_TOP -> level.tiles[x][y] = Tiles.GRASS_CORNER_TOP_LEFT
            }
        }
    }

    private fun processTopGrass(level: MarioLevel, x: Int, y: Int) {
        // right end
        if (level.tiles.size - 1 > x && level.tiles[x + 1][y] == Tiles.NOTHING)
            level.tiles[x][y] = Tiles.GRASS_TOP_RIGHT

        // left end
        if (x > 0 && level.tiles[x - 1][y] == Tiles.NOTHING)
            level.tiles[x][y] = Tiles.GRASS_TOP_LEFT
    }

    private fun tryAddEnvironment(level: MarioLevel, x: Int, y: Int) {
        this.tryAddStartArrow(level, x, y)
        this.tryAddBushes(level, x, y)
        this.tryAddEnvGrass(level, x, y)
    }

    private fun tryAddStartArrow(level: MarioLevel, x: Int, y: Int) {
        if (x == 2 && level.tiles[x][y] == Tiles.GRASS_TOP) {
            level.tiles[x][y - 1] = Tiles.ARROW_BOTTOM_LEFT
            level.tiles[x + 1][y - 1] = Tiles.ARROW_BOTTOM_RIGHT
            level.tiles[x][y - 2] = Tiles.ARROW_TOP_LEFT
            level.tiles[x + 1][y - 2] = Tiles.ARROW_TOP_RIGHT
        }
    }

    private fun tryAddBushes(level: MarioLevel, x: Int, y: Int) {
        if (x > level.tiles.size - 6) return
        if (level.tiles[x][y] != Tiles.GRASS_TOP || level.tiles[x + 1][y] != Tiles.GRASS_TOP || level.tiles[x + 2][y] != Tiles.GRASS_TOP) return
        if (level.tiles[x][y - 1] != Tiles.NOTHING || level.tiles[x + 1][y - 1] != Tiles.NOTHING || level.tiles[x + 2][y - 1] != Tiles.NOTHING) return
        if (this.random.nextFloat() > this.BUSHES_PROBABILITY) return

        level.tiles[x][y - 1] = Tiles.BUSH_START
        level.tiles[x + 1][y - 1] = Tiles.BUSH_MIDDLE
        level.tiles[x + 2][y - 1] = Tiles.BUSH_END
    }

    private fun tryAddEnvGrass(level: MarioLevel, x: Int, y: Int) {
        if (x > level.tiles.size - 6) return
        if (level.tiles[x][y] != Tiles.GRASS_TOP || level.tiles[x + 1][y] != Tiles.GRASS_TOP || level.tiles[x + 2][y] != Tiles.GRASS_TOP) return
        if (level.tiles[x][y - 1] != Tiles.NOTHING || level.tiles[x + 1][y - 1] != Tiles.NOTHING || level.tiles[x + 2][y - 1] != Tiles.NOTHING) return
        if (this.random.nextFloat() > this.ENV_GRASS_PROBABILITY) return

        level.tiles[x][y - 1] = Tiles.ENV_GRASS_START
        level.tiles[x + 1][y - 1] = Tiles.ENV_GRASS_MIDDLE
        level.tiles[x + 2][y - 1] = Tiles.ENV_GRASS_END
    }

    private fun copyLevel(level: MarioLevel): MarioLevel {
        val tiles: Array<ByteArray> = Array(level.tiles.size) { column ->
            ByteArray(level.tiles[column].size) {row ->
                level.tiles[column][row]
            }
        }

        val enemies: Array<Array<Int>> = Array(level.enemies.size) { column ->
            Array(level.enemies[column].size) {row ->
                level.enemies[column][row]
            }
        }

        return DirectMarioLevel(tiles, enemies)
    }

}