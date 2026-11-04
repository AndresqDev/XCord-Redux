#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."
ROOT="$PWD"
INPUT="${1:-$ROOT/work/Xcord-renamed.jar}"
OUTPUT="${2:-$ROOT/Xcord-libs.jar}"

python3 - "$INPUT" "$OUTPUT" <<'PY'
import sys
import zipfile

src, dst = sys.argv[1], sys.argv[2]
exclude_prefixes = (
    'xcord/',
    'me/minebuilders/proxyfork/',
    'native-compressor-installer.so',
    'me/minebuilders/natives/compression/LibNativePrepare.class',
    'net/md_5/bungee/jni/prepare/LibDeflateCompressorPreparer.class',
    'net/md_5/bungee/compress/CompressFactory.class',
)
exclude_exact = {'net/md_5/bungee/BungeeCord.class'}

with zipfile.ZipFile(src) as zin, zipfile.ZipFile(dst, 'w', zipfile.ZIP_DEFLATED) as zout:
    count = 0
    for item in zin.infolist():
        name = item.filename
        if name.startswith(exclude_prefixes) or name in exclude_exact:
            continue
        if name.startswith('net/md_5/bungee/BungeeCord$'):
            continue
        zout.writestr(item, zin.read(name))
        count += 1

print(f'{dst}: {count} entries')
PY

if jar tf "$OUTPUT" | grep -q 'native-compressor-installer'; then
  echo "ERROR: native-compressor-installer is still present" >&2
  exit 1
fi
