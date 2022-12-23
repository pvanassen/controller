#!/bin/sh
DOCKER_BUILDKIT=1 docker build . --build-arg arch=amd64 -t led-controller