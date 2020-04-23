package cz.cuni.mff.aspect.evolution.levels

import cz.cuni.mff.aspect.mario.level.MarioLevel
import java.io.Serializable


interface LevelGenerator : Serializable {

    fun generate(): MarioLevel

}