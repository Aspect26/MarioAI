package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.Agents
import cz.cuni.mff.aspect.evolution.levels.TrainingLevelsSet
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.NeatAgentNetwork
import cz.cuni.mff.aspect.mario.level.original.*
import cz.cuni.mff.aspect.storage.NeatAIStorage
import kotlin.system.exitProcess


fun main() {
    aiPlayLevel()
    // neatAiPlayLevel()
    exitProcess(0)
}


fun aiPlayLevel() {
    val agent = Agents.RuleBased.arnold
    // val agentController = ObjectStorage.load("experiments/Gaussian test evaluation - S4S/NeuroEvolution, Mutator 0.25_ai.ai") as SimpleANNController
    // agentController.setLegacy()
    // val agent = MarioAgent(agentController)

    // val levels = arrayOf<MarioLevel>(PathWithHolesLevel) + Stage2Level1Split.levels
    val levels = TrainingLevelsSet

    val gameSimulator = GameSimulator(400)
    val statistics = levels.map { gameSimulator.playMario(agent, it, true) }
    print(statistics.sumBy { if (it.levelFinished) 1 else 0 })
}


fun neatAiPlayLevel() {
    val genome = NeatAIStorage.loadAi(NeatAIStorage.STAGE4_LEVEL1)
    val network = NeatAgentNetwork(NetworkSettings(5, 5, 0, 2), genome)
    val controller = SimpleANNController(network)

    val levels = arrayOf(Stage4Level1)
    val simulator = GameSimulator(10000)

    for (level in levels) {
        val stats = simulator.playMario(controller, level, true)
        print(stats.jumps)
    }


}
