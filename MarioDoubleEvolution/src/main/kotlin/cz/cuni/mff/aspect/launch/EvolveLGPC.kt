package cz.cuni.mff.aspect.launch

import ch.idsia.agents.controllers.keyboard.CheaterKeyboardAgent
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.chunks.ChunksLevelGeneratorGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.*
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.storage.ObjectStorage
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser

fun main() {
    evolvePC()
//    playLatestPC()
//    createDefaultPC()
}

fun evolvePC() {
    val agentFactory = { MarioAgent(ObjectStorage.load("data/coev/first_ai.ai") as MarioController) }
//    val agentFactory = { Agents.NEAT.Stage4Level1Solver }

    val levelGeneratorEvolution = ChunksLevelGeneratorGeneratorEvolution(
        populationSize = 50,
        generationsCount = 20,
        evaluateOnLevelsCount = 10,
        fitnessFunction = AgentHalfPassing()
    )

    val levelGenerator = levelGeneratorEvolution.evolve(agentFactory)
    ObjectStorage.store("data/latest_pc_lg.lg", levelGenerator)

    val level = levelGenerator.generate()
    val postProcessed = LevelPostProcessor.postProcess(level, true)
    LevelVisualiser().display(postProcessed)

//    val agent = CheaterKeyboardAgent()
    val agent = agentFactory()
    GameSimulator().playMario(agent, postProcessed, true)
}

fun playLatestPC() {
    val levelGenerator: PCLevelGenerator = ObjectStorage.load("data/latest_pc_lg.lg") as PCLevelGenerator
    val simulator = GameSimulator(15000)

    for (i in 0..15) {
        val level = levelGenerator.generate()
        val postProcessed = LevelPostProcessor.postProcess(level)
//        val agent = CheaterKeyboardAgent()
//        val agent = Agents.NeuroEvolution.Stage4Level1Solver
        val agent = MarioAgent(ObjectStorage.load("data/coev/first_ai.ai") as MarioController)
        LevelVisualiser().display(level)
        val stats = simulator.playMario(agent, postProcessed, true)
    }
}

fun createDefaultPC() {
    val levelGenerator = PCLevelGenerator()
    val gameSimulator = GameSimulator(500000000)

    for (i in 0 .. 10) {
        val agent = CheaterKeyboardAgent()
        val defaultLevel = levelGenerator.generate()
        val chunksMetadata: ChunksLevelMetadata = levelGenerator.lastChunksMetadata

//        levelDifficulty(defaultLevel)
//        println(chunksMetadata.chunks.joinToString(", ") { it.chunk.name })

        LevelVisualiser().display(defaultLevel)
        val postProcessed = LevelPostProcessor.postProcess(defaultLevel)
        gameSimulator.playMario(agent, postProcessed, true)
    }
}