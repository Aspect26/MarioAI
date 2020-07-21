package cz.cuni.mff.aspect.launch

import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.DifficultyEvaluator
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.LinearityEvaluator
import cz.cuni.mff.aspect.evolution.levels.pmp.evaluators.PNGCompressionEvaluator
import cz.cuni.mff.aspect.evolution.levels.pmp.PMPLevelGenerator
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.GameStatistics
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.storage.ObjectStorage
import cz.cuni.mff.aspect.visualisation.charts.DataSeries
import cz.cuni.mff.aspect.visualisation.charts.evolution.CoevolutionLineChart
import cz.cuni.mff.aspect.visualisation.charts.evolution.EvolutionLineChart
import cz.cuni.mff.aspect.visualisation.charts.linechart.AverageLineChart
import cz.cuni.mff.aspect.visualisation.charts.linechart.LineChart
import cz.cuni.mff.aspect.visualisation.charts.linechart.LineChartData
import cz.cuni.mff.aspect.visualisation.charts.linechart.LineChartDataFile
import cz.cuni.mff.aspect.visualisation.level.LevelVisualiser
import java.awt.Color
import java.io.File


/** Launcher used for development purposes. */
fun main() {
//    increaseChartTexts()
//    outputData()
//    fixCharts()
//    renderCoevolutionAverageChart()
//    levelImages("data/experiments/final-experiments/coev/final-upl9-2/neuro_pc/lg_20.lg")
    evaluatePlayer(
        "data/experiments/final-experiments/coev/final-upl7/neuro_pmp/lg_20.lg",
        "data/experiments/final-experiments/ai/neuro/against coev/upl7-pmp/NeuroEvolution, experiment 1_ai.ai"
//        "data/experiments/final-experiments/coev/final-upl8/neuro_pc/ai_20.ai"
    )
//    evaluateCoevLGs(arrayOf(
//        "data/experiments/final-experiments/coev/final-chic-1/neuro_pmp",
//        "data/experiments/final-experiments/coev/final-dyscalculia/neuro_pmp",
//        "data/experiments/final-experiments/coev/final-mayrau-1/neuro_pmp",
//        "data/experiments/final-experiments/coev/final-upl6/neuro_pmp",
//        "data/experiments/final-experiments/coev/final-upl7/neuro_pmp",
//        "data/experiments/final-experiments/coev/final-upl7-2/neuro_pmp",
//        "data/experiments/final-experiments/coev/final-upl8/neuro_pmp",
//        "data/experiments/final-experiments/coev/final-upl8-2/neuro_pmp",
//        "data/experiments/final-experiments/coev/final-upl9/neuro_pmp",
//        "data/experiments/final-experiments/coev/final-upl9-2/neuro_pmp"
//    ), 20, 50)
//    fixChartY("data/experiments/final-experiments/ai/neuro/against coev/upl7-pmp/NeuroEvolution, experiment 1_chart.svg.dat")
}

private fun outputData() {
    val experiment = "500:100:DWP30:7x7:false:false"
    val path = "data/experiments/final-experiments/neat/$experiment/average_chart.svg.dat"
    val chartData = LineChartDataFile.loadData(path)

    chartData.series.forEach {
        println("${it.label} : ${it.data.map { it.second }.max()}")
    }
}

private fun renderCoevolutionAverageChart() {
//    val experiments = arrayOf(
//        "final-chic-1",
//        "final-dyscalculia",
//        "final-mayrau-1",
//        "final-upl6",
//        "final-upl7",
//        "final-upl7-2",
//        "final-upl8",
//        "final-upl8-2",
//        "final-upl9",
//        "final-upl9-2"
//    )
//    val type = "neuro_pmp"

    val experiments = arrayOf(
        "neat-upl1",
        "neat-upl2",
        "neat-upl3",
        "neat-upl4",
        "neat-upl5",
        "neat-upl6",
        "neat-upl7",
        "neat-upl8",
        "neat-upl9",
        "neat-upl10"
    )
    val type = "neat_pmp"

    val fitnessLineCharts = experiments
        .filter{ File("data/experiments/final-experiments/coev/$it/$type/coev-fitness.svg.dat").exists() }
        .map{ LineChart.loadFromFile("data/experiments/final-experiments/coev/$it/$type/coev-fitness.svg.dat") }

    val objectiveLineCharts = experiments
        .filter{ File("data/experiments/final-experiments/coev/$it/$type/coev-objective.svg.dat").exists() }
        .map{ LineChart.loadFromFile("data/experiments/final-experiments/coev/$it/$type/coev-objective.svg.dat") }

//    val c1 = AverageLineChart(fitnessLineCharts.toTypedArray())
//    c1.averagedLineChart.setFontSize(20f)
//    c1.renderChart()

    val c2 = AverageLineChart(objectiveLineCharts.toTypedArray())
    c2.averagedLineChart.setFontSize(20f)
    c2.averagedLineChart.setLegendOutside()
    c2.renderChart()
}

private fun fixCharts() {
    val paths = arrayOf(
        "data/experiments/final-experiments/coev/final-chic-1/neuro_pc",
        "data/experiments/final-experiments/coev/final-chic-1/neuro_pmp",
        "data/experiments/final-experiments/coev/final-dyscalculia/neuro_pc",
        "data/experiments/final-experiments/coev/final-dyscalculia/neuro_pmp",
        "data/experiments/final-experiments/coev/final-mayrau-1/neuro_pc",
        "data/experiments/final-experiments/coev/final-mayrau-1/neuro_pmp",
        "data/experiments/final-experiments/coev/final-upl6/neuro_pc",
        "data/experiments/final-experiments/coev/final-upl6/neuro_pmp",
        "data/experiments/final-experiments/coev/final-upl7/neuro_pc",
        "data/experiments/final-experiments/coev/final-upl7/neuro_pmp",
        "data/experiments/final-experiments/coev/final-upl7-2/neuro_pc",
        "data/experiments/final-experiments/coev/final-upl7-2/neuro_pmp",
        "data/experiments/final-experiments/coev/final-upl8/neuro_pc",
        "data/experiments/final-experiments/coev/final-upl8/neuro_pmp",
        "data/experiments/final-experiments/coev/final-upl8-2/neuro_pc",
        "data/experiments/final-experiments/coev/final-upl8-2/neuro_pmp",
        "data/experiments/final-experiments/coev/final-upl9/neuro_pc",
        "data/experiments/final-experiments/coev/final-upl9/neuro_pmp",
        "data/experiments/final-experiments/coev/final-upl9-2/neuro_pc",
        "data/experiments/final-experiments/coev/final-upl9-2/neuro_pmp"
    )

    // LG
    paths.forEach { path ->
        val chartDataFile = LineChartDataFile.loadData("$path/lg.svg.dat")
        val updatedSeries = chartDataFile.series.map { dataSeries ->
            if (!dataSeries.label.contains("objective")) {
                return@map dataSeries
            }

            DataSeries(dataSeries.label, dataSeries.color, dataSeries.data.map { (x, y) ->
                Pair(x, y / 50000)
            }.toMutableList())
        }

        val updatedData = LineChartData(chartDataFile.label, chartDataFile.xLabel, chartDataFile.yLabel, chartDataFile.stops, updatedSeries)
        val chart = LineChart.loadFromData(updatedData)
        chart.save("$path/lg.svg")
    }

    // coev-missing last generation
    paths.forEach { path ->
        val controllerChart = EvolutionLineChart.loadFromFile("$path/ai.svg.dat")
        val levelGeneratorChart = EvolutionLineChart.loadFromFile("$path/lg.svg.dat")

        val coevolutionChart = CoevolutionLineChart(
            controllerChart,
            levelGeneratorChart,
            "Coevolution"
        )

        coevolutionChart.storeChart("$path/coev.svg")
    }

    // coev-fitness
    paths.forEach { path ->
        val chartDataFile = LineChartDataFile.loadData("$path/coev-fitness.svg.dat")
        val updatedSeries = chartDataFile.series.map { dataSeries ->
            if (!dataSeries.label.contains("Level Generator")) {
                return@map dataSeries
            }

            DataSeries(dataSeries.label, dataSeries.color, dataSeries.data.map { (x, y) ->
                Pair(x, y * 35000)
            }.toMutableList())
        }

        val updatedData = LineChartData(chartDataFile.label, chartDataFile.xLabel, chartDataFile.yLabel, chartDataFile.stops, updatedSeries)
        val chart = LineChart.loadFromData(updatedData)
        chart.save("$path/coev-fitness.svg")
    }

    // coev-objective
    paths.forEach { path ->
        val chartDataFile = LineChartDataFile.loadData("$path/coev-objective.svg.dat")
        val updatedSeries = chartDataFile.series.map { dataSeries ->
            if (!dataSeries.label.contains("Level Generator")) {
                return@map dataSeries
            }

            DataSeries(dataSeries.label, dataSeries.color, dataSeries.data.map { (x, y) ->
                Pair(x, y * 50000)
            }.toMutableList())
        }

        val updatedData = LineChartData(chartDataFile.label, chartDataFile.xLabel, chartDataFile.yLabel, chartDataFile.stops, updatedSeries)
        val chart = LineChart.loadFromData(updatedData)
        chart.save("$path/coev-objective.svg")
    }
}

private fun increaseChartTexts() {
//    val chartsData = (1 .. 4).map { LineChartDataFile.loadData("./data/experiments/final-experiments/lg/pc/fit+complexity+linearity_bestNEATPlayer/experiment ${it}_chart.svg.dat") }
//    val averagedChart = AverageLineChart.fromData(chartsData)
//    val chart = averagedChart.averagedLineChart

    val chart = LineChart.loadFromFile("chart.svg.dat")

    chart.setFontSize(20f)
    chart.renderChart()
}

private fun levelImages(path: String, count: Int = 5) {
    val generator = ObjectStorage.load<LevelGenerator>(path)

    repeat(count) {
        LevelVisualiser().display(generator.generate())
    }
}

private fun evaluatePlayer(lgPath: String, aiPath: String, count: Int = 200) {
    val generator = ObjectStorage.load<LevelGenerator>(lgPath)
    val player = MarioAgent(ObjectStorage.load(aiPath))
    val simulator = GameSimulator(2000)

    val victories = (1 .. count)
        .map { simulator.playMario(player, generator.generate(), false).levelFinished }
        .map { if (it) 1 else 0 }

    val victoriesCount = victories.sum()

    println("Levels finished: $victoriesCount/${victories.size} (${((victoriesCount / victories.size.toFloat()) * 100).toInt()}%)")
}

private fun evaluateCoevLG(coevPath: String, generations: Int, evalOnCount: Int = 100): LineChart {
    val linearityData: MutableList<Pair<Double, Double>> = mutableListOf()
    val difficultyData: MutableList<Pair<Double, Double>> = mutableListOf()
    val complexityData: MutableList<Pair<Double, Double>> = mutableListOf()

    (1 .. generations).forEach {
        println("Evaluating generation: $it")
        val lg = ObjectStorage.load<PMPLevelGenerator>("$coevPath/lg_$it.lg")
        val linearity = (0 until evalOnCount).map { LinearityEvaluator().evaluateOne(lg.generate(), lg.lastMetadata, GameStatistics.empty()) }.average()
        val complexity = (0 until evalOnCount).map { PNGCompressionEvaluator().evaluateOne(lg.generate(), lg.lastMetadata, GameStatistics.empty()) }.average()
        val difficulty = (0 until evalOnCount).map { DifficultyEvaluator().evaluateOne(lg.generate(), lg.lastMetadata, GameStatistics.empty()) }.average()

        linearityData.add(Pair(it.toDouble(), linearity))
        complexityData.add(Pair(it.toDouble(), complexity / 750f))
        difficultyData.add(Pair(it.toDouble(), difficulty))
    }

    val series = listOf(
        DataSeries("Linearity", Color(134, 52, 9), linearityData),
        DataSeries("Difficulty", Color(72, 28, 134), difficultyData),
        DataSeries("Complexity", Color(10, 98, 135), complexityData)
    )
    val chart = LineChart("Multipass level generator evaluation", "Coevolution generation", "Fitness value")
    chart.updateChart(series)

    return chart
}

private fun evaluateCoevLGs(coevPaths: Array<String>, generations: Int, evalOnCount: Int = 100) {
    val charts = coevPaths.map { evaluateCoevLG(it, generations, evalOnCount) }.toTypedArray()
    val averageChart = AverageLineChart(charts)
    averageChart.averagedLineChart.setFontSize(20f)
    averageChart.storeChart("data/charts/lg_evaluation/pmp.svg")
}

private fun fixChartY(chartDataFilePath: String) {
    val chart = LineChart.loadFromFile(chartDataFilePath)
    chart.setFontSize(20f)
    chart.renderChart()
}