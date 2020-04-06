package cz.cuni.mff.aspect.evolution.levels.pmp

import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.visualisation.level.LevelToImageConverter
import java.io.ByteArrayOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam


object LevelImageCompressor {

    fun smallPngSize(level: MarioLevel): Int = compressionSize(level, "png", 0.1f)

    fun mediumPngSize(level: MarioLevel): Int = compressionSize(level, "png", 0.5f)

    fun largePngSize(level: MarioLevel): Int = compressionSize(level, "png", 0.8f)

    fun jpgSize(level: MarioLevel): Int =compressionSize(level, "jpg", 0.1f)

    fun compressionSize(level: MarioLevel, imageType: String, compressionQuality: Float): Int {
        val image = LevelToImageConverter.create(level, noalpha=true)

        val baos = ByteArrayOutputStream()
        val writers = ImageIO.getImageWritersByFormatName(imageType)

        val writer = writers.next()
        val param = writer.defaultWriteParam
        param.compressionMode = ImageWriteParam.MODE_EXPLICIT
        param.compressionQuality = compressionQuality
        val ios = ImageIO.createImageOutputStream(baos)
        writer.output = ios
        writer.write(null, IIOImage(image, null, null), param)
        val data: ByteArray = baos.toByteArray()
        writer.dispose()

        return data.size
    }

}