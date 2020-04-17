package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.evaluators.compression.SmallPNGCompression
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.visualisation.level.LevelToImageConverter
import io.jenetics.Optimize

class PNGCompressionEvaluator : SummingEvaluator() {

    override fun evaluateOne(level: MarioLevel, levelMetadata: MarioLevelMetadata, gameStatistics: GameStatistics): Float {
        val image = LevelToImageConverter.createMinified(level, noAlpha=true)
        val compressionSize = SmallPNGCompression.getSize(image)

        // TODO: konstanta vycucana z prsta...
        return compressionSize / 4000f
    }

    override val optimize: Optimize get() = Optimize.MAXIMUM

}
