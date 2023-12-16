#!/bin/sh
docker buildx build . --platform linux/arm64 -t pvanassen.nl/led/led-controller:latest --pull --push --progress=plain