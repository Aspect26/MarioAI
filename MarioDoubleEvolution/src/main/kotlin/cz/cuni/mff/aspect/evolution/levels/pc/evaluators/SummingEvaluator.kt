package cz.cuni.mff.aspect.evolution.levels.pc.evaluators

import cz.cuni.mff.aspect.evolution.levels.pc.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.utils.sumByFloat

/** Abstract Probabilistic Chunks levels evaluator, which sums evaluation values computed on each generated level. */
abstract class SummingEvaluator : PCLevelEvaluator<Float> {

    override fun invoke(
        levels: List<MarioLevel>,
        levelsChunkMetadata: List<ChunksLevelMetadata>,
        gameStatistics: List<GameStatistics>
    ): Float {
        return (levels.indices).map { this.evaluateOne(levels[it], levelsChunkMetadata[it], gameStatistics[it]) }.sumByFloat()
    }

    abstract fun evaluateOne(level: MarioLevel, levelMetadata: ChunksLevelMetadata, gameStatistics: GameStatistics): Float

}