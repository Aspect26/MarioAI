package cz.cuni.mff.aspect.visualisation.charts.linechart

import cz.cuni.mff.aspect.visualisation.charts.DataSeries

/** Represents data of a [LineChart]. */
internal data class LineChartData(
    val label: String,
    val xLabel: String,
    val yLabel: String,
    val stops: List<Double>,
    val series: List<DataSeries>
)
