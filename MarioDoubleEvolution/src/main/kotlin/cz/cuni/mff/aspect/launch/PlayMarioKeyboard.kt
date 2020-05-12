package cz.cuni.mff.aspect.launch

import ch.idsia.agents.controllers.keyboard.CheaterKeyboardAgent
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.evolution.results.LevelGenerators
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser
import kotlin.system.exitProcess


fun main() {
    keyboardPlay()
}


fun keyboardPlay() {
    val levelGenerator = PMPLevelGenerator()
    val levels: Array<MarioLevel> = Array(15) { LevelPostProcessor.postProcess(levelGenerator.generate(), true) }

    val marioSimulator = GameSimulator(2500)

    for (level in levels) {
//        val agent = Agents.NEAT.Stage4Level1Solver
        LevelVisualiser().display(level)
        val agent = CheaterKeyboardAgent()
        marioSimulator.playMario(agent, level, true)
    }
}
