package cz.cuni.mff.aspect.evolution.levels.chunks.chunks

import cz.cuni.mff.aspect.evolution.levels.ge.grammar.ColumnHelpers
import cz.cuni.mff.aspect.mario.Tiles


open class HoleChunk(private val length: Int): MarioLevelChunk() {

    override fun generate(level: Int): Array<ByteArray> =
        Array(this.length + 4) {
            if (it in 2 .. this.length + 1) ColumnHelpers.getSpaceColumn()
            else ColumnHelpers.getPathColumn(level)
        }

}

open class PathChunk(private val length: Int): MarioLevelChunk() {

    override fun generate(level: Int): Array<ByteArray> =
        Array(this.length) { ColumnHelpers.getPathColumn(level)}

}


open class SinglePlatformChunk(private val length: Int, private val platformType: Byte): MarioLevelChunk() {

    override fun generate(level: Int): Array<ByteArray> =
        Array(this.length + 2) {
            if (it in 1 .. this.length) ColumnHelpers.getPlatformColumn(level, level - 4, platformType)
            else ColumnHelpers.getPathColumn(level)
        }

}


open class PipeChunk(private val size: Int): MarioLevelChunk() {

    override fun generate(level: Int): Array<ByteArray> =
        Array(4) {
            when (it) {
                1 -> ColumnHelpers.getPipeStartColumn(level, size)
                2 -> ColumnHelpers.getPipeEndColumn(level, size)
                else -> ColumnHelpers.getPathColumn(level)
            }
        }

}


open class BulletBillChunk(private val size: Int): MarioLevelChunk() {

    override fun generate(level: Int): Array<ByteArray> =
        Array(3) {
            when (it) {
                1 -> ColumnHelpers.getBlasterBulletBillColumn(level, size)
                else -> ColumnHelpers.getPathColumn(level)
            }
        }

}


open class StairChunk(private val length: Int): MarioLevelChunk() {

    override fun generate(level: Int): Array<ByteArray> =
        Array(this.length + 2) {
            if (it in 1 .. this.length) ColumnHelpers.getStonesColumn(level, it)
            else ColumnHelpers.getPathColumn(level)
        }

}


open class DoublePlatformChunk(private val length: Int, private val firstPlatformType: Byte, private val secondPlatformType: Byte): MarioLevelChunk() {

    override fun generate(level: Int): Array<ByteArray> =
        Array(this.length + 2) {
            when (it) {
                0, this.length + 1 -> ColumnHelpers.getPathColumn(level)
                1, this.length -> ColumnHelpers.getPlatformColumn(level, level - 4, this.firstPlatformType)
                else -> ColumnHelpers.getColumnWithTwoBlocks(level, level - 4, this.firstPlatformType, level - 8, this.secondPlatformType)
            }
        }

}


class Hole2Chunk: HoleChunk(2)
class Hole3Chunk: HoleChunk(3)
class Hole4Chunk: HoleChunk(4)
class Path3Chunk: PathChunk(3)
class Path4Chunk: PathChunk(4)
class Path5Chunk: PathChunk(5)
class Path6Chunk: PathChunk(6)
class SingleBricks1Platform: SinglePlatformChunk(1, Tiles.BRICK)
class SingleBricks3Platform: SinglePlatformChunk(3, Tiles.BRICK)
class SingleBricks5Platform: SinglePlatformChunk(5, Tiles.BRICK)
class SingleQM1Platform: SinglePlatformChunk(1, Tiles.QM_WITH_COIN)
class SingleQM3Platform: SinglePlatformChunk(3, Tiles.QM_WITH_COIN)
class SingleQM5Platform: SinglePlatformChunk(5, Tiles.QM_WITH_COIN)
class Pipe2Chunk: PipeChunk(2)
class Pipe3Chunk: PipeChunk(3)
class Pipe4Chunk: PipeChunk(4)
class BulletBill1Chunk: BulletBillChunk(1)
class BulletBill2Chunk: BulletBillChunk(2)
class BulletBill3Chunk: BulletBillChunk(3)
class BulletBill4Chunk: BulletBillChunk(4)
class Stair2Chunk: StairChunk(2)
class Stair3Chunk: StairChunk(3)
class Stair4Chunk: StairChunk(4)
class Stair5Chunk: StairChunk(5)
class DoubleBrickPlatforms5Chunk: DoublePlatformChunk(5, Tiles.BRICK, Tiles.BRICK)
class DoubleQMPlatforms5Chunk: DoublePlatformChunk(5, Tiles.QM_WITH_COIN, Tiles.QM_WITH_COIN)
class BrickAndQMPlatforms5Chunk: DoublePlatformChunk(5, Tiles.BRICK, Tiles.QM_WITH_COIN)
