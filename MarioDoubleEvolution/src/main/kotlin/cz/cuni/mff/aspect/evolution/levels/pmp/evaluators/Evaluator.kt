package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel

typealias PMPLevelEvaluator<F> = (level: MarioLevel, levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics) -> F
