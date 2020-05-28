package cz.cuni.mff.aspect.launch


import cz.cuni.mff.aspect.coevolution.Coevolution
import cz.cuni.mff.aspect.coevolution.CoevolutionSettings
import cz.cuni.mff.aspect.evolution.controller.neuroevolution.NeuroControllerEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGenerator
import cz.cuni.mff.aspect.evolution.levels.chunks.PCLevelGeneratorEvolution
import cz.cuni.mff.aspect.evolution.levels.chunks.evaluators.AgentHalfPassing
import cz.cuni.mff.aspect.mario.controllers.ann.NetworkSettings
import cz.cuni.mff.aspect.mario.controllers.ann.SimpleANNController
import cz.cuni.mff.aspect.mario.controllers.ann.networks.HiddenLayerControllerNetwork
import cz.cuni.mff.aspect.visualisation.charts.evolution.EvolutionLineChart

/** Launcher used for development purposes. */
fun main() {
//    startCoev()
//    recreateChart()
    continueCoev()
}

private const val generationsCount = 10
private val networkSettings = NetworkSettings(5, 5, 0, 2, 5)
private val coevolutionSettings = CoevolutionSettings(
    NeuroControllerEvolution(
        networkSettings,
        populationSize = 20,
        generationsCount = generationsCount,
        displayChart = true),

    PCLevelGeneratorEvolution(
        populationSize = 20,
        generationsCount = generationsCount,
        fitnessFunction = AgentHalfPassing(),
        objectiveFunction = AgentHalfPassing(),
        evaluateOnLevelsCount = 10,
        chunksCount = 20,
        displayChart = true),

    SimpleANNController(HiddenLayerControllerNetwork(networkSettings)),
    PCLevelGenerator.createSimplest(),
    5,
    2,
    "result/test"
)

private fun startCoev() {
    val coevolution = Coevolution()
    coevolution.startEvolution(coevolutionSettings)
}

private fun recreateChart() {
    val aiChart = EvolutionLineChart.loadFromFile("${coevolutionSettings.storagePath}/ai.svg.dat")
    val lgChart = EvolutionLineChart.loadFromFile("${coevolutionSettings.storagePath}/lg.svg.dat")

    aiChart.show()
    lgChart.show()
}

private fun continueCoev() {
    val coevolution = Coevolution()
    coevolution.continueCoevolution(coevolutionSettings)
}
