package cz.cuni.mff.aspect.launch

import com.evo.NEAT.Genome
import cz.cuni.mff.aspect.coevolution.Coevolution
import cz.cuni.mff.aspect.coevolution.CoevolutionSettings
import cz.cuni.mff.aspect.evolution.controller.*
import cz.cuni.mff.aspect.evolution.controller.evaluators.DistanceOnlyEvaluator
import cz.cuni.mff.aspect.evolution.controller.evaluators.VictoriesOnlyEvaluator
import cz.cuni.mff.aspect.evolution.controller.neat.NeatControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.neuroevolution.NeuroControllerEvolution
import cz.cuni.mff.aspect.evolution.jenetics.alterers.UpdatedGaussianMutator
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.All
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.WinRatioEvaluator
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.NeatAgentNetwork
import cz.cuni.mff.aspect.mario.controllers.ann.networks.HiddenLayerControllerNetwork
import cz.cuni.mff.aspect.storage.ObjectStorage
import cz.cuni.mff.aspect.visualisation.charts.evolution.CoevolutionLineChart
import io.jenetics.GaussianMutator

private const val generations = 25
private const val repeatGeneratorsCount = 5

/**
 * Launches multiple coevolutions in series. The settings of the evolutions are specified by [NeuroEvolution],
 * [NEATEvolution], [PCEvolution] and [PMPEvolution] objects. It is also able to showcase the result of the coevolution
 * by playing one level from each coevolution step.
 */
fun main() {
    coevolve("result/neuro_pc", NeuroEvolution, PCEvolution, generations, repeatGeneratorsCount)
//    coevolve("result/neuro_pmp", NeuroEvolution, PMPEvolution, generations, repeatGeneratorsCount)
//    coevolve("result/neat_pc", NEATEvolution, PCEvolution, generations, repeatGeneratorsCount)
//    coevolve("result/neat_pmp", NEATEvolution, PMPEvolution, generations, repeatGeneratorsCount)

//    continueCoevolution("result/neuro_pmp", NeuroEvolution, PMPEvolution, generations, repeatGeneratorsCount)

//    playCoevolution("data/coev/14_pc_preserve_lgs")
}

private interface ControllerEvolutionSettings {
    val evolution: ControllerEvolution
    val initialController: MarioController
}

private interface LevelGeneratorEvolutionSettings {
    val evolution: LevelGeneratorEvolution
    val initialLevelGenerator: LevelGenerator
}

private object NeuroEvolution : ControllerEvolutionSettings {
    override val evolution
            get() = NeuroControllerEvolution(
                null,
                populationSize = 50,
                generationsCount = 35,
                fitnessFunction = DistanceOnlyEvaluator(),
                objectiveFunction = VictoriesOnlyEvaluator(),
                evaluateOnLevelsCount = 25,
                alterers = arrayOf(GaussianMutator(0.55)),
                parallel = true,
                displayChart = false,
                chartLabel = "Agent NeuroEvolution"
            )

    override val initialController
            get() = SimpleANNController(
                HiddenLayerControllerNetwork(NetworkSettings(
                    receptiveFieldSizeRow = 5,
                    receptiveFieldSizeColumn = 5,
                    receptiveFieldRowOffset = 0,
                    receptiveFieldColumnOffset = 2,
                    hiddenLayerSize = 7,
                    denseInput = true,
                    oneHotOnEnemies = false
                ))
            )
}

private object NEATEvolution : ControllerEvolutionSettings {
    private val networkSettings = NetworkSettings(5, 5, 0, 2,
        denseInput = false,
        oneHotOnEnemies = false)
    private val inputsCount = NeatAgentNetwork.inputLayerSize(networkSettings)

    override val evolution: ControllerEvolution
        get() = NeatControllerEvolution(
            networkSettings,
            populationSize = 100,
            generationsCount = 75,
            fitnessFunction = DistanceOnlyEvaluator(),
            objectiveFunction = VictoriesOnlyEvaluator(),
            evaluateOnLevelsCount = 25,
            displayChart = false,
            chartLabel = "Agent NeuroEvolution"
        )

    override val initialController: MarioController
        get() = SimpleANNController(
            NeatAgentNetwork(
                networkSettings,
                Genome(inputsCount, 4)
            )
        )

}

private object PCEvolution : LevelGeneratorEvolutionSettings {
    override val evolution: LevelGeneratorEvolution
        get() =
            PCLevelGeneratorEvolution(
                populationSize = 50,
                generationsCount = 15,
                evaluateOnLevelsCount = 36,
                fitnessFunction = All(0.75f),
                objectiveFunction = cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.WinRatioEvaluator(0.75f, 50000f),
                chunksCount = 55,
                displayChart = false,
                chartLabel = "PC Level Generator"
            )

    override val initialLevelGenerator: LevelGenerator
        get() = PCLevelGenerator.createSimplest()

}

private object PMPEvolution : LevelGeneratorEvolutionSettings {
    override val evolution: LevelGeneratorEvolution
        get() = PMPLevelGeneratorEvolution(
            populationSize = 50,
            generationsCount = 35,
            evaluateOnLevelsCount = 30,
            fitnessFunction = cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.All(0.5f),
            objectiveFunction = WinRatioEvaluator(0.5f, 50000f),
            alterers = arrayOf(UpdatedGaussianMutator(0.03, 0.1) /*, SinglePointCrossover(0.2)*/),
            displayChart = true,
            levelLength = 300,
            chartLabel = "PMP Level Generator"
        )

    override val initialLevelGenerator: LevelGenerator
        get() = PMPLevelGenerator.createSimplest()

}

private fun coevolve(
    storagePath: String,
    controllerEvolutionSettings: ControllerEvolutionSettings,
    levelGeneratorEvolutionSettings: LevelGeneratorEvolutionSettings,
    generations: Int,
    repeatGeneratorsCount: Int
) {
    val controllerEvolution = controllerEvolutionSettings.evolution
    val levelGeneratorEvolution = levelGeneratorEvolutionSettings.evolution
    val coevolutionSettings = CoevolutionSettings(
        controllerEvolution,
        levelGeneratorEvolution,
        controllerEvolutionSettings.initialController,
        levelGeneratorEvolutionSettings.initialLevelGenerator,
        generations,
        repeatGeneratorsCount,
        storagePath
    )

    val coevolver = Coevolution()

    coevolver.startEvolution(coevolutionSettings)

    val controllerChart = controllerEvolution.chart
    val levelGeneratorChart = levelGeneratorEvolution.chart
    val coevolutionChart = CoevolutionLineChart(
        controllerChart,
        levelGeneratorChart,
        "Coevolution"
    )

    controllerChart.store("$storagePath/ai.svg")
    levelGeneratorChart.store("$storagePath/lg.svg")

    coevolutionChart.storeChart("$storagePath/coev.svg")
}

private fun continueCoevolution(
    storagePath: String,
    controllerEvolutionSettings: ControllerEvolutionSettings,
    levelGeneratorEvolutionSettings: LevelGeneratorEvolutionSettings,
    generations: Int,
    repeatGeneratorsCount: Int
) {
    val controllerEvolution = controllerEvolutionSettings.evolution
    val levelGeneratorEvolution = levelGeneratorEvolutionSettings.evolution
    val coevolutionSettings = CoevolutionSettings(
        controllerEvolution,
        levelGeneratorEvolution,
        controllerEvolutionSettings.initialController,
        levelGeneratorEvolutionSettings.initialLevelGenerator,
        generations,
        repeatGeneratorsCount,
        storagePath
    )

    val coevolver = Coevolution()

    coevolver.startEvolution(coevolutionSettings)

    val controllerChart = controllerEvolution.chart
    val levelGeneratorChart = levelGeneratorEvolution.chart
    val coevolutionChart = CoevolutionLineChart(
        controllerChart,
        levelGeneratorChart,
        "Coevolution"
    )

    controllerChart.store("$storagePath/ai.svg")
    levelGeneratorChart.store("$storagePath/lg.svg")

    coevolutionChart.storeChart("$storagePath/coev.svg")
}

private fun playCoevolution(dataPath: String) {
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

        currentController = ObjectStorage.load("$dataPath/ai_$i.ai") as MarioController
//        println("AI update - ${MarioGameplayEvaluators.victoriesOnly(Array(10) { simulator.playMario(currentController, currentGenerator.generate(), false) }) / 1000}")
//        println("AI update - ${MarioGameplayEvaluators.victoriesOnly(Array(10) { simulator.playMario(currentController, currentGenerator.generate(), false) }) / 1000}")
//        println("AI update - ${MarioGameplayEvaluators.victoriesOnly(Array(10) { simulator.playMario(currentController, currentGenerator.generate(), false) }) / 1000}")
//        println("AI update - ${MarioGameplayEvaluators.victoriesOnly(Array(10) { simulator.playMario(currentController, currentGenerator.generate(), false) }) / 1000}")
        simulator.playMario(currentController, LevelPostProcessor.postProcess(currentGenerator.generate(), false))

        currentGenerator = ObjectStorage.load("$dataPath/lg_$i.lg") as LevelGenerator
        evalLg(currentGenerator, currentController)
//        repeat(5) { LevelVisualiser().display(currentGenerator.generate()) }
//        println("Generator update; LG evaluation - ${AgentHalfPassing()(currentGenerator as PCLevelGenerator, {MarioAgent(currentController)}, 100) / 1000}")
//        println("Generator update; LG evaluation - ${AgentHalfPassing()(currentGenerator as PCLevelGenerator, {MarioAgent(currentController)}, 100) / 1000}")
//        println("Generator update; LG evaluation - ${AgentHalfPassing()(currentGenerator as PCLevelGenerator, {MarioAgent(currentController)}, 100) / 1000}")
//        println("Generator update; LG evaluation - ${AgentHalfPassing()(currentGenerator as PCLevelGenerator, {MarioAgent(currentController)}, 100) / 1000}")
        simulator.playMario(currentController, LevelPostProcessor.postProcess(currentGenerator.generate(), false))
    }

}

private fun evalLg(levelGenerator: LevelGenerator, controller: MarioController) {
    val simulator = GameSimulator()
    val wins = (0 until 100).map { if (simulator.playMario(MarioAgent(controller), levelGenerator.generate(), false).levelFinished) 1 else 0 }.sum()
    println("LG eval -> $wins/100")
}