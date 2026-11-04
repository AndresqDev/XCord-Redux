#!/usr/bin/env python3
"""Local PROXY protocol v1 front end for XCord.

Lets XCord see a specific source IP, such as a VPS datacenter address,
without opening public ports or using NAT.
"""
import socket
import threading

LISTEN = ("127.0.0.1", 25578)
TARGET = ("127.0.0.1", 25577)
SPOOF_IP = "203.0.113.10"
LOCAL_IP = "127.0.0.1"


def pump(src, dst):
    try:
        while True:
            data = src.recv(65536)
            if not data:
                break
            dst.sendall(data)
    except Exception:
        pass
    try:
        dst.shutdown(socket.SHUT_WR)
    except Exception:
        pass


def handle(client, addr):
    try:
        upstream = socket.create_connection(TARGET, timeout=10)
        header = f"PROXY TCP4 {SPOOF_IP} {LOCAL_IP} {addr[1]} 25577\r\n".encode()
        upstream.sendall(header)
        threading.Thread(target=pump, args=(client, upstream), daemon=True).start()
        pump(upstream, client)
    except Exception as e:
        print("front error:", e, flush=True)
    finally:
        try:
            client.close()
        except Exception:
            pass


def main():
    srv = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    srv.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    srv.bind(LISTEN)
    srv.listen(50)
    print(f"PROXY front {LISTEN} -> {TARGET}, spoof={SPOOF_IP}", flush=True)
    while True:
        client, addr = srv.accept()
        threading.Thread(target=handle, args=(client, addr), daemon=True).start()


if __name__ == "__main__":
    main()
