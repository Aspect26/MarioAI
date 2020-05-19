package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.evaluators.compression.ImageHuffmanCompression
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.PMPLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.visualisation.level.LevelToImageConverter
import io.jenetics.Optimize

/** Probabilistic Multipass level generator evaluator returning compression by [ImageHuffmanCompression] compression. */
class HuffmanCompressionEvaluator : SummingEvaluator() {

    override fun evaluateOne(level: MarioLevel, levelMetadata: PMPLevelMetadata, gameStatistics: GameStatistics): Float {
        val image = LevelToImageConverter.createMinified(level, noAlpha=true)
        val compressionSize = ImageHuffmanCompression(2).getSize(image)

        return compressionSize.toFloat()
    }

    override val optimize: Optimize get() = Optimize.MAXIMUM

}
