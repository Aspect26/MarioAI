package cz.cuni.mff.aspect.evolution.results

import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.storage.ObjectStorage

object LevelGenerators {

    object ChunkGenerator {

        val NeuroS4L1: LevelGenerator = ObjectStorage.load("data/level-generators/pc_neuro_s4l1.lg") as LevelGenerator

    }
}