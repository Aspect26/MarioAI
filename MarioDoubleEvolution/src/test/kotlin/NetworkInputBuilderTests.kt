import ch.idsia.agents.controllers.modules.Entities
import ch.idsia.agents.controllers.modules.Tiles
import ch.idsia.benchmark.mario.engine.generalization.Entity
import ch.idsia.benchmark.mario.engine.generalization.EntityType
import ch.idsia.benchmark.mario.engine.generalization.MarioEntity
import ch.idsia.benchmark.mario.engine.generalization.Tile
import ch.idsia.benchmark.mario.engine.sprites.Sprite
import cz.cuni.mff.aspect.mario.controllers.ann.networks.NetworkInputBuilder
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkInputBuilderTests {

    @Test
    fun `test tiles - mario in middle`() {
        val networkBuilder = this.givenBuilderWithTiles(arrayOf (
            arrayOf(Tile.NOTHING, Tile.NOTHING, Tile.NOTHING),
            arrayOf(Tile.NOTHING, Tile.NOTHING, Tile.BRICK),
            arrayOf(Tile.BRICK, Tile.BRICK, Tile.BRICK)
        ), marioPosition = Pair(1, 1))

        val input = networkBuilder.build()

        val expectedTilesResult = arrayOf(
            0, 0, 0,
            0, 0, 1,
            1, 1, 1
        )

        this.assertInputTilesEqual(input, expectedTilesResult)
    }

    @Test
    fun `test tiles - mario not in middle`() {
        val networkBuilder = this.givenBuilderWithTiles(arrayOf (
            arrayOf(Tile.NOTHING, Tile.NOTHING, Tile.NOTHING, Tile.NOTHING),
            arrayOf(Tile.NOTHING, Tile.NOTHING, Tile.BRICK, Tile.NOTHING),
            arrayOf(Tile.BRICK, Tile.BRICK, Tile.BRICK, Tile.QUESTION_BRICK)
        ), marioPosition = Pair(2, 1))

        val input = networkBuilder.build()

        val expectedTilesResult = arrayOf(
            0, 0, 0,
            0, 1, 0,
            1, 1, 1
        )

        this.assertInputTilesEqual(input, expectedTilesResult)
    }

    @Test
    fun `test dense tiles - mario aligned`() {
        val networkBuilder = this.givenDenseBuilderWithTiles(arrayOf (
            arrayOf(Tile.NOTHING, Tile.NOTHING, Tile.NOTHING),
            arrayOf(Tile.NOTHING, Tile.NOTHING, Tile.BRICK),
            arrayOf(Tile.BRICK, Tile.BRICK, Tile.BRICK)
        ), marioPosition = Pair(1, 1), marioInTilePosition = Pair(0, 0))

        val input = networkBuilder.build()

        val expectedTilesResult = arrayOf(
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 1,
            0, 0, 0, 0, 1, 1,
            1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1
        )

        this.assertInputTilesEqual(input, expectedTilesResult)
    }

    @Test
    fun `test dense tiles - mario not aligned X`() {
        val networkBuilder = this.givenDenseBuilderWithTiles(arrayOf (
            arrayOf(Tile.NOTHING, Tile.NOTHING, Tile.NOTHING, Tile.NOTHING),
            arrayOf(Tile.NOTHING, Tile.NOTHING, Tile.BRICK, Tile.NOTHING),
            arrayOf(Tile.BRICK, Tile.BRICK, Tile.BRICK, Tile.BRICK)
        ), marioPosition = Pair(1, 1), marioInTilePosition = Pair(10, 0))

        val input = networkBuilder.build()

        val expectedTilesResult = arrayOf(
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 0,
            0, 0, 0, 1, 1, 0,
            1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1
        )

        this.assertInputTilesEqual(input, expectedTilesResult)
    }

    @Test
    fun `test dense tiles - mario not aligned Y`() {
        val networkBuilder = this.givenDenseBuilderWithTiles(arrayOf (
            arrayOf(Tile.NOTHING, Tile.NOTHING, Tile.NOTHING),
            arrayOf(Tile.NOTHING, Tile.NOTHING, Tile.BRICK),
            arrayOf(Tile.BRICK, Tile.BRICK, Tile.BRICK),
            arrayOf(Tile.BRICK, Tile.BRICK, Tile.NOTHING)
        ), marioPosition = Pair(1, 1), marioInTilePosition = Pair(0, 10))

        val input = networkBuilder.build()

        val expectedTilesResult = arrayOf(
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 1, 1,
            0, 0, 0, 0, 1, 1,
            1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 0, 0
        )

        this.assertInputTilesEqual(input, expectedTilesResult)
    }

    @Test
    fun `test dense entities - mario aligned`() {
        val goombaEntity = Entity<Sprite>(null, EntityType.GOOMBA, 0, 0, 0f, 0f, 0f)

        val networkBuilder = this.givenDenseBuilderWithEntities(arrayOf (
            arrayOf(listOf(goombaEntity), emptyList(), emptyList()),
            arrayOf(emptyList(), emptyList(), emptyList()),
            arrayOf(emptyList(), emptyList(), emptyList())
        ), marioPosition = Pair(1, 1), marioInTilePosition = Pair(0, 0))

        val input = networkBuilder.build()

        val expectedEntitiesResult = arrayOf(
            1, 1, 0, 0, 0, 0,
            1, 1, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0
        )

        this.assertInputEntitiesEqual(input, expectedEntitiesResult)
    }

    @Test
    fun `test dense entities - mario not aligned`() {
        val goombaEntity = Entity<Sprite>(null, EntityType.GOOMBA, 0, 0, 0f, 0f, 0f)

        val networkBuilder = this.givenDenseBuilderWithEntities(arrayOf (
            arrayOf(listOf(goombaEntity), emptyList(), emptyList(), listOf(goombaEntity)),
            arrayOf(emptyList(), emptyList(), emptyList(), listOf(goombaEntity)),
            arrayOf(emptyList(), emptyList(), emptyList(), emptyList()),
            arrayOf(emptyList(), listOf(goombaEntity), emptyList(), emptyList())
        ), marioPosition = Pair(1, 1), marioInTilePosition = Pair(10, 10))

        val input = networkBuilder.build()

        val expectedEntitiesResult = arrayOf(
            1, 0, 0, 0, 0, 1,
            0, 0, 0, 0, 0, 1,
            0, 0, 0, 0, 0, 1,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 1, 1, 0, 0, 0
        )

        this.assertInputEntitiesEqual(input, expectedEntitiesResult)
    }

    private fun givenBuilderWithTiles(tilesArray: Array<Array<Tile>>, marioPosition: Pair<Int, Int> = Pair(1, 1)): NetworkInputBuilder {
        val tiles = Tiles()
        tiles.tileField = tilesArray

        val entities = givenEmptyEntities(Pair(tiles.tileField.size, tiles.tileField[0].size))
        val mario = this.givenMario(marioPosition, Pair(0, 0))

        return this.givenNetworkInputBuilder(tiles, entities, mario, false)
    }

    private fun givenDenseBuilderWithTiles(tilesArray: Array<Array<Tile>>, marioPosition: Pair<Int, Int> = Pair(1, 1), marioInTilePosition: Pair<Int, Int> = Pair(0, 0)): NetworkInputBuilder {
        val tiles = Tiles()
        tiles.tileField = tilesArray

        val entities = givenEmptyEntities(Pair(tiles.tileField.size, tiles.tileField[0].size))
        val mario = this.givenMario(marioPosition, marioInTilePosition)

        return this.givenNetworkInputBuilder(tiles, entities, mario, true)
    }

    private fun givenDenseBuilderWithEntities(entitiesArray: Array<Array<List<Entity<Sprite>>>>, marioPosition: Pair<Int, Int> = Pair(1, 1), marioInTilePosition: Pair<Int, Int> = Pair(0, 0)): NetworkInputBuilder {
        val entities = Entities()
        entities.entityField = entitiesArray

        val tiles = Tiles()
        tiles.tileField = Array(entities.entityField.size) {
            Array(entities.entityField[it].size) {
                Tile.NOTHING
            }
        }

        val mario = this.givenMario(marioPosition, marioInTilePosition)
        return this.givenNetworkInputBuilder(tiles, entities, mario, true)
    }

    private fun givenEmptyEntities(size: Pair<Int, Int>): Entities {
        val entities = Entities()
        entities.entityField = Array(size.first) {
            Array<List<Entity<Sprite>>>(size.second) {
                emptyList()
            }
        }

        return entities
    }

    private fun givenMario(marioPosition: Pair<Int, Int>, marioInTilePosition: Pair<Int, Int>): MarioEntity {
        val mario = MarioEntity()
        mario.egoCol = marioPosition.first
        mario.egoRow = marioPosition.second
        mario.inTileX = marioInTilePosition.first
        mario.inTileY = marioInTilePosition.second

        return mario
    }

    private fun givenNetworkInputBuilder(tiles: Tiles, entities: Entities, mario: MarioEntity, useDenseInput: Boolean): NetworkInputBuilder {
        val builder = NetworkInputBuilder()
            .tiles(tiles)
            .entities(entities)
            .mario(mario)
            .receptiveFieldOffset(0, 0)
            .receptiveFieldSize(3, 3)

        if (useDenseInput)
            builder.useDenserInput()

        return builder
    }

    private fun assertInputTilesEqual(input: IntArray, expectedTiles: Array<Int>) {
        val receptiveFieldGridSize = expectedTiles.size

        for (index in (0 until receptiveFieldGridSize)) {
            // The entities grid comes first in the input
            assertEquals("Created input is not as expected at index $input", expectedTiles[index], input[index + receptiveFieldGridSize])
        }
    }

    private fun assertInputEntitiesEqual(input: IntArray, expectedEntities: Array<Int>) {
        val receptiveFieldGridSize = expectedEntities.size

        for (index in (0 until receptiveFieldGridSize)) {
            assertEquals("Created input is not as expected at index $input", expectedEntities[index], input[index])
        }
    }

}