package cz.cuni.mff.aspect.evolution.controller.neat

import com.evo.NEAT.Environment
import com.evo.NEAT.Genome
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluator
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.NeatAgentNetwork
import kotlin.math.max

internal class ControllerEvolutionEnvironment(
    private val levelGenerators: List<LevelGenerator>,
    private val networkSettings: NetworkSettings,
    private val fitnessFunction: MarioGameplayEvaluator<Float>,
    private val objectiveFunction: MarioGameplayEvaluator<Float>,
    private val evaluateOnLevelsCount: Int,
    private val denseInput: Boolean = true
) : Environment {
    private lateinit var lastEvaluationFitnesses: FloatArray
    private lateinit var lastEvaluationObjectives: FloatArray

    override fun evaluateFitness(population: ArrayList<Genome>) {
        this.lastEvaluationFitnesses = FloatArray(population.size)
        this.lastEvaluationObjectives = FloatArray(population.size)

        population.forEachIndexed { index, genome ->
            val neatNetwork = NeatAgentNetwork(this.networkSettings, genome)
            val controller = SimpleANNController(neatNetwork)

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

    val averageFitnessFromLastGeneration: Float get() =
        this.lastEvaluationFitnesses.fold(0.0f, { accumulator, fitnessValue -> accumulator + fitnessValue }) / this.lastEvaluationFitnesses.size

    val maxFitnessFromLastGeneration: Float get() =
        this.lastEvaluationFitnesses.fold(-Float.MAX_VALUE, { accumulator, fitnessValue -> max(accumulator, fitnessValue) })

    val averageObjectiveFromLastGeneration: Float get() =
        this.lastEvaluationObjectives.fold(0.0f, { accumulator, objectiveValue -> accumulator + objectiveValue }) / this.lastEvaluationFitnesses.size

    val maxObjectiveFromLastGeneration: Float get() =
        this.lastEvaluationObjectives.fold(-Float.MAX_VALUE, { accumulator, objectiveValue -> max(accumulator, objectiveValue) })
}