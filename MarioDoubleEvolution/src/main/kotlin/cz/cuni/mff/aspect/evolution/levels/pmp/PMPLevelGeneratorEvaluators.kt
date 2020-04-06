package cz.cuni.mff.aspect.evolution.levels.pmp

import cz.cuni.mff.aspect.evolution.levels.levelDifficulty
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.utils.min
import kotlin.math.abs
import kotlin.math.pow

typealias MetadataLevelsEvaluator<F> = (level: MarioLevel, levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics) -> F

object PMPLevelEvaluators {

    fun distanceDiversityEnemiesLinearity(level: MarioLevel, levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics): Float {
        val distance = gameStatistic.finalMarioDistance

        val nonLinearityFactor = (averageHeightChange(levelMetadata)).coerceAtMost(1f)
        val difficultyFactor = (levelDifficulty(level) / (levelMetadata.levelLength - 2 * PMPLevelGenerator.SAFE_ZONE_LENGTH)).coerceAtMost(1f)
        val compressionFactor = LevelImageCompressor.jpgSize(level).toFloat() / 200000

        val allFactors = listOf(nonLinearityFactor, difficultyFactor)
        val minFactor = min(allFactors)

//        println("$compressionFactor | $nonLinearityFactor | $difficultyFactor")
//        return compressionFactor
//        return distance * (0.5f + compressionFactor)
        return distance * (2 + nonLinearityFactor + difficultyFactor + 2*compressionFactor)
    }

    private fun featuresUniformDistributionDifference(levelMetadata: MarioLevelMetadata): Float {
        val avg = (levelMetadata.holesCount + levelMetadata.pipesCount + levelMetadata.billsCount + levelMetadata.boxPlatformsCount +
                levelMetadata.stoneColumnsCount) / 5f
        return (abs(levelMetadata.holesCount - avg) - 3).coerceAtLeast(0f).pow(2) +
                (abs(levelMetadata.pipesCount - avg) - 3).coerceAtLeast(0f).pow(2) +
                (abs(levelMetadata.billsCount - avg) - 3).coerceAtLeast(0f).pow(2) +
                (abs(levelMetadata.boxPlatformsCount - avg) - 3).coerceAtLeast(0f).pow(2) +
                (abs(levelMetadata.stoneColumnsCount * avg) - 3).coerceAtLeast(0f).pow(2)
    }

    private fun averageHeightChange(levelMetadata: MarioLevelMetadata): Float {
        var totalHeightChange = 0
        for (i in PMPLevelGenerator.SAFE_ZONE_LENGTH until levelMetadata.groundHeight.size - PMPLevelGenerator.SAFE_ZONE_LENGTH step 2) {
            totalHeightChange += abs(levelMetadata.groundHeight[i] - levelMetadata.groundHeight[i - 2])
        }

        return totalHeightChange.toFloat() / (levelMetadata.groundHeight.size - 2 * PMPLevelGenerator.SAFE_ZONE_LENGTH)
    }

    private fun discretize(value: Float, availableValues: FloatArray): Float {
        for (availableValue in availableValues.reversed()) if (value >= availableValue) return availableValue
        return availableValues.last()
    }

}
