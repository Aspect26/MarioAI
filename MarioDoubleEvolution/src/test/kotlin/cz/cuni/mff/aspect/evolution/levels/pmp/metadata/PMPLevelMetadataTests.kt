package cz.cuni.mff.aspect.evolution.levels.pmp.metadata

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PMPLevelMetadataTests {

    @Test
    fun `is hole at`() {
        val metadata = givenMetadataWithHoles(
            intArrayOf(0, 0, 3, 0, 0, 0, 1, 0, 0)
        )

        assertFalse(metadata.isHoleAt(0))
        assertFalse(metadata.isHoleAt(1))
        assertTrue(metadata.isHoleAt(2))
        assertTrue(metadata.isHoleAt(3))
        assertTrue(metadata.isHoleAt(4))
        assertFalse(metadata.isHoleAt(5))
        assertTrue(metadata.isHoleAt(6))
        assertFalse(metadata.isHoleAt(7))
        assertFalse(metadata.isHoleAt(8))
    }

    @Test
    fun `is obstacle at - ground`() {
        val metadata = givenMetadataWithGroundHeight(
            intArrayOf(5, 5, 6, 6, 7)
        )

        assertTrue(metadata.isObstacleAt(0, 5))
        assertFalse(metadata.isObstacleAt(0, 6))

        assertTrue(metadata.isObstacleAt(1, 5))
        assertFalse(metadata.isObstacleAt(1, 6))

        assertTrue(metadata.isObstacleAt(2, 5))
        assertTrue(metadata.isObstacleAt(2, 6))
        assertFalse(metadata.isObstacleAt(2, 7))

        assertTrue(metadata.isObstacleAt(3, 5))
        assertTrue(metadata.isObstacleAt(3, 6))
        assertFalse(metadata.isObstacleAt(3, 7))

        assertTrue(metadata.isObstacleAt(4, 5))
        assertTrue(metadata.isObstacleAt(4, 6))
        assertTrue(metadata.isObstacleAt(4, 7))
        assertFalse(metadata.isObstacleAt(4, 8))
    }

    @Test
    fun `is obstacles at - pipes`() {
        val metadata = givenMetadataWithPipes(
            intArrayOf(5, 5, 5, 5, 5, 5),
            intArrayOf(0, 0, 2, 0, 0, 0)
        )

        assertFalse(metadata.isObstacleAt(1, 6))
        assertTrue(metadata.isObstacleAt(2, 6))
        assertTrue(metadata.isObstacleAt(3, 6))
        assertFalse(metadata.isObstacleAt(4, 6))

        assertFalse(metadata.isObstacleAt(1, 7))
        assertTrue(metadata.isObstacleAt(2, 7))
        assertTrue(metadata.isObstacleAt(3, 7))
        assertFalse(metadata.isObstacleAt(4, 7))

        assertFalse(metadata.isObstacleAt(1, 8))
        assertFalse(metadata.isObstacleAt(2, 8))
        assertFalse(metadata.isObstacleAt(3, 8))
        assertFalse(metadata.isObstacleAt(4, 8))
    }

    @Test
    fun `is obstacle at - bills`() {
        val metadata = givenMetadataWithBills(
            intArrayOf(7, 7, 7),
            intArrayOf(0, 3, 0)
        )

        assertFalse(metadata.isObstacleAt(0, 8))
        assertTrue(metadata.isObstacleAt(1, 8))
        assertFalse(metadata.isObstacleAt(2, 8))

        assertFalse(metadata.isObstacleAt(0, 9))
        assertTrue(metadata.isObstacleAt(1, 9))
        assertFalse(metadata.isObstacleAt(2, 9))

        assertFalse(metadata.isObstacleAt(0, 10))
        assertTrue(metadata.isObstacleAt(1, 10))
        assertFalse(metadata.isObstacleAt(2, 10))

        assertFalse(metadata.isObstacleAt(0, 11))
        assertFalse(metadata.isObstacleAt(1, 11))
        assertFalse(metadata.isObstacleAt(2, 11))
    }

    @Test
    fun `is obstacle at - stone columns`() {
        val metadata = givenMetadataWithStoneColumns(
            intArrayOf(7, 7, 7),
            intArrayOf(0, 2, 0)
        )

        assertFalse(metadata.isObstacleAt(0, 8))
        assertTrue(metadata.isObstacleAt(1, 8))
        assertFalse(metadata.isObstacleAt(2, 8))

        assertFalse(metadata.isObstacleAt(0, 9))
        assertTrue(metadata.isObstacleAt(1, 9))
        assertFalse(metadata.isObstacleAt(2, 9))

        assertFalse(metadata.isObstacleAt(0, 10))
        assertFalse(metadata.isObstacleAt(1, 10))
        assertFalse(metadata.isObstacleAt(2, 10))
    }

    @Test
    fun `is obstacle at - platform`() {
        val metadata: PMPLevelMetadata = givenMetadataWithBoxPlatforms(
            arrayOf(
                BoxPlatform(0, 7, listOf(), BoxPlatformType.BRICKS),
                BoxPlatform(2, 7, listOf(), BoxPlatformType.BRICKS)
            ),
            5
        )

        assertFalse(metadata.isObstacleAt(0, 6))
        assertFalse(metadata.isObstacleAt(1, 6))
        assertFalse(metadata.isObstacleAt(2, 6))
        assertFalse(metadata.isObstacleAt(3, 6))

        assertFalse(metadata.isObstacleAt(0, 7))
        assertTrue(metadata.isObstacleAt(1, 7))
        assertTrue(metadata.isObstacleAt(2, 7))
        assertFalse(metadata.isObstacleAt(3, 7))

        assertFalse(metadata.isObstacleAt(0, 8))
        assertFalse(metadata.isObstacleAt(1, 8))
        assertFalse(metadata.isObstacleAt(2, 8))
        assertFalse(metadata.isObstacleAt(3, 8))
    }

    @Test
    fun `horizontal ray until obstacle`() {
        val metadata = givenMetadataWithPipes(
            intArrayOf(5, 5, 5, 5, 5, 5, 5, 5),
            intArrayOf(0, 0, 0, 0, 0, 3, 0, 0)
        )

        assertEquals(5, metadata.horizontalRayUntilObstacle(0, 6))
        assertEquals(4, metadata.horizontalRayUntilObstacle(1, 6))
        assertEquals(7, metadata.horizontalRayUntilObstacle(1, 10))
        assertEquals(1, metadata.horizontalRayUntilObstacle(4, 7))
        assertEquals(0, metadata.horizontalRayUntilObstacle(5, 7))
        assertEquals(0, metadata.horizontalRayUntilObstacle(6, 7))
    }

    private fun givenMetadataWithHoles(holes: IntArray): PMPLevelMetadata = PMPLevelMetadata(
        15, intArrayOf(), intArrayOf(), holes, intArrayOf(), intArrayOf(), arrayOf(), intArrayOf()
    )

    private fun givenMetadataWithGroundHeight(groundHeight: IntArray): PMPLevelMetadata {
        val emptyData = IntArray(groundHeight.size) { 0 }
        return PMPLevelMetadata(
            15, groundHeight, emptyData, emptyData, emptyData, emptyData, arrayOf(), emptyData
        )
    }

    private fun givenMetadataWithPipes(groundHeight: IntArray, pipes: IntArray): PMPLevelMetadata {
        val emptyData = IntArray(pipes.size) { 0 }
        return PMPLevelMetadata(
            15, groundHeight, emptyData, emptyData, pipes, emptyData, arrayOf(), emptyData
        )
    }

    private fun givenMetadataWithBills(groundHeight: IntArray, bills: IntArray): PMPLevelMetadata {
        val emptyData = IntArray(bills.size) { 0 }
        return PMPLevelMetadata(
            15, groundHeight, emptyData, emptyData, emptyData, bills, arrayOf(), emptyData
        )
    }

    private fun givenMetadataWithStoneColumns(groundHeight: IntArray, stoneColumns: IntArray): PMPLevelMetadata {
        val emptyData = IntArray(stoneColumns.size) { 0 }
        return PMPLevelMetadata(
            15, groundHeight, emptyData, emptyData, emptyData, emptyData, arrayOf(), stoneColumns
        )
    }

    private fun givenMetadataWithBoxPlatforms(boxPlatforms: Array<BoxPlatform>, levelLength: Int): PMPLevelMetadata {
        val emptyData = IntArray(levelLength) { 0 }
        return PMPLevelMetadata(
            15, emptyData, emptyData, emptyData, emptyData, emptyData, boxPlatforms, emptyData
        )
    }

}