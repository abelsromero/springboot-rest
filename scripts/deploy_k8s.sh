#!/usr/bin/env bash

set -euo pipefail

release_tag="$(date +"%Y%m%d-%H%M%S")"
#readonly image_name="springapp:$release_tag"
readonly image_name="us-west2-docker.pkg.dev/shepherd-v2-environment-1/sh2-mail-3811775-oci-registry/springapp:20250221-150725"
readonly release_tag

build() {
  ./gradlew bootBuildImage --imageName="$image_name"
}

kind_load() {
  kind load docker-image "$image_name"
}

deploy_app() {
  local app_name="${1:?must be set}"
  local replicas="${2:?must be set}"

  kubectl delete deploy "$app_name" || true
  kubectl delete svc "$app_name" || true

  kubectl create deployment "$app_name" --image "$image_name" --replicas="$replicas"
  kubectl expose deployment "$app_name" --port=8080 --target-port=8080
}

main() {
#  build
#  kind_load
  deploy_app "backend" 4
  deploy_app "consumer" 1
}

main