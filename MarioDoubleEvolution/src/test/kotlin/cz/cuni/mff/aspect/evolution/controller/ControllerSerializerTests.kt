package cz.cuni.mff.aspect.evolution.controller

import com.evo.NEAT.Genome
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.HiddenLayerControllerNetwork
import cz.cuni.mff.aspect.mario.controllers.ann.networks.NeatAgentNetwork
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class ControllerSerializerTests {

    @Test
    fun `test hidden layer controller serialize and deserialize`() {
        val random = Random(26270)
        val originalNetworkSettings = NetworkSettings(5, 7, 2,
            -1, 10, denseInput = true, oneHotOnEnemies = false)
        val network = HiddenLayerControllerNetwork(originalNetworkSettings)

        val originalNetworkWeights = DoubleArray(network.weightsCount) { random.nextDouble() }
        network.setNetworkWeights(originalNetworkWeights)

        val controller = SimpleANNController(network)

        val serializedController = ControllerSerializer.serialize(controller)
        val deserializedController = ControllerSerializer.deserialize(serializedController)

        MatcherAssert.assertThat(deserializedController, Matchers.instanceOf(SimpleANNController::class.java))
        val typedController = deserializedController as SimpleANNController

        MatcherAssert.assertThat(typedController.network, Matchers.instanceOf(HiddenLayerControllerNetwork::class.java))
        val typedNetwork = typedController.network as HiddenLayerControllerNetwork

        assertEquals(originalNetworkSettings, typedNetwork.networkSettings, "The deserialized settings should be the same as original")
        repeat(originalNetworkWeights.size) {
            assertEquals(originalNetworkWeights[it], typedNetwork.getNetworkWeights()[it], 0.000001, "The deserialized weights should be equal")
        }
    }

    @Test
    fun `test neat controller serialize and deserialize`() {
        val random = Random(26270)
        val mockedRandom = mockk<Random>()
        every { mockedRandom.nextBoolean() } returns random.nextBoolean()
        every { mockedRandom.nextFloat() } returns random.nextFloat()

        val originalNetworkSettings = NetworkSettings(5, 7, 2,
            -1, 10, denseInput = true, oneHotOnEnemies = false)
        val inputsCount = 20
        val outputsCount = 5
        var originalGenome = Genome(inputsCount, outputsCount)
        repeat(250) { originalGenome.Mutate() }

        val originalNetwork = NeatAgentNetwork(originalNetworkSettings, originalGenome)

        val controller = SimpleANNController(originalNetwork)
        val serializedController = ControllerSerializer.serialize(controller)
        val deserializedController = ControllerSerializer.deserialize(serializedController)

        MatcherAssert.assertThat(deserializedController, Matchers.instanceOf(SimpleANNController::class.java))
        val typedController = deserializedController as SimpleANNController

        MatcherAssert.assertThat(typedController.network, Matchers.instanceOf(NeatAgentNetwork::class.java))
        val typedNetwork = typedController.network as NeatAgentNetwork

        assertEquals(originalNetworkSettings, typedNetwork.networkSettings, "The deserialized settings should be the same as original")

        val deserializedGenome = typedNetwork.genome
        repeat(10) {
            val randomInput = FloatArray(inputsCount) { random.nextFloat() }
            assertEquals(originalGenome.evaluateNetwork(randomInput).toList(), deserializedGenome.evaluateNetwork(randomInput).toList(),
                "The original and deserialized genomes should return the same value on same inputs")
        }
    }

}