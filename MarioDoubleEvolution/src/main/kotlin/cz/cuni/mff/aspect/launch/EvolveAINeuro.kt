package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.NeuroControllerEvolution
import cz.cuni.mff.aspect.evolution.results.LevelGenerators
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.controllers.ann.networks.UpdatedAgentNetwork
import io.jenetics.GaussianMutator
import kotlin.system.exitProcess


fun main() {
    evolveAI()
    exitProcess(0)
}


fun evolveAI() {
    val controllerANN = UpdatedAgentNetwork(5, 5, 0, 2, 20)
    val controllerEvolution: ControllerEvolution = NeuroControllerEvolution(controllerANN,
        50,
        50,
        chartLabel = "Neuroevolution Stage 1 Level 1 split",
        mutators = arrayOf(GaussianMutator(0.25))
    )
    val levelGenerator = LevelGenerators.PMPGenerator.all
    val resultController = controllerEvolution.evolve(levelGenerator, MarioGameplayEvaluators::distanceOnly, MarioGameplayEvaluators::victoriesOnly)

    val marioSimulator = GameSimulator()

    Array(5) { levelGenerator.generate() }.forEach {
        marioSimulator.playMario(resultController, it, true)
    }

}
