package cz.cuni.mff.aspect.evolution.levels.pmp.evaluators

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.MarioLevel
import io.jenetics.Optimize

class DistanceLinearityDifficultyCompressionEvaluator : SummingEvaluator() {

    override fun evaluateOne(level: MarioLevel, levelMetadata: MarioLevelMetadata, gameStatistics: GameStatistics): Float {
        val distance = gameStatistics.finalMarioDistance

        val linearityFactor = LinearityEvaluator().evaluateOne(level, levelMetadata, gameStatistics)
        val difficultyFactor = DifficultyEvaluator().evaluateOne(level, levelMetadata, gameStatistics)
        val compressionFactor = HuffmanCompressionEvaluator().evaluateOne(level, levelMetadata, gameStatistics)

        return distance * (1 + linearityFactor + difficultyFactor + compressionFactor)
    }

    override val optimize: Optimize get() = Optimize.MAXIMUM

}