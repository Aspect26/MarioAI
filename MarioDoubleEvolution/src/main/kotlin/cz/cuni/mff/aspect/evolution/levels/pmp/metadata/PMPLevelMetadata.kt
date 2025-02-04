package cz.cuni.mff.aspect.evolution.levels.pmp.metadata

import cz.cuni.mff.aspect.evolution.levels.ge.grammar.ColumnHelpers
import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.DirectMarioLevel
import cz.cuni.mff.aspect.mario.level.MarioLevel

/** Level metadata as generaetd by [cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator]. */
data class PMPLevelMetadata (
    val levelHeight: Int,
    val groundHeight: IntArray,
    val entities: IntArray,
    val gaps: IntArray,
    val pipes: IntArray,
    val bulletBills: IntArray,
    val boxPlatforms: Array<BoxPlatform>,
    val stoneColumns: IntArray) {

    val levelLength: Int get() = this.groundHeight.size

    /** Creates a Super Mario level by using metadata represented by this entity. */
    fun createLevel(): MarioLevel {
        val entities: Array<IntArray> = this.createEntities()
        val tiles: Array<ByteArray> = this.createGround()
        this.insertHoles(tiles)
        this.insertPipes(tiles, entities)
        this.insertBulletBills(tiles)
        this.insertBoxPlatforms(tiles)
        this.insertStoneColumns(tiles)

        return DirectMarioLevel(tiles, entities)
    }

    val enemiesCount: Int get() = this.entities.filter { it != Entities.NOTHING }.size
    val holesCount: Int get() = this.gaps.filter { it > 0}.size
    val pipesCount: Int get() = this.pipes.filter { it > 0}.size
    val billsCount: Int get() = this.bulletBills.filter { it > 0}.size
    val boxPlatformsCount: Int get() = this.boxPlatforms.filter { it.length > 0}.size
    val stoneColumnsCount: Int get() = this.stoneColumns.filter { it > 0}.size

    /** Computes whether there is a hole on the given column in the level represented by this metadata. */
    fun isHoleAt(checkingColumn: Int): Boolean {
        for (currentColumn in this.gaps.indices) {
            val holeLength = this.gaps[currentColumn]
            if (holeLength > 0 && checkingColumn in currentColumn until currentColumn + holeLength) return true
        }

        return false
    }

    /** Computes length of a ray until obstacle from given position in the level represented by this metadata. */
    fun horizontalRayUntilObstacle(fromColumn: Int, fromRow: Int): Int {
        var currentLength = 0

        for (currentColumn in fromColumn until this.levelLength) {
            if (this.isObstacleAt(currentColumn, fromRow)) break
            currentLength++
        }

        return currentLength
    }

    /** Computes whether there is an obstacle on the given column and row in the level represented by this metadata. */
    fun isObstacleAt(checkingColumn: Int, row: Int): Boolean {
        return this.groundHeight[checkingColumn] >= row
                || this.groundHeight[checkingColumn] + this.pipes[checkingColumn] >= row
                || checkingColumn > 0 && (this.pipes[checkingColumn - 1] > 0 && this.groundHeight[checkingColumn] + this.pipes[checkingColumn - 1] >= row)
                || this.groundHeight[checkingColumn] + this.bulletBills[checkingColumn] >= row
                || this.groundHeight[checkingColumn] + this.stoneColumns[checkingColumn] >= row
                || this.boxPlatforms.filterIndexed { column, boxPlatform ->
                        boxPlatform.length > 0 && boxPlatform.boxesLevel == row && checkingColumn in (column until column + boxPlatform.length)
                    }.isNotEmpty()
    }

    private fun createEntities(): Array<IntArray> = Array(this.levelLength) { column ->
        IntArray(this.levelHeight) { height ->
            when (height) {
                this.levelHeight - (groundHeight[column] + 1 + stoneColumns[column]) -> entities[column]
                else -> Entities.NOTHING
            }
        }
    }

    private fun createGround(): Array<ByteArray> = Array(this.levelLength) {
        val currentLevel = this.levelHeight - this.groundHeight[it]
        val currentChunk = ColumnHelpers.getPathColumn(currentLevel, this.levelHeight)
        currentChunk
    }

    private fun insertHoles(tiles: Array<ByteArray>) {
        for (column in this.gaps.indices) {
            val gapLength = this.gaps[column]
            if (gapLength > 0) {
                this.insertHole(tiles, column, gapLength)
            }
        }
    }

    private fun insertPipes(tiles: Array<ByteArray>, entities: Array<IntArray>) {
        for (column in this.pipes.indices) {
            val pipeHeight = this.pipes[column]
            if (pipeHeight > 0) {
                this.insertPipe(tiles, entities, column, pipeHeight)
            }
        }
    }

    private fun insertBulletBills(tiles: Array<ByteArray>) {
        for (column in this.bulletBills.indices) {
            val billHeight = this.bulletBills[column]
            if (billHeight > 0) {
                this.insertBulletBill(tiles, column, billHeight)
            }
        }
    }

    private fun insertBoxPlatforms(tiles: Array<ByteArray>) {
        for (column in this.boxPlatforms.indices) {
            if (this.boxPlatforms[column].length > 0) {
                this.insertBoxPlatform(tiles, column, this.boxPlatforms[column])
            }
        }
    }

    private fun insertStoneColumns(tiles: Array<ByteArray>) {
        for (column in this.stoneColumns.indices) {
            val columnSize = this.stoneColumns[column]
            if (columnSize > 0) {
                this.insertStoneColumns(tiles, column, columnSize)
            }
        }
    }

    private fun insertHole(tiles: Array<ByteArray>, column: Int, holeLength: Int) {
        for (currentColumn in 0 until holeLength) {
            tiles[column + currentColumn] = ColumnHelpers.getSpaceColumn(this.levelHeight)
        }
    }

    private fun insertPipe(tiles: Array<ByteArray>, entities: Array<IntArray>, column: Int, pipeHeight: Int) {
        val groundLevel = this.levelHeight - this.groundHeight[column]
        for (level in groundLevel - pipeHeight + 1 until groundLevel) {
            tiles[column][level] = Tiles.PIPE_MIDDLE_LEFT
            tiles[column + 1][level] = Tiles.PIPE_MIDDLE_RIGHT
        }
        tiles[column][groundLevel - pipeHeight] = Tiles.PIPE_TOP_LEFT
        tiles[column + 1][groundLevel - pipeHeight] = Tiles.PIPE_TOP_RIGHT

        entities[column][groundLevel - pipeHeight ] = Entities.Flower.NORMAL
    }

    private fun insertBulletBill(tiles: Array<ByteArray>, column: Int, bulletBillHeight: Int) {
        val groundLevel = this.levelHeight - this.groundHeight[column]

        tiles[column][groundLevel - bulletBillHeight] = Tiles.BULLET_BLASTER_TOP
        if (bulletBillHeight > 1) tiles[column][groundLevel - bulletBillHeight + 1] = Tiles.BULLET_BLASTER_MIDDLE
        if (bulletBillHeight > 2) {
            for (level in groundLevel - bulletBillHeight + 2 until groundLevel) {
                tiles[column][level] = Tiles.BULLET_BLASTER_BOTTOM
            }
            tiles[column][groundLevel - 1] = Tiles.BULLET_BLASTER_BOTTOM
        }
    }

    private fun insertBoxPlatform(tiles: Array<ByteArray>, column: Int, boxPlatform: BoxPlatform) {
        val platformLevel = this.levelHeight - boxPlatform.boxesLevel
        if (platformLevel < 0) return

        val platformTile = if (boxPlatform.type == BoxPlatformType.BRICKS) Tiles.BRICK else Tiles.QM_WITH_COIN
        val powerUpTile = if (boxPlatform.type == BoxPlatformType.BRICKS) Tiles.BRICK_WITH_POWERUP else Tiles.QM_WITH_POWERUP

        for (currentColumn in 0 until boxPlatform.length) {
            tiles[column + currentColumn][platformLevel] = if (boxPlatform.powerUpPosition.contains(currentColumn)) powerUpTile else platformTile
        }
    }

    private fun insertStoneColumns(tiles: Array<ByteArray>, column: Int, columnSize: Int) {
        val groundLevel = this.levelHeight - this.groundHeight[column]

        for (level in 0 until columnSize) {
            if (groundLevel - level - 1 < 0) break
            tiles[column][groundLevel - level - 1] = Tiles.STONE
        }
    }

}
