package cz.cuni.mff.aspect.mario.level.original

import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.mario.level.splitting.LevelSplit
import cz.cuni.mff.aspect.mario.level.splitting.LevelSplitter

/** Implementation of original Super Mario Bros game's Level 1 from Stage 5 split into multiple parts. */
object Stage5Level1Split {

    val levels: Array<MarioLevel>

    init {
        val splits = arrayOf(
            LevelSplit(0, 60),
            LevelSplit(33, 50),
            LevelSplit(69, 50),
            LevelSplit(95, 56),
            LevelSplit(128, 50),
            LevelSplit(157, 49)
        )

        levels = LevelSplitter.splitLevel(Stage5Level1, splits)
    }

}