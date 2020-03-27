package cz.cuni.mff.aspect.launch

import ch.idsia.agents.controllers.keyboard.CheaterKeyboardAgent
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.chunks.ChunksLevelGeneratorGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelEvaluators
import cz.cuni.mff.aspect.evolution.levels.chunks.ProbabilisticChunksLevelGenerator
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.storage.ObjectStorage
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser

fun main() {
    evolvePC()
//    playLatest()
//    createDefaultPc()
}

fun evolvePC() {
    val agentFactory = { Agents.NEAT.Stage4Level1Solver }

    val levelGeneratorEvolution = ChunksLevelGeneratorGeneratorEvolution(
        populationSize = 50,
        generationsCount = 75,
        evaluateOnLevelsCount = 5,
        fitnessFunction = PCLevelEvaluators::marioDistanceAndDiversity)

    val levelGenerator = levelGeneratorEvolution.evolve(agentFactory)
    ObjectStorage.store("data/latest_pc_lg.lg", levelGenerator)

    val level = levelGenerator.generate()
    val postProcessed = LevelPostProcessor.postProcess(level, true)
    LevelVisualiser().display(postProcessed)

//    val agent = CheaterKeyboardAgent()
    val agent = agentFactory()
    GameSimulator().playMario(agent, postProcessed, true)
}

fun playLatest() {
    val generator: LevelGenerator = ObjectStorage.load("data/latest_pc_lg.lg") as LevelGenerator
    val level = generator.generate()
    val postProcessed = LevelPostProcessor.postProcess(level, true)
    val agent = CheaterKeyboardAgent()
    GameSimulator().playMario(agent, postProcessed, true)
}

fun createDefaultPc() {
    val levelGenerator = ProbabilisticChunksLevelGenerator()

    val defaultLevel = levelGenerator.generate()
    val chunks = levelGenerator.lastChunkNames

    println(chunks.joinToString(", "))

    val postProcessed = LevelPostProcessor.postProcess(defaultLevel)
    LevelVisualiser().display(postProcessed)
    GameSimulator(500000000).playMario(CheaterKeyboardAgent(), postProcessed, true)
}