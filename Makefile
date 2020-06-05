-include .env.make
export

MARIO_SIMULATOR_PROJECT = "MarioAI4J"
MARIO_COEVOLUTION_PROJECT = "MarioDoubleEvolution"

GRADLE_VERSION = "6.5"

get-gradle-wrapper:
	gradle wrapper --gradle-version $(GRADLE_VERSION)

build-simulator: get-gradle-wrapper
	./gradlew $(MARIO_SIMULATOR_PROJECT):jar

jar: build-simulator
	./gradlew $(MARIO_COEVOLUTION_PROJECT):jar

test: get-gradle-wrapper
	./gradlew $(MARIO_COEVOLUTION_PROJECT):test

run-coev: build-simulator
	./gradlew $(MARIO_COEVOLUTION_PROJECT):runCoevolution

run-experiment: build-simulator
	./gradlew $(MARIO_COEVOLUTION_PROJECT):runExperiment

documentation:
	./gradlew $(MARIO_COEVOLUTION_PROJECT):dokka

changes-simulator:
	git difftool 5ab3f ./MarioAI4J/

changes-neat:
	git difftool c69ab ./MarioDoubleEvolution/src/main/java/com/evo/NEAT

