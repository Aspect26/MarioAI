package cz.cuni.mff.aspect.evolution.controller

import com.evo.NEAT.Environment
import com.evo.NEAT.Genome
import com.evo.NEAT.Pool
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.NeatAgentNetwork
import cz.cuni.mff.aspect.utils.DeepCopy
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit
import kotlin.math.max


class NeatControllerEvolution(
    private val networkSettings: NetworkSettings,
    private var generationsCount: Int = 200,
    private val populationSize: Int = 150,
    private val evaluateOnLevelsCount: Int = 25,
    private val denseInput: Boolean = true,
    private val displayChart: Boolean = true,
    chartLabel: String = "NEAT Evolution"
) : ControllerEvolution {
    private lateinit var topGenome: Genome

    class ControllerEvolutionEnvironment(private val levelGenerators: List<LevelGenerator>,
                                         private val networkSettings: NetworkSettings,
                                         private val fitnessFunction: MarioGameplayEvaluator<Float>,
                                         private val objectiveFunction: MarioGameplayEvaluator<Float>,
                                         private val evaluateOnLevelsCount: Int,
                                         private val denseInput: Boolean = true) : Environment {

        private lateinit var lastEvaluationFitnesses: FloatArray
        private lateinit var lastEvaluationObjectives: FloatArray

        override fun evaluateFitness(population: ArrayList<Genome>) {
            this.lastEvaluationFitnesses = FloatArray(population.size)
            this.lastEvaluationObjectives = FloatArray(population.size)

            population.forEachIndexed { index, genome ->
                val neatNetwork = NeatAgentNetwork(this.networkSettings, genome)
                val controller = SimpleANNController(neatNetwork)

                if (this.denseInput) {
                    controller.setDenseInput()
                }

                val marioSimulator = GameSimulator()
                val levels = Array(this.evaluateOnLevelsCount) {
                    this.levelGenerators[it % this.levelGenerators.size].generate()
                }
                val statistics = marioSimulator.playMario(controller, levels,false)

                val fitnessValue = this.fitnessFunction(statistics)
                val objectiveValue = this.objectiveFunction(statistics)

                genome.fitness = fitnessValue

                this.lastEvaluationFitnesses[index] = fitnessValue
                this.lastEvaluationObjectives[index] = objectiveValue
            }
        }

        fun getAverageFitnessFromLastGeneration(): Float =
            this.lastEvaluationFitnesses.fold(0.0f, { accumulator, fitnessValue -> accumulator + fitnessValue }) / this.lastEvaluationFitnesses.size

        fun getMaxFitnessFromLastGeneration(): Float =
            this.lastEvaluationFitnesses.fold(Float.MIN_VALUE, { accumulator, fitnessValue -> max(accumulator, fitnessValue) })

        fun getAverageObjectiveFromLastGeneration(): Float =
            this.lastEvaluationObjectives.fold(0.0f, { accumulator, objectiveValue -> accumulator + objectiveValue }) / this.lastEvaluationFitnesses.size

        fun getMaxObjectiveFromLastGeneration(): Float =
            this.lastEvaluationObjectives.fold(Float.MIN_VALUE, { accumulator, objectiveValue -> max(accumulator, objectiveValue) })
    }

    override val chart: EvolutionLineChart = EvolutionLineChart(chartLabel, hideNegative = true)

    override fun evolve(
        levelGenerators: List<LevelGenerator>,
        fitness: MarioGameplayEvaluator<Float>,
        objective: MarioGameplayEvaluator<Float>
    ): MarioController {
        val networkInputSize = NeatAgentNetwork(this.networkSettings, Genome(0, 0)).inputLayerSize
        val networkOutputSize = 4

        val pool = Pool(this.populationSize)
        pool.initializePool(networkInputSize, networkOutputSize)

        return this.doEvolution(levelGenerators, fitness, objective, pool, this.networkSettings, this.denseInput)
    }

    override fun continueEvolution(
        controller: MarioController,
        levelGenerators: List<LevelGenerator>,
        fitness: MarioGameplayEvaluator<Float>,
        objective: MarioGameplayEvaluator<Float>
    ): MarioController {
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

        return this.doEvolution(levelGenerators, fitness, objective, pool, controller.network.networkSettings, controller.network.denseInput)
    }

    private fun doEvolution(
        levelGenerators: List<LevelGenerator>,
        fitness: MarioGameplayEvaluator<Float>,
        objective: MarioGameplayEvaluator<Float>,
        genotypePool: Pool,
        networkSettings: NetworkSettings,
        denseInput: Boolean
    ): MarioController {
        val evolution = ControllerEvolutionEnvironment(levelGenerators, networkSettings, fitness, objective, this.evaluateOnLevelsCount, denseInput)

        if (this.displayChart && !this.chart.isShown) this.chart.show()
        this.chart.addStop()

        var generation = 1
        val startTime = System.currentTimeMillis()
        while (generation < this.generationsCount) {
            genotypePool.evaluateFitness(evolution)

            val averageFitness = evolution.getAverageFitnessFromLastGeneration()
            val maxFitness = evolution.getMaxFitnessFromLastGeneration()
            val averageObjective = evolution.getAverageObjectiveFromLastGeneration()
            val maxObjective = evolution.getMaxObjectiveFromLastGeneration()

            this.chart.nextGeneration(maxFitness.toDouble(), averageFitness.toDouble(), maxObjective.toDouble(), averageObjective.toDouble())
            val currentTimeMillis = System.currentTimeMillis() - startTime
            val timeString = String.format("%02d min, %02d sec",
                TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis),
                TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis))
            )
            println("($timeString) Neat gen: $generation: Fitness - Max: $maxFitness, Avg: $averageFitness")

            genotypePool.breedNewGeneration()
            generation++
        }

        this.topGenome = genotypePool.topGenome
        val network = NeatAgentNetwork(networkSettings, this.topGenome)

        return SimpleANNController(network)
    }

}
