#!/bin/sh
DOCKER_BUILDKIT=1 docker build . --build-arg arch=arm64 -t led-controller