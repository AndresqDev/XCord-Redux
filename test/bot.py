#!/usr/bin/env python3
"""Minimal test bot for XCord (Minecraft 1.8.x, protocol 47).

Usage:
  python3 bot.py --name TestBot
  python3 bot.py --name TestBot --chat "hello from the bot"
  python3 bot.py --count 5 --stay 10
  python3 bot.py --count 20 --delay 0.1 --stay 3

Interactive commands (single-bot mode):
  say <text>           send chat
  pos <x> <y> <z>      move
  look <yaw> <pitch>   rotate
  status               show state
  quit                 exit
"""
import argparse
import json
import socket
import struct
import sys
import threading
import time
import zlib

PROTOCOL = 47


def varint(n):
    out = b""
    while True:
        b = n & 0x7F
        n >>= 7
        if n:
            out += bytes([b | 0x80])
        else:
            return out + bytes([b])


def read_varint_sock(s):
    n = 0
    shift = 0
    while True:
        b = s.recv(1)
        if not b:
            raise EOFError("eof")
        b = b[0]
        n |= (b & 0x7F) << shift
        if not (b & 0x80):
            return n
        shift += 7
        if shift > 35:
            raise ValueError("varint too big")


def read_varint_bytes(b, i):
    n = 0
    shift = 0
    while True:
        v = b[i]
        i += 1
        n |= (v & 0x7F) << shift
        if not (v & 0x80):
            return n, i
        shift += 7


def read_exact(s, n):
    out = b""
    while len(out) < n:
        c = s.recv(n - len(out))
        if not c:
            raise EOFError("eof")
        out += c
    return out


def read_string(b, i):
    n, i = read_varint_bytes(b, i)
    return b[i:i + n].decode("utf-8", "replace"), i + n


def flatten(c):
    if isinstance(c, str):
        return c
    if isinstance(c, list):
        return "".join(flatten(x) for x in c)
    if isinstance(c, dict):
        out = c.get("text", "")
        if "translate" in c:
            out += c["translate"]
        for x in c.get("with", []):
            out += flatten(x)
        for x in c.get("extra", []):
            out += flatten(x)
        return out
    return ""


class Bot:
    def __init__(self, name, host, port, bind=None, quiet=False, delay_login=0, large_plugin=0):
        self.name = name
        self.host = host
        self.port = port
        self.bind = bind
        self.quiet = quiet
        self.delay_login = delay_login
        self.large_plugin = large_plugin
        self.sock = None
        self.comp = False
        self.threshold = -1
        self.state = "login"
        self.joined = False
        self.running = False
        self.x = 0.0
        self.y = 4.0
        self.z = 0.0
        self.yaw = 0.0
        self.pitch = 0.0

    def log(self, msg):
        if not self.quiet:
            print(f"[{self.name}] {msg}", flush=True)

    def send_packet(self, pid, payload=b""):
        data = varint(pid) + payload
        if self.comp:
            if self.threshold > 0 and len(data) < self.threshold:
                data = b"\x00" + data
            else:
                data = varint(len(data)) + zlib.compress(data)
        self.sock.sendall(varint(len(data)) + data)

    def read_packet(self):
        ln = read_varint_sock(self.sock)
        data = read_exact(self.sock, ln)
        if self.comp:
            ulen, i = read_varint_bytes(data, 0)
            data = data[i:]
            if ulen:
                data = zlib.decompress(data)
        pid, i = read_varint_bytes(data, 0)
        return pid, data[i:]

    def connect(self):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.settimeout(10)
        if self.bind:
            s.bind((self.bind, 0))
        s.connect((self.host, self.port))
        self.sock = s
        host = self.host.encode()
        hs = varint(PROTOCOL) + varint(len(host)) + host + struct.pack(">H", self.port) + varint(2)
        self.send_packet(0x00, hs)
        if self.delay_login:
            self.log(f"delaying login {self.delay_login}s")
            time.sleep(self.delay_login)
        name = self.name.encode()
        self.send_packet(0x00, varint(len(name)) + name)
        self.log(f"connected from {self.bind or '127.0.0.1'} to {self.host}:{self.port}")
        self.running = True
        threading.Thread(target=self._read_loop, daemon=True).start()

    def _read_loop(self):
        try:
            while self.running:
                pid, data = self.read_packet()
                if self.state == "login":
                    if pid == 0x03:  # Set Compression (login state only)
                        th, _ = read_varint_bytes(data, 0)
                        self.threshold = th
                        self.comp = th >= 0
                        self.log(f"compression enabled (threshold {th})")
                    elif pid == 0x02:  # Login Success
                        uuid, i = read_string(data, 0)
                        name, i = read_string(data, i)
                        self.log(f"login OK uuid={uuid} name={name}")
                        self.state = "play"
                    elif pid == 0x00:  # Disconnect
                        reason, _ = read_string(data, 0)
                        self.log(f"disconnect (login): {reason}")
                        self.running = False
                        return
                else:
                    self._handle_play(pid, data)
        except Exception as e:
            if self.running:
                self.log(f"disconnected: {e!r}")
            self.running = False

    def _handle_play(self, pid, data):
        if pid == 0x00:  # KeepAlive -> echo
            self.send_packet(0x00, data)
        elif pid == 0x01:  # Join Game
            self.joined = True
            self.log("Join Game received")
            # Client Settings
            loc = b"en_US"
            self.send_packet(0x15, varint(len(loc)) + loc + bytes([8, 0, 1, 0x7F]))
            # Brand
            ch = b"MC|Brand"
            br = b"vanilla"
            self.send_packet(0x17, varint(len(ch)) + ch + varint(len(br)) + br)
            if self.large_plugin:
                ch2 = b"MC|Brand"
                data = b"A" * self.large_plugin
                self.send_packet(0x17, varint(len(ch2)) + ch2 + varint(len(data)) + data)
        elif pid == 0x02:  # Chat
            msg, _ = read_string(data, 0)
            try:
                obj = json.loads(msg)
                text = flatten(obj)
            except Exception:
                text = msg
            self.log(f"chat: {text}")
        elif pid == 0x08:  # Position And Look
            if len(data) >= 33:
                x, y, z = struct.unpack(">ddd", data[:24])
                yaw, pitch = struct.unpack(">ff", data[24:32])
                self.x, self.y, self.z, self.yaw, self.pitch = x, y, z, yaw, pitch
                self.send_packet(0x06, struct.pack(">dddff", x, y, z, yaw, pitch) + b"\x01")
        elif pid == 0x40:  # Disconnect (play)
            reason, _ = read_string(data, 0)
            self.log(f"disconnect: {flatten(json.loads(reason)) if reason.startswith('{') else reason}")
            self.running = False

    def wait_joined(self, timeout=8):
        start = time.time()
        while not self.joined and self.running and time.time() - start < timeout:
            time.sleep(0.1)
        return self.joined

    def say(self, text):
        if not self.wait_joined():
            self.log("not in play state yet; not sending the command")
            return
        b = text.encode()
        self.send_packet(0x01, varint(len(b)) + b)

    def pos(self, x, y, z):
        if not self.wait_joined():
            self.log("not in play state yet; not sending the position")
            return
        self.x, self.y, self.z = float(x), float(y), float(z)
        self.send_packet(0x04, struct.pack(">ddd", self.x, self.y, self.z) + b"\x01")

    def look(self, yaw, pitch):
        if not self.wait_joined():
            self.log("not in play state yet; not sending the rotation")
            return
        self.yaw, self.pitch = float(yaw), float(pitch)
        self.send_packet(0x05, struct.pack(">ff", self.yaw, self.pitch) + b"\x01")

    def close(self):
        self.running = False
        try:
            self.sock.close()
        except Exception:
            pass


def run_multi(args):
    bots = []
    for i in range(args.count):
        name = f"{args.name}{i + 1}"
        bind = args.bind or f"127.0.0.{2 + (i % 250)}"
        b = Bot(name, args.host, args.port, bind, args.quiet, args.delay_login, args.large_plugin)
        bots.append(b)
        try:
            b.connect()
        except Exception as e:
            print(f"[{name}] error: {e!r}")
        time.sleep(args.delay)
    if args.chat:
        for b in bots:
            if b.running:
                b.say(args.chat)
    end = time.time() + args.stay
    while time.time() < end:
        time.sleep(0.5)
    for b in bots:
        b.close()
    print(f"Closed {len(bots)} bots")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--name", default="TestBot")
    ap.add_argument("--host", default="127.0.0.1")
    ap.add_argument("--port", type=int, default=25577)
    ap.add_argument("--bind", default=None, help="source IP (for example 127.0.0.2)")
    ap.add_argument("--count", type=int, default=1)
    ap.add_argument("--delay", type=float, default=0.3)
    ap.add_argument("--stay", type=float, default=15)
    ap.add_argument("--chat", default=None)
    ap.add_argument("--quiet", action="store_true")
    ap.add_argument("--delay-login", type=float, default=0)
    ap.add_argument("--large-plugin", type=int, default=0)
    args = ap.parse_args()

    if args.count > 1:
        run_multi(args)
        return

    bot = Bot(args.name, args.host, args.port, args.bind, args.quiet, args.delay_login, args.large_plugin)
    try:
        bot.connect()
    except Exception as e:
        print(f"Connection error: {e!r}")
        sys.exit(1)
    if args.chat:
        time.sleep(1)
        bot.say(args.chat)
    print("Commands: say <text>, pos x y z, look yaw pitch, status, quit")
    try:
        while bot.running:
            try:
                line = input("bot> ").strip()
            except EOFError:
                break
            if not line:
                continue
            cmd, _, rest = line.partition(" ")
            if cmd in ("quit", "exit"):
                break
            if cmd == "say":
                bot.say(rest)
            elif cmd == "pos":
                try:
                    x, y, z = rest.split()
                    bot.pos(x, y, z)
                except Exception:
                    print("usage: pos x y z")
            elif cmd == "look":
                try:
                    y, p = rest.split()
                    bot.look(y, p)
                except Exception:
                    print("usage: look yaw pitch")
            elif cmd == "status":
                print(f"joined={bot.joined} state={bot.state} pos=({bot.x},{bot.y},{bot.z}) comp={bot.comp}")
            elif cmd == "help":
                print("say <text> | pos x y z | look yaw pitch | status | quit")
            else:
                print("unknown command; use help")
    finally:
        bot.close()


if __name__ == "__main__":
    main()
