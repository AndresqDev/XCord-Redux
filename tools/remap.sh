#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."
ROOT="$PWD"
INPUT="${1:?usage: tools/remap.sh <input.jar> <output.jar> [mapping.txt]}"
OUTPUT="${2:?usage: tools/remap.sh <input.jar> <output.jar> [mapping.txt]}"
MAPPING="${3:-$ROOT/mapping.txt}"

java -cp "$ROOT/decomp/asm/asm-9.7.1.jar:$ROOT/decomp/asm/asm-commons-9.7.1.jar:$ROOT/decomp/remap/classes" \
  RemapJar "$INPUT" "$OUTPUT" "$MAPPING"
