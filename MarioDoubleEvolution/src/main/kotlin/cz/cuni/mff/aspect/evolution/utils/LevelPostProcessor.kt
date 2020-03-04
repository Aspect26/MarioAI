package cz.cuni.mff.aspect.evolution.utils

import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.DirectMarioLevel
import cz.cuni.mff.aspect.mario.level.MarioLevel

object LevelPostProcessor {

    fun postProcess(level: MarioLevel): MarioLevel {
        val newLevel = this.copyLevel(level)

        for (x in newLevel.tiles.indices) {
            for (y in newLevel.tiles[x].indices) {
                val currentTile = newLevel.tiles[x][y]

                if (currentTile == Tiles.DIRT) this.processDirt(newLevel, x, y)
                else if (currentTile == Tiles.GRASS_TOP) this.processTopGrass(newLevel, x ,y)
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