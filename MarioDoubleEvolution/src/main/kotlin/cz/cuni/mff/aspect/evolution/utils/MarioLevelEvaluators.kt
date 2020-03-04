package cz.cuni.mff.aspect.evolution.utils

import cz.cuni.mff.aspect.evolution.algorithm.grammar.GrammarSentence
import cz.cuni.mff.aspect.mario.GameStatistics

typealias MarioGrammarLevelEvaluator<F> = (sentence: GrammarSentence, gameStatistics: GameStatistics) -> F

object MarioLevelEvaluators {

    fun distanceOnly(sentence: GrammarSentence, statistics: GameStatistics): Float {
        return statistics.finalMarioDistance
    }

    fun distanceActionsVictory(sentence: GrammarSentence, statistics: GameStatistics): Float {
        return statistics.finalMarioDistance + statistics.kills * 20 + if (statistics.levelFinished) 200f else 0f
    }

}