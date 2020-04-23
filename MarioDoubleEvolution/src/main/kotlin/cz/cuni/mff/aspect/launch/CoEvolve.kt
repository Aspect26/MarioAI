package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.coevolution.MarioCoEvolver
import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.controller.NeuroControllerEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.ChunksLevelGeneratorGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.AgentHalfPassing
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.UpdatedAgentNetwork
import cz.cuni.mff.aspect.storage.ObjectStorage
import io.jenetics.GaussianMutator

private val RESULT_FILES_PATH = "data/coev/test"

fun main() {
    coevolve()
//    playLatestCo()
}

fun coevolve() {
    val controllerEvolution: ControllerEvolution = NeuroControllerEvolution(
        null,
        populationSize = 50,
        generationsCount = 10,
        levelsPerGeneratorCount = 5,
        mutators = arrayOf(GaussianMutator(0.55)),
        parallel = true,
        showChart = true,
        chartLabel = "Agent NeuroEvolution"
    )

    val levelGeneratorEvolution = ChunksLevelGeneratorGeneratorEvolution(
        populationSize = 50,
        generationsCount = 3,
        evaluateOnLevelsCount = 5,
        fitnessFunction = AgentHalfPassing(),
        displayChart = true,
        chartLabel = "PC Leven Generator"
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
    simulator.playMario(currentController, currentGenerator.generate())

    for (i in 1 .. 10) {
        println("Update AI ($i)")
        currentController = ObjectStorage.load("$RESULT_FILES_PATH/ai_$i.ai") as MarioController
        simulator.playMario(currentController, currentGenerator.generate())

        println("Update Level Generator ($i)")
        currentGenerator = ObjectStorage.load("$RESULT_FILES_PATH/lg_$i.lg") as LevelGenerator
        simulator.playMario(currentController, currentGenerator.generate())
    }

}
