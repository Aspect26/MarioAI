package cz.cuni.mff.aspect.evolution.levels.evaluators.compression

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

/**
 * Implementation of an image compression using [ImageIO].
 * @param formatType specifies type of the compression.
 * @param compressionQuality specifies quality of the compression. See [ImageWriteParam.setCompressionQuality] for more info.
 * @see FormatType
 */
open class ImageIOCompression(private val formatType: FormatType, private val compressionQuality: Float) : ImageCompression {

    /** Specifies image formats that this compression algorithm supports. */
    enum class FormatType(val imageIOValue: String) {
        JPG("jpg"), PNG("png")
    }

    /** Computes the size of the given image when compressed by algorithm specified in the primary constructor. */
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

/** Implementation of PNG compression with small compression quality (the resulting size will not be much smaller). */
object SmallPNGCompression : ImageIOCompression(FormatType.PNG, 0.1f)

/** Implementation of PNG compression with medium compression quality. */
object MediumPngCompression : ImageIOCompression(FormatType.PNG, 0.5f)

/** Implementation of PNG compression with high compression quality (the resulting size will be much smaller). */
object HighPngCompression : ImageIOCompression(FormatType.PNG, 0.8f)

/** Implementation of JPG compression with small compression quality (the information loss will be smaller). */
object SmallJPGCompression : ImageIOCompression(FormatType.JPG, 0.1f)

/** Implementation of JPG compression with medium compression quality (the information loss will be medium). */
object MediumJPGCompression : ImageIOCompression(FormatType.JPG, 0.5f)

/** Implementation of JPG compression with high compression quality (the information loss will be high). */
object HighJPGCompression : ImageIOCompression(FormatType.JPG, 0.8f)