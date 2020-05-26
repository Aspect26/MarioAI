package cz.cuni.mff.aspect.evolution.jenetics.genotype

import io.jenetics.DoubleChromosome
import io.jenetics.DoubleGene
import io.jenetics.Genotype
import io.jenetics.util.DoubleRange
import io.jenetics.util.Factory

/**
 * Genotype factory which creates genotype of [DoubleGene] with given length, where all genes are initialized to 0.
 *
 * @param genotypeLength fixed length of the genotype.
 * @param valuesRange range of values which the genes can acquire.
 */
class ZeroInitializingDoubleGenotypeFactory(
    private val genotypeLength: Int,
    private val valuesRange: DoubleRange
) : Factory<Genotype<DoubleGene>> {

    override fun newInstance(): Genotype<DoubleGene> =
        Genotype.of(
            DoubleChromosome.of(
                List<DoubleGene>(this.genotypeLength) {
                    DoubleGene.of(0.0, valuesRange)
                }
            )
        )

}