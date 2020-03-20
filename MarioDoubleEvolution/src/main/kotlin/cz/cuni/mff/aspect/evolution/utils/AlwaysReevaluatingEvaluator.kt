package cz.cuni.mff.aspect.evolution.utils

import io.jenetics.Gene
import io.jenetics.Genotype
import io.jenetics.Phenotype
import io.jenetics.engine.Evaluator
import io.jenetics.internal.util.Concurrency
import io.jenetics.util.ISeq
import io.jenetics.util.Seq
import java.util.concurrent.Executor


class AlwaysReevaluatingEvaluator<G : Gene<*, G>, C : Comparable<C>>(
    private val fitnessFunction: (genotype: Genotype<G>) -> C,
    private val executor: Executor? = null
) : Evaluator<G, C> {

    /**
     * Evaluate implementation, possibly reevaluating all individuals in a population.
     */
    override fun eval(population: Seq<Phenotype<G, C>>): ISeq<Phenotype<G, C>> {
        // Evaluation happens twice per evolution step in jenetics
        // But we should do the "always evaluation" only once, since it would be wasteful otherwise
        val notAllEvaluated = !isEverybodyEvaluated(population)

        return if (notAllEvaluated) {
            val phenotypeEvaluators = population.map { pt -> PhenotypeEvaluator(pt, this.fitnessFunction) }

            val concurrency = Concurrency.with(this.executor)
            concurrency.execute(phenotypeEvaluators)
            concurrency.close()

            ISeq.of(phenotypeEvaluators.map { it.phenotype() })
        } else {
            population.asISeq()
        }
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
    private val fitnessFunction: (genotype: Genotype<G>) -> C
) : Runnable {
    private lateinit var fitness: C

    override fun run() {
        this.fitness = this.fitnessFunction(this.phenotype.genotype)
    }

    internal fun phenotype(): Phenotype<G, C> {
        return phenotype.withFitness(fitness)
    }

}
