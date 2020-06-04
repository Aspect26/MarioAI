package cz.cuni.mff.aspect.coevolution

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.joda.time.DateTime
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class CoevolutionTimerTests {

    @BeforeEach
    fun beforeEach() {
        mockkStatic(DateTime::class)

        val tmpFile = File(".tests")
        if (tmpFile.exists() && tmpFile.isDirectory) {
            tmpFile.deleteRecursively()
        }
    }

    @Test
    fun `test correct controller evolution time is computed after one run`() {
        val timer = CoevolutionTimer()

        this.runControllerEvolutionTimer(timer, 100L, 250L)

        assertEquals(150L, timer.totalControllerEvolutionTime, "The controller evolution timer was running for 150 millis")
        assertEquals(0L, timer.totalGeneratorsEvolutionTime, "No generators evolution timer was run")
    }

    @Test
    fun `test correct controller evolution time is computed after multiple runs`() {
        val timer = CoevolutionTimer()

        this.runControllerEvolutionTimer(timer, 100L, 250L)
        this.runControllerEvolutionTimer(timer, 500L, 750L)
        this.runControllerEvolutionTimer(timer, 1500L, 2500L)

        assertEquals(150L + 250L + 1000L, timer.totalControllerEvolutionTime, "The controller evolution timer was running for 150 + 250 + 1000 millis")
        assertEquals(0L, timer.totalGeneratorsEvolutionTime, "No generators evolution timer was run")
    }

    @Test
    fun `test exception throws when stopping not started controller timer`() {
        val timer = CoevolutionTimer()
        timer.startControllerEvolution()
        timer.stopControllerEvolution()

        assertThrows<IllegalStateException> { timer.stopControllerEvolution() }
    }

    @Test
    fun `test correct generators evolution time is computed after one run`() {
        val timer = CoevolutionTimer()

        this.runGeneratorsEvolutionTimer(timer, 1100L, 2250L)

        assertEquals(1150L, timer.totalGeneratorsEvolutionTime, "The generators evolution timer was running for 1150 millis")
        assertEquals(0L, timer.totalControllerEvolutionTime, "No controller evolution timer was run")
    }

    @Test
    fun `test correct generators evolution time is computed after multiple runs`() {
        val timer = CoevolutionTimer()

        this.runGeneratorsEvolutionTimer(timer, 50L, 250L)
        this.runGeneratorsEvolutionTimer(timer, 500L, 800L)
        this.runGeneratorsEvolutionTimer(timer, 1500L, 2500L)

        assertEquals(200L + 300L + 1000L, timer.totalGeneratorsEvolutionTime, "The generators evolution timer was running for 200 + 300 + 1000 millis")
        assertEquals(0L, timer.totalControllerEvolutionTime, "No controllers evolution timer was run")
    }

    @Test
    fun `test exception throws when stopping not started generators timer`() {
        val timer = CoevolutionTimer()
        timer.startGeneratorsEvolution()
        timer.stopGeneratorsEvolution()

        assertThrows<IllegalStateException> { timer.stopGeneratorsEvolution() }
    }

    @Test
    fun `test store and load correct`() {
        val timer = CoevolutionTimer()

        this.runControllerEvolutionTimer(timer, 50L, 250L)
        this.runGeneratorsEvolutionTimer(timer, 300L, 550L)

        this.runControllerEvolutionTimer(timer, 550L, 1000L)
        this.runGeneratorsEvolutionTimer(timer, 1200L, 1500L)

        this.runControllerEvolutionTimer(timer, 2000L, 2550L)

        timer.store(".tests/timer.dat")
        val loadedTimer: CoevolutionTimer = CoevolutionTimer.loadFromFile(".tests/timer.dat")

        assertEquals(200L + 450L + 550L, loadedTimer.totalControllerEvolutionTime,
            "The controllers evolution timer was running for 200 + 450 + 550 millis")
        assertEquals(250L + 300L, loadedTimer.totalGeneratorsEvolutionTime,
            "The generators evolution timer was running for 250 + 300 millis")
    }

    private fun runControllerEvolutionTimer(timer: CoevolutionTimer, startTime: Long, endTime: Long) {
        every { DateTime.now() } returns DateTime(startTime)
        timer.startControllerEvolution()
        every { DateTime.now() } returns DateTime(endTime)
        timer.stopControllerEvolution()
    }

    private fun runGeneratorsEvolutionTimer(timer: CoevolutionTimer, startTime: Long, endTime: Long) {
        every { DateTime.now() } returns DateTime(startTime)
        timer.startGeneratorsEvolution()
        every { DateTime.now() } returns DateTime(endTime)
        timer.stopGeneratorsEvolution()
    }

    companion object {

        @AfterAll
        @JvmStatic
        fun afterAll() {
            unmockkAll()
        }

    }
}