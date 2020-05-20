package cz.cuni.mff.aspect.mario.level.original

import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.mario.level.splitting.LevelSplit
import cz.cuni.mff.aspect.mario.level.splitting.LevelSplitter

/** Implementation of original Super Mario Bros game's Level 1 from Stage 1 split into multiple parts. */
object Stage1Level1Split {

    val levels: Array<MarioLevel>

    init {
        val splits = arrayOf(
            LevelSplit(0, 69),
            LevelSplit(60, 40),
            LevelSplit(102, 44),
            LevelSplit(143, 60)
        )

        levels = LevelSplitter.splitLevel(Stage1Level1, splits)
    }

}
