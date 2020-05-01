package cz.cuni.mff.aspect.visualisation.charts

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


class LineChart(label: String = "Line chart", xLabel: String = "X", yLabel: String = "Y") {

    private val chart: XYChartWithStops =
        XYChartWithStops(
            XYChartBuilder()
                .width(1920)
                .height(1080)
                .title(label)
                .xAxisTitle(xLabel)
                .yAxisTitle(yLabel)
        )

    private var series: MutableList<Series> = mutableListOf()
    private lateinit var chartUIPanel: XChartPanel<XYChart>
    private var windowShown = false

    init {
        chart.styler.defaultSeriesRenderStyle = XYSeries.XYSeriesRenderStyle.Line
        chart.styler.isChartTitleVisible = true
        chart.styler.legendPosition = Styler.LegendPosition.InsideNW
        chart.styler.isLegendVisible = true
        chart.styler.markerSize = 16
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

    fun updateChart(values: List<SeriesData>, stops: List<Double> = emptyList()) {
        for ((seriesLabel, seriesColor, seriesData) in values) {
            val currentSeries = this.getOrCreateSeries(seriesLabel, seriesColor)

            val xData: DoubleArray = seriesData.map { it.first }.toDoubleArray()
            val yData: DoubleArray = seriesData.map { it.second }.toDoubleArray()

            this.chart.updateXYSeries(currentSeries.name, xData, yData, null)
        }

        this.chart.setStops(stops)

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