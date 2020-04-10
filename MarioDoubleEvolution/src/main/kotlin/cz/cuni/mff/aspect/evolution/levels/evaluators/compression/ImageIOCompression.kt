package cz.cuni.mff.aspect.evolution.levels.evaluators.compression

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

open class ImageIOCompression(private val formatType: FormatType, private val compressionQuality: Float) : ImageCompression {

    enum class FormatType(val imageIOValue: String) {
        JPG("jpg"), PNG("png")
    }

    override fun getSize(image: BufferedImage): Int {
        val baos = ByteArrayOutputStream()
        val writers = ImageIO.getImageWritersByFormatName(this.formatType.imageIOValue)

        val writer = writers.next()
        val param = writer.defaultWriteParam
        param.compressionMode = ImageWriteParam.MODE_EXPLICIT
        param.compressionQuality = this.compressionQuality
        val ios = ImageIO.createImageOutputStream(baos)
        writer.output = ios
        writer.write(null, IIOImage(image, null, null), param)
        val data: ByteArray = baos.toByteArray()
        writer.dispose()

        return data.size
    }

}


object SmallPNGCompression : ImageIOCompression(FormatType.PNG, 0.1f)
object MediumPngCompression : ImageIOCompression(FormatType.PNG, 0.5f)
object LargePngCompression : ImageIOCompression(FormatType.PNG, 0.8f)
object SmallJPGCompression : ImageIOCompression(FormatType.JPG, 0.1f)
object MediumJPGCompression : ImageIOCompression(FormatType.JPG, 0.5f)
object LargeJPGCompression : ImageIOCompression(FormatType.JPG, 0.8f)