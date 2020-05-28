package cz.cuni.mff.aspect.coevolution

import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.storage.ObjectStorage
import cz.cuni.mff.aspect.visualisation.charts.evolution.CoevolutionLineChart
import cz.cuni.mff.aspect.visualisation.charts.evolution.EvolutionLineChart
import java.io.File
import java.util.regex.Pattern

/** Storage for data from coevolution. */
object CoevolutionStorage {

    /**
     * Stores given controller which is a result of a coevolution after given generation.
     *
     * @param settings coevolution settings used to run the coevolution.
     * @param coevolutionGeneration the generation of coevolution.
     * @param controller the resulting controller.
     */
    fun storeController(settings: CoevolutionSettings, coevolutionGeneration: Int, controller: MarioController) =
        ObjectStorage.store("${settings.storagePath}/ai_${coevolutionGeneration}.ai", controller)

    /**
     * Loads result controller from a given coevolution generation. If the generation is not specified, it loads
     * controller from the last generation.
     *
     * @param settings coevolution settings used to run the coevolution.
     * @param coevolutionGeneration coevolution generation from which the controller should be loaded.
     */
    fun loadController(settings: CoevolutionSettings, coevolutionGeneration: Int = settings.generations): MarioController =
        ObjectStorage.load("${settings.storagePath}/ai_$coevolutionGeneration.ai")

    /**
     * Stores given level generator which is a result of a coevolution after given generation.
     *
     * @param settings coevolution settings used to run the coevolution.
     * @param coevolutionGeneration the generation of coevolution.
     * @param levelGenerator the resulting level generator.
     */
    fun storeLevelGenerator(settings: CoevolutionSettings, coevolutionGeneration: Int, levelGenerator: LevelGenerator) =
        ObjectStorage.store("${settings.storagePath}/lg_${coevolutionGeneration}.lg", levelGenerator)

    /**
     * Loads result level generator from a given coevolution generation. If the generation is not specified, it loads
     * controller from the last generation.
     *
     * @param settings coevolution settings used to run the coevolution.
     * @param coevolutionGeneration coevolution generation from which the level generator should be loaded.
     */
    fun loadLevelGenerator(settings: CoevolutionSettings, coevolutionGeneration: Int = settings.generations): LevelGenerator =
        ObjectStorage.load("${settings.storagePath}/lg_$coevolutionGeneration.lg")

    /**
     * Stores all coevolution charts, which are controller evolution chart, level generators evolution chart and
     * combined coevolution chart.
     *
     * @param settings coevolution settings used to run the coevolution.
     */
    fun storeCharts(settings: CoevolutionSettings) {
        val controllerChart = settings.controllerEvolution.chart
        val levelGeneratorChart = settings.generatorEvolution.chart
        val coevolutionChart = CoevolutionLineChart(controllerChart, levelGeneratorChart, "Coevolution")

        controllerChart.store("${settings.storagePath}/ai.svg")
        levelGeneratorChart.store("${settings.storagePath}/lg.svg")
        coevolutionChart.storeChart("${settings.storagePath}/coev.svg")
    }

    /**
     * Loads evolution line chart of controller from given coevolution.
     *
     * @param settings coevolution settings used to run the coevolution.
     */
    fun loadControllerChart(settings: CoevolutionSettings): EvolutionLineChart =
        EvolutionLineChart.loadFromFile("${settings.storagePath}/ai.svg.dat")


    /**
     * Loads evolution line chart of level generators from given coevolution.
     *
     * @param settings coevolution settings used to run the coevolution.
     */
    fun loadLevelGeneratorsChart(settings: CoevolutionSettings): EvolutionLineChart =
        EvolutionLineChart.loadFromFile("${settings.storagePath}/lg.svg.dat")

    /**
     * Gets number of last finished generation of given coevolution.
     *
     * @param settings coevolution settings used to run the coevolution.
     */
    fun getLastStoredCoevolutionGenerationNumber(settings: CoevolutionSettings): Int {
        val storagePath = settings.storagePath
        val storageDirectory = File(storagePath)
        if (!storageDirectory.exists()) throw IllegalArgumentException("Can't restore the given coevolution, because directory $storagePath does not exist")
        if (!storageDirectory.isDirectory) throw IllegalArgumentException("Can't restore the given coevolution, because file $storagePath is not a directory")

        val filesInDirectory = storageDirectory.listFiles() ?: throw IllegalArgumentException("Can't restore the given coevolution, because directory $storagePath is empty")

        val lgFilePattern = Pattern.compile("lg_([0-9]+)\\.lg")

        return filesInDirectory
            .filter { it.isFile }
            .map { it.name }
            .filter { lgFilePattern.matcher(it).matches() }
            .map { val matcher = lgFilePattern.matcher(it); matcher.matches(); matcher.group(1).toInt() }
            .max() ?: 0
    }

}