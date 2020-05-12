package cz.cuni.mff.aspect.mario.level.splitting

import cz.cuni.mff.aspect.mario.level.DirectMarioLevel
import cz.cuni.mff.aspect.mario.level.MarioLevel

object LevelSplitter {

    fun splitLevel(level: MarioLevel, splits: Array<LevelSplit>): Array<MarioLevel> =
        Array(splits.size) {
            getSubLevel(level, splits[it], it)
        }

    private fun getSubLevel(level: MarioLevel, split: LevelSplit, splitIndex: Int): MarioLevel {
        val subTiles = level.tiles.sliceArray((split.xPosition..split.xPosition + split.length))
        val subEnemies = level.entities.sliceArray((split.xPosition..split.xPosition + split.length))
        return DirectMarioLevel(subTiles, subEnemies, "$level-part-${splitIndex + 1}")
    }

}