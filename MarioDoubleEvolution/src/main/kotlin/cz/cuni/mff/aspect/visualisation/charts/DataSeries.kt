package cz.cuni.mff.aspect.visualisation.charts

import java.awt.Color

/** Represents [LineChart]'s  data series. */
data class DataSeries(val label: String = "", val color: Color = Color.BLUE, var data: MutableList<Pair<Double, Double>>)
