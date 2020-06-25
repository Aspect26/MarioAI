package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.controller.TrainingLevelsSet
import cz.cuni.mff.aspect.evolution.controller.evaluators.DistanceOnlyEvaluator
import cz.cuni.mff.aspect.evolution.controller.evaluators.DistanceWithLeastActionsEvaluator
import cz.cuni.mff.aspect.evolution.controller.evaluators.MarioGameplayEvaluator
import cz.cuni.mff.aspect.evolution.controller.evaluators.VictoriesOnlyEvaluator
import cz.cuni.mff.aspect.evolution.controller.neat.NeatControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.neuroevolution.NeuroControllerEvolution
import cz.cuni.mff.aspect.evolution.jenetics.alterers.UpdatedGaussianMutator
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.results.LevelGenerators
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.storage.ObjectStorage
import cz.cuni.mff.aspect.visualisation.charts.linechart.AverageLineChart
import cz.cuni.mff.aspect.visualisation.charts.linechart.LineChart
import io.jenetics.*
import io.jenetics.util.DoubleRange

/**
 * Launches multiple Neuroevolutions or NEAT evolutions of AI. Evolution properties are set by instances of
 * [NeuroEvolutionLauncher] and [NeatEvolutionLauncher] classes.
 */
fun main() {
    doManyNeuroEvolution()
//    doManyNEATEvolution()
}


private fun doManyNeuroEvolution() {
    val levels = TrainingLevelsSet
    val levelGenerator = LevelGenerators.StaticGenerator(levels)
    val evaluationName = "final-experiments/neuro/50:50:DO:0.65:7:5x5:false:false:100"

    val generationsCount = 50
    val populationSize = 50
    val fitness = DistanceOnlyEvaluator()
    val mutators = arrayOf<Alterer<DoubleGene, Float>>(UpdatedGaussianMutator(1.0, 0.65))
    val hiddenLayerSize = 7
    val offspringsSelector = TournamentSelector<DoubleGene, Float>(2)
    val networkSettings = NetworkSettings(5, 5, 0, 2,
        hiddenLayerSize, denseInput = false, oneHotOnEnemies = false)
    val evaluateOnLevelsCount = levels.size
    val weightsRange = DoubleRange.of(-100.0, 100.0)

    val evolutions = arrayOf(
        NeuroEvolutionLauncher(
            networkSettings = networkSettings,
            levelGenerator = levelGenerator,
            fitnessFunction = fitness,
            objectiveFunction = VictoriesOnlyEvaluator(),
            mutators = mutators,
            survivorsSelector = EliteSelector(2),
            offspringSelector = offspringsSelector,
            generationsCount = generationsCount,
            populationSize = populationSize,
            evaluateOnLevelsCount = evaluateOnLevelsCount,
            weightsRange = weightsRange,
            label = "NeuroEvolution, experiment 1",
            runParallel = true,
            alwaysReevaluate = false,
            dataLocation = evaluationName
        ),

        NeuroEvolutionLauncher(
            networkSettings = networkSettings,
            levelGenerator = levelGenerator,
            fitnessFunction = fitness,
            objectiveFunction = VictoriesOnlyEvaluator(),
            mutators = mutators,
            survivorsSelector = EliteSelector(2),
            offspringSelector = offspringsSelector,
            generationsCount = generationsCount,
            populationSize = populationSize,
            evaluateOnLevelsCount = evaluateOnLevelsCount,
            weightsRange = weightsRange,
            label = "NeuroEvolution, experiment 2",
            runParallel = true,
            alwaysReevaluate = false,
            dataLocation = evaluationName
        ),

        NeuroEvolutionLauncher(
            networkSettings = networkSettings,
            levelGenerator = levelGenerator,
            fitnessFunction = fitness,
            objectiveFunction = VictoriesOnlyEvaluator(),
            mutators = mutators,
            survivorsSelector = EliteSelector(2),
            offspringSelector = offspringsSelector,
            generationsCount = generationsCount,
            populationSize = populationSize,
            evaluateOnLevelsCount = evaluateOnLevelsCount,
            weightsRange = weightsRange,
            label = "NeuroEvolution, experiment 3",
            runParallel = true,
            alwaysReevaluate = false,
            dataLocation = evaluationName
        ),

        NeuroEvolutionLauncher(
            networkSettings = networkSettings,
            levelGenerator = levelGenerator,
            fitnessFunction = fitness,
            objectiveFunction = VictoriesOnlyEvaluator(),
            mutators = mutators,
            survivorsSelector = EliteSelector(2),
            offspringSelector = offspringsSelector,
            generationsCount = generationsCount,
            populationSize = populationSize,
            evaluateOnLevelsCount = evaluateOnLevelsCount,
            weightsRange = weightsRange,
            label = "NeuroEvolution, experiment 4",
            runParallel = true,
            alwaysReevaluate = false,
            dataLocation = evaluationName
        )
    )

    evolutions.forEach {
        it.run()
    }
    createAverageChart(evaluationName, "NeuroEvolution")
}


private fun doManyNEATEvolution() {
    val levelGenerators = listOf(LevelGenerators.PCGenerator.all)
    val evaluationName = "final-experiments/neat/500:100:DWP:5x5:false:false"

    val generationsCount = 500
    val populationSize = 100
    val fitness = DistanceWithLeastActionsEvaluator()
    val receptiveFieldSize = Pair(5, 5)
    val evaluateOnLevelsCount = 7
    val networkSettings = NetworkSettings(
        5, 5, 0, 2, 0,
        false, false
    )

    val evolutions = arrayOf(
        NeatEvolutionLauncher(
            networkSettings = networkSettings,
            levelGenerators = levelGenerators,
            fitnessFunction = fitness,
            objectiveFunction = VictoriesOnlyEvaluator(),
            generationsCount = generationsCount,
            populationSize = populationSize,
            evaluateOnLevelsCount = evaluateOnLevelsCount,
            label = "NEAT evolution, experiment 1",
            dataLocation = evaluationName
        ),
        NeatEvolutionLauncher(
            networkSettings = networkSettings,
            levelGenerators = levelGenerators,
            fitnessFunction = fitness,
            objectiveFunction = VictoriesOnlyEvaluator(),
            generationsCount = generationsCount,
            populationSize = populationSize,
            evaluateOnLevelsCount = evaluateOnLevelsCount,
            label = "NEAT evolution, experiment 2",
            dataLocation = evaluationName
        ),
        NeatEvolutionLauncher(
            networkSettings = networkSettings,
            levelGenerators = levelGenerators,
            fitnessFunction = fitness,
            objectiveFunction = VictoriesOnlyEvaluator(),
            generationsCount = generationsCount,
            populationSize = populationSize,
            evaluateOnLevelsCount = evaluateOnLevelsCount,
            label = "NEAT evolution, experiment 3",
            dataLocation = evaluationName
        ),
        NeatEvolutionLauncher(
            networkSettings = networkSettings,
            levelGenerators = levelGenerators,
            fitnessFunction = fitness,
            objectiveFunction = VictoriesOnlyEvaluator(),
            generationsCount = generationsCount,
            populationSize = populationSize,
            evaluateOnLevelsCount = evaluateOnLevelsCount,
            label = "NEAT evolution, experiment 4",
            dataLocation = evaluationName
        )
    )

    evolutions.forEach {
        it.run()
    }
    createAverageChart(evaluationName, "NEAT evolution")
}


private interface EvolutionLauncher {
    fun run()
}

private class NeuroEvolutionLauncher(
    private val networkSettings: NetworkSettings,
    private val levelGenerator: LevelGenerator,
    private val generationsCount: Int,
    private val populationSize: Int,
    private val label: String,
    private val fitnessFunction: MarioGameplayEvaluator,
    private val objectiveFunction: MarioGameplayEvaluator,
    private val mutators: Array<Alterer<DoubleGene, Float>>,
    private val survivorsSelector: Selector<DoubleGene, Float>,
    private val offspringSelector: Selector<DoubleGene, Float>,
    private val evaluateOnLevelsCount: Int,
    private val weightsRange: DoubleRange,
    private val runParallel: Boolean,
    private val alwaysReevaluate: Boolean,
    private val dataLocation: String
) : EvolutionLauncher {

    override fun run() {
        val controllerEvolution =
            NeuroControllerEvolution(
                networkSettings,
                generationsCount,
                populationSize,
                fitnessFunction = fitnessFunction,
                objectiveFunction = objectiveFunction,
                chartLabel = label,
                alterers = mutators,
                survivorsSelector = survivorsSelector,
                offspringSelector = offspringSelector,
                weightsRange = weightsRange,
                parallel = runParallel,
                evaluateOnLevelsCount = evaluateOnLevelsCount,
                alwaysReevaluate = alwaysReevaluate
            )

        val resultController = controllerEvolution.evolve(listOf(levelGenerator))
        controllerEvolution.chart.store("data/experiments/$dataLocation/${label}_chart.svg")
        ObjectStorage.store("data/experiments/$dataLocation/${label}_ai.ai", resultController.bestController)
    }
}

class NeatEvolutionLauncher(
    private val networkSettings: NetworkSettings,
    private val levelGenerators: List<LevelGenerator>,
    private val generationsCount: Int,
    private val populationSize: Int,
    private val label: String,
    private val fitnessFunction: MarioGameplayEvaluator,
    private val objectiveFunction: MarioGameplayEvaluator,
    private val evaluateOnLevelsCount: Int,
    private val dataLocation: String
) : EvolutionLauncher {

    override fun run() {

        val controllerEvolution =
            NeatControllerEvolution(
                networkSettings = networkSettings,
                generationsCount = generationsCount,
                populationSize = populationSize,
                fitnessFunction = fitnessFunction,
                objectiveFunction = objectiveFunction,
                chartLabel = label,
                evaluateOnLevelsCount = evaluateOnLevelsCount
            )

        val resultController = controllerEvolution.evolve(levelGenerators)
        controllerEvolution.chart.store("data/experiments/$dataLocation/${label}_chart.svg")
        ObjectStorage.store("data/experiments/$dataLocation/${label}_ai.ai", resultController.bestController)
    }

}

private fun createAverageChart(dataLocation: String, filesPrefix: String) {
    val lineCharts = (1 .. 4)
        .map{ LineChart.loadFromFile("data/experiments/$dataLocation/$filesPrefix, experiment ${it}_chart.svg.dat") }

    val averageChart = AverageLineChart(lineCharts.toTypedArray())
    averageChart.renderChart()
    averageChart.storeChart("data/experiments/$dataLocation/average_chart.svg")
}
