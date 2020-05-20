package cz.cuni.mff.aspect.visualisation.charts

import java.awt.Color

/** Represents [cz.cuni.mff.aspect.visualisation.charts.linechart.LineChart]'s  data series. */
data class DataSeries(val label: String = "", val color: Color = Color.BLUE, var data: MutableList<Pair<Double, Double>>) {

    fun multiplyValuesBy(factor: Double) {
        this.data = this.data.map { (x, y) -> Pair(x, y * factor) }.toMutableList()
    }

}
