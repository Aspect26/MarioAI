package cz.cuni.mff.aspect.utils

import io.jenetics.Genotype
import io.jenetics.IntegerGene
import io.jenetics.NumericGene
import io.jenetics.util.ISeq
import io.jenetics.util.Seq


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

fun Genotype<IntegerGene>.getIntValues(): IntArray {
    val geneSequence: Seq<IntegerGene> = ISeq.of(this.chromosome())

    val array = IntArray(geneSequence.size())
    var i = geneSequence.size()
    while (--i >= 0) {
        array[i] = geneSequence[i].allele()
    }
    return array
}
