package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.evolution.levels.levelDifficulty
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel

class DifficultyEvaluator : PCLevelEvaluator<Float> {

    override operator fun invoke(level: MarioLevel, chunkMetadata: ChunksLevelMetadata, gameStatistic: GameStatistics): Float =
        (levelDifficulty(level) / (level.tiles.size)).coerceAtMost(1f)

}