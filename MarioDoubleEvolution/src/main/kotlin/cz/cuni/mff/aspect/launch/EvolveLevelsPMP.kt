package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.pmp.ProbabilisticMultipassEvolution
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.storage.LevelStorage
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser

fun main() {
    var agent = Agents.NeuroEvolution.Stage4Level1Solver
    val marioAgent = agent as MarioAgent

    val levelEvolution = ProbabilisticMultipassEvolution()
    val levels = levelEvolution.evolve(marioAgent)
    val firstLevel = levels.first()

    val postprocessed = LevelPostProcessor.postProcess(firstLevel)
    LevelStorage.storeLevel("current.lvl", postprocessed)

    LevelVisualiser().display(postprocessed)

//    agent = CheaterKeyboardAgent()
    GameSimulator().playMario(agent, postprocessed, true)

}