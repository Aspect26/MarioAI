package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.PMPLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.utils.discretize
import io.jenetics.Optimize

class DistanceLinearityDifficultyCompressionDiscretizedEvaluator : SummingEvaluator() {

    override fun evaluateOne(level: MarioLevel, levelMetadata: PMPLevelMetadata, gameStatistics: GameStatistics): Float {
        val distance = gameStatistics.finalMarioDistance

        val linearityFactor = LinearityEvaluator().evaluateOne(level, levelMetadata, gameStatistics)
        val difficultyFactor = DifficultyEvaluator().evaluateOne(level, levelMetadata, gameStatistics)
        val compressionFactor = HuffmanCompressionEvaluator().evaluateOne(level, levelMetadata, gameStatistics)

        val linearityDiscretized = discretize(linearityFactor, arrayOf(0.0f, 0.3f, 0.6f, 1.0f))
        val difficultyDiscretized = discretize(difficultyFactor, arrayOf(0.0f, 0.3f, 0.6f, 1.0f))
        val compressionDiscretized = discretize(compressionFactor, arrayOf(0.0f, 0.3f, 0.6f, 1.0f))

        return distance * (1 + linearityDiscretized + difficultyDiscretized + compressionDiscretized)
    }

    override val optimize: Optimize get() = Optimize.MAXIMUM

}