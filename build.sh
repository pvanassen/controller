#!/bin/sh
docker buildx build . --platform linux/amd64,linux/arm64 -t pvanassen.nl/led/led-controller:latest --push --progress=plain