package cz.cuni.mff.aspect.evolution.levels.chunks

import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel

typealias ChunkedLevelEvaluator<F> = (level: MarioLevel, levelChunks: Array<String>, gameStatistic: GameStatistics) -> F

object PCLevelEvaluators {

    fun marioDistanceAndDiversity(level: MarioLevel, levelChunks: Array<String>, gameStatistic: GameStatistics): Float {
        val distance = gameStatistic.finalMarioDistance

        val chunksUsed = levelChunks.distinct().size
        val diversityFactor = chunksUsed / (ProbabilisticChunksLevelGenerator.DEFAULT_CHUNKS_COUNT.toFloat() / 3)

        val jumpsFactor = (gameStatistic.jumps / 40.0f).coerceAtMost(1.0f)

        val enemiesCount = level.enemies.flatten().filter { it > 0 }.size
        val enemiesFactor = (enemiesCount / 10f).coerceAtMost(2.0f)

//        println("$enemiesFactor : $diversityFactor : $jumpsFactor - ${levelChunks.joinToString(", ")}")

        return distance * (3 + diversityFactor + jumpsFactor + enemiesFactor)
    }

}
