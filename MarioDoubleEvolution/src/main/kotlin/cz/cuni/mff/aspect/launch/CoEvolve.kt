package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.coevolution.Coevolution
import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.controller.neuroevolution.NeuroControllerEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.AgentHalfPassing
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.All
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.HiddenLayerControllerNetwork
import cz.cuni.mff.aspect.storage.ObjectStorage
import io.jenetics.GaussianMutator

private val RESULT_FILES_PATH = "data/coev/08_dense_input/neuro_pc"

/** Launches a coevolution or plays mario using AIs and level generators from the latest coevolution. */
fun main() {
//    coevolve()
    playLatestCo()
}

fun coevolve() {
    val controllerEvolution: ControllerEvolution =
        NeuroControllerEvolution(
            null,
            populationSize = 50,
            generationsCount = 20,
            fitnessFunction = MarioGameplayEvaluators::distanceOnly,
            objectiveFunction = MarioGameplayEvaluators::victoriesOnly,
            evaluateOnLevelsCount = 25,
            alterers = arrayOf(GaussianMutator(0.55)),
            parallel = true,
            displayChart = true,
            chartLabel = "Agent NeuroEvolution"
        )

    val levelGeneratorEvolution = PCLevelGeneratorEvolution(
        populationSize = 50,
        generationsCount = 5,
        evaluateOnLevelsCount = 5,
        fitnessFunction = All(),
        objectiveFunction = AgentHalfPassing(),
        displayChart = true,
        chartLabel = "PC Level Generator"
    )

    val initialLevelGenerator = PCLevelGenerator.createSimplest()

    val initialController = SimpleANNController(HiddenLayerControllerNetwork(NetworkSettings(
        receptiveFieldSizeRow = 5,
        receptiveFieldSizeColumn = 5,
        receptiveFieldRowOffset = 0,
        receptiveFieldColumnOffset = 2,
        hiddenLayerSize = 7
    )))

    val coevolver = Coevolution()

    coevolver.evolve(
        controllerEvolution,
        levelGeneratorEvolution,
        initialController,
        initialLevelGenerator,
        25,
        5,
        RESULT_FILES_PATH
    )

}

fun playLatestCo() {
    val simulator = GameSimulator()

    var currentController: MarioController = SimpleANNController(HiddenLayerControllerNetwork(NetworkSettings(
        receptiveFieldSizeRow = 5,
        receptiveFieldSizeColumn = 5,
        receptiveFieldRowOffset = 0,
        receptiveFieldColumnOffset = 2,
        hiddenLayerSize = 7
    )))
    var currentGenerator: LevelGenerator = PCLevelGenerator.createSimplest()
//    simulator.playMario(currentController, currentGenerator.generate())

    for (i in 1 .. 25) {
        println("Generation: $i")

        currentController = ObjectStorage.load("$RESULT_FILES_PATH/ai_$i.ai") as MarioController
//        println("AI update - ${MarioGameplayEvaluators.victoriesOnly(Array(10) { simulator.playMario(currentController, currentGenerator.generate(), false) }) / 1000}")
//        println("AI update - ${MarioGameplayEvaluators.victoriesOnly(Array(10) { simulator.playMario(currentController, currentGenerator.generate(), false) }) / 1000}")
//        println("AI update - ${MarioGameplayEvaluators.victoriesOnly(Array(10) { simulator.playMario(currentController, currentGenerator.generate(), false) }) / 1000}")
//        println("AI update - ${MarioGameplayEvaluators.victoriesOnly(Array(10) { simulator.playMario(currentController, currentGenerator.generate(), false) }) / 1000}")
        simulator.playMario(currentController, LevelPostProcessor.postProcess(currentGenerator.generate(), false))

        currentGenerator = ObjectStorage.load("$RESULT_FILES_PATH/lg_$i.lg") as LevelGenerator
//        repeat(5) { LevelVisualiser().display(currentGenerator.generate()) }
//        println("Generator update; LG evaluation - ${AgentHalfPassing()(currentGenerator as PCLevelGenerator, {MarioAgent(currentController)}, 100) / 1000}")
//        println("Generator update; LG evaluation - ${AgentHalfPassing()(currentGenerator as PCLevelGenerator, {MarioAgent(currentController)}, 100) / 1000}")
//        println("Generator update; LG evaluation - ${AgentHalfPassing()(currentGenerator as PCLevelGenerator, {MarioAgent(currentController)}, 100) / 1000}")
//        println("Generator update; LG evaluation - ${AgentHalfPassing()(currentGenerator as PCLevelGenerator, {MarioAgent(currentController)}, 100) / 1000}")
        simulator.playMario(currentController, LevelPostProcessor.postProcess(currentGenerator.generate(), false))
    }

}
