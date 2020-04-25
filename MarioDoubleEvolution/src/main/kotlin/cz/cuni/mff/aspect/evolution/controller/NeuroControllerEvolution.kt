package cz.cuni.mff.aspect.evolution.controller

import cz.cuni.mff.aspect.evolution.Charted
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.utils.getDoubleValues
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.UpdatedAgentNetwork
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart
import cz.woitee.endlessRunners.evolution.utils.MarioEvaluator
import io.jenetics.*
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.internal.util.Concurrency
import io.jenetics.util.DoubleRange
import io.jenetics.util.Factory
import java.lang.IllegalArgumentException
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
    private val levelsPerGeneratorCount: Int = 5,
    private val chartLabel: String = "NeuroController evolution",
    private val showChart: Boolean = true,
    private val chart: EvolutionLineChart = EvolutionLineChart(chartLabel, hideNegative = true)
) : ControllerEvolution, Charted by chart {

    private lateinit var levelGenerators: List<LevelGenerator>
    private lateinit var fitnessFunction: MarioGameplayEvaluator<Float>
    private lateinit var objectiveFunction: MarioGameplayEvaluator<Float>
    private var initialAgentNetwork: UpdatedAgentNetwork? = null

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
        if (this.showChart && !this.chart.isShown) this.chart.show()
        this.chart.addStop()

        val genotype = this.createInitialGenotypes()
        val evaluator = this.createEvaluator()
        val engine = this.createEvolutionEngine(genotype, evaluator)
        val result = this.doEvolution(engine, evaluator)

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

    private fun createEvaluator(): MarioEvaluator<DoubleGene, Float> {
        val executor = if (this.parallel) ForkJoinPool.commonPool() else Concurrency.SERIAL_EXECUTOR

        return MarioEvaluator(
            executor,
            fitnessFunction,
            objectiveFunction,
            this.createControllerNetwork(),
            levelGenerators,
            levelsPerGeneratorCount,
            alwaysEvaluate = true
        )
    }

    private fun createEvolutionEngine(initialGenotype: Factory<Genotype<DoubleGene>>, evaluator: MarioEvaluator<DoubleGene, Float>): Engine<DoubleGene, Float> {
        val engine = Engine.Builder(evaluator, initialGenotype)
                .optimize(Optimize.MAXIMUM)
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

    private fun doEvolution(evolutionEngine: Engine<DoubleGene, Float>, evaluator: MarioEvaluator<DoubleGene, Float>): EvolutionResult<DoubleGene, Float> {
        return evolutionEngine.stream()
            .limit(this.generationsCount)
            .peek {
                val bestFitness = it.bestFitness().toDouble()
                val averageFitness = this.getAverageFitness(it).toDouble()
                val maxObjective = this.getBestObjectiveValue(evaluator).toDouble()
                val averageObjective = this.getAverageObjectiveValue(evaluator).toDouble()
                this.chart.nextGeneration(bestFitness, averageFitness, maxObjective, averageObjective)
                println("new gen: ${it.generation()} (best fitness: ${it.bestFitness()}, best objective: ${evaluator.getBestObjectiveFromLastGeneration()})")
            }
            .collect(EvolutionResult.toBestEvolutionResult<DoubleGene, Float>())
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

    private fun getAverageFitness(evolutionResult: EvolutionResult<DoubleGene, Float>): Float {
        return evolutionResult.population().asList().fold(0.0f, {accumulator, genotype -> accumulator + genotype.fitness()}) / evolutionResult.population().length()
    }

    private fun getBestObjectiveValue(evaluator: MarioEvaluator<DoubleGene, Float>): Float {
        return evaluator.getBestObjectiveFromLastGeneration()
    }

    private fun getAverageObjectiveValue(evaluator: MarioEvaluator<DoubleGene, Float>): Float {
        return evaluator.getAverageObjectiveFromLastGeneration().toFloat()
    }

    companion object {
        private const val DEFAULT_POPULATION_SIZE = 50
        private const val DEFAULT_GENERATIONS_COUNT: Long = 50
    }

}
