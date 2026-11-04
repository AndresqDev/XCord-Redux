# Decompilation Tools

This directory contains the tools used to reconstruct the jar. The source in
`decomp/remap/RemapJar.java` is kept comment-free; usage is documented here.

## Layout

```text
decomp/
  vineflower/vineflower.jar
  cfr/cfr.jar
  asm/asm-9.7.1.jar
  asm/asm-commons-9.7.1.jar
  remap/RemapJar.java
  remap/classes/RemapJar*.class
```

## Full workflow

Run these commands from the repository root. The original jar is not part of
the repository. Place it at `input/Xcord.jar`.

### 1. Remap the original jar

```bash
tools/remap.sh input/Xcord.jar work/Xcord-renamed.jar
```

The script invokes `RemapJar` with `mapping.txt`. The remapper resolves field
and method references through the class hierarchy, then rewrites the class
names, member names, and class entries in the jar.

`Xcord-renamed.jar` is an analysis artifact only and is not used by the build.

### 2. Build the compilation classpath

```bash
tools/make-libs.sh work/Xcord-renamed.jar Xcord-libs.jar
```

The script copies the remapped jar while excluding:

- `xcord/**`
- `me/minebuilders/proxyfork/**`
- `net/md_5/bungee/BungeeCord.class` and its inner classes
- `net/md_5/bungee/compress/CompressFactory.class`
- `native-compressor-installer.so`
- the native compressor preparation classes

The repository ships a known-good `Xcord-libs.jar`. Regenerate it only after
changing the mapping or the input jar.

### 3. Decompile for inspection

```bash
tools/decompile.sh input/Xcord.jar work/decompiled
```

Vineflower runs first. CFR is included as a second opinion and is useful for
methods that Vineflower cannot decompile.

### 4. Build

```bash
./build.sh
```

The build refuses to run when `Xcord-libs.jar` still contains the native
installer.

## RemapJar mapping format

`mapping.txt` uses whitespace-separated records:

```text
C <old/internal/Name> <new/internal/Name>
F <owner/internal/Name> <fieldName> <descriptor> <newFieldName>
M <owner/internal/Name> <methodName> <descriptor> <newMethodName>
```

Blank lines and lines starting with `#` are ignored by the remapper.

## Tool versions

- Vineflower 1.10.1
- CFR 0.152
- ASM 9.7.1

The original jar and generated `work/` files are intentionally not committed.
