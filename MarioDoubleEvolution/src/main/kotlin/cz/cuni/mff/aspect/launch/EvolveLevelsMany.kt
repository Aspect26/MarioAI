package cz.cuni.mff.aspect.launch

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.ge.grammar.GrammarLevelEvolution
import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.storage.LevelStorage
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser

fun main() {
    doManyGrammarEvolution()
}

fun doManyGrammarEvolution() {

    val launchers = arrayOf(
        GrammarEvolutionLauncher(
            label = "firstManyTest",
            agent = Agents.NeuroEvolution.BestGeneric,
            populationSize = 50,
            generationsCount = 50,
            levelsCount = 1,
            postProcess = false
        )
    )

    launchers.forEach { it.evolve() }
}

class GrammarEvolutionLauncher(
    private val label: String,
    private val agent: IAgent,
    private val populationSize: Int,
    private val generationsCount: Long,
    private val levelsCount: Int,
    private val postProcess: Boolean
) {

    private val levelVisualiser = LevelVisualiser()

    fun evolve()  {
        val levelEvolution = GrammarLevelEvolution(
            levelsCount = this.levelsCount,
            populationSize = this.populationSize,
            generationsCount = this.generationsCount
        )

        var levels = levelEvolution.evolve(agent)

        if (this.postProcess)
            levels = levels.map { LevelPostProcessor.postProcess(it) }.toTypedArray()

        levels.forEachIndexed { index, level -> LevelStorage.storeLevel("experiments/$label/$index.lvl", level) }
        levels.forEachIndexed { index, level -> levelVisualiser.displayAndStore(level, "data/levels/experiments/$label/$index") }
    }

}