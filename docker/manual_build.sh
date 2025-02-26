#!/usr/bin/env bash

set -euo pipefail

main() {
  ./gradlew bootJar
  cp build/libs/*.jar docker/app.jar

  docker build docker --platform=linux/amd64 -t test-amd64:test
  docker build docker -t test-arm:test

  docker inspect --format '{{.Architecture}}' test-amd64:test
  docker inspect --format '{{.Architecture}}' test-arm:test
}

main