package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.evaluators.DifficultyEvaluator
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.PMPLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import io.jenetics.Optimize

/** Probabilistic Multipass level generator evaluator returning difficulty of levels using [DifficultyEvaluator]. */
class DifficultyEvaluator : SummingEvaluator() {

    override fun evaluateOne(level: MarioLevel, levelMetadata: PMPLevelMetadata, gameStatistics: GameStatistics): Float =
        (DifficultyEvaluator()(level, gameStatistics) / (levelMetadata.levelLength - 2 * PMPLevelGenerator.SAFE_ZONE_LENGTH)).coerceAtMost(1f)

    override val optimize: Optimize get() = Optimize.MAXIMUM

}