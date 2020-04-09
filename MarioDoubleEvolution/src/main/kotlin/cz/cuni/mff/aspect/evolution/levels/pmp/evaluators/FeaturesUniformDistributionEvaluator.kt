package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import kotlin.math.abs
import kotlin.math.pow

class FeaturesUniformDistributionEvaluator : PMPLevelEvaluator<Float> {

    override fun invoke(level: MarioLevel, levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics): Float {
        val avg = (levelMetadata.holesCount + levelMetadata.pipesCount + levelMetadata.billsCount + levelMetadata.boxPlatformsCount +
                levelMetadata.stoneColumnsCount) / 5f
        return (abs(levelMetadata.holesCount - avg) - 3).coerceAtLeast(0f).pow(2) +
                (abs(levelMetadata.pipesCount - avg) - 3).coerceAtLeast(0f).pow(2) +
                (abs(levelMetadata.billsCount - avg) - 3).coerceAtLeast(0f).pow(2) +
                (abs(levelMetadata.boxPlatformsCount - avg) - 3).coerceAtLeast(0f).pow(2) +
                (abs(levelMetadata.stoneColumnsCount * avg) - 3).coerceAtLeast(0f).pow(2)
    }

}