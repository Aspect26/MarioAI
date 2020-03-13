package cz.cuni.mff.aspect.launch

import ch.idsia.agents.controllers.keyboard.CheaterKeyboardAgent
import ch.idsia.agents.controllers.keyboard.KeyboardAgent
import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelCreator
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser

fun main() {

    var level = PMPLevelCreator.createDefault()
    val agent = CheaterKeyboardAgent()

    level = LevelPostProcessor.postProcess(level)

    LevelVisualiser().display(level)
    GameSimulator().playMario(agent, level, true)

}