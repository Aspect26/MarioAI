package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.coevolution.MarioCoEvolver
import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.controller.NeuroControllerEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.ChunksLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.AgentHalfPassing
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.UpdatedAgentNetwork
import cz.cuni.mff.aspect.storage.ObjectStorage
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser
import io.jenetics.GaussianMutator

private val RESULT_FILES_PATH = "data/coev/repeat-lgs"

fun main() {
//    coevolve()
    playLatestCo()
}

fun coevolve() {
    val controllerEvolution: ControllerEvolution = NeuroControllerEvolution(
        null,
        populationSize = 50,
        generationsCount = 20,
        levelsPerGeneratorCount = 5,
        mutators = arrayOf(GaussianMutator(0.55)),
        parallel = true,
        showChart = true,
        chartLabel = "Agent NeuroEvolution"
    )

    val levelGeneratorEvolution = ChunksLevelGeneratorEvolution(
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

    val coevolver = MarioCoEvolver()

    // TODO: omg wtf preco AI evolucia ma fitness v evolve() a lg evolucia v c'tore -_-
    val result = coevolver.evolve(
        controllerEvolution,
        levelGeneratorEvolution,
        initialController,
        initialLevelGenerator,
        MarioGameplayEvaluators::distanceOnly,
        25,
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

    for (i in 1 .. 25) {
        println("Update AI ($i)")
        currentController = ObjectStorage.load("$RESULT_FILES_PATH/ai_$i.ai") as MarioController
        simulator.playMario(currentController, currentGenerator.generate())

        println("Update Level Generator ($i)")
        currentGenerator = ObjectStorage.load("$RESULT_FILES_PATH/lg_$i.lg") as LevelGenerator
        repeat(5) { LevelVisualiser().display(currentGenerator.generate()) }
        simulator.playMario(currentController, currentGenerator.generate())
    }

}
