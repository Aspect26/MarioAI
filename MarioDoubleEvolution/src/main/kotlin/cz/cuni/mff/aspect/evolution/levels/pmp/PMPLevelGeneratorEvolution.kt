package cz.cuni.mff.aspect.evolution.levels.pmp

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.DistanceLinearityDifficultyCompressionDiscretizedEvaluator
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.PMPLevelGeneratorEvaluator
import cz.cuni.mff.aspect.evolution.utils.AlwaysReevaluatingEvaluator
import cz.cuni.mff.aspect.evolution.utils.UpdatedGaussianMutator
import cz.cuni.mff.aspect.utils.getDoubleValues
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart
import io.jenetics.*
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.util.Factory
import java.util.concurrent.ForkJoinPool

class PMPLevelGeneratorEvolution(
    private val populationSize: Int = 50,
    private val generationsCount: Int = 100,
    private val levelLength: Int = 200,
    private val evaluateOnLevelsCount: Int = 5,
    private val fitnessFunction: PMPLevelGeneratorEvaluator<Float> = DistanceLinearityDifficultyCompressionDiscretizedEvaluator(),
    private val maxProbability: Double = 1.0,
    private val chartLabel: String = "PMP Level Evolution",
    private val displayChart: Boolean = true
) : LevelGeneratorEvolution {

    private lateinit var agentFactory: () -> IAgent
    private val chart = EvolutionLineChart(label = this.chartLabel, hideNegative = false)

    override fun evolve(agentFactory: () -> IAgent): LevelGenerator {
        this.agentFactory = agentFactory
        val genotype = this.createInitialGenotype()
        val evolutionEngine = this.createEvolutionEngine(genotype)
        val resultIndividual = this.doEvolution(evolutionEngine)
        val genes = resultIndividual.genotype().getDoubleValues()

        return PMPLevelGenerator(genes, this.levelLength)
    }

    fun storeChart(path: String) {
        this.chart.save(path)
    }

    private fun createInitialGenotype(): Genotype<DoubleGene> {
        return Genotype.of(DoubleChromosome.of(List<DoubleGene>(PMPLevelGenerator.PROBABILITIES_COUNT) { DoubleGene.of(0.0, 0.0, this.maxProbability) }))
    }

    private fun createEvolutionEngine(initialGenotype: Factory<Genotype<DoubleGene>>): Engine<DoubleGene, Float> {
        return Engine.Builder(AlwaysReevaluatingEvaluator(
                this::computeFitness,
                ForkJoinPool.commonPool()), initialGenotype)
            .optimize(Optimize.MINIMUM)
            .populationSize(this.populationSize)
            .alterers(UpdatedGaussianMutator(0.5, 0.6))
            .survivorsSelector(EliteSelector(2))
            .offspringSelector(RouletteWheelSelector())
            .build()
    }

    private fun doEvolution(evolutionEngine: Engine<DoubleGene, Float>): Phenotype<DoubleGene, Float> {
        if (this.displayChart) this.chart.show()

        return evolutionEngine.stream()
            .limit(this.generationsCount.toLong())
            .peek {
                val generation = it.generation().toInt()
                val bestFitness = it.bestFitness().toDouble()
                val averageFitness = it.population().asList().fold(0.0f, {accumulator, genotype -> accumulator + genotype.fitness()}) / it.population().length()
                this.chart.update(generation, bestFitness, averageFitness.toDouble(), 0.0, 0.0)
                println("new gen: ${it.generation()} (best fitness: ${it.bestFitness()})")
            }
            .collect(EvolutionResult.toBestEvolutionResult<DoubleGene, Float>())
            .bestPhenotype()
    }

    private fun computeFitness(genotype: Genotype<DoubleGene>): Float {
        val genes = genotype.getDoubleValues()
        val levelGenerator = PMPLevelGenerator(genes, this.levelLength)

        return this.fitnessFunction(levelGenerator, this.agentFactory, this.evaluateOnLevelsCount)
    }

}
