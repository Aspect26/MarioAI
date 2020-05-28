package cz.cuni.mff.aspect.evolution.levels.evaluators

import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.level.DirectMarioLevel
import cz.cuni.mff.aspect.mario.level.MarioLevel
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class WinRatioEvaluatorTests {

    @Test
    fun `test exactly what expected`() {
        val evaluator = WinRatioEvaluator(0.5f, 1f)
        val (levels, gameStatistics) = this.givenWonLevelsOf10(5)

        val result = evaluator(levels, gameStatistics)

        assertEquals(1f, result, "The expected ratio and actual ratio are 0.5 so the evaluation value should be 1")
    }

    @Test
    fun `test exactly what expected 2`() {
        val evaluator = WinRatioEvaluator(0.8f, 1f)
        val (levels, gameStatistics) = this.givenWonLevelsOf10(8)

        val result = evaluator(levels, gameStatistics)

        assertEquals(1f, result, "The expected ratio and actual ratio are 0.5 so the evaluation value should be 1")
    }

    @Test
    fun `test low difference`() {
        val evaluator = WinRatioEvaluator(0.8f, 1f)
        val (levels, gameStatistics) = this.givenWonLevelsOf10(7)

        val result = evaluator(levels, gameStatistics)

        assertNotEquals(1f, result)
        assertNotEquals(0f, result)
        assertTrue(result > 0.85f)
    }

    @Test
    fun `test high difference`() {
        val evaluator = WinRatioEvaluator(0.8f, 1f)
        val (levels, gameStatistics) = this.givenWonLevelsOf10(1)

        val result = evaluator(levels, gameStatistics)

        assertNotEquals(1f, result)
        assertNotEquals(0f, result)
        assertTrue(result < 0.15f)
    }

    @Test
    fun `test minimum value`() {
        val evaluator = WinRatioEvaluator(0.3f, 1f)
        val (levels, gameStatistics) = this.givenWonLevelsOf10(10)

        val result = evaluator(levels, gameStatistics)

        assertEquals(0f, result, "The expected ratio and actual ratio differ by the highest possible amount, so the result should be 0")
    }

    @Test
    fun `test minimum value 2`() {
        val evaluator = WinRatioEvaluator(0.7f, 1f)
        val (levels, gameStatistics) = this.givenWonLevelsOf10(0)

        val result = evaluator(levels, gameStatistics)

        assertEquals(0f, result, "The expected ratio and actual ratio differ by the highest possible amount, so the result should be 0")
    }

    private fun givenWonLevelsOf10(wonCount: Int): Pair<List<MarioLevel>, List<GameStatistics>> {
        val levels = List(10) { DirectMarioLevel(arrayOf(), arrayOf()) }
        val gameStatistics = List(10) { GameStatistics(0f, 0, 0, 0, 0, false, it < wonCount)}

        return Pair(levels, gameStatistics)
    }
}