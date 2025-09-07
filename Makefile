.DEFAULT_GOAL := build-run

clean:
	./gradlew clean

build:
	./gradlew clean build

start:
	./gradlew bootRun

start-prod:
	./gradlew bootRun --args='--spring.profiles.active=prod'

installDist:
	./gradlew installDist

start-dist:
	./build/install/app/bin/app

lint:
	./gradlew checkstyleMain checkstyleTest

test:
	./gradlew test

reload-classes:
	./gradlew -t classes

report:
	./gradlew jacocoTestReport

check-java-deps:
	./gradlew dependencyUpdates -Drevision=release

.PHONY: build
