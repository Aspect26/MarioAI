package cz.cuni.mff.aspect.evolution.controller

import cz.cuni.mff.aspect.utils.sumByFloat
import cz.cuni.mff.aspect.mario.GameStatistics


typealias MarioGameplayEvaluator<F> = (gameStatistics: Array<GameStatistics>) -> F


// TODO: refactor me (add evaluators to class)
object MarioGameplayEvaluators {

    /*
    fun obstaclesOvercome(statistics: Array<GameStatistics>, levelMetadata: Array<MarioLevelMetadata>): Float {
        for (i in statistics.indices) {
            val marioDistanceTile: Int = statistics[i].finalMarioDistance.toInt() / 16
            val currentLevelMetadata = levelMetadata[i]
            val holesOvercome = currentLevelMetadata.holes.filterIndexed { columnIndex, holeLength -> columnIndex < marioDistanceTile && holeLength > 0 }.size
            val
        }
        return 0f
    }
     */

    fun distanceOnly(statistics: Array<GameStatistics>): Float {
        return statistics.sumByFloat { it.finalMarioDistance }
    }

    fun distanceAndVictories(statistics: Array<GameStatistics>): Float {
        return statistics.sumByFloat { it.finalMarioDistance + if (it.levelFinished) 500f else 0f }
    }


    fun distanceLeastActions(statistics: Array<GameStatistics>): Float {
        val sumFinalDistances: Float = statistics.sumByFloat { it.finalMarioDistance }
        val sumJumps = statistics.sumBy { it.jumps }
        val sumSpecials = statistics.sumBy { it.specials }
        val levelsFinished = statistics.sumBy { if (it.levelFinished) 1 else 0 }

        return sumFinalDistances - sumJumps * 20 - sumSpecials * 20 + levelsFinished * 200.0f
    }

    fun victoriesOnly(statistics: Array<GameStatistics>): Float {
        return statistics.sumByFloat { if (it.levelFinished) 1.0f else 0.0f } * 1000
    }

}
