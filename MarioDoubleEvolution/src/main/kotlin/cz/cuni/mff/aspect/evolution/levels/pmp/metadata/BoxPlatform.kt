package cz.cuni.mff.aspect.evolution.levels.pmp.metadata

data class BoxPlatform(val length: Int, val powerUpPosition: IntArray, val type: BoxPlatformType)

enum class BoxPlatformType { BRICKS, QUESTION_MARKS }
