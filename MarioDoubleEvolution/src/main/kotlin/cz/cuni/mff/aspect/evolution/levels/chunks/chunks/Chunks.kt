package cz.cuni.mff.aspect.evolution.levels.chunks.chunks

import cz.cuni.mff.aspect.evolution.levels.ge.grammar.ColumnHelpers


data class HoleChunk(private val length: Int): MarioLevelChunk("hole") {

    override fun generate(level: Int): Array<ByteArray> =
        Array(this.length + 4) {
            if (it in 2 .. this.length + 1) ColumnHelpers.getSpaceColumn()
            else ColumnHelpers.getPathColumn(level)
        }

    override fun copySelf(): MarioLevelChunk = this.copy()

}


data class PathChunk(private val length: Int): MarioLevelChunk("path") {

    override fun generate(level: Int): Array<ByteArray> =
        Array(this.length) { ColumnHelpers.getPathColumn(level) }

    override fun copySelf(): MarioLevelChunk = this.copy()

}


data class SinglePlatformChunk(private val length: Int, private val platformType: Byte): MarioLevelChunk("single-path") {

    override fun generate(level: Int): Array<ByteArray> =
        Array(this.length + 2) {
            if (it in 1 .. this.length) ColumnHelpers.getPlatformColumn(level, level - 4, platformType)
            else ColumnHelpers.getPathColumn(level)
        }

    override fun copySelf(): MarioLevelChunk = this.copy()

}


data class PipeChunk(private val size: Int): MarioLevelChunk("pipe") {

    override fun generate(level: Int): Array<ByteArray> =
        Array(4) {
            when (it) {
                1 -> ColumnHelpers.getPipeStartColumn(level, size)
                2 -> ColumnHelpers.getPipeEndColumn(level, size)
                else -> ColumnHelpers.getPathColumn(level)
            }
        }

    override fun copySelf(): MarioLevelChunk = this.copy()

}


data class BulletBillChunk(private val size: Int): MarioLevelChunk("bullet-bill") {

    override fun generate(level: Int): Array<ByteArray> =
        Array(3) {
            when (it) {
                1 -> ColumnHelpers.getBlasterBulletBillColumn(level, size)
                else -> ColumnHelpers.getPathColumn(level)
            }
        }

    override fun copySelf(): MarioLevelChunk = this.copy()

}


data class StairChunk(private val length: Int): MarioLevelChunk("stairs") {

    override fun generate(level: Int): Array<ByteArray> =
        Array(this.length + 2) {
            if (it in 1 .. this.length) ColumnHelpers.getStonesColumn(level, it)
            else ColumnHelpers.getPathColumn(level)
        }

    override fun copySelf(): MarioLevelChunk = this.copy()

}


data class DoublePlatformChunk(private val length: Int, private val firstPlatformType: Byte, private val secondPlatformType: Byte): MarioLevelChunk("double-path") {

    override fun generate(level: Int): Array<ByteArray> =
        Array(this.length + 2) {
            when (it) {
                0, this.length + 1 -> ColumnHelpers.getPathColumn(level)
                1, this.length -> ColumnHelpers.getPlatformColumn(level, level - 4, this.firstPlatformType)
                else -> ColumnHelpers.getColumnWithTwoBlocks(level, level - 4, this.firstPlatformType, level - 8, this.secondPlatformType)
            }
        }

    override fun copySelf(): MarioLevelChunk = this.copy()

}
