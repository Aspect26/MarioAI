package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.PMPLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import io.jenetics.Optimize

class NullEvaluator : PMPLevelEvaluator<Float> {

    override fun invoke(
        levels: List<MarioLevel>,
        metadata: List<PMPLevelMetadata>,
        gameStatistics: List<GameStatistics>
    ): Float {
        return 0f
    }

    override val optimize: Optimize
        get() = Optimize.MAXIMUM

}