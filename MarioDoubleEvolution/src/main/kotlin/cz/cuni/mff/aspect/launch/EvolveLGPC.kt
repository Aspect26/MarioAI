package cz.cuni.mff.aspect.launch

import ch.idsia.agents.controllers.keyboard.CheaterKeyboardAgent
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.*
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.storage.ObjectStorage
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser

/**
 * Launches evolution of Probabilistic Chunks level generator / plays latest evolved generator / plays Super Mario
 * using default settings of Probabilistic Chunks level generator.
 */
fun main() {
    evolve()
//    playLatest()
//    createDefault()
}

private fun evolve() {
//    val agentFactory = { MarioAgent(ObjectStorage.load("data/coev/first_ai.ai") as MarioController) }
    val agentFactory = { Agents.RuleBased.goingRight }

    val levelGeneratorEvolution = PCLevelGeneratorEvolution(
        populationSize = 50,
        generationsCount = 10,
        evaluateOnLevelsCount = 10,
        fitnessFunction = All(0.5f),
        objectiveFunction = WinRatioEvaluator(0.5f, 1f),
        chunksCount = 55,
        displayChart = true
    )

    val levelGenerator = levelGeneratorEvolution.evolve(agentFactory).bestLevelGenerator
    ObjectStorage.store("data/latest_pc_lg.lg", levelGenerator)
    levelGeneratorEvolution.chart.store("latest_pc_5.svg")

    val level = levelGenerator.generate()
    val postProcessed = LevelPostProcessor.postProcess(level, true)
    LevelVisualiser().displayAndStore(postProcessed, "img/pc_complexity.png")

//    val agent = CheaterKeyboardAgent()
    val agent = agentFactory()
    GameSimulator().playMario(agent, postProcessed, true)
}

private fun playLatest() {
    val levelGenerator: PCLevelGenerator = ObjectStorage.load("data/latest_pc_lg.lg") as PCLevelGenerator
    val simulator = GameSimulator(15000)

    for (i in 0..15) {
        val level = levelGenerator.generate()
        val postProcessed = LevelPostProcessor.postProcess(level)
//        val agent = CheaterKeyboardAgent()
//        val agent = Agents.NeuroEvolution.Stage4Level1Solver
        val agent = MarioAgent(ObjectStorage.load("data/coev/first_ai.ai") as MarioController)
        LevelVisualiser().display(level)
        simulator.playMario(agent, postProcessed, true)
    }
}

private fun createDefault() {
    val levelGenerator = PCLevelGenerator()
    val gameSimulator = GameSimulator(500000000)

    for (i in 0 .. 10) {
        val agent = CheaterKeyboardAgent()
        val defaultLevel = levelGenerator.generate()

        LevelVisualiser().display(defaultLevel)
        val postProcessed = LevelPostProcessor.postProcess(defaultLevel)
        gameSimulator.playMario(agent, postProcessed, true)
    }
}