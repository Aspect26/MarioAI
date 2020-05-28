package cz.cuni.mff.aspect.evolution.levels

/**
 * Data class representing result of a level generator evolution.
 *
 * @param bestLevelGenerator the best evolved level generator.
 * @param lastPopulation population of individuals from the last evolution generation.
 * @param T level generator type
 */
data class LevelGeneratorEvolutionResult<T: LevelGenerator>(val bestLevelGenerator: LevelGenerator, val lastPopulation: List<T>)
