
.PHONY: clean
clean:
	./gradlew clean

.PHONY: test
test:
	./gradlew build test --no-build-cache --rerun-tasks

.PHONY: lint
lint:
	./gradlew ktlintCheck

.PHONY: format
format:
	./gradlew ktlintFormat
