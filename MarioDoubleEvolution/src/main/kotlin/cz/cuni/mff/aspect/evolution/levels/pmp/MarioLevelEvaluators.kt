package cz.cuni.mff.aspect.evolution.levels.pmp

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.extensions.sumByFloat
import cz.cuni.mff.aspect.mario.GameStatistics

typealias MetadataLevelsEvaluator<F> = (levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics) -> F

object PMPLevelEvaluators {

    fun marioDistance(levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics): Float =
        gameStatistic.finalMarioDistance

    fun marioDistanceAndLevelDiversity(levelMetadata: List<MarioLevelMetadata>, gameStatistic: List<GameStatistics>): Float {
        return 0f
    }

}