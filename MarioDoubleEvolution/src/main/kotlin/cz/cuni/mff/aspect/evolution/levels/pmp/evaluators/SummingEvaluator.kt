package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.PMPLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.utils.sumByFloat

abstract class SummingEvaluator : PMPLevelEvaluator<Float> {

    override fun invoke(
        levels: List<MarioLevel>,
        metadata: List<PMPLevelMetadata>,
        gameStatistics: List<GameStatistics>
    ): Float {
        return levels.indices.map { this.evaluateOne(levels[it], metadata[it], gameStatistics[it]) }.sumByFloat()
    }

    abstract fun evaluateOne(level: MarioLevel, levelMetadata: PMPLevelMetadata, gameStatistics: GameStatistics): Float

}