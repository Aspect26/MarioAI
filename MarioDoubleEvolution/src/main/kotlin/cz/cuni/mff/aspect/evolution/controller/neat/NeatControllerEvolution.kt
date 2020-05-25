package cz.cuni.mff.aspect.evolution.controller.neat

import com.evo.NEAT.Genome
import com.evo.NEAT.Pool
import cz.cuni.mff.aspect.evolution.controller.*
import cz.cuni.mff.aspect.evolution.controller.evaluators.DistanceOnlyEvaluator
import cz.cuni.mff.aspect.evolution.controller.evaluators.MarioGameplayEvaluator
import cz.cuni.mff.aspect.evolution.controller.evaluators.VictoriesOnlyEvaluator
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.NeatAgentNetwork
import cz.cuni.mff.aspect.utils.DeepCopy
import cz.cuni.mff.aspect.visualisation.charts.evolution.EvolutionLineChart
import java.lang.IllegalArgumentException

/**
 * Implementation of a Super Mario controller evolution using NEAT algorithm. The implementation is highly customizable
 * in terms of multiple properties of the evolution, which can be specified via primary constructor.
 *
 * @param networkSettings specifies network settings, however, not all the settings can be applied using NEAT. The only
 * property which is not used in this implementation is [NetworkSettings.hiddenLayerSize].
 * @param generationsCount number specifying how many generations the evolution should run for.
 * @param populationSize size of a population.
 * @param evaluateOnLevelsCount number of levels on which each individual should be evaluated. If the individuals are
 * evaluated on multiple level generators, this number is evenly split amongst them. If that it not possible, some
 * generators will generate one level less.
 * @param fitnessFunction specifies fitness function.
 * @param objectiveFunction specifies objective function.
 * @param displayChart specifies, whether a real-time chart, which displays fitness values and objective values,
 * should be displayed.
 * @param chartLabel label of the chart.
 * @see NetworkSettings
 * @see MarioGameplayEvaluator
 */
class NeatControllerEvolution(
    private val networkSettings: NetworkSettings,
    private val generationsCount: Int = 200,
    private val populationSize: Int = 150,
    private val evaluateOnLevelsCount: Int = 25,
    private val fitnessFunction: MarioGameplayEvaluator = DistanceOnlyEvaluator(),
    private val objectiveFunction: MarioGameplayEvaluator = VictoriesOnlyEvaluator(),
    private val displayChart: Boolean = true,
    chartLabel: String = "NEAT Evolution"
) : ControllerEvolution {
    private lateinit var topGenome: Genome

    override var chart: EvolutionLineChart =
        EvolutionLineChart(
            chartLabel,
            hideNegative = true
        )

    override fun evolve(levelGenerators: List<LevelGenerator>): MarioController {
        val networkInputSize = NeatAgentNetwork.inputLayerSize(this.networkSettings)
        val networkOutputSize = NeatAgentNetwork.OUTPUT_LAYER_SIZE

        val pool = Pool(this.populationSize)
        pool.initializePool(networkInputSize, networkOutputSize)

        return this.doEvolution(levelGenerators, pool, this.networkSettings)
    }

    override fun continueEvolution(controller: MarioController, levelGenerators: List<LevelGenerator>): MarioController {
        if (controller !is SimpleANNController) throw IllegalArgumentException(
            "This implementation of controller evolution supports only `$SimpleANNController` instances to continue evolution"
        )
        if (controller.network !is NeatAgentNetwork) throw IllegalArgumentException(
            "This implementation of controller evolution supports only `$SimpleANNController` with `$NeatAgentNetwork` as its network"
        )

        val controllerGenome = controller.network.genome

        val genomes: ArrayList<Genome> = ArrayList(this.populationSize)
        repeat((0 until this.populationSize).count()) { genomes.add(DeepCopy.copy(controllerGenome)) }

        val pool = Pool(this.populationSize)
        pool.initializePool(genomes)

        return this.doEvolution(levelGenerators, pool, controller.network.networkSettings)
    }

    private fun doEvolution(
        levelGenerators: List<LevelGenerator>,
        genotypePool: Pool,
        networkSettings: NetworkSettings
    ): MarioController {
        val environment = ControllerEvolutionEnvironment(
            levelGenerators, networkSettings, this.fitnessFunction, this.objectiveFunction, this.evaluateOnLevelsCount
        )

        if (this.displayChart && !this.chart.isShown) this.chart.show()
        if (!this.chart.isEmpty) this.chart.addStop()

        var generation = 1
        while (generation < this.generationsCount) {
            genotypePool.evaluateFitness(environment)

            val averageFitness = environment.averageFitnessFromLastGeneration
            val maxFitness = environment.maxFitnessFromLastGeneration
            val averageObjective = environment.averageObjectiveFromLastGeneration
            val maxObjective = environment.maxObjectiveFromLastGeneration

            this.chart.nextGeneration(maxFitness.toDouble(), averageFitness.toDouble(), maxObjective.toDouble(), averageObjective.toDouble())

            println("new gen: $generation (best fitness: $maxFitness, best objective: $maxObjective)")

            genotypePool.breedNewGeneration()
            generation++
        }

        this.topGenome = genotypePool.topGenome
        val network = NeatAgentNetwork(networkSettings, this.topGenome)

        return SimpleANNController(network)
    }

}
