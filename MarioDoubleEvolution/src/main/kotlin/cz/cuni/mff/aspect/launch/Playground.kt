package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.levels.evaluators.compression.ImageHuffmanCompression
import cz.cuni.mff.aspect.evolution.results.LevelGenerators
import cz.cuni.mff.aspect.visualisation.level.LevelToImageConverter

fun main() {
    val levelGenerator = LevelGenerators.PMPGenerator.all
    val level = levelGenerator.generate()

    val image = LevelToImageConverter.create(level)
    val imageMinified = LevelToImageConverter.createMinified(level)

    val compression = ImageHuffmanCompression(2)

    println(compression.getSize(image))
    println(compression.getSize(imageMinified))

}