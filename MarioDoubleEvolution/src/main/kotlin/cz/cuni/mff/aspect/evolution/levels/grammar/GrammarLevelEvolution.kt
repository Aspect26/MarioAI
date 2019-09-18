package cz.cuni.mff.aspect.evolution.levels.grammar

import cz.cuni.mff.aspect.evolution.algorithm.grammar.GrammarEvolution
import cz.cuni.mff.aspect.evolution.algorithm.grammar.GrammarSentence
import cz.cuni.mff.aspect.evolution.algorithm.grammar.getString
import cz.cuni.mff.aspect.evolution.algorithm.grammar.jenetics.ByteGene
import cz.cuni.mff.aspect.evolution.levels.LevelEvolution
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.level.*
import io.jenetics.Alterer
import io.jenetics.Mutator
import io.jenetics.SinglePointCrossover
import io.jenetics.util.IntRange


class GrammarLevelEvolution : LevelEvolution {

    private lateinit var controller: MarioController

    override fun evolve(controller: MarioController): Array<MarioLevel> {
        this.controller = controller
        val grammarEvolution = GrammarEvolution.Builder(LevelGrammar.get())
            .fitness(this::fitness)
            .chromosomeLength(CHROMOSOME_LENGTH)
            .alterers(*ALTERERS)
            .populationSize(POPULATION_SIZE)
            .generationsCount(GENERATIONS_COUNT)
            .build()

        val resultSentence = grammarEvolution.evolve()
        val resultLevel = this.createLevelFromSentence(resultSentence)

        println("BEST SENTENCE: ${resultSentence.getString()}")

        return arrayOf(resultLevel)
    }

    private fun fitness(sentence: GrammarSentence): Float {
        if (sentence.isEmpty())
            return 0f

        val level = this.createLevelFromSentence(sentence)
        val marioSimulator = GameSimulator()
        val agent = MarioAgent(this.controller)

        marioSimulator.playMario(agent, level, false)

        val marioDistance = marioSimulator.finalDistance

        var penalty = 0
        for (i in 1 until sentence.size) {
            if (sentence[i] == sentence[i - 1])
                penalty += 20
            else if (sentence[i].value == sentence[i - 1].value)
                penalty += 10
        }

        return marioDistance - penalty
    }

    // TODO: this may be its own class
    fun createLevelFromSentence(sentence: GrammarSentence): MarioLevel {
        val levelChunks = mutableListOf<MarioLevelChunk>()
        sentence.forEach {
            val chunkTerminal = (it as LevelChunkTerminal)
            levelChunks.add(chunkTerminal.generateChunk())
        }

        return ChunkedMarioLevel(levelChunks.toTypedArray())
    }

    companion object {
        private const val POPULATION_SIZE: Int = 30
        private const val GENERATIONS_COUNT: Long = 200L
        private val CHROMOSOME_LENGTH: IntRange = IntRange.of(80, 100)
        private val ALTERERS: Array<Alterer<ByteGene, Float>> = arrayOf(SinglePointCrossover(0.3), Mutator(0.1))
    }

}
