package cz.cuni.mff.aspect.launch

import com.evo.NEAT.Genome
import cz.cuni.mff.aspect.coevolution.Coevolution
import cz.cuni.mff.aspect.evolution.controller.*
import cz.cuni.mff.aspect.evolution.controller.neat.NeatControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.neuroevolution.NeuroControllerEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.AgentHalfPassing
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.All
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGeneratorEvolution
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.NeatAgentNetwork
import cz.cuni.mff.aspect.mario.controllers.ann.networks.HiddenLayerControllerNetwork
import cz.cuni.mff.aspect.visualisation.charts.CoevolutionLineChart
import io.jenetics.GaussianMutator

private const val generations = 20

/**
 * Launches multiple coevolutions in series. The settings of the evolutions are specified by [NeuroEvolution],
 * [NEATEvolution], [PCEvolution] and [PMPEvolution] objects.
 */
fun main() {
    coevolve("result/neuro_pc", NeuroEvolution, PCEvolution, generations, 5)
//    coevolve("result/neuro_pmp", NeuroEvolution, PMPEvolution, generations)
//    coevolve("result/neat_pc", NEATEvolution, PCEvolution, generations)
//    coevolve("result/neat_pmp", NEATEvolution, PMPEvolution, generations)
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
                fitnessFunction = MarioGameplayEvaluators::distanceOnly,
                objectiveFunction = MarioGameplayEvaluators::victoriesOnly,
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
    private val networkSettings = NetworkSettings(7, 7, 0, 2)
    private val inputsCount = NeatAgentNetwork.inputLayerSize(networkSettings)

    override val evolution: ControllerEvolution
        get() = NeatControllerEvolution(
            networkSettings,
            populationSize = 50,
            generationsCount = 35,
            fitnessFunction = MarioGameplayEvaluators::distanceOnly,
            objectiveFunction = MarioGameplayEvaluators::victoriesOnly,
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
                fitnessFunction = All(),
                objectiveFunction = AgentHalfPassing(),
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
            generationsCount = 5,
            evaluateOnLevelsCount = 5,
            fitnessFunction = cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.AgentHalfPassing(),
            objectiveFunction = cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.AgentHalfPassing(),
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
    val coevolver = Coevolution()

    coevolver.evolve(
        controllerEvolution,
        levelGeneratorEvolution,
        controllerEvolutionSettings.initialController,
        levelGeneratorEvolutionSettings.initialLevelGenerator,
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
