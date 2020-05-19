package cz.cuni.mff.aspect.launch

import ch.idsia.agents.controllers.keyboard.CheaterKeyboardAgent
import cz.cuni.mff.aspect.controllers.GoingRightAndJumpingController
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
    evolvePMP()
//    playLatestPMP()
//    evaluateLatestPMP()
//    createDefaultPMP()
}

private fun evolvePMP() {

    val agentFactory = { MarioAgent(GoingRightAndJumpingController()) }

    val levelEvolution = PMPLevelGeneratorEvolution(
        generationsCount = 20,
        populationSize = 50,
        fitnessFunction = PNGCompressionEvaluator(),
        objectiveFunction = NullEvaluator(),
        evaluateOnLevelsCount = 36,
        levelLength = 300
    )

    val levelGenerator = levelEvolution.evolve(agentFactory)
    ObjectStorage.store(FILE_PATH_LATEST_PMP, levelGenerator)

    val level = levelGenerator.generate()
    val postProcessed = LevelPostProcessor.postProcess(level, true)
    LevelVisualiser().display(postProcessed)

//    val agent = CheaterKeyboardAgent()
    val agent = agentFactory()
    GameSimulator().playMario(agent, postProcessed, true)
}

private fun playLatestPMP() {
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

private fun evaluateLatestPMP() {
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

private fun createDefaultPMP() {
    val defaultLevel = PMPLevelGenerator().generate()
    val postProcessed = LevelPostProcessor.postProcess(defaultLevel)
    LevelVisualiser().display(postProcessed)
    GameSimulator(500000000).playMario(CheaterKeyboardAgent(), postProcessed, true)
}