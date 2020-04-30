package cz.woitee.endlessRunners.evolution.utils

import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluator
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.ControllerArtificialNetwork
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.utils.getDoubleValues
import io.jenetics.Genotype
import io.jenetics.NumericGene
import io.jenetics.Phenotype
import io.jenetics.engine.Evaluator
import io.jenetics.internal.util.Concurrency
import io.jenetics.util.ISeq
import io.jenetics.util.Seq
import java.util.*
import java.util.concurrent.Executor
import java.util.stream.Stream

/**
 * A custom implementation of concurrent evaluator (mostly rewriting io.jenetics.engine.ConcurrentEvaluator to Kotlin),
 * which was necessary, for the original has only internal visibility.
 *
 *
 * Additionally we deal with some of the issues when using jenetics. First, we provide a simple option to reevaluate all,
 * even surviving individuals in each generation. Secondly, we can distribute seeds consistently to fitness evaluations,
 * such that we have reproducible results.
 *
 * Implementation was updated to support jenetics version 5.0 and compute also objective function
 */
class MarioEvaluator<G, C>(
    private val executor: Executor,
    private val fitnessFunction: MarioGameplayEvaluator<C>,
    private val objectiveFunction: MarioGameplayEvaluator<C>,
    private val controllerNetwork: ControllerArtificialNetwork,
    private val levelGenerators: List<LevelGenerator>,
    private val levelsPerGeneratorCount: Int,
    private val alwaysEvaluate: Boolean = false
) : Evaluator<G, C>
        where G : NumericGene<*, G>,
              C : Comparable<C>,
              C : Number {
    private val objectiveResults = mutableListOf<HashMap<Int, C>>()

    /**
     * Evaluate implementation, possibly reevaluating all individuals in a population.
     */
    override fun eval(population: Seq<Phenotype<G, C>>): ISeq<Phenotype<G, C>> {
        // Evaluation happens twice per evolution step in jenetics
        // But we should do the "always evaluation" only once, since it would be wasteful otherwise
        val shouldReevaluateAll = alwaysEvaluate && isEverybodyEvaluated(population)

        return if (shouldReevaluateAll) {
            val populationStream = population.stream()
            // The only way to un-evaluate a phenotype is to create a new one
            // val phenotypes = populationStream.map { pt -> pt.withGeneration(pt.generation); }
            evaluate(populationStream)
        } else {
            val originalEvaluated = population.filter { pt -> pt.isEvaluated }
            val originalNotEvaluated = population.stream().filter { pt -> !pt.isEvaluated }
            val evaluated: Seq<Phenotype<G, C>> = evaluate(originalNotEvaluated)

            evaluated.prepend(originalEvaluated).asISeq()
        }
    }

    fun getBestObjectiveFromLastGeneration(): C {
        return this.objectiveResults.last().values.max()!!
    }

    fun getAverageObjectiveFromLastGeneration(): Double {
        val objectives = this.objectiveResults.last().values
        val objectivesSum = objectives.sumByDouble { it.toDouble() }
        return objectivesSum / objectives.size
    }

    private fun evaluate(phenotypes: Stream<Phenotype<G, C>>): ISeq<Phenotype<G, C>> {
        val phenotypeRunnables = phenotypes
            .map { pt -> PhenotypeEvaluation(pt, fitnessFunction, objectiveFunction, controllerNetwork, levelGenerators, levelsPerGeneratorCount) }
            .collect(ISeq.toISeq())

        val concurrency = Concurrency.with(executor)
        concurrency.execute(phenotypeRunnables)
        concurrency.close()

        val newPhenotypes = phenotypeRunnables.map { it.phenotype() }
        val objectives = hashMapOf<Int, C>()
        phenotypeRunnables.forEach {
            val genotypeHash = System.identityHashCode(it.phenotype().genotype())
            val objectiveValue = it.objective
            objectives[genotypeHash] = objectiveValue
        }
        this.objectiveResults.add(objectives)

        return newPhenotypes
    }

    /**
     * Returns whether all of the individuals are already evaluated in a population.
     */
    private fun isEverybodyEvaluated(population: Iterable<Phenotype<G, C>>): Boolean {
        for (phenotype in population) {
            if (!phenotype.isEvaluated) return false
        }
        return true
    }
}

private class PhenotypeEvaluation<G, C> internal constructor(
    private val _phenotype: Phenotype<G, C>,
    private val fitnessFunction: MarioGameplayEvaluator<C>,
    private val objectiveFunction: MarioGameplayEvaluator<C>,
    private val controllerNetwork: ControllerArtificialNetwork,
    private val levelGenerators: List<LevelGenerator>,
    private val evaluateOnLevelsCount: Int
) : Runnable
        where G : NumericGene<*, G>,
              C : Comparable<C>,
              C : Number {
    lateinit var objective: C
    private lateinit var fitness: C

    override fun run() {
        this.computeFitnessAndObjective(_phenotype.genotype())
    }

    private fun computeFitnessAndObjective(genotype: Genotype<G>) {
        val networkWeights: DoubleArray = genotype.getDoubleValues()
        val controllerNetwork = this.controllerNetwork.newInstance()
        controllerNetwork.setNetworkWeights(networkWeights)

        val controller = SimpleANNController(controllerNetwork)
        val levels = this.generateLevelsToPlay()
        val marioSimulator = GameSimulator(1000)
        val statistics = marioSimulator.playMario(controller, levels, false)

        fitness = fitnessFunction(statistics)
        objective = objectiveFunction(statistics)
    }

    internal fun phenotype(): Phenotype<G, C> {
        return _phenotype.withFitness(fitness)
    }

    private fun generateLevelsToPlay(): Array<MarioLevel> =
        Array(this.evaluateOnLevelsCount * this.levelGenerators.size) {
            this.levelGenerators[it % this.levelGenerators.size].generate()
        }

}
