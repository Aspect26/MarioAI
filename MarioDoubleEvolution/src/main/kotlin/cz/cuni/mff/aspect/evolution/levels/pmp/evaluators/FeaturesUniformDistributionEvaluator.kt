package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.PMPLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import io.jenetics.Optimize
import kotlin.math.abs
import kotlin.math.pow

/**
 * Probabilistic Multipass level generator evaluator returns how much uniformly are features of Super Mario level
 * distributed in the generated levels.
 */
class FeaturesUniformDistributionEvaluator : SummingEvaluator() {

    override fun evaluateOne(level: MarioLevel, levelMetadata: PMPLevelMetadata, gameStatistics: GameStatistics): Float {
        val avg = (levelMetadata.holesCount + levelMetadata.pipesCount + levelMetadata.billsCount + levelMetadata.boxPlatformsCount +
                levelMetadata.stoneColumnsCount) / 5f
        return (abs(levelMetadata.holesCount - avg) - 3).coerceAtLeast(0f).pow(2) +
                (abs(levelMetadata.pipesCount - avg) - 3).coerceAtLeast(0f).pow(2) +
                (abs(levelMetadata.billsCount - avg) - 3).coerceAtLeast(0f).pow(2) +
                (abs(levelMetadata.boxPlatformsCount - avg) - 3).coerceAtLeast(0f).pow(2) +
                (abs(levelMetadata.stoneColumnsCount * avg) - 3).coerceAtLeast(0f).pow(2)
    }

    override val optimize: Optimize get() = Optimize.MAXIMUM

}