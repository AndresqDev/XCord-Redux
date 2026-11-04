# Anti-VPN

## Requirements

1. Enable the module in `xcord.yml`:

   ```yaml
   login:
     anti-vpn:
       enabled: true
       log-non-cache-detection: true
       blacklist-on-detection: false
       only-blacklist-on-detection-during-attack: false
       initial-connection-checkenabled: true
       initial-connection-checkonly-during-bot-attack: false
       initial-connection-checkcache-only: false
   ```

   The `initial-connection-check*` keys have no hyphen between `check` and
   `enabled`; this is how the original configuration generator writes them.

2. Enable at least one provider.

   BlackBox is the easiest option and does not require a key:

   ```yaml
   blackbox-api:
     enabled: true
   ```

   It queries `https://blackbox.ipinfo.app/lookup/<IP>` and returns `Y` or `N`.

   IPHub requires a free key from `iphub.info`:

   ```yaml
   iphub-api:
     enabled: true
     key: "YOUR_KEY"
   ```

   It queries `http://v2.api.iphub.info/ip/<IP>` with an `X-Key` header.

   ProxyCheck.io requires a free key:

   ```yaml
   proxycheckerio-api:
     enabled: true
     key: "YOUR_KEY"
     arguments: "&risk=1&vpn=1&asn=1&tag={name}"
   ```

   It queries `http://proxycheck.io/v2/<IP>?key=...`.

3. Allow outbound HTTP/HTTPS from the proxy to the selected provider.
4. Use a real public player IP. Private or residential addresses may return
   `N`. The end-to-end test used a public VPS datacenter IP. The address
   `203.0.113.10` in this document is a reserved documentation placeholder;
   BlackBox reported the real test address as `Y`.
5. Optionally block by ASN or country. IPHub and ProxyCheck return ASN and
   country data; BlackBox does not.

   ```yaml
   blocked-asn:
     - AS31898
   blocked-country:
     - US
   ```

## End-to-end test without opening public ports

The listener was started with `proxy_protocol: true`. A local front-end sent:

```text
PROXY TCP4 203.0.113.10 127.0.0.1 <port> 25577
```

XCord saw the forwarded datacenter IP, BlackBox returned `Y`, and the bot was
kicked. The log below uses `203.0.113.10` as a placeholder for the redacted
test address:

```text
[VpnBot] disconnect: VPN/Proxy's are not allowed on this server
[XCord] Blackbox query for 203.0.113.10 = Y
[XCord] Detected proxy: IP=203.0.113.10 USER=VpnBot
```

## Notes

- BlackBox uses HTTPS and does not need a key.
- IPHub and ProxyCheck use plain HTTP and their own keys. Do not expose keys in
  logs or public repositories.
- Sending player IPs to a third-party provider has privacy implications.
- `blacklist-on-detection` adds the detected IP to the dynamic blacklist. Set it
  to `false` if detection should only kick the connection.
- Private test IPs will usually return `N`; that is not an XCord failure.
