package cz.cuni.mff.aspect.evolution.levels.evaluators.compression

import com.marvinjason.huffmancoding.HuffmanCoding
import org.apache.commons.lang3.StringUtils
import java.awt.image.BufferedImage
import java.lang.StringBuilder

/**
 * Compression of an image using adjusted Huffman compression.
 *
 * The image is split into 2x2 pixels wide grid, and each tile gets its own character. The same tiles will get the same
 * characters. The image is then converted to a String by going through the tiles from left to right, from top to bottom
 * and taking each tile's character. This string is then compressed using original Huffman algorithm. This way, the more
 * tiles in the image are different, the higher the resulting compression size will be.
 */
class ImageHuffmanCompression(private val gridSize: Int) : ImageCompression {

    override fun getSize(image: BufferedImage): Int {
        val imageToStringConverter = ImageToStringConverter(this.gridSize)
        val imageString: String = imageToStringConverter.imageToString(image)
        val huffman = HuffmanCoding(imageString)
        huffman.compress()

        return huffman.compressedSize
    }

    /** Implementation of a conversion of a image to the string as described in [ImageHuffmanCompression]. */
    class ImageToStringConverter(private val gridSize: Int) {

        fun imageToString(image: BufferedImage): String {
            val stringBuilder = StringBuilder()
            var latestGridSquareCode = 1
            val xGridLines = (image.width / this.gridSize)
            val yGridLines = (image.height / this.gridSize)

            val gridSquaresToChar: MutableMap<String, Char> = hashMapOf()

            for (yGridLine in 0 until yGridLines) {
                for (xGridLine in 0 until xGridLines) {
                    val gridSquareRepresentation = this.getGridSquareRepresentation(image, xGridLine, yGridLine)

                    if (!gridSquaresToChar.containsKey(gridSquareRepresentation)) {
                        gridSquaresToChar[gridSquareRepresentation] = latestGridSquareCode.toChar()
                        latestGridSquareCode++
                    }

                    stringBuilder.append(gridSquaresToChar[gridSquareRepresentation])
                }
            }

            return stringBuilder.toString()
        }

        private fun getGridSquareRepresentation(image: BufferedImage, xGridLine: Int, yGridLine: Int): String {
            val pixelColors = image.getRGB(xGridLine * this.gridSize, yGridLine * this.gridSize, this.gridSize, this.gridSize, null, 0, this.gridSize)
            val pixelColorStrings = pixelColors.map { StringUtils.leftPad(it.toString(), 10, '0') }

            return pixelColorStrings.joinToString("")
        }

    }

}