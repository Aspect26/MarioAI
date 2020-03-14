package cz.cuni.mff.aspect.evolution.levels.pmp

import cz.cuni.mff.aspect.evolution.levels.ge.grammar.ChunkHelpers
import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.DirectMarioLevel
import cz.cuni.mff.aspect.mario.level.MarioLevel
import java.util.*


object PMPLevelCreator {

    private const val LEVEL_HEIGHT = 15
    private const val STARTING_HEIGHT = 5
    private const val SAFE_ZONE_LENGTH = 10
    private const val MAX_HOLE_SIZE = 4

    private const val PI_DECREASE_HEIGHT = 0
    private const val PI_INCREASE_HEIGHT = 1
    private const val PI_START_HOLE = 2
    private const val PI_END_HOLE = 3
    private const val PI_ENEMY_GOOMBA = 4
    private const val PI_ENEMY_KOOPA_GREEN = 5
    private const val PI_ENEMY_KOOPA_RED = 6
    private const val PI_ENEMY_SPIKES = 7
    private const val PI_ENEMY_BULLET_BILL = 8
    private const val PI_PIPE = 9
    private const val PI_START_BOXES = 10

    const val PROBABILITIES_COUNT = 11

    private val random = Random()

    fun createDefault(): MarioLevel =
        this.create(200, DoubleArray(11) {
            when (it) {
                PI_DECREASE_HEIGHT -> 0.07
                PI_INCREASE_HEIGHT -> 0.07
                PI_START_HOLE -> 0.05
                PI_END_HOLE -> 0.33

                PI_ENEMY_GOOMBA -> 0.03
                PI_ENEMY_KOOPA_GREEN -> 0.03
                PI_ENEMY_KOOPA_RED -> 0.03
                PI_ENEMY_SPIKES -> 0.03
                PI_ENEMY_BULLET_BILL -> 0.03

                PI_PIPE -> 0.03

                PI_START_BOXES -> 0.05

                else -> 0.0
            }
        })

    fun create(length: Int, probabilities: DoubleArray): MarioLevel {
        val levelMetadata = this.createInitialLevel(length)
        this.groundPass(levelMetadata, probabilities)
        this.pipesPass(levelMetadata, probabilities)
        this.boxesPass(levelMetadata, probabilities)
        this.enemiesPass(levelMetadata, probabilities)

        return this.createLevel(levelMetadata)
    }

    private fun createInitialLevel(length: Int): MarioLevelMetadata {
        val groundHeight = IntArray(length) { this.STARTING_HEIGHT }
        val entities = IntArray(length) { Entities.NOTHING }
        val pipes = BooleanArray(length) { false }
        val startBoxes = BooleanArray(length) { false }

        return MarioLevelMetadata(groundHeight, entities, pipes, startBoxes)
    }

    private fun groundPass(levelMetadata: MarioLevelMetadata, probabilities: DoubleArray) {
        var currentHeight = levelMetadata.groundHeight[0]
        var previousHeight = currentHeight
        var currentHoleSize = 0
        var changedHeight = false

        val changeOptions = intArrayOf(PI_INCREASE_HEIGHT, PI_DECREASE_HEIGHT, PI_START_HOLE, PI_END_HOLE)

        for (tileIndex in this.SAFE_ZONE_LENGTH .. levelMetadata.groundHeight.size - this.SAFE_ZONE_LENGTH) {
            if (!changedHeight) {
                val selected = this.selectChangeFrom(changeOptions, probabilities)
                changedHeight = true
                currentHeight = when (selected) {
                    PI_INCREASE_HEIGHT -> (currentHeight + this.nextHeightChange).coerceAtMost(this.LEVEL_HEIGHT - 7)
                    PI_DECREASE_HEIGHT -> (currentHeight - this.nextHeightChange).coerceAtLeast(0)
                    PI_START_HOLE -> {previousHeight = if (currentHeight != 0) currentHeight else previousHeight; 0 }
                    PI_END_HOLE -> previousHeight
                    else -> { changedHeight = false; currentHeight }
                }
            } else {
                changedHeight = false
            }

            levelMetadata.groundHeight[tileIndex] = currentHeight
            if (currentHeight == 0) currentHoleSize++
            if (currentHoleSize == this.MAX_HOLE_SIZE) currentHeight = previousHeight
        }
    }

    private fun pipesPass(levelMetadata: MarioLevelMetadata, probabilities: DoubleArray) {
        for (tileIndex in this.SAFE_ZONE_LENGTH .. levelMetadata.groundHeight.size - this.SAFE_ZONE_LENGTH) {
            val shouldBePipe = this.random.nextFloat() < probabilities[this.PI_PIPE]
            val canBePipe: Boolean = !levelMetadata.pipes[tileIndex - 1]
                    && levelMetadata.groundHeight[tileIndex] == levelMetadata.groundHeight[tileIndex + 1]
                    && levelMetadata.groundHeight[tileIndex] == levelMetadata.groundHeight[tileIndex - 1]
                    && levelMetadata.groundHeight[tileIndex] != 0

            if (shouldBePipe && canBePipe) {
                levelMetadata.pipes[tileIndex] = true
            }
        }
    }

    private fun boxesPass(levelMetadata: MarioLevelMetadata, probabilities: DoubleArray) {
        for (tileIndex in this.SAFE_ZONE_LENGTH .. levelMetadata.groundHeight.size - this.SAFE_ZONE_LENGTH) {
            if (levelMetadata.groundHeight[tileIndex] == 0) continue

            val startBoxes = this.random.nextFloat() < probabilities[this.PI_START_BOXES]
            levelMetadata.startBoxes[tileIndex] = startBoxes
        }
    }

    private fun enemiesPass(levelMetadata: MarioLevelMetadata, probabilities: DoubleArray) {
        val changeOptions = intArrayOf(PI_ENEMY_GOOMBA, PI_ENEMY_KOOPA_GREEN, PI_ENEMY_KOOPA_RED, PI_ENEMY_SPIKES, PI_ENEMY_BULLET_BILL)

        for (tileIndex in this.SAFE_ZONE_LENGTH .. levelMetadata.groundHeight.size - this.SAFE_ZONE_LENGTH) {
            if (levelMetadata.groundHeight[tileIndex] == 0) continue

            var entity = when (this.selectChangeFrom(changeOptions, probabilities)) {
                PI_ENEMY_GOOMBA -> Entities.Goomba.NORMAL
                PI_ENEMY_KOOPA_GREEN -> Entities.Koopa.GREEN
                PI_ENEMY_KOOPA_RED -> Entities.Koopa.RED
                PI_ENEMY_SPIKES -> Entities.Spiky.NORMAL
                PI_ENEMY_BULLET_BILL -> Entities.BulletBill.NORMAL
                else -> Entities.NOTHING
            }

            if (entity == Entities.BulletBill.NORMAL && levelMetadata.groundHeight[tileIndex] != levelMetadata.groundHeight[tileIndex - 1]) entity = Entities.NOTHING
            levelMetadata.entities[tileIndex] = entity
        }
    }

    private val nextHeightChange: Int get() = 2 + this.random.nextInt() % 3

    private fun createLevel(metadata: MarioLevelMetadata): MarioLevel {
        val levelLength = metadata.groundHeight.size

        val entities: Array<Array<Int>> = Array(levelLength) { column -> Array(this.LEVEL_HEIGHT) {height ->
            when {
                height == this.LEVEL_HEIGHT - (metadata.groundHeight[column] + 1) && metadata.entities[column] != Entities.BulletBill.NORMAL -> metadata.entities[column]
                else -> Entities.NOTHING
            }
        } }

        val tilesList: MutableList<ByteArray> = mutableListOf()
        var lastPipeHeight = 0
        var boxesLength = 0
        var boxesLevel = 0

        for (column in metadata.groundHeight.indices) {
            val currentLevel = this.LEVEL_HEIGHT - metadata.groundHeight[column]
            if (metadata.startBoxes[column]) {
                boxesLength = 2 + this.random.nextInt(6)
                boxesLevel = currentLevel - 4
            }

            val currentChunk = when {
                metadata.entities[column] == Entities.BulletBill.NORMAL -> ChunkHelpers.getBlasterBulletBillColumn(currentLevel, 2 + this.random.nextInt(3))
                metadata.pipes[column] -> { lastPipeHeight = 2 + this.random.nextInt(3); entities[column][currentLevel - lastPipeHeight + 1] = Entities.Flower.NORMAL; ChunkHelpers.getPipeStartColumn(currentLevel, lastPipeHeight) }
                column > 0 && metadata.pipes[column - 1] -> ChunkHelpers.getPipeEndColumn(currentLevel, lastPipeHeight)
                else -> ChunkHelpers.getPathColumn(currentLevel)
            }

            if (boxesLength > 0) {
                currentChunk[boxesLevel] = Tiles.BRICK
                boxesLength -= 1
            }

            tilesList.add(currentChunk)
        }

        // TODO: use level height also for these 'chunks'
        val tiles: Array<ByteArray> = tilesList.toTypedArray()

        return DirectMarioLevel(tiles, entities)
    }

    private fun selectChangeFrom(options: IntArray, probabilities: DoubleArray): Int {
        var cumulativeProbability = 0.0
        val probability = this.random.nextDouble()

        for (option in options) {
            if (probability < cumulativeProbability + probabilities[option]) {
                return option
            } else {
                cumulativeProbability += probabilities[option]
            }
        }

        return -1
    }

}