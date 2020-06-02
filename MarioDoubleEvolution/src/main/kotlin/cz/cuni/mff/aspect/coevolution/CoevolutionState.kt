package cz.cuni.mff.aspect.coevolution

import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.visualisation.charts.evolution.EvolutionLineChart

/**
 * Represents coevolution state which can be used to store and load the coevolution again.
 *
 * @param LevelGeneratorType type of level generator being evolved.
 */
data class CoevolutionState<LevelGeneratorType: LevelGenerator>(
    val lastFinishedGeneration: Int,
    val latestController: MarioController,
    val latestGenerator: LevelGenerator,
    val latestGeneratorsPopulation: List<LevelGeneratorType>,
    val controllerEvolutionChart: EvolutionLineChart,
    val generatorEvolutionChart: EvolutionLineChart,
    val coevolutionTimer: CoevolutionTimer
)
