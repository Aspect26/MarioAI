package cz.cuni.mff.aspect.coevolution

import cz.cuni.mff.aspect.evolution.controller.ControllerSerializer
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
        lastControllerPopulation: List<MarioController>,
        lastLevelGeneratorsPopulation: List<LevelGenerator>,
        coevolutionTimer: CoevolutionTimer
    ) {
        ObjectStorage.store("${settings.storagePath}/ai_${coevolutionGeneration}.ai", controller)
        ObjectStorage.store("${settings.storagePath}/lg_${coevolutionGeneration}.lg", levelGenerator)
        LocalTextFileStorage.storeData("${settings.storagePath}/lg_last_population.dat",
            lastLevelGeneratorsPopulation.joinToString(System.lineSeparator()) { LevelGeneratorSerializer.serialize(it) })
        LocalTextFileStorage.storeData("${settings.storagePath}/ai_last_population.dat",
            lastControllerPopulation.joinToString(System.lineSeparator()) { ControllerSerializer.serialize(it) })
        this.storeCharts(settings)
        coevolutionTimer.store("${settings.storagePath}/timers.dat")
    }

    fun <T: LevelGenerator> loadState(settings: CoevolutionSettings<T>): CoevolutionState<T> {
        val lastFinishedGeneration = this.getLastStoredCoevolutionGenerationNumber(settings)
        if (lastFinishedGeneration == 0) {
            throw IllegalArgumentException("Can't load state of given coevolution because no generation was finished.")
        }

        val rawLastGeneratorPopulationData = LocalTextFileStorage.loadData("${settings.storagePath}/lg_last_population.dat")
        val lastGeneratorsPopulation = rawLastGeneratorPopulationData.lines().map { LevelGeneratorSerializer.deserialize<T>(it) }

        val rawLastControllerPopulationData = LocalTextFileStorage.loadData("${settings.storagePath}/ai_last_population.dat")
        val lastControllersPopulation = rawLastControllerPopulationData.lines().map { ControllerSerializer.deserialize(it) }

        val controller = ObjectStorage.load<MarioController>("${settings.storagePath}/ai_$lastFinishedGeneration.ai")
        val levelGenerator = ObjectStorage.load<LevelGenerator>("${settings.storagePath}/lg_$lastFinishedGeneration.lg")
        val controllerEvolutionChart = EvolutionLineChart.loadFromFile("${settings.storagePath}/ai.svg.dat")
        val generatorEvolutionChart = EvolutionLineChart.loadFromFile("${settings.storagePath}/lg.svg.dat")
        val coevolutionTimer = CoevolutionTimer.loadFromFile("${settings.storagePath}/timers.dat")

        return CoevolutionState(lastFinishedGeneration, controller, levelGenerator, lastControllersPopulation,
            lastGeneratorsPopulation, controllerEvolutionChart, generatorEvolutionChart, coevolutionTimer)
    }

    /**
     * Gets number of last finished generation of given coevolution or 0 if no coevolution generation was finished.
     *
     * @param settings coevolution settings used to run the coevolution.
     */
    private fun <T: LevelGenerator> getLastStoredCoevolutionGenerationNumber(settings: CoevolutionSettings<T>): Int {
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

    /**
     * Stores coevolution charts.
     *
     * @param settings coevolution settings used to run the coevolution.
     */
    private fun <T: LevelGenerator> storeCharts(settings: CoevolutionSettings<T>) {
        val controllerChart = settings.controllerEvolution.chart
        val levelGeneratorChart = settings.generatorEvolution.chart
        val coevolutionChart = CoevolutionLineChart(controllerChart, levelGeneratorChart, "Coevolution")

        controllerChart.store("${settings.storagePath}/ai.svg")
        levelGeneratorChart.store("${settings.storagePath}/lg.svg")
        coevolutionChart.storeChart("${settings.storagePath}/coev.svg")
    }

}