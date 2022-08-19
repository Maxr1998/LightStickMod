#!/bin/sh
docker run --rm -it -v $PWD:/config --network host esphome/esphome:latest run firmware.yaml
