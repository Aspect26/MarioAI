package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.evaluators.DistanceOnlyEvaluator
import cz.cuni.mff.aspect.evolution.controller.TrainingLevelsSet
import cz.cuni.mff.aspect.evolution.controller.evaluators.VictoriesOnlyEvaluator
import cz.cuni.mff.aspect.evolution.controller.neuroevolution.NeuroControllerEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.evolution.results.LevelGenerators
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.level.original.Stage4Level1Split
import cz.cuni.mff.aspect.storage.ObjectStorage
import io.jenetics.GaussianMutator

private val PATH_TO_LATEST_AI = "data/latest_neuro_ai.ai"

/** Launches Neuroevolution algorithm to evolve AI using specified settings. */
fun main() {
    evolveAI()
//    continueEvolveAI()
//    playLatestAI()
}


fun evolveAI() {
    val controllerEvolution: ControllerEvolution =
        NeuroControllerEvolution(
            NetworkSettings(5, 5, 0, 2, 5,
                denseInput = false, oneHotOnEnemies = true),
            100,
            50,
            fitnessFunction = DistanceOnlyEvaluator(),
            objectiveFunction = VictoriesOnlyEvaluator(),
            evaluateOnLevelsCount = Stage4Level1Split.levels.size,
            chartLabel = "NeuroEvolution",
            alterers = arrayOf(GaussianMutator(0.55)),
            alwaysReevaluate = false
        )
//    val levelGenerator = PCLevelGenerator.createSimplest()
    val levelGenerator = LevelGenerators.StaticGenerator(Stage4Level1Split.levels)
    val resultController = controllerEvolution.evolve(listOf(levelGenerator))
    ObjectStorage.store(PATH_TO_LATEST_AI, resultController)
    controllerEvolution.chart.store("data/latest.svg")

    val marioSimulator = GameSimulator()

    Array(5) { levelGenerator.generate() }.forEach {
        marioSimulator.playMario(resultController, it, true)
    }

}


private fun continueEvolveAI() {
    val controllerEvolution: ControllerEvolution =
        NeuroControllerEvolution(
            null,
            20,
            50,
            fitnessFunction = DistanceOnlyEvaluator(),
            objectiveFunction = VictoriesOnlyEvaluator(),
            evaluateOnLevelsCount = 10,
            chartLabel = "NeuroEvolution Update half",
            alterers = arrayOf(GaussianMutator(0.55)),
            parallel = true
        )
//    val levelGenerator = LevelGenerators.PCGenerator.halfSolvingNE
    val levelGenerator = ObjectStorage.load("data/coev/second_lg.lg") as LevelGenerator
    val initialController = (Agents.NeuroEvolution.Stage4Level1Solver as MarioAgent).controller

    val resultController = controllerEvolution.continueEvolution(initialController, listOf(levelGenerator))
    ObjectStorage.store(PATH_TO_LATEST_AI, resultController)

    val marioSimulator = GameSimulator()

    Array(5) { levelGenerator.generate() }.forEach {
        marioSimulator.playMario(resultController, it, true)
    }

}


private fun playLatestAI() {
    val controller = ObjectStorage.load(PATH_TO_LATEST_AI) as MarioController
    val levelGenerator = LevelGenerators.StaticGenerator(TrainingLevelsSet)
    val marioSimulator = GameSimulator()

    Array(25) { levelGenerator.generate() }.forEach {
        marioSimulator.playMario(controller, it, true)
    }
}
