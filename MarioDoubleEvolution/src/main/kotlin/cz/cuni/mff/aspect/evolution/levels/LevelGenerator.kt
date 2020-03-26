package cz.cuni.mff.aspect.evolution.levels

import cz.cuni.mff.aspect.mario.level.MarioLevel


interface LevelGenerator {

    fun generate(): MarioLevel

}