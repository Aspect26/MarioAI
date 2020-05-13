package cz.cuni.mff.aspect.visualisation.charts.linechart

import cz.cuni.mff.aspect.visualisation.charts.DataSeries
import cz.cuni.mff.aspect.visualisation.charts.xcharts.XYChartWithStops
import org.knowm.xchart.*
import org.knowm.xchart.internal.series.Series
import org.knowm.xchart.style.Styler
import org.knowm.xchart.style.markers.SeriesMarkers
import java.awt.BorderLayout
import java.awt.Color
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import javax.swing.JFrame

/**
 * A wrapper of `xcharts` library's line chart which specifies the overall chart style and is able to display the chart
 * using Java's `swing` GUI library, store the chart as an SVG and update the chart's data in realtime.
 */
class LineChart(internal val label: String = "Line chart", internal val xLabel: String = "X", internal val yLabel: String = "Y") {

    private val chart: XYChartWithStops = XYChartWithStops(XYChartBuilder()
        .width(1920)
        .height(1080)
        .title(label)
        .xAxisTitle(xLabel)
        .yAxisTitle(yLabel)
    )

    private val chartUIPanel: XChartPanel<XYChart>
    private val xchartSeries: MutableList<Series> = mutableListOf()

    private var windowShown = false
    internal var stops: List<Double> = mutableListOf()
    internal var series: List<DataSeries> = mutableListOf()

    init {
        chart.styler.apply {
            defaultSeriesRenderStyle = XYSeries.XYSeriesRenderStyle.Line
            isChartTitleVisible = true
            legendPosition = Styler.LegendPosition.InsideNW
            isLegendVisible = true
            markerSize = 16
        }

        chartUIPanel = XChartPanel(chart)
    }

    fun renderChart() {
        this.windowShown = true
        javax.swing.SwingUtilities.invokeLater {
            val frame = JFrame("Chart")
            frame.layout = BorderLayout()

            frame.add(this.chartUIPanel, BorderLayout.CENTER)

            frame.pack()
            frame.isVisible = true
            frame.extendedState = frame.extendedState or JFrame.MAXIMIZED_BOTH
            frame.addWindowListener(object : WindowAdapter() {
                override fun windowClosed(e: WindowEvent?) {
                    windowShown = false
                }
            })
        }
    }

    fun updateChart(series: List<DataSeries>, stops: List<Double> = emptyList()) {
        for ((seriesLabel, seriesColor, seriesData) in series) {
            val currentSeries = this.getOrCreateSeries(seriesLabel, seriesColor)

            val xData: DoubleArray = seriesData.map { it.first }.toDoubleArray()
            val yData: DoubleArray = seriesData.map { it.second }.toDoubleArray()

            this.chart.updateXYSeries(currentSeries.name, xData, yData, null)
        }

        this.chart.setStops(stops)
        this.stops = stops
        this.series = series

        this.repaint()
    }

    fun save(path: String) {
        LineChartDataFile.storeData(path, LineChartData(label, xLabel, yLabel, stops, series))
        this.storeChart(path)
    }

    val isShown get() = this.windowShown

    private fun getOrCreateSeries(label: String, color: Color): Series {
        val existingSeries = this.xchartSeries.find { it.name == label }

        if (existingSeries != null) {
            return existingSeries
        }

        val newSeries = this.chart.addSeries(label, doubleArrayOf(0.0), doubleArrayOf(0.0))
        newSeries.marker = SeriesMarkers.NONE
        newSeries.lineColor = color
        this.xchartSeries.add(newSeries)

        return newSeries
    }

    private fun repaint() {
        this.chartUIPanel.let {
            it.revalidate()
            it.repaint()
        }
    }

    private fun storeChart(filePath: String) =
        VectorGraphicsEncoder.saveVectorGraphic(this.chart, filePath, VectorGraphicsEncoder.VectorGraphicsFormat.SVG)

    companion object {
        fun loadFromFile(filePath: String): LineChart = with(LineChartDataFile.loadData(filePath)) {
            LineChart(label, xLabel, yLabel).apply {
                updateChart(series, stops)
            }
        }
    }
}