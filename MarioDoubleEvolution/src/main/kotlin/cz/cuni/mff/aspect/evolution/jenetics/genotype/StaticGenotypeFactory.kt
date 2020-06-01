package cz.cuni.mff.aspect.evolution.jenetics.genotype

import io.jenetics.Gene
import io.jenetics.Genotype
import io.jenetics.util.Factory

/**
 * Static genotype factory creating only given genotypes.
 *
 * @param genotypes the genotypes to be used when factoring new data.
 */
class StaticGenotypeFactory<G : Gene<*, G>?>(private val genotypes: List<Genotype<G>>) : Factory<Genotype<G>> {

    private var latestGeneratedIndex = 0

    override fun newInstance(): Genotype<G> = this.genotypes[latestGeneratedIndex++ % this.genotypes.size]

}
