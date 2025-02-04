package cz.cuni.mff.aspect.launch

import ch.idsia.agents.controllers.keyboard.CheaterKeyboardAgent
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser

/** Launches Super Mario game using specified level generator and keyboard controlled agent. */
fun main() {
    keyboardPlay()
}


private fun keyboardPlay() {
    val levelGenerator = PMPLevelGenerator()
//    val levelGenerator = PCLevelGenerator(chunksInLevelCount = 55)
    val levels: Array<MarioLevel> = Array(15) { LevelPostProcessor.postProcess(levelGenerator.generate(), true) }

    val marioSimulator = GameSimulator(2500)

    for (level in levels) {
//        val agent = Agents.NEAT.Stage4Level1Solver
//        LevelVisualiser().display(level)
        val agent = CheaterKeyboardAgent()
        marioSimulator.playMario(agent, level, true)
    }
}
