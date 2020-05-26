package cz.cuni.mff.aspect.evolution.controller.neuroevolution

import cz.cuni.mff.aspect.evolution.ChartedJeneticsEvolution
import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluator
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.HiddenLayerControllerNetwork
import cz.cuni.mff.aspect.utils.getDoubleValues
import cz.cuni.mff.aspect.visualisation.charts.evolution.EvolutionLineChart
import io.jenetics.*
import io.jenetics.util.DoubleRange
import io.jenetics.util.Factory


/**
 * Implementation of a Super Mario controller evolution using simple neuroevolution algorithm, which evolves only
 * weights on a given non-changing ANN. The implementation is highly customizable in terms of multiple properties
 * of the evolution, which can be specified via primary constructor.
 *
 * @param controllerNetworkSettings specifies network settings.
 * @param generationsCount number specifying how many generations the evolution should run for.
 * @param populationSize size of a population.
 * @param evaluateOnLevelsCount number of levels on which each individual should be evaluated. If the individuals are
 * evaluated on multiple level generators, this number is evenly split amongst them. If that it not possible, some
 * generators will generate one level less.
 * @param fitnessFunction specifies fitness function.
 * @param objectiveFunction specifies objective function.
 * @param parallel specifies, whether the evolution should run on multiple CPU cores.
 * @param alterers specifies alterers (mutators) for the evolution.
 * @param survivorsSelector specifies selector for the survivors in the evolution.
 * @param offspringSelector specifies selector for the offsprings in the evolution.
 * @param weightsRange specifies range for the weights.
 * @param alwaysReevaluate specifies, whether the individuals should be reevaluated in each generation during the
 * evolution, or only when they are created. This is useful when the fitness computation is randomized somehow.
 * @param displayChart specifies, whether a real-time chart, which displays fitness values and objective values,
 * should be displayed.
 * @param chartLabel label of the chart.
 * @see NetworkSettings
 * @see MarioGameplayEvaluator
 * @see Alterer
 * @see Selector
 */
class NeuroControllerEvolution(
    private var controllerNetworkSettings: NetworkSettings? = null,
    generationsCount: Int = DEFAULT_GENERATIONS_COUNT,
    populationSize: Int = DEFAULT_POPULATION_SIZE,
    private val fitnessFunction: MarioGameplayEvaluator<Float> = MarioGameplayEvaluators::distanceOnly,
    private val objectiveFunction: MarioGameplayEvaluator<Float>  = MarioGameplayEvaluators::victoriesOnly,
    parallel: Boolean = true,
    alterers: Array<Alterer<DoubleGene, Float>> = arrayOf(Mutator(0.05)),
    survivorsSelector: Selector<DoubleGene, Float> = EliteSelector(2),
    offspringSelector: Selector<DoubleGene, Float> = TournamentSelector(2),
    private val weightsRange: DoubleRange = DoubleRange.of(-1.0, 1.0),
    private val evaluateOnLevelsCount: Int = 25,
    alwaysReevaluate: Boolean = true,
    displayChart: Boolean = true,
    chartLabel: String = "NeuroController evolution"
) : ChartedJeneticsEvolution<MarioController>(
    populationSize = populationSize,
    generationsCount = generationsCount,
    fitnessOptimization = Optimize.MAXIMUM,
    objectiveOptimization = Optimize.MAXIMUM,
    alterers = alterers,
    survivorsSelector = survivorsSelector,
    offspringSelector = offspringSelector,
    displayChart = displayChart,
    chart = EvolutionLineChart(
        chartLabel,
        hideNegative = false
    ),
    parallel = parallel,
    alwaysReevaluate = alwaysReevaluate
), ControllerEvolution {

    private lateinit var levelGenerators: List<LevelGenerator>

    private var initialAgentNetwork: HiddenLayerControllerNetwork? = null

    override fun evolve(levelGenerators: List<LevelGenerator>): MarioController {
        this.levelGenerators = levelGenerators
        return this.evolve()
    }

    override fun continueEvolution(controller: MarioController, levelGenerators: List<LevelGenerator>): MarioController {
        if (controller !is SimpleANNController) throw IllegalArgumentException(
            "This implementation of controller evolution supports only `${SimpleANNController}` instances to continue evolution"
        )

        if (controller.network !is HiddenLayerControllerNetwork) throw IllegalArgumentException(
            "This implementation of controller evolution supports only `${SimpleANNController}` with `${HiddenLayerControllerNetwork}` as its network"
        )

        this.initialAgentNetwork = controller.network
        this.levelGenerators = levelGenerators
        this.controllerNetworkSettings = controller.network.networkSettings.copy()

        return this.evolve()
    }

    override fun createGenotypeFactory(): Factory<Genotype<DoubleGene>> {
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

    override fun entityFromIndividual(genotype: Genotype<DoubleGene>): MarioController {
        val controllerNetwork = this.createControllerNetwork()
        controllerNetwork.setNetworkWeights(genotype.getDoubleValues())

        return SimpleANNController(controllerNetwork)
    }

    override fun computeFitnessAndObjective(genotype: Genotype<DoubleGene>): Pair<Float, Float> {
        val networkWeights: DoubleArray = genotype.getDoubleValues()
        val controllerNetwork = this.createControllerNetwork()
        controllerNetwork.setNetworkWeights(networkWeights)

        val controller = SimpleANNController(controllerNetwork)
        val levels = Array(this.evaluateOnLevelsCount) {
            this.levelGenerators[it % this.levelGenerators.size].generate()
        }
        val marioSimulator = GameSimulator(1500)
        val statistics = marioSimulator.playMario(controller, levels, false)

        return Pair(fitnessFunction(statistics), objectiveFunction(statistics))
    }

    private fun createControllerNetwork(): HiddenLayerControllerNetwork {
        if (this.controllerNetworkSettings == null) throw UnsupportedOperationException("Controller network settings not specified")
        return HiddenLayerControllerNetwork(this.controllerNetworkSettings!!)
    }

    companion object {
        private const val DEFAULT_POPULATION_SIZE = 50
        private const val DEFAULT_GENERATIONS_COUNT = 50
    }

}
