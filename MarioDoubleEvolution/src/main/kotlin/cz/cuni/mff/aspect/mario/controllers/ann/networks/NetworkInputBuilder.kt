package cz.cuni.mff.aspect.mario.controllers.ann.networks

import ch.idsia.agents.controllers.modules.Entities
import ch.idsia.agents.controllers.modules.Tiles
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity


data class NetworkInputBuilder(
    private var tiles: Tiles? = null,
    private var entities: Entities? = null,
    private var mario: MarioEntity? = null,
    private var receptiveFieldRows: Int = 5,
    private var receptiveFieldColumns: Int = 5,
    private var receptiveFieldOffsetRows: Int = 0,
    private var receptiveFieldOffsetColumns: Int = 0,
    private var denseInput: Boolean = false
) {

    class NetworkInputBuilderException(error: String) : Exception(error)

    fun tiles(tiles: Tiles) = apply { this.tiles = tiles }
    fun entities(entities: Entities) = apply { this.entities = entities }
    fun mario(mario: MarioEntity) = apply { this.mario = mario }
    fun receptiveFieldSize(rows: Int, columns: Int) = apply { this.receptiveFieldRows = rows; this.receptiveFieldColumns = columns }
    fun receptiveFieldOffset(rows: Int, columns: Int) = apply { this.receptiveFieldOffsetRows = rows; this.receptiveFieldOffsetColumns = columns }
    fun useDenserInput() = apply { this.denseInput = true }

    private val receptiveFieldSize: Int get() = this.receptiveFieldRows * this.receptiveFieldColumns * if (this.denseInput) 4 else 1
    private val inputLayerSize: Int get() = this.receptiveFieldSize * 2

    // TODO: DRY!!!!!
    fun build(): IntArray {
        val (flatTiles, flatEntities, inputLayerSize) = this.createFlatArrays()

        return IntArray(inputLayerSize) {
            when {
                it >= flatEntities.size -> flatTiles[it - flatEntities.size]
                else -> flatEntities[it]
            }
        }
    }

    fun buildFloat(): FloatArray {
        val (flatTiles, flatEntities, inputLayerSize) = this.createFlatArrays()

        return FloatArray(inputLayerSize) {
            when {
                it >= flatEntities.size -> flatTiles[it - flatEntities.size].toFloat()
                else -> flatEntities[it].toFloat()
            }
        }
    }

    fun buildDouble(): DoubleArray {
        val (flatTiles, flatEntities, inputLayerSize) = this.createFlatArrays()

        return DoubleArray(inputLayerSize) {
            when {
                it >= flatEntities.size -> flatTiles[it - flatEntities.size].toDouble()
                else -> flatEntities[it].toDouble()
            }
        }
    }

    private fun createFlatArrays(): Triple<IntArray, IntArray, Int> {
        this.ensureHasInput()
        return Triple(this.createFlatTiles(), this.createFlatEntities(), this.inputLayerSize)
    }

    private fun ensureHasInput() {
        if (this.tiles == null) throw NetworkInputBuilderException("'Tiles' not specified")
        if (this.entities == null) throw NetworkInputBuilderException("'Entities' not specified")
        if (this.mario == null) throw NetworkInputBuilderException("'Mario' not specified")
    }

    private fun createFlatTiles(): IntArray {
        val flatTilesSize = this.receptiveFieldSize
        // TODO: this would be better if we initialize it directly to the required values
        val flatTiles = IntArray(flatTilesSize) { 0 }

        this.iterateOverReceptiveField { index, row, column ->
            val tileAtPosition = this.tiles!!.tileField[row][column]

            val tileCode = if (tileAtPosition.code != 0) 1 else 0
            flatTiles[index] = tileCode
        }

        return flatTiles
    }

    private fun createFlatEntities(): IntArray {
        val flatEntitiesSize = this.receptiveFieldSize
        // TODO: this would be better if we initialize it directly to the required values
        val flatEntities = IntArray(flatEntitiesSize) { 0 }

        this.iterateOverReceptiveField { index, row, column ->
            val entitiesAtPosition = this.entities!!.entityField[row][column]
            flatEntities[index] = if (entitiesAtPosition.size > 0) 1 else 0
        }

        return flatEntities
    }

    private fun iterateOverReceptiveField(callback: (Int, Int, Int) -> Unit) {
        val receptiveFieldRowMiddle: Int = this.receptiveFieldRows / 2
        val receptiveFieldColumnMiddle: Int = this.receptiveFieldColumns / 2
        val marioX = this.mario!!.egoCol
        val marioY = this.mario!!.egoRow

        if (this.denseInput) {
            this.iterateOverDenseReceptiveField(marioX, marioY, receptiveFieldColumnMiddle, receptiveFieldRowMiddle, callback)
        } else {
            this.iterateOverOriginalReceptiveField(marioX, marioY, receptiveFieldColumnMiddle, receptiveFieldRowMiddle, callback)
        }
    }

    private fun iterateOverDenseReceptiveField(marioX: Int, marioY: Int, receptiveFieldColumnMiddle: Int,
                                               receptiveFieldRowMiddle: Int, callback: (Int, Int, Int) -> Unit) {
        var index = 0
        for (row in 0 until this.receptiveFieldRows * 2) {
            val refinedRow = if (row % 2 == 1 && this.mario!!.inTileY >= 8) (row / 2) + 1 else (row / 2)
            val offsetRow = refinedRow - receptiveFieldRowMiddle + this.receptiveFieldOffsetRows
            for (column in 0 until this.receptiveFieldColumns * 2) {
                val refinedColumn = if (column % 2 == 1 && this.mario!!.inTileX >= 8) (column / 2) + 1 else (column / 2)
                val offsetColumn = refinedColumn - receptiveFieldColumnMiddle + this.receptiveFieldOffsetColumns
                callback(index, marioY + offsetRow, marioX + offsetColumn)
                index++
            }
        }
    }

    private fun iterateOverOriginalReceptiveField(marioX: Int, marioY: Int, receptiveFieldColumnMiddle: Int,
                                                  receptiveFieldRowMiddle: Int, callback: (Int, Int, Int) -> Unit) {
        var index = 0
        for (row in 0 until this.receptiveFieldRows) {
            val offsetRow = row - receptiveFieldRowMiddle + this.receptiveFieldOffsetRows
            for (column in 0 until this.receptiveFieldColumns) {
                val offsetColumn = column - receptiveFieldColumnMiddle + this.receptiveFieldOffsetColumns
                callback(index, marioY + offsetRow, marioX + offsetColumn)
                index++
            }
        }
    }

}