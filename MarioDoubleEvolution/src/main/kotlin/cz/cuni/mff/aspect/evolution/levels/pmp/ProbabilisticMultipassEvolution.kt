package cz.cuni.mff.aspect.evolution.levels.pmp

import ch.idsia.agents.IAgent
import cz.cuni.mff.aspect.evolution.levels.LevelEvolution
import cz.cuni.mff.aspect.evolution.levels.MarioLevelEvaluators
import cz.cuni.mff.aspect.extensions.getDoubleValues
import cz.cuni.mff.aspect.mario.GameSimulator
import cz.cuni.mff.aspect.mario.MarioAgent
import cz.cuni.mff.aspect.mario.level.MarioLevel
import io.jenetics.*
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import java.util.function.Function

class ProbabilisticMultipassEvolution(
    private val populationSize: Int = 50,
    private val generationsCount: Int = 200,
    private val levelLength: Int = 200,
    private val evaluateOnLevelsCount: Int = 5
) : LevelEvolution {

    private lateinit var agent: IAgent

    override fun evolve(agent: IAgent): Array<MarioLevel> {
        this.agent = agent
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
        // TODO: constant omg
        return Genotype.of(DoubleChromosome.of(*Array<DoubleGene>(11) { DoubleGene.of(0.0, 0.0, 0.3) }))
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
            // TODO: this is a hack!!! for some reason if we directly pass agent, it crashes
            val agent = MarioAgent((this.agent as MarioAgent).controller)
            val marioSimulator = GameSimulator()
            marioSimulator.playMario(agent, level, false)
        }

        // TODO: fitness should be in ctor 
        return MarioLevelEvaluators.distanceOnly(stats)
    }

}