# External Services and License Dependencies

Status: the current `build/Xcord.jar` does not perform the Java license request
and does not download blacklists. It uses local `blacklistv2.txt` and
`dosblacklist.txt` files.

Basis: the reconstructed `src/main/java` tree and the generated `xcord.yml`.

## Summary

- The original license system was used for the Java license check, the global
  blacklist, and the DoS-filter blacklist.
- The rest of the anti-bot and anti-DDoS stack does not require an XCord
  license: VerifyV2, exploit monitoring, name/ping/packet checks, and country
  blocking all worked independently.
- With the original defaults (`shared-global-blacklist.enabled=true` and
  `anti-ddos.dos-ip-filter.enabled=true`), the proxy attempted to download both
  blacklists at startup and every hour.
- Blacklist data was not executed as code, but in IPSet mode the file was passed
  directly to `ipset restore`.
- Downloaded code can be executed from PaperMC modules (`modules/*.jar`) and
  Maven plugin libraries (`libraries/*.jar`). They do not depend on the XCord
  license.

## External endpoints

| # | Endpoint | Protocol | Auth | Trigger | Result | License required |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | `http://xcord.minebuilders.org:2052/license` | HTTP | `license-key` header | startup | text (`0`/`1`/other) | Yes; logs only |
| 2 | `http://xcord.minebuilders.org:2052/blacklistv2.txt` | HTTP | `license-key` header | `shared-global-blacklist.enabled`, every 3600 s | IP/CIDR list | Yes (401 without a key) |
| 3 | `http://xcord.minebuilders.org:2052/dosblacklist.txt` | HTTP | `license-key` header | `anti-ddos.dos-ip-filter.enabled`, every 3600 s | IP/CIDR list | Yes (401 without a key) |
| 4 | `http://<ipdeny.com>/ipblocks/data/countries/<ISO>.zone` | HTTP | none | `kernel-level-ipset-country-blocker.enabled` (default false) | CIDR ranges | No |
| 5 | `https://blackbox.ipinfo.app/lookup/<IP>` | HTTPS | none | anti-VPN + `blackbox-api.enabled` | `Y`/`N` | No (third party) |
| 6 | `http://v2.api.iphub.info/ip/<IP>` | HTTP | `X-Key` header | anti-VPN + `iphub-api.enabled` | JSON | No (IPHub key) |
| 7 | `http://proxycheck.io/v2/<IP>?key=...` | HTTP | key in URL | anti-VPN + `proxycheckerio-api.enabled` | JSON | No (ProxyCheck key) |
| 8 | `https://papermc.io/api/v2/projects/travertine/versions/<v>/builds/<b>/downloads/<module>.jar` | HTTPS | none | module version differs from proxy | JAR | No |
| 9 | `https://repo.maven.apache.org/maven2` | HTTPS | none | plugin declares `libraries` | JAR | No |
| 10 | `https://sessionserver.mojang.com/session/minecraft/hasJoined?...` | HTTPS | none | online-mode login | profile JSON | No |
| 11 | `8.8.8.8:53` (TCP DNS) | TCP | none | `DnsResolver` fallback | DNS response | No |
| 12 | `8.8.8.8:10002` (UDP connect) | UDP | none | startup local-IP detection | none | No |
| 13 | apt/yum repositories | HTTP(S) | root/sudo | missing ipset | `ipset` package | No |

Notes:

- URLs 1-3 were built by resolving the domain to an IP and using the IP in the
  URL (`http://<IP>:2052/...`).
- URLs 2-3 had no size limit or checksum. The response was written to a `.tmp`
  file and passed to the backend.
- `spigotmc.org` and `papermc.io/ci/...` only appeared in help messages.

## Executed versus parsed

Executed as code:

- PaperMC modules (`cmd_alert`, `cmd_find`, `cmd_list`, `cmd_send`,
  `cmd_server`, `reconnect_yaml`) downloaded as JARs and loaded by BungeeCord.
- Maven libraries declared by plugins and loaded through a `URLClassLoader`.
- Local plugins in `plugins/` (not downloaded by XCord, but executed).
- Native libraries in the jar (`native-cipher`, `native-compress`).
- `ipset` packages installed through apt or yum.

Parsed only:

- License response text.
- `blacklistv2.txt` and `dosblacklist.txt` (text to ipset or TIntSet).
- ipdeny country zones (text to ipset).
- IPHub, ProxyCheck, and BlackBox responses (JSON/text used for a kick decision).
- DNS responses.

## License-gated features

| Feature | Configuration | Default | License required | Without a license |
| --- | --- | --- | --- | --- |
| Java check | always | on | Yes; sends key | Logs trial/crack, no feature block |
| Global blacklist | `shared-global-blacklist.enabled` | true | Yes (401) | List not loaded |
| DoS blacklist | `anti-ddos.dos-ip-filter.enabled` | true | Yes (401) | List not loaded |
| Country blocking | `kernel-level-ipset-country-blocker.enabled` | false | No | Works through ipdeny |
| Anti-VPN BlackBox | `login.anti-vpn.enabled` + `blackbox-api.enabled` | false + true | No | Works without an XCord key |
| Anti-VPN IPHub | `...iphub-api.enabled` + key | false | No | Needs an IPHub key |
| Anti-VPN ProxyCheck | `...proxycheckerio-api.enabled` + key | false | No | Needs a ProxyCheck key |
| VerifyV2 / anti-bot | `anti-bot.*` | varies | No | Fully functional |
| Anti-DDoS / anti-exploit | `anti-ddos.*`, `exploit-monitor.*` | varies | No | Fully functional |
| PaperMC modules | `modules.yml` | on | No | Downloads and runs JARs |
| Maven libraries | plugin `plugin.yml` | - | No | Downloads and runs JARs |

Removing the XCord license only removes the two shared blacklists. The rest of
the anti-bot and anti-DDoS stack remains functional. Blacklists can be supplied
from local files or another server.

## Original startup behavior without a license

With a freshly generated `xcord.yml`:

1. `XCordConfig` ran `sudo ipset` and, if missing, attempted
   `sudo apt-get install ipset -y` or `sudo yum install ipset -y`.
2. `XCord` called `xcord.minebuilders.org:2052/license` with
   `license-key: Get_License_From_Discord`.
3. `GlobalBlacklist.reload()` created the IPSet table and iptables rules, then
   attempted to download `blacklistv2.txt` and received 401.
4. `DosFilterBlacklist.reload()` did the same for `xdosfilter` and
   `dosblacklist.txt`.
5. `ModuleManager` attempted to download PaperMC modules.
6. If `login.anti-vpn.enabled=true`, the configured providers were queried.
7. If country blocking was enabled, ipdeny zones were downloaded.

## Current implementation

The build removes the Java license request and the remote blacklist paths. It
uses local file loaders and templates. The original native installer is excluded
from the build and its loader classes are not used.

The local files are documented in the main README. No official blacklist is
downloaded. If you have a licensed copy, copy its files into the working
directory; otherwise add entries manually.
