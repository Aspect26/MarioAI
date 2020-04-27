package cz.cuni.mff.aspect.evolution.results

import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.storage.ObjectStorage

object LevelGenerators {

    object PCGenerator {

        val difficulty = ObjectStorage.load("data/level-generators/pc_difficulty.lg") as LevelGenerator
        val linearity = ObjectStorage.load("data/level-generators/pc_linearity.lg") as LevelGenerator
        val compression = ObjectStorage.load("data/level-generators/pc_compression_png_01.lg") as LevelGenerator
        val all = ObjectStorage.load("data/level-generators/pc_all.lg") as LevelGenerator
        val min = ObjectStorage.load("data/level-generators/pc_min.lg") as LevelGenerator
        val allDiscretized = ObjectStorage.load("data/level-generators/pc_all_discretized.lg") as LevelGenerator

        val halfSolvingNE = ObjectStorage.load("data/level-generators/pc_50_ne.lg") as LevelGenerator
        val halfSolvingNEAT = ObjectStorage.load("data/level-generators/pc_50_neat.lg") as LevelGenerator

    }

    object PMPGenerator {

        val difficulty = ObjectStorage.load("data/level-generators/pmp_difficulty.lg") as LevelGenerator
        val linearity = ObjectStorage.load("data/level-generators/pmp_linearity.lg") as LevelGenerator
        val compression = ObjectStorage.load("data/level-generators/pmp_compression_png_01.lg") as LevelGenerator
        val all = ObjectStorage.load("data/level-generators/pmp_all.lg") as LevelGenerator
        val min = ObjectStorage.load("data/level-generators/pmp_min.lg") as LevelGenerator
        val allDiscretized = ObjectStorage.load("data/level-generators/pmp_all_discretized.lg") as LevelGenerator
        val lol = ObjectStorage.load("data/level-generators/pmp_lol.lg") as LevelGenerator

        val halfSolving = ObjectStorage.load("data/level-generators/pmp_50.lg") as LevelGenerator

    }

    class StaticGenerator(private val levels: Array<MarioLevel>) : LevelGenerator {

        private var currentLevel = 0

        override fun generate(): MarioLevel = this.levels[this.currentLevel++ % this.levels.size]

    }
}
