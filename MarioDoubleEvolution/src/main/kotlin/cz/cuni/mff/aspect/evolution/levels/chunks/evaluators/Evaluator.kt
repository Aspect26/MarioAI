package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel


typealias PCLevelEvaluator<F> = (level: MarioLevel, chunkMetadata: ChunksLevelMetadata, gameStatistic: GameStatistics) -> F