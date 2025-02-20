#!/usr/bin/env bash

set -euo pipefail

readonly image_name="springapp:$(date +"%Y%m%d-%H%M%S")"

kubectl delete deploy springapp
kubectl delete svc springapp

./gradlew bootBuildImage --imageName="$image_name"
kind load docker-image "$image_name"
kubectl create deploy springapp --image "$image_name" --replicas=3
kubectl expose deployment springapp --port=8080 --target-port=8080