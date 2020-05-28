package cz.cuni.mff.aspect.coevolution

import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorEvolution
import cz.cuni.mff.aspect.mario.controllers.MarioController
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class CoevolutionStorageTests {

    private val mockController = mockk<MarioController>()
    private val mockGenerator = mockk<LevelGenerator>()
    private val mockLevelGeneratorEvolution = mockk<LevelGeneratorEvolution>()
    private val mockControllerEvolution = mockk<ControllerEvolution>()

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

        CoevolutionStorage.storeController(mockSettings, generation, mockController)
        CoevolutionStorage.loadController(mockSettings, generation)
    }

    @Test
    fun `test store and load level generator doesn't crash`() {
        val generation = 5

        CoevolutionStorage.storeLevelGenerator(mockSettings, generation, mockGenerator)
        CoevolutionStorage.loadLevelGenerator(mockSettings, generation)
    }

    @Test
    fun `test correct last stored generation number is computed`() {
        CoevolutionStorage.storeController(mockSettings, 1, mockController)
        CoevolutionStorage.storeLevelGenerator(mockSettings, 1, mockGenerator)

        CoevolutionStorage.storeController(mockSettings, 2, mockController)
        CoevolutionStorage.storeLevelGenerator(mockSettings, 2, mockGenerator)

        CoevolutionStorage.storeController(mockSettings, 3, mockController)
        CoevolutionStorage.storeLevelGenerator(mockSettings, 3, mockGenerator)

        val actualResult = CoevolutionStorage.getLastStoredCoevolutionGenerationNumber(mockSettings)

        assertEquals(3, actualResult, "There were 3 fully finished generations of coevolution stored.")
    }

    @Test
    fun `test correct last stored generation number is computed when last lg evolution result is missing`() {
        CoevolutionStorage.storeController(mockSettings, 1, mockController)
        CoevolutionStorage.storeLevelGenerator(mockSettings, 1, mockGenerator)

        CoevolutionStorage.storeController(mockSettings, 2, mockController)
        CoevolutionStorage.storeLevelGenerator(mockSettings, 2, mockGenerator)

        CoevolutionStorage.storeController(mockSettings, 3, mockController)

        val actualResult = CoevolutionStorage.getLastStoredCoevolutionGenerationNumber(mockSettings)

        assertEquals(2, actualResult, "There were 2 fully finished generations of coevolution stored.")
    }

}