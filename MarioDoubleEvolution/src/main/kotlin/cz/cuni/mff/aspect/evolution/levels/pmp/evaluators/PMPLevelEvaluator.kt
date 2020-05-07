package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.PMPLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import io.jenetics.Optimize

interface PMPLevelEvaluator<F> {

    operator fun invoke(levels: List<MarioLevel>, metadata: List<PMPLevelMetadata>, gameStatistics: List<GameStatistics>): F

    val optimize: Optimize

}
