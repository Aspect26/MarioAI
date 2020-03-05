package cz.cuni.mff.aspect.visualisation.level

import cz.cuni.mff.aspect.mario.Entities
import cz.cuni.mff.aspect.mario.Tiles
import cz.cuni.mff.aspect.mario.level.MarioLevel
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import java.io.File
import javax.imageio.ImageIO


object LevelToImageConverter {

    private const val SPRITES_SIZE = 16
    private const val ENTITIES_PADDING = 2

    fun create(level: MarioLevel, path: String): Image {
        val width = level.tiles.size * this.SPRITES_SIZE
        val height = level.tiles[0].size * this.SPRITES_SIZE

        val image = WritableImage(width, height)
        val pixelWriter = image.pixelWriter

        for (x in level.tiles.indices) {
            for (y in level.tiles[x].indices) {
                val tile = level.tiles[x][y]
                val color = this.getTileColor(tile)

                for (i in 0 until this.SPRITES_SIZE) {
                    for (j in 0 until this.SPRITES_SIZE) {
                        pixelWriter.setColor(x * SPRITES_SIZE + i, y * SPRITES_SIZE + j, color)
                    }
                }
            }
        }

        for (x in level.enemies.indices) {
            for (y in level.enemies[x].indices) {
                val entity = level.enemies[x][y]

                if (entity != Entities.NOTHING) {
                    val color = this.getEntityColor(entity)

                    for (i in ENTITIES_PADDING .. this.SPRITES_SIZE - 2 * ENTITIES_PADDING) {
                        for (j in ENTITIES_PADDING .. this.SPRITES_SIZE - 2 * ENTITIES_PADDING) {
                            val selectedColor = if (i == j || i == this.SPRITES_SIZE - ENTITIES_PADDING - j) Color.WHITE else color
                            pixelWriter.setColor(x * SPRITES_SIZE + i, y * SPRITES_SIZE + j, selectedColor)
                        }
                    }
                }
            }
        }

        return image
    }

    private fun getTileColor(tile: Byte): Color =
        when (tile) {
            Tiles.NOTHING -> Color.WHITE
            Tiles.DIRT -> Color.SANDYBROWN
            Tiles.STONE -> Color.SLATEGREY
            Tiles.BRICK -> Color.DARKRED
            Tiles.QM_WITH_COIN -> Color.ROSYBROWN
            Tiles.QM_WITH_POWERUP -> Color.YELLOW
            Tiles.GRASS_TOP, Tiles.GRASS_LEFT, Tiles.GRASS_RIGHT, Tiles.GRASS_TOP_RIGHT, Tiles.GRASS_TOP_LEFT,
                Tiles.GRASS_CORNER_TOP_LEFT, Tiles.GRASS_CORNER_TOP_RIGHT -> Color.GREEN
            Tiles.PIPE_MIDDLE_LEFT, Tiles.PIPE_MIDDLE_RIGHT, Tiles.PIPE_TOP_LEFT, Tiles.PIPE_TOP_RIGHT -> Color.LIGHTGREEN
            Tiles.BULLET_BLASTER_BOTTOM, Tiles.BULLET_BLASTER_MIDDLE, Tiles.BULLET_BLASTER_TOP -> Color.DARKORANGE

            Tiles.BUSH_START, Tiles.BUSH_MIDDLE, Tiles.BUSH_END,
            Tiles.ENV_GRASS_START, Tiles.ENV_GRASS_MIDDLE, Tiles.ENV_GRASS_END,
            Tiles.ARROW_BOTTOM_LEFT, Tiles.ARROW_BOTTOM_RIGHT, Tiles.ARROW_TOP_RIGHT, Tiles.ARROW_TOP_LEFT -> Color.LIGHTGOLDENRODYELLOW

            else -> Color.BLACK
        }

    private fun getEntityColor(entity: Int): Color =
        when (entity) {
            Entities.Goomba.NORMAL -> Color.SADDLEBROWN
            Entities.Koopa.GREEN -> Color.FORESTGREEN
            Entities.Flower.NORMAL -> Color.RED
            Entities.BulletBill.NORMAL -> Color.ORANGE
            Entities.PrincessPeach.NORMAL -> Color.HOTPINK

            else -> Color.BLACK
        }

}
