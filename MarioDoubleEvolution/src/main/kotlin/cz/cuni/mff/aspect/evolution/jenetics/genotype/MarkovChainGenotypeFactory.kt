package cz.cuni.mff.aspect.evolution.jenetics.genotype

import io.jenetics.DoubleChromosome
import io.jenetics.DoubleGene
import io.jenetics.Genotype
import io.jenetics.util.Factory

/**
 * Genotype factory for genotypes representing a Markov Chain, with an option to add additional probabilities to the end
 * of the individual.
 */
class MarkovChainGenotypeFactory(private val statesCount: Int, private val additionalProbabilitiesCount: Int) :
    Factory<Genotype<DoubleGene>> {

    override fun newInstance(): Genotype<DoubleGene> =
        Genotype.of(DoubleChromosome.of(List<DoubleGene>(statesCount + statesCount * statesCount + additionalProbabilitiesCount) {
            when {
                it < statesCount + statesCount * statesCount && it % statesCount == 0 -> DoubleGene.of(1.0, 0.0, 1.0)
                it < statesCount + statesCount * statesCount -> DoubleGene.of(0.0, 0.0, 1.0)
                else -> DoubleGene.of(0.0, 0.0, 1.0)
            }
        }))

}
