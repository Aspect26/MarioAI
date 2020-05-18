package cz.cuni.mff.aspect.evolution.controller

import cz.cuni.mff.aspect.mario.level.MarioLevel
import cz.cuni.mff.aspect.mario.level.custom.OnlyPathLevel
import cz.cuni.mff.aspect.mario.level.custom.PathWithHolesLevel
import cz.cuni.mff.aspect.mario.level.original.Stage1Level1Split
import cz.cuni.mff.aspect.mario.level.original.Stage2Level1Split
import cz.cuni.mff.aspect.mario.level.original.Stage4Level1Split
import cz.cuni.mff.aspect.mario.level.original.Stage5Level1Split

/**
 * Super Mario levels data set. Contains original Super Mario Bros game levels `Stage 1 Level 1`, `Stage 2 Level 1`,
 * `Stage 4 Level 1` and `Stage 5 Level 1` split into smaller parts, as well as one flat level (doesn't contain any
 * obstacles and is flat), and one level containing only holes.
 */
val TrainingLevelsSet: Array<MarioLevel> = arrayOf(OnlyPathLevel, PathWithHolesLevel, *Stage1Level1Split.levels,
    *Stage2Level1Split.levels, *Stage4Level1Split.levels, *Stage5Level1Split.levels)
