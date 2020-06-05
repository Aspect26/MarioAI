package cz.cuni.mff.aspect.visualisation.level

import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.MarioLevel
import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/** Creates images from given Super Mario levels. */
object LevelToImageConverter {

    private const val SPRITES_SIZE = 16

    private val tileSheet: BufferedImage = ImageIO.read(javaClass.classLoader.getResource("mapsheet.png"))
    private val enemySheet: BufferedImage = ImageIO.read(javaClass.classLoader.getResource("enemysheet.png"))
    private val backgroundSheet: BufferedImage = ImageIO.read(javaClass.classLoader.getResource("bgsheet.png"))

    /**
     * Creates image from Super Mario level using Super Mario Infinite's tile sheets.
     *
     * @param level the level's whose image is to be created.
     * @param noAlpha specifies, whether the resulting image should contain alpha channel.
     */
    fun create(level: MarioLevel, noAlpha: Boolean = false): BufferedImage {
        val width = level.tiles.size * this.SPRITES_SIZE
        val height = level.tiles[0].size * this.SPRITES_SIZE

        val imageType = if (noAlpha) BufferedImage.TYPE_INT_RGB else BufferedImage.TYPE_INT_ARGB
        val image = BufferedImage(width, height, imageType)

        for (x in 0 until level.tiles.size * this.SPRITES_SIZE) {
            for (y in 0 until level.tiles[0].size * this.SPRITES_SIZE) {
                val color = this.getBackgroundColor(x, y)
                image.setRGB(x, y, color)
            }
        }

        for (x in level.tiles.indices) {
            for (y in level.tiles[x].indices) {
                val tile = level.tiles[x][y]

                for (i in 0 until this.SPRITES_SIZE) {
                    for (j in 0 until this.SPRITES_SIZE) {
                        val color = this.getTileColor(tile, i, j)
                        if (!this.isTransparent(color)) {
                            image.setRGB(x * SPRITES_SIZE + i, y * SPRITES_SIZE + j, color)
                        }
                    }
                }
            }
        }

        for (x in level.entities.indices) {
            for (y in level.entities[x].indices) {
                val entity = level.entities[x][y]

                if (entity != Entities.NOTHING) {

                    for (i in 0 until this.SPRITES_SIZE) {
                        for (j in 0 until this.SPRITES_SIZE * 2) {
                            val color = this.getEntityColor(entity, i, j)
                            if (!this.isTransparent(color)) {
                                val imageY = y * SPRITES_SIZE + j - this.SPRITES_SIZE
                                val imageX = x * SPRITES_SIZE + i

                                if (imageY < 0 || imageX < 0 || imageX > image.width || imageY > image.height) continue

                                image.setRGB(imageX, imageY, color)
                            }
                        }
                    }
                }
            }
        }

        return image
    }

    /**
     * Creates minified image from given Super Mario level, where each tile is represented by one pixel.
     * Each tile uses its own color from custom color palette.
     *
     * @param level the level's whose image is to be created.
     * @param noAlpha specifies, whether the resulting image should contain alpha channel.
     */
    fun createMinified(level: MarioLevel, noAlpha: Boolean = false): BufferedImage {
        val width = level.tiles.size
        val height = level.tiles[0].size

        val imageType = if (noAlpha) BufferedImage.TYPE_INT_RGB else BufferedImage.TYPE_INT_ARGB
        val image = BufferedImage(width, height, imageType)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val entity = level.entities[x][y]
                val color = if (entity != Entities.NOTHING && entity != Entities.Flower.NORMAL && entity !=  Entities.PrincessPeach.NORMAL)
                    this.getEntityColor(level.entities[x][y]).rgb
                else this.getTileColor(level.tiles[x][y]).rgb

                image.setRGB(x, y, color)
            }
        }

        return image
    }

    private fun getBackgroundColor(x: Int, y: Int): Int {
        val xPos = 130 + x % 30
        val ySize = 95
        val yPos = if ((y / ySize) % 2 == 0) y % ySize else ySize - (y % ySize)

        return this.backgroundSheet.getRGB(xPos, yPos)
    }

    private fun getTileColor(tile: Byte, x: Int, y: Int): Int {
        val tilePosition: Int = if (tile >= 0) tile.toInt() else tile + 256
        val tileX = tilePosition % 16
        val tileY = (tilePosition - tileX) / 16

        return this.tileSheet.getRGB(tileX * 16 + x, tileY * 16 + y)
    }

    private fun getTileColor(tile: Byte): Color =
        when (tile) {
            Tiles.GRASS_TOP, Tiles.GRASS_CORNER_TOP_RIGHT, Tiles.GRASS_CORNER_TOP_LEFT, Tiles.GRASS_TOP_LEFT,
            Tiles.GRASS_TOP_RIGHT, Tiles.GRASS_LEFT, Tiles.GRASS_RIGHT, Tiles.GRASS_CORNER_BOTTOM_LEFT,
            Tiles.GRASS_CORNER_BOTTOM_RIGHT, Tiles.DIRT -> Color(255, 255, 0)
            Tiles.BULLET_BLASTER_BOTTOM, Tiles.BULLET_BLASTER_MIDDLE, Tiles.BULLET_BLASTER_TOP -> Color(150, 150, 150)
            Tiles.PIPE_TOP_LEFT, Tiles.PIPE_TOP_RIGHT, Tiles.PIPE_MIDDLE_RIGHT, Tiles.PIPE_MIDDLE_LEFT -> Color(0, 255, 0)
            Tiles.BRICK, Tiles.BRICK_WITH_COIN, Tiles.BRICK_WITH_POWERUP -> Color(118, 81, 49)
            Tiles.EXPIRED_QM, Tiles.QM_WITH_COIN, Tiles.QM_WITH_POWERUP -> Color(120, 20, 30)
            Tiles.STONE -> Color(255, 156, 26)
            Tiles.NOTHING, Tiles.ENV_GRASS_END, Tiles.ENV_GRASS_MIDDLE, Tiles.ENV_GRASS_START, Tiles.BUSH_START,
            Tiles.BUSH_MIDDLE, Tiles.BUSH_END, Tiles.ARROW_TOP_LEFT, Tiles.ARROW_TOP_RIGHT, Tiles.ARROW_BOTTOM_RIGHT,
            Tiles.ARROW_BOTTOM_LEFT -> Color(255, 255, 255)

            else -> Color(0, 0, 0)
        }

    private fun getEntityColor(entity: Int, x: Int, y: Int): Int {
        val xReversed = 31 - x
//        val xReversed = x

        return when (entity) {
            Entities.Goomba.NORMAL -> this.enemySheet.getRGB(0 * 32 + xReversed, 2 * 32 + y)
            Entities.Koopa.GREEN -> this.enemySheet.getRGB(0 * 32 + xReversed, 1 * 32 + y)
            Entities.Koopa.GREEN_WINGED -> {
                val koopaPixel = this.enemySheet.getRGB(0 * 32 + xReversed, 1 * 32 + y)
                if (this.isTransparent(koopaPixel))
                    this.enemySheet.getRGB(0 * 32 + xReversed, 4 * 32 + y + 16)
                else
                    koopaPixel
            }
            Entities.Koopa.RED -> this.enemySheet.getRGB(0 * 32 + xReversed, 0 * 32 + y)
            Entities.Spiky.NORMAL -> this.enemySheet.getRGB(0 * 32 + xReversed, 3 * 32 + y)
            Entities.Flower.NORMAL -> this.enemySheet.getRGB(0 * 32 + xReversed, 6 * 32 + y)
            Entities.BulletBill.NORMAL -> this.enemySheet.getRGB(0 * 32 + xReversed, 5 * 32 + y)
            Entities.PrincessPeach.NORMAL -> Color(225, 122, 157).rgb

            else -> Color.BLACK.rgb
        }
    }

    private fun getEntityColor(entity: Int): Color =
        when (entity) {
            Entities.Goomba.NORMAL -> Color(0, 0, 150)
            Entities.Koopa.GREEN -> Color(0, 100, 0)
            Entities.Koopa.GREEN_WINGED -> Color(0, 100, 100)
            Entities.Koopa.RED -> Color(255, 0, 0)
            Entities.Spiky.NORMAL -> Color(255,  0, 255)
            else -> Color(0, 0, 0)
        }

    private fun isTransparent(color: Int): Boolean {
        return color == 16777215 || color == 47104 || color == 63488 || color == 30720
    }
}
