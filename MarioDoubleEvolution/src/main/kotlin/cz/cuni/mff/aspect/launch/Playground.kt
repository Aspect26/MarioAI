package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.levels.LevelPostProcessor
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.visualisation.charts.DataSeries
import cz.cuni.mff.aspect.visualisation.charts.linechart.AverageLineChart
import cz.cuni.mff.aspect.visualisation.charts.linechart.LineChartData
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser
import java.awt.Color
import kotlin.random.Random

/** Launcher used for development purposes. */
fun main() {
    val randomLevel = PMPLevelGenerator().generate(12)
    LevelVisualiser().display(LevelPostProcessor.postProcess(randomLevel, true))
}
