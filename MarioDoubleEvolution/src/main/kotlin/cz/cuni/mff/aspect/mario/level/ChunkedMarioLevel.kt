package cz.cuni.mff.aspect.mario.level

import cz.cuni.mff.aspect.evolution.levels.ge.algorithm.Terminal

/**
 * Implementation of [MarioLevel] where the level is represented in multiple chunks connected together. The chunkd are
 * represented by [MarioLevelChunk].
 */
class ChunkedMarioLevel(chunks: Array<MarioLevelChunk>) : MarioLevel {

    override val tiles: Array<ByteArray>
    override val entities: Array<IntArray>

    init {
        val totalWidth = chunks.sumBy { it.width }

        var currentColumn = 0
        var currentChunkIndex = 0
        tiles = Array(totalWidth) {
            if (currentColumn == chunks[currentChunkIndex].width) {
                currentColumn = 0
                currentChunkIndex++
            }

            val nextColumn = chunks[currentChunkIndex].getColumn(currentColumn)
            currentColumn++
            nextColumn
        }
    }

    init {
        val totalWidth = chunks.sumBy { it.width }
        entities = Array(totalWidth) { IntArray(15) { 0 } }

        var currentChunkStart = 0
        chunks.forEach { chunk ->
            val enemySpawns = chunk.getMonsterSpawns()
            enemySpawns.forEach {
                entities[currentChunkStart + it.xPos][it.yPos] = it.monsterType
            }

            currentChunkStart += chunk.width
        }
    }

}

/** Interface representing a Super Mario level chunk. */
interface MarioLevelChunk {

    /** Width of this chunk in tiles. */
    val width: Int

    /** Get monster spawns in this chunk. */
    fun getMonsterSpawns(): Array<MonsterSpawn>

    /** Gets tiles in the given column of this chunk. */
    fun getColumn(index: Int): ByteArray

}

/** Implementation of [MarioLevelChunk] where all the necessary data is specified via primary constructor. */
open class ArrayMarioLevelChunk(private val columns: Array<ByteArray>, private val monsterSpawns: Array<MonsterSpawn>) : MarioLevelChunk {

    override val width: Int = columns.size
    override fun getColumn(index: Int): ByteArray = columns[index]
    override fun getMonsterSpawns(): Array<MonsterSpawn> = monsterSpawns

}

/** Representation of a Mario Chunk which was created by a grammar terminal. */
class TerminalMarioLevelChunk(val terminal: Terminal, columns: Array<ByteArray>, monsterSpawns: Array<MonsterSpawn>) :
    ArrayMarioLevelChunk(columns, monsterSpawns)

/**
 * Representation of a monster spawn in a chunk.
 *
 * @param xPos x position in the chunk.
 * @param yPos y position in the chunk.
 * @param monsterType type of the monster to be spawned.
 */
data class MonsterSpawn(val xPos: Int, val yPos: Int, val monsterType: Int)

