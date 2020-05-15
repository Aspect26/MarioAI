package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.evolution.levels.evaluators.compression.ImageHuffmanCompression
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.visualisation.level.LevelToImageConverter
import io.jenetics.Optimize
import kotlin.math.abs

// TODO: better name...
class All : PCLevelEvaluator<Float> {

    override fun invoke(
        levels: List<MarioLevel>,
        levelsChunkMetadata: List<ChunksLevelMetadata>,
        gameStatistics: List<GameStatistics>
    ): Float {
        val wonCount = gameStatistics.sumBy { if (it.levelFinished) 1 else 0 }
        val lostCount = gameStatistics.size - wonCount

        val maxDifference = gameStatistics.size
        val wonLostDifference = abs(wonCount - lostCount)
        val reversedWonLostDifference = maxDifference - wonLostDifference

        val compressionFactor = List(levels.size) {
            val image = LevelToImageConverter.createMinified(levels[it], noAlpha=true)
            ImageHuffmanCompression(2).getSize(image)
        }.sum() / 125180f

        val linearityFactor = List(levels.size) {
            LinearityEvaluator().evaluateOne(levels[it], levelsChunkMetadata[it], gameStatistics[it])
        }.average().toFloat()

        return reversedWonLostDifference.toFloat() * (1 + compressionFactor + linearityFactor) * 1000
    }

    override val optimize: Optimize get() = Optimize.MAXIMUM

}