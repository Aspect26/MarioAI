package cz.cuni.mff.aspect.visualisation.level

import cz.cuni.mff.aspect.mario.level.MarioLevel
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JScrollPane

class LevelVisualiser {

    fun displayAndStore(level: MarioLevel, fileName: String) {
        val image = LevelToImageConverter.create(level, "level_image_test.png")

        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", File(fileName))
        this.displayImage(image)
    }

    private fun displayImage(image: Image) {
        val fxImage = SwingFXUtils.fromFXImage(image, null)

        val label = JLabel()
        label.icon = ImageIcon(fxImage)

        val frame = JFrame()
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.contentPane.add(JScrollPane(label))
        frame.setSize(1800, 400)
        frame.setLocation(200, 200)
        frame.isVisible = true
    }

}