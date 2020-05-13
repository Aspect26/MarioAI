package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.controller.TrainingLevelsSet
import cz.cuni.mff.aspect.evolution.controller.neat.NeatControllerEvolution
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.evolution.results.LevelGenerators
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.level.original.Stage1Level1Split
import cz.cuni.mff.aspect.storage.ObjectStorage

private val PATH_TO_LATEST_AI = "data/latest_neat_ai.ai"

/** Launches NEAT algorithm to evolve AI using specified settings. */
fun main() {
    evolve()
//    continueEvolution()
//    playLatest()
}

private fun evolve() {
    val networkSettings = NetworkSettings(5, 5, 0, 2,
        denseInput = false, oneHotOnEnemies = true)
    val controllerEvolution =
        NeatControllerEvolution(
            networkSettings,
            generationsCount = 500,
            populationSize = 100,
            fitnessFunction = MarioGameplayEvaluators::distanceOnly,
            objectiveFunction = MarioGameplayEvaluators::victoriesOnly,
            evaluateOnLevelsCount = TrainingLevelsSet.size,
            chartLabel = "NEAT Evolution",
            displayChart = true
        )
    val levelGenerator = LevelGenerators.StaticGenerator(TrainingLevelsSet)
    val resultController = controllerEvolution.evolve(listOf(levelGenerator))
    ObjectStorage.store(PATH_TO_LATEST_AI, resultController)
    controllerEvolution.chart.store("data/latest_neat.svg")

    val marioSimulator = GameSimulator()

    Array(5) { levelGenerator.generate() }.forEach {
        marioSimulator.playMario(resultController, it, true)
    }
}


private fun continueEvolution() {
    val networkSettings = NetworkSettings(5, 5, 0, 2, denseInput = false)
    val controllerEvolution =
        NeatControllerEvolution(
            networkSettings,
            generationsCount = 50,
            populationSize = 100,
            fitnessFunction = MarioGameplayEvaluators::distanceOnly,
            objectiveFunction = MarioGameplayEvaluators::victoriesOnly,
            evaluateOnLevelsCount = 10,
            chartLabel = "NEAT Evolution continuation"
        )
    val levelGenerator = LevelGenerators.PCGenerator.halfSolvingNE
    val initialController = (Agents.NEAT.Stage4Level1Solver as MarioAgent).controller

    val resultController = controllerEvolution.continueEvolution(initialController, listOf(levelGenerator))
    ObjectStorage.store(PATH_TO_LATEST_AI, resultController)

    val marioSimulator = GameSimulator()


    Array(5) { levelGenerator.generate() }.forEach {
        marioSimulator.playMario(resultController, it, true)
    }
}

private fun playLatest() {
    val controller = ObjectStorage.load(PATH_TO_LATEST_AI) as MarioController
    val agent = MarioAgent(controller)
//    val levelGenerator = LevelGenerators.PCGenerator.halfSolvingNE
    val levelGenerator = LevelGenerators.StaticGenerator(Stage1Level1Split.levels)
    val gameSimulator = GameSimulator()

    repeat(5) {
        val level = levelGenerator.generate()
        gameSimulator.playMario(agent, level, true)
    }
}
