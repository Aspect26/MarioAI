package org.knowm.xchart.internal.chartpart

import org.knowm.xchart.XYSeries
import org.knowm.xchart.style.XYStyler

/** Reimplementation of `xcharts` [Plot_XY] to add support for vertical stops in the plot. */
class XYPlotWithStops(chart: Chart<XYStyler, XYSeries>) : Plot_XY<XYStyler, XYSeries>(chart) {

    init {
        this.plotContent = XYPlotContentWithStops(chart)
    }

    fun setStops(stops: List<Double>) {
        (this.plotContent as XYPlotContentWithStops).stops = stops
    }

}