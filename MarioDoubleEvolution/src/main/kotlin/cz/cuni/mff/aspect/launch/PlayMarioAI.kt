package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.evolution.controller.TrainingLevelsSet
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.storage.LevelStorage
import cz.cuni.mff.aspect.storage.ObjectStorage
import kotlin.system.exitProcess


fun main() {
//    aiPlayLevel()
    neatAiPlayLevel()
    exitProcess(0)
}


fun aiPlayLevel() {
    val agent = Agents.NeuroEvolution.Stage4Level1Solver
    //val agentController = ObjectStorage.load("experiments/Newest/NeuroEvolution, experiment 1_ai.ai") as SimpleANNController
    // agentController.setLegacy()
    //val agent = MarioAgent(agentController)

//    val levels = TrainingLevelsSet
//    val levels = listOf<MarioLevel>(OnlyPathLevel) + arrayOf<MarioLevel>(*Stage4Level1Split.levels) + PathWithHolesLevel
    val levels = listOf<MarioLevel>(LevelStorage.loadLevel("current.lvl"))

    val gameSimulator = GameSimulator(1400)
    val statistics = levels.map { gameSimulator.playMario(agent, it, true) }
    print(statistics.sumBy { if (it.levelFinished) 1 else 0 })
}


fun neatAiPlayLevel() {
    val controller = ObjectStorage.load("experiments/NEAT - All - 500-100 - fitness only distance/NEAT evolution, experiment 3_ai.ai") as SimpleANNController
//    val agent = MarioAgent(controller)
    val agent = Agents.NEAT.BestGeneric

//    val levels = arrayOf<MarioLevel>(*Stage4Level1Split.levels) + PathWithHolesLevel
    val levels = TrainingLevelsSet
    val simulator = GameSimulator(1000)

    for (level in levels) {
        val stats = simulator.playMario(agent, level, true)
        println(stats.jumps)
    }


}
