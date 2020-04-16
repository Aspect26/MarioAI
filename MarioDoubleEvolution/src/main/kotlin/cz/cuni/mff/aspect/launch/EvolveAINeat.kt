package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.NeatControllerEvolution
import cz.cuni.mff.aspect.evolution.results.LevelGenerators
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import kotlin.system.exitProcess


fun main() {
    evolveNeatAI()
    exitProcess(0)
}

fun evolveNeatAI() {
    val networkSettings = NetworkSettings(5, 5, 0, 2)
    val controllerEvolution: ControllerEvolution = NeatControllerEvolution(
        networkSettings,
        denseInput = false,
        generationsCount = 500,
        populationSize = 100,
        chartName = "NEAT Evolution S4L1")
    val levelGenerator = LevelGenerators.PMPGenerator.all
    val resultController = controllerEvolution.evolve(levelGenerator, MarioGameplayEvaluators::distanceOnly, MarioGameplayEvaluators::victoriesOnly)

    val marioSimulator = GameSimulator()


    Array(5) { levelGenerator.generate() }.forEach {
        marioSimulator.playMario(resultController, it, true)
    }
}
