package cz.cuni.mff.aspect.evolution.jenetics.alterers

import cz.cuni.mff.aspect.utils.getDoubleValues
import io.jenetics.*
import io.jenetics.util.RandomRegistry
import io.jenetics.util.Seq
import java.util.*

class MarkovChainMutator(
    private val statesCount: Int,
    private val changeInRandomVariableProbability: Double,
    private val swapInRandomVariableProbability: Double,
    private val additionalProbabilitiesChangeProbability: Double
) : Alterer<DoubleGene, Float> {

    override fun alter(population: Seq<Phenotype<DoubleGene, Float>>, generation: Long): AltererResult<DoubleGene, Float> {
        val random = RandomRegistry.random()

        val mutationResults: Seq<MutatorResult<Phenotype<DoubleGene, Float>>> = population.map {
            this.mutate(it.genotype(), generation, random)
        }

        return AltererResult.of(
            mutationResults.map {it.result()}.asISeq(),
            mutationResults.stream().mapToInt { it.mutations() }.sum()
        )
    }

    private fun mutate(oldIndividual: Genotype<DoubleGene>, generation: Long, random: Random): MutatorResult<Phenotype<DoubleGene, Float>> {
        val genes = oldIndividual.getDoubleValues()
        var mutations = 0

        // mutate additional probabilities
        for (index in statesCount +  statesCount * statesCount until genes.size) {
            val oldGene = oldIndividual.get(0).get(index)
            genes[index] = if (random.nextDouble() < this.additionalProbabilitiesChangeProbability) {
                mutations++
                (oldGene.doubleValue() + (random.nextDouble() / 2 - 0.25)).coerceIn(0.0, 1.0)
            } else {
                oldGene.doubleValue()
            }
        }

        // mutate individual random variables
        for (randomVariableIndex in 0 until this.statesCount + 1) {
            for (index in 0 until this.statesCount) {
                if (random.nextDouble() < this.changeInRandomVariableProbability) {
                    mutations++
                    val geneToChangeIndex = randomVariableIndex * this.statesCount + index
                    val valueChange = (genes[geneToChangeIndex] - (random.nextDouble())).coerceIn(0.0, 1.0) - genes[geneToChangeIndex]

                    for (i in 0 until this.statesCount) {
                        genes[randomVariableIndex * this.statesCount + i] += when (i) {
                            index -> valueChange
                            else -> -(valueChange / (this.statesCount - 1.0))
                        }
                    }

                    break
                }
            }
        }

        // swap in individual random variables
        for (randomVariableIndex in 0 until this.statesCount + 1) {
            if (random.nextDouble() < this.swapInRandomVariableProbability) {
                val firstIndex: Int = (random.nextDouble() * this.statesCount).toInt()
                val secondIndex: Int = (random.nextDouble() * this.statesCount).toInt()

                if (firstIndex == secondIndex) continue

                mutations++

                val firstIndexValue = genes[randomVariableIndex * this.statesCount + firstIndex]
                val secondIndexValue = genes[randomVariableIndex * this.statesCount + secondIndex]

                genes[randomVariableIndex * this.statesCount + firstIndex] = secondIndexValue
                genes[randomVariableIndex * this.statesCount + secondIndex] = firstIndexValue

                break
            }
        }

        val newIndividual = Genotype.of(DoubleChromosome.of(List<DoubleGene>(genes.size) {
            DoubleGene.of(genes[it], 0.0, 1.0)
        }))

        return MutatorResult.of(Phenotype.of(newIndividual, generation), mutations)
    }

}