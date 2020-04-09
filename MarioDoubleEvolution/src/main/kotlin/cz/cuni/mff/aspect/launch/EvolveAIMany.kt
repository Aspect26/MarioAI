package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.controller.NeatControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.NeuroControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.TrainingLevelsSet
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluator
import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.networks.UpdatedAgentNetwork
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.mario.level.custom.PathWithHolesLevel
import cz.cuni.mff.aspect.mario.level.original.*
import cz.cuni.mff.aspect.storage.ObjectStorage
import io.jenetics.*
import io.jenetics.util.DoubleRange


fun main() {
//    doManyNeuroEvolution()
    doManyNEATEvolution()
}

// TODO: something weird is happening - when training on only 1 level, the objective can reach 1 and then drop back to 0 with fitness distanceOnly
// fitness is also dropping even with elite selector...


fun doManyNEATEvolution() {
    val learningLevels = TrainingLevelsSet
    val evaluationName = "NEAT - All - 500-100 - fitness distance least actions"

    val generationsCount = 500
    val populationSize = 100
    val fitness = MarioGameplayEvaluators::distanceLeastActions
    val receptiveFieldSize = Pair(5, 5)

    val evolutions = arrayOf(
        NeatEvolutionLauncher(
            levels = learningLevels,
            fitnessFunction = fitness,
            objectiveFunction = MarioGameplayEvaluators::victoriesOnly,
            generationsCount = generationsCount,
            populationSize = populationSize,
            receptiveFieldSize = receptiveFieldSize,
            receptiveFieldOffset = Pair(0, 2),
            label = "NEAT evolution, experiment 1",
            dataLocation = evaluationName,
            denseInput = false
        ),
        NeatEvolutionLauncher(
            levels = learningLevels,
            fitnessFunction = fitness,
            objectiveFunction = MarioGameplayEvaluators::victoriesOnly,
            generationsCount = generationsCount,
            populationSize = populationSize,
            receptiveFieldSize = receptiveFieldSize,
            receptiveFieldOffset = Pair(0, 2),
            label = "NEAT evolution, experiment 2",
            dataLocation = evaluationName,
            denseInput = false
        ),
        NeatEvolutionLauncher(
            levels = learningLevels,
            fitnessFunction = fitness,
            objectiveFunction = MarioGameplayEvaluators::victoriesOnly,
            generationsCount = generationsCount,
            populationSize = populationSize,
            receptiveFieldSize = receptiveFieldSize,
            receptiveFieldOffset = Pair(0, 2),
            label = "NEAT evolution, experiment 3",
            dataLocation = evaluationName,
            denseInput = false
        ),
        NeatEvolutionLauncher(
            levels = learningLevels,
            fitnessFunction = fitness,
            objectiveFunction = MarioGameplayEvaluators::victoriesOnly,
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


fun doManyNeuroEvolution() {
    val learningLevels = arrayOf<MarioLevel>(*Stage4Level1Split.levels) + PathWithHolesLevel
    val evaluationName = "Newest"

    val generationsCount = 50
    val populationSize = 50
    val fitness = MarioGameplayEvaluators::distanceOnly
    val mutators = arrayOf<Alterer<DoubleGene, Float>>(GaussianMutator(0.45))
    val hiddenLayerSize = 5
    val offspringsSelector = TournamentSelector<DoubleGene, Float>(2)

    val evolutions = arrayOf(
        NeuroEvolutionLauncher(
            levels = learningLevels,
            fitnessFunction = fitness,
            objectiveFunction = MarioGameplayEvaluators::victoriesOnly,
            mutators = mutators,
            survivorsSelector = EliteSelector(2),
            offspringSelector = offspringsSelector,
            generationsCount = generationsCount,
            populationSize = populationSize,
            receptiveFieldSize = Pair(5, 5),
            receptiveFieldOffset = Pair(0, 2),
            hiddenLayerSize = hiddenLayerSize,
            weightsRange = DoubleRange.of(-2.0, 2.0),
            label = "NeuroEvolution, experiment 1",
            runParallel = true,
            dataLocation = evaluationName
        ),

        NeuroEvolutionLauncher(
            levels = learningLevels,
            fitnessFunction = fitness,
            objectiveFunction = MarioGameplayEvaluators::victoriesOnly,
            mutators = mutators,
            survivorsSelector = EliteSelector(2),
            offspringSelector = offspringsSelector,
            generationsCount = generationsCount,
            populationSize = populationSize,
            receptiveFieldSize = Pair(5, 5),
            receptiveFieldOffset = Pair(0, 2),
            hiddenLayerSize = hiddenLayerSize,
            weightsRange = DoubleRange.of(-2.0, 2.0),
            label = "NeuroEvolution, experiment 2",
            runParallel = true,
            dataLocation = evaluationName
        ),

        NeuroEvolutionLauncher(
            levels = learningLevels,
            fitnessFunction = fitness,
            objectiveFunction = MarioGameplayEvaluators::victoriesOnly,
            mutators = mutators,
            survivorsSelector = EliteSelector(2),
            offspringSelector = offspringsSelector,
            generationsCount = generationsCount,
            populationSize = populationSize,
            receptiveFieldSize = Pair(5, 5),
            receptiveFieldOffset = Pair(0, 2),
            hiddenLayerSize = hiddenLayerSize,
            weightsRange = DoubleRange.of(-2.0, 2.0),
            label = "NeuroEvolution, experiment 3",
            runParallel = true,
            dataLocation = evaluationName
        ),

        NeuroEvolutionLauncher(
            levels = learningLevels,
            fitnessFunction = fitness,
            objectiveFunction = MarioGameplayEvaluators::victoriesOnly,
            mutators = mutators,
            survivorsSelector = EliteSelector(2),
            offspringSelector = offspringsSelector,
            generationsCount = generationsCount,
            populationSize = populationSize,
            receptiveFieldSize = Pair(5, 5),
            receptiveFieldOffset = Pair(0, 2),
            hiddenLayerSize = hiddenLayerSize,
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


interface EvolutionLauncher {
    fun run()
}

class NeuroEvolutionLauncher(
    private val levels: Array<MarioLevel>,
    private val generationsCount: Int,
    private val populationSize: Int,
    private val receptiveFieldSize: Pair<Int, Int>,
    private val receptiveFieldOffset: Pair<Int, Int>,
    private val hiddenLayerSize: Int,
    private val label: String,
    private val fitnessFunction: MarioGameplayEvaluator<Float>,
    private val objectiveFunction: MarioGameplayEvaluator<Float>,
    private val mutators: Array<Alterer<DoubleGene, Float>>,
    private val survivorsSelector: Selector<DoubleGene, Float>,
    private val offspringSelector: Selector<DoubleGene, Float>,
    private val weightsRange: DoubleRange,
    private val runParallel: Boolean,
    private val dataLocation: String
) : EvolutionLauncher {

    override fun run() {
        val controllerANN = UpdatedAgentNetwork(
            receptiveFieldSize.first,
            receptiveFieldSize.second,
            receptiveFieldOffset.first,
            receptiveFieldOffset.second,
            hiddenLayerSize
        )

        val controllerEvolution = NeuroControllerEvolution(
            controllerANN,
            generationsCount.toLong(),
            populationSize,
            chartLabel = label,
            mutators = mutators,
            survivorsSelector = survivorsSelector,
            offspringSelector = offspringSelector,
            weightsRange = weightsRange,
            parallel = runParallel
        )

        val resultController = controllerEvolution.evolve(levels, fitnessFunction, objectiveFunction)
        controllerEvolution.storeChart("data/experiments/$dataLocation/${label}_chart.svg")
        ObjectStorage.store("data/experiments/$dataLocation/${label}_ai.ai", resultController)
    }
}

class NeatEvolutionLauncher(
    private val levels: Array<MarioLevel>,
    private val generationsCount: Int,
    private val populationSize: Int,
    private val receptiveFieldSize: Pair<Int, Int>,
    private val receptiveFieldOffset: Pair<Int, Int>,
    private val label: String,
    private val fitnessFunction: MarioGameplayEvaluator<Float>,
    private val objectiveFunction: MarioGameplayEvaluator<Float>,
    private val dataLocation: String,
    private val denseInput: Boolean
) : EvolutionLauncher {

    override fun run() {
        val networkSettings = NetworkSettings(receptiveFieldSize.first, receptiveFieldSize.second, receptiveFieldOffset.first, receptiveFieldOffset.second)
        val controllerEvolution = NeatControllerEvolution(
            networkSettings,
            generationsCount = generationsCount,
            populationSize = populationSize,
            denseInput = denseInput,
            chartName = label)

        val resultController = controllerEvolution.evolve(levels, fitnessFunction, objectiveFunction)
        controllerEvolution.storeChart("data/experiments/$dataLocation/${label}_chart.svg")
        ObjectStorage.store("data/experiments/$dataLocation/${label}_ai.ai", resultController)
    }

}