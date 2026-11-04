#!/usr/bin/env bash
set -euo pipefail

jar="${1:?usage: $0 <jar>}"
cp "$jar" "$jar.bak"

python3 - "$jar" <<'PY'
import os
import sys
import zipfile

path = sys.argv[1]
tmp = path + '.tmp'
removed = 0
with zipfile.ZipFile(path) as zin, zipfile.ZipFile(tmp, 'w', zipfile.ZIP_DEFLATED) as zout:
    for item in zin.infolist():
        if item.filename == 'native-compressor-installer.so':
            removed += 1
            continue
        zout.writestr(item, zin.read(item.filename))
os.replace(tmp, path)
print(f'removed {removed} file(s); backup: {path}.bak')
PY
