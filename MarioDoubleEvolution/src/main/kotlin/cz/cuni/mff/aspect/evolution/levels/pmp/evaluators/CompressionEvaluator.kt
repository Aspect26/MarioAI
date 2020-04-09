package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.LevelImageCompressor
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel

class CompressionEvaluator : PMPLevelEvaluator<Float> {

    override operator fun invoke(level: MarioLevel, levelMetadata: MarioLevelMetadata, gameStatistic: GameStatistics): Float =
        // TODO: konstanta vycucana z prsta...
        LevelImageCompressor.smallPngSize(level) / 4000f

}
