package cz.cuni.mff.aspect.launch

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.ge.GrammarLevelEvolution
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.DistanceLinearityDifficultyCompressionDiscretizedEvaluator
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.PMPLevelEvaluator
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.storage.LevelStorage
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser

fun main() {
//    doManyGrammarEvolution()
    doManyPMPEvolution()
}


fun doManyPMPEvolution() {

    val experimentsName = "pmp_v3/NEATs4l1solver"
    val generationsCount = 100
    val agentFactory = { Agents.NEAT.Stage4Level1Solver }
    val fitnessFunction: PMPLevelEvaluator<Float> = DistanceLinearityDifficultyCompressionDiscretizedEvaluator()

    val launchers = arrayOf(
        PMPEvolutionLauncher(
            storageLocation = experimentsName,
            label = "experiment_1",
            agentFactory = agentFactory,
            populationSize = 50,
            generationsCount = generationsCount,
            fitnessFunction = fitnessFunction,
            evaluateOnLevelsCount = 5,
            resultLevelsCount = 5,
            postProcess = false
        ),

        PMPEvolutionLauncher(
            storageLocation = experimentsName,
            label = "experiment_2",
            agentFactory = agentFactory,
            populationSize = 50,
            generationsCount = generationsCount,
            fitnessFunction = fitnessFunction,
            evaluateOnLevelsCount = 5,
            resultLevelsCount = 5,
            postProcess = false
        ),

        PMPEvolutionLauncher(
            storageLocation = experimentsName,
            label = "experiment_3",
            agentFactory = agentFactory,
            populationSize = 50,
            generationsCount = generationsCount,
            fitnessFunction = fitnessFunction,
            evaluateOnLevelsCount = 5,
            resultLevelsCount = 5,
            postProcess = false
        ),

        PMPEvolutionLauncher(
            storageLocation = experimentsName,
            label = "experiment_4",
            agentFactory = agentFactory,
            populationSize = 50,
            generationsCount = generationsCount,
            fitnessFunction = fitnessFunction,
            evaluateOnLevelsCount = 5,
            resultLevelsCount = 5,
            postProcess = false
        )
    )

    launchers.forEach { it.launch() }

}


fun doManyGrammarEvolution() {

    val launchers = arrayOf(
        GrammarEvolutionLauncher(
            label = "firstManyTest",
            agentFactory = { Agents.NeuroEvolution.BestGeneric },
            populationSize = 50,
            generationsCount = 50,
            levelsCount = 1,
            postProcess = false
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
    private val resultLevelsCount: Int,
    private val evaluateOnLevelsCount: Int,
    private val postProcess: Boolean
) {

    private val levelVisualiser = LevelVisualiser()

    fun launch()  {
        val levelEvolution = PMPLevelGeneratorEvolution(
            populationSize = this.populationSize,
            generationsCount = this.generationsCount,
            fitnessFunction = this.fitnessFunction,
            evaluateOnLevelsCount = this.evaluateOnLevelsCount,
            chartLabel = this.label
        )

        val levelGenerator = levelEvolution.evolve(this.agentFactory)
        var levels = Array(this.resultLevelsCount) { levelGenerator.generate() }

        if (this.postProcess)
            levels = levels.map { LevelPostProcessor.postProcess(it) }.toTypedArray()

        levels.forEachIndexed { index, level -> LevelStorage.storeLevel("data/levels/experiments/$storageLocation/${label}_$index.lvl", level) }
        levels.forEachIndexed { index, level -> levelVisualiser.store(level, "data/levels/experiments/$storageLocation/${label}_$index") }
        levelEvolution.storeChart("data/levels/experiments/$storageLocation/$label")
    }
}

class GrammarEvolutionLauncher(
    private val label: String,
    private val agentFactory: () -> IAgent,
    private val populationSize: Int,
    private val generationsCount: Long,
    private val levelsCount: Int,
    private val postProcess: Boolean
) {

    private val levelVisualiser = LevelVisualiser()

    fun launch()  {
        val levelEvolution = GrammarLevelEvolution(
            levelsCount = this.levelsCount,
            populationSize = this.populationSize,
            generationsCount = this.generationsCount
        )

        var levels = levelEvolution.evolve(this.agentFactory)

        if (this.postProcess)
            levels = levels.map { LevelPostProcessor.postProcess(it) }.toTypedArray()

        levels.forEachIndexed { index, level -> LevelStorage.storeLevel("data/levels/experiments/$label/$index.lvl", level) }
        levels.forEachIndexed { index, level -> levelVisualiser.store(level, "data/levels/experiments/$label/$index") }
    }

}