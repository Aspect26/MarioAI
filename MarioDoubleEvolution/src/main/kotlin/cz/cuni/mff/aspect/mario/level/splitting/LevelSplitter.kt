package cz.cuni.mff.aspect.mario.level.splitting

import cz.cuni.mff.aspect.mario.level.DirectMarioLevel
import cz.cuni.mff.aspect.mario.level.MarioLevel

/** Used for splitting [MarioLevel]s into multiple parts. */
object LevelSplitter {

    /**
     * Splits the given level into multiple parts.
     *
     * @param level the level to be split.
     * @param splits position, where the level is to be split.
     * @return resulting parts of the level represented again as [MarioLevel]s.
     */
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