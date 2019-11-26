package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.utils.MarioGameplayEvaluators
import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.controller.NeatControllerEvolution
import cz.cuni.mff.aspect.evolution.levels.TrainingLevelsSet
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.mario.level.custom.PathWithHolesLevel
import cz.cuni.mff.aspect.mario.level.original.Stage1Level1
import kotlin.system.exitProcess


fun main() {
    evolveNeatAI()
    exitProcess(0)
}

fun evolveNeatAI() {
    val networkSettings = NetworkSettings(5, 5, 0, 2)
    val controllerEvolution: ControllerEvolution = NeatControllerEvolution(
        networkSettings,
        generationsCount = 100,
        populationSize = 100,
        chartName = "NEAT Evolution S1L1")
    val levels = emptyArray<MarioLevel>() + Stage1Level1 + PathWithHolesLevel
    //val levels = arrayOf<MarioLevel>(*TrainingLevelsSet)
    val resultController = controllerEvolution.evolve(levels, MarioGameplayEvaluators::distanceOnly, MarioGameplayEvaluators::victoriesOnly)

    val marioSimulator = GameSimulator()

    levels.forEach {
        marioSimulator.playMario(resultController, it, true)
    }
}
