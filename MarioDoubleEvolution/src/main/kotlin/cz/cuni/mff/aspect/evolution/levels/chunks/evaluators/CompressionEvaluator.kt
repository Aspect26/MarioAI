package cz.cuni.mff.aspect.evolution.levels.chunks.evaluators

import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.evolution.levels.LevelImageCompressor
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel

class CompressionEvaluator : PCLevelEvaluator<Float> {

    override operator fun invoke(level: MarioLevel, chunkMetadata: ChunksLevelMetadata, gameStatistic: GameStatistics): Float =
        // TODO: konstanta vycucana z prsta...
        LevelImageCompressor.smallPngSize(level) / 3200f

}