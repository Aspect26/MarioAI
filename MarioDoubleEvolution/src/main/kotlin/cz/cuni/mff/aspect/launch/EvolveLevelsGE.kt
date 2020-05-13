package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.evolution.levels.ge.GrammarLevelEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.storage.LevelStorage
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser

/** Launches grammar evolution of levels. */
@Deprecated("Grammar evolution of levels is no longer maintained",
    replaceWith = ReplaceWith("Some other launcher"))
fun main() {
     grammarEvolution()
}

fun grammarEvolution() {
    val agent = Agents.NeuroEvolution.Stage4Level1Solver

    val levelEvolution = GrammarLevelEvolution()
    val levels = levelEvolution.evolve { Agents.NeuroEvolution.Stage4Level1Solver }
    val firstLevel = levels.first()

    val postprocessed = LevelPostProcessor.postProcess(firstLevel)
    LevelStorage.storeLevel("current.lvl", postprocessed)

    LevelVisualiser().display(postprocessed)
    GameSimulator().playMario(agent, postprocessed, true)
}
