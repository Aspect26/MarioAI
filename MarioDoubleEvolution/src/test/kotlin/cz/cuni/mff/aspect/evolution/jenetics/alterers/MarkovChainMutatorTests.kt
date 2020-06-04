package cz.cuni.mff.aspect.evolution.jenetics.alterers

import cz.cuni.mff.aspect.utils.getDoubleValues
import io.jenetics.*
import io.jenetics.util.RandomRegistry
import io.jenetics.util.Seq
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import junit.framework.AssertionFailedError
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class MarkovChainMutatorTests {

    @BeforeEach
    fun mockAllUriInteractions() {
        // We do not want randomization in unit tests
        mockkStatic(RandomRegistry::class)
        every {
            RandomRegistry.random()
        } returns Random(26270L)
    }

    @Test
    fun `sanity check - check whether initial population does comply to markov chain rules`() {
        val statesCount = 10
        val initialPopulation = mockInitialPopulation(statesCount)

        this.assertPopulationContainsValidMarkovChains(initialPopulation, statesCount)
    }

    @Test
    fun `mutations in random variable actually do some changes`() {
        val statesCount = 10
        val mutator = MarkovChainMutator(statesCount, 0.5, 0.0, 0.0)
        val initialPopulation = mockInitialPopulation(statesCount)

        val result = mutator.alter(initialPopulation, 1)
        val firstIndividualInResult = result.population()[0].genotype().getDoubleValues()

        this.assertIsNotInitialMarkovChain(firstIndividualInResult, statesCount, "The individual is still the same after applying mutations")
    }

    @Test
    fun `swaps in random variable actually do some changes`() {
        val statesCount = 10
        val mutator = MarkovChainMutator(statesCount, 0.0, 1.0, 0.0)
        val initialPopulation = mockInitialPopulation(statesCount)

        val result = mutator.alter(initialPopulation, 1)
        val firstIndividualInResult = result.population()[0].genotype().getDoubleValues()

        this.assertIsNotInitialMarkovChain(firstIndividualInResult, statesCount, "The individual is still the same after applying mutations")
    }

    @Test
    fun `markov chain conditions are maintained after mutations in random variable`() {
        val statesCount = 10
        val mutator = MarkovChainMutator(statesCount, 0.5, 0.0, 0.0)
        val initialPopulation = mockInitialPopulation(statesCount)

        val result = mutator.alter(initialPopulation, 1)

        this.assertPopulationContainsValidMarkovChains(result.population(), statesCount)
    }

    @Test
    fun `markov chain conditions are maintained after swaps in random variable`() {
        val statesCount = 10
        val mutator = MarkovChainMutator(statesCount, 0.0, 0.5, 0.0)
        val initialPopulation = mockInitialPopulation(statesCount)

        val result = mutator.alter(initialPopulation, 1)

        this.assertPopulationContainsValidMarkovChains(result.population(), statesCount)
    }

    private fun mockInitialPopulation(statesCount: Int): Seq<Phenotype<DoubleGene, Float>> =
        Seq.of(Phenotype.of(Genotype.of(DoubleChromosome.of(List(statesCount + statesCount * statesCount) {
            if (it % statesCount == 0) DoubleGene.of(1.0, 0.0, 0.0) else DoubleGene.of(0.0, 0.0, 0.0)
        })), 1))

    private fun assertPopulationContainsValidMarkovChains(population: Seq<Phenotype<DoubleGene, Float>>, statesCount: Int) =
        population.forEach {
            this.assertIndividualIsValidMarkovChain(it.genotype(), statesCount)
        }

    private fun assertIndividualIsValidMarkovChain(individual: Genotype<DoubleGene>, statesCount: Int) =
        this.assertArrayIsValidMarkovChain(individual.getDoubleValues(), statesCount)

    private fun assertArrayIsValidMarkovChain(values: DoubleArray, statesCount: Int) {
        assertTrue(values.size >= statesCount + statesCount * statesCount,
            "Markov Chain contains a transition matrix (probability of a transition between each states pair " +
                    "and an vector of initial probabilities for each state")

        // Rows of the transition matrix actually represent random variable distributions
        for (randomVariableIndex in 0 until statesCount + 1) {
            val randomVariableDistribution = values.toList().subList(randomVariableIndex * statesCount, (randomVariableIndex + 1) * statesCount)
            val distributionSum = randomVariableDistribution.sum()

            assertEquals(1.0, distributionSum, 0.0000001,"Distribution of a random variable should sum to 1")

            randomVariableDistribution.forEach { probability ->
                assertTrue(probability >= 0.0, "Each probability in a random variable distribution must be not-negative")
            }
        }
    }

    private fun assertIsNotInitialMarkovChain(actualValues: DoubleArray, statesCount: Int, message: String = "") {
        val initialMarkovChain = DoubleArray(statesCount + statesCount * statesCount) {
            if (it % statesCount == 0) 1.0 else 0.0
        }

        assertArraysNotEqual(initialMarkovChain, actualValues, message)
    }

    private fun assertArraysNotEqual(notExpected: DoubleArray, actual: DoubleArray, message: String = "") {
        if (notExpected.size != actual.size) return

        var someValueDiffers = false
        for (index in notExpected.indices) {
            if (notExpected[index] != actual[index]) {
                someValueDiffers = true
                break
            }
        }

        if (!someValueDiffers) {
            throw AssertionFailedError("The arrays are equal!\n\n$message")
        }
    }

    companion object {

        @AfterAll
        @JvmStatic
        fun afterAll() {
            unmockkAll()
        }

    }
}