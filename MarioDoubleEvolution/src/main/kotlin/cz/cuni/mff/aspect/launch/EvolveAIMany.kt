package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.controller.evaluators.DistanceOnlyEvaluator
import cz.cuni.mff.aspect.evolution.controller.evaluators.DistanceWithLeastActionsEvaluator
import cz.cuni.mff.aspect.evolution.controller.evaluators.MarioGameplayEvaluator
import cz.cuni.mff.aspect.evolution.controller.evaluators.VictoriesOnlyEvaluator
import cz.cuni.mff.aspect.evolution.controller.neat.NeatControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.neuroevolution.NeuroControllerEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.results.LevelGenerators
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.storage.ObjectStorage
import io.jenetics.*
import io.jenetics.util.DoubleRange

/**
 * Launches multiple Neuroevolutions or NEAT evolutions of AI. Evolution properties are set by instances of
 * [NeuroEvolutionLauncher] and [NeatEvolutionLauncher] classes.
 */
fun main() {
//    doManyNeuroEvolution()
    doManyNEATEvolution()
}


private fun doManyNEATEvolution() {
    val levelGenerators = listOf(LevelGenerators.PCGenerator.all)
    val evaluationName = "NEAT - All - 500-100 - fitness distance least actions"

    val generationsCount = 500
    val populationSize = 100
    val fitness = DistanceWithLeastActionsEvaluator()
    val receptiveFieldSize = Pair(5, 5)

    val evolutions = arrayOf(
        NeatEvolutionLauncher(
            levelGenerators = levelGenerators,
            fitnessFunction = fitness,
            objectiveFunction = VictoriesOnlyEvaluator(),
            generationsCount = generationsCount,
            populationSize = populationSize,
            receptiveFieldSize = receptiveFieldSize,
            receptiveFieldOffset = Pair(0, 2),
            label = "NEAT evolution, experiment 1",
            dataLocation = evaluationName,
            denseInput = false
        ),
        NeatEvolutionLauncher(
            levelGenerators = levelGenerators,
            fitnessFunction = fitness,
            objectiveFunction = VictoriesOnlyEvaluator(),
            generationsCount = generationsCount,
            populationSize = populationSize,
            receptiveFieldSize = receptiveFieldSize,
            receptiveFieldOffset = Pair(0, 2),
            label = "NEAT evolution, experiment 2",
            dataLocation = evaluationName,
            denseInput = false
        ),
        NeatEvolutionLauncher(
            levelGenerators = levelGenerators,
            fitnessFunction = fitness,
            objectiveFunction = VictoriesOnlyEvaluator(),
            generationsCount = generationsCount,
            populationSize = populationSize,
            receptiveFieldSize = receptiveFieldSize,
            receptiveFieldOffset = Pair(0, 2),
            label = "NEAT evolution, experiment 3",
            dataLocation = evaluationName,
            denseInput = false
        ),
        NeatEvolutionLauncher(
            levelGenerators = levelGenerators,
            fitnessFunction = fitness,
            objectiveFunction = VictoriesOnlyEvaluator(),
            generationsCount = generationsCount,
            populationSize = populationSize,
            receptiveFieldSize = receptiveFieldSize,
            receptiveFieldOffset = Pair(0, 2),
            label = "NEAT evolution, experiment 4",
            dataLocation = evaluationName,
            denseInput = false
        )
    )

    evolutions.forEach {
        it.run()
    }
}


private fun doManyNeuroEvolution() {
    val levelGenerator = LevelGenerators.PCGenerator.all
    val evaluationName = "Newest"

    val generationsCount = 50
    val populationSize = 50
    val fitness = DistanceOnlyEvaluator()
    val mutators = arrayOf<Alterer<DoubleGene, Float>>(GaussianMutator(0.45))
    val hiddenLayerSize = 5
    val offspringsSelector = TournamentSelector<DoubleGene, Float>(2)
    val networkSettings = NetworkSettings(5, 5, 0, 2, hiddenLayerSize)

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
            weightsRange = DoubleRange.of(-2.0, 2.0),
            label = "NeuroEvolution, experiment 1",
            runParallel = true,
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
            weightsRange = DoubleRange.of(-2.0, 2.0),
            label = "NeuroEvolution, experiment 2",
            runParallel = true,
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
            weightsRange = DoubleRange.of(-2.0, 2.0),
            label = "NeuroEvolution, experiment 3",
            runParallel = true,
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
            weightsRange = DoubleRange.of(-2.0, 2.0),
            label = "NeuroEvolution, experiment 4",
            runParallel = true,
            dataLocation = evaluationName
        )
    )

    evolutions.forEach {
        it.run()
    }
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
    private val weightsRange: DoubleRange,
    private val runParallel: Boolean,
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
                parallel = runParallel
            )

        val resultController = controllerEvolution.evolve(listOf(levelGenerator))
        controllerEvolution.chart.store("data/experiments/$dataLocation/${label}_chart.svg")
        ObjectStorage.store("data/experiments/$dataLocation/${label}_ai.ai", resultController)
    }
}

class NeatEvolutionLauncher(
    private val levelGenerators: List<LevelGenerator>,
    private val generationsCount: Int,
    private val populationSize: Int,
    private val receptiveFieldSize: Pair<Int, Int>,
    private val receptiveFieldOffset: Pair<Int, Int>,
    private val label: String,
    private val fitnessFunction: MarioGameplayEvaluator,
    private val objectiveFunction: MarioGameplayEvaluator,
    private val dataLocation: String,
    private val denseInput: Boolean
) : EvolutionLauncher {

    override fun run() {
        val networkSettings = NetworkSettings(
            receptiveFieldSize.first,
            receptiveFieldSize.second,
            receptiveFieldOffset.first,
            receptiveFieldOffset.second,
            denseInput = denseInput)

        val controllerEvolution =
            NeatControllerEvolution(
                networkSettings,
                generationsCount = generationsCount,
                populationSize = populationSize,
                fitnessFunction = fitnessFunction,
                objectiveFunction = objectiveFunction,
                chartLabel = label
            )

        val resultController = controllerEvolution.evolve(levelGenerators)
        controllerEvolution.chart.store("data/experiments/$dataLocation/${label}_chart.svg")
        ObjectStorage.store("data/experiments/$dataLocation/${label}_ai.ai", resultController)
    }

}
