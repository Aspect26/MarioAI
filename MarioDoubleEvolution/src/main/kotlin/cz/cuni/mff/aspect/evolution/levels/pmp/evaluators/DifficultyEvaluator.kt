package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.levelDifficulty
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel

class DifficultyEvaluator : PMPLevelEvaluator<Float> {

    override fun invoke(level: MarioLevel, levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics): Float =
        (levelDifficulty(level) / (levelMetadata.levelLength - 2 * PMPLevelGenerator.SAFE_ZONE_LENGTH)).coerceAtMost(1f)

}