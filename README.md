# XCord Redux

**R**econstructed, **E**xtracted, **D**ecompiled, **U**pdated, e**X**amined.

(The acronym is not exact, but I liked how it fit together. d:)

XCord Redux is a readable Java reconstruction of the XCord proxy jar, a
Travertine/BungeeCord 1.19 fork. The obfuscated application classes were
remapped, decompiled, reviewed, and rebuilt from source.

The current working tree is the `unpolished-production-code` branch. It is the
known-good reconstruction used for testing. A later `main` branch will contain
the final naming and cleanup pass.

## Status

- Builds with JDK 8 or newer.
- Produces `build/Xcord.jar`.
- Starts a BungeeCord-compatible proxy and listens on port `25577` by default.
- Excludes the original native installer and remote license checks, and
  uses local blacklist files.
- Keeps the XCord anti-bot, anti-DDoS, anti-VPN, country blocking, verification,
  and network hardening features.

## Repository layout

| Path | Purpose |
| --- | --- |
| `src/main/java/` | 7,053 decompiled Java sources: XCord, BungeeCord, and shaded libraries. |
| `src/main/resources/` | Resources from the jar, including icons, translations, and native libraries. |
| `Xcord-libs.jar` | Compilation classpath: the remapped jar without the application classes being rebuilt. |
| `build.sh` | Compiles the application classes and packages `build/Xcord.jar`. |
| `decomp/` | Decompilation and remapping tools. |
| `tools/` | Reproducible remap, libs, decompile, and utility scripts. |
| `mapping.txt` | Class, field, and method remapping used for the reconstruction. |
| `map-config.txt` | `XCordConfig` field mapping derived from `xcord.yml` keys. |
| `reference/` | `javap` output used to generate the mapping. |
| `docs/` | External-services inventory, class map, anti-VPN notes, and testing notes. |
| `licenses/` | Third-party license and notice files. |

## Documentation

- [External services and license dependencies](docs/EXTERNAL-SERVICES.md)
- [Class map](docs/CLASS-MAP.md)
- [Anti-VPN setup](docs/ANTIVPN.md)
- [Testing notes](docs/TESTING.md)
- [Provenance and licensing](docs/PROVENANCE.md)

## Build

Requirements: JDK 8+ with `javac` and `jar` on `PATH`.

```bash
./build.sh
java -jar build/Xcord.jar
```

The build compiles only the reconstructed application classes and the modified
BungeeCord classes. Everything else comes from `Xcord-libs.jar`.

## Reconstruction workflow

The original jar is not committed. Put a copy at `input/Xcord.jar` and run the
steps from the repository root. The `work/` and `input/` directories are
ignored by Git.

### 1. Remap the original jar

```bash
tools/remap.sh input/Xcord.jar work/Xcord-renamed.jar
```

This uses the ASM-based `RemapJar` utility and `mapping.txt`. It rewrites
classes, fields, and methods, including references inherited through the class
hierarchy.

### 2. Rebuild the compilation classpath

```bash
tools/make-libs.sh work/Xcord-renamed.jar Xcord-libs.jar
```

This removes `xcord/**`, `me/minebuilders/proxyfork/**`, the modified BungeeCord
classes, and `native-compressor-installer.so` from the remapped jar. The result
is the classpath used by `build.sh`. The repository already includes a known
good `Xcord-libs.jar`; regenerate it when the mapping or the original jar
changes.

### 3. Decompile for inspection

```bash
tools/decompile.sh input/Xcord.jar work/decompiled
```

The script runs Vineflower 1.10.1 and CFR 0.152 without comments. Vineflower is
the primary decompiler used for the reconstruction; CFR is kept for comparison
and for recovering individual methods when Vineflower fails.

### 4. Build

```bash
./build.sh
```

The script refuses to build if `Xcord-libs.jar` contains
`native-compressor-installer.so`.

## Blacklists

The official XCord servers do not serve `blacklistv2.txt` or `dosblacklist.txt`
to unlicensed installations. This reconstruction does not attempt to download
them. Both lists are local files in the working directory and must be filled
manually:

- `blacklistv2.txt` for `shared-global-blacklist`.
- `dosblacklist.txt` for `anti-ddos.dos-ip-filter`.

Format: one IPv4 address or CIDR range per line. Lines starting with `#` or
`//` are ignored. If a file does not exist, XCord creates a template.

If you have a licensed XCord copy, you can copy its blacklist files into the
working directory. Otherwise, add the addresses or ranges you need by hand.

IPSet mode supports CIDR ranges. Memory mode (`use-ipset: false`) supports plain
IPv4 addresses only. Lists are reloaded at the configured
`update-database-interval-seconds` value (3600 seconds by default), or
immediately with `/xcordreload`.

## Changes from the original jar

- Removed `native-compressor-installer.so` and all code that loaded it.
- Removed the Java license check against `xcord.minebuilders.org`.
- Removed the `license-key` configuration key and its use.
- Replaced remote blacklist downloads with local files and templates.
- Removed automatic `ipset` installation through `apt-get` or `yum`.
- Restored the original kick messages; only the removed license-status message
  was replaced.
- Recompiled the modified BungeeCord and compression classes so the build uses
  the reconstructed sources instead of the old bytecode in the library jar.

If you reuse an old `xcord.yml` or `xcord-messages.yml`, review it before
starting the proxy. YAML files keep old keys and messages until they are edited.

## Branches and releases

- `unpolished-production-code` is the first working branch. It is intentionally
  close to the decompiled output and is the branch used for functional testing.
- `main` will be created for the final polish pass: clearer variable names,
  smaller methods, and general cleanup. That work is deliberately deferred.
- Release tags such as `v1.0.0` build the jar and publish it as a GitHub
  release through `.github/workflows/release.yml`.

## Limitations

- This is reconstructed source, not the original source tree. Some generated
  constructs may differ semantically in small details.
- Serialized files such as `IPData.db` and `antivpn-whitelist.db` store renamed
  class names. Old files may not deserialize; use a fresh directory or migrate
  them with the original jar.
- The full shaded-library tree is included for reference, but only the XCord
  application classes and the modified BungeeCord classes are compiled by
  `build.sh`.
- Native libraries such as `native-compress-xcord.so` and `native-cipher.so`
  remain in the resources. Their declarations are under
  `me.minebuilders.natives`.
- The reconstruction does not grant rights to the original XCord code. See
  [LICENSE](LICENSE) and [docs/PROVENANCE.md](docs/PROVENANCE.md).

## License and provenance

This repository contains a reconstruction of a third-party jar. The original
`LICENSE` file is preserved and states the rights of the original author.
Third-party components retain their own licenses; see
[licenses/](licenses/) and [docs/PROVENANCE.md](docs/PROVENANCE.md).

This project is not affiliated with Mojang, SpigotMC, PaperMC, BungeeCord,
Travertine, Waterfall, or the original XCord author.
