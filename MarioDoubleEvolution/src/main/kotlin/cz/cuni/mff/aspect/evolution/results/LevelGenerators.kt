package cz.cuni.mff.aspect.evolution.results

import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.storage.ObjectStorage

object LevelGenerators {

    object ChunkGenerator {

        val Neuro1: LevelGenerator = ObjectStorage.load("data/level-generators/pc_neuro_1.lg") as LevelGenerator
        val NEAT1: LevelGenerator = ObjectStorage.load("data/level-generators/pc_neat_1.lg") as LevelGenerator
        val NEAT2: LevelGenerator = ObjectStorage.load("data/level-generators/pc_neat_2.lg") as LevelGenerator
        val NEAT3: LevelGenerator = ObjectStorage.load("data/level-generators/pc_neat_3.lg") as LevelGenerator

    }
}