package cz.cuni.mff.aspect.evolution.levels

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.evolution.jenetics.evaluators.MarioJeneticsEvaluator
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart
import io.jenetics.*
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.util.Factory
import java.util.concurrent.ForkJoinPool

abstract class JeneticsLevelGeneratorEvolution(
    protected val populationSize: Int,
    protected val generationsCount: Int,
    protected val optimize: Optimize,
    protected val alterers: Array<Alterer<DoubleGene, Float>>,
    protected val survivorsSelector: Selector<DoubleGene, Float>,
    protected val offspringSelector: Selector<DoubleGene, Float>,
    private val displayChart: Boolean,
    override val chart: EvolutionLineChart
) : LevelGeneratorEvolution {

    protected lateinit var agentFactory: () -> IAgent
    private lateinit var evaluator: MarioJeneticsEvaluator<DoubleGene, Float>

    override fun evolve(agentFactory: () -> IAgent): LevelGenerator {
        this.agentFactory = agentFactory
        this.evaluator = this.createNewEvaluator()
        val initialGenotype = this.createInitialGenotype()
        val evolutionEngine = this.createEvolutionEngine(initialGenotype)
        val resultIndividual = this.doEvolution(evolutionEngine)
        val resultGenotype = resultIndividual.genotype()

        return levelGeneratorFromIndividual(resultGenotype)
    }

    private fun createNewEvaluator(): MarioJeneticsEvaluator<DoubleGene, Float> =
        MarioJeneticsEvaluator(
            this::computeFitnessAndObjective,
            true,
            ForkJoinPool.commonPool()
        )

    protected abstract fun createInitialGenotype(): Factory<Genotype<DoubleGene>>

    protected abstract fun levelGeneratorFromIndividual(genotype: Genotype<DoubleGene>): LevelGenerator

    protected abstract fun computeFitnessAndObjective(genotype: Genotype<DoubleGene>): Pair<Float, Float>

    private fun createEvolutionEngine(initialGenotype: Factory<Genotype<DoubleGene>>): Engine<DoubleGene, Float> {
        return Engine.Builder(this.evaluator, initialGenotype)
            .optimize(this.optimize)
            .populationSize(this.populationSize)
            .alterers(this.alterers[0], *this.alterers.slice(1 until this.alterers.size).toTypedArray())
            .survivorsSelector(this.survivorsSelector)
            .offspringSelector(this.offspringSelector)
            .build()
    }

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
        val averageFitness = evolutionResult.population().asList().fold(0.0f, { acc, genotype -> acc + genotype.fitness() }) / evolutionResult.population().length()

        val averageObjective = this.evaluator.lastGenerationObjectives.average()
        val bestObjective = if (this.optimize == Optimize.MAXIMUM) this.evaluator.lastGenerationObjectives.max() else this.evaluator.lastGenerationObjectives.min()

        this.chart.nextGeneration(bestFitness, averageFitness.toDouble(), bestObjective!!.toDouble(), averageObjective)

        println("new gen: ${evolutionResult.generation()} (best fitness: ${evolutionResult.bestFitness()}, best objective: ${bestObjective})")
    }
}