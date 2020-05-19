package cz.cuni.mff.aspect.evolution.results

import ch.idsia.agents.IAgent
import cz.cuni.mff.agents.arnold.ArnoldRuleBasedAgent
import cz.cuni.mff.agents.aspect.MyRuleBasedAgent
import cz.cuni.mff.aspect.controllers.*
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.storage.ObjectStorage

/** Contains some evolved agents. */
object Agents {

    /** Ruled based agents. */
    object RuleBased {

        // Sanity checks agents
        val standing: MarioAgent get() = MarioAgent(StandingController())
        val random: MarioAgent get() = MarioAgent(RandomController())
        val goingRight: MarioAgent get() = MarioAgent(GoingRightController())
        val goingRightJumping: MarioAgent get() = MarioAgent(GoingRightAndJumpingController())

        // My best implementation from AI course
        val my: IAgent get() = MyRuleBasedAgent()

        // Jakub Arnold's implementation from AI course
        val arnold: IAgent get() = ArnoldRuleBasedAgent()

    }

    /** Agents evolved by simple neuroevolution. */
    object NeuroEvolution {

        val Stage4Level1Solver: IAgent
            get() {
                // experiments/Phase I - AI/Gaussian test evaluation - S4S/NeuroEvolution, Mutator 0.25_ai.ai   // jumpee
                // experiments/Phase I - AI/Gaussian test evaluation - S4S (3)/NeuroEvolution, Mutator 0.1_ai.ai // non jumpee
                // experiments/Phase I - AI/Hidden layer test evaluation - S4S/NeuroEvolution, hidden layer size 5_ai.ai // non jumpee
                // experiments/Phase I - AI/Gaussian test evaluation - S4S - Population 100/NeuroEvolution, Mutator 0.10_ai.ai  // solves also PathWithHoles but fails on one part of S4S
                // experiments/Phase I - AI/Hidden layer test evaluation - S4S/NeuroEvolution, hidden layer size 15_ai.ai  // solves also PathWithHoles nicely but fails on one part of S4S and is overfited
                val controller = ObjectStorage.load("data/experiments/Phase I - AI/NeuroEvolution/Hidden layer test evaluation - S4S/NeuroEvolution, hidden layer size 5_ai.ai") as SimpleANNController

                return MarioAgent(controller)
            }

    }

    /** Agents evolved by NEAT. */
    object NEAT {
        val Stage4Level1Solver: IAgent
            get() {
                // val controller = ObjectStorage.load("experiments/Phase I - AI/NEAT/NEAT - S4S - Population 100, Generations - 200, no dense input/NEAT evolution, experiment 2_ai.ai") as SimpleANNController
                val controller = ObjectStorage.load("data/experiments/Phase I - AI/NEAT/NEAT - SS - Population 100, Generations - 100, no dense input/NEAT evolution, experiment 1_ai.ai") as SimpleANNController
                return MarioAgent(controller)
            }

        val BestGeneric: IAgent
            get() {
                //solves 12
                //val controller = ObjectStorage.load("experiments/Phase I - AI/NEAT/NEAT - All - 300-100 - fitness least actions/NEAT evolution, experiment 1_ai.ai") as SimpleANNController

                // solves 13
                val controller = ObjectStorage.load("data/experiments/Phase I - AI/NEAT/NEAT - All - 300-100 - fitness only distance/NEAT evolution, experiment 2_ai.ai") as SimpleANNController
                return MarioAgent(controller)
            }
    }

}