import socket, struct, json, sys

def varint(n):
    out=b''
    while True:
        b=n & 0x7f; n >>= 7
        if n: out += bytes([b|0x80])
        else: out += bytes([b]); return out

def read_varint(s):
    n=0; shift=0
    while True:
        b=s.recv(1)
        if not b: raise EOFError
        b=b[0]; n |= (b & 0x7f) << shift
        if not (b & 0x80): return n
        shift += 7

def read_exact(s,n):
    out=b''
    while len(out)<n:
        c=s.recv(n-len(out))
        if not c: raise EOFError
        out+=c
    return out

def ping(host, port):
    s=socket.create_connection((host,port), timeout=5)
    host_b=host.encode()
    hs = b'\x00' + varint(47) + varint(len(host_b)) + host_b + struct.pack('>H', port) + varint(1)
    s.sendall(varint(len(hs))+hs)
    s.sendall(varint(1)+b'\x00')
    ln=read_varint(s); data=read_exact(s,ln)
    pid=data[0]
    # string length varint
    i=1; strlen=0; shift=0
    while True:
        b=data[i]; i+=1; strlen |= (b & 0x7f) << shift
        if not (b & 0x80): break
        shift += 7
    js=data[i:i+strlen].decode('utf-8')
    s.close()
    return json.loads(js)

for host,port in [('127.0.0.1',25577),('127.0.0.1',25565)]:
    print('---',host,port)
    try:
        j=ping(host,port)
        print(json.dumps(j, indent=2, ensure_ascii=False)[:1200])
    except Exception as e:
        print('ERROR',repr(e))
