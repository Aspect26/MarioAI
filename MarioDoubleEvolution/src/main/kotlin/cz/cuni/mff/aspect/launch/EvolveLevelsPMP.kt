package cz.cuni.mff.aspect.launch

import ch.idsia.agents.controllers.keyboard.CheaterKeyboardAgent
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelEvaluators
import cz.cuni.mff.aspect.evolution.levels.pmp.ProbabilisticMultipassLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.storage.ObjectStorage
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser

fun main() {
//    evolvePMP()
    playLatestPMP()
//    createDefaultPMP()
}

fun evolvePMP() {
    val agentFactory = { Agents.NEAT.Stage4Level1Solver }

    val levelEvolution = ProbabilisticMultipassLevelGeneratorEvolution(
        generationsCount = 100,
        populationSize = 50,
        fitnessFunction = PMPLevelEvaluators::distanceDiversityEnemiesLinearity,
        evaluateOnLevelsCount = 5
    )

    val levelGenerator = levelEvolution.evolve(agentFactory)
    ObjectStorage.store("data/latest_pmp_lg.lg", levelGenerator)

    val level = levelGenerator.generate()
    val postProcessed = LevelPostProcessor.postProcess(level, true)
    LevelVisualiser().display(postProcessed)

//    val agent = CheaterKeyboardAgent()
    val agent = agentFactory()
    GameSimulator().playMario(agent, postProcessed, true)
}

fun playLatestPMP() {
    val levelGenerator = ObjectStorage.load("data/latest_pmp_lg.lg") as LevelGenerator
    val levels = Array(15) { levelGenerator.generate() }
    val gameSimulator = GameSimulator()

    for (level in levels) {
        val postProcessed = LevelPostProcessor.postProcess(level, true)
//        val agent = CheaterKeyboardAgent()
        val agent = Agents.NEAT.Stage4Level1Solver
        gameSimulator.playMario(agent, postProcessed, true)
    }
}

fun createDefaultPMP() {
    val defaultLevel = PMPLevelGenerator().generate()
    val postProcessed = LevelPostProcessor.postProcess(defaultLevel)
    LevelVisualiser().display(postProcessed)
    GameSimulator(500000000).playMario(CheaterKeyboardAgent(), postProcessed, true)
}