package org.knowm.xchart.internal.chartpart

import org.knowm.xchart.XYSeries
import org.knowm.xchart.internal.Utils
import org.knowm.xchart.style.AxesChartStyler
import org.knowm.xchart.style.XYStyler
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D

/** Reimplementation of `xcharts` [PlotContent_XY] to add support for vertical stops in the plot content. */
class XYPlotContentWithStops(chart: Chart<XYStyler, XYSeries>) : PlotContent_XY<XYStyler, XYSeries>(chart) {

    var stops = listOf<Double>()

    private val xyStyler: AxesChartStyler

    init {
        xyStyler = chart.getStyler()
    }

    override fun doPaint(g: Graphics2D) {
        super.doPaint(g)

        val xTickSpace = xyStyler.plotContentSize * bounds.width
        val xLeftMargin = Utils.getTickStartOffset(bounds.width, xTickSpace)
        val xMin = chart.xAxis.min
        val xMax = chart.xAxis.max

        val yTickSpace = xyStyler.plotContentSize * bounds.height
        val yTopMargin = Utils.getTickStartOffset(bounds.height, yTickSpace)

        g.color = Color.BLACK
        for (x in stops) {
            val xPosition = bounds.x + xLeftMargin + (x - xMin) / (xMax - xMin) * xTickSpace
            val nextXPosition = bounds.x + xLeftMargin + ((x + 1) - xMin) / (xMax - xMin) * xTickSpace

            val rectangle = Rectangle2D.Double()
            rectangle.x = xPosition
            rectangle.y = 0.0
            rectangle.width = nextXPosition - xPosition
            rectangle.height = bounds.height + yTopMargin

            g.fill(rectangle)
        }
    }
}