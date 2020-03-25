package cz.cuni.mff.aspect.evolution.levels.chunks

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.levels.ge.algorithm.GrammarEvolution
import cz.cuni.mff.aspect.evolution.levels.ge.algorithm.GrammarSentence
import cz.cuni.mff.aspect.evolution.levels.ge.algorithm.jenetics.ByteGene
import cz.cuni.mff.aspect.evolution.levels.LevelEvolution
import cz.cuni.mff.aspect.evolution.levels.ge.algorithm.getString
import cz.cuni.mff.aspect.evolution.levels.ge.grammar.LevelChunkTerminal
import cz.cuni.mff.aspect.evolution.levels.ge.grammar.LevelGrammar
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelCreator
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.level.ChunkedMarioLevel
import cz.cuni.mff.aspect.mario.level.DirectMarioLevel
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.mario.level.MarioLevelChunk
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart
import io.jenetics.*
import io.jenetics.util.IntRange


class ChunksLevelGeneratorEvolution(private val populationSize: Int = POPULATION_SIZE,
                                    private val generationsCount: Long = GENERATIONS_COUNT,
                                    private val evaluateOnLevelsCount: Int = 5,
                                    private val chartLabel: String = "Chunks level generator evolution"
) : LevelEvolution {

    private lateinit var agentFactory: () -> IAgent
    private val chart = EvolutionLineChart(label = this.chartLabel, hideNegative = true)

    override fun evolve(agentFactory: () -> IAgent): Array<MarioLevel> {
        /*
        this.agentFactory = agentFactory
        val genotype = this.createInitialGenotype()
        val evolutionEngine = this.createEvolutionEngine(genotype)
        val resultIndividuals = this.doEvolution(evolutionEngine)

        return Array(resultIndividuals.size) { PMPLevelCreator.create(this.levelLength, resultIndividuals[it]).createLevel() }
         */
        return arrayOf(DirectMarioLevel(arrayOf(), arrayOf()))
    }

    fun storeChart(path: String) {
        this.chart.save(path)
    }

    private fun createInitialGenotype(): Genotype<DoubleGene> {
        return Genotype.of(DoubleChromosome.of(*Array<DoubleGene>(PMPLevelCreator.PROBABILITIES_COUNT) { DoubleGene.of(0.0, 0.0, 1.0) }))
    }



    companion object {
        private const val POPULATION_SIZE: Int = 70
        private const val GENERATIONS_COUNT: Long = 50
        private val ALTERERS: Array<Alterer<ByteGene, Float>> = arrayOf(SinglePointCrossover(0.3), Mutator(0.05))
    }

}
