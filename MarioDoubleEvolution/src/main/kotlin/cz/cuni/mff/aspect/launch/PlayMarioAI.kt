package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.controller.TrainingLevelsSet
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.evolution.results.LevelGenerators
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.storage.LevelStorage
import cz.cuni.mff.aspect.storage.ObjectStorage

/** Launches Super Mario simulator using specified AI player and level generator. */
fun main() {
    aiPlayLevel()
}


private fun aiPlayLevel() {
//    val agent = Agents.NeuroEvolution.Stage4Level1Solver
    val agent = MarioAgent(ObjectStorage.load("data/experiments/final-experiments/ai/neat/03 fitness/500:100:DO:7x7:false:false/NEAT evolution, experiment 2_ai.ai") as MarioController)
//    val levelGenerator = PCLevelGenerator()
//    val levelGenerator = ObjectStorage.load("data/coev/third_lg.lg") as LevelGenerator
    val levelGenerator = LevelGenerators.StaticGenerator(TrainingLevelsSet)

    val gameSimulator = GameSimulator(1400)
    val stats = Array(10) { levelGenerator.generate() }.map {
        gameSimulator.playMario(agent, it, true)
    }
    println(stats.sumBy { if (it.levelFinished) 1 else 0 })
}

