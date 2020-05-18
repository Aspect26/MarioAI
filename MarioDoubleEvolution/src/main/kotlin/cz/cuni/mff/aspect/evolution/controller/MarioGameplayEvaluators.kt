package cz.cuni.mff.aspect.evolution.controller

import cz.cuni.mff.aspect.utils.sumByFloat
import cz.cuni.mff.aspect.mario.GameStatistics


typealias MarioGameplayEvaluator<F> = (gameStatistics: Array<GameStatistics>) -> F


// TODO: refactor me (add evaluators to class)
/** Contains implementations of multiple fitness functions for Mario Controllers. */
object MarioGameplayEvaluators {

    /** Computes sum of distances, which the agent was able to get to in given levels. */
    fun distanceOnly(statistics: Array<GameStatistics>): Float {
        return statistics.sumByFloat { it.finalMarioDistance }
    }

    /**
     * Computes sum of distances, which the agent was able to get to in given levels, and awards bonus for each
     * solved level.
     */
    fun distanceAndVictories(statistics: Array<GameStatistics>): Float {
        return statistics.sumByFloat { it.finalMarioDistance + if (it.levelFinished) 500f else 0f }
    }

    /**
     * Computes sum of distances, which the agent was able to get to in given levels, and punishes the agent for
     * each action if has done.
     */
    fun distanceLeastActions(statistics: Array<GameStatistics>): Float {
        val sumFinalDistances: Float = statistics.sumByFloat { it.finalMarioDistance }
        val sumJumps = statistics.sumBy { it.jumps }
        val sumSpecials = statistics.sumBy { it.specials }
        val levelsFinished = statistics.sumBy { if (it.levelFinished) 1 else 0 }

        return sumFinalDistances - sumJumps * 20 - sumSpecials * 20 + levelsFinished * 200.0f
    }

    /** Computes how many victories the agent has achieved on given levels, multiplied by 1000. */
    fun victoriesOnly(statistics: Array<GameStatistics>): Float {
        return statistics.sumByFloat { if (it.levelFinished) 1.0f else 0.0f } * 1000
    }

}
