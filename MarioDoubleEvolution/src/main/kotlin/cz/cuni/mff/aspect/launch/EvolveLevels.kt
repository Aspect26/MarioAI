package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.Agents
import cz.cuni.mff.aspect.evolution.levels.direct.encoded.DirectEncodedLevelEvolution
import cz.cuni.mff.aspect.evolution.levels.grammar.GrammarLevelEvolution
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.storage.LevelStorage


fun main() {
     grammarEvolution()
//    directEncodedEvolution()
}

fun grammarEvolution() {
    val agent = Agents.NeuroEvolution.stage4Level1Solver
    val marioAgent = agent as MarioAgent

    val levelEvolution = GrammarLevelEvolution()
    val levels = levelEvolution.evolve(marioAgent.controller)
    val firstLevel = levels.first()

    LevelStorage.storeLevel("current.lvl", firstLevel)

    GameSimulator().playMario(agent, firstLevel, true)
}

fun directEncodedEvolution() {
    val agent = Agents.NeuroEvolution.stage4Level1Solver
    val marioAgent = agent as MarioAgent
    val levelEvolution = DirectEncodedLevelEvolution()
    val levels = levelEvolution.evolve(marioAgent.controller)

    val firstLevel = levels.first()
    LevelStorage.storeLevel("current.lvl", firstLevel)

    GameSimulator().playMario(agent, firstLevel, true)
}