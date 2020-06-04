package cz.cuni.mff.aspect.coevolution

import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.HiddenLayerControllerNetwork
import cz.cuni.mff.aspect.visualisation.charts.evolution.EvolutionLineChart
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.random.Random

class CoevolutionStorageTests {

    private val random: Random = Random(26270)
    private val mockController = mockk<MarioController>()
    private val mockGenerator = mockk<LevelGenerator>()
    private val mockLevelGeneratorEvolution = mockk<LevelGeneratorEvolution<LevelGenerator>>()
    private val mockControllerEvolution = mockk<ControllerEvolution>()
    private val mockCoevolutionTimer = mockk<CoevolutionTimer>()
    private val mockLastGeneratorsPopulation = List(10) {
        PMPLevelGenerator(DoubleArray(PMPLevelGenerator.PROBABILITIES_COUNT) { 0.1 }, 50)
    }
    private val mockLastControllersPopulation = List<MarioController>(10) {
        val network = HiddenLayerControllerNetwork(NetworkSettings(7, 7, 2, -2, 5, false, false))
        network.setNetworkWeights(DoubleArray(network.weightsCount) { random.nextDouble() })
        SimpleANNController(network)
    }
    private val mockEvolutionChart = EvolutionLineChart()

    private val mockSettings = CoevolutionSettings(
        controllerEvolution = mockControllerEvolution,
        generatorEvolution = mockLevelGeneratorEvolution,
        initialController = mockController,
        initialLevelGenerator = mockGenerator,
        generations = 10,
        repeatGeneratorsCount = 5,
        storagePath = ".tests/tmp/coev-storage"
    )

    @BeforeEach
    fun beforeEach() {
        mockkObject(CoevolutionTimer)

        every { mockControllerEvolution.chart } returns mockEvolutionChart
        every { mockLevelGeneratorEvolution.chart } returns mockEvolutionChart
        every { mockCoevolutionTimer.store(any()) } returns Unit
        every { CoevolutionTimer.loadFromFile(any()) } returns mockCoevolutionTimer
    }

    @AfterEach
    fun afterEach() {
        val tmpFile = File(".tests")
        if (tmpFile.exists() && tmpFile.isDirectory) {
            tmpFile.deleteRecursively()
        }
    }

    @Test
    fun `test store and load doesn't crash`() {
        val generation = 5

        (1..generation).forEach { CoevolutionStorage.storeState(mockSettings, it, mockController, mockGenerator,
            mockLastControllersPopulation, mockLastGeneratorsPopulation, mockCoevolutionTimer) }
        CoevolutionStorage.loadState(mockSettings)
    }

    @Test
    fun `test correct last stored generation number is computed`() {
        CoevolutionStorage.storeState(mockSettings, 1, mockController, mockGenerator,
            mockLastControllersPopulation, mockLastGeneratorsPopulation, mockCoevolutionTimer)
        CoevolutionStorage.storeState(mockSettings, 2, mockController, mockGenerator,
            mockLastControllersPopulation, mockLastGeneratorsPopulation, mockCoevolutionTimer)
        CoevolutionStorage.storeState(mockSettings, 3, mockController, mockGenerator,
            mockLastControllersPopulation, mockLastGeneratorsPopulation, mockCoevolutionTimer)

        val loadedState = CoevolutionStorage.loadState(mockSettings)

        assertEquals(3, loadedState.lastFinishedGeneration, "There were 3 fully finished generations of coevolution stored.")
    }

    @Test
    fun `test correct last generators population is stored and loaded`() {
        val random = Random(26270)
        val populationSize = 10

        val generatorsProbabilities = List(populationSize) { DoubleArray(PMPLevelGenerator.PROBABILITIES_COUNT) { random.nextDouble() } }
        val actualGeneratorsPopulation = List(10) { PMPLevelGenerator(generatorsProbabilities[it], 50) }

        CoevolutionStorage.storeState(mockSettings, 3, mockController, mockGenerator,
            mockLastControllersPopulation, actualGeneratorsPopulation, mockCoevolutionTimer)
        val loadedState = CoevolutionStorage.loadState(mockSettings)

        val loadedGeneratorsPopulation = loadedState.latestGeneratorsPopulation

        (0 until populationSize).forEach {
            val loadedGenerator = loadedGeneratorsPopulation[it]
            if (loadedGenerator !is PMPLevelGenerator) {
                throw AssertionError("Loaded generator is not of PMPLevelGenerator type")
            }
            assertGeneratorsEqual(actualGeneratorsPopulation[it], loadedGenerator, "The generators population does not equal")
        }
    }

    @Test
    fun `test correct last controllers population is stored and loaded`() {
        val random = Random(26270)
        val populationSize = 10

        val actualControllersPopulation = List(10) {
            val network = HiddenLayerControllerNetwork(NetworkSettings(7, 7, 2, -2, 5, false, false))
            network.setNetworkWeights(DoubleArray(network.weightsCount) { random.nextDouble() })
            SimpleANNController(network)
        }

        CoevolutionStorage.storeState(mockSettings, 3, mockController, mockGenerator,
            actualControllersPopulation, mockLastGeneratorsPopulation, mockCoevolutionTimer)
        val loadedState = CoevolutionStorage.loadState(mockSettings)

        val loadedControllersPopulation = loadedState.latestControllersPopulation

        (0 until populationSize).forEach {
            val loadedController = loadedControllersPopulation[it]
            if (loadedController !is SimpleANNController) {
                throw AssertionError("Loaded controller is not of SimpleANNController type")
            }
            if (loadedController.network !is HiddenLayerControllerNetwork) {
                throw AssertionError("Loaded controller's network is not of HiddenLayerControllerNetwork type")
            }

            assertNetworksEqual(actualControllersPopulation[it].network as HiddenLayerControllerNetwork,
                loadedController.network as HiddenLayerControllerNetwork, "The controllers populations are not equal")
        }
    }

    private fun assertGeneratorsEqual(expectedGenerator: PMPLevelGenerator, actualGenerator: PMPLevelGenerator, message: String) {
        if (!expectedGenerator.equalsGenerator(actualGenerator)) throw AssertionError(message)
    }

    private fun assertNetworksEqual(actual: HiddenLayerControllerNetwork, expected: HiddenLayerControllerNetwork, message: String) {
        assertEquals(actual.networkSettings, expected.networkSettings, "$message\nNetwork settings are not equal")
        assertEquals(actual.getNetworkWeights().toList(), expected.getNetworkWeights().toList(), "$message\nNetwork weights are not equal")
    }

    companion object {

        @AfterAll
        @JvmStatic
        fun afterAll() {
            unmockkAll()
        }

    }

}