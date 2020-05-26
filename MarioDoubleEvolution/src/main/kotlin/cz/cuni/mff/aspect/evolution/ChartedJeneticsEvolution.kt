package cz.cuni.mff.aspect.evolution

import cz.cuni.mff.aspect.evolution.jenetics.evaluators.MarioJeneticsEvaluator
import cz.cuni.mff.aspect.visualisation.charts.evolution.EvolutionLineChart
import io.jenetics.*
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.internal.util.Concurrency
import io.jenetics.util.Factory
import java.util.concurrent.ForkJoinPool

/**
 * Wrapper for evolution based on Jenetics library which plots evolution data in a chart. It is highly configurable
 * via primary constructor's parameters.
 *
 * @param populationSize population size.
 * @param generationsCount number of generations after which the evolution is stopped.
 * @param fitnessOptimization specifies, whether fitness values should be minimized or maximized.
 * @param objectiveOptimization specifies, whether objective values should be minimized or maximized.
 * @param alterers evolution alterers.
 * @param survivorsSelector evolution survivors selector.
 * @param offspringSelector evolution offsprings selector.
 * @param displayChart specifies, whether the evolution chart should be displayed.
 * @param chart the evolution chart.
 * @param parallel specifies, whether the evaluations of individuals should be run on multiple CPU cores.
 * @param alwaysReevaluate specifies, whether the individuals should be reevaluated in each generation during the
 * evolution, or only when they are created. This is useful when the fitness computation is randomized somehow.
 */
abstract class ChartedJeneticsEvolution<T>(
    protected val populationSize: Int,
    protected val generationsCount: Int,
    protected val fitnessOptimization: Optimize,
    protected val objectiveOptimization: Optimize,
    protected val alterers: Array<Alterer<DoubleGene, Float>>,
    protected val survivorsSelector: Selector<DoubleGene, Float>,
    protected val offspringSelector: Selector<DoubleGene, Float>,
    private val displayChart: Boolean,
    val chart: EvolutionLineChart,
    private val parallel: Boolean = true,
    private val alwaysReevaluate: Boolean = true
) {

    lateinit var evaluator: MarioJeneticsEvaluator<DoubleGene, Float>

    fun evolve(): T {
        this.evaluator = this.createNewEvaluator()
        val initialGenotype = this.createGenotypeFactory()
        val evolutionEngine = this.createEvolutionEngine(initialGenotype)
        val resultIndividual = this.doEvolution(evolutionEngine)
        val resultGenotype = resultIndividual.genotype()

        return entityFromIndividual(resultGenotype)
    }

    private fun createNewEvaluator(): MarioJeneticsEvaluator<DoubleGene, Float> {
        val executor = if (this.parallel) ForkJoinPool.commonPool() else Concurrency.SERIAL_EXECUTOR
        return MarioJeneticsEvaluator(this::computeFitnessAndObjective,this.alwaysReevaluate, executor)
    }

    protected abstract fun createGenotypeFactory(): Factory<Genotype<DoubleGene>>

    protected abstract fun entityFromIndividual(genotype: Genotype<DoubleGene>): T

    protected abstract fun computeFitnessAndObjective(genotype: Genotype<DoubleGene>): Pair<Float, Float>

    private fun createEvolutionEngine(initialGenotypeFactory: Factory<Genotype<DoubleGene>>): Engine<DoubleGene, Float> =
        Engine.Builder(this.evaluator, initialGenotypeFactory)
            .optimize(this.fitnessOptimization)
            .populationSize(this.populationSize)
            .alterers(this.alterers[0], *this.alterers.slice(1 until this.alterers.size).toTypedArray())
            .survivorsSelector(this.survivorsSelector)
            .offspringSelector(this.offspringSelector)
            .build()

    private fun doEvolution(evolutionEngine: Engine<DoubleGene, Float>): Phenotype<DoubleGene, Float> {
        if (this.displayChart && !this.chart.isShown) this.chart.show()
        if (!this.chart.isEmpty) this.chart.addStop()

        return evolutionEngine.stream()
            .limit(this.generationsCount.toLong())
            .peek(this::onGenerationPassed)
            .collect(EvolutionResult.toBestEvolutionResult<DoubleGene, Float>())
            .bestPhenotype()
    }

    private fun onGenerationPassed(evolutionResult: EvolutionResult<DoubleGene, Float>) {
        val bestFitness = evolutionResult.bestFitness().toDouble()
        val averageFitness = evolutionResult.population().asList()
            .fold(0.0f, { acc, genotype -> acc + genotype.fitness() }) / evolutionResult.population().length()

        val averageObjective = this.evaluator.lastGenerationObjectives.average()
        val bestObjective = if (this.objectiveOptimization == Optimize.MAXIMUM) this.evaluator.lastGenerationObjectives.max() else this.evaluator.lastGenerationObjectives.min()

        this.chart.nextGeneration(bestFitness, averageFitness.toDouble(), bestObjective!!.toDouble(), averageObjective)

        println("new gen: ${evolutionResult.generation()} " +
                "(best fitness: ${evolutionResult.bestFitness()}, best objective: ${bestObjective})")
    }
}
