#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."
ROOT="$PWD"
INPUT="${1:?usage: tools/decompile.sh <input.jar> [output-dir]}"
OUTPUT="${2:-$ROOT/work/decompiled}"

mkdir -p "$OUTPUT/vineflower" "$OUTPUT/cfr"

java -jar "$ROOT/decomp/vineflower/vineflower.jar" \
  -dgs=1 -hdc=0 -asc=1 -rsy=1 -jvn=1 \
  "$INPUT" "$OUTPUT/vineflower"

java -jar "$ROOT/decomp/cfr/cfr.jar" \
  "$INPUT" \
  --outputdir "$OUTPUT/cfr" \
  --silent true \
  --comments false

echo "Vineflower: $OUTPUT/vineflower"
echo "CFR:        $OUTPUT/cfr"
