package cz.cuni.mff.aspect.evolution.levels.pc.chunks


data class HoleChunk(private val holeLength: Int): MarioLevelChunk("hole") {

    override fun generateColumn(column: Int, level: Int): ByteArray =
        when (column) {
            in 2 until 2 + this.holeLength -> ColumnHelpers.getSpaceColumn()
            else -> ColumnHelpers.getPathColumn(level)
        }

    override val length: Int get() = this.holeLength + 4

    override fun copySelf(): MarioLevelChunk = this.copy()

}


data class PathChunk(override val length: Int): MarioLevelChunk("path") {

    override fun generateColumn(column: Int, level: Int): ByteArray =
        ColumnHelpers.getPathColumn(level)

    override fun copySelf(): MarioLevelChunk = this.copy()

}


data class SinglePlatformChunk(private val platformLength: Int, private val platformType: Byte): MarioLevelChunk("single-path") {

    override fun generateColumn(column: Int, level: Int): ByteArray =
        when (column) {
            in 1 until 1 + this.platformLength -> ColumnHelpers.getPlatformColumn(level, level - 4, platformType)
            else -> ColumnHelpers.getPathColumn(level)
        }

    override val length: Int get() = this.platformLength + 2

    override fun copySelf(): MarioLevelChunk = this.copy()

}


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


data class BulletBillChunk(private val billSize: Int): MarioLevelChunk("bullet-bill") {

    override fun generateColumn(column: Int, level: Int): ByteArray =
        when (column) {
            1 -> ColumnHelpers.getBlasterBulletBillColumn(level, billSize)
            else -> ColumnHelpers.getPathColumn(level)
        }

    override val length: Int get() = 3

    override fun copySelf(): MarioLevelChunk = this.copy()

}


data class StairChunk(private val stairsLength: Int): MarioLevelChunk("stairs") {

    override fun generateColumn(column: Int, level: Int): ByteArray =
        when (column) {
            in 1 until 1 + this.stairsLength -> ColumnHelpers.getStonesColumn(level, column)
            else -> ColumnHelpers.getPathColumn(level)
        }

    override val length: Int get() = this.stairsLength + 2

    override fun copySelf(): MarioLevelChunk = this.copy()

}


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
