# Testing

The reconstruction was tested against a local Spigot 1.8.8 backend. Clients used
protocol 47 (Minecraft 1.8.9). The proxy ran on `127.0.0.1:25577` and the backend
on `127.0.0.1:25565`.

Test clients:

- `test/bot.py`: a minimal Python bot for login, chat, movement, timing tests,
  and multi-bot scenarios.
- `test/mineflayer/advanced-bot.js`: a full Mineflayer client.

## Coverage

| Feature | Test | Result |
| --- | --- | --- |
| Startup and console | XCord starts without a license check or native installer | Pass |
| Ping, MOTD, protocol | 1.8.9 status ping | Pass |
| Proxy login to Spigot | Python bot and real client | Pass |
| Chat | Bot to player | Pass |
| Multi-bot and `/list` | Three bots plus a player | Pass |
| Movement | `pos` and `look` | Pass |
| Permissions and groups | `WinToWin` and `TestBot` as admins | Pass |
| Bungee commands | `/list`, `/server`, `/bungee`, `/ip`, `/perms` | Pass |
| XCord commands | `/xcordreload`, `/clearblacklist`, `/xvpn`, `/timedelayedevents` | Pass |
| `/testmemory` | Profiler enable/disable and `Memory Stats` logs | Pass |
| `/greload` | Bungee reload | Pass |
| `/alert` | `cmd_alert` module not installed | Not available |
| VerifyV2 | Real client | Pass |
| VerifyV2 | Minimal bot | Detected: timeout |
| VerifyV2 | Mineflayer | Detected: advanced-verify ID 4 |
| Anti-bot activation | Five connections with log filter on/off | Pass |
| Speedy-login | Two bots from one IP; second kicked | Pass |
| Strange-name | `TeSt1BoT2` (5/1 pattern) closed | Pass |
| Name-pattern | 87.5 percent similarity closed | Pass |
| Anti-hang | Slow bot with `--delay-login 8` cut at 3 s | Pass |
| Anti-hang | Fast Mineflayer client | Pass |
| Linux socket check | LAN IP `192.0.2.25` detected as proxy | Pass |
| Anti-DDoS login | One-packet limit produced `sent over 2+ packets` | Pass |
| Anti-DDoS gameplay | Two-packet limit produced `sent over 3+ packets` | Pass |
| DoS IP filter | IP in `dosblacklist.txt` rejected | Pass |
| ExploitMonitor | 3012-byte plugin message terminated | Pass |
| Global blacklist | Local IP `127.0.0.5` rejected | Pass |
| IPSet restore format | Simulated `create/add` output | Pass |
| Country blocker | Simulated CN list, 8815 ranges restored | Pass |
| Anti-VPN whitelist | Add, list, remove | Pass |
| Anti-VPN detection | BlackBox and datacenter IP through PROXY protocol | Pass: `Y`, kicked |
| IP spoofing | `SpoofBot` reported `203.0.113.25` | Pass |
| Native compressor | Deflate and inflate | Pass |
| Native cipher | mbedTLS loads | Pass |
| Second-machine bot | VPS bot through an SSH tunnel | Pass |
| Direct VPS connection | Blocked by local firewall (ufw) | Not tested |

## Evidence

Addresses in this section are documentation placeholders. The live test used a
public VPS address that is not published here.

- VerifyV2 with a real client:
  `[WinToWin|...] <-> ServerConnector [lobby] has connected`.
- VerifyV2 bot detection:
  `Disconnected AdvBot, failed advanced-verify bot-check ID:4`.
- Minimal bot detection: timeout after
  `XCORD >> Verifying your Minecraft client...`.
- Speedy-login:
  `You're connecting too quickly! Slow down or you'll be temporarily blacklisted`.
- Anti-hang: `ReadTimeoutException` / `read timed out` after three seconds.
- Linux socket check: `X 192.0.2.25 for Socket-Check: Proxy`.
- Anti-DDoS:
  `Anti-DDOS: ... sent over 2+ packets in a second (Terminated)`.
- ExploitMonitor:
  `just sent a packet 3012 bytes long. Terminated the connection!`.
- Country blocker:
  `XCord blocked 8815 IP ranges from your specified ISO codes!`.
- IP spoof:
  `XCord spoofed localhost with 203.0.113.25 for SpoofBot`.
- Anti-VPN:
  `Blackbox query for 203.0.113.10 = Y` followed by
  `Detected proxy: IP=203.0.113.10`, then kick with
  `VPN/Proxy's are not allowed on this server`.
- VPS bot: `[VpsBot] <-> ServerConnector [lobby] has connected` over an SSH
  tunnel.

## Limitations

- Real kernel IPSet/iptables blocking was not tested because root and ipset were
  unavailable. The `ipset restore` format and the `sudo` flow were simulated.
- Anti-VPN detection was tested end to end with BlackBox. IPHub and ProxyCheck
  need their own keys and were not exercised.
- The local firewall blocked port 25577 from Tailscale. Direct VPS connections
  require opening the port or using a PROXY-protocol front end.
- Bungee modules `cmd_alert`, `cmd_send`, and `cmd_find` were not installed.
  `/alert` was unavailable. `/list` and `/server` were added as native commands.
- VerifyV2 rejects the full Mineflayer bot as well as the minimal bot. A real
  client passes. This is expected anti-bot behavior.
- `timeout-flood` was not tested because it requires play-state read timeouts.
  The related anti-hang path was tested.

## Local setup

```text
Proxy:   localhost:25577
Backend: 127.0.0.1:25565
Bots:    test/bot.py, test/mineflayer/advanced-bot.js
Logs:    logs/xcord.log, logs/spigot.log
```

The repository does not include Spigot, Java runtimes, worlds, or bot
dependencies. Install Mineflayer in `test/mineflayer` with `npm install` before
running the JavaScript client.
