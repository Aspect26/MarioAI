MARIO_SIMULATOR_PROJECT = "MarioAI4J"
MARIO_COEVOLUTION_PROJECT = "MarioDoubleEvolution"

get-gradle-wrapper:
	gradle wrapper --gradle-version 5.5.1

build-jar: get-gradle-wrapper
	./gradlew $(MARIO_SIMULATOR_PROJECT):jar
	./gradlew $(MARIO_COEVOLUTION_PROJECT):jar

test: get-gradle-wrapper
	./gradlew $(MARIO_COEVOLUTION_PROJECT):test

run-coev: build-jar
	# TODO: make custom gradle command to build coevolution jar
	java -jar ./MarioDoubleEvolution/build/libs/MarioDoubleEvolution.jar 

simulator-changes:
	git difftool 5ab3f ./MarioAI4J/
