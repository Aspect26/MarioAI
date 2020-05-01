package cz.cuni.mff.aspect.evolution.levels.ge.algorithm

import cz.cuni.mff.aspect.evolution.levels.ge.algorithm.jenetics.ByteChromosome
import cz.cuni.mff.aspect.evolution.levels.ge.algorithm.jenetics.ByteGene
import cz.cuni.mff.aspect.utils.getByteValues
import io.jenetics.*
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.util.IntRange
import java.util.stream.Collectors


@Deprecated("No longer maintained")
class GrammarEvolution private constructor(private val grammar: Grammar,
                                           private val fitnessComputation: (sentence: GrammarSentence) -> Float,
                                           private val chromosomeLengthRange: IntRange,
                                           private val alterers: Array<Alterer<ByteGene, Float>>,
                                           private val populationSize: Int,
                                           private val generationsCount: Long,
                                           private val resultsCount: Int) {

    private val genesToSentenceConverter: GenesToSentenceConverter = GenesToSentenceConverter(this.grammar)

    fun evolve(): List<GrammarSentence> {
        val genotype = this.createInitialGenotype()
        val evolutionEngine = this.createEvolutionEngine(genotype, this.populationSize)

        return when {
            this.resultsCount == 1 -> {
                val result = this.evolveBest(evolutionEngine, this.generationsCount)
                val genes = result.bestPhenotype().genotype().getByteValues()
                listOf(this.getGrammarSentence(genes))
            }
            this.resultsCount > 1 -> {
                val result = this.evolveMultipleBest(evolutionEngine, this.generationsCount, this.resultsCount)
                result.map { it.genotype().getByteValues() }.map { this.getGrammarSentence(it) }
            }
            else -> {
                throw IllegalArgumentException("Result count cannot be less than 1")
            }
        }
    }

    private fun createInitialGenotype(): Genotype<ByteGene> = Genotype.of(ByteChromosome.of(this.chromosomeLengthRange))

    private fun createEvolutionEngine(initialGenotype: Genotype<ByteGene>, populationSize: Int): Engine<ByteGene, Float> =
        Engine.builder(fitness, initialGenotype)
            .optimize(Optimize.MAXIMUM)
            .populationSize(populationSize)
            .alterers(this.alterers[0], *this.alterers.slice(1 until this.alterers.size).toTypedArray())
            .survivorsSelector(EliteSelector(10))
            .offspringSelector(RouletteWheelSelector())
            .mapping { evolutionResult ->
                println("[GE] new gen: ${evolutionResult.generation()} (best fitness: ${evolutionResult.bestFitness()})")
                evolutionResult
            }
            .build()

    private fun evolveBest(evolutionEngine: Engine<ByteGene, Float>, generationsCount: Long): EvolutionResult<ByteGene, Float> =
        evolutionEngine.stream()
            .limit(generationsCount)
            .collect(EvolutionResult.toBestEvolutionResult<ByteGene, Float>())

    private fun evolveMultipleBest(evolutionEngine: Engine<ByteGene, Float>, generationsCount: Long, resultCount: Int): List<Phenotype<ByteGene, Float>> {
        val allGenerations = evolutionEngine.stream()
            .limit(generationsCount)
            .collect(Collectors.toList())

        val allIndividuals = allGenerations.map { it.population().toList() }.flatten()
        return allIndividuals.sortedByDescending { it.fitness() }.subList(0, resultCount)
    }

    private val fitness = java.util.function.Function<Genotype<ByteGene>, Float> { genotype -> fitness(genotype) }
    private fun fitness(genotype: Genotype<ByteGene>): Float {
        val genes = genotype.getByteValues()
        val sentence = this.getGrammarSentence(genes)

        return this.fitnessComputation(sentence)
    }

    private fun getGrammarSentence(genes: ByteArray): GrammarSentence {
        return this.genesToSentenceConverter.convert(genes, MAX_WRAPS_COUNT)
    }

    companion object {
        private const val MAX_WRAPS_COUNT = 100
    }

    @Suppress("ArrayInDataClass")
    data class Builder(private var grammar: Grammar,
                       private var fitnessComputation: (sentence: GrammarSentence) -> Float = { _ -> 0.0f },
                       private var chromosomeLengthRange: IntRange = IntRange.of(10, 30),
                       private var alterers: Array<Alterer<ByteGene, Float>> = arrayOf(SinglePointCrossover(0.4), Mutator(0.3)),
                       private var populationSize: Int = 20,
                       private var generationsCount: Long = 50,
                       private var resultsCount: Int = 1) {

        fun grammar(grammar: Grammar): Builder = apply { this.grammar = grammar }
        fun fitness(fitnessComputation: (sentence: GrammarSentence) -> Float): Builder = apply { this.fitnessComputation = fitnessComputation }
        fun chromosomeLength(length: IntRange): Builder = apply { this.chromosomeLengthRange = length }
        fun alterers(vararg alterers: Alterer<ByteGene, Float>): Builder = apply { this.alterers = arrayOf(*alterers) }
        fun populationSize(populationSize: Int): Builder = apply { this.populationSize = populationSize }
        fun generationsCount(generationsCount: Long): Builder = apply { this.generationsCount = generationsCount }
        fun resultsCount(resultsCount: Int): Builder = apply { this.resultsCount = resultsCount }

        fun build(): GrammarEvolution = GrammarEvolution(this.grammar, this.fitnessComputation, this.chromosomeLengthRange,
            this.alterers, this.populationSize, this.generationsCount, this.resultsCount)

    }

}
