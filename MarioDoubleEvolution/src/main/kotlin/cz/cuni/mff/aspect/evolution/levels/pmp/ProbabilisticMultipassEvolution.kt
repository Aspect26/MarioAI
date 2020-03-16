package cz.cuni.mff.aspect.evolution.levels.pmp

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.evolution.levels.LevelEvolution
import cz.cuni.mff.aspect.evolution.levels.MarioLevelEvaluator
import cz.cuni.mff.aspect.evolution.levels.MarioLevelEvaluators
import cz.cuni.mff.aspect.extensions.getDoubleValues
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.level.MarioLevel
import io.jenetics.*
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import java.util.function.Function
import java.util.stream.Collectors

class ProbabilisticMultipassEvolution(
    private val populationSize: Int = 50,
    private val generationsCount: Int = 100,
    private val levelLength: Int = 200,
    private val evaluateOnLevelsCount: Int = 5,
    private val fitnessFunction: MarioLevelEvaluator<Float> = MarioLevelEvaluators::distanceOnly,
    private val maxProbability: Double = 0.3
) : LevelEvolution {

    private lateinit var agentFactory: () -> IAgent

    override fun evolve(agentFactory: () -> IAgent): Array<MarioLevel> {
        this.agentFactory = agentFactory
        val genotype = this.createInitialGenotype()
        val evolutionEngine = this.createEvolutionEngine(genotype)
        val result = this.doEvolution(evolutionEngine)

        // TODO: return multiple levels
        val genes = result.bestPhenotype.genotype.getDoubleValues()
        val level = PMPLevelCreator.create(levelLength, genes)
        print(genes.contentToString())
        return arrayOf(level)
    }

    private fun createInitialGenotype(): Genotype<DoubleGene> {
        return Genotype.of(DoubleChromosome.of(*Array<DoubleGene>(PMPLevelCreator.PROBABILITIES_COUNT) { DoubleGene.of(0.0, 0.0, this.maxProbability) }))
    }

    private fun createEvolutionEngine(initialGenotype: Genotype<DoubleGene>): Engine<DoubleGene, Float> {
        return Engine.builder(fitness, initialGenotype)
            .optimize(Optimize.MAXIMUM)
            .populationSize(this.populationSize)
            .alterers(GaussianMutator(0.60))
            .survivorsSelector(EliteSelector(2))
            .offspringSelector(RouletteWheelSelector())
            .mapping { evolutionResult ->
                println("new gen: ${evolutionResult.generation} (best fitness: ${evolutionResult.bestFitness})")
                evolutionResult
            }
            .build()
    }

    private fun doEvolution(evolutionEngine: Engine<DoubleGene, Float>): EvolutionResult<DoubleGene, Float> {
        return evolutionEngine.stream()
            .limit(this.generationsCount.toLong())
            .collect(EvolutionResult.toBestEvolutionResult<DoubleGene, Float>())
    }

    private val fitness = Function<Genotype<DoubleGene>, Float> { genotype -> fitness(genotype) }
    private fun fitness(genotype: Genotype<DoubleGene>): Float {

        // TODO: reevaluate on every generation for all individuals
        val genes = genotype.getDoubleValues()

        val levels = Array(this.evaluateOnLevelsCount) { PMPLevelCreator.create(levelLength, genes) }
        val stats = levels.map {level ->
            val agent = this.agentFactory()
            val marioSimulator = GameSimulator()
            marioSimulator.playMario(agent, level, false)
        }

        return this.fitnessFunction(stats)
    }

}
