package cz.cuni.mff.aspect.coevolution

import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorEvolution
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.visualisation.charts.evolution.EvolutionLineChart
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class CoevolutionStorageTests {

    private val mockController = mockk<MarioController>()
    private val mockGenerator = mockk<LevelGenerator>()
    private val mockLevelGeneratorEvolution = mockk<LevelGeneratorEvolution>()
    private val mockControllerEvolution = mockk<ControllerEvolution>()
    private val mockEvolutionChart = EvolutionLineChart()

    @BeforeEach
    fun beforeEach() {
        every { mockControllerEvolution.chart } returns mockEvolutionChart
        every { mockLevelGeneratorEvolution.chart } returns mockEvolutionChart
    }

    private val mockSettings = CoevolutionSettings(
        controllerEvolution = mockControllerEvolution,
        generatorEvolution = mockLevelGeneratorEvolution,
        initialController = mockController,
        initialLevelGenerator = mockGenerator,
        generations = 10,
        repeatGeneratorsCount = 5,
        storagePath = ".tests/tmp/coev-storage"
    )

    @AfterEach
    fun afterEach() {
        val tmpFile = File(".tests")
        if (tmpFile.exists() && tmpFile.isDirectory) {
            tmpFile.deleteRecursively()
        }
    }

    @Test
    fun `test store and load controller doesn't crash`() {
        val generation = 5

        CoevolutionStorage.storeState(mockSettings, generation, mockController, mockGenerator)
        CoevolutionStorage.loadController(mockSettings, generation)
    }

    @Test
    fun `test store and load level generator doesn't crash`() {
        val generation = 7

        CoevolutionStorage.storeState(mockSettings, generation, mockController, mockGenerator)
        CoevolutionStorage.loadLevelGenerator(mockSettings, generation)
    }

    @Test
    fun `test correct last stored generation number is computed`() {
        CoevolutionStorage.storeState(mockSettings, 1, mockController, mockGenerator)
        CoevolutionStorage.storeState(mockSettings, 2, mockController, mockGenerator)
        CoevolutionStorage.storeState(mockSettings, 3, mockController, mockGenerator)

        val actualResult = CoevolutionStorage.getLastStoredCoevolutionGenerationNumber(mockSettings)

        assertEquals(3, actualResult, "There were 3 fully finished generations of coevolution stored.")
    }

}