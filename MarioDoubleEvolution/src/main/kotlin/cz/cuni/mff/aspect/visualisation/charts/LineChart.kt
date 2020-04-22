package cz.cuni.mff.aspect.visualisation.charts

import org.knowm.xchart.*
import org.knowm.xchart.internal.chartpart.*
import org.knowm.xchart.internal.series.Series
import org.knowm.xchart.style.Styler
import org.knowm.xchart.style.XYStyler
import org.knowm.xchart.style.markers.SeriesMarkers
import java.awt.BorderLayout
import java.awt.Color
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import javax.swing.JFrame


class LineChart(label: String = "Line chart", xLabel: String = "X", yLabel: String = "Y") {

    private val chart: XYChart = XYChartBuilder()
        .width(600)
        .height(480)
        .title(label)
        .xAxisTitle(xLabel)
        .yAxisTitle(yLabel)
        .build()
    private lateinit var customPlotContent: XYPlotContentWithStops

    private var series: MutableList<Series> = mutableListOf()
    private lateinit var chartUIPanel: XChartPanel<XYChart>
    private var windowShown = false

    init {
        chart.styler.defaultSeriesRenderStyle = XYSeries.XYSeriesRenderStyle.Line
        chart.styler.isChartTitleVisible = true
        chart.styler.legendPosition = Styler.LegendPosition.InsideNW
        chart.styler.isLegendVisible = true
        chart.styler.markerSize = 16

        // TODO: why not override XYChart
        val plot = Chart::class.java.getDeclaredField("plot").apply { isAccessible = true }.get(chart) as Plot_<*, *>
        val plotContent = Plot_::class.java.getDeclaredField("plotContent").apply { isAccessible = true }.get(plot) as PlotContent_<*, *>
        val plotContentChart = PlotContent_::class.java.getDeclaredField("chart").apply { isAccessible = true }.get(plotContent) as Chart<XYStyler, XYSeries>

        this.customPlotContent = XYPlotContentWithStops(plotContentChart)
        val field = Plot_::class.java.getDeclaredField("plotContent")
        field.trySetAccessible()
        field.set(plot, this.customPlotContent)

        println(plotContentChart.toString())
    }

    fun renderChart() {
        this.windowShown = true
        javax.swing.SwingUtilities.invokeLater {
            val frame = JFrame("Chart")
            frame.layout = BorderLayout()

            this.chartUIPanel = XChartPanel(chart)
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

    fun updateChart(values: List<Triple<String, Color, List<Pair<Double, Double>>>>, stops: List<Double> = emptyList()) {
        for ((seriesLabel, seriesColor, seriesData) in values) {
            val currentSeries = this.getOrCreateSeries(seriesLabel, seriesColor)

            val xData: DoubleArray = seriesData.map { it.first }.toDoubleArray()
            val yData: DoubleArray = seriesData.map { it.second }.toDoubleArray()

            this.chart.updateXYSeries(currentSeries.name, xData, yData, null)
        }

        this.customPlotContent.stops = stops

        if (this::chartUIPanel.isInitialized) {
            this.chartUIPanel.revalidate()
            this.chartUIPanel.repaint()
        }
    }

    fun save(path: String) {
        val directoryPath = path.replaceAfterLast("/", "")

        val directory = File(directoryPath)
        if (!directory.exists()) directory.mkdirs()

        VectorGraphicsEncoder.saveVectorGraphic(this.chart, path, VectorGraphicsEncoder.VectorGraphicsFormat.SVG)
    }

    val isShown get() = this.windowShown

    private fun getOrCreateSeries(label: String, color: Color): Series {
        val existingSeries = this.series.find { it.name == label }

        if (existingSeries != null) {
            return existingSeries
        }

        val newSeries = this.chart.addSeries(label, doubleArrayOf(0.0), doubleArrayOf(0.0))
        newSeries.marker = SeriesMarkers.NONE
        newSeries.lineColor = color
        this.series.add(newSeries)

        return newSeries
    }

}