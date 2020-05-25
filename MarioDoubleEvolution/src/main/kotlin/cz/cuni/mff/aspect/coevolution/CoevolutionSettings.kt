package cz.cuni.mff.aspect.coevolution

import cz.cuni.mff.aspect.evolution.controller.ControllerEvolution
import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorEvolution
import cz.cuni.mff.aspect.mario.controllers.MarioController

/**
 * Data class representing coevolution settings.
 *
 * @param controllerEvolution the evolution algorithm for controller.
 * @param generatorEvolution the evolution algorithm for level generator.
 * @param initialController initial controller, which is being evolved by the algorithm.
 * @param initialLevelGenerator initial level generator used in the first generation of the coevolution
 * @param generations number of coevolution generations.
 * @param repeatGeneratorsCount number of level generators, on which the controller evolution should evaluate the controllers.
 * @param storagePath path, where the results of the coevolution are to be stored.
 */
data class CoevolutionSettings(
    val controllerEvolution: ControllerEvolution,
    val generatorEvolution: LevelGeneratorEvolution,
    val initialController: MarioController,
    val initialLevelGenerator: LevelGenerator,
    val generations: Int,
    val repeatGeneratorsCount: Int,
    val storagePath: String
)
