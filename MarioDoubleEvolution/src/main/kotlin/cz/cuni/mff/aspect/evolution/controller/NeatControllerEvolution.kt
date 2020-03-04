package cz.cuni.mff.aspect.evolution.controller

import com.evo.NEAT.Environment
import com.evo.NEAT.Genome
import com.evo.NEAT.Pool
import cz.cuni.mff.aspect.evolution.utils.MarioGameplayEvaluator
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.NeatAgentNetwork
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.max


class NeatControllerEvolution(
    private val networkSettings: NetworkSettings,
    private var generationsCount: Int = 200,
    private val populationSize: Int = 150,
    private val denseInput: Boolean = true,
    private val chartName: String = "NEAT Evolution"
) : ControllerEvolution {

    private lateinit var topGenome: Genome
    private val chart = EvolutionLineChart(
        label = this.chartName,
        hideNegative = true
    )

    class ControllerEvolutionEnvironment(private val levels: Array<MarioLevel>,
                                         private val networkSettings: NetworkSettings,
                                         private val fitnessFunction: MarioGameplayEvaluator<Float>,
                                         private val objectiveFunction: MarioGameplayEvaluator<Float>,
                                         private val denseInput: Boolean = true,
                                         private val levelsCount: Int = levels.size) : Environment {

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
                val statistics = marioSimulator.playRandomLevels(controller, this.levels, this.levelsCount, false)

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

    override fun evolve(levels: Array<MarioLevel>, fitness: MarioGameplayEvaluator<Float>, objective: MarioGameplayEvaluator<Float>): MarioController {
        val startTime = System.currentTimeMillis()
        // TODO: levelsCount as parameter to the evolution
        val evolution = ControllerEvolutionEnvironment(levels, this.networkSettings, fitness, objective, this.denseInput)
        val networkInputSize = NeatAgentNetwork(this.networkSettings, Genome(0,0)).inputLayerSize
        val networkOutputSize = 4
        val pool = Pool(this.populationSize)

        pool.initializePool(networkInputSize, networkOutputSize)
        this.chart.show()

        var generation = 1

        while (generation < this.generationsCount) {
            pool.evaluateFitness(evolution)

            val averageFitness = evolution.getAverageFitnessFromLastGeneration()
            val maxFitness = evolution.getMaxFitnessFromLastGeneration()
            val averageObjective = evolution.getAverageObjectiveFromLastGeneration()
            val maxObjective = evolution.getMaxObjectiveFromLastGeneration()

            this.chart.update(generation, maxFitness.toDouble(), averageFitness.toDouble(), maxObjective.toDouble(), averageObjective.toDouble())
            val currentTimeMillis = System.currentTimeMillis() - startTime
            val timeString = String.format("%02d min, %02d sec",
                TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis),
                TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis))
            )
            println("($timeString) Neat gen: $generation: Fitness - Max: $maxFitness, Avg: $averageFitness")

            pool.breedNewGeneration()
            generation++
        }

        this.topGenome = pool.topGenome
        val network = NeatAgentNetwork(this.networkSettings, this.topGenome)

        return SimpleANNController(network)
    }

    fun storeChart(path: String) = this.chart.save(path)

}
