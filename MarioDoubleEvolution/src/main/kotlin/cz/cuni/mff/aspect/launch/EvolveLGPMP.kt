package cz.cuni.mff.aspect.launch

import ch.idsia.agents.controllers.keyboard.CheaterKeyboardAgent
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.DifficultyEvaluator
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.DistanceLinearityDifficultyCompressionEvaluator
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.DistanceLinearityDifficultyCompressionMinimumEvaluator
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.HuffmanCompressionEvaluator
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.storage.ObjectStorage
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser

fun main() {
    evolvePMP()
//    playLatestPMP()
//    createDefaultPMP()
}

fun evolvePMP() {

    val agentFactory = { Agents.NEAT.Stage4Level1Solver }

    val levelEvolution = PMPLevelGeneratorEvolution(
        generationsCount = 40,
        populationSize = 50,
        fitnessFunction = DistanceLinearityDifficultyCompressionEvaluator(),
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
    val levelGenerator = ObjectStorage.load("data/latest_pmp_lg.lg") as PMPLevelGenerator
    val gameSimulator = GameSimulator()

    for (levelNumber in 0 until 5) {
        val level = levelGenerator.generate()
        cz.cuni.mff.aspect.evolution.levels.evaluators.DifficultyEvaluator()(level, GameStatistics(0f, 0, 0, 0, 0, true, true))
        LevelVisualiser().display(level)
        val postProcessed = LevelPostProcessor.postProcess(level, true)
        val agent = CheaterKeyboardAgent()
//        val agent = Agents.NEAT.Stage4Level1Solver

        gameSimulator.playMario(agent, postProcessed, true)
    }
}

fun createDefaultPMP() {
    val defaultLevel = PMPLevelGenerator().generate()
    val postProcessed = LevelPostProcessor.postProcess(defaultLevel)
    LevelVisualiser().display(postProcessed)
    GameSimulator(500000000).playMario(CheaterKeyboardAgent(), postProcessed, true)
}