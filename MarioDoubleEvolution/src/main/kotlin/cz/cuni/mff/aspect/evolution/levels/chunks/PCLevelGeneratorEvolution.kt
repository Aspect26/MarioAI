package cz.cuni.mff.aspect.evolution.levels.chunks

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.evolution.Charted
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.LinearityEvaluator
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.PCLevelGeneratorEvaluator
import cz.cuni.mff.aspect.evolution.utils.AlwaysReevaluatingEvaluator
import cz.cuni.mff.aspect.utils.getDoubleValues
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart
import io.jenetics.*
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.util.Factory
import io.jenetics.util.RandomRegistry
import io.jenetics.util.Seq
import java.util.*
import java.util.concurrent.ForkJoinPool


class ChunksLevelGeneratorEvolution(private val populationSize: Int = POPULATION_SIZE,
                                    private val generationsCount: Int = GENERATIONS_COUNT,
                                    private val fitnessFunction: PCLevelGeneratorEvaluator<Float> = LinearityEvaluator(),
                                    private val evaluateOnLevelsCount: Int = 5,
                                    private val chunksCount: Int = 35,
                                    private val chartLabel: String = "Chunks level generator evolution",
                                    private val displayChart: Boolean = true,
                                    private val chart: EvolutionLineChart = EvolutionLineChart(chartLabel, hideNegative = false)
) : LevelGeneratorEvolution, Charted by chart {
    private lateinit var agentFactory: () -> IAgent

    override fun evolve(agentFactory: () -> IAgent): LevelGenerator {
        this.agentFactory = agentFactory
        val genotype = this.createInitialGenotype()
        val evolutionEngine = this.createEvolutionEngine(genotype)
        val resultIndividual = this.doEvolution(evolutionEngine)

        this.computeFitness(resultIndividual)

        return PCLevelGenerator(resultIndividual.getDoubleValues().toList(), this.chunksCount)
    }

    private fun createInitialGenotype(): Factory<Genotype<DoubleGene>> =
        MarkovChainGenotypeFactory(PCLevelGenerator.DEFAULT_CHUNKS_COUNT, PCLevelGenerator.ENEMY_TYPES_COUNT + 1)

    private fun createEvolutionEngine(initialGenotype: Factory<Genotype<DoubleGene>>): Engine<DoubleGene, Float> {
        return Engine.Builder(
            AlwaysReevaluatingEvaluator(this::computeFitness, ForkJoinPool.commonPool()),
//            AlwaysReevaluatingEvaluator(this::computeFitness, Concurrency.SERIAL_EXECUTOR),
            initialGenotype
        )
            .optimize(this.fitnessFunction.optimize)
            .populationSize(this.populationSize)
            .alterers(MarkovChainMutator(PCLevelGenerator.DEFAULT_CHUNKS_COUNT, 0.2, 0.2, 0.2))
            .survivorsSelector(EliteSelector(2))
            .offspringSelector(RouletteWheelSelector())
            .build()
    }

    private fun doEvolution(evolutionEngine: Engine<DoubleGene, Float>): Genotype<DoubleGene> {
        if (this.displayChart && !this.chart.isShown) this.chart.show()
        this.chart.addStop()

        return evolutionEngine.stream()
            .limit(this.generationsCount.toLong())
            .peek {
                val bestFitness = it.bestFitness().toDouble()
                val averageFitness = it.population().asList().fold(0.0f, {accumulator, genotype -> accumulator + genotype.fitness()}) / it.population().length()
                this.chart.nextGeneration(bestFitness, averageFitness.toDouble(), 0.0, 0.0)
                println("new gen: ${it.generation()} (best fitness: ${it.bestFitness()})")
            } .collect(EvolutionResult.toBestEvolutionResult<DoubleGene, Float>())
            .bestPhenotype().genotype()
    }

    private fun computeFitness(genotype: Genotype<DoubleGene>): Float {
        val genes = genotype.getDoubleValues()
        val levelGenerator = PCLevelGenerator(genes.toList(), this.chunksCount)

        return this.fitnessFunction(levelGenerator, this.agentFactory, this.evaluateOnLevelsCount)
    }

    companion object {
        private const val POPULATION_SIZE: Int = 70
        private const val GENERATIONS_COUNT: Int = 50
    }

}

class MarkovChainGenotypeFactory(private val statesCount: Int, private val additionalProbabilitiesCount: Int) : Factory<Genotype<DoubleGene>> {

    override fun newInstance(): Genotype<DoubleGene> =
        Genotype.of(DoubleChromosome.of(List<DoubleGene>(statesCount + statesCount * statesCount + additionalProbabilitiesCount) {
            when {
                it < statesCount + statesCount * statesCount && it % statesCount == 0 -> DoubleGene.of(1.0, 0.0, 1.0)
                it < statesCount + statesCount * statesCount -> DoubleGene.of(0.0, 0.0, 1.0)
                else -> DoubleGene.of(0.0, 0.0, 1.0)
            }
        }))

}

class MarkovChainMutator(
    private val statesCount: Int,
    private val changeInRandomVariableProbability: Double,
    private val swapInRandomVariableProbability: Double,
    private val additionalProbabilitiesChangeProbability: Double
) : Alterer<DoubleGene, Float> {

    override fun alter(population: Seq<Phenotype<DoubleGene, Float>>, generation: Long): AltererResult<DoubleGene, Float> {
        val random = RandomRegistry.random()

        val mutationResults: Seq<MutatorResult<Phenotype<DoubleGene, Float>>> = population.map {
            this.mutate(it.genotype(), generation, random)
        }

        return AltererResult.of(
            mutationResults.map {it.result()}.asISeq(),
            mutationResults.stream().mapToInt { it.mutations() }.sum()
        )
    }

    private fun mutate(oldIndividual: Genotype<DoubleGene>, generation: Long, random: Random): MutatorResult<Phenotype<DoubleGene, Float>> {
        val genes = oldIndividual.getDoubleValues()
        var mutations = 0

        // mutate additional probabilities
        for (index in statesCount +  statesCount * statesCount until genes.size) {
            val oldGene = oldIndividual.get(0).get(index)
            genes[index] = if (random.nextDouble() < this.additionalProbabilitiesChangeProbability) {
                mutations++
                (oldGene.doubleValue() + (random.nextDouble() / 2 - 0.25)).coerceIn(0.0, 1.0)
            } else {
                oldGene.doubleValue()
            }
        }

        // mutate individual random variables
        for (randomVariableIndex in 0 until this.statesCount + 1) {
            for (index in 0 until this.statesCount) {
                if (random.nextDouble() < this.changeInRandomVariableProbability) {
                    mutations++
                    val geneToChangeIndex = randomVariableIndex * this.statesCount + index
                    val valueChange = (genes[geneToChangeIndex] - (random.nextDouble())).coerceIn(0.0, 1.0) - genes[geneToChangeIndex]

                    for (i in 0 until this.statesCount) {
                        genes[randomVariableIndex * this.statesCount + i] += when (i) {
                            index -> valueChange
                            else -> -(valueChange / (this.statesCount - 1.0))
                        }
                    }

                    break
                }
            }
        }

        // swap in individual random variables
        for (randomVariableIndex in 0 until this.statesCount + 1) {
            if (random.nextDouble() < this.swapInRandomVariableProbability) {
                val firstIndex: Int = (random.nextDouble() * this.statesCount).toInt()
                val secondIndex: Int = (random.nextDouble() * this.statesCount).toInt()

                if (firstIndex == secondIndex) continue

                mutations++

                val firstIndexValue = genes[randomVariableIndex * this.statesCount + firstIndex]
                val secondIndexValue = genes[randomVariableIndex * this.statesCount + secondIndex]

                genes[randomVariableIndex * this.statesCount + firstIndex] = secondIndexValue
                genes[randomVariableIndex * this.statesCount + secondIndex] = firstIndexValue

                break
            }
        }

        val newIndividual = Genotype.of(DoubleChromosome.of(List<DoubleGene>(genes.size) {
            DoubleGene.of(genes[it], 0.0, 1.0)
        }))

        return MutatorResult.of(Phenotype.of(newIndividual, generation), mutations)
    }

}
