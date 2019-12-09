package cz.cuni.mff.aspect.evolution

import ch.idsia.agents.IAgent
import cz.cuni.mff.arnold.ArnoldRuleBasedAgent
import cz.cuni.mff.aspect.MyRuleBasedAgent
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.storage.ObjectStorage

object Agents {

    object RuleBased {

        // My implementation from AI course
        val my: IAgent get() = MyRuleBasedAgent()

        // Jakub Arnold's implementation from AI course
        val arnold: IAgent get() = ArnoldRuleBasedAgent()

    }

    object NeuroEvolution {

        val stage2Level1Solver: IAgent
            get() {
                // best S2S, doesn't solve everything, but does some nice things and solves PathWithHoles level
                val controller = ObjectStorage.load("experiments/Phase I - AI/Gaussian test evaluation - S2S/NeuroEvolution, Mutator 0.45_ai.ai") as SimpleANNController
                controller.setLegacy()

                return MarioAgent(controller)
            }

        val stage4Level1Solver: IAgent
            get() {
                // experiments/Phase I - AI/Gaussian test evaluation - S4S/NeuroEvolution, Mutator 0.25_ai.ai   // jumpee
                // experiments/Phase I - AI/Gaussian test evaluation - S4S (3)/NeuroEvolution, Mutator 0.1_ai.ai // non jumpee
                // experiments/Phase I - AI/Hidden layer test evaluation - S4S/NeuroEvolution, hidden layer size 5_ai.ai // non jumpee
                // experiments/Phase I - AI/Gaussian test evaluation - S4S - Population 100/NeuroEvolution, Mutator 0.10_ai.ai  // solves also PathWithHoles but fails on one part of S4S
                // experiments/Phase I - AI/Hidden layer test evaluation - S4S/NeuroEvolution, hidden layer size 15_ai.ai  // solves also PathWithHoles nicely but fails on one part of S4S and is overfited
                val controller = ObjectStorage.load("experiments/Phase I - AI/Hidden layer test evaluation - S4S/NeuroEvolution, hidden layer size 5_ai.ai") as SimpleANNController
                controller.setLegacy()

                return MarioAgent(controller)
            }

        val bestGeneric: IAgent
            get() {
                // experiments/Phase I - AI/Doubled input - All/NeuroEvolution, experiment 1_ai.ai // solves 12 of all
                // experiments/Phase I - AI/Doubled input - All - Randomized levels/NeuroEvolution, experiment 1_ai.ai  // solves 9 of all random
                val controller = ObjectStorage.load("experiments/Phase I - AI/Doubled input - All - Randomized levels/NeuroEvolution, experiment 1_ai.ai") as SimpleANNController
                controller.setDenseInput()

                return MarioAgent(controller)
            }
    }

}