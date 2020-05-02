package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.controller.neat.NeatControllerEvolution
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.evolution.results.LevelGenerators
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.level.original.Stage1Level1Split
import cz.cuni.mff.aspect.mario.level.original.Stage4Level1Split
import cz.cuni.mff.aspect.storage.ObjectStorage

private val PATH_TO_LATEST_AI = "data/latest_neat_ai.ai"

fun main() {
    evolve()
//    continueEvolution()
//    playLatest()
}

private fun evolve() {
    val networkSettings = NetworkSettings(5, 7, 0, 3)
    val controllerEvolution: ControllerEvolution =
        NeatControllerEvolution(
            networkSettings,
            denseInput = false,
            generationsCount = 50,
            populationSize = 100,
            fitnessFunction = MarioGameplayEvaluators::distanceOnly,
            objectiveFunction = MarioGameplayEvaluators::victoriesOnly,
            evaluateOnLevelsCount = 4,
            chartLabel = "NEAT Evolution S4L1"
        )
    val levelGenerator = LevelGenerators.StaticGenerator(Stage4Level1Split.levels)
    val resultController = controllerEvolution.evolve(listOf(levelGenerator))
    ObjectStorage.store(PATH_TO_LATEST_AI, resultController)

    val marioSimulator = GameSimulator()

    Array(5) { levelGenerator.generate() }.forEach {
        marioSimulator.playMario(resultController, it, true)
    }
}


private fun continueEvolution() {
    val networkSettings = NetworkSettings(5, 5, 0, 2)
    val controllerEvolution =
        NeatControllerEvolution(
            networkSettings,
            denseInput = false,
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
