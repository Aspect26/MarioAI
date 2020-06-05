package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser


/** Launcher used for development purposes. */
fun main() {
    val level = PCLevelGenerator().generate()
    LevelVisualiser().display(level)
}
