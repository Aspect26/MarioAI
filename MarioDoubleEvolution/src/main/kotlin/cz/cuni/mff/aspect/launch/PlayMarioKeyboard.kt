package cz.cuni.mff.aspect.launch

import ch.idsia.agents.controllers.keyboard.CheaterKeyboardAgent
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.results.Levels
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.storage.LevelStorage
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser
import kotlin.system.exitProcess


fun main() {
    keyboardPlay()
    exitProcess(0)
}


fun keyboardPlay() {
//     val levels = Stage2Level1Split.levels.sliceArray(1..1)
//    val levels = arrayOf(Stage5Level1Split.levels[5])
//    val levels = arrayOf(Levels.GrammarEvolution.Level3)
//    val levels = Levels.GrammarEvolution.AllLevels

    val experimentName = "firstManyTest"
//    val levels = arrayOf(
//        LevelStorage.loadLevel("experiments/$experimentName/0.lvl")
//    )

//    val levels = arrayOf(LevelStorage.loadLevel("current.lvl"))
    val levels = Levels.GrammarEvolution.AllLevels

    val marioSimulator = GameSimulator(15000)

    for (level in levels) {
        LevelVisualiser().display(level)
        val agent = CheaterKeyboardAgent()
        val stats = marioSimulator.playMario(agent, level, true)
    }
}
