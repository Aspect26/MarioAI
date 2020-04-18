package cz.cuni.mff.aspect.evolution.levels.chunks

import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.chunks.*
import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunkWithHeight
import cz.cuni.mff.aspect.evolution.levels.chunks.metadata.ChunksLevelMetadata
import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.MarioLevel
import java.util.*


class PCLevelGenerator(
    private val probabilities: List<Double> = List(DEFAULT_CHUNKS_COUNT + DEFAULT_CHUNKS_COUNT * DEFAULT_CHUNKS_COUNT + ENEMY_TYPES_COUNT + 1) {
        when (it) {
            in 0 until DEFAULT_CHUNKS_COUNT -> 1.0 / DEFAULT_CHUNKS_COUNT
            in DEFAULT_CHUNKS_COUNT until DEFAULT_CHUNKS_COUNT + DEFAULT_CHUNKS_COUNT * DEFAULT_CHUNKS_COUNT -> 1.0 / DEFAULT_CHUNKS_COUNT
            DEFAULT_CHUNKS_COUNT + DEFAULT_CHUNKS_COUNT * DEFAULT_CHUNKS_COUNT + ENEMY_TYPES_COUNT -> 0.3
            else -> 0.02
        }
    },
    private val chunksInLevelCount: Int = DEFAULT_CHUNKS_COUNT
) : LevelGenerator {

    private lateinit var random: Random
    private lateinit var _lastChunksMetadata: ChunksLevelMetadata

    override fun generate(): MarioLevel {
        this.random = Random()
        return this.generate(
            defaultChunks,
            this.probabilities.subList(0, defaultChunks.size),
            this.probabilities.subList(
                defaultChunks.size,
                defaultChunks.size + defaultChunks.size * defaultChunks.size
            ),
            this.chunksInLevelCount,
            startChunk,
            endChunk,
            this.probabilities.subList(
                defaultChunks.size + defaultChunks.size * defaultChunks.size,
                defaultChunks.size + defaultChunks.size * defaultChunks.size + Companion.ENEMY_TYPES_COUNT
            ),
            this.probabilities[defaultChunks.size + defaultChunks.size * defaultChunks.size + Companion.ENEMY_TYPES_COUNT]
        )
    }

    val lastChunksMetadata: ChunksLevelMetadata get() = this._lastChunksMetadata

    private fun generate(chunks: Array<MarioLevelChunk>, startingProbabilities: List<Double>, transitionProbabilities: List<Double>,
                         chunksInLevelCount: Int, startChunk: MarioLevelChunk, endChunk: MarioLevelChunk, entityProbabilities: List<Double>,
                         heightChangeProbability: Double): MarioLevel {
        if (transitionProbabilities.size != chunks.size * chunks.size) {
            throw IllegalArgumentException("The length of transition probabilities arrays must equal to the length of chunks array squared")
        }

        if (startingProbabilities.size != chunks.size) {
            throw IllegalArgumentException("The length of starting probabilities arrays must equal to the length of chunks array")
        }

        if (entityProbabilities.size != ENEMY_TYPES_COUNT) {
            throw IllegalArgumentException("The length of entities probabilities arrays must equal to $ENEMY_TYPES_COUNT")
        }

        val metadata: ChunksLevelMetadata = this.createMetadata(chunks, startingProbabilities, transitionProbabilities, chunksInLevelCount,
            startChunk, endChunk, heightChangeProbability, entityProbabilities)

        this._lastChunksMetadata = metadata

        return metadata.createLevel()
    }

    private fun createMetadata(availableChunks: Array<MarioLevelChunk>, startingProbabilities: List<Double>, transitionProbabilities: List<Double>,
                               chunksInLevelCount: Int, startChunk: MarioLevelChunk, endChunk: MarioLevelChunk,
                               heightChangeProbability: Double, entityProbabilities: List<Double>): ChunksLevelMetadata {
        val chunks = this.createChunks(availableChunks, startingProbabilities, transitionProbabilities, chunksInLevelCount, 
            startChunk, endChunk, heightChangeProbability)
        val tiles = ChunksLevelMetadata(
            chunks,
            emptyArray()
        ).createTiles()
        val entities = this.createEntities(tiles, entityProbabilities)
        
        return ChunksLevelMetadata(
            chunks,
            entities
        )
    }

    private fun createChunks(chunks: Array<MarioLevelChunk>, startingProbabilities: List<Double>, transitionProbabilities: List<Double>,
                               chunksInLevelCount: Int, startChunk: MarioLevelChunk, endChunk: MarioLevelChunk,
                               heightChangeProbability: Double): List<ChunkWithHeight> {
        val chunksWithHeight: MutableList<ChunkWithHeight> = mutableListOf()

        // starting chunk
        var currentLevel = 10 + this.randomInt(-2, 2)
        chunksWithHeight.add(ChunkWithHeight(startChunk.copySelf(), currentLevel))

        // first inner chunk
        if (this.random.nextDouble() < heightChangeProbability)
            currentLevel = (currentLevel + this.nextHeightChange).coerceIn(5, 14)
        var currentChunkIndex = this.randomChoice(startingProbabilities)
        chunksWithHeight.add(ChunkWithHeight(chunks[currentChunkIndex].copySelf(), currentLevel))

        // inner chunks
        for (chunkNumber in 1 until chunksInLevelCount) {
            if (this.random.nextDouble() < heightChangeProbability)
                currentLevel = (currentLevel + this.nextHeightChange).coerceIn(5, 14)

            val tpStartIndex = chunks.size * currentChunkIndex
            val tpEndIndex = chunks.size * (currentChunkIndex + 1)

            val nextChunkProbabilities = transitionProbabilities.subList(tpStartIndex, tpEndIndex)
            currentChunkIndex = this.randomChoice(nextChunkProbabilities)

            chunksWithHeight.add(ChunkWithHeight(chunks[currentChunkIndex].copySelf(),currentLevel))
        }

        // last chunk
        chunksWithHeight.add(ChunkWithHeight(endChunk.copySelf(), currentLevel))

        return chunksWithHeight
    }

    private fun createEntities(tiles: Array<ByteArray>, entityProbabilities: List<Double>): Array<Array<Int>> {
        val entities: Array<Array<Int>> = Array(tiles.size) { column ->
            Array(tiles[column].size) { row ->
                if (tiles[column][row] != Tiles.PIPE_TOP_LEFT) Entities.NOTHING else Entities.Flower.NORMAL
            }
        }

        for (column in SAFE_ZONE_LENGTH until tiles.size - Companion.SAFE_ZONE_LENGTH) {
            val firstEmpty = tiles[column].size - tiles[column].reversedArray().indexOfFirst { it == Tiles.NOTHING }
            if (firstEmpty <= 0 || firstEmpty >= tiles[column].size) continue

            val entity = when (this.randomChoice(entityProbabilities)) {
                0 -> Entities.Goomba.NORMAL
                1 -> Entities.Koopa.GREEN
                2 -> Entities.Koopa.RED
                3 -> Entities.Koopa.GREEN_WINGED
                4 -> Entities.Spiky.NORMAL
                else -> Entities.NOTHING
            }
            entities[column][firstEmpty - 1] = entity
        }

        val princessColumn = tiles.size - 3
        val firstEmpty = tiles[princessColumn].size - tiles[princessColumn].reversedArray().indexOfFirst { it == Tiles.NOTHING }
        entities[princessColumn][firstEmpty - 1] = Entities.PrincessPeach.NORMAL

        return entities
    }

    private val nextHeightChange: Int get() = this.randomInt(-4, 4)

    private fun randomInt(lowerBound: Int, higherBound: Int): Int {
        val range = higherBound - lowerBound + 1
        return (this.random.nextFloat() * range).toInt() + lowerBound
    }

    private fun randomChoice(probabilities: List<Double>): Int {
        var cumulativeProbability = 0.0
        val probability = this.random.nextDouble()

        for (probabilityIndex in probabilities.indices) {
            if (probability < cumulativeProbability + probabilities[probabilityIndex]) {
                return probabilityIndex
            } else {
                cumulativeProbability += probabilities[probabilityIndex]
            }
        }

        return -1
    }

    companion object {
        private val defaultChunks = arrayOf(
            PathChunk(3), PathChunk(4), PathChunk(5), PathChunk(6),
            HoleChunk(2), HoleChunk(3), HoleChunk(4),
            SinglePlatformChunk(1, Tiles.BRICK), SinglePlatformChunk(3, Tiles.BRICK), SinglePlatformChunk(5, Tiles.BRICK),
            SinglePlatformChunk(1, Tiles.QM_WITH_COIN), SinglePlatformChunk(3, Tiles.QM_WITH_COIN), SinglePlatformChunk(5, Tiles.QM_WITH_COIN),
            PipeChunk(2), PipeChunk(3), PipeChunk(4),
            BulletBillChunk(1), BulletBillChunk(2), BulletBillChunk(3), BulletBillChunk(4),
            StairChunk(2), StairChunk(3), StairChunk(4),
            DoublePlatformChunk(5, Tiles.BRICK, Tiles.BRICK), DoublePlatformChunk(5, Tiles.QM_WITH_COIN, Tiles.QM_WITH_COIN),
            DoublePlatformChunk(5, Tiles.BRICK, Tiles.QM_WITH_COIN))
        private val startChunk: MarioLevelChunk = PathChunk(8)
        private val endChunk: MarioLevelChunk = PathChunk(8)
        
        private const val SAFE_ZONE_LENGTH: Int = 7
        const val ENEMY_TYPES_COUNT = 5
        val DEFAULT_CHUNKS_COUNT: Int = defaultChunks.size
        val DEFAULT_CHUNK_TYPES_COUNT: Int = defaultChunks.map { it.name }.distinct().size

        fun createSimplest(): PCLevelGenerator = PCLevelGenerator(
            List(DEFAULT_CHUNKS_COUNT + DEFAULT_CHUNKS_COUNT * DEFAULT_CHUNKS_COUNT + ENEMY_TYPES_COUNT + 1) {
                when (it) {
                    in 0 until DEFAULT_CHUNKS_COUNT + DEFAULT_CHUNKS_COUNT * DEFAULT_CHUNKS_COUNT -> if (it % DEFAULT_CHUNKS_COUNT == 0) 1.0 else 0.0
                    else -> 0.0
                }
            },
            DEFAULT_CHUNKS_COUNT
        )
    }

}
