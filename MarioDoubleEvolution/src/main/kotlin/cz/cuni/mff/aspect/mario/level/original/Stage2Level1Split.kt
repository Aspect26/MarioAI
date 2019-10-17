package cz.cuni.mff.aspect.mario.level.original

import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.mario.level.splitting.LevelSplit
import cz.cuni.mff.aspect.mario.level.splitting.LevelSplitter

object Stage2Level1Split {

    val levels: Array<MarioLevel>

    init {
        val splits = arrayOf(
            LevelSplit(0, 50),
            LevelSplit(50, 50),
            LevelSplit(96, 50),
            LevelSplit(142, 50)
        )

        levels = LevelSplitter.splitLevel(Stage2Level1, splits)
    }

}