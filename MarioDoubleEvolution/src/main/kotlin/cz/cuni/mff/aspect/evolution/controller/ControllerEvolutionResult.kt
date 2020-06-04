package cz.cuni.mff.aspect.evolution.controller

import cz.cuni.mff.aspect.mario.controllers.MarioController

/**
 * Data class representing result of a controller evolution.
 *
 * @param bestController the best evolved controller.
 * @param lastPopulation population of individuals from the last evolution generation.
 */
data class ControllerEvolutionResult(val bestController: MarioController, val lastPopulation: List<MarioController>)
