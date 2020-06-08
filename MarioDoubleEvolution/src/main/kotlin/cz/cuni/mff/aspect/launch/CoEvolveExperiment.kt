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
    println("STARTING EXPERIMENTS!")
    val experimentNumber = 1

    coevolve("result/$experimentNumber/neuro_pc", NeuroEvolutionLarge, PCEvolutionLarge, generations, repeatGeneratorsCount)
    coevolve("result/$experimentNumber/neuro_pmp", NeuroEvolutionLarge, PMPEvolutionLarge, generations, repeatGeneratorsCount)
//    coevolve("result/neat_pc", NEATEvolutionLarge, PCEvolutionLarge, generations, repeatGeneratorsCount)
//    coevolve("result/neat_pmp", NEATEvolutionLarge, PMPEvolutionLarge, generations, repeatGeneratorsCount)
}

private object NeuroEvolutionLarge : ControllerEvolutionSettings {
    private val networkSettings = NetworkSettings(
        receptiveFieldSizeRow = 5,
        receptiveFieldSizeColumn = 5,
        receptiveFieldRowOffset = 0,
        receptiveFieldColumnOffset = 2,
        hiddenLayerSize = 5,
        denseInput = false,
        oneHotOnEnemies = false
    )

    override val evolution
            get() = NeuroControllerEvolution(
                networkSettings,
                populationSize = 50,
                generationsCount = 50,
                fitnessFunction = DistanceOnlyEvaluator(),
                objectiveFunction = VictoriesOnlyEvaluator(),
                evaluateOnLevelsCount = 25,
                alterers = arrayOf(UpdatedGaussianMutator(1.0, 0.05)),
                parallel = true,
                displayChart = false,
                chartLabel = "Agent NeuroEvolution"
            )

    override val initialController
            get() = SimpleANNController(HiddenLayerControllerNetwork(networkSettings))
}

private object NEATEvolutionLarge : ControllerEvolutionSettings {
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

private object PCEvolutionLarge : LevelGeneratorEvolutionSettings<PCLevelGenerator> {
    override val evolution: LevelGeneratorEvolution<PCLevelGenerator>
        get() =
            PCLevelGeneratorEvolution(
                populationSize = 50,
                generationsCount = 15,
                evaluateOnLevelsCount = 36,
                fitnessFunction = All(0.5f),
                objectiveFunction = cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.WinRatioEvaluator(0.5f, 50000f),
                chunksCount = 55,
                displayChart = false,
                chartLabel = "PC Level Generator"
            )

    override val initialLevelGenerator: PCLevelGenerator
        get() = PCLevelGenerator.createSimplest()

}

private object PMPEvolutionLarge : LevelGeneratorEvolutionSettings<PMPLevelGenerator> {
    override val evolution: LevelGeneratorEvolution<PMPLevelGenerator>
        get() = PMPLevelGeneratorEvolution(
            populationSize = 50,
            generationsCount = 35,
            evaluateOnLevelsCount = 30,
            fitnessFunction = cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.All(0.5f),
            objectiveFunction = WinRatioEvaluator(0.5f, 50000f),
            alterers = arrayOf(UpdatedGaussianMutator(0.03, 0.1) /*, SinglePointCrossover(0.2)*/),
            displayChart = false,
            levelLength = 300,
            chartLabel = "PMP Level Generator"
        )

    override val initialLevelGenerator: PMPLevelGenerator
        get() = PMPLevelGenerator.createSimplest()

}
