package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.evolution.levels.evaluators.compression.ImageHuffmanCompression
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.visualisation.level.LevelToImageConverter

class HuffmanCompressionEvaluator : SummingEvaluator() {

    override fun evaluateOne(level: MarioLevel, levelMetadata: ChunksLevelMetadata, gameStatistics: GameStatistics): Float {
        val image = LevelToImageConverter.createMinified(level, noAlpha=true)
        val compressionSize = ImageHuffmanCompression(2).getSize(image)

        // TODO: konstanta vycucana z prsta...
        return compressionSize / 2400f
    }

}
