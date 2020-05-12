package cz.cuni.mff.aspect.mario.controllers.ann.networks

import ch.idsia.agents.controllers.modules.Entities
import ch.idsia.agents.controllers.modules.Tiles
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings


data class NetworkInputBuilder(
    private var tiles: Tiles? = null,
    private var entities: Entities? = null,
    private var mario: MarioEntity? = null,
    private var receptiveFieldRows: Int = 5,
    private var receptiveFieldColumns: Int = 5,
    private var receptiveFieldOffsetRows: Int = 0,
    private var receptiveFieldOffsetColumns: Int = 0,
    private var addMarioInTilePosition: Boolean = false,
    private var denseInput: Boolean = false
) {

    class NetworkInputBuilderException(error: String) : Exception(error)

    fun tiles(tiles: Tiles) = apply { this.tiles = tiles }
    fun entities(entities: Entities) = apply { this.entities = entities }
    fun mario(mario: MarioEntity) = apply { this.mario = mario }
    fun receptiveFieldSize(rows: Int, columns: Int) = apply { this.receptiveFieldRows = rows; this.receptiveFieldColumns = columns }
    fun receptiveFieldOffset(rows: Int, columns: Int) = apply { this.receptiveFieldOffsetRows = rows; this.receptiveFieldOffsetColumns = columns }
    fun addMarioInTilePosition() = apply { this.addMarioInTilePosition = false }
    fun useDenseInput(useIt: Boolean) = apply { this.denseInput = useIt }

    fun buildDouble(): DoubleArray = this.build().map { it.toDouble() }.toDoubleArray()

    fun buildFloat(): FloatArray = this.build().map { it.toFloat() }.toFloatArray()

    fun build(): IntArray {
        val (flatTiles, flatEntities, inputLayerSize) = this.createFlatArrays()

        return IntArray(inputLayerSize) {
            when {
                this.addMarioInTilePosition && it == inputLayerSize - 1 -> this.mario!!.inTileX
                this.addMarioInTilePosition && it == inputLayerSize - 2 -> this.mario!!.inTileY
                it >= flatEntities.size -> flatTiles[it - flatEntities.size]
                else -> flatEntities[it]
            }
        }
    }

    private fun createFlatArrays(): Triple<IntArray, IntArray, Int> {
        this.checkInput()
        return Triple(
            this.createFlatTiles(),
            this.createFlatEntities(),
            inputSize(this.receptiveFieldRows, this.receptiveFieldColumns, this.denseInput, this.addMarioInTilePosition))
    }

    private fun checkInput() {
        if (this.tiles == null) throw NetworkInputBuilderException("'Tiles' not specified")
        if (this.entities == null) throw NetworkInputBuilderException("'Entities' not specified")
        if (this.mario == null) throw NetworkInputBuilderException("'Mario' not specified")
    }

    private fun createFlatTiles(): IntArray {
        val flatTilesSize = receptiveFieldSize(this.receptiveFieldRows, this.receptiveFieldColumns, this.denseInput)
        val flatTiles = IntArray(flatTilesSize) { 0 }

        this.iterateOverReceptiveField { index, row, column ->
            val tileAtPosition = this.tiles!!.tileField[row][column]

            val tileCode = when (tileAtPosition.code) {
                -60 -> -1
                else -> tileAtPosition.code
            }
            flatTiles[index] = tileCode
        }

        return flatTiles
    }

    private fun createFlatEntities(): IntArray {
        val flatEntitiesSize = receptiveFieldSize(this.receptiveFieldRows, this.receptiveFieldColumns, this.denseInput)
        val flatEntities = IntArray(flatEntitiesSize) { 0 }

        this.iterateOverReceptiveField { index, row, column ->
            val entitiesAtPosition = this.entities!!.entityField[row][column]
            flatEntities[index] = if (entitiesAtPosition.size > 0) entitiesAtPosition[0].type.code else 0
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

    companion object {
        private fun receptiveFieldSize(receptiveFieldRows: Int, receptiveFieldColumns: Int, denseInput: Boolean): Int =
            receptiveFieldRows * receptiveFieldColumns * if (denseInput) 4 else 1

        fun inputSize(networkSettings: NetworkSettings, marioInTilePosition: Boolean): Int =
            inputSize(networkSettings.receptiveFieldSizeRow, networkSettings.receptiveFieldSizeColumn, networkSettings.denseInput, marioInTilePosition)

        fun inputSize(receptiveFieldRows: Int, receptiveFieldColumns: Int, denseInput: Boolean, addMarioInTilePosition: Boolean): Int =
            receptiveFieldSize(receptiveFieldRows, receptiveFieldColumns, denseInput) * 2 + if (addMarioInTilePosition) 2 else 0

    }

}