package cz.cuni.mff.aspect.visualisation.level

import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.level.MarioLevel
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


object LevelToImageConverter {

    private const val SPRITES_SIZE = 16

    private val tileSheet: BufferedImage = ImageIO.read(File("resources/mapsheet.png"))
    private val enemySheet: BufferedImage = ImageIO.read(File("resources/enemysheet.png"))
    private val backgroundSheet: BufferedImage = ImageIO.read(File("resources/bgsheet.png"))

    fun create(level: MarioLevel): BufferedImage {
        val width = level.tiles.size * this.SPRITES_SIZE
        val height = level.tiles[0].size * this.SPRITES_SIZE

        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

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

        for (x in level.enemies.indices) {
            for (y in level.enemies[x].indices) {
                val entity = level.enemies[x][y]

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

    private fun getEntityColor(entity: Int, x: Int, y: Int): Int =
        when (entity) {
            Entities.Goomba.NORMAL -> this.enemySheet.getRGB(0 * 32 + x, 2 * 32 + y)
            Entities.Koopa.GREEN -> this.enemySheet.getRGB(0 * 32 + x, 1 * 32 + y)
            Entities.Koopa.GREEN_WINGED -> {
                val koopaPixel = this.enemySheet.getRGB(0 * 32 + x, 1 * 32 + y)
                if (this.isTransparent(koopaPixel))
                    this.enemySheet.getRGB(0 * 32 + x, 4 * 32 + y + 16)
                else
                    koopaPixel
            }
            Entities.Koopa.RED -> this.enemySheet.getRGB(0 * 32 + x, 0 * 32 + y)
            Entities.Spiky.NORMAL -> this.enemySheet.getRGB(0 * 32 + x, 3 * 32 + y)
            Entities.Flower.NORMAL -> this.enemySheet.getRGB(0 * 32 + x, 6 * 32 + y)
            Entities.BulletBill.NORMAL -> this.enemySheet.getRGB(0 * 32 + x, 5 * 32 + y)
            Entities.PrincessPeach.NORMAL -> Color(225, 122, 157).rgb

            else -> Color.BLACK.rgb
        }

    private fun isTransparent(color: Int): Boolean {
        return color == 16777215 || color == 47104 || color == 63488 || color == 30720
    }
}
