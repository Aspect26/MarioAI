package cz.cuni.mff.aspect.launch

import com.evo.NEAT.Genome
import cz.cuni.mff.aspect.coevolution.MarioCoEvolution
import cz.cuni.mff.aspect.evolution.controller.*
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.pc.PCLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.pc.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pc.evaluators.AgentHalfPassing
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGeneratorEvolution
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.NeatAgentNetwork
import cz.cuni.mff.aspect.mario.controllers.ann.networks.UpdatedAgentNetwork
import cz.cuni.mff.aspect.visualisation.charts.CoevolutionLineChart
import io.jenetics.GaussianMutator

private const val generations = 30

fun main() {
    coevolve("result/neuro_pc", NeuroEvolution, PCEvolution, generations, 10)
//    coevolve("result/neuro_pmp", NeuroEvolution, PMPEvolution, generations)
//    coevolve("result/neat_pc", NEATEvolution, PCEvolution, generations)
//    coevolve("result/neat_pmp", NEATEvolution, PMPEvolution, generations)
}

private interface ControllerEvolutionSettings {
    val evolution: ControllerEvolution
    val initialController: MarioController
    val fitnessFunction: MarioGameplayEvaluator<Float>
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
                generationsCount = 5,
                evaluateOnLevelsCount = 25,
                mutators = arrayOf(GaussianMutator(0.55)),
                parallel = true,
                displayChart = false,
                chartLabel = "Agent NeuroEvolution"
            )

    override val fitnessFunction: MarioGameplayEvaluator<Float>
            get() = MarioGameplayEvaluators::distanceOnly

    override val initialController
            get() = SimpleANNController(
                UpdatedAgentNetwork(
                    receptiveFieldSizeRow = 5,
                    receptiveFieldSizeColumn = 5,
                    receptiveFieldRowOffset = 0,
                    receptiveFieldColumnOffset = 2,
                    hiddenLayerSize = 7
                )
            )
}

private object NEATEvolution : ControllerEvolutionSettings {
    private val networkSettings = NetworkSettings(7, 7, 0, 2)
    private val inputsCount = NeatAgentNetwork(networkSettings, Genome(0, 0)).inputLayerSize

    override val evolution: ControllerEvolution
        get() = NeatControllerEvolution(
            networkSettings,
            populationSize = 50,
            generationsCount = 35,
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

    override val fitnessFunction: MarioGameplayEvaluator<Float>
        get() = MarioGameplayEvaluators::distanceOnly

}

private object PCEvolution : LevelGeneratorEvolutionSettings {
    override val evolution: LevelGeneratorEvolution
            get() =
                PCLevelGeneratorEvolution(
                    populationSize = 50,
                    generationsCount = 3,
                    evaluateOnLevelsCount = 15,
                    fitnessFunction = AgentHalfPassing(),
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
            generationsCount = 5,
            evaluateOnLevelsCount = 5,
            fitnessFunction = cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.AgentHalfPassing(),
            displayChart = false,
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
    val coevolver = MarioCoEvolution()

    coevolver.evolve(
        controllerEvolution,
        levelGeneratorEvolution,
        controllerEvolutionSettings.initialController,
        levelGeneratorEvolutionSettings.initialLevelGenerator,
        controllerEvolutionSettings.fitnessFunction,
        generations,
        repeatGeneratorsCount,
        storagePath
    )

    val controllerChart = controllerEvolution.chart
    val levelGeneratorChart = levelGeneratorEvolution.chart
    val coevolutionChart = CoevolutionLineChart(controllerChart, levelGeneratorChart, "Coevolution")

    controllerChart.store("$storagePath/ai.svg")
    levelGeneratorChart.store("$storagePath/lg.svg")

    coevolutionChart.storeChart("$storagePath/coev.svg")
}
