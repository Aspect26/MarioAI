package cz.cuni.mff.aspect.evolution.levels.grammar

import cz.cuni.mff.aspect.evolution.algorithm.grammar.Grammar
import cz.cuni.mff.aspect.evolution.algorithm.grammar.NonTerminal
import cz.cuni.mff.aspect.evolution.algorithm.grammar.ProductionRule


object LevelGrammar {

    val level = NonTerminal("LEVEL")

    private val blockSequence = NonTerminal("BLOCK SEQUENCE")
    private val block = NonTerminal("LEVEL BLOCK")

    private val beginPlatform = BeginPlatformChunkTerminal()
    private val endPlatform = EndPlatformChunkTerminal()
    private val nothing = NothingChunkTerminal()
    private val start = StartChunkTerminal()
    private val path = PathChunkTerminal()
    private val boxes = BoxesChunkTerminal()
    private val secrets = SecretsChunkTerminal()
    private val pipe = PipeChunkTerminal()
    private val bulletBill = BulletBillChunkTerminal()
    private val stairs = StairChunkTerminal()
    private val twoPaths = DoublePathChunkTerminal()

    private val grammar = Grammar(arrayOf(
        ProductionRule(level, arrayOf(start, blockSequence)),

        ProductionRule(blockSequence, arrayOf(beginPlatform, block, block, block, block, block, block, block, block, block, block, block, block, block, block, block, block, block, block, block, block, block, block, block, block, block, block, block, block, endPlatform)),
        ProductionRule(block, arrayOf(nothing)),
        ProductionRule(block, arrayOf(path)),
        ProductionRule(block, arrayOf(boxes)),
        ProductionRule(block, arrayOf(secrets)),
        ProductionRule(block, arrayOf(pipe)),
        ProductionRule(block, arrayOf(bulletBill)),
        ProductionRule(block, arrayOf(stairs)),
        ProductionRule(block, arrayOf(twoPaths))
    ), level)

    fun get(): Grammar = grammar

}
