package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.controller.NeuroControllerEvolution
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.evolution.results.LevelGenerators
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.storage.ObjectStorage
import io.jenetics.GaussianMutator

private val PATH_TO_LATEST_AI = "data/latest_neuro_ai.ai"

fun main() {
//    evolveAI()
    continueEvolveAI()
//    playLatestAI()
}


fun evolveAI() {
    val controllerEvolution: ControllerEvolution = NeuroControllerEvolution(
        NetworkSettings(5, 5, 0, 2, 7),
        50,
        50,
        evaluateOnLevelsCount = 10,
        chartLabel = "NeuroEvolution - Update half",
        mutators = arrayOf(GaussianMutator(0.55))
    )
    val levelGenerator = LevelGenerators.PCGenerator.halfSolving
    val resultController = controllerEvolution.evolve(levelGenerator, MarioGameplayEvaluators::distanceOnly, MarioGameplayEvaluators::victoriesOnly)
    ObjectStorage.store(PATH_TO_LATEST_AI, resultController)

    val marioSimulator = GameSimulator()

    Array(5) { levelGenerator.generate() }.forEach {
        marioSimulator.playMario(resultController, it, true)
    }

}


fun continueEvolveAI() {
    val controllerEvolution: ControllerEvolution = NeuroControllerEvolution(null,
        20,
        50,
        evaluateOnLevelsCount = 10,
        chartLabel = "NeuroEvolution Update half",
        mutators = arrayOf(GaussianMutator(0.55)),
        parallel = true
    )
    val levelGenerator = LevelGenerators.PCGenerator.halfSolving
    val initialController = (Agents.NeuroEvolution.Stage4Level1Solver as MarioAgent).controller

    val fitness = MarioGameplayEvaluators::distanceOnly

    val resultController = controllerEvolution.continueEvolution(initialController, levelGenerator, fitness, MarioGameplayEvaluators::victoriesOnly)
    ObjectStorage.store(PATH_TO_LATEST_AI, resultController)

    val marioSimulator = GameSimulator()

    Array(5) { levelGenerator.generate() }.forEach {
        marioSimulator.playMario(resultController, it, true)
    }

}


fun playLatestAI() {
    val controller = ObjectStorage.load(PATH_TO_LATEST_AI) as MarioController
    val levelGenerator = LevelGenerators.PCGenerator.halfSolving
    val marioSimulator = GameSimulator()

    Array(5) { levelGenerator.generate() }.forEach {
        marioSimulator.playMario(controller, it, true)
    }
}