package cz.cuni.mff.aspect.visualisation.level

import cz.cuni.mff.aspect.mario.level.MarioLevel
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JScrollPane

class LevelVisualiser {

    fun displayAndStore(level: MarioLevel, fileName: String) {
        val image = LevelToImageConverter.create(level)
        this.storeImage(image, fileName)
        this.displayImage(image)
    }

    fun display(level: MarioLevel) {
        val image = LevelToImageConverter.create(level)
        this.displayImage(image)
    }

    fun store(level: MarioLevel, fileName: String) {
        val image = LevelToImageConverter.create(level)
        this.storeImage(image, fileName)
    }

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
        file.parentFile.mkdirs()
        ImageIO.write(image, "png", file)
    }

}