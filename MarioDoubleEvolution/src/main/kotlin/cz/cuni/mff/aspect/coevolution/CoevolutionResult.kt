package cz.cuni.mff.aspect.coevolution

import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.mario.controllers.MarioController

/**
 * Data class holding result of a coevolution. It contains the best individuals from both latest evolutions.
 */
data class CoevolutionResult constructor(val controller: MarioController, val levelGenerator: LevelGenerator)
