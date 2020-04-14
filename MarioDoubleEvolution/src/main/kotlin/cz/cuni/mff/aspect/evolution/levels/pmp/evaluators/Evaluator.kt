package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel

typealias PMPLevelGeneratorEvaluator<F> = (levelGenerator: PMPLevelGenerator, agentFactory: () -> IAgent, levelsCount: Int) -> F

abstract class PMPLevelGeneratorEvaluatorBase : PMPLevelGeneratorEvaluator<Float> {

    override operator fun invoke(levelGenerator: PMPLevelGenerator, agentFactory: () -> IAgent, levelsCount: Int): Float {
        val levels: MutableList<MarioLevel> = mutableListOf()
        val metadata: MutableList<MarioLevelMetadata> = mutableListOf()
        val gameStatistics: MutableList<GameStatistics> = mutableListOf()

        repeat((0 until levelsCount).count()) {
            val agent = agentFactory()
            val level = levelGenerator.generate()
            val levelMetadata = levelGenerator.lastMetadata

            val marioSimulator = GameSimulator()
            val currentGameStatistics = marioSimulator.playMario(agent, level, false)

            levels.add(level)
            metadata.add(levelMetadata)
            gameStatistics.add(currentGameStatistics)
        }

        return this.evaluate(levels, metadata, gameStatistics)
    }

    abstract fun evaluate(levels: List<MarioLevel>, metadata: List<MarioLevelMetadata>, gameStatistics: List<GameStatistics>): Float

}
