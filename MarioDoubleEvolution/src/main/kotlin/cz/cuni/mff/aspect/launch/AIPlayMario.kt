package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.Agents
import cz.cuni.mff.aspect.evolution.levels.TrainingLevelsSet
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.NeatAgentNetwork
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.mario.level.custom.PathWithHolesLevel
import cz.cuni.mff.aspect.mario.level.original.*
import cz.cuni.mff.aspect.storage.NeatAIStorage
import cz.cuni.mff.aspect.storage.ObjectStorage
import kotlin.system.exitProcess


fun main() {
//    aiPlayLevel()
    neatAiPlayLevel()
    exitProcess(0)
}


fun aiPlayLevel() {
    //val agent = Agents.RuleBased.arnold
    val agentController = ObjectStorage.load("experiments/Newest/NeuroEvolution, experiment 1_ai.ai") as SimpleANNController
    // agentController.setLegacy()
    val agent = MarioAgent(agentController)

    // val levels = arrayOf<MarioLevel>(PathWithHolesLevel) + Stage2Level1Split.levels
    val levels = arrayOf<MarioLevel>(*Stage4Level1Split.levels) + PathWithHolesLevel

    val gameSimulator = GameSimulator(1400)
    val statistics = levels.map { gameSimulator.playMario(agent, it, true) }
    print(statistics.sumBy { if (it.levelFinished) 1 else 0 })
}


fun neatAiPlayLevel() {
    val controller = ObjectStorage.load("experiments/NEAT - Newest/NEAT evolution, experiment 1_ai.ai") as SimpleANNController

    val levels = arrayOf<MarioLevel>(*Stage4Level1Split.levels) + PathWithHolesLevel
    val simulator = GameSimulator(1000)

    for (level in levels) {
        val stats = simulator.playMario(controller, level, true)
        print(stats.jumps)
    }


}
