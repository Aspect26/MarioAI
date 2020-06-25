package cz.cuni.mff.aspect.evolution.levels.chunks.chunks

import cz.cuni.mff.aspect.evolution.levels.ge.grammar.ColumnHelpers

/**
 * Super Mario level chunk representing a gap between platforms of given length.
 * @param holeLength length of the gap. Two additional columns of path are generated on each side of the hole.
 */
data class GapChunk(private val holeLength: Int): MarioLevelChunk("gap") {

    override fun generateColumn(column: Int, level: Int): ByteArray =
        when (column) {
            in 2 until 2 + this.holeLength -> ColumnHelpers.getSpaceColumn()
            else -> ColumnHelpers.getPathColumn(level)
        }

    override val length: Int get() = this.holeLength + 4

    override fun copySelf(): MarioLevelChunk = this.copy()

}

/**
 * Super Mario level chunk representing a flat path of given length.
 * @param length length of the path.
 */
data class PathChunk(override val length: Int): MarioLevelChunk("path") {

    override fun generateColumn(column: Int, level: Int): ByteArray =
        ColumnHelpers.getPathColumn(level)

    override fun copySelf(): MarioLevelChunk = this.copy()

}

/**
 * Super Mario level chunk representing a single platform, 4 tiles above floor level.
 * @param platformLength length of the platform. One additional column of path is generated on each side of the hole.
 * @param platformType type of the platform, see [cz.cuni.mff.aspect.mario.Tiles] for supported values.
 */
data class SinglePlatformChunk(private val platformLength: Int, private val platformType: Byte): MarioLevelChunk("single-path") {

    override fun generateColumn(column: Int, level: Int): ByteArray =
        when (column) {
            in 1 until 1 + this.platformLength -> ColumnHelpers.getPlatformColumn(level, level - 4, platformType)
            else -> ColumnHelpers.getPathColumn(level)
        }

    override val length: Int get() = this.platformLength + 2

    override fun copySelf(): MarioLevelChunk = this.copy()

}

/**
 * Super Mario chunk representing a pipe of a given height. The chunk consists of 4 columns, the side ones being a path
 * and the inner ones being the pipe.
 * @param pipeHeight height of the pipe.
 */
data class PipeChunk(private val pipeHeight: Int): MarioLevelChunk("pipe") {

    override fun generateColumn(column: Int, level: Int): ByteArray =
        when (column) {
            1 -> ColumnHelpers.getPipeStartColumn(level, pipeHeight)
            2 -> ColumnHelpers.getPipeEndColumn(level, pipeHeight)
            else -> ColumnHelpers.getPathColumn(level)
        }

    override val length: Int get() = 4

    override fun copySelf(): MarioLevelChunk = this.copy()

}

/**
 * Super Mario chunk representing a bullet bill of a given height. The chunk consists of 3 columns, the side ones being
 * a path and the inner one being the bullet bill.
 * @param billHeight height of the buller bill.
 */
data class BulletBillChunk(private val billHeight: Int): MarioLevelChunk("bullet-bill") {

    override fun generateColumn(column: Int, level: Int): ByteArray =
        when (column) {
            1 -> ColumnHelpers.getBlasterBulletBillColumn(level, billHeight)
            else -> ColumnHelpers.getPathColumn(level)
        }

    override val length: Int get() = 3

    override fun copySelf(): MarioLevelChunk = this.copy()

}

/**
 * Super Mario level chunk representing stairs built from stone blocks of a given length.
 * @param stairsLength length of the stairs. One additional path column is added to the each side of the stairs.
 */
data class StairChunk(private val stairsLength: Int): MarioLevelChunk("stairs") {

    override fun generateColumn(column: Int, level: Int): ByteArray =
        when (column) {
            in 1 until 1 + this.stairsLength -> ColumnHelpers.getStonesColumn(level, column)
            else -> ColumnHelpers.getPathColumn(level)
        }

    override val length: Int get() = this.stairsLength + 2

    override fun copySelf(): MarioLevelChunk = this.copy()

}

/**
 * Super Mario level chunk representing a double platform. The chunk is made of floor, first platform 4 tiles higher, and
 * second platform 4 other files higher.
 * @param platformLength length of the platform. The second platform is always two tiles shorter (one on each side). The
 * chunk also contains one additional path column on each side of the platform.
 * @param firstPlatformType type of the first platform, see [cz.cuni.mff.aspect.mario.Tiles] for supported values.
 * @param secondPlatformType type of the second platform, see [cz.cuni.mff.aspect.mario.Tiles] for supported values.
 */
data class DoublePlatformChunk(private val platformLength: Int, private val firstPlatformType: Byte, private val secondPlatformType: Byte): MarioLevelChunk("double-path") {

    override fun generateColumn(column: Int, level: Int): ByteArray =
        when (column) {
            0, this.platformLength + 1 -> ColumnHelpers.getPathColumn(level)
            1, this.platformLength -> ColumnHelpers.getPlatformColumn(level, level - 4, this.firstPlatformType)
            else -> ColumnHelpers.getColumnWithTwoBlocks(level, level - 4, this.firstPlatformType, level - 8, this.secondPlatformType)
        }

    override val length: Int get() = this.platformLength + 2

    override fun copySelf(): MarioLevelChunk = this.copy()

}
