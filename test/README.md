# Test Clients

These clients were used to validate the reconstruction against a local Spigot
1.8.8 backend and an XCord proxy on `127.0.0.1:25577`.

## Minimal Python bot

The bot has no third-party dependencies. It uses the 1.8.x protocol (47), handles
compression, and can send chat, movement, and rotation packets.

```bash
python3 test/bot.py --name TestBot
python3 test/bot.py --name TestBot --chat "hello"
python3 test/bot.py --count 5 --stay 10
```

Interactive commands: `say <text>`, `pos x y z`, `look yaw pitch`, `status`,
`help`, and `quit`.

Use `--bind 127.0.0.2` to test per-IP behavior. `--delay-login` delays the login
packet after the handshake. `--large-plugin` sends an oversized plugin message
for ExploitMonitor tests.

## Mineflayer client

```bash
cd test/mineflayer
npm install
node advanced-bot.js --name AdvBot
node advanced-bot.js --name AdvBot --chat "hello"
node advanced-bot.js --count 2 --stay 20
```

The client uses offline authentication. It is expected to fail VerifyV2 because
it is a bot; a real Minecraft client should pass.

## Status ping

```bash
python3 test/ping.py
```

The script prints the status response for `127.0.0.1:25577` and
`127.0.0.1:25565`.

## PROXY protocol front end

This tool lets a local bot present a chosen source IP to XCord without opening
public ports:

```bash
python3 test/proxy_protocol_front.py
python3 test/bot.py --name VpnBot --port 25578 --bind 127.0.0.1
```

The front end listens on `127.0.0.1:25578`, prepends a PROXY v1 header with
the documentation address `203.0.113.10`, and forwards to `127.0.0.1:25577`.
Replace that constant with a public test address when exercising anti-VPN. Enable
`proxy_protocol: true` on the XCord listener and `login.anti-vpn.enabled` in
`xcord.yml` first.

## Limitations

The repository does not include Spigot, a Java runtime, worlds, or Node
dependencies. The test matrix and evidence are summarized in
[../docs/TESTING.md](../docs/TESTING.md).
