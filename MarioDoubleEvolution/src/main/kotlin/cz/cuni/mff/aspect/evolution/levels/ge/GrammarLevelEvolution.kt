package cz.cuni.mff.aspect.evolution.levels.ge

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.evolution.levels.ge.algorithm.GrammarEvolution
import cz.cuni.mff.aspect.evolution.levels.ge.algorithm.GrammarSentence
import cz.cuni.mff.aspect.evolution.levels.ge.algorithm.jenetics.ByteGene
import cz.cuni.mff.aspect.evolution.levels.LevelEvolution
import cz.cuni.mff.aspect.evolution.levels.MarioLevelEvaluators
import cz.cuni.mff.aspect.evolution.levels.ge.algorithm.getString
import cz.cuni.mff.aspect.evolution.levels.ge.grammar.LevelChunkTerminal
import cz.cuni.mff.aspect.evolution.levels.ge.grammar.LevelGrammar
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.level.ChunkedMarioLevel
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.mario.level.MarioLevelChunk
import io.jenetics.Alterer
import io.jenetics.Mutator
import io.jenetics.SinglePointCrossover
import io.jenetics.util.IntRange


class GrammarLevelEvolution(private val levelsCount: Int = 1,
                            private val populationSize: Int = POPULATION_SIZE,
                            private val generationsCount: Long = GENERATIONS_COUNT
) : LevelEvolution {

    private lateinit var agent: IAgent

    override fun evolve(controller: IAgent): Array<MarioLevel> {
        this.agent = controller
        val grammarEvolution = GrammarEvolution.Builder(LevelGrammar.get())
            .fitness(this::fitness)
            .chromosomeLength(CHROMOSOME_LENGTH)
            .alterers(*ALTERERS)
            .populationSize(populationSize)
            .generationsCount(generationsCount)
            .resultsCount(levelsCount)
            .build()

        val resultSentences = grammarEvolution.evolve()
        val resultLevels = resultSentences.map { this.createLevelFromSentence(it) }.toTypedArray()

        println("BEST SENTENCE: ${resultSentences[0].getString()}")

        return resultLevels
    }

    private fun fitness(sentence: GrammarSentence): Float {
        if (sentence.isEmpty())
            return 0f

        val level = this.createLevelFromSentence(sentence)
        val gameSimulator = GameSimulator()

        // TODO: this is a hack!!! for some reason if we directly pass agent, it crashes
        val agent = MarioAgent((this.agent as MarioAgent).controller)
        val stats = gameSimulator.playMario(agent, level, false)

        return MarioLevelEvaluators.distanceActionsVictory(stats)
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
        private const val POPULATION_SIZE: Int = 70
        private const val GENERATIONS_COUNT: Long = 50
        private val CHROMOSOME_LENGTH: IntRange = IntRange.of(400, 500)
        private val ALTERERS: Array<Alterer<ByteGene, Float>> = arrayOf(SinglePointCrossover(0.3), Mutator(0.05))
    }

}
