package cz.cuni.mff.aspect.evolution.levels.pmp

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.evolution.levels.LevelEvolution
import cz.cuni.mff.aspect.evolution.levels.MarioLevelEvaluator
import cz.cuni.mff.aspect.evolution.levels.MarioLevelEvaluators
import cz.cuni.mff.aspect.extensions.getDoubleValues
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart
import io.jenetics.*
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

class ProbabilisticMultipassEvolution(
    private val populationSize: Int = 50,
    private val generationsCount: Int = 100,
    private val levelLength: Int = 200,
    private val evaluateOnLevelsCount: Int = 5,
    private val resultLevelsCount: Int = 5,
    private val fitnessFunction: MarioLevelEvaluator<Float> = MarioLevelEvaluators::distanceOnly,
    private val maxProbability: Double = 0.3,
    private val chartLabel: String = "PMP Level Evolution"
) : LevelEvolution {

    private lateinit var agentFactory: () -> IAgent
    private val chart = EvolutionLineChart(label = this.chartLabel, hideNegative = true)

    override fun evolve(agentFactory: () -> IAgent): Array<MarioLevel> {
        this.agentFactory = agentFactory
        val genotype = this.createInitialGenotype()
        val evolutionEngine = this.createEvolutionEngine(genotype)
        val resultIndividuals = this.doEvolution(evolutionEngine)

        return Array(resultIndividuals.size) { PMPLevelCreator.create(levelLength, resultIndividuals[it]) }
    }

    fun storeChart(path: String) {
        this.chart.save(path)
    }

    private fun createInitialGenotype(): Genotype<DoubleGene> {
        return Genotype.of(DoubleChromosome.of(*Array<DoubleGene>(PMPLevelCreator.PROBABILITIES_COUNT) { DoubleGene.of(0.0, 0.0, this.maxProbability) }))
    }

    private fun createEvolutionEngine(initialGenotype: Genotype<DoubleGene>): Engine<DoubleGene, Float> {
        return Engine.builder(fitness, initialGenotype)
            .optimize(Optimize.MAXIMUM)
            .populationSize(this.populationSize)
            .alterers(GaussianMutator(0.60))
            .survivorsSelector(EliteSelector(2))
            .offspringSelector(RouletteWheelSelector())
            .build()
    }

    private fun doEvolution(evolutionEngine: Engine<DoubleGene, Float>): List<DoubleArray> {
        this.chart.show()

        val evolutionStream = evolutionEngine.stream()
            .limit(this.generationsCount.toLong())
            .peek {
                val generation = it.generation.toInt()
                val bestFitness = it.bestFitness.toDouble()
                val averageFitness = it.population.asList().fold(0.0f, {accumulator, genotype -> accumulator + genotype.fitness}) / it.population.length()
                this.chart.update(generation, bestFitness, averageFitness.toDouble(), 0.0, 0.0)
                println("new gen: ${it.generation} (best fitness: ${it.bestFitness})")
            }

        return if (this.resultLevelsCount == 1) {
            val evolutionBestResult = this.collectSingleEvolutionResult(evolutionStream)
            listOf(evolutionBestResult)
        } else {
            this.collectMultiEvolutionResult(evolutionStream, this.resultLevelsCount)
        }
    }

    private fun collectSingleEvolutionResult(evolutionStream: Stream<EvolutionResult<DoubleGene, Float>>): DoubleArray {
        return evolutionStream
            .collect(EvolutionResult.toBestEvolutionResult<DoubleGene, Float>())
            .bestPhenotype.genotype.getDoubleValues()
    }

    private fun collectMultiEvolutionResult(evolutionStream: Stream<EvolutionResult<DoubleGene, Float>>, resultsCount: Int): List<DoubleArray> {
        val allGenerations = evolutionStream.collect(Collectors.toList())
        val allIndividuals = allGenerations.flatMap { it.population }
        val allIndividualsSorted = allIndividuals.sortedByDescending { it.fitness }
        val bestIndividuals = allIndividualsSorted.subList(0, resultsCount.coerceAtMost(allIndividuals.size))

        return bestIndividuals.map { it.genotype.getDoubleValues() }
    }

    private val fitness = Function<Genotype<DoubleGene>, Float> { genotype -> fitness(genotype) }
    private fun fitness(genotype: Genotype<DoubleGene>): Float {

        // TODO: reevaluate on every generation for all individuals
        val genes = genotype.getDoubleValues()

        val levels = Array(this.evaluateOnLevelsCount) { PMPLevelCreator.create(levelLength, genes) }
        val stats = levels.map {level ->
            val agent = this.agentFactory()
            val marioSimulator = GameSimulator()
            marioSimulator.playMario(agent, level, false)
        }

        return this.fitnessFunction(stats)
    }

}
