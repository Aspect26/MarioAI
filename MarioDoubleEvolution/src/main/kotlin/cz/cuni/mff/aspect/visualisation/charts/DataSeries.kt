package cz.cuni.mff.aspect.visualisation.charts

import java.awt.Color

data class DataSeries(val label: String = "", val color: Color = Color.BLUE, var data: MutableList<Pair<Double, Double>>)
