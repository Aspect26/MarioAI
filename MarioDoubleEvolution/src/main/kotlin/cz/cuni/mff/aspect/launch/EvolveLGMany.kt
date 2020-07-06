package cz.cuni.mff.aspect.launch

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.coevolution.CoevolutionTimer
import cz.cuni.mff.aspect.controllers.GoingRightAndJumpingController
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.PCLevelEvaluator
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.NullEvaluator
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.PMPLevelEvaluator
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.PNGCompressionEvaluator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.storage.ObjectStorage

/**
 * Launches multiple evolutions of Probabilistic Multipass level generator using settings specified by instances of
 * [PMPEvolutionLauncher] class.
 */
fun main() {
    doManyPCEvolution()
//    doManyPMPEvolution()
}

private fun doManyPCEvolution() {
    val experimentsName = "data/experiments/final-experiments/lg/pc/fit+complexity+linearity"
    val generationsCount = 50
    val agentFactory = { MarioAgent(GoingRightAndJumpingController()) }
    val fitnessFunction: PCLevelEvaluator<Float> = cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.All()
    val objectiveFunction: PCLevelEvaluator<Float> = cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.WinRatioEvaluator(0.5f, 1f)
    val evaluateOnLevelsCount = 20

    val launchers = arrayOf(
        PCEvolutionLauncher(
            storageLocation = experimentsName,
            label = "experiment 1",
            agentFactory = agentFactory,
            populationSize = 50,
            generationsCount = generationsCount,
            fitnessFunction = fitnessFunction,
            objectiveFunction = objectiveFunction,
            evaluateOnLevelsCount = evaluateOnLevelsCount
        ),

        PCEvolutionLauncher(
            storageLocation = experimentsName,
            label = "experiment 2",
            agentFactory = agentFactory,
            populationSize = 50,
            generationsCount = generationsCount,
            fitnessFunction = fitnessFunction,
            objectiveFunction = objectiveFunction,
            evaluateOnLevelsCount = evaluateOnLevelsCount
        ),

        PCEvolutionLauncher(
            storageLocation = experimentsName,
            label = "experiment 3",
            agentFactory = agentFactory,
            populationSize = 50,
            generationsCount = generationsCount,
            fitnessFunction = fitnessFunction,
            objectiveFunction = objectiveFunction,
            evaluateOnLevelsCount = evaluateOnLevelsCount
        ),

        PCEvolutionLauncher(
            storageLocation = experimentsName,
            label = "experiment 4",
            agentFactory = agentFactory,
            populationSize = 50,
            generationsCount = generationsCount,
            fitnessFunction = fitnessFunction,
            objectiveFunction = objectiveFunction,
            evaluateOnLevelsCount = evaluateOnLevelsCount
        )
    )

    launchers.forEach { it.launch() }
}


private fun doManyPMPEvolution() {

    val experimentsName = "data/experiments/final-experiments/pmp/complexity"
    val generationsCount = 50
    val agentFactory = { MarioAgent(GoingRightAndJumpingController()) }
    val fitnessFunction: PMPLevelEvaluator<Float> = PNGCompressionEvaluator()
    val objectiveFunction: PMPLevelEvaluator<Float> = NullEvaluator()
    val evaluateOnLevelsCount = 20

    val launchers = arrayOf(
        PMPEvolutionLauncher(
            storageLocation = experimentsName,
            label = "experiment 1",
            agentFactory = agentFactory,
            populationSize = 50,
            generationsCount = generationsCount,
            fitnessFunction = fitnessFunction,
            objectiveFunction = objectiveFunction,
            evaluateOnLevelsCount = evaluateOnLevelsCount
        ),

        PMPEvolutionLauncher(
            storageLocation = experimentsName,
            label = "experiment 2",
            agentFactory = agentFactory,
            populationSize = 50,
            generationsCount = generationsCount,
            fitnessFunction = fitnessFunction,
            objectiveFunction = objectiveFunction,
            evaluateOnLevelsCount = evaluateOnLevelsCount
        ),

        PMPEvolutionLauncher(
            storageLocation = experimentsName,
            label = "experiment 3",
            agentFactory = agentFactory,
            populationSize = 50,
            generationsCount = generationsCount,
            fitnessFunction = fitnessFunction,
            objectiveFunction = objectiveFunction,
            evaluateOnLevelsCount = evaluateOnLevelsCount
        ),

        PMPEvolutionLauncher(
            storageLocation = experimentsName,
            label = "experiment 4",
            agentFactory = agentFactory,
            populationSize = 50,
            generationsCount = generationsCount,
            fitnessFunction = fitnessFunction,
            objectiveFunction = objectiveFunction,
            evaluateOnLevelsCount = evaluateOnLevelsCount
        )
    )

    launchers.forEach { it.launch() }

}

class PMPEvolutionLauncher(
    private val storageLocation: String,
    private val label: String,
    private val agentFactory: () -> IAgent,
    private val populationSize: Int,
    private val generationsCount: Int,
    private val fitnessFunction: PMPLevelEvaluator<Float>,
    private val objectiveFunction: PMPLevelEvaluator<Float>,
    private val evaluateOnLevelsCount: Int
) {
    fun launch()  {
        val timer = CoevolutionTimer()
        val levelEvolution = PMPLevelGeneratorEvolution(
            populationSize = this.populationSize,
            generationsCount = this.generationsCount,
            fitnessFunction = this.fitnessFunction,
            objectiveFunction = this.objectiveFunction,
            evaluateOnLevelsCount = this.evaluateOnLevelsCount,
            chartLabel = this.label
        )

        timer.startControllerEvolution()
        val evolutionResult = levelEvolution.evolve(this.agentFactory)
        timer.stopControllerEvolution()

        ObjectStorage.store("$storageLocation/${label}_lg.lg", evolutionResult.bestLevelGenerator)
        levelEvolution.chart.store("$storageLocation/${label}_chart.svg")
        timer.store("$storageLocation/${label}_time.txt")
    }
}

class PCEvolutionLauncher(
    private val storageLocation: String,
    private val label: String,
    private val agentFactory: () -> IAgent,
    private val populationSize: Int,
    private val generationsCount: Int,
    private val fitnessFunction: PCLevelEvaluator<Float>,
    private val objectiveFunction: PCLevelEvaluator<Float>,
    private val evaluateOnLevelsCount: Int
) {
    fun launch()  {
        val timer = CoevolutionTimer()
        val levelEvolution = PCLevelGeneratorEvolution(
            populationSize = this.populationSize,
            generationsCount = this.generationsCount,
            fitnessFunction = this.fitnessFunction,
            objectiveFunction = this.objectiveFunction,
            evaluateOnLevelsCount = this.evaluateOnLevelsCount,
            chartLabel = this.label
        )

        timer.startControllerEvolution()
        val evolutionResult = levelEvolution.evolve(this.agentFactory)
        timer.stopControllerEvolution()

        ObjectStorage.store("$storageLocation/${label}_lg.lg", evolutionResult.bestLevelGenerator)
        levelEvolution.chart.store("$storageLocation/${label}_chart.svg")
        timer.store("$storageLocation/${label}_time.txt")
    }
}