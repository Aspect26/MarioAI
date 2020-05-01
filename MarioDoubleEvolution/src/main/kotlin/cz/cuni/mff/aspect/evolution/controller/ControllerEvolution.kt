package cz.cuni.mff.aspect.evolution.controller

import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.visualisation.charts.EvolutionLineChart


/**
 * Interface representing controller evolution.
 */
interface ControllerEvolution {

    /**
     * Get line chart of the evolution
     */
    val chart: EvolutionLineChart

    /**
     * Evolves a mario agent controller, being provided with level generator.
     */
    fun evolve(levelGenerators: List<LevelGenerator>): MarioController

    /**
     * Continue evolution of already evolved controller.
     */
    fun continueEvolution(controller: MarioController, levelGenerators: List<LevelGenerator>): MarioController

}
