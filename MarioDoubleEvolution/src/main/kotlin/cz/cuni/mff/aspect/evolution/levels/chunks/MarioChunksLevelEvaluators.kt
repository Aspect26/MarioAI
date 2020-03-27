package cz.cuni.mff.aspect.evolution.levels.chunks

import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel

typealias ChunkedLevelEvaluator<F> = (level: MarioLevel, levelChunks: Array<String>, gameStatistic: GameStatistics, heightChangeProbability: Float) -> F

object PCLevelEvaluators {

    fun distanceDiversityEnemiesLinearity(level: MarioLevel, levelChunks: Array<String>, gameStatistic: GameStatistics, heightChangeProbability: Float): Float {
        val distance = gameStatistic.finalMarioDistance

        val chunksUsed = levelChunks.distinct()
        val chunksUsedCount = chunksUsed.size
        val diversityFactor = chunksUsedCount / (ProbabilisticChunksLevelGenerator.DEFAULT_CHUNKS_COUNT.toFloat() / 3)

        val chunkUsage = chunksUsed.map { currentChunk -> Pair(currentChunk, levelChunks.filter { it == currentChunk}.size) }.toList()
        val minChunkUsage: Int = chunkUsage.minBy { it.second }!!.second
        val maxChunkUsage: Int = chunkUsage.maxBy { it.second }!!.second
        val minMaxDifference = maxChunkUsage - minChunkUsage
        val chunkUsageFactor = if (minMaxDifference > 0f) 1f / minMaxDifference else 1f

        val jumpsFactor = (gameStatistic.jumps / 40.0f).coerceAtMost(1.0f)

        val enemyTypes: Int = level.enemies.flatten().filter { it > 0 }.distinct().size
        val enemiesCount = level.enemies.flatten().filter { it > 0 }.size
        val enemiesFactor = (enemiesCount / 20f).coerceAtMost(2.0f)
        val enemiesDiversityFactor = enemyTypes / ProbabilisticChunksLevelGenerator.ENEMY_TYPES_COUNT.toFloat()

        val linearityFactor = heightChangeProbability

//        println("$enemiesFactor : $diversityFactor : $jumpsFactor - ${levelChunks.joinToString(", ")}")

        return (distance / 5) + distance * chunkUsageFactor * diversityFactor * linearityFactor * enemiesFactor * enemiesDiversityFactor
//        return distance * (5 + diversityFactor + chunkUsageFactor + enemiesFactor + linearityFactor + jumpsFactor)
    }

}
