package cz.cuni.mff.aspect.evolution.levels.evaluators

import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel

/** Typealias for a generic level evaluator. */
typealias LevelEvaluator<F> = (level: MarioLevel, gameStatistic: GameStatistics) -> F
