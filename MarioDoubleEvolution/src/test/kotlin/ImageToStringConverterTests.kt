import cz.cuni.mff.aspect.evolution.levels.evaluators.compression.ImageHuffmanCompression
import org.apache.commons.lang3.StringUtils
import org.junit.Assert
import org.junit.Test
import java.awt.Color
import java.awt.image.BufferedImage

class ImageToStringConverterTests {

    @Test
    fun `one color image with alpha`() {
        val gridSize = 3
        val imageSize = gridSize * 3

        val image = BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB)
        image.setRGB(0, 0, imageSize, imageSize, IntArray(imageSize * imageSize) { Color.WHITE.rgb }, 0, 0)

        val imageToStringConverter = ImageHuffmanCompression.ImageToStringConverter(gridSize)
        val resultString = imageToStringConverter.imageToString(image)
        val expectedResult: String = StringUtils.repeat(1.toChar(), 9)

        Assert.assertEquals(
            "Expected string with ones only because all grid squares are the same",
            expectedResult,
            resultString
        )
    }

    @Test
    fun `one color image without alpha`() {
        val gridSize = 3
        val imageSize = gridSize * 3

        val image = BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB)
        image.setRGB(0, 0, imageSize, imageSize, IntArray(imageSize * imageSize) { Color.WHITE.rgb }, 0, 0)

        val imageToStringConverter = ImageHuffmanCompression.ImageToStringConverter(gridSize)
        val resultString = imageToStringConverter.imageToString(image)
        val expectedResult: String = StringUtils.repeat(1.toChar(), 9)

        Assert.assertEquals(
            "Expected string with ones only because all grid squares are the same",
            expectedResult,
            resultString
        )
    }

    @Test
    fun `one different grid square at the beginning`() {
        val gridSize = 3
        val imageSize = gridSize * 3

        val image = BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB)
        image.setRGB(0, 0, imageSize, imageSize, IntArray(imageSize * imageSize) { Color.WHITE.rgb }, 0, 0)
        image.setRGB(0, 0, Color.BLACK.rgb)

        val imageToStringConverter = ImageHuffmanCompression.ImageToStringConverter(gridSize)
        val resultString = imageToStringConverter.imageToString(image)
        val expectedResult: String = arrayOf(1, 2, 2, 2, 2, 2, 2, 2, 2).map { it.toChar() }.joinToString("")

        Assert.assertEquals(
            expectedResult,
            resultString
        )
    }

    @Test
    fun `one different grid square in the middle`() {
        val gridSize = 3
        val imageSize = gridSize * 3

        val image = BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB)
        image.setRGB(0, 0, imageSize, imageSize, IntArray(imageSize * imageSize) { Color.WHITE.rgb }, 0, 0)
        image.setRGB(8, 0, Color.BLACK.rgb)

        val imageToStringConverter = ImageHuffmanCompression.ImageToStringConverter(gridSize)
        val resultString = imageToStringConverter.imageToString(image)
        val expectedResult: String = arrayOf(1, 1, 2, 1, 1, 1, 1, 1, 1).map { it.toChar() }.joinToString("")

        Assert.assertEquals(
            expectedResult,
            resultString
        )
    }

    @Test
    fun `two different grid square in the middle`() {
        val gridSize = 3
        val imageSize = gridSize * 3

        val image = BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB)
        image.setRGB(0, 0, imageSize, imageSize, IntArray(imageSize * imageSize) { Color.WHITE.rgb }, 0, 0)
        image.setRGB(7, 0, Color.BLACK.rgb)
        image.setRGB(4, 3, Color.BLACK.rgb)

        val imageToStringConverter = ImageHuffmanCompression.ImageToStringConverter(gridSize)
        val resultString = imageToStringConverter.imageToString(image)
        val expectedResult: String = arrayOf(1, 1, 2, 1, 2, 1, 1, 1, 1).map { it.toChar() }.joinToString("")

        Assert.assertEquals(
            expectedResult,
            resultString
        )
    }

    @Test
    fun `multiple different grid square in the middle`() {
        val gridSize = 3
        val imageSize = gridSize * 3

        val image = BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB)
        image.setRGB(0, 0, imageSize, imageSize, IntArray(imageSize * imageSize) { Color.WHITE.rgb }, 0, 0)
        image.setRGB(7, 0, Color.BLACK.rgb)
        image.setRGB(4, 4, Color.BLACK.rgb)

        val imageToStringConverter = ImageHuffmanCompression.ImageToStringConverter(gridSize)
        val resultString = imageToStringConverter.imageToString(image)
        val expectedResult: String = arrayOf(1, 1, 2, 1, 3, 1, 1, 1, 1).map { it.toChar() }.joinToString("")

        Assert.assertEquals(
            expectedResult,
            resultString
        )
    }

    @Test
    fun `multiple different grid square in the middle 2`() {
        val gridSize = 3
        val imageSize = gridSize * 3

        val image = BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB)
        image.setRGB(0, 0, imageSize, imageSize, IntArray(imageSize * imageSize) { Color.WHITE.rgb }, 0, 0)

        image.setRGB(0, 0, Color.BLACK.rgb)
        image.setRGB(1, 0, Color.BLACK.rgb)

        image.setRGB(3, 0, Color.BLACK.rgb)

        val imageToStringConverter = ImageHuffmanCompression.ImageToStringConverter(gridSize)
        val resultString = imageToStringConverter.imageToString(image)
        val expectedResult: String = arrayOf(1, 2, 3, 3, 3, 3, 3, 3, 3).map { it.toChar() }.joinToString("")

        Assert.assertEquals(
            expectedResult,
            resultString
        )
    }

    @Test
    fun `two different colored grid squares`() {
        val gridSize = 3
        val imageSize = gridSize * 3

        val image = BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB)
        image.setRGB(0, 0, imageSize, imageSize, IntArray(imageSize * imageSize) { Color.WHITE.rgb }, 0, 0)

        image.setRGB(0, 3, Color.BLACK.rgb)

        image.setRGB(3, 3, Color.RED.rgb)

        val imageToStringConverter = ImageHuffmanCompression.ImageToStringConverter(gridSize)
        val resultString = imageToStringConverter.imageToString(image)
        val expectedResult: String = arrayOf(1, 1, 1, 2, 3, 1, 1, 1, 1).map { it.toChar() }.joinToString("")

        Assert.assertEquals(
            expectedResult,
            resultString
        )
    }
}