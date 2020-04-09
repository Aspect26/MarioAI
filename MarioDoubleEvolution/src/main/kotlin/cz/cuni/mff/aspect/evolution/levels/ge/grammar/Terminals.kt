package cz.cuni.mff.aspect.evolution.levels.ge.grammar

import cz.cuni.mff.aspect.evolution.levels.ge.algorithm.Terminal
import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.MarioLevelChunk
import cz.cuni.mff.aspect.mario.level.MonsterSpawn
import cz.cuni.mff.aspect.mario.level.TerminalMarioLevelChunk


abstract class LevelChunkTerminal(value: String) : Terminal(value) {
    abstract fun generateChunk(): MarioLevelChunk
    override fun equals(other: Any?): Boolean = other is LevelChunkTerminal && other.value == this.value
    override fun hashCode(): Int = javaClass.hashCode() * this.value.hashCode()

    abstract val width: Int
}


class NothingChunkTerminal(private var _width: Int = 3) : LevelChunkTerminal("nothing") {

    override fun takeParameters(iterator: Iterator<Int>) {
        this._width = iterator.next() % 4 + 1
    }

    override fun generateChunk(): MarioLevelChunk {
        val emptyColumn = ColumnHelpers.getSpaceColumn()
        return TerminalMarioLevelChunk(this, Array(this._width) { emptyColumn }, emptyArray())
    }

    override val width: Int get() = this._width
    override fun copy(): LevelChunkTerminal =
        NothingChunkTerminal(this._width)
    override fun toString(): String = "${this.value}(${this._width})"
    override fun equals(other: Any?): Boolean = other is NothingChunkTerminal && other._width == this._width
    override fun hashCode(): Int = javaClass.hashCode() * this._width.hashCode()
}

class BeginPlatformChunkTerminal(private var level: Int = 5) : LevelChunkTerminal("begin") {

    override fun takeParameters(iterator: Iterator<Int>) {
        this.level = iterator.next() % 10 + 3
    }

    override fun generateChunk(): MarioLevelChunk {
        val pathColumn = ColumnHelpers.getPathColumn(this.level)
        return TerminalMarioLevelChunk(this, Array(this.width) { pathColumn }, emptyArray())
    }

    override val width: Int get() = 12
    override fun copy(): LevelChunkTerminal =
        BeginPlatformChunkTerminal(this.level)
    override fun toString(): String = "${this.value}(${this.level})"
    override fun equals(other: Any?): Boolean = other is BeginPlatformChunkTerminal && other.level == this.level
    override fun hashCode(): Int = javaClass.hashCode() * this.level.hashCode()
}

class EndPlatformChunkTerminal(private var level: Int = 5) : LevelChunkTerminal("end") {

    override fun takeParameters(iterator: Iterator<Int>) {
        this.level = iterator.next() % 10 + 3
    }

    override fun generateChunk(): MarioLevelChunk {
        val pathColumn = ColumnHelpers.getPathColumn(this.level)
        return TerminalMarioLevelChunk(this, Array(this.width) { pathColumn }, arrayOf(MonsterSpawn(this.width / 2, this.level - 1, Entities.PrincessPeach.NORMAL)))
    }

    override val width: Int get() = 12
    override fun copy(): LevelChunkTerminal =
        EndPlatformChunkTerminal(this.level)
    override fun toString(): String = "${this.value}(${this.level})"
    override fun equals(other: Any?): Boolean = other is EndPlatformChunkTerminal && other.level == this.level
    override fun hashCode(): Int = javaClass.hashCode() * this.level.hashCode()
}


abstract class MonsterSpawningChunkTerminal(terminalName: String, protected var monsterSpawns: Array<MonsterSpawn>) :
    LevelChunkTerminal(terminalName) {

    abstract fun takeChunkParameters(iterator: Iterator<Int>)

    override fun takeParameters(iterator: Iterator<Int>) {
        this.takeChunkParameters(iterator)

        val monsterCount: Int = iterator.next() % 3
        this.monsterSpawns = Array(monsterCount) {
            val xPos = iterator.next() % this.width
            val yPos = this.monstersLevel
            when (iterator.next() % 2) {
                0 -> MonsterSpawn(xPos, yPos, Entities.Goomba.NORMAL)
                1 -> MonsterSpawn(xPos, yPos, Entities.Koopa.GREEN)
                else -> MonsterSpawn(xPos, yPos, Entities.Goomba.WINGED)
            }
        }

    }

    protected abstract var level: Int
    protected open val monstersLevel: Int get() = this.level - 1
}


class PathChunkTerminal(monsterSpawns: Array<MonsterSpawn> = arrayOf(), override var level: Int = 5, var _width: Int = 3) :
    MonsterSpawningChunkTerminal("path", monsterSpawns) {

    override fun takeChunkParameters(iterator: Iterator<Int>) {
        this.level = iterator.next() % 10 + 3
        this._width = iterator.next() % 4 + 4
    }

    override fun generateChunk(): MarioLevelChunk {
        val pathColumn = ColumnHelpers.getPathColumn(this.level)
        return TerminalMarioLevelChunk(this, Array(this._width) { pathColumn }, this.monsterSpawns)
    }

    override val width: Int get() = this._width
    override fun copy(): PathChunkTerminal =
        PathChunkTerminal(
            this.monsterSpawns,
            this.level,
            this._width
        )
    override fun toString(): String = "${this.value}(${this._width},${this.level})"
    override fun equals(other: Any?): Boolean = other is PathChunkTerminal && other._width == this._width && other.level == this.level
    override fun hashCode(): Int = javaClass.hashCode() * this._width.hashCode() * this.level.hashCode()
}


class StartChunkTerminal(private var _width: Int = 7, private var level: Int = 14) :
    LevelChunkTerminal("start") {

    override fun takeParameters(iterator: Iterator<Int>) {
        this.level = iterator.next() % 5 + 9
        this._width = iterator.next() % 4 + 5
    }

    override fun generateChunk(): MarioLevelChunk {
        val pathColumn = ColumnHelpers.getPathColumn(this.level)
        return TerminalMarioLevelChunk(this, Array(this._width) { pathColumn }, emptyArray())
    }

    override val width: Int get() = this._width
    override fun copy(): StartChunkTerminal = StartChunkTerminal(this._width, this.level)
    override fun toString(): String = "${this.value}(${this._width})"
    override fun equals(other: Any?): Boolean = other is StartChunkTerminal && other._width == this._width
    override fun hashCode(): Int = javaClass.hashCode() * this._width.hashCode()
}


class BoxesChunkTerminal(monsterSpawns: Array<MonsterSpawn> = arrayOf(), override var level: Int = 5, private var _width: Int = 5, private var boxesPadding: Int = 2) :
    MonsterSpawningChunkTerminal("boxes", monsterSpawns) {

    override fun takeChunkParameters(iterator: Iterator<Int>) {
        this.level = iterator.next() % 10 + 2
        this._width = iterator.next() % 4 + 5
        this.boxesPadding = iterator.next() % 2 + 1
    }

    override fun generateChunk(): MarioLevelChunk {
        val pathColumn = ColumnHelpers.getPathColumn(this.level)
        val boxesColumn = ColumnHelpers.getBoxesColumn(this.level, this.level - 4)
        return TerminalMarioLevelChunk(this, Array(this._width) {
            if (it in (0 + this.boxesPadding until this._width - this.boxesPadding))
                boxesColumn
            else
                pathColumn
        }, this.monsterSpawns)
    }

    override val width: Int get() = this._width
    override fun copy(): BoxesChunkTerminal =
        BoxesChunkTerminal(
            this.monsterSpawns,
            this.level,
            this._width,
            this.boxesPadding
        )
    override fun toString(): String = "${this.value}(${this._width},${this.level}.${this.boxesPadding})"
    override fun equals(other: Any?): Boolean = other is BoxesChunkTerminal && other._width == this._width && other.level == this.level && this.boxesPadding == boxesPadding
    override fun hashCode(): Int = javaClass.hashCode() * this._width.hashCode() * this.level.hashCode() * this.boxesPadding.hashCode()
}


class SecretsChunkTerminal(monsterSpawns: Array<MonsterSpawn> = arrayOf(), override var level: Int = 5, private var _width: Int = 5,
                           private var secretsPadding: Int = 2, private var hasPowerUp: Boolean = false, private var powerUpLocation: Int = 0) :
    MonsterSpawningChunkTerminal("secrets", monsterSpawns) {

    override fun takeChunkParameters(iterator: Iterator<Int>) {
        this.level = iterator.next() % 10 + 2
        this._width = iterator.next() % 4 + 5
        this.secretsPadding = iterator.next() % 2 + 1
        this.hasPowerUp = iterator.next() % 5 == 0
        this.powerUpLocation = iterator.next() % this._width - this.secretsPadding * 2
    }

    override fun generateChunk(): MarioLevelChunk {
        val pathColumn = ColumnHelpers.getPathColumn(this.level)
        val secretsColumn = ColumnHelpers.getSecretBoxesColumn(this.level, this.level - 4)

        return TerminalMarioLevelChunk(this, Array(this._width) {
            if (it in (0 + this.secretsPadding until this._width - this.secretsPadding))
                secretsColumn
            else if (hasPowerUp && it == this.secretsPadding + this.powerUpLocation)
                ColumnHelpers.getSecretPowerUpColumn(this.level, this.level - 4)
            else
                pathColumn
        }, this.monsterSpawns)
    }

    override val width: Int get() = this._width
    override fun copy(): SecretsChunkTerminal =
        SecretsChunkTerminal(
            this.monsterSpawns,
            this.level,
            this._width,
            this.secretsPadding
        )
    override fun toString(): String = "${this.value}(${this._width},${this.level}.${this.secretsPadding},${this.hasPowerUp})"
    override fun equals(other: Any?): Boolean = other is SecretsChunkTerminal && other._width == this._width && other.level == this.level && this.secretsPadding == secretsPadding
    override fun hashCode(): Int = javaClass.hashCode() * this._width.hashCode() * this.level.hashCode() * this.secretsPadding.hashCode()

}

class PipeChunkTerminal(private var _width: Int = 4, private var pipeHeight: Int = 3, private var level: Int = 5) : LevelChunkTerminal("pipe") {

    override fun takeParameters(iterator: Iterator<Int>) {
        this._width = (iterator.next() % 3) * 2 + 4
        this.pipeHeight = iterator.next() % 3 + 2
        this.level = iterator.next() % 6 + 7
    }

    override val width: Int
        get() = this._width

    override fun generateChunk(): MarioLevelChunk {
        val pathColumn = ColumnHelpers.getPathColumn(this.level)

        return TerminalMarioLevelChunk(this, Array(this._width) {
            when (it) {
                this._width / 2 -> ColumnHelpers.getPipeEndColumn(this.level, this.pipeHeight)
                this._width / 2 - 1 -> ColumnHelpers.getPipeStartColumn(this.level, this.pipeHeight)
                else -> pathColumn
            }
        }, arrayOf(MonsterSpawn(this._width / 2 - 1, this.level - (this.pipeHeight - 1), Entities.Flower.NORMAL)))
    }

    override fun copy(): LevelChunkTerminal =
        PipeChunkTerminal(
            this._width,
            this.pipeHeight,
            this.level
        )
    override fun toString(): String = "${this.value}(${this._width},${this.level},${this.pipeHeight})"
    override fun equals(other: Any?): Boolean = other is PipeChunkTerminal && other._width == this._width && other.level == this.level && other.pipeHeight == pipeHeight
    override fun hashCode(): Int = javaClass.hashCode() * this._width.hashCode() * this.pipeHeight * this.level

}

class BulletBillChunkTerminal(private var _width: Int = 3, private var billHeight: Int = 3, private var level: Int = 5) : LevelChunkTerminal("bullet_bill") {

    override fun takeParameters(iterator: Iterator<Int>) {
        this._width = (iterator.next() % 2) * 2 + 3
        this.billHeight = iterator.next() % 3 + 2
        this.level = iterator.next() % 6 + 7
    }

    override val width: Int
        get() = this._width

    override fun generateChunk(): MarioLevelChunk {
        val pathColumn = ColumnHelpers.getPathColumn(this.level)

        return TerminalMarioLevelChunk(this, Array(this._width) {
            when (it) {
                this._width / 2 -> ColumnHelpers.getBlasterBulletBillColumn(this.level, this.billHeight)
                else -> pathColumn
            }
        }, arrayOf(MonsterSpawn(this._width / 2, this.level - (this.billHeight), Entities.BulletBill.NORMAL)))
    }

    override fun copy(): LevelChunkTerminal =
        BulletBillChunkTerminal(
            this._width,
            this.billHeight,
            this.level
        )
    override fun toString(): String = "${this.value}(${this._width},${this.level},${this.billHeight})"
    override fun equals(other: Any?): Boolean = other is BulletBillChunkTerminal && other._width == this._width && other.level == this.level && other.billHeight == billHeight
    override fun hashCode(): Int = javaClass.hashCode() * this._width.hashCode() * this.billHeight * this.level

}

class StairChunkTerminal(monsterSpawns: Array<MonsterSpawn> = arrayOf(), private var _width: Int = 3, override var level: Int = 5) :
    MonsterSpawningChunkTerminal("stairs", monsterSpawns) {

    override fun takeChunkParameters(iterator: Iterator<Int>) {
        this._width = (iterator.next() % 4) + 3 + 2
        this.level = iterator.next() % 6 + 8
    }

    override val width: Int
        get() = this._width

    override val monstersLevel: Int get() = this.level - this._width + 1

    override fun generateChunk(): MarioLevelChunk {
        val pathColumn = ColumnHelpers.getPathColumn(this.level)

        return TerminalMarioLevelChunk(this, Array(this._width) {
            when (it) {
                0 or this._width - 1 -> pathColumn
                else -> ColumnHelpers.getStonesColumn(this.level, it)
            }
        }, this.monsterSpawns)
    }

    override fun copy(): LevelChunkTerminal =
        StairChunkTerminal(
            this.monsterSpawns,
            this.width,
            this.level
        )
    override fun toString(): String = "${this.value}(${this._width},${this.level})"
    override fun equals(other: Any?): Boolean = other is StairChunkTerminal && other._width == this._width && other.level == this.level
    override fun hashCode(): Int = javaClass.hashCode() * this._width.hashCode() * this.level

}

class DoublePathChunkTerminal(monsterSpawns: Array<MonsterSpawn> = arrayOf(), private var _width: Int = 3, override var level: Int = 5,
                              private var firstLevelPadding: Int = 1, private var secondLevelPadding: Int = 0,
                              private var isFirstSecrets: Boolean = false, private var isSecondSecrets: Boolean = true) :
    MonsterSpawningChunkTerminal("two_paths", monsterSpawns) {

    override fun takeChunkParameters(iterator: Iterator<Int>) {
        this._width = iterator.next() % 7 + 3
        this.level = iterator.next() % 6 + 7
        this.firstLevelPadding = iterator.next() % 2 + 1
        this.secondLevelPadding = iterator.next() % 3
        this.isFirstSecrets = iterator.next() % 100 < 50
        this.isSecondSecrets = iterator.next() % 100 < 50
    }

    override val width: Int
        get() = this._width

    override fun generateChunk(): MarioLevelChunk {
        val pathColumn = ColumnHelpers.getPathColumn(this.level)
        val oneLevelColumn = ColumnHelpers.getBoxesColumn(this.level, this.level - 4)
        val firstLevelBlockType = if (this.isFirstSecrets) Tiles.QM_WITH_COIN else Tiles.BRICK
        val secondLevelBlockType = if (this.isSecondSecrets) Tiles.QM_WITH_COIN else Tiles.BRICK
        val twoLevelsColumn = ColumnHelpers.getColumnWithTwoBlocks(this.level, this.level - 4, firstLevelBlockType, this.level - 8, secondLevelBlockType)

        return TerminalMarioLevelChunk(this, Array(this._width) {
            when (it) {
                in 0..firstLevelPadding -> pathColumn
                in firstLevelPadding..(firstLevelPadding + secondLevelPadding) -> oneLevelColumn
                in (firstLevelPadding + secondLevelPadding)..(_width - firstLevelPadding - secondLevelPadding) -> twoLevelsColumn
                in (_width - firstLevelPadding - secondLevelPadding)..(_width - firstLevelPadding) -> oneLevelColumn
                in (_width - firstLevelPadding).._width -> pathColumn
                else -> pathColumn
            }
        }, this.monsterSpawns)
    }

    override fun copy(): LevelChunkTerminal =
        DoublePathChunkTerminal(
            monsterSpawns,
            width,
            level,
            firstLevelPadding,
            secondLevelPadding,
            isFirstSecrets,
            isSecondSecrets
        )
    override fun toString(): String = "$value($_width,$level,$firstLevelPadding,$secondLevelPadding,$isFirstSecrets,$isSecondSecrets)"
    override fun equals(other: Any?): Boolean = other is DoublePathChunkTerminal && other._width == this._width && other.level == this.level
            && other.firstLevelPadding == this.firstLevelPadding && other.secondLevelPadding == this.secondLevelPadding
            && other.isFirstSecrets == this.isFirstSecrets && other.isSecondSecrets == this.isSecondSecrets
    override fun hashCode(): Int = javaClass.hashCode() * _width.hashCode() * level * firstLevelPadding * secondLevelPadding

}