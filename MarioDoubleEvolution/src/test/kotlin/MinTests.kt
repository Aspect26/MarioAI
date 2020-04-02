import cz.cuni.mff.aspect.utils.min
import org.junit.Assert
import org.junit.Test

class MinTests {

    @Test(expected = IllegalArgumentException::class)
    fun `empty arguments`() {
        min<Float>(emptyList())
    }

    @Test
    fun `single argument`() {
        Assert.assertEquals(5, min<Int>(listOf(5)))
        Assert.assertEquals(42f, min<Float>(listOf(42f)))
        Assert.assertEquals(66.0, min<Double>(listOf(66.0)), 0.0000001)
    }

    @Test
    fun `two arguments`() {
        Assert.assertEquals(5, min<Int>(listOf(5, 6)))
        Assert.assertEquals(5, min<Int>(listOf(20, 5)))
    }

    @Test
    fun `more arguments`() {
        Assert.assertEquals(5, min<Int>(listOf(5, 6, 7)))
        Assert.assertEquals(5, min<Int>(listOf(6, 5, 7)))
        Assert.assertEquals(5, min<Int>(listOf(7, 6, 5)))
    }

    @Test
    fun `more arguments 2`() {
        Assert.assertEquals(0, min<Int>(listOf(8, 5, 6, 2, 1, 4, 3, 0, 2, 1)))
    }
}