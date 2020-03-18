package cz.cuni.mff.aspect.evolution.levels.pmp

import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.BoxPlatform
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.BoxPlatformType
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.Entities
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
        this.bulletBillsPass(levelMetadata, probabilities)
        this.boxesPass(levelMetadata, probabilities)
        this.enemiesPass(levelMetadata, probabilities)

        return levelMetadata.createLevel()
    }

    private fun createInitialLevel(length: Int): MarioLevelMetadata {
        val groundHeight = IntArray(length) { this.STARTING_HEIGHT }
        val entities = IntArray(length) { Entities.NOTHING }
        val pipes = IntArray(length) { 0 }
        val bulletBills = IntArray(length) { 0 }
        val boxPlatforms = Array(length) { BoxPlatform(0, intArrayOf(), BoxPlatformType.BRICKS) }
        val stairs = IntArray(length) { 0 }

        return MarioLevelMetadata(
            this.LEVEL_HEIGHT,
            groundHeight,
            entities,
            pipes,
            bulletBills,
            boxPlatforms,
            stairs
        )
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
            val canBePipe: Boolean = levelMetadata.pipes[tileIndex - 1] == 0
                    && levelMetadata.groundHeight[tileIndex] == levelMetadata.groundHeight[tileIndex + 1]
                    && levelMetadata.groundHeight[tileIndex] == levelMetadata.groundHeight[tileIndex - 1]
                    && levelMetadata.groundHeight[tileIndex] != 0

            if (shouldBePipe && canBePipe) {
                val pipeHeight = this.randomInt(2, 4)
                levelMetadata.pipes[tileIndex] = pipeHeight
            }
        }
    }

    private fun bulletBillsPass(levelMetadata: MarioLevelMetadata, probabilities: DoubleArray) {
        for (tileIndex in this.SAFE_ZONE_LENGTH .. levelMetadata.groundHeight.size - this.SAFE_ZONE_LENGTH) {
            val shouldBeBulletBill = this.random.nextFloat() < probabilities[this.PI_ENEMY_BULLET_BILL]
            val canBeBulletBill: Boolean = levelMetadata.pipes[tileIndex - 1] == 0
                    && levelMetadata.pipes[tileIndex] == 0
                    && levelMetadata.groundHeight[tileIndex] == levelMetadata.groundHeight[tileIndex - 1]
                    && levelMetadata.groundHeight[tileIndex] != 0

            if (shouldBeBulletBill && canBeBulletBill) {
                val bulletBillHeight = this.randomInt(1, 4)
                levelMetadata.bulletBills[tileIndex] = bulletBillHeight
            }
        }
    }

    private fun boxesPass(levelMetadata: MarioLevelMetadata, probabilities: DoubleArray) {
        for (tileIndex in this.SAFE_ZONE_LENGTH .. levelMetadata.groundHeight.size - this.SAFE_ZONE_LENGTH) {
            if (levelMetadata.groundHeight[tileIndex] == 0) continue

            val shouldBeBoxes = this.random.nextFloat() < probabilities[this.PI_START_BOXES]
            if (shouldBeBoxes) {
                val boxesLength = this.randomInt(2, 7)
                val type = if (this.random.nextFloat() < 0.5) BoxPlatformType.BRICKS else BoxPlatformType.QUESTION_MARKS
                // TODO: powerups!
                levelMetadata.boxPlatforms[tileIndex] = BoxPlatform(boxesLength, intArrayOf(), type)
            }
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
                else -> Entities.NOTHING
            }

            if (entity == Entities.BulletBill.NORMAL && levelMetadata.groundHeight[tileIndex] != levelMetadata.groundHeight[tileIndex - 1]) entity = Entities.NOTHING
            levelMetadata.entities[tileIndex] = entity
        }
    }

    private fun randomInt(lowerBound: Int, higherBound: Int): Int {
        val range = higherBound - lowerBound + 1
        return (this.random.nextFloat() * range).toInt() + lowerBound
    }

    private val nextHeightChange: Int get() = 2 + this.random.nextInt() % 3

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