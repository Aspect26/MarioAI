package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.utils.sumByFloat

abstract class PCSummingEvaluator : PCLevelGeneratorEvaluatorBase() {

    override fun evaluate(
        levels: List<MarioLevel>,
        levelsChunkMetadata: List<ChunksLevelMetadata>,
        gameStatistics: List<GameStatistics>
    ): Float {
        return (levels.indices).map { this.evaluateOne(levels[it], levelsChunkMetadata[it], gameStatistics[it]) }.sumByFloat()
    }

    abstract fun evaluateOne(level: MarioLevel, levelMetadata: ChunksLevelMetadata, gameStatistics: GameStatistics): Float

}