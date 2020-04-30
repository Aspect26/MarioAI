package cz.cuni.mff.aspect.evolution.levels

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.evolution.Charted
import cz.cuni.mff.aspect.evolution.jenetics.evaluators.AlwaysReevaluatingEvaluator
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
    private val chart: EvolutionLineChart
) : LevelGeneratorEvolution, Charted by chart {

    protected lateinit var agentFactory: () -> IAgent

    override fun evolve(agentFactory: () -> IAgent): LevelGenerator {
        this.agentFactory = agentFactory
        val initialGenotype = this.createInitialGenotype()
        val evolutionEngine = this.createEvolutionEngine(initialGenotype)
        val resultIndividual = this.doEvolution(evolutionEngine)
        val resultGenotype = resultIndividual.genotype()

        return levelGeneratorFromIndividual(resultGenotype)
    }

    protected abstract fun createInitialGenotype(): Factory<Genotype<DoubleGene>>

    protected abstract fun levelGeneratorFromIndividual(genotype: Genotype<DoubleGene>): LevelGenerator

    protected abstract fun computeFitnessAndObjective(genotype: Genotype<DoubleGene>): Pair<Float, Float>

    private fun createEvolutionEngine(initialGenotype: Factory<Genotype<DoubleGene>>): Engine<DoubleGene, Float> {
        return Engine.Builder(
            AlwaysReevaluatingEvaluator(
                ForkJoinPool.commonPool(),
                this::computeFitnessAndObjective
            ),
            initialGenotype
        )
            .optimize(this.optimize)
            .populationSize(this.populationSize)
            .alterers(this.alterers[0], *this.alterers.slice(1 until this.alterers.size).toTypedArray())
            .survivorsSelector(this.survivorsSelector)
            .offspringSelector(this.offspringSelector)
            .build()
    }

    // TODO: add stop to chart only if empty
    // TODO: add objective to chart
    private fun doEvolution(evolutionEngine: Engine<DoubleGene, Float>): Phenotype<DoubleGene, Float> {
        if (this.displayChart && !this.chart.isShown) this.chart.show()
        this.chart.addStop()

        return evolutionEngine.stream()
            .limit(this.generationsCount.toLong())
            .peek {
                val bestFitness = it.bestFitness().toDouble()
                val averageFitness = it.population().asList().fold(0.0f, {accumulator, genotype -> accumulator + genotype.fitness()}) / it.population().length()
                this.chart.nextGeneration(bestFitness, averageFitness.toDouble(), 0.0, 0.0)
                println("new gen: ${it.generation()} (best fitness: ${it.bestFitness()})")
            }
            .collect(EvolutionResult.toBestEvolutionResult<DoubleGene, Float>())
            .bestPhenotype()
    }
}