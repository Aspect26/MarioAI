package cz.cuni.mff.aspect.evolution.levels.ge.algorithm.jenetics

import io.jenetics.BoundedChromosome
import io.jenetics.Chromosome
import io.jenetics.NumericChromosome
import io.jenetics.util.ISeq
import io.jenetics.util.IntRange


class ByteChromosome(@Transient private val genes: ISeq<ByteGene>, private val lengthRange: IntRange) :
        BoundedChromosome<Byte, ByteGene>, NumericChromosome<Byte, ByteGene> {

    override fun length(): Int {
        return this.genes.length()
    }

    override fun newInstance(genes: ISeq<ByteGene>): Chromosome<ByteGene> {
        return ByteChromosome(genes, lengthRange)
    }

    override fun newInstance(): Chromosome<ByteGene> {
        return of(lengthRange)
    }

    override fun get(index: Int): ByteGene {
        return this.genes.get(index)
    }

    override fun iterator(): MutableIterator<ByteGene> {
        return this.genes.iterator()
    }

    override fun isValid(): Boolean {
        return this.genes.forAll { it.isValid }
    }

    companion object {

        fun of(lengthRange: IntRange): ByteChromosome {
            val values = ByteGene.seq(lengthRange)
            return ByteChromosome(values, lengthRange)
        }

    }

}
