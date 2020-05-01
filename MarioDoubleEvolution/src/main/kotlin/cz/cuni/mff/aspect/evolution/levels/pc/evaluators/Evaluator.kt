package cz.cuni.mff.aspect.evolution.levels.pc.evaluators

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.evolution.levels.pc.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pc.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import io.jenetics.Optimize

interface PCLevelGeneratorEvaluator<F> {

    operator fun invoke(levelGenerator: PCLevelGenerator, agentFactory: () -> IAgent, levelsCount: Int): F

    val optimize: Optimize

}

abstract class PCLevelGeneratorEvaluatorBase : PCLevelGeneratorEvaluator<Float> {

    override operator fun invoke(levelGenerator: PCLevelGenerator, agentFactory: () -> IAgent, levelsCount: Int): Float {
        val levels: MutableList<MarioLevel> = mutableListOf()
        val metadata: MutableList<ChunksLevelMetadata> = mutableListOf()
        val gameStatistics: MutableList<GameStatistics> = mutableListOf()

        repeat((0 until levelsCount).count()) {
            val agent = agentFactory()
            val level = levelGenerator.generate()
            val levelMetadata = levelGenerator.lastChunksMetadata

            val marioSimulator = GameSimulator(2500)
            val currentGameStatistics = marioSimulator.playMario(agent, level, false)

            levels.add(level)
            metadata.add(levelMetadata)
            gameStatistics.add(currentGameStatistics)
        }

        return this.evaluate(levels, metadata, gameStatistics)
    }

    abstract fun evaluate(levels: List<MarioLevel>, levelsChunkMetadata: List<ChunksLevelMetadata>, gameStatistics: List<GameStatistics>): Float

}