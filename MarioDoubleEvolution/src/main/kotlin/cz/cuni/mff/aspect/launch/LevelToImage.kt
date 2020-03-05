package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.utils.LevelPostProcessor
import cz.cuni.mff.aspect.storage.LevelStorage
import cz.cuni.mff.aspect.visualisation.level.LevelToImageConverter
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JScrollPane


fun main() {
    val level = LevelStorage.loadLevel("grammar/03.lvl")
    val postProcessedLevel = LevelPostProcessor.postProcess(level, true)

    val image = LevelToImageConverter.create(postProcessedLevel, "level_image_test.png")
    displayImage(image)
}

fun displayImage(image: Image) {
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