package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.coevolution.MarioCoEvolver
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.controller.NeatControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.NeuroControllerEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.ChunksLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.AgentHalfPassing
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGeneratorEvolution
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.UpdatedAgentNetwork
import io.jenetics.GaussianMutator

fun main() {
    coevNeatPMP()
    coevNeatPC()
    coevNeuroPMP()
    coevNeuroPC()
}

fun coevNeuroPMP() {

    val controllerEvolution = NeuroControllerEvolution(
        null,
        populationSize = 50,
        generationsCount = 20,
        levelsPerGeneratorCount = 5,
        mutators = arrayOf(GaussianMutator(0.55)),
        parallel = true,
        showChart = false,
        chartLabel = "Agent NeuroEvolution"
    )

    val levelGeneratorEvolution = PMPLevelGeneratorEvolution(
        populationSize = 50,
        generationsCount = 5,
        evaluateOnLevelsCount = 5,
        fitnessFunction = cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.AgentHalfPassing(),
        displayChart = false,
        chartLabel = "PMP Level Generator"
    )

    val initialLevelGenerator = PMPLevelGenerator.createSimplest()

    val initialController = SimpleANNController(
        UpdatedAgentNetwork(
        receptiveFieldSizeRow = 5,
        receptiveFieldSizeColumn = 5,
        receptiveFieldRowOffset = 0,
        receptiveFieldColumnOffset = 2,
        hiddenLayerSize = 7
    )
    )

    val coevolver = MarioCoEvolver()
    val storagePath = "result/neuro_pmp"

    coevolver.evolve(
        controllerEvolution,
        levelGeneratorEvolution,
        initialController,
        initialLevelGenerator,
        MarioGameplayEvaluators::distanceOnly,
        25,
        storagePath
    )

    levelGeneratorEvolution.storeChart("$storagePath/lg.svg")
    controllerEvolution.storeChart("$storagePath/lg.svg")
}

fun coevNeuroPC() {

    val controllerEvolution = NeuroControllerEvolution(
        null,
        populationSize = 50,
        generationsCount = 20,
        levelsPerGeneratorCount = 5,
        mutators = arrayOf(GaussianMutator(0.55)),
        parallel = true,
        showChart = false,
        chartLabel = "Agent NeuroEvolution"
    )

    val levelGeneratorEvolution = ChunksLevelGeneratorEvolution(
        populationSize = 50,
        generationsCount = 5,
        evaluateOnLevelsCount = 5,
        fitnessFunction = AgentHalfPassing(),
        displayChart = false,
        chartLabel = "PC Level Generator"
    )

    val initialLevelGenerator = PMPLevelGenerator.createSimplest()

    val initialController = SimpleANNController(
        UpdatedAgentNetwork(
            receptiveFieldSizeRow = 5,
            receptiveFieldSizeColumn = 5,
            receptiveFieldRowOffset = 0,
            receptiveFieldColumnOffset = 2,
            hiddenLayerSize = 7
        )
    )

    val coevolver = MarioCoEvolver()
    val storagePath = "result/neuro_pc"

    coevolver.evolve(
        controllerEvolution,
        levelGeneratorEvolution,
        initialController,
        initialLevelGenerator,
        MarioGameplayEvaluators::distanceOnly,
        25,
        storagePath
    )

    levelGeneratorEvolution.storeChart("$storagePath/lg.svg")
    controllerEvolution.storeChart("$storagePath/lg.svg")
}

fun coevNeatPMP() {

    val controllerEvolution = NeatControllerEvolution(
        NetworkSettings(7, 7, 0, 2),
        populationSize = 50,
        generationsCount = 35,
        levelsPerGeneratorCount = 5,
        showChart = false,
        chartLabel = "Agent NeuroEvolution"
    )

    val levelGeneratorEvolution = PMPLevelGeneratorEvolution(
        populationSize = 50,
        generationsCount = 5,
        evaluateOnLevelsCount = 5,
        fitnessFunction = cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.AgentHalfPassing(),
        displayChart = false,
        chartLabel = "PMP Level Generator"
    )

    val initialLevelGenerator = PMPLevelGenerator.createSimplest()
    val initialController = SimpleANNController(
        UpdatedAgentNetwork(
            receptiveFieldSizeRow = 5,
            receptiveFieldSizeColumn = 5,
            receptiveFieldRowOffset = 0,
            receptiveFieldColumnOffset = 2,
            hiddenLayerSize = 7
        )
    )

    val coevolver = MarioCoEvolver()
    val storagePath = "result/neat_pmp"

    coevolver.evolve(
        controllerEvolution,
        levelGeneratorEvolution,
        initialController,
        initialLevelGenerator,
        MarioGameplayEvaluators::distanceOnly,
        25,
        storagePath
    )

    levelGeneratorEvolution.storeChart("$storagePath/lg.svg")
    controllerEvolution.storeChart("$storagePath/lg.svg")

}

fun coevNeatPC() {

    val controllerEvolution = NeatControllerEvolution(
        NetworkSettings(7, 7, 0, 2),
        populationSize = 50,
        generationsCount = 35,
        levelsPerGeneratorCount = 5,
        showChart = false,
        chartLabel = "Agent NeuroEvolution"
    )

    val levelGeneratorEvolution = ChunksLevelGeneratorEvolution(
        populationSize = 50,
        generationsCount = 5,
        evaluateOnLevelsCount = 5,
        fitnessFunction = AgentHalfPassing(),
        displayChart = false,
        chartLabel = "PC Level Generator"
    )

    val initialLevelGenerator = PCLevelGenerator.createSimplest()
    val initialController = SimpleANNController(
        UpdatedAgentNetwork(
            receptiveFieldSizeRow = 5,
            receptiveFieldSizeColumn = 5,
            receptiveFieldRowOffset = 0,
            receptiveFieldColumnOffset = 2,
            hiddenLayerSize = 7
        )
    )

    val coevolver = MarioCoEvolver()
    val storagePath = "result/neat_pc"

    coevolver.evolve(
        controllerEvolution,
        levelGeneratorEvolution,
        initialController,
        initialLevelGenerator,
        MarioGameplayEvaluators::distanceOnly,
        25,
        storagePath
    )

    levelGeneratorEvolution.storeChart("$storagePath/lg.svg")
    controllerEvolution.storeChart("$storagePath/lg.svg")

}