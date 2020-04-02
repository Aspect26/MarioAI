package cz.cuni.mff.aspect.evolution.levels.pmp

import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.BoxPlatform
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.BoxPlatformType
import cz.cuni.mff.aspect.evolution.levels.pmp.metadata.MarioLevelMetadata
import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.level.MarioLevel
import java.util.*
import kotlin.math.min


class PMPLevelGenerator(
    private val probabilities: DoubleArray = DoubleArray(PROBABILITIES_COUNT) {
        when (it) {
            PI_CHANGE_HEIGHT -> 0.07
            PI_CREATE_HOLE -> 0.05

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
    private lateinit var _lastMetadata: MarioLevelMetadata

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

    val lastMetadata: MarioLevelMetadata get() = this._lastMetadata

    private fun createInitialLevel(): MarioLevelMetadata {
        val groundHeight = IntArray(this.length) { STARTING_HEIGHT }
        val entities = IntArray(this.length) { Entities.NOTHING }
        val holes = IntArray(this.length) { 0 }
        val pipes = IntArray(this.length) { 0 }
        val bulletBills = IntArray(this.length) { 0 }
        val boxPlatforms = Array(this.length) { BoxPlatform(0, 0, listOf(), BoxPlatformType.BRICKS) }
        val stairs = IntArray(this.length) { 0 }

        return MarioLevelMetadata(
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

    private fun groundPass(levelMetadata: MarioLevelMetadata) {
        var currentHeight = levelMetadata.groundHeight[0]
        var lastChangeAtColumn = 0
        var lastHoleEndColumn = 0
        val changeOptions = intArrayOf(PI_CHANGE_HEIGHT, PI_CREATE_HOLE)

        for (column in SAFE_ZONE_LENGTH until levelMetadata.levelLength) {
            if (column - lastHoleEndColumn <= 1 || column - lastChangeAtColumn <= 1 || column >= levelMetadata.levelLength - SAFE_ZONE_LENGTH) {
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
                PI_CREATE_HOLE -> {
                    val holeLength = this.randomInt(2, 4)
                    lastHoleEndColumn = column + holeLength
                    levelMetadata.holes[column] = holeLength
                }
            }

            levelMetadata.groundHeight[column] = currentHeight
        }
    }

    private fun pipesPass(levelMetadata: MarioLevelMetadata) {
        for (column in SAFE_ZONE_LENGTH until levelMetadata.levelLength - SAFE_ZONE_LENGTH) {
            val shouldBePipe = this.random.nextFloat() < this.probabilities[PI_PIPE]
            val canBePipe: Boolean = levelMetadata.pipes[column - 1] == 0
//                    && levelMetadata.groundHeight[column] == levelMetadata.groundHeight[column + 1]
//                    && levelMetadata.groundHeight[column] == levelMetadata.groundHeight[column - 1]
                    && !levelMetadata.isHoleAt(column - 1)
                    && !levelMetadata.isHoleAt(column)
                    && !levelMetadata.isHoleAt(column + 1)

            if (shouldBePipe && canBePipe) {
                val pipeHeight = this.randomInt(2, 4)
                levelMetadata.pipes[column] = pipeHeight
            }
        }
    }

    private fun bulletBillsPass(levelMetadata: MarioLevelMetadata) {
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

    private fun stairsPass(levelMetadata: MarioLevelMetadata) {
        for (column in SAFE_ZONE_LENGTH until levelMetadata.levelLength - SAFE_ZONE_LENGTH) {
            val shouldBeStairs = this.random.nextFloat() < this.probabilities[PI_STAIRS]
            val canBeStairs = !levelMetadata.isHoleAt(column)
                    && !levelMetadata.isHoleAt(column - 1)
                    && levelMetadata.groundHeight[column] == levelMetadata.groundHeight[column - 1]

            if (shouldBeStairs && canBeStairs) {
                val nearestHoleIndex = this.nearestHoleOrEnd(levelMetadata, column)
                val nearestGroundHeightChangeIndex = this.nearestHeightChangeOrEnd(levelMetadata, column)
                val chosenLength = this.randomInt(3, 6)
                val maxLength = min(
                    levelMetadata.horizontalRayUntilObstacle(column, levelMetadata.groundHeight[column] + 1),
                    min(nearestHoleIndex - column, nearestGroundHeightChangeIndex - column))
                val stairsLength = min(chosenLength, maxLength)

                if (stairsLength == 1) continue

                for (step in 0 until stairsLength) {
                    levelMetadata.stoneColumns[column + step] = step + 1
                }
            }

        }
    }

    private fun boxesPass(levelMetadata: MarioLevelMetadata) {
        for (column in SAFE_ZONE_LENGTH until levelMetadata.levelLength - SAFE_ZONE_LENGTH) {
            if (levelMetadata.groundHeight[column] == 0) continue
            val canBeBoxes = levelMetadata.pipes[column] == 0
                    && levelMetadata.pipes[column - 1] == 0
                    && levelMetadata.bulletBills[column] == 0
            val shouldBeBoxes = this.random.nextFloat() < this.probabilities[PI_START_BOXES]
            val shouldBeDoubleBoxes = this.random.nextFloat() < this.probabilities[PI_DOUBLE_BOXES]

            // TODO: refactor this
            if (canBeBoxes && shouldBeBoxes) {
                val boxesLevel = levelMetadata.groundHeight[column] + 4

                val nearestPipeIndex = this.nearestPipeOrEnd(levelMetadata, column)
                val nearestBillIndex = this.nearestBillOrEnd(levelMetadata, column)

                val chosenBoxesLength = this.randomInt(2, 7)
                val maxBoxesLengthAvailable = min(
                    min(levelMetadata.horizontalRayUntilObstacle(column, boxesLevel - 1), levelMetadata.horizontalRayUntilObstacle(column, boxesLevel - 1) - 1),
                    min(nearestPipeIndex, nearestBillIndex) - column)
                val boxesLength = min(chosenBoxesLength, maxBoxesLengthAvailable)

                val type = if (this.random.nextFloat() < 0.5) BoxPlatformType.BRICKS else BoxPlatformType.QUESTION_MARKS
                val powerUps = mutableListOf<Int>()
                if (this.random.nextFloat() < probabilities[Companion.PI_POWER_UP])
                    powerUps.add(this.randomInt(0, boxesLength -1))
                levelMetadata.boxPlatforms[column] = BoxPlatform(boxesLength, boxesLevel, powerUps, type)

                if (shouldBeDoubleBoxes && chosenBoxesLength > 2) {
                    val boxesLevel2 = levelMetadata.groundHeight[column] + 8
                    val chosenBoxesLength2 = this.randomInt(2, chosenBoxesLength - 2)
                    val maxBoxesLengthAvailable2 = min(levelMetadata.horizontalRayUntilObstacle(column + 1, boxesLevel2 - 1),
                        levelMetadata.horizontalRayUntilObstacle(column + 1, boxesLevel2 - 1) - 1)
                    val boxesLength2 = min(chosenBoxesLength2, maxBoxesLengthAvailable2)

                    val type2 = if (this.random.nextFloat() < 0.5) BoxPlatformType.BRICKS else BoxPlatformType.QUESTION_MARKS
                    val powerUps2 = mutableListOf<Int>()
                    if (this.random.nextFloat() < probabilities[Companion.PI_POWER_UP])
                        powerUps2.add(this.randomInt(0, boxesLength2 -1))
                    levelMetadata.boxPlatforms[column + 1] = BoxPlatform(boxesLength2, boxesLevel2, powerUps2, type2)
                }
            }
        }
    }

    private fun enemiesPass(levelMetadata: MarioLevelMetadata) {
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

            levelMetadata.entities[column] = if (canBeEntity) entity else Entities.NOTHING
        }
    }

    private fun nearestPipeOrEnd(levelMetadata: MarioLevelMetadata, fromColumn: Int): Int {
        for (index in fromColumn until levelMetadata.pipes.size) if (levelMetadata.pipes[index] > 0) return index
        return levelMetadata.levelLength
    }

    private fun nearestBillOrEnd(levelMetadata: MarioLevelMetadata, fromColumn: Int): Int {
        for (index in fromColumn until levelMetadata.bulletBills.size) if (levelMetadata.bulletBills[index] > 0) return index
        return levelMetadata.levelLength
    }

    private fun nearestHoleOrEnd(levelMetadata: MarioLevelMetadata, fromColumn: Int): Int {
        for (index in fromColumn until levelMetadata.holes.size) if (levelMetadata.holes[index] > 0) return index
        return levelMetadata.levelLength
    }

    private fun nearestHeightChangeOrEnd(levelMetadata: MarioLevelMetadata, fromColumn: Int): Int {
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
        const val SAFE_ZONE_LENGTH = 10
        private const val LEVEL_HEIGHT = 15
        private const val STARTING_HEIGHT = 5

        private const val PI_CHANGE_HEIGHT = 0
        private const val PI_CREATE_HOLE = 1
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
    }

}
