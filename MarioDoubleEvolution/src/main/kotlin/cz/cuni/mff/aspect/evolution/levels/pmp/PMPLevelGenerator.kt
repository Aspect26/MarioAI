package cz.cuni.mff.aspect.evolution.levels.pmp

import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.BoxPlatform
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.BoxPlatformType
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.PMPLevelMetadata
import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.utils.lastIndexOf
import java.util.*
import kotlin.math.min

/**
 * Implementation of Probabilistic Multipass level generator algorithm. It generates Super Mario levels in multiple stages:
 * 1. generate terrain
 * 2. generate pipes
 * 3. generate bullet bills
 * 4. generate stone stairs
 * 5. generate box platforms
 * 6. generate entities
 *
 * In each stage, the level is traversed from left to right, and in each tile it is decided (based on a spawn
 * probability of current stage) whether an obstacle/entityof current stage is generated. The spawn probabilities can be
 * specified.
 *
 * @param probabilities specifies the spawn probabilities.
 * @param length specifies the length of generated levels in tiles.
 */
class PMPLevelGenerator(
    private val probabilities: DoubleArray = DoubleArray(PROBABILITIES_COUNT) {
        when (it) {
            PI_CHANGE_HEIGHT -> 0.1
            PI_CREATE_GAP -> 0.05

            PI_ENEMY_GOOMBA -> 0.03
            PI_ENEMY_KOOPA_GREEN -> 0.03
            PI_ENEMY_KOOPA_GREEN_WINGED -> 0.01
            PI_ENEMY_KOOPA_RED -> 0.03
            PI_ENEMY_SPIKES -> 0.03

            PI_BULLET_BILL -> 0.03
            PI_PIPE -> 0.03

            PI_START_BOXES -> 0.07
            PI_DOUBLE_BOXES -> 0.33
            PI_POWER_UP -> 0.25

            PI_STAIRS -> 0.1

            else -> 0.0
        }
    },
    private val length: Int = 200
) : LevelGenerator {

    private val random = Random()
    private lateinit var _lastMetadata: PMPLevelMetadata

    fun equalsGenerator(other: PMPLevelGenerator): Boolean =
        other.length == this.length && other.probabilities.contentEquals(this.probabilities)

    override fun generate(): MarioLevel {
        val levelMetadata = this.createInitialLevel()

        this.groundPass(levelMetadata)
        this.pipesPass(levelMetadata)
        this.bulletBillsPass(levelMetadata)
        this.stairsPass(levelMetadata)
        this.boxesPass(levelMetadata)
        this.enemiesPass(levelMetadata)

        this._lastMetadata = levelMetadata
        return levelMetadata.createLevel()
    }

    fun generate(seed: Long): MarioLevel {
        random.setSeed(seed)
        return this.generate()
    }

    val lastMetadata: PMPLevelMetadata get() = this._lastMetadata
    val data: DoubleArray get() = this.probabilities

    private fun createInitialLevel(): PMPLevelMetadata {
        val groundHeight = IntArray(this.length) { STARTING_HEIGHT }
        val entities = IntArray(this.length) { Entities.NOTHING }
        val holes = IntArray(this.length) { 0 }
        val pipes = IntArray(this.length) { 0 }
        val bulletBills = IntArray(this.length) { 0 }
        val boxPlatforms = Array(this.length) { BoxPlatform(0, 0, listOf(), BoxPlatformType.BRICKS) }
        val stairs = IntArray(this.length) { 0 }

        return PMPLevelMetadata(
            LEVEL_HEIGHT,
            groundHeight,
            entities,
            holes,
            pipes,
            bulletBills,
            boxPlatforms,
            stairs
        )
    }

    private fun groundPass(levelMetadata: PMPLevelMetadata) {
        var currentHeight = levelMetadata.groundHeight[0]
        var lastChangeAtColumn = 0
        var lastLengthEndColumn = 0
        val changeOptions = intArrayOf(PI_CHANGE_HEIGHT, PI_CREATE_GAP)

        for (column in SAFE_ZONE_LENGTH until levelMetadata.levelLength) {
            if (column - lastLengthEndColumn <= 1 || column - lastChangeAtColumn <= 1 || column >= levelMetadata.levelLength - SAFE_ZONE_LENGTH) {
                levelMetadata.groundHeight[column] = currentHeight
                continue
            }

            when (this.selectChangeFrom(changeOptions, this.probabilities)) {
                PI_CHANGE_HEIGHT -> {
                    lastChangeAtColumn = column
                    val heightChange = this.nextHeightChange
                    val newHeight = (currentHeight + heightChange).coerceIn(1, LEVEL_HEIGHT - 5)
                    currentHeight = if (newHeight == currentHeight) {
                        (currentHeight - heightChange).coerceIn(1, LEVEL_HEIGHT - 5)
                    } else {
                        newHeight
                    }
                }
                PI_CREATE_GAP -> {
                    val gapLength = this.randomInt(2, 4)
                    lastLengthEndColumn = column + gapLength
                    levelMetadata.gaps[column] = gapLength
                }
            }

            levelMetadata.groundHeight[column] = currentHeight
        }
    }

    private fun pipesPass(levelMetadata: PMPLevelMetadata) {
        for (column in SAFE_ZONE_LENGTH until levelMetadata.levelLength - SAFE_ZONE_LENGTH) {
            val shouldBePipe = this.random.nextFloat() < this.probabilities[PI_PIPE]
            val canBePipe: Boolean = levelMetadata.pipes[column - 1] == 0
                    && levelMetadata.groundHeight[column] == levelMetadata.groundHeight[column + 1]
                    && levelMetadata.groundHeight[column] == levelMetadata.groundHeight[column - 1]
                    && !levelMetadata.isHoleAt(column - 1)
                    && !levelMetadata.isHoleAt(column)
                    && !levelMetadata.isHoleAt(column + 1)

            if (shouldBePipe && canBePipe) {
                val pipeHeight = this.randomInt(2, 4)
                levelMetadata.pipes[column] = pipeHeight
            }
        }
    }

    private fun bulletBillsPass(levelMetadata: PMPLevelMetadata) {
        for (column in SAFE_ZONE_LENGTH until levelMetadata.levelLength - SAFE_ZONE_LENGTH) {
            val shouldBeBulletBill = this.random.nextFloat() < this.probabilities[PI_BULLET_BILL]
            val canBeBulletBill: Boolean = levelMetadata.pipes[column - 1] == 0
                    && levelMetadata.pipes[column] == 0
                    && levelMetadata.groundHeight[column] == levelMetadata.groundHeight[column - 1]
                    && !levelMetadata.isHoleAt(column - 1)
                    && !levelMetadata.isHoleAt(column)

            if (shouldBeBulletBill && canBeBulletBill) {
                val bulletBillHeight = this.randomInt(1, 4)
                levelMetadata.bulletBills[column] = bulletBillHeight
            }
        }
    }

    private fun stairsPass(levelMetadata: PMPLevelMetadata) {
        for (column in SAFE_ZONE_LENGTH until levelMetadata.levelLength - SAFE_ZONE_LENGTH) {
            val shouldBeStairs = this.random.nextFloat() < this.probabilities[PI_STAIRS]
            val canBeStairs = !levelMetadata.isHoleAt(column)
                    && !levelMetadata.isHoleAt(column - 1)
                    && levelMetadata.groundHeight[column] == levelMetadata.groundHeight[column - 1]
                    && levelMetadata.stoneColumns.lastIndexOf { it != 0 } < column - 30

            if (shouldBeStairs && canBeStairs) {
                this.addStairs(levelMetadata, column)
            }

        }
    }

    private fun boxesPass(levelMetadata: PMPLevelMetadata) {
        for (column in SAFE_ZONE_LENGTH until levelMetadata.levelLength - SAFE_ZONE_LENGTH) {
            if (levelMetadata.groundHeight[column] == 0) continue
            val canBeBoxes = levelMetadata.pipes[column] == 0
                    && levelMetadata.pipes[column - 1] == 0
                    && levelMetadata.bulletBills[column] == 0
                    && levelMetadata.boxPlatforms.mapIndexed { columnIndex, platform -> if (platform.length == 0) -1 else columnIndex + platform.length }.max()!! < (column - 15)

            val shouldBeBoxes = this.random.nextFloat() < this.probabilities[PI_START_BOXES]
            val shouldBeDoubleBoxes = this.random.nextFloat() < this.probabilities[PI_DOUBLE_BOXES]

            if (canBeBoxes && shouldBeBoxes) {
                if (shouldBeDoubleBoxes) {
                    this.addDoubleBoxPlatform(levelMetadata, column)
                } else {
                    this.addBoxPlatform(levelMetadata, column)
                }
            }
        }
    }

    private fun enemiesPass(levelMetadata: PMPLevelMetadata) {
        val changeOptions = intArrayOf(PI_ENEMY_GOOMBA, PI_ENEMY_KOOPA_GREEN, PI_ENEMY_KOOPA_RED, PI_ENEMY_SPIKES)

        for (column in SAFE_ZONE_LENGTH until levelMetadata.levelLength - SAFE_ZONE_LENGTH) {
            if (levelMetadata.groundHeight[column] == 0) continue

            val entity = when (this.selectChangeFrom(changeOptions, this.probabilities)) {
                PI_ENEMY_GOOMBA -> Entities.Goomba.NORMAL
                PI_ENEMY_KOOPA_GREEN -> Entities.Koopa.GREEN
                PI_ENEMY_KOOPA_RED -> Entities.Koopa.RED
                PI_ENEMY_SPIKES -> Entities.Spiky.NORMAL
                else -> Entities.NOTHING
            }

            val canBeEntity = !levelMetadata.isHoleAt(column)
                    && !levelMetadata.isObstacleAt(column, levelMetadata.groundHeight[column] + 1)
                    && levelMetadata.entities.toList().subList(column - 10, column - 1).sumBy { if (it == 0) 0 else 1 } < 5
                    && levelMetadata.entities.toList().subList(column - 6, column - 1).sumBy { if (it == 0) 0 else 1 } < 3

            levelMetadata.entities[column] = if (canBeEntity) entity else Entities.NOTHING
        }
    }

    private fun addStairs(levelMetadata: PMPLevelMetadata, column: Int) {
        val nearestHoleIndex = this.nearestHoleOrEnd(levelMetadata, column)
        val nearestGroundHeightChangeIndex = this.nearestHeightChangeOrEnd(levelMetadata, column)
        val chosenLength = this.randomInt(3, 6)

        val stairsLength = min(chosenLength,
            min(levelMetadata.horizontalRayUntilObstacle(column, levelMetadata.groundHeight[column] + 1),
            min(nearestHoleIndex - column, nearestGroundHeightChangeIndex - column)))

        if (stairsLength <= 1) return

        for (step in 0 until stairsLength) {
            levelMetadata.stoneColumns[column + step] = step + 1
        }
    }

    private fun addBoxPlatform(levelMetadata: PMPLevelMetadata, column: Int) {
        val platformLevel = levelMetadata.groundHeight[column] + 4
        this.addBoxPlatformAt(levelMetadata, column, platformLevel)
    }

    private fun addDoubleBoxPlatform(levelMetadata: PMPLevelMetadata, column: Int) {
        val firstPlatformLevel = levelMetadata.groundHeight[column] + 4
        val firstPlatform = this.addBoxPlatformAt(levelMetadata, column, firstPlatformLevel) ?: return

        val secondPlatformLevel = levelMetadata.groundHeight[column] + 8
        this.addBoxPlatformAt(levelMetadata, column + 1, secondPlatformLevel, firstPlatform.length - 2)
    }

    private fun addBoxPlatformAt(levelMetadata: PMPLevelMetadata, column: Int, platformLevel: Int, maxLength: Int? = null): BoxPlatform? {
        val chosenPlatformLength: Int = this.randomInt(2, maxLength ?: 7)
        val maxPlatformLengthAvailable = levelMetadata.horizontalRayUntilObstacle(column, platformLevel - 1) - 1
        val platformLength = min(chosenPlatformLength, maxPlatformLengthAvailable)

        if (platformLength <= 0) return null

        val type = if (this.random.nextFloat() < 0.5) BoxPlatformType.BRICKS else BoxPlatformType.QUESTION_MARKS

        val powerUps = mutableListOf<Int>()
        if (this.random.nextFloat() < probabilities[PI_POWER_UP])
            powerUps.add(this.randomInt(0, platformLength -1))

        val platform = BoxPlatform(platformLength, platformLevel, powerUps, type)
        levelMetadata.boxPlatforms[column] = platform

        return platform
    }

    private fun nearestHoleOrEnd(levelMetadata: PMPLevelMetadata, fromColumn: Int): Int {
        for (index in fromColumn until levelMetadata.gaps.size) if (levelMetadata.gaps[index] > 0) return index
        return levelMetadata.levelLength
    }

    private fun nearestHeightChangeOrEnd(levelMetadata: PMPLevelMetadata, fromColumn: Int): Int {
        for (index in fromColumn + 1 until levelMetadata.groundHeight.size) if (levelMetadata.groundHeight[index] != levelMetadata.groundHeight[index - 1]) return index
        return levelMetadata.levelLength
    }

    private val nextHeightChange: Int get() {
        var change = this.randomInt(-4, 3)
        if (change >= 0) change++
        return change
    }

    private fun randomInt(lowerBound: Int, higherBound: Int): Int {
        val range = higherBound - lowerBound + 1
        return (this.random.nextFloat() * range).toInt() + lowerBound
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

    companion object {
        private val serialVersionUID = 6193608689969572463L
        const val SAFE_ZONE_LENGTH = 10
        private const val LEVEL_HEIGHT = 15
        private const val STARTING_HEIGHT = 5

        private const val PI_CHANGE_HEIGHT = 0
        private const val PI_CREATE_GAP = 1
        private const val PI_ENEMY_GOOMBA = 2
        private const val PI_ENEMY_KOOPA_GREEN = 3
        private const val PI_ENEMY_KOOPA_GREEN_WINGED = 4
        private const val PI_ENEMY_KOOPA_RED = 5
        private const val PI_ENEMY_SPIKES = 6
        private const val PI_BULLET_BILL = 7
        private const val PI_PIPE = 8
        private const val PI_START_BOXES = 9
        private const val PI_DOUBLE_BOXES = 10
        private const val PI_POWER_UP = 11
        private const val PI_STAIRS = 12

        const val PROBABILITIES_COUNT = 13

        fun createSimplest(): PMPLevelGenerator = PMPLevelGenerator(
            DoubleArray(PROBABILITIES_COUNT) { 0.0 }, 200
        )
    }

}
