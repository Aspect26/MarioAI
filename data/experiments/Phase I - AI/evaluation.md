## Neuroevolution

### What we started with

Trying to evolve agent using neuroevolution to be able to solve some of the original Super Mario Bros levels. These levels are: Stage 1 Level 1, Stage 2 Level 1, Stage 4 Level 1 and Stage 5 Level 1. We already had some neuroevolution configuration, which was able to evolve overfited agents on single level to solve them. Then we decided to split these levels into multiple parts (for better training). A difficult artificial level with some holes was added so that mario would learn not to simply jump all the times.  

The purpose of these experiments was to make the evolution evolve better agents and in lesser number of generations. 


#### General info to experiments
Fitness used in tests was `distanceOnly` if not stated otherwis

Objective function used in all tests was `number of finished levels * 1000` (to better fit in the charts (so 4000 objective means 4 levels finished by given individual))

First experiments use hidden layer size 20; except experiments testing hidden layer size, after that, the size used was 5

No crossover function was used in the experiments
Survivors selector in all experiments was `EliteSelector(2)`
Offsprings selector in all experiments was `TournamentSelector(2)`
Receptive field size in all experiments was `(5, 5)`
Receptive field mario offset in all experiments was `(0, 2)`
Population size used in all experiments except the one testing population size was `50`


#### Experiment 1 (Mutation probability)
We tried evolve an agent using different mutation probabilities. The experiments are in folder `Mutation probability S1S`. Levels used `*Stage1Level1Split.levels + PathWithHolesLevel + OnlyPathLevel`. The experiments seem to indicate that the probability doesn't affect the results much (weird).

#### Experiments 2 (Gaussian mutator)
Gaussian mutator was found to give better results than classic random mutator (`All test, All test - classic mutator`). Here we did multiple experiments to find out, which mutation probability fits the best. The tests are in folders `Gaussian test evaluation - S2S, Gaussian test evaluation - S4S, Gaussian test evaluation - S4S (2), Gaussian test evaluation - S4S (3), Gaussian test evaluation - S4S (4), Gaussian test evaluation - S4S (5), Gaussian test evaluation - S5S`. SxS in the name of the test indicates, that levels used for the evaluation were `Stage x Level 1 (splitted) + PathWithHoles + PathOnly). The experiments seem to show that the highest tested probability - *0.45* seems to give the best results (but not that much different from others). The experiments also show that 50 generations for learning should be enough.

#### Experiments 3 (Population size)
We tried increasing the population size, to find out if it will have some impact on the results to 100 (test `Gaussian test evaluation - S4S - Population 100`). But it didn't seem to help.

#### Experiments 4 (Hidden layer size)
We tried adjusting hidden layer size, to see if bigger hidden layer yields better results. Tests are `Hidden layer test evaluation - S4S, Hidden layer test evaluation - S4S (2), Hidden layer test evaluation - S4S (3), Hidden layer test evaluation - S4S (4)`. The tests doesn't seem to indicate that bigger hidden layer yields consistently better results, so we stick with hidden layer size 5. From now on, we use only this size to all later tests.

#### Experiments 5 (Multi gaussian)
Instead of using one gaussian we tried multiple of them to simulate softer updates in later stages of learning. The parameters used were (probability, std): `[(0.25, 0.1), (0.15, 0.2), (0.05, 0.4), (0.01, 0.6)]`. Didn't seem to yield consistently better results too. (Also tried fitness with victories)

#### Experiments fitness
Using fitness `distanceOnly` instead of `distanceLeastActions (20pts penalty)` was also able to evolve agents which were not just jumping all the time, so using `distanceLeastActions` was discouraged.

#### Experiments 6 (Doubled input)
In these experiments, instead of learning only on S4S we tried to use whole training data set (4 original mario levels + path with holes level + path only level). We ran the evolution with configuration found from previous experiments (experiment `All test`), also comparing again with classic mutator (experiment `All test - classic mutator`), and multi gaussian again (`All test- multi gaussian`), and then using denser input (what mario sees wasn't only tiles from grid, but we made the grid denser (1x1 tile was now 2x2)) (test `Doubled input - all`), and using randomized fitness (playing random 15 levels from the dataset) (test `Doubled input - all - randomized levels`). Doubled input seems to yield slightly better results, so we use it. We also ran `Double input - S4S)` (doubled input on stage 4 leve 1 split + artificial levels).

#### Experiments 7 (Increasing levels)
We also tried simulate learning firstly simple things then more difficult ones, havin diferent levels in different generations:
`
_phenotype.generation < 10 -> arrayOf(PathWithHolesLevel)
_phenotype.generation < 20 -> arrayOf<MarioLevel>(PathWithHolesLevel) + Stage1Level1Split.levels
_phenotype.generation < 30 -> arrayOf<MarioLevel>(PathWithHolesLevel) + Stage1Level1Split.levels + Stage2Level1Split.levels
_phenotype.generation < 40 -> arrayOf<MarioLevel>(PathWithHolesLevel) + Stage1Level1Split.levels + Stage2Level1Split.levels  + Stage4Level1Split.levels
else -> arrayOf<MarioLevel>(PathWithHolesLevel) + Stage1Level1Split.levels + Stage2Level1Split.levels + Stage4Level1Split.levels + Stage5Level1Split.levels
`
tests are `Increasing levels, Increasing levels - fitness with victories, Increasing levels - increase hidden layer (20 maybe?)`
Doesn't seem to help much

#### Special experiment
Instead of zero as space use 1 in neural network - `Non zero space - S4S` -> no improvement