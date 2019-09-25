package cz.cuni.mff.aspect.evolution.algorithm.grammar


fun GrammarSentence.getString(): String = this.joinToString(" ", transform = { symbol -> if (symbol.expandable) "<${symbol}>" else symbol.toString() })
