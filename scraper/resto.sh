#!/usr/bin/env bash

set -euo pipefail


# Update symlink
rm -f resto/1.0/week
ln -s "menu/$(date +%Y)" resto/1.0/week

# Run scraper
python3 resto.py
python3 sandwiches.py
python3 cafetaria.py
rsync -a resto/1.0/ ~/public/api/1.0/resto/
rsync -a resto/2.0/ ~/public/api/2.0/resto/
