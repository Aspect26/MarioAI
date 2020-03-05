package cz.cuni.mff.aspect.evolution.levels.ge.algorithm


fun GrammarSentence.getString(): String = this.joinToString(" ", transform = { symbol -> if (symbol.expandable) "<${symbol}>" else symbol.toString() })
