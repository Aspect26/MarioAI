package cz.cuni.mff.aspect.launch

import ch.idsia.agents.controllers.keyboard.CheaterKeyboardAgent
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.results.LevelGenerators
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser
import kotlin.system.exitProcess


fun main() {
    keyboardPlay()
    exitProcess(0)
}


fun keyboardPlay() {
    val levelGenerator = LevelGenerators.PMPGenerator.NEAT2
    val levels: Array<MarioLevel> = Array(15) { LevelPostProcessor.postProcess(levelGenerator.generate(), true) }

    val marioSimulator = GameSimulator(15000)

    for (level in levels) {
//        LevelVisualiser().display(level)
        val agent = CheaterKeyboardAgent()
        val stats = marioSimulator.playMario(agent, level, true)
    }
}
