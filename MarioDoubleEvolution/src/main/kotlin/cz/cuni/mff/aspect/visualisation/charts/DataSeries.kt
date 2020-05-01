package cz.cuni.mff.aspect.visualisation.charts

import java.awt.Color

data class DataSeries(val label: String, val color: Color, val data: MutableList<Pair<Double, Double>>)