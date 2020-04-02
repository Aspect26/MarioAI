package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.coevolution.MarioCoEvolver
import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.controller.NeatControllerEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelEvaluators
import cz.cuni.mff.aspect.evolution.levels.pmp.ProbabilisticMultipassLevelGeneratorEvolution
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.storage.ObjectStorage

fun main() {
    coevolve()
//    playLatestCo()
}

fun coevolve() {
    val controllerEvolution: ControllerEvolution = NeatControllerEvolution(
        networkSettings = NetworkSettings(),
        populationSize = 100,
        generationsCount = 5,
        denseInput = false,
        displayChart = false
    )

    val levelGenerator: LevelGeneratorEvolution = ProbabilisticMultipassLevelGeneratorEvolution(
        populationSize = 50,
        generationsCount = 5,
        evaluateOnLevelsCount = 5,
        fitnessFunction = PMPLevelEvaluators::distanceDiversityEnemiesLinearity,
        displayChart = false
    )

    val coevolver = MarioCoEvolver()
    val result = coevolver.evolve(controllerEvolution, levelGenerator, MarioGameplayEvaluators::distanceOnly, 10)

    ObjectStorage.store("data/coev/controller.ai", result.controller)
    ObjectStorage.store("data/coev/generator.lg", result.levelGenerator)

}

fun playLatestCo() {
    val controller = ObjectStorage.load("data/coev/controller.ai") as MarioController
    val levelGenerator = ObjectStorage.load("data/coev/generator.lg") as LevelGenerator

    val levels = Array(5) { levelGenerator.generate() }
    val simulator = GameSimulator()

    levels.forEach {
        val agent = MarioAgent(controller)

        simulator.playMario(agent, it, true)
    }
}