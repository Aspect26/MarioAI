package cz.cuni.mff.aspect.evolution.levels.pmp.metadata

/** Represents a box platform in [PMPLevelMetadata]. */
data class BoxPlatform(val length: Int, val boxesLevel: Int, val powerUpPosition: List<Int>, val type: BoxPlatformType)

/** Represents a type of a [BoxPlatform]. */
enum class BoxPlatformType {

    /** A box platform type consisting of brick blocks only. */
    BRICKS,

    /** A box platform type consisting of question mark blocks only. */
    QUESTION_MARKS
}
