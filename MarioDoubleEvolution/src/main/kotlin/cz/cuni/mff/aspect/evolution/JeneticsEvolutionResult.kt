package cz.cuni.mff.aspect.evolution

import io.jenetics.DoubleGene
import io.jenetics.Genotype

/** Data class representing result of an evolution via [ChartedJeneticsEvolution]. */
data class JeneticsEvolutionResult<T>(val bestIndividual: T, val lastGenerationPopulation: List<Genotype<DoubleGene>>)
