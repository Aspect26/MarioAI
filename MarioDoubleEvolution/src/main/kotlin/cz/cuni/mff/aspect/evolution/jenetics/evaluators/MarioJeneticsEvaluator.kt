package cz.cuni.mff.aspect.evolution.jenetics.evaluators

import io.jenetics.Gene
import io.jenetics.Genotype
import io.jenetics.Phenotype
import io.jenetics.engine.Evaluator
import io.jenetics.internal.util.Concurrency
import io.jenetics.util.ISeq
import io.jenetics.util.Seq
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool

/**
 * This evaluator evaluates also objective value, not only fitness and has the ability to evaluate fitness of each
 * individual in each generation whereas the default Jenetics evaluator evaluates fitness of an individual only once
 * throughout the whole evolution.
 */
class MarioJeneticsEvaluator<G : Gene<*, G>, C : Comparable<C>>(
    private val fitnessAndObjectiveFunction: (genotype: Genotype<G>) -> Pair<C, C>,
    private val alwaysEvaluate: Boolean,
    private val executor: Executor = ForkJoinPool.commonPool()
) : Evaluator<G, C> {
    private val concurrency = Concurrency.with(this.executor)

    private var _lastGenerationObjectives: List<C> = listOf()
    private var _lastGenerationFitnesses: List<C> = listOf()
    private var _lastGeneration: List<Genotype<G>> = listOf()

    private val objectiveValues: MutableMap<Int, C> = mutableMapOf()
    private val fitnessValues: MutableMap<Int, C> = mutableMapOf()

    val lastGenerationObjectives: List<C> get() = this._lastGenerationObjectives
    val lastGenerationFitnesses: List<C> get() = this._lastGenerationFitnesses
    val lastGeneration: List<Genotype<G>> get() = this._lastGeneration

    /**
     * Evaluate implementation, possibly reevaluating all individuals in a population.
     */
    override fun eval(population: Seq<Phenotype<G, C>>): ISeq<Phenotype<G, C>> {
        // Evaluation happens twice per evolution step in jenetics
        // But we should do the "always evaluation" only once, since it would be wasteful otherwise
        val newPopulation: ISeq<Phenotype<G, C>> = when {
            isEverybodyEvaluated(population) -> {
                population.asISeq()
            }
            alwaysEvaluate -> {
                val evaluationsResult = evaluate(population)
                ISeq.of(evaluationsResult.map { it.phenotype })
            }
            else -> {
                val evaluated = population.filter { pt -> pt.isEvaluated }.toMutableList()
                val notEvaluated = ISeq.of(population.filter { pt -> !pt.isEvaluated })

                val evaluationsResult = evaluate(notEvaluated)

                evaluated.addAll(evaluationsResult.map { it.phenotype })
                ISeq.of(evaluated)
            }
        }

        this._lastGenerationObjectives = newPopulation.map { this.objectiveValues[it.hashCode()]!! }.asList()
        this._lastGenerationFitnesses = newPopulation.map { this.fitnessValues[it.hashCode()]!! }.asList()
        this._lastGeneration = newPopulation.asList().map { it.genotype() }.toList()

        return newPopulation
    }

    private fun evaluate(phenotypes: Seq<Phenotype<G, C>>): List<EvaluationResult<G, C>> {
        val evaluators = phenotypes.map { pt ->
            PhenotypeEvaluator(
                pt,
                this.fitnessAndObjectiveFunction
            )
        }

        this.concurrency.execute(evaluators)
        this.concurrency.close()

        val evaluationsResults = evaluators.map { it.result }
        evaluationsResults.forEach {
            objectiveValues[it.phenotype.hashCode()] = it.objective
            fitnessValues[it.phenotype.hashCode()] = it.fitness
        }

        return evaluationsResults.asList()
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


private class PhenotypeEvaluator<G : Gene<*, G>, C : Comparable<C>> internal constructor(
    private val phenotype: Phenotype<G, C>,
    private val fitnessAndObjectiveFunction: (genotype: Genotype<G>) -> Pair<C, C>
) : Runnable {
    private lateinit var fitness: C
    private lateinit var objective: C

    override fun run() {
        val (fitness, objective) = this.fitnessAndObjectiveFunction(this.phenotype.genotype())
        this.fitness = fitness
        this.objective = objective
    }

    internal val result: EvaluationResult<G, C>
        get() = EvaluationResult(
            this.phenotype.withFitness(fitness),
            this.fitness,
            this.objective
        )

}

private data class EvaluationResult<G : Gene<*, G>, C : Comparable<C>>(
    val phenotype: Phenotype<G, C>,
    val fitness: C,
    val objective: C
)