package cz.cuni.mff.aspect.mario

import ch.idsia.agents.IAgent
import ch.idsia.benchmark.mario.engine.sprites.Mario.STATUS_DEAD
import ch.idsia.benchmark.mario.engine.sprites.Mario.STATUS_WIN
import ch.idsia.benchmark.mario.environments.MarioEnvironment
import ch.idsia.benchmark.mario.options.FastOpts
import ch.idsia.benchmark.mario.options.MarioOptions
import cz.cuni.mff.aspect.mario.controllers.MarioAction
import cz.cuni.mff.aspect.mario.controllers.MarioController
import cz.cuni.mff.aspect.mario.level.MarioLevel

/**
 * Wrapper atop Super Mario Infinite's simulator.
 *
 * It uses an implementation of [MarioAgent] or [MarioController] as an agent playing Super Mario Infinite. It also uses
 * [MarioLevel] entities specifying which levels the agent should play.
 */
class GameSimulator(private val maxTicks: Int = DEFAULT_MAX_TICKS) {

    private val environment = MarioEnvironment(null)
    private var currentTick: Int = 0
    lateinit var statistics: GameStatistics

    fun playRandomLevels(controller: MarioController, levels: Array<MarioLevel>, levelsCount: Int, visualize: Boolean = true): Array<GameStatistics> {
        val lastIndex = if (levels.size < levelsCount) levels.size else levelsCount
        val levelsToPlay = levels.toMutableList().shuffled().subList(0, lastIndex).toTypedArray()

        return this.playMario(controller, levelsToPlay, visualize)
    }

    fun playMario(marioController: MarioController, levels: Array<MarioLevel>, visualize: Boolean = true): Array<GameStatistics> {
        return Array(levels.size) {
            this.playMario(marioController, levels[it], visualize)
        }
    }

    fun playMario(marioController: MarioController, level: MarioLevel, visualize: Boolean = true): GameStatistics {
        val marioAgent = MarioAgent(marioController)
        val marioLevelGenerator = SingleLevelLevelGenerator(level)

        return this.playMario(marioAgent, marioLevelGenerator, visualize)
    }

    fun playMario(marioAgent: IAgent, level: MarioLevel, visualize: Boolean = true): GameStatistics {
        val marioLevelGenerator = SingleLevelLevelGenerator(level)

        return this.playMario(marioAgent, marioLevelGenerator, visualize)
    }

    fun playMario(marioAgent: IAgent, levelGenerator: SingleLevelLevelGenerator, visualize: Boolean = true): GameStatistics {
        this.currentTick = 0

        if (visualize) {
            MarioOptions.reset(FastOpts.VIS_ON_2X)
        } else {
            MarioOptions.reset(FastOpts.VIS_OFF)
        }

        this.environment.reset(marioAgent, levelGenerator)
        var marioJumps = 0
        var marioSpecials = 0
        var marioHurts = 0
        var currentMarioMode = environment.mario.mode

        while (!environment.isLevelFinished && this.currentTick < this.maxTicks) {
            environment.tick()
            marioAgent.observe(environment)
            val actions = marioAgent.actionSelection()
            if (marioAgent is MarioAgent && marioAgent.lastActions.contains(MarioAction.JUMP) && environment.mario.mayJump)
                marioJumps++

            if (marioAgent is MarioAgent && marioAgent.lastActions.contains(MarioAction.SPECIAL))
                marioSpecials++

            environment.mario.mode

            environment.performAction(actions)
            marioAgent.receiveReward(environment.intermediateReward.toFloat())

            if (currentMarioMode != null && environment.mario.mode.code < currentMarioMode.code)
                marioHurts++

            if (environment.mario.status == STATUS_DEAD)
                marioHurts++

            currentMarioMode = environment.mario.mode

            this.currentTick++
        }

        this.statistics = GameStatistics(environment.marioSprite.x, marioJumps, marioSpecials, environment.mario.killsTotal,
            marioHurts, environment.mario.status == STATUS_DEAD, environment.mario.status == STATUS_WIN)

        return this.statistics
    }

    companion object {
        private const val DEFAULT_MAX_TICKS = 1000
    }

}


data class GameStatistics(val finalMarioDistance: Float, val jumps: Int, val specials: Int, val kills: Int,
                          val marioHurts: Int, val marioDied: Boolean, val levelFinished: Boolean) {
    companion object {
        fun empty(): GameStatistics = GameStatistics(0f, 0, 0, 0, 0,
            false, false)
    }
}
