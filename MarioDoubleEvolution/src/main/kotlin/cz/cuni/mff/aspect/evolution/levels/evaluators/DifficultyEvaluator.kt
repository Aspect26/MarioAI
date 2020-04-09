package cz.cuni.mff.aspect.evolution.levels.evaluators

import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.MarioLevel

class DifficultyEvaluator : LevelEvaluator<Float> {

    // TODO: decrease difficulty for each platform box
    override fun invoke(level: MarioLevel, gameStatistic: GameStatistics): Float {
        val flatEntities = level.entities.flatten()

        val enemiesDifficulty = flatEntities.sumBy {
            when(it) {
                Entities.Goomba.NORMAL -> 1
                Entities.Koopa.GREEN -> 2
                Entities.Koopa.GREEN_WINGED -> 4
                Entities.Koopa.RED -> 2
                Entities.Spiky.NORMAL -> 3
                Entities.Flower.NORMAL -> 3
                else -> 0
            }
        }

        var currentHoleLength = 0
        var holesDifficulty = 0f
        for (tilesColumn in level.tiles) {
            if (tilesColumn[tilesColumn.size - 1] == Tiles.NOTHING) {
                currentHoleLength++
            } else if (currentHoleLength > 0) {
                holesDifficulty += when (currentHoleLength) {
                    0 -> 0.0f
                    1 -> 0.5f
                    2 -> 1.5f
                    3 -> 2.0f
                    4 -> 2.5f
                    else -> 3.0f
                }
                currentHoleLength = 0
            }
        }

        var billsDifficulty = 0f
        for (tilesColumn in level.tiles) {
            val billSize = tilesColumn.filter { it == Tiles.BULLET_BLASTER_TOP || it == Tiles.BULLET_BLASTER_MIDDLE || it == Tiles.BULLET_BLASTER_BOTTOM }.size
            billsDifficulty += when (billSize) {
                0 -> 0.0f
                1 -> 1.5f
                else -> 1.0f
            }
        }

        return enemiesDifficulty + holesDifficulty + billsDifficulty
    }

}