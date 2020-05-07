package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import io.jenetics.Optimize

interface PCLevelEvaluator<F> {

    operator fun invoke(levels: List<MarioLevel>, levelsChunkMetadata: List<ChunksLevelMetadata>, gameStatistics: List<GameStatistics>): F

    val optimize: Optimize

}
