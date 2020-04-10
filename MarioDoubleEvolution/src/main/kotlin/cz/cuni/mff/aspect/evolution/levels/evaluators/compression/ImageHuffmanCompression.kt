package cz.cuni.mff.aspect.evolution.levels.evaluators.compression

import com.marvinjason.huffmancoding.HuffmanCoding
import org.apache.commons.lang3.StringUtils
import java.awt.image.BufferedImage
import java.lang.StringBuilder

class ImageHuffmanCompression(private val gridSize: Int) : ImageCompression {

    override fun getSize(image: BufferedImage): Int {
        val imageToStringConverter = ImageToStringConverter(this.gridSize)
        val imageString: String = imageToStringConverter.imageToString(image)
        val huffman = HuffmanCoding(imageString)
        huffman.compress()

        return huffman.compressedSize
    }

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