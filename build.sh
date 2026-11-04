#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"
ROOT="$PWD"
OUT="$ROOT/build"
CLASSES="$OUT/classes"
JAR="$OUT/Xcord.jar"

if [[ ! -f "$ROOT/Xcord-libs.jar" ]]; then
  echo "Xcord-libs.jar is missing. Run tools/make-libs.sh first." >&2
  exit 1
fi

if jar tf "$ROOT/Xcord-libs.jar" | grep -q 'native-compressor-installer'; then
  echo "Xcord-libs.jar still contains native-compressor-installer.so. Regenerate it with tools/make-libs.sh." >&2
  exit 1
fi

rm -rf "$OUT"
mkdir -p "$CLASSES"

mapfile -t SOURCES < <(
  find "$ROOT/src/main/java/xcord" "$ROOT/src/main/java/me" -name '*.java'
  for file in \
    BungeeCord.java BungeeCordLauncher.java BungeeServerInfo.java ServerConnector.java UserConnection.java \
    command/CommandList.java command/CommandServer.java command/CommandResetBlacklist.java command/CommandXCordReload.java \
    conf/YamlConfig.java connection/DownstreamBridge.java connection/InitialHandler.java connection/PingHandler.java connection/UpstreamBridge.java \
    compress/CompressFactory.java http/HttpClient.java jni/zlib/XCordNativeZLib.java \
    netty/ChannelWrapper.java netty/HandlerBoss.java netty/PipelineUtils.java \
    protocol/MinecraftDecoder.java protocol/packet/Chat.java protocol/packet/ClientSettings.java protocol/packet/LoginRequest.java
  do
    echo "$ROOT/src/main/java/net/md_5/bungee/$file"
  done
)

echo "Compiling ${#SOURCES[@]} Java files..."
if javac --release 8 -version >/dev/null 2>&1; then
  javac --release 8 -encoding UTF-8 -nowarn -cp "$ROOT/Xcord-libs.jar" -d "$CLASSES" "${SOURCES[@]}"
elif javac -source 8 -target 8 -version >/dev/null 2>&1; then
  javac -source 8 -target 8 -encoding UTF-8 -nowarn -cp "$ROOT/Xcord-libs.jar" -d "$CLASSES" "${SOURCES[@]}"
else
  javac -encoding UTF-8 -nowarn -cp "$ROOT/Xcord-libs.jar" -d "$CLASSES" "${SOURCES[@]}"
fi

echo "Packaging $JAR..."
cp "$ROOT/Xcord-libs.jar" "$JAR"
( cd "$CLASSES" && jar uf "$JAR" . )

echo "Done: $JAR"
echo "Run: java -jar $JAR"
