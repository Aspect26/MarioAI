package cz.cuni.mff.aspect.evolution.results

import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.storage.LevelStorage

/** Contains evolved Super Mario levels. */
object Levels {

    object GrammarEvolution {

        val Level1: MarioLevel = LevelStorage.loadLevel("grammar/01.lvl")
        val Level2: MarioLevel = LevelStorage.loadLevel("grammar/02.lvl")
        val Level3: MarioLevel = LevelStorage.loadLevel("grammar/03.lvl")
        val Level5: MarioLevel = LevelStorage.loadLevel("grammar/05.lvl")
        val Level6: MarioLevel = LevelStorage.loadLevel("grammar/06.lvl")

        val AllLevels: Array<MarioLevel> = arrayOf(Level1, Level2, Level3, Level5, Level6)

    }

}