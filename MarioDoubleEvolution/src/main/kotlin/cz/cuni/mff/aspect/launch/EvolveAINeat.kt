package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.controller.NeatControllerEvolution
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.evolution.results.LevelGenerators
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings


fun main() {
//    evolveNeatAI()
    continueEvolveNeatAI()
}

fun evolveNeatAI() {
    val networkSettings = NetworkSettings(5, 5, 0, 2)
    val controllerEvolution: ControllerEvolution = NeatControllerEvolution(
        networkSettings,
        denseInput = false,
        generationsCount = 50,
        populationSize = 100,
        evolveOnLevelsCount = 5,
        chartName = "NEAT Evolution S4L1")
    val levelGenerator = LevelGenerators.PCGenerator.halfSolvingNE
    val resultController = controllerEvolution.evolve(levelGenerator, MarioGameplayEvaluators::distanceOnly, MarioGameplayEvaluators::victoriesOnly)

    val marioSimulator = GameSimulator()


    Array(5) { levelGenerator.generate() }.forEach {
        marioSimulator.playMario(resultController, it, true)
    }
}


fun continueEvolveNeatAI() {
    val networkSettings = NetworkSettings(5, 5, 0, 2)
    val controllerEvolution = NeatControllerEvolution(
        networkSettings,
        denseInput = false,
        generationsCount = 50,
        populationSize = 100,
        evolveOnLevelsCount = 10,
        chartName = "NEAT Evolution continuation")
    val levelGenerator = LevelGenerators.PCGenerator.halfSolvingNE
    val initialController = (Agents.NEAT.Stage4Level1Solver as MarioAgent).controller

    val resultController = controllerEvolution.continueEvolution(initialController, levelGenerator, MarioGameplayEvaluators::distanceOnly, MarioGameplayEvaluators::victoriesOnly)

    val marioSimulator = GameSimulator()


    Array(5) { levelGenerator.generate() }.forEach {
        marioSimulator.playMario(resultController, it, true)
    }
}
