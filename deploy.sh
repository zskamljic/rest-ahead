set -e
./mvnw clean test
./mvnw -pl '!:test-report-aggregator,!:demo' deploy -DskipTests