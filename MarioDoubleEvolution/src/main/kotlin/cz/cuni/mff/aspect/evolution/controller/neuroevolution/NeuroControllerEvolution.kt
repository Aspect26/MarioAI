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
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart
import io.jenetics.*
import io.jenetics.util.DoubleRange
import io.jenetics.util.Factory


/**
 * Implementation of an evolution of ANN agent controller.
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
    chart = EvolutionLineChart(chartLabel, hideNegative = false),
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

    override fun createInitialGenotype(): Factory<Genotype<DoubleGene>> {
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
        val marioSimulator = GameSimulator(1000)
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
