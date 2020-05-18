package serialization

import cz.cuni.mff.aspect.evolution.levels.pc.PCLevelGenerator
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.storage.ObjectStorage
import org.junit.jupiter.api.Test

class ControllerTests {

    @Test
    fun test_TryPlayWithCoevResults() {
        val anyLevel = PCLevelGenerator().generate()
        val simulator = GameSimulator(10)

        assertControllerIsPlayable("1_10-sl-window", simulator, anyLevel)
        assertControllerIsPlayable("2_better-coev-params", simulator, anyLevel)
        assertControllerIsPlayable("3_increased-levels-on-lg-evaluation", simulator, anyLevel)
        assertControllerIsPlayable("4_lg_with_huffman", simulator, anyLevel)
        assertControllerIsPlayable("5_chart_update", simulator, anyLevel)
        assertControllerIsPlayable("6_all_fitness_lg", simulator, anyLevel)
        assertControllerIsPlayable("7_enemies_one_hot", simulator, anyLevel)
        assertControllerIsPlayable("8_dense_input", simulator, anyLevel)
    }

    private fun assertControllerIsPlayable(dirPath: String, simulator: GameSimulator, level: MarioLevel) {
        // TODO: we assert here that no exception is thrown
        val currentController = ObjectStorage.load("../data/coev/$dirPath/neuro_pc/ai_${15}.ai") as MarioController
        simulator.playMario(currentController, level, false)
    }

}