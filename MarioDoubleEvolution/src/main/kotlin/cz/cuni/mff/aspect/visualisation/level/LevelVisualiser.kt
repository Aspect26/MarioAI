package cz.cuni.mff.aspect.visualisation.level

import cz.cuni.mff.aspect.mario.level.MarioLevel
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JScrollPane

/** Displays and stores images of Super Mario levels. */
class LevelVisualiser {

    /**
     * Displays and stores Super Mario level on given location.
     *
     * @param level level, whose image is to be created.
     * @param fileName location, where the image should be stored.
     * @param minified specifies, whether the image should be minified.
     * @see LevelToImageConverter.createMinified for image minification details.
     */
    fun displayAndStore(level: MarioLevel, fileName: String, minified: Boolean = false) {
        val image = this.createImage(level, minified)
        this.storeImage(image, fileName)
        this.displayImage(image)
    }

    /**
     * Displays Super Mario level.
     *
     * @param level level, whose image is to be created.
     * @param minified specifies, whether the image should be minified.
     * @see LevelToImageConverter.createMinified for image minification details.
     */
    fun display(level: MarioLevel, minified: Boolean = false) {
        val image = this.createImage(level, minified)
        this.displayImage(image)
    }

    /**
     * Stores Super Mario level on given location.
     *
     * @param level level, whose image is to be created.
     * @param fileName location, where the image should be stored.
     * @param minified specifies, whether the image should be minified.
     * @see LevelToImageConverter.createMinified for image minification details.
     */
    fun store(level: MarioLevel, fileName: String, minified: Boolean = false) {
        val image = this.createImage(level, minified)
        this.storeImage(image, fileName)
    }

    private fun createImage(level: MarioLevel, minified: Boolean) : BufferedImage =
        if (minified) LevelToImageConverter.createMinified(level) else LevelToImageConverter.create(level)

    private fun displayImage(image: BufferedImage) {
        val label = JLabel()
        label.icon = ImageIcon(image)

        val frame = JFrame()
        frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        frame.contentPane.add(JScrollPane(label))
        frame.setSize(1800, 400)
        frame.setLocation(200, 200)
        frame.isVisible = true
    }

    private fun storeImage(image: BufferedImage, fileName: String) {
        val file = File(fileName)
        if (file.parentFile != null) file.parentFile.mkdirs()
        ImageIO.write(image, "png", file)
    }

}