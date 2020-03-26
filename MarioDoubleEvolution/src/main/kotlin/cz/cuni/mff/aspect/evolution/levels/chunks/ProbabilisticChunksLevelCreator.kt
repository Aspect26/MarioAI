package cz.cuni.mff.aspect.evolution.levels.chunks

import cz.cuni.mff.aspect.evolution.levels.chunks.chunks.*
import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.DirectMarioLevel
import cz.cuni.mff.aspect.mario.level.MarioLevel
import java.util.*


object ProbabilisticChunksLevelCreator {

    private const val SAFE_ZONE_LENGTH: Int = 7
    private val DEFAULT_CHUNKS = arrayOf(Hole2Chunk(), Hole3Chunk(), Hole4Chunk(),
        Path3Chunk(), Path4Chunk(), Path5Chunk(), Path6Chunk(),
        SingleBricks1Platform(), SingleBricks3Platform(), SingleBricks5Platform(),
        SingleQM1Platform(), SingleQM3Platform(), SingleQM5Platform(),
        Pipe2Chunk(), Pipe3Chunk(), Pipe4Chunk(),
        BulletBill1Chunk(), BulletBill1Chunk(), BulletBill2Chunk(), BulletBill3Chunk(),
        Stair2Chunk(), Stair3Chunk(), Stair4Chunk(), Stair5Chunk(),
        DoubleBrickPlatforms5Chunk(), DoubleQMPlatforms5Chunk(), BrickAndQMPlatforms5Chunk())
    private val DEFAULT_CHUNKS_SMALL = arrayOf(
        Hole2Chunk(),
        Path6Chunk(),
        SingleBricks3Platform(),
        Pipe4Chunk())
    private val DEFAULT_START_CHUNK: MarioLevelChunk = PathChunk(8)
    private val DEFAULT_END_CHUNK: MarioLevelChunk = PathChunk(8)

    private val random = Random()

    val DEFAULT_CHUNKS_COUNT: Int = this.DEFAULT_CHUNKS.size
    const val ENEMY_TYPES_COUNT = 5

    fun createDefault(): MarioLevel =
        this.create(
            this.DEFAULT_CHUNKS,
            List(this.DEFAULT_CHUNKS.size) { 1.0 / this.DEFAULT_CHUNKS.size },
            List(this.DEFAULT_CHUNKS.size * this.DEFAULT_CHUNKS.size) { 1.0 / this.DEFAULT_CHUNKS.size },
            35,
            this.DEFAULT_START_CHUNK,
            this.DEFAULT_END_CHUNK,
            List(5) { 0.05 },
            0.5
        )

    fun createFromDefaultChunks(probabilities: List<Double>, chunksInLevelCount: Int): MarioLevel {
//        this.printProbabilities(probabilities)
        return this.create(
            this.DEFAULT_CHUNKS,
            probabilities.subList(0, this.DEFAULT_CHUNKS.size),
            probabilities.subList(
                this.DEFAULT_CHUNKS.size,
                this.DEFAULT_CHUNKS.size + this.DEFAULT_CHUNKS.size * this.DEFAULT_CHUNKS.size
            ),
            chunksInLevelCount,
            this.DEFAULT_START_CHUNK,
            this.DEFAULT_END_CHUNK,
            probabilities.subList(
                this.DEFAULT_CHUNKS.size + this.DEFAULT_CHUNKS.size * this.DEFAULT_CHUNKS.size,
                this.DEFAULT_CHUNKS.size + this.DEFAULT_CHUNKS.size * this.DEFAULT_CHUNKS.size + this.ENEMY_TYPES_COUNT
            ),
            probabilities[this.DEFAULT_CHUNKS.size + this.DEFAULT_CHUNKS.size * this.DEFAULT_CHUNKS.size + this.ENEMY_TYPES_COUNT]
        )
    }

    fun create(chunks: Array<MarioLevelChunk>, startingProbabilities: List<Double>, transitionProbabilities: List<Double>,
               chunksInLevelCount: Int, startChunk: MarioLevelChunk, endChunk: MarioLevelChunk, entitiesProbabilities: List<Double>,
               heightChangeProbability: Double): MarioLevel {
        if (transitionProbabilities.size != chunks.size * chunks.size) {
            throw IllegalArgumentException("The length of transition probabilities arrays must equal to the length of chunks array squared")
        }

        if (startingProbabilities.size != chunks.size) {
            throw IllegalArgumentException("The length of starting probabilities arrays must equal to the length of chunks array")
        }

        if (entitiesProbabilities.size != this.ENEMY_TYPES_COUNT) {
            throw IllegalArgumentException("The length of entities probabilities arrays must equal to ${this.ENEMY_TYPES_COUNT}")
        }

        val tiles: Array<ByteArray> = this.createTiles(chunks, startingProbabilities, transitionProbabilities, chunksInLevelCount,
            startChunk, endChunk, heightChangeProbability)

        val entities: Array<Array<Int>> = this.createEntities(tiles, entitiesProbabilities)

        return DirectMarioLevel(tiles, entities)
    }

    private fun createTiles(chunks: Array<MarioLevelChunk>, startingProbabilities: List<Double>, transitionProbabilities: List<Double>,
                            chunksInLevelCount: Int, startChunk: MarioLevelChunk, endChunk: MarioLevelChunk,
                            heightChangeProbability: Double): Array<ByteArray> {
        val tilesList: MutableList<ByteArray> = mutableListOf()

        var currentHeight = 10
        tilesList.addAll(startChunk.generate(currentHeight))

        var currentChunkIndex = this.randomChoice(startingProbabilities)
        tilesList.addAll(chunks[currentChunkIndex].generate(currentHeight))

        for (chunkNumber in 1 until chunksInLevelCount) {
            if (this.random.nextDouble() < heightChangeProbability)
                currentHeight = (currentHeight + this.nextHeightChange).coerceIn(5, 14)

            val tpStartIndex = chunks.size * currentChunkIndex
            val tpEndIndex = chunks.size * (currentChunkIndex + 1)

            val nextChunkProbabilities = transitionProbabilities.subList(tpStartIndex, tpEndIndex)
            currentChunkIndex = this.randomChoice(nextChunkProbabilities)

            tilesList.addAll(chunks[currentChunkIndex].generate(currentHeight))
        }

        tilesList.addAll(endChunk.generate(currentHeight))

        return tilesList.toTypedArray()
    }

    private fun createEntities(tiles: Array<ByteArray>, entityProbabilities: List<Double>): Array<Array<Int>> {
        val entities: Array<Array<Int>> = Array(tiles.size) { column ->
            Array(tiles[column].size) { row ->
                if (tiles[column][row] != Tiles.PIPE_TOP_LEFT) Entities.NOTHING else Entities.Flower.NORMAL
            }
        }

        for (column in this.SAFE_ZONE_LENGTH until tiles.size - this.SAFE_ZONE_LENGTH) {
            val firstEmpty = tiles[column].size - tiles[column].reversedArray().indexOfFirst { it == Tiles.NOTHING }
            if (firstEmpty <= 0 || firstEmpty >= tiles[column].size) continue

            val entity = when (this.randomChoice(entityProbabilities)) {
                0 -> Entities.Goomba.NORMAL
                1 -> Entities.Koopa.GREEN
                2 -> Entities.Koopa.GREEN_WINGED
                3 -> Entities.Koopa.RED
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

    private fun printProbabilities(probabilities: List<Double>) {
        val chunksCount = this.DEFAULT_CHUNKS.size

        val initialProbabilities = probabilities.subList(0, chunksCount)
        val probabilitiesMatrix = probabilities.subList(chunksCount, chunksCount + chunksCount * chunksCount)
        val enemiesMatrix = probabilities.subList(chunksCount + chunksCount * chunksCount, chunksCount + chunksCount * chunksCount + this.ENEMY_TYPES_COUNT)

        println("PBOS:")
        println(initialProbabilities.joinToString(", "))
        println("-----------")
        for (x in 0 until chunksCount) {
            println(probabilitiesMatrix.subList(x * chunksCount, (x + 1) * chunksCount).joinToString(", "))
        }
        println("-----------")
        println(enemiesMatrix.joinToString(", "))
        println()

    }

}
