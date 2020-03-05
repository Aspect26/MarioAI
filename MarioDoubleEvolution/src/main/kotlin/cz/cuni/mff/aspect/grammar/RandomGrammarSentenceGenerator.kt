package cz.cuni.mff.aspect.grammar.algorithm

import cz.cuni.mff.aspect.evolution.levels.ge.algorithm.Grammar
import cz.cuni.mff.aspect.evolution.levels.ge.algorithm.GrammarSentence
import cz.cuni.mff.aspect.evolution.levels.ge.algorithm.Symbol
import java.util.*
import kotlin.math.abs

class RandomGrammarSentenceGenerator(private val grammar: Grammar) {

    private val random: Random = Random()

    fun generate(maxLength: Int = 100, seed: Long? = null): GrammarSentence {
        if (seed != null)
            this.random.setSeed(seed)

        val currentSentence = mutableListOf<Symbol>(grammar.startingSymbol)
        var firstNonTerminal: Symbol? = currentSentence.find { it.expandable }
        val parametersIterator =
            cz.cuni.mff.aspect.evolution.levels.ge.algorithm.CircularIterator(Array(100) { this.random.nextInt() })

        while (firstNonTerminal != null && currentSentence.size < maxLength) {
            val applicableRules = grammar.getRules(firstNonTerminal)
            val randomApplicableRule = applicableRules[abs(this.random.nextInt()) % applicableRules.size]
            val rightSideOfRule = randomApplicableRule.to.map { s -> s.copy() }
            rightSideOfRule.forEach { it.takeParameters(parametersIterator) }

            val firstNonTerminalIndex: Int = currentSentence.indexOf(firstNonTerminal)
            currentSentence.removeAt(firstNonTerminalIndex)
            currentSentence.addAll(firstNonTerminalIndex, rightSideOfRule)

            firstNonTerminal = currentSentence.find { it.expandable }
        }

        return currentSentence.toTypedArray()
    }
}
