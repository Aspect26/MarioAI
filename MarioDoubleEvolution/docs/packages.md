# Package cz.cuni.mff.aspect.coevolution
Contains implementation of coevolution of AI and level generators.


# Package cz.cuni.mff.aspect.controllers
Contains implementation of multiple AI Controllers


# Package cz.cuni.mff.aspect.evolution
Contains implementation of all evolutions. These evolutions include AI evolution using NeuroEvolution and NEAT algorithms, level generator evolutions using SGA and level evolution using grammar evolution.


# Package cz.cuni.mff.aspect.launch
Contains multiple entry point of the project.

## Coevolution
To launch coevolution use [CoEvolve.kt] or [CoEvolveMulti.kt]

## Evolution
To launch AI evolution, use one of these:
 * [EvolveAINeuro.kt] - launches evolution of AI using simple NeuroEvolution
 * [EvolveAINEAT.kt] - launches evolution of AI using NEAT algorithm
 * [EvolveAIMany.kt] - launches multiple evolutions of AI
 
To launch level generator evolution, use one of these:
 * [EvolveLGPMP.kt] - launches evolution of a Probabilistic Multipass level generator
 * [EvolveLGPC.kt] - launches evolution of a Probabilistic Chunks level generator
 * [EvolveLGMany.kt] - launches multiple evolutions of level generators
 
## Playing mario
To play Super Mario, use on of these:
 * [PlayMarioAI.kt] - launches Super Mario controlled by an AI agent
 * [PlayMarioKeyboard.kt] - launches Super Mario controlled by user input


# Package cz.cuni.mff.aspect.mario
Contains implementation of usage of Super Mario simulator from [here](https://code.google.com/archive/p/marioai/).
 
 
# Package cz.cuni.mff.aspect.storage
Contains implementation of multiple storages used in the project.


# Package cz.cuni.mff.aspect.utils
Contains utility classes and extensions.


# Package cz.cuni.mff.aspect.visualisation
Contains multiple visualisation used in the project. These visualisations are evolution charts and Super Mario level visualisation.


# Package cz.cuni.mff.aspect.visualisation.charts
Contains implementations of chart visualisations of evolution and coevolution.


# Package cz.cuni.mff.aspect.visualisation.charts.linechart
Contains implementation of line chart atop `xcharts` library


# Package cz.cuni.mff.aspect.visualisation.charts.xcharts
Contains reimplementations of some of the `xcharts` library classes. 


# Package cz.cuni.mff.aspect.visualisation.level
Contains implementations of level visualisers.
