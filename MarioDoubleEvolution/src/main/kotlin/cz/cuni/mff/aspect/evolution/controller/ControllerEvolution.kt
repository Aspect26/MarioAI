package cz.cuni.mff.aspect.evolution.controller

import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.visualisation.charts.evolution.EvolutionLineChart


/** Interface representing controller evolution. */
interface ControllerEvolution {

    /**
     * Chart of the evolution.
     * @see EvolutionLineChart for more information about the chart.
     */
    var chart: EvolutionLineChart

    /** Evolves a mario agent controller, which will be evaluated using level generators given. */
    fun evolve(levelGenerators: List<LevelGenerator>): ControllerEvolutionResult

    /** Continues evolution of already evolved controller, using given initial population. */
    fun continueEvolution(levelGenerators: List<LevelGenerator>, initialPopulation: List<MarioController>): ControllerEvolutionResult

}
