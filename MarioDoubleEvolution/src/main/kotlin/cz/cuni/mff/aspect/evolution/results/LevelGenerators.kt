package cz.cuni.mff.aspect.evolution.results

import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.storage.ObjectStorage

object LevelGenerators {

    object ChunkGenerator {

    }

    object PMPGenerator {

        val NEAT1: LevelGenerator = ObjectStorage.load("data/level-generators/pmp_neat_1.lg") as LevelGenerator
        val NonLinear: LevelGenerator = ObjectStorage.load("data/level-generators/pmp_non_linear.lg") as LevelGenerator
        val HC: LevelGenerator = ObjectStorage.load("data/level-generators/pmp_hc.lg") as LevelGenerator
        val NEAT2: LevelGenerator = ObjectStorage.load("data/level-generators/pmp_neat_2.lg") as LevelGenerator

    }
}