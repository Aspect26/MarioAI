package cz.cuni.mff.aspect.launch

import ch.idsia.agents.controllers.keyboard.CheaterKeyboardAgent
import cz.cuni.mff.aspect.evolution.jenetics.alterers.UpdatedGaussianMutator
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.*
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.storage.ObjectStorage
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser

const val FILE_PATH_LATEST_PMP = "data/latest_pmp_lg.lg"

/**
 * Launches evolution of Probabilistic Multipass level generator / plays latest evolved generator / plays Super Mario
 * using default settings of Probabilistic Multipass level generator.
 */
fun main() {
    evolve()
//    playLatest()
//    evaluateLatest()
//    createDefault()
}

private fun evolve() {

//    val agentFactory = { MarioAgent(GoingRightController()) }
    val agentFactory = { MarioAgent(ObjectStorage.load("data/coev/16_pc_last/neuro_pc/ai_25.ai")) }

    val levelGeneratorEvolution = PMPLevelGeneratorEvolution(
        populationSize = 50,
        generationsCount = 10,
        evaluateOnLevelsCount = 10,
        fitnessFunction = All(0.5f),
        objectiveFunction = WinRatioEvaluator(0.5f, 1f),
        alterers = arrayOf(UpdatedGaussianMutator(0.5, 0.3) /*, SinglePointCrossover(0.2)*/),
        displayChart = true,
        levelLength = 300,
        chartLabel = "PMP Level Generator"
    )

    val levelGenerator = levelGeneratorEvolution.evolve(agentFactory).bestLevelGenerator
    ObjectStorage.store(FILE_PATH_LATEST_PMP, levelGenerator)
    levelGeneratorEvolution.chart.store("latest_pmp_5.svg")

    val level = levelGenerator.generate()
    val postProcessed = LevelPostProcessor.postProcess(level, true)
    LevelVisualiser().displayAndStore(postProcessed, "img/pmp_difficulty.png")

//    val agent = CheaterKeyboardAgent()
    val agent = agentFactory()
    GameSimulator().playMario(agent, postProcessed, true)
}

private fun playLatest() {
    val levelGenerator = ObjectStorage.load(FILE_PATH_LATEST_PMP) as PMPLevelGenerator
    val gameSimulator = GameSimulator()

    for (levelNumber in 0 until 5) {
        val level = levelGenerator.generate()
        cz.cuni.mff.aspect.evolution.levels.evaluators.DifficultyEvaluator()(level, GameStatistics(0f, 0, 0, 0, 0, true, true))
        LevelVisualiser().display(level)
        val postProcessed = LevelPostProcessor.postProcess(level, true)
//        val agent = CheaterKeyboardAgent()
        val agent = Agents.NeuroEvolution.Stage4Level1Solver

        gameSimulator.playMario(agent, postProcessed, true)
    }
}

private fun evaluateLatest() {
    val levelGenerator = ObjectStorage.load(FILE_PATH_LATEST_PMP) as PMPLevelGenerator
    val evaluator = DifficultyEvaluator()

    (0 until 5).forEach { _ ->
        val level = levelGenerator.generate()
        val levelMetadata = levelGenerator.lastMetadata
        val dummyGameStatistics = GameStatistics(0f, 0, 0, 0, 0, false, false)

        LevelVisualiser().display(level)
        evaluator.evaluateOne(level, levelMetadata, dummyGameStatistics)
        readLine()
    }
}

private fun createDefault() {
    val defaultLevel = PMPLevelGenerator().generate()
    val postProcessed = LevelPostProcessor.postProcess(defaultLevel)
    LevelVisualiser().display(postProcessed)
    GameSimulator(500000000).playMario(CheaterKeyboardAgent(), postProcessed, true)
}
