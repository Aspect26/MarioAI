package cz.cuni.mff.aspect.mario.level.splitting

/**
 * Represents split position of a level for [LevelSplitter].
 *
 * @param xPosition x position where the level should be split.
 * @param length length of the level part created by this split.
 */
data class LevelSplit(val xPosition: Int, val length: Int)
