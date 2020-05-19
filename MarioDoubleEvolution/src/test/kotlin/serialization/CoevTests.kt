package serialization

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.controllers.GoingRightAndJumpingController
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.storage.ObjectStorage
import org.junit.jupiter.api.Test

class CoevTests {

    @Test
    fun `test coevolution experiments resulting AIs`() {
        val anyLevel = PCLevelGenerator().generate()
        val simulator = GameSimulator(10)

        assertControllerIsPlayable("1_10-sl-window/neuro_pc", simulator, anyLevel)
        assertControllerIsPlayable("2_better-coev-params/neuro_pc", simulator, anyLevel)
        assertControllerIsPlayable("3_increased-levels-on-lg-evaluation/neuro_pc", simulator, anyLevel)
        assertControllerIsPlayable("4_lg_with_huffman/neuro_pc", simulator, anyLevel)
        assertControllerIsPlayable("5_chart_update/neuro_pc", simulator, anyLevel)
        assertControllerIsPlayable("6_all_fitness_lg/neuro_pc", simulator, anyLevel)
        assertControllerIsPlayable("7_enemies_one_hot/neuro_pc", simulator, anyLevel)
        assertControllerIsPlayable("8_dense_input/neuro_pc", simulator, anyLevel)
        assertControllerIsPlayable("9_neat/neat_pc", simulator, anyLevel)
    }

    @Test
    fun `test coevolution experiments resulting LGs`() {
        val anyAgent = { MarioAgent(GoingRightAndJumpingController()) }
        val simulator = GameSimulator(10)

        assertLevelGeneratorIsPlayable("1_10-sl-window/neuro_pc", simulator, anyAgent)
        assertLevelGeneratorIsPlayable("2_better-coev-params/neuro_pc", simulator, anyAgent)
        assertLevelGeneratorIsPlayable("3_increased-levels-on-lg-evaluation/neuro_pc", simulator, anyAgent)
        assertLevelGeneratorIsPlayable("4_lg_with_huffman/neuro_pc", simulator, anyAgent)
        assertLevelGeneratorIsPlayable("5_chart_update/neuro_pc", simulator, anyAgent)
        assertLevelGeneratorIsPlayable("6_all_fitness_lg/neuro_pc", simulator, anyAgent)
        assertLevelGeneratorIsPlayable("7_enemies_one_hot/neuro_pc", simulator, anyAgent)
        assertLevelGeneratorIsPlayable("8_dense_input/neuro_pc", simulator, anyAgent)
        assertLevelGeneratorIsPlayable("9_neat/neat_pc", simulator, anyAgent)
    }

    private fun assertControllerIsPlayable(dirPath: String, simulator: GameSimulator, level: MarioLevel) {
        // we assert here that no exception is thrown
        listOf(1, 15, 20).forEach {
            val currentController = ObjectStorage.load("../data/coev/$dirPath/ai_$it.ai") as MarioController
            simulator.playMario(currentController, level, false)
        }
    }

    private fun assertLevelGeneratorIsPlayable(dirPath: String, simulator: GameSimulator, agentFactory: () -> IAgent) {
        // we assert here that no exception is thrown
        listOf(1, 15, 20).forEach {
            val currentLevelGenerator = ObjectStorage.load("../data/coev/$dirPath/lg_$it.lg") as LevelGenerator
            simulator.playMario(agentFactory(), currentLevelGenerator.generate(), false)
        }
    }

}