package cz.cuni.mff.aspect.evolution.results

import ch.idsia.agents.IAgent
import cz.cuni.mff.arnold.ArnoldRuleBasedAgent
import cz.cuni.mff.aspect.MyRuleBasedAgent
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.storage.ObjectStorage

object Agents {

    object RuleBased {

        // My best implementation from AI course
        val my: IAgent get() = MyRuleBasedAgent()

        // Going right only -> for testing
        val goingRight: MarioAgent get() = GoingRightAgent()

        // Jakub Arnold's implementation from AI course
        val arnold: IAgent get() = ArnoldRuleBasedAgent()

    }

    object NeuroEvolution {

        val Stage2Level1Solver: IAgent
            get() {
                // best S2S, doesn't solve everything, but does some nice things and solves PathWithHoles level
                val controller = ObjectStorage.load("experiments/Phase I - AI/NeuroEvolution/Gaussian test evaluation - S2S/NeuroEvolution, Mutator 0.45_ai.ai") as SimpleANNController
                controller.setLegacy()

                return MarioAgent(controller)
            }

        val Stage4Level1Solver: IAgent
            get() {
                // experiments/Phase I - AI/Gaussian test evaluation - S4S/NeuroEvolution, Mutator 0.25_ai.ai   // jumpee
                // experiments/Phase I - AI/Gaussian test evaluation - S4S (3)/NeuroEvolution, Mutator 0.1_ai.ai // non jumpee
                // experiments/Phase I - AI/Hidden layer test evaluation - S4S/NeuroEvolution, hidden layer size 5_ai.ai // non jumpee
                // experiments/Phase I - AI/Gaussian test evaluation - S4S - Population 100/NeuroEvolution, Mutator 0.10_ai.ai  // solves also PathWithHoles but fails on one part of S4S
                // experiments/Phase I - AI/Hidden layer test evaluation - S4S/NeuroEvolution, hidden layer size 15_ai.ai  // solves also PathWithHoles nicely but fails on one part of S4S and is overfited
                val controller = ObjectStorage.load("experiments/Phase I - AI/NeuroEvolution/Hidden layer test evaluation - S4S/NeuroEvolution, hidden layer size 5_ai.ai") as SimpleANNController
                controller.setLegacy()

                return MarioAgent(controller)
            }

        val BestGeneric: IAgent
            get() {
                // experiments/Phase I - AI/Doubled input - All/NeuroEvolution, experiment 1_ai.ai // solves 12 of all
                // experiments/Phase I - AI/Doubled input - All - Randomized levels/NeuroEvolution, experiment 1_ai.ai  // solves 9 of all random
                val controller = ObjectStorage.load("experiments/Phase I - AI/NeuroEvolution/Doubled input - All - Randomized levels/NeuroEvolution, experiment 1_ai.ai") as SimpleANNController
                controller.setDenseInput()

                return MarioAgent(controller)
            }
    }

    object NEAT {
        val Stage4Level1Solver: IAgent
            get() {
                // val controller = ObjectStorage.load("experiments/Phase I - AI/NEAT/NEAT - S4S - Population 100, Generations - 200, no dense input/NEAT evolution, experiment 2_ai.ai") as SimpleANNController
                val controller = ObjectStorage.load("experiments/Phase I - AI/NEAT/NEAT - SS - Population 100, Generations - 100, no dense input/NEAT evolution, experiment 1_ai.ai") as SimpleANNController
                return MarioAgent(controller)
            }

        val BestGeneric: IAgent
            get() {
                //solves 12
                //val controller = ObjectStorage.load("experiments/Phase I - AI/NEAT/NEAT - All - 300-100 - fitness least actions/NEAT evolution, experiment 1_ai.ai") as SimpleANNController

                // solves 13
                val controller = ObjectStorage.load("experiments/NEAT - All - 300-100 - fitness only distance/NEAT evolution, experiment 2_ai.ai") as SimpleANNController
                return MarioAgent(controller)
            }
    }

}