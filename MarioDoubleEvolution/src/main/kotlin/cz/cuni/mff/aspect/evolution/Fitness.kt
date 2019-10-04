package cz.cuni.mff.aspect.evolution

import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.level.MarioLevel


fun fitnessOnlyDistance(controller: MarioController, levels: Array<MarioLevel>): Float {
    val statistics = playRandomLevel(controller, levels)
    return statistics.finalMarioDistance
}


fun fitnessDistanceLeastActions(controller: MarioController, levels: Array<MarioLevel>): Float {
    val statistics = playRandomLevel(controller, levels)
    return statistics.finalMarioDistance - statistics.jumps - statistics.specials + if(statistics.levelFinished) 100.0f else 0.0f
}


fun fitnessOnlyVictory(controller: MarioController, levels: Array<MarioLevel>): Float {
    val statistics = playRandomLevel(controller, levels)
    return if (statistics.levelFinished) 1.0f else 0.0f
}


fun fitnessDistanceJumpsSpecialsHurtsKills(controller: MarioController, levels: Array<MarioLevel>): Float {
    val statistics = playRandomLevel(controller, levels)
    return statistics.finalMarioDistance + statistics.kills * 50 - statistics.jumps * 5 - statistics.specials - statistics.marioHurts * 20
}


private fun playRandomLevel(controller: MarioController, levels: Array<MarioLevel>): GameStatistics {
    val marioSimulator = GameSimulator()
    marioSimulator.playMario(controller, levels.random(), false)

    return marioSimulator.statistics
}