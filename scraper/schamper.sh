#!/usr/bin/env bash

python schamper.py
rsync -a schamper ~/public/api/1.0
