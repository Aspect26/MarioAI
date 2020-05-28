package cz.cuni.mff.aspect.coevolution

import cz.cuni.mff.aspect.evolution.levels.LevelGenerator
import cz.cuni.mff.aspect.evolution.levels.LevelGeneratorSerializer
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.storage.LocalTextFileStorage
import cz.cuni.mff.aspect.storage.ObjectStorage
import cz.cuni.mff.aspect.visualisation.charts.evolution.CoevolutionLineChart
import cz.cuni.mff.aspect.visualisation.charts.evolution.EvolutionLineChart
import java.io.File
import java.util.regex.Pattern

/** Storage for data from coevolution. */
object CoevolutionStorage {

    /**
     * Stores coevolution state after given generation.
     *
     * @param settings coevolution settings used to run the coevolution.
     * @param coevolutionGeneration the generation of coevolution.
     * @param controller the resulting controller.
     */
    fun <T: LevelGenerator> storeState(
        settings: CoevolutionSettings<T>,
        coevolutionGeneration: Int,
        controller: MarioController,
        levelGenerator: LevelGenerator,
        lastLevelGeneratorsPopulation: List<LevelGenerator>
    ) {
        ObjectStorage.store("${settings.storagePath}/ai_${coevolutionGeneration}.ai", controller)
        ObjectStorage.store("${settings.storagePath}/lg_${coevolutionGeneration}.lg", levelGenerator)
        LocalTextFileStorage.storeData("${settings.storagePath}/lg_last_population.dat",
            lastLevelGeneratorsPopulation.joinToString { LevelGeneratorSerializer.serialize(it) + System.lineSeparator() })
        this.storeCharts(settings)
    }

    // TODO: maybe also introduce load state instead of multiple of these load functions

    /**
     * Loads result controller from a given coevolution generation. If the generation is not specified, it loads
     * controller from the last generation.
     *
     * @param settings coevolution settings used to run the coevolution.
     * @param coevolutionGeneration coevolution generation from which the controller should be loaded.
     */
    fun <T: LevelGenerator> loadController(settings: CoevolutionSettings<T>, coevolutionGeneration: Int = settings.generations): MarioController =
        ObjectStorage.load("${settings.storagePath}/ai_$coevolutionGeneration.ai")

    /**
     * Loads result level generator from a given coevolution generation. If the generation is not specified, it loads
     * controller from the last generation.
     *
     * @param settings coevolution settings used to run the coevolution.
     * @param coevolutionGeneration coevolution generation from which the level generator should be loaded.
     */
    fun <T: LevelGenerator> loadLevelGenerator(settings: CoevolutionSettings<T>, coevolutionGeneration: Int = settings.generations): LevelGenerator =
        ObjectStorage.load("${settings.storagePath}/lg_$coevolutionGeneration.lg")

    /**
     * Loads last poopulation of level generators evolution.
     *
     * @param settings coevolution settings used to run the coevolution.
     */
    fun <T: LevelGenerator> loadLastLevelGeneratorsPopulation(settings: CoevolutionSettings<T>): List<T> {
        val rawData = LocalTextFileStorage.loadData("${settings.storagePath}/lg_last_population.dat")
        return rawData.lines().map { LevelGeneratorSerializer.deserialize<T>(it) }
    }

    /**
     * Loads evolution line chart of controller from given coevolution.
     *
     * @param settings coevolution settings used to run the coevolution.
     */
    fun <T: LevelGenerator> loadControllerChart(settings: CoevolutionSettings<T>): EvolutionLineChart =
        EvolutionLineChart.loadFromFile("${settings.storagePath}/ai.svg.dat")

    /**
     * Loads evolution line chart of level generators from given coevolution.
     *
     * @param settings coevolution settings used to run the coevolution.
     */
    fun <T: LevelGenerator> loadLevelGeneratorsChart(settings: CoevolutionSettings<T>): EvolutionLineChart =
        EvolutionLineChart.loadFromFile("${settings.storagePath}/lg.svg.dat")


    /**
     * Gets number of last finished generation of given coevolution.
     *
     * @param settings coevolution settings used to run the coevolution.
     */
    fun <T: LevelGenerator> getLastStoredCoevolutionGenerationNumber(settings: CoevolutionSettings<T>): Int {
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

    private fun <T: LevelGenerator> storeCharts(settings: CoevolutionSettings<T>) {
        val controllerChart = settings.controllerEvolution.chart
        val levelGeneratorChart = settings.generatorEvolution.chart
        val coevolutionChart = CoevolutionLineChart(controllerChart, levelGeneratorChart, "Coevolution")

        controllerChart.store("${settings.storagePath}/ai.svg")
        levelGeneratorChart.store("${settings.storagePath}/lg.svg")
        coevolutionChart.storeChart("${settings.storagePath}/coev.svg")
    }

}