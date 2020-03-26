package cz.cuni.mff.aspect.launch

import ch.idsia.agents.controllers.keyboard.CheaterKeyboardAgent
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelEvaluators
import cz.cuni.mff.aspect.evolution.levels.pmp.ProbabilisticMultipassLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.storage.LevelStorage
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser

fun main() {
//     evolve()
    createDefault()
}

fun evolve() {
    //    var agent = Agents.NeuroEvolution.Stage4Level1Solver
//    agent = Agents.RuleBased.goingRightJumping

    val agentFactory = { Agents.NEAT.Stage4Level1Solver }
    val fitness = PMPLevelEvaluators::marioDistanceAndLevelDiversity

    val levelEvolution = ProbabilisticMultipassLevelGeneratorEvolution(generationsCount = 70, fitnessFunction = fitness)
    val levelGenerator = levelEvolution.evolve(agentFactory)
    val level = levelGenerator.generate()

    val postprocessed = LevelPostProcessor.postProcess(level)
    LevelStorage.storeLevel("current.lvl", postprocessed)

    LevelVisualiser().display(postprocessed)

//    agent = CheaterKeyboardAgent()
    GameSimulator().playMario(agentFactory(), postprocessed, true)
}

fun createDefault() {
    val defaultLevel = PMPLevelGenerator().generate()
    val postProcessed = LevelPostProcessor.postProcess(defaultLevel)
    LevelVisualiser().display(postProcessed)
    GameSimulator(500000000).playMario(CheaterKeyboardAgent(), postProcessed, true)
}