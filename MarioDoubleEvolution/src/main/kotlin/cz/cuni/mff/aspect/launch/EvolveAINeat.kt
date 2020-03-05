package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.controller.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.NeatControllerEvolution
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.mario.level.custom.PathWithHolesLevel
import kotlin.system.exitProcess


fun main() {
    evolveNeatAI()
    exitProcess(0)
}

fun evolveNeatAI() {
    val networkSettings = NetworkSettings(5, 5, 0, 2)
    val controllerEvolution: ControllerEvolution = NeatControllerEvolution(
        networkSettings,
        denseInput = false,
        generationsCount = 500,
        populationSize = 100,
        chartName = "NEAT Evolution S4L1")
    val levels = emptyArray<MarioLevel>() /* + Stage4Level1Split.levels */ + PathWithHolesLevel
    //val levels = arrayOf<MarioLevel>(*TrainingLevelsSet)
    val resultController = controllerEvolution.evolve(levels, MarioGameplayEvaluators::distanceOnly, MarioGameplayEvaluators::victoriesOnly)

    val marioSimulator = GameSimulator()

    levels.forEach {
        marioSimulator.playMario(resultController, it, true)
    }
}
