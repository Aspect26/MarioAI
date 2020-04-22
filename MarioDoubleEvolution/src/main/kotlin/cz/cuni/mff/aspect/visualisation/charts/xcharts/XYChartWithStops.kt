package cz.cuni.mff.aspect.visualisation.charts.xcharts

import org.knowm.xchart.XYChart
import org.knowm.xchart.XYChartBuilder
import org.knowm.xchart.internal.chartpart.XYPlotWithStops

class XYChartWithStops(chartBuilder: XYChartBuilder?) : XYChart(chartBuilder) {

    init {
        this.plot = XYPlotWithStops(this)
    }

    fun setStops(stops: List<Double>) {
        (this.plot as XYPlotWithStops).setStops(stops)
    }

}
