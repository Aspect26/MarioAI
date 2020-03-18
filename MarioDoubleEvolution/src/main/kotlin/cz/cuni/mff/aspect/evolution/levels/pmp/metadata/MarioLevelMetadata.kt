package cz.cuni.mff.aspect.evolution.levels.pmp.metadata

import cz.cuni.mff.aspect.evolution.levels.ge.grammar.ChunkHelpers
import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.DirectMarioLevel
import cz.cuni.mff.aspect.mario.level.MarioLevel


data class MarioLevelMetadata (
    val levelHeight: Int,
    val groundHeight: IntArray,
    val entities: IntArray,
    val pipes: IntArray,
    val bulletBills: IntArray,
    val boxPlatforms: Array<BoxPlatform>,
    val stairs: IntArray) {

    // TODO: this constant is duplicated in PMPLevelCreator
    private val levelLength: Int get() = this.groundHeight.size

    fun createLevel(): MarioLevel {
        val entities: Array<Array<Int>> = this.createEntities()
        val tiles: Array<ByteArray> = this.createGround()
        this.insertPipes(tiles, entities)
        this.insertBulletBills(tiles)
        this.insertBoxPlatforms(tiles)
        this.insertStairs(tiles)

        return DirectMarioLevel(tiles, entities)
    }

    private fun createEntities(): Array<Array<Int>> = Array(this.levelLength) { column ->
        Array(this.levelHeight) { height ->
            when (height) {
                this.levelHeight - (groundHeight[column] + 1) -> entities[column]
                else -> Entities.NOTHING
            }
        }
    }

    private fun createGround(): Array<ByteArray> = Array(this.levelLength) {
        val currentLevel = this.levelHeight - this.groundHeight[it]
        val currentChunk = ChunkHelpers.getPathColumn(currentLevel, this.levelHeight)
        currentChunk
    }

    private fun insertPipes(tiles: Array<ByteArray>, entities: Array<Array<Int>>) {
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

    private fun insertStairs(tiles: Array<ByteArray>) {
        for (column in this.stairs.indices) {
            val stairsLength = this.stairs[column]
            if (stairsLength > 0) {
                this.insertStairs(tiles, column, stairsLength)
            }
        }
    }

    private fun insertPipe(tiles: Array<ByteArray>, entities: Array<Array<Int>>, column: Int, pipeHeight: Int) {
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
        val groundLevel = this.levelHeight - this.groundHeight[column]
        val platformLevel = groundLevel - 4
        val platformTile = if (boxPlatform.type == BoxPlatformType.BRICKS) Tiles.BRICK else Tiles.QM_WITH_COIN
        val powerUpTile = if (boxPlatform.type == BoxPlatformType.BRICKS) Tiles.BRICK_WITH_POWERUP else Tiles.QM_WITH_POWERUP

        for (currentColumn in 0 until boxPlatform.length) {
            tiles[column + currentColumn][platformLevel] = if (boxPlatform.powerUpPosition.contains(currentColumn)) powerUpTile else platformTile
        }
    }

    private fun insertStairs(tiles: Array<ByteArray>, column: Int, stairsLength: Int) {
        val groundLevel = this.levelHeight - this.groundHeight[column]

        for (currentColumn in 0 until stairsLength) {
            for (stairHeight in 0 until currentColumn)
                tiles[column + currentColumn][groundLevel - 1 - stairHeight] = Tiles.STONE
        }
    }

}
