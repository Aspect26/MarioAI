package cz.cuni.mff.aspect.evolution.levels

import cz.cuni.mff.aspect.mario.level.MarioLevel
import java.io.Serializable

/** Interface for level generators for Super Mario. */
interface LevelGenerator : Serializable {

    /** Generates one Super Mario level. */
    fun generate(): MarioLevel

}