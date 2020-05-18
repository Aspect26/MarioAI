package cz.cuni.mff.aspect.evolution.levels.evaluators.compression

import java.awt.image.BufferedImage

/** Interface for image compression of a [BufferedImage]. */
interface ImageCompression {

    /** Compute size of the given image using this compression algorithm. */
    fun getSize(image: BufferedImage): Int

}
