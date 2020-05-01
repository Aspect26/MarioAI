package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.coevolution.MarioCoEvolution
import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.controller.neuroevolution.NeuroControllerEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.AgentHalfPassing
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.UpdatedAgentNetwork
import cz.cuni.mff.aspect.storage.ObjectStorage
import io.jenetics.GaussianMutator

private val RESULT_FILES_PATH = "data/coev/10-sl-window/neuro_pc"

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
        fitnessFunction = AgentHalfPassing(),
        displayChart = true,
        chartLabel = "PC Level Generator"
    )

    val initialLevelGenerator = PCLevelGenerator.createSimplest()

    val initialController = SimpleANNController(UpdatedAgentNetwork(
        receptiveFieldSizeRow = 5,
        receptiveFieldSizeColumn = 5,
        receptiveFieldRowOffset = 0,
        receptiveFieldColumnOffset = 2,
        hiddenLayerSize = 7
    ))

    val coevolver = MarioCoEvolution()

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

    var currentController: MarioController = SimpleANNController(UpdatedAgentNetwork(
        receptiveFieldSizeRow = 5,
        receptiveFieldSizeColumn = 5,
        receptiveFieldRowOffset = 0,
        receptiveFieldColumnOffset = 2,
        hiddenLayerSize = 7
    ))
    var currentGenerator: LevelGenerator = PCLevelGenerator.createSimplest()
//    simulator.playMario(currentController, currentGenerator.generate())

    for (i in 16 .. 30) {
        currentController = ObjectStorage.load("$RESULT_FILES_PATH/ai_$i.ai") as MarioController
        currentGenerator = ObjectStorage.load("$RESULT_FILES_PATH/lg_$i.lg") as LevelGenerator

        println("Generation: $i")
//        repeat(5) { LevelVisualiser().display(currentGenerator.generate()) }
        simulator.playMario(currentController, currentGenerator.generate())
    }

}
