package cz.cuni.mff.aspect.visualisation.charts

import java.awt.Color

data class SeriesData(val label: String, val color: Color, val data: MutableList<Pair<Double, Double>>)