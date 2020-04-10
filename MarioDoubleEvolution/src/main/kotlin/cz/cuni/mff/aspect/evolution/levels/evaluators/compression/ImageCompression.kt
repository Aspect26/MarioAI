package cz.cuni.mff.aspect.evolution.levels.evaluators.compression

import java.awt.image.BufferedImage


interface ImageCompression {

    fun getSize(image: BufferedImage): Int

}
