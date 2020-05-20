package cz.cuni.mff.aspect.mario

import ch.idsia.benchmark.mario.engine.level.Level
import ch.idsia.benchmark.mario.engine.level.SpriteTemplate
import ch.idsia.benchmark.mario.engine.sprites.Sprite
import cz.cuni.mff.aspect.mario.level.MarioLevel

/**
 * Level generator for Super Mario Infinite simulator which take our implementation of Super Mario level ([MarioLevel])
 * and converts it to Super Mario Infinite simulator's level implementation [Level].
 *
 * Super Mario Infinite requires level generator, which supplies levels to the simulator. This implementation takes as
 * a primary constructor parameter [MarioLevel] which it always supplies to the simulator.
 */
class SingleLevelLevelGenerator(private val level: MarioLevel) : ch.idsia.benchmark.mario.engine.level.LevelGenerator {

    override fun createLevel(): Level {
        val tiles = level.tiles
        val enemies = level.entities

        val level = Level(tiles.size, tiles[0].size)
        level.type = LevelTypes.DEFAULT

        level.xExit = level.length - 1
        level.yExit = 15
        level.randomSeed = 123
        level.difficulty = 1

        for (x in tiles.indices) {
            for (y in tiles[x].indices) {
                val tile = tiles[x][y]
                level.setBlock(x, y, tile)

                if (tile == Tiles.PEACH)
                    level.setSpriteTemplate(x, y, SpriteTemplate(Sprite.KIND_PRINCESS))
            }
        }

        for (x in enemies.indices) {
            for (y in enemies[x].indices) {
                val enemy = enemies[x][y]
                if (enemy != Entities.NOTHING) {
                    level.setSpriteTemplate(x, y, SpriteTemplate(enemy))
                }
            }
        }

        return level
    }

}