package cz.cuni.mff.aspect.launch

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.ge.GrammarLevelEvolution
import cz.cuni.mff.aspect.evolution.levels.pmp.ProbabilisticMultipassEvolution
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.storage.LevelStorage
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser

fun main() {
//    doManyGrammarEvolution()
    doManyPMPEvolution()
}


fun doManyPMPEvolution() {

    val experimentsLabel = "pmp/goingRightAgent"
    val generationsCount = 150

    val launchers = arrayOf(
        PMPEvolutionLauncher(
            label = "${experimentsLabel}_1",
            agentFactory = { Agents.RuleBased.goingRight },
            populationSize = 50,
            generationsCount = generationsCount,
            evaluateOnLevelsCount = 5,
            resultLevelsCount = 5,
            postProcess = false
        ),

        PMPEvolutionLauncher(
            label = "${experimentsLabel}_2",
            agentFactory = { Agents.RuleBased.goingRight },
            populationSize = 50,
            generationsCount = generationsCount,
            evaluateOnLevelsCount = 5,
            resultLevelsCount = 5,
            postProcess = false
        ),

        PMPEvolutionLauncher(
            label = "${experimentsLabel}_3",
            agentFactory = { Agents.RuleBased.goingRight },
            populationSize = 50,
            generationsCount = generationsCount,
            evaluateOnLevelsCount = 5,
            resultLevelsCount = 5,
            postProcess = false
        ),

        PMPEvolutionLauncher(
            label = "${experimentsLabel}_4",
            agentFactory = { Agents.RuleBased.goingRight },
            populationSize = 50,
            generationsCount = generationsCount,
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
    private val label: String,
    private val agentFactory: () -> IAgent,
    private val populationSize: Int,
    private val generationsCount: Int,
    private val resultLevelsCount: Int,
    private val evaluateOnLevelsCount: Int,
    private val postProcess: Boolean
) {

    private val levelVisualiser = LevelVisualiser()

    fun launch()  {
        val levelEvolution = ProbabilisticMultipassEvolution(
            resultLevelsCount = this.resultLevelsCount,
            populationSize = this.populationSize,
            generationsCount = this.generationsCount,
            evaluateOnLevelsCount = this.evaluateOnLevelsCount
        )

        var levels = levelEvolution.evolve(this.agentFactory)

        if (this.postProcess)
            levels = levels.map { LevelPostProcessor.postProcess(it) }.toTypedArray()

        levels.forEachIndexed { index, level -> LevelStorage.storeLevel("experiments/${label}_$index.lvl", level) }
        levels.forEachIndexed { index, level -> levelVisualiser.displayAndStore(level, "data/levels/experiments/${label}_$index") }
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

        levels.forEachIndexed { index, level -> LevelStorage.storeLevel("experiments/$label/$index.lvl", level) }
        levels.forEachIndexed { index, level -> levelVisualiser.displayAndStore(level, "data/levels/experiments/$label/$index") }
    }

}