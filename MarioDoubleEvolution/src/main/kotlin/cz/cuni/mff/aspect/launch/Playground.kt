package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.evolution.results.LevelGenerators
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.UpdatedAgentNetwork

fun main() {
    val agent = Agents.NeuroEvolution.Stage4Level1Solver
    val levelGenerator = LevelGenerators.PCGenerator.halfSolvingNE

    val originalNetwork = (((agent as MarioAgent).controller as SimpleANNController).network as UpdatedAgentNetwork)
    val weights = originalNetwork.getNetworkWeights()

    val recreatedNetwork = UpdatedAgentNetwork(5, 5, 0, 2, 5)
    recreatedNetwork.setNetworkWeights(weights)
    recreatedNetwork.legacy = originalNetwork.legacy
    val recreatedAgent = MarioAgent(SimpleANNController(recreatedNetwork))

    val gameSimulator = GameSimulator(1400)

    compareAgents(agent, recreatedAgent)

    val levels = Array(100) { levelGenerator.generate() }

    println(levels.map { gameSimulator.playMario(agent, it, false) }.sumBy { if (it.levelFinished) 1 else 0 })
    println(levels.map { gameSimulator.playMario(recreatedAgent, it, false) }.sumBy { if (it.levelFinished) 1 else 0 })

}

fun compareAgents(original: MarioAgent, recreated: MarioAgent) {
    val originalWeights = ((original.controller as SimpleANNController).network as UpdatedAgentNetwork).getNetworkWeights()
    val recreatedWeights = ((recreated.controller as SimpleANNController).network as UpdatedAgentNetwork).getNetworkWeights()

    println(originalWeights contentEquals recreatedWeights)
}