package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.evaluators.DistanceOnlyEvaluator
import cz.cuni.mff.aspect.evolution.controller.evaluators.VictoriesOnlyEvaluator
import cz.cuni.mff.aspect.evolution.controller.neuroevolution.NeuroControllerEvolution
import cz.cuni.mff.aspect.evolution.jenetics.alterers.UpdatedGaussianMutator
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.storage.ObjectStorage
import io.jenetics.GaussianMutator

private val PATH_TO_LATEST_AI = "data/latest_neuro_ai.ai"

/** Launches Neuroevolution algorithm to evolve AI using specified settings. */
fun main() {
    evolve()
//    continueEvolution)
//    playLatest()
}


private fun evolve() {
    val controllerEvolution: ControllerEvolution =
        NeuroControllerEvolution(
            NetworkSettings(5, 5, 0, 2, 5,
                denseInput = true, oneHotOnEnemies = false),
            100,
            100,
            fitnessFunction = DistanceOnlyEvaluator(),
            objectiveFunction = VictoriesOnlyEvaluator(),
            evaluateOnLevelsCount = 10,
            chartLabel = "NeuroEvolution",
            alterers = arrayOf(UpdatedGaussianMutator(1.0, 0.05)),
            alwaysReevaluate = true
        )
//    val levelGenerator = PCLevelGenerator.createSimplest()
    val levelGenerator = ObjectStorage.load<LevelGenerator>("data/coev/17_pmp_last/neuro_pmp/lg_${6}.lg")
    val resultController = controllerEvolution.evolve(listOf(levelGenerator)).bestController
    ObjectStorage.store(PATH_TO_LATEST_AI, resultController)
    controllerEvolution.chart.store("data/latest-3.svg")

    val marioSimulator = GameSimulator()

    Array(5) { levelGenerator.generate() }.forEach {
        marioSimulator.playMario(resultController, it, true)
    }

}


private fun continueEvolution() {
    val controllerEvolution: ControllerEvolution =
        NeuroControllerEvolution(
            NetworkSettings(5, 5, 0, 2, 5,
                denseInput = false, oneHotOnEnemies = true),
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
    val initialPopulation = List(50) { (Agents.NeuroEvolution.Stage4Level1Solver as MarioAgent).controller }

    val resultController = controllerEvolution.continueEvolution(listOf(levelGenerator), initialPopulation).bestController
    ObjectStorage.store(PATH_TO_LATEST_AI, resultController)

    val marioSimulator = GameSimulator()

    Array(5) { levelGenerator.generate() }.forEach {
        marioSimulator.playMario(resultController, it, true)
    }

}


private fun playLatest() {
    val controller = ObjectStorage.load(PATH_TO_LATEST_AI) as MarioController
//    val levelGenerator = LevelGenerators.StaticGenerator(TrainingLevelsSet)
    val levelGenerator = ObjectStorage.load<LevelGenerator>("data/coev/17_pmp_last/neuro_pmp/lg_${6}.lg")
    val marioSimulator = GameSimulator(2500)

    val solved = Array(100) { levelGenerator.generate() }.map {
        marioSimulator.playMario(controller, it, false)
    }.sumBy { if (it.levelFinished) 1 else 0 }

    println(solved)
}
