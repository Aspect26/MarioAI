package cz.cuni.mff.aspect.evolution.levels.pmp

data class MarioLevelMetadata (
    val groundHeight: IntArray,
    val entities: IntArray,
    val pipes: BooleanArray,
    val startBoxes: BooleanArray
)
