package cz.cuni.mff.aspect.evolution.results

import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.storage.LevelStorage

object Levels {

    object GrammarEvolution {

        val Level1: MarioLevel = LevelStorage.loadLevel("grammar/01.lvl")
        val Level2: MarioLevel = LevelStorage.loadLevel("grammar/02.lvl")
        val Level3: MarioLevel = LevelStorage.loadLevel("grammar/03.lvl")
        val Level4: MarioLevel = LevelStorage.loadLevel("grammar/04.lvl")

        val AllLevels: Array<MarioLevel> = arrayOf(Level1, Level2, Level3, Level4)

    }

}