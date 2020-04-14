package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.utils.sumByFloat

abstract class SummingEvaluator : PMPLevelGeneratorEvaluatorBase() {

    override fun evaluate(
        levels: List<MarioLevel>,
        metadata: List<MarioLevelMetadata>,
        gameStatistics: List<GameStatistics>
    ): Float {
        return levels.indices.map { this.evaluateOne(levels[it], metadata[it], gameStatistics[it]) }.sumByFloat()
    }

    abstract fun evaluateOne(level: MarioLevel, levelMetadata: MarioLevelMetadata, gameStatistics: GameStatistics): Float

}