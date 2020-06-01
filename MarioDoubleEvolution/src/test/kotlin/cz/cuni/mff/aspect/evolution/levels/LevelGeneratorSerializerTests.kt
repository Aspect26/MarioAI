package cz.cuni.mff.aspect.evolution.levels

import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.mario.level.DirectMarioLevel
import cz.cuni.mff.aspect.mario.level.MarioLevel
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

class LevelGeneratorSerializerTests {

    @Test
    fun `test serialize and deserialize PMP level generator`() {
        val levelLength = 267
        val generator = PMPLevelGenerator(length = 267)
        val expectedData = generator.data

        val serializationData = LevelGeneratorSerializer.serialize(generator)
        val deserializedGenerator = LevelGeneratorSerializer.deserialize<PMPLevelGenerator>(serializationData)

        assertThat(deserializedGenerator, instanceOf(PMPLevelGenerator::class.java))
        assertEquals(expectedData.toList(), deserializedGenerator.data.toList())

        deserializedGenerator.generate()
        assertEquals(levelLength, deserializedGenerator.lastMetadata.levelLength)
    }

    @Test
    fun `test serialize and deserialize PC level generator`() {
        val chunksCount = 23
        val generator = PCLevelGenerator(chunksInLevelCount = chunksCount)
        val expectedData = generator.data

        val serializationData = LevelGeneratorSerializer.serialize(generator)
        val deserializedGenerator = LevelGeneratorSerializer.deserialize<PCLevelGenerator>(serializationData)

        assertThat(deserializedGenerator, instanceOf(PCLevelGenerator::class.java))
        assertEquals(expectedData, deserializedGenerator.data)

        deserializedGenerator.generate()
        assertEquals(chunksCount, deserializedGenerator.lastChunksMetadata.chunks.size)
    }

    @Test
    fun `test exception thrown when unknown serialization string deserializing`() {
        assertThrows<IllegalArgumentException> {
            LevelGeneratorSerializer.deserialize<PCLevelGenerator>("Crack of dawn, all is gone except the will to be, now they see what will be, blinded eyes to see")
        }
    }

    @Test
    fun `test exception thrown when unknown level generator serializing`() {
        assertThrows<IllegalArgumentException> {
            LevelGeneratorSerializer.serialize(object : LevelGenerator {
                override fun generate(): MarioLevel = DirectMarioLevel(arrayOf(), arrayOf())
            })
        }
    }
}