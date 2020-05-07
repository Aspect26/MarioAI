package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.PMPLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import io.jenetics.Optimize
import kotlin.math.abs

class LinearityEvaluator : SummingEvaluator() {

    override fun evaluateOne(level: MarioLevel, levelMetadata: PMPLevelMetadata, gameStatistics: GameStatistics): Float =
        this.averageHeightChange(levelMetadata).coerceAtMost(1f)

    private fun averageHeightChange(levelMetadata: PMPLevelMetadata): Float {
        var totalHeightChange = 0
        for (i in PMPLevelGenerator.SAFE_ZONE_LENGTH until levelMetadata.groundHeight.size - PMPLevelGenerator.SAFE_ZONE_LENGTH step 2) {
            totalHeightChange += abs(levelMetadata.groundHeight[i] - levelMetadata.groundHeight[i - 2])
        }

        return totalHeightChange.toFloat() / (levelMetadata.groundHeight.size - 2 * PMPLevelGenerator.SAFE_ZONE_LENGTH)
    }

    override val optimize: Optimize get() = Optimize.MAXIMUM

}