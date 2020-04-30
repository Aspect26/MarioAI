package cz.cuni.mff.aspect.evolution.jenetics.alterers

import io.jenetics.Alterer
import io.jenetics.Mutator
import io.jenetics.NumericGene
import io.jenetics.internal.math.Basics.clamp
import java.lang.String.format

import java.util.Random

/**
 * Updated [io.jenetics.GaussianMutator] with added possibility to define standard deviation.
 */
class UpdatedGaussianMutator<G : NumericGene<*, G>, C : Comparable<C>> @JvmOverloads constructor(
    private val standardDeviation: Double,
    probability: Double = Alterer.DEFAULT_ALTER_PROBABILITY
) : Mutator<G, C>(probability) {

    override fun mutate(gene: G, random: Random): G {
        val min = gene.min().toDouble()
        val max = gene.max().toDouble()
        val std = (max - min) * this.standardDeviation

        val value = gene.doubleValue()
        val gaussian = random.nextGaussian()
        return gene.newInstance(clamp(gaussian * std + value, min, max))
    }

    override fun toString(): String {
        return format("%s[p=%f]", javaClass.simpleName, _probability)
    }

}
