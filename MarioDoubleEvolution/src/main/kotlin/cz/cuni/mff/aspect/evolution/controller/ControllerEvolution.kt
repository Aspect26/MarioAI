package cz.cuni.mff.aspect.evolution.controller

import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.mario.controllers.MarioController


/**
 * Interface representing controller evolution.
 */
interface ControllerEvolution {

    /**
     * Evolves a mario agent controller, being provided with level generator.
     *
     * @return the evolved agent controller
     */
    fun evolve(levelGenerator: LevelGenerator, fitness: MarioGameplayEvaluator<Float>, objective: MarioGameplayEvaluator<Float>): MarioController

}
