package cz.cuni.mff.aspect.utils

import cz.cuni.mff.aspect.evolution.levels.ge.algorithm.jenetics.ByteGene
import io.jenetics.Genotype
import io.jenetics.IntegerGene
import io.jenetics.NumericGene
import io.jenetics.util.ISeq
import io.jenetics.util.Seq

/** Get values of this genotype in a double array. */
fun <G> Genotype<G>.getDoubleValues(): DoubleArray
        where G : NumericGene<*, G> {
    val geneSequence: Seq<G> = ISeq.of(this.chromosome())

    val array = DoubleArray(geneSequence.size())
    var i = geneSequence.size()
    while (--i >= 0) {
        array[i] = geneSequence[i].allele().toDouble()
    }

    return array
}

/** Get values of this genotype in an integer array. */
fun Genotype<IntegerGene>.getIntValues(): IntArray {
    val geneSequence: Seq<IntegerGene> = ISeq.of(this.chromosome())

    val array = IntArray(geneSequence.size())
    var i = geneSequence.size()
    while (--i >= 0) {
        array[i] = geneSequence[i].allele()
    }

    return array
}

/** Get values of this genotype in an byte array. */
fun Genotype<ByteGene>.getByteValues(): ByteArray {
    val geneSequence: Seq<ByteGene> = ISeq.of(this.chromosome())

    val array = ByteArray(geneSequence.size())
    var i = geneSequence.size()
    while (--i >= 0) {
        array[i] = geneSequence[i].allele()
    }

    return array
}
