package cz.cuni.mff.aspect.launch

import ch.idsia.agents.controllers.keyboard.CheaterKeyboardAgent
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.chunks.ChunksLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelEvaluators
import cz.cuni.mff.aspect.evolution.levels.chunks.ProbabilisticChunksLevelCreator
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.storage.LevelStorage
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser

fun main() {
    evolvePC()
//    createDefaultPc()
}

fun evolvePC() {
    val agentFactory = { Agents.NeuroEvolution.Stage4Level1Solver }

    val fitnessFunction = PCLevelEvaluators::marioDistanceAndDiversity

    val levelGeneratorEvolution = ChunksLevelGeneratorEvolution(
        populationSize = 50,
        generationsCount = 50,
        evaluateOnLevelsCount = 5,
        fitnessFunction = fitnessFunction)

    val levels = levelGeneratorEvolution.evolve(agentFactory)
    val firstLevel = levels.first()

    val postprocessed = LevelPostProcessor.postProcess(firstLevel)
    LevelStorage.storeLevel("current.lvl", postprocessed)

    LevelVisualiser().displayAndStore(postprocessed, "current.png")

//    val agent = CheaterKeyboardAgent()
    val agent = agentFactory()
    GameSimulator().playMario(agent, postprocessed, true)
}

fun createDefaultPc() {
    val (defaultLevel, chunks) = ProbabilisticChunksLevelCreator.createDefault()
    println(chunks.joinToString(", "))
    val postProcessed = LevelPostProcessor.postProcess(defaultLevel)
    LevelVisualiser().display(postProcessed)
    GameSimulator(500000000).playMario(CheaterKeyboardAgent(), postProcessed, true)
}