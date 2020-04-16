package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.evolution.results.LevelGenerators
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.storage.LevelStorage


fun main() {
    aiPlayLevel()
//    neatAiPlayLevel()
}


fun aiPlayLevel() {
    val agent = Agents.NeuroEvolution.Stage4Level1Solver
    val levelGenerator = LevelGenerators.PCGenerator.halfSolvingNE

    val gameSimulator = GameSimulator(1400)
    val stats = Array(10) { levelGenerator.generate() }.map {
        gameSimulator.playMario(agent, it, false)
    }
    println(stats.sumBy { if (it.levelFinished) 1 else 0 })
}


fun neatAiPlayLevel() {
//    val agent = MarioAgent(controller)
    val agent = Agents.NEAT.Stage4Level1Solver

    val levels = listOf<MarioLevel>(LevelStorage.loadLevel("current.lvl"))
    val simulator = GameSimulator(1000)

    for (level in levels) {
        val stats = simulator.playMario(agent, level, true)
        println(stats.jumps)
    }


}
