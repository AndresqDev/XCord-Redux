# Tools

| Script | Purpose |
| --- | --- |
| `remap.sh` | Applies `mapping.txt` to a jar with the ASM remapper. |
| `make-libs.sh` | Produces `Xcord-libs.jar` without application classes or the native installer. |
| `decompile.sh` | Runs Vineflower and CFR on a jar. |
| `gen_config.py` | Generates `map-config.txt` from `reference/y.code.txt`. |
| `gen_map.py` | Generates `mapping.txt` from `reference/members.txt`. |
| `disable-native-installer.sh` | Removes `native-compressor-installer.so` from an existing jar. |

Run the scripts from the repository root. See [decomp/README.md](../decomp/README.md)
for the full reconstruction workflow.
