package cz.cuni.mff.aspect.evolution

import cz.cuni.mff.aspect.evolution.jenetics.evaluators.MarioJeneticsEvaluator
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart
import io.jenetics.*
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.internal.util.Concurrency
import io.jenetics.util.Factory
import java.util.concurrent.ForkJoinPool

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
        val initialGenotype = this.createInitialGenotype()
        val evolutionEngine = this.createEvolutionEngine(initialGenotype)
        val resultIndividual = this.doEvolution(evolutionEngine)
        val resultGenotype = resultIndividual.genotype()

        return entityFromIndividual(resultGenotype)
    }

    private fun createNewEvaluator(): MarioJeneticsEvaluator<DoubleGene, Float> {
        val executor = if (this.parallel) ForkJoinPool.commonPool() else Concurrency.SERIAL_EXECUTOR
        return MarioJeneticsEvaluator(this::computeFitnessAndObjective,this.alwaysReevaluate, executor)
    }

    protected abstract fun createInitialGenotype(): Factory<Genotype<DoubleGene>>

    protected abstract fun entityFromIndividual(genotype: Genotype<DoubleGene>): T

    protected abstract fun computeFitnessAndObjective(genotype: Genotype<DoubleGene>): Pair<Float, Float>

    private fun createEvolutionEngine(initialGenotype: Factory<Genotype<DoubleGene>>): Engine<DoubleGene, Float> =
        Engine.Builder(this.evaluator, initialGenotype)
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
