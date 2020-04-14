package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.evolution.levels.evaluators.DifficultyEvaluator
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel

class DifficultyEvaluator : PCSummingEvaluator() {

    override fun evaluateOne(level: MarioLevel, levelMetadata: ChunksLevelMetadata, gameStatistics: GameStatistics): Float {
        val levelLength = levelMetadata.chunks.subList(1, levelMetadata.chunks.size - 1).map { it.chunk.length }.sum()
        return (DifficultyEvaluator()(level, gameStatistics) / (levelLength)).coerceAtMost(1f)
    }

}