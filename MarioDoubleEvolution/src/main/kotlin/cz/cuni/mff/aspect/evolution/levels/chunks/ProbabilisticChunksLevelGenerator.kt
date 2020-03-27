package cz.cuni.mff.aspect.evolution.levels.chunks

import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.chunks.*
import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.DirectMarioLevel
import cz.cuni.mff.aspect.mario.level.MarioLevel
import java.util.*


class ProbabilisticChunksLevelGenerator(
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

    private val DEFAULT_CHUNKS = arrayOf(
        Path3Chunk(), Path4Chunk(), Path5Chunk(), Path6Chunk(),
        Hole2Chunk(), Hole3Chunk(), Hole4Chunk(),
        SingleBricks1Platform(), SingleBricks3Platform(), SingleBricks5Platform(),
        SingleQM1Platform(), SingleQM3Platform(), SingleQM5Platform(),
        Pipe2Chunk(), Pipe3Chunk(), Pipe4Chunk(),
        BulletBill1Chunk(), BulletBill1Chunk(), BulletBill2Chunk(), BulletBill3Chunk(),
        Stair2Chunk(), Stair3Chunk(), Stair4Chunk(), Stair5Chunk(),
        DoubleBrickPlatforms5Chunk(), DoubleQMPlatforms5Chunk(), BrickAndQMPlatforms5Chunk())
    private val START_CHUNK: MarioLevelChunk = PathChunk(8)
    private val END_CHUNK: MarioLevelChunk = PathChunk(8)

    private lateinit var random: Random
    private lateinit var _lastChunkNames: Array<String>

    override fun generate(): MarioLevel {
        this.random = Random()
        return this.generate(
            this.DEFAULT_CHUNKS,
            this.probabilities.subList(0, this.DEFAULT_CHUNKS.size),
            this.probabilities.subList(
                this.DEFAULT_CHUNKS.size,
                this.DEFAULT_CHUNKS.size + this.DEFAULT_CHUNKS.size * this.DEFAULT_CHUNKS.size
            ),
            this.chunksInLevelCount,
            this.START_CHUNK,
            this.END_CHUNK,
            this.probabilities.subList(
                this.DEFAULT_CHUNKS.size + this.DEFAULT_CHUNKS.size * this.DEFAULT_CHUNKS.size,
                this.DEFAULT_CHUNKS.size + this.DEFAULT_CHUNKS.size * this.DEFAULT_CHUNKS.size + Companion.ENEMY_TYPES_COUNT
            ),
            this.probabilities[this.DEFAULT_CHUNKS.size + this.DEFAULT_CHUNKS.size * this.DEFAULT_CHUNKS.size + Companion.ENEMY_TYPES_COUNT]
        )
    }

    val lastChunkNames: Array<String> get() = this._lastChunkNames

    private fun generate(chunks: Array<MarioLevelChunk>, startingProbabilities: List<Double>, transitionProbabilities: List<Double>,
               chunksInLevelCount: Int, startChunk: MarioLevelChunk, endChunk: MarioLevelChunk, entitiesProbabilities: List<Double>,
               heightChangeProbability: Double): MarioLevel {
        if (transitionProbabilities.size != chunks.size * chunks.size) {
            throw IllegalArgumentException("The length of transition probabilities arrays must equal to the length of chunks array squared")
        }

        if (startingProbabilities.size != chunks.size) {
            throw IllegalArgumentException("The length of starting probabilities arrays must equal to the length of chunks array")
        }

        if (entitiesProbabilities.size != ENEMY_TYPES_COUNT) {
            throw IllegalArgumentException("The length of entities probabilities arrays must equal to $ENEMY_TYPES_COUNT")
        }

        val (tiles, chunkNames) = this.createTiles(chunks, startingProbabilities, transitionProbabilities, chunksInLevelCount,
            startChunk, endChunk, heightChangeProbability)
        this._lastChunkNames = chunkNames

        val entities: Array<Array<Int>> = this.createEntities(tiles, entitiesProbabilities)

        return DirectMarioLevel(tiles, entities)
    }

    private fun createTiles(chunks: Array<MarioLevelChunk>, startingProbabilities: List<Double>, transitionProbabilities: List<Double>,
                            chunksInLevelCount: Int, startChunk: MarioLevelChunk, endChunk: MarioLevelChunk,
                            heightChangeProbability: Double): Pair<Array<ByteArray>, Array<String>> {
        val tilesList: MutableList<ByteArray> = mutableListOf()
        val chunksArray: Array<String> = Array(chunksInLevelCount) { "" }

        var currentHeight = 10
        tilesList.addAll(startChunk.generate(currentHeight))

        var currentChunkIndex = this.randomChoice(startingProbabilities)
        tilesList.addAll(chunks[currentChunkIndex].generate(currentHeight))
        chunksArray[0] = chunks[currentChunkIndex].toString()

        for (chunkNumber in 1 until chunksInLevelCount) {
            if (this.random.nextDouble() < heightChangeProbability)
                currentHeight = (currentHeight + this.nextHeightChange).coerceIn(5, 14)

            val tpStartIndex = chunks.size * currentChunkIndex
            val tpEndIndex = chunks.size * (currentChunkIndex + 1)

            val nextChunkProbabilities = transitionProbabilities.subList(tpStartIndex, tpEndIndex)
            currentChunkIndex = this.randomChoice(nextChunkProbabilities)

            tilesList.addAll(chunks[currentChunkIndex].generate(currentHeight))
            chunksArray[chunkNumber] = chunks[currentChunkIndex].toString()
        }

        tilesList.addAll(endChunk.generate(currentHeight))

        return Pair(tilesList.toTypedArray(), chunksArray)
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
        private const val SAFE_ZONE_LENGTH: Int = 7
        const val ENEMY_TYPES_COUNT = 5
        const val DEFAULT_CHUNKS_COUNT: Int = 27 // TODO: lol constant
    }

}
