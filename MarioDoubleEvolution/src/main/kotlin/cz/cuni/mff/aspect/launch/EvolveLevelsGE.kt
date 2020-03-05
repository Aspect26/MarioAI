package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.results.Agents
import cz.cuni.mff.aspect.evolution.levels.direct.DirectEncodedLevelEvolution
import cz.cuni.mff.aspect.evolution.levels.ge.grammar.GrammarLevelEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.storage.LevelStorage


fun main() {
     grammarEvolution()
//    directEncodedEvolution()
}

fun grammarEvolution() {
    val agent = Agents.NeuroEvolution.Stage4Level1Solver
    val marioAgent = agent as MarioAgent

    val levelEvolution = GrammarLevelEvolution()
    val levels = levelEvolution.evolve(marioAgent.controller)
    val firstLevel = levels.first()

    LevelPostProcessor.postProcess(firstLevel)
    LevelStorage.storeLevel("current.lvl", firstLevel)

    GameSimulator().playMario(agent, firstLevel, true)
}

fun directEncodedEvolution() {
    val agent = Agents.NeuroEvolution.Stage4Level1Solver
    val marioAgent = agent as MarioAgent
    val levelEvolution = DirectEncodedLevelEvolution()
    val levels = levelEvolution.evolve(marioAgent.controller)

    val firstLevel = levels.first()
    LevelStorage.storeLevel("current.lvl", firstLevel)

    GameSimulator().playMario(agent, firstLevel, true)
}