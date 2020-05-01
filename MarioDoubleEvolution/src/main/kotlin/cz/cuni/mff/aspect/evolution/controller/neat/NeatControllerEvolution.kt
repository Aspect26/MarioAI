package cz.cuni.mff.aspect.evolution.controller.neat

import com.evo.NEAT.Genome
import com.evo.NEAT.Pool
import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluator
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.NeatAgentNetwork
import cz.cuni.mff.aspect.utils.DeepCopy
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart
import java.lang.IllegalArgumentException


class NeatControllerEvolution(
    private val networkSettings: NetworkSettings,
    private var generationsCount: Int = 200,
    private val populationSize: Int = 150,
    private val evaluateOnLevelsCount: Int = 25,
    private val fitnessFunction: MarioGameplayEvaluator<Float> = MarioGameplayEvaluators::distanceOnly,
    private val objectiveFunction: MarioGameplayEvaluator<Float>  = MarioGameplayEvaluators::victoriesOnly,
    private val denseInput: Boolean = true,
    private val displayChart: Boolean = true,
    chartLabel: String = "NEAT Evolution"
) : ControllerEvolution {
    private lateinit var topGenome: Genome

    override val chart: EvolutionLineChart = EvolutionLineChart(chartLabel, hideNegative = true)

    override fun evolve(levelGenerators: List<LevelGenerator>): MarioController {
        val networkInputSize = NeatAgentNetwork.inputLayerSize(this.networkSettings)
        val networkOutputSize = NeatAgentNetwork.OUTPUT_LAYER_SIZE

        val pool = Pool(this.populationSize)
        pool.initializePool(networkInputSize, networkOutputSize)

        return this.doEvolution(levelGenerators, pool, this.networkSettings, this.denseInput)
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

        return this.doEvolution(levelGenerators, pool, controller.network.networkSettings, controller.network.denseInput)
    }

    private fun doEvolution(
        levelGenerators: List<LevelGenerator>,
        genotypePool: Pool,
        networkSettings: NetworkSettings,
        denseInput: Boolean
    ): MarioController {
        val environment = ControllerEvolutionEnvironment(
            levelGenerators, networkSettings, this.fitnessFunction, this.objectiveFunction, this.evaluateOnLevelsCount, denseInput
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
