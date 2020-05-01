package cz.cuni.mff.aspect.evolution.controller

import cz.cuni.mff.aspect.evolution.jenetics.evaluators.MarioJeneticsEvaluator
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.UpdatedAgentNetwork
import cz.cuni.mff.aspect.utils.getDoubleValues
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart
import io.jenetics.*
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.internal.util.Concurrency
import io.jenetics.util.DoubleRange
import io.jenetics.util.Factory
import java.util.concurrent.ForkJoinPool


/**
 * Implementation of an evolution of ANN agent controller.
 */
class NeuroControllerEvolution(
    private var controllerNetworkSettings: NetworkSettings? = null,
    private val generationsCount: Long = DEFAULT_GENERATIONS_COUNT,
    private val populationSize: Int = DEFAULT_POPULATION_SIZE,
    private val parallel: Boolean = true,
    private val mutators: Array<Alterer<DoubleGene, Float>> = arrayOf(Mutator(0.05)),
    private val survivorsSelector: Selector<DoubleGene, Float> = EliteSelector(2),
    private val offspringSelector: Selector<DoubleGene, Float> = TournamentSelector(2),
    private val weightsRange: DoubleRange = DoubleRange.of(-1.0, 1.0),
    private val evaluateOnLevelsCount: Int = 25,
    private val alwaysReevaluate: Boolean = true,
    private val displayChart: Boolean = true,
    chartLabel: String = "NeuroController evolution"
) : ControllerEvolution {

    private lateinit var levelGenerators: List<LevelGenerator>
    private lateinit var fitnessFunction: MarioGameplayEvaluator<Float>
    private lateinit var objectiveFunction: MarioGameplayEvaluator<Float>
    private var initialAgentNetwork: UpdatedAgentNetwork? = null
    private lateinit var evaluator: MarioJeneticsEvaluator<DoubleGene, Float>
    private val optimize = Optimize.MAXIMUM

    override val chart: EvolutionLineChart = EvolutionLineChart(chartLabel, hideNegative = true)

    override fun evolve(
        levelGenerators: List<LevelGenerator>,
        fitness: MarioGameplayEvaluator<Float>,
        objective: MarioGameplayEvaluator<Float>
    ): MarioController {
        this.levelGenerators = levelGenerators
        this.fitnessFunction = fitness
        this.objectiveFunction = objective

        return this.doEvolution()
    }

    override fun continueEvolution(
        controller: MarioController,
        levelGenerators: List<LevelGenerator>,
        fitness: MarioGameplayEvaluator<Float>,
        objective: MarioGameplayEvaluator<Float>
    ): MarioController {
        if (controller !is SimpleANNController) throw IllegalArgumentException(
            "This implementation of controller evolution supports only `${SimpleANNController}` instances to continue evolution"
        )

        if (controller.network !is UpdatedAgentNetwork) throw IllegalArgumentException(
            "This implementation of controller evolution supports only `${SimpleANNController}` with `${UpdatedAgentNetwork}` as its network"
        )

        this.initialAgentNetwork = controller.network
        this.levelGenerators = levelGenerators
        this.fitnessFunction = fitness
        this.objectiveFunction = objective
        this.controllerNetworkSettings = this.createNetworkSettings(controller.network)

        return this.doEvolution()
    }

    private fun doEvolution(): MarioController {
        if (this.displayChart && !this.chart.isShown) this.chart.show()
        this.chart.addStop()

        this.evaluator = this.createEvaluator()
        val genotype = this.createInitialGenotypes()
        val engine = this.createEvolutionEngine(genotype)
        val result = this.doEvolution(engine)

        println("Best fitness - ${result.bestFitness()}")

        val resultGenes = result.bestPhenotype().genotype().getDoubleValues()
        val controllerNetwork = this.createControllerNetwork()
        controllerNetwork.setNetworkWeights(resultGenes)

        return SimpleANNController(controllerNetwork)
    }

    private fun createInitialGenotypes(): Factory<Genotype<DoubleGene>> {
        return if (this.initialAgentNetwork == null) {
            Genotype.of(DoubleChromosome.of(this.weightsRange, this.createControllerNetwork().weightsCount))
        } else {
            val initialNetworkWeights = this.initialAgentNetwork!!.getNetworkWeights()
            val minValue = initialNetworkWeights.min()!!
            val maxValue = initialNetworkWeights.max()!!

            Factory {
                Genotype.of(DoubleChromosome.of(*Array<DoubleGene>(initialNetworkWeights.size) {
                    DoubleGene.of(initialNetworkWeights[it], DoubleRange.of(minValue, maxValue))
                }))
            }
        }
    }

    private fun createEvaluator(): MarioJeneticsEvaluator<DoubleGene, Float> {
        val executor = if (this.parallel) ForkJoinPool.commonPool() else Concurrency.SERIAL_EXECUTOR

        return MarioJeneticsEvaluator(
            this::computeFitnessAndObjective,
            this.alwaysReevaluate,
            executor
        )
    }

    private fun createEvolutionEngine(initialGenotype: Factory<Genotype<DoubleGene>>): Engine<DoubleGene, Float> {
        val engine = Engine.Builder(this.evaluator, initialGenotype)
                .optimize(this.optimize)
                .populationSize(this.populationSize)

        if (this.mutators.isNotEmpty()) {
            val (first, rest) = this.getFirstAndRest(this.mutators)
            engine.alterers(first, *rest)
        }

        return engine
            .survivorsSelector(this.survivorsSelector)
            .offspringSelector(this.offspringSelector)
            .build()
    }

    private inline fun <reified T> getFirstAndRest(data: Array<T>): Pair<T, Array<T>> {
        val rest = Array(data.size - 1) { data[it + 1] }
        return Pair(data[0], rest)
    }

    private fun doEvolution(evolutionEngine: Engine<DoubleGene, Float>): EvolutionResult<DoubleGene, Float> {
        return evolutionEngine.stream()
            .limit(this.generationsCount)
            .peek (this::onGenerationPassed)
            .collect(EvolutionResult.toBestEvolutionResult<DoubleGene, Float>())
    }

    private fun onGenerationPassed(evolutionResult: EvolutionResult<DoubleGene, Float>) {
        val bestFitness = evolutionResult.bestFitness().toDouble()
        val averageFitness = evolutionResult.population().asList().fold(0.0f, { acc, genotype -> acc + genotype.fitness() }) / evolutionResult.population().length()

        val averageObjective = this.evaluator.lastGenerationObjectives.average()
        val bestObjective = if (this.optimize == Optimize.MAXIMUM) this.evaluator.lastGenerationObjectives.max() else this.evaluator.lastGenerationObjectives.min()

        this.chart.nextGeneration(bestFitness, averageFitness.toDouble(), bestObjective!!.toDouble(), averageObjective)

        println("new gen: ${evolutionResult.generation()} (best fitness: ${evolutionResult.bestFitness()}, best objective: ${bestObjective})")
    }

    private fun createNetworkSettings(agentNetwork: UpdatedAgentNetwork): NetworkSettings {
        return NetworkSettings(
            agentNetwork.receptiveFieldSizeRow,
            agentNetwork.receptiveFieldSizeColumn,
            agentNetwork.receptiveFieldRowOffset,
            agentNetwork.receptiveFieldColumnOffset,
            agentNetwork.hiddenLayerSize
        )
    }

    private fun createControllerNetwork(): UpdatedAgentNetwork {
        if (this.controllerNetworkSettings == null) throw UnsupportedOperationException("Controller network settings not specified")
        val network = UpdatedAgentNetwork(
            receptiveFieldSizeRow = this.controllerNetworkSettings!!.receptiveFieldSizeRow,
            receptiveFieldSizeColumn = this.controllerNetworkSettings!!.receptiveFieldSizeColumn,
            receptiveFieldRowOffset = this.controllerNetworkSettings!!.receptiveFieldRowOffset,
            receptiveFieldColumnOffset = this.controllerNetworkSettings!!.receptiveFieldColumnOffset,
            hiddenLayerSize = this.controllerNetworkSettings!!.hiddenLayerSize
        )

        if (this.initialAgentNetwork != null)
            network.legacy = this.initialAgentNetwork!!.legacy

        return network
    }

    private fun computeFitnessAndObjective(genotype: Genotype<DoubleGene>): Pair<Float, Float> {
        val networkWeights: DoubleArray = genotype.getDoubleValues()
        val controllerNetwork = this.createControllerNetwork()
        controllerNetwork.setNetworkWeights(networkWeights)

        val controller = SimpleANNController(controllerNetwork)
        val levels = Array(this.evaluateOnLevelsCount) {
            this.levelGenerators[it % this.levelGenerators.size].generate()
        }
        val marioSimulator = GameSimulator(1000)
        val statistics = marioSimulator.playMario(controller, levels, false)

        return Pair(fitnessFunction(statistics), objectiveFunction(statistics))
    }

    companion object {
        private const val DEFAULT_POPULATION_SIZE = 50
        private const val DEFAULT_GENERATIONS_COUNT: Long = 50
    }

}
