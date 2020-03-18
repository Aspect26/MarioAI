package cz.cuni.mff.aspect.launch

import ch.idsia.agents.controllers.keyboard.CheaterKeyboardAgent
import cz.cuni.mff.aspect.agents.GoingRightAgent
import cz.cuni.mff.aspect.agents.GoingRightAndJumpingAgent
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelCreator
import cz.cuni.mff.aspect.evolution.levels.pmp.ProbabilisticMultipassEvolution
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.storage.LevelStorage
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser

fun main() {
    // evolve()
    createDefault()
}

fun evolve() {
    //    var agent = Agents.NeuroEvolution.Stage4Level1Solver
//    agent = Agents.RuleBased.goingRightJumping

    val levelEvolution = ProbabilisticMultipassEvolution()
    val levels = levelEvolution.evolve { Agents.RuleBased.goingRightJumping }
    val firstLevel = levels.first()

    val postprocessed = LevelPostProcessor.postProcess(firstLevel)
    LevelStorage.storeLevel("current.lvl", postprocessed)

    LevelVisualiser().display(postprocessed)

//    agent = CheaterKeyboardAgent()
    GameSimulator().playMario(Agents.RuleBased.goingRightJumping, postprocessed, true)
}

fun createDefault() {
    val defaultLevel = PMPLevelCreator.createDefault()
    val postProcessed = LevelPostProcessor.postProcess(defaultLevel)
    LevelVisualiser().display(postProcessed)
    GameSimulator(500000000).playMario(CheaterKeyboardAgent(), postProcessed, true)
}