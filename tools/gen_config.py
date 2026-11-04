#!/usr/bin/env python3
import os
import re

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
JAVAP = os.path.join(ROOT, "reference")


def parse(path):
    lines = open(path, encoding="utf-8", errors="replace").read().splitlines()
    ins = []
    for ln in lines:
        m = re.match(r"\s+(\d+):\s+(\S+)\s*(.*)", ln)
        if m:
            ins.append((int(m.group(1)), m.group(2), m.group(3)))
    return ins


def camel(s):
    s = re.sub(r"[^A-Za-z0-9]+", " ", s)
    parts = [p for p in s.split() if p]
    if not parts:
        return "value"
    out = parts[0].lower()
    for p in parts[1:]:
        out += p[:1].upper() + p[1:]
    return out


def cap(s):
    return s[:1].upper() + s[1:]


def analyze(cls):
    ins = parse(f"{JAVAP}/{cls}.code.txt")
    locals_ = {}
    fields = []
    for idx, (pc, op, arg) in enumerate(ins):
        if op in ("ldc", "ldc_w") and "// String " in arg:
            val = arg.split("// String ", 1)[1]
            if idx + 1 < len(ins) and ins[idx + 1][1].startswith("astore"):
                slot = ins[idx + 1][1].replace("astore_", "").replace("astore", "") or "0"
                locals_[slot] = val
        if op == "putfield" and "// Field " in arg:
            fref = arg.split("// Field ", 1)[1].strip()
            suffix = None
            prefix = None
            for j in range(idx - 1, max(0, idx - 40), -1):
                p, a = ins[j][1], ins[j][2]
                if suffix is None and p in ("ldc", "ldc_w") and "// String " in a:
                    suffix = a.split("// String ", 1)[1]
                if p.startswith("aload"):
                    slot = p.replace("aload_", "") or "0"
                    if slot in locals_:
                        prefix = locals_[slot]
                        break
            fields.append((fref, (prefix or "") + (suffix or "")))
    return fields


def main():
    fields = analyze("y")
    seen = {}
    entries = []
    for fref, key in fields:
        name, desc = fref.split(":", 1)
        if not re.fullmatch(r"[A-Za-z0-9][A-Za-z0-9._\-]{5,}", key or ""):
            continue
        base = camel(key)
        if base in seen:
            seen[base] += 1
            base = f"{base}{seen[base]}"
        else:
            seen[base] = 0
        entries.append((name, desc, base, key))

    out = ["# XCordConfig fields derived from xcord.yml keys"]
    for name, desc, base, key in entries:
        out.append(f"F y {name} {desc} {base}")

    text = open(f"{JAVAP}/y.code.txt", encoding="utf-8", errors="replace").read()
    blocks = re.split(r"\n  (?=public |private |protected )", text)
    ret_desc = {
        "int": "I", "boolean": "Z", "long": "J", "double": "D",
        "java.lang.String": "Ljava/lang/String;",
        "java.util.Set": "Ljava/util/Set;",
        "java.util.List": "Ljava/util/List;",
        "java.util.Collection": "Ljava/util/Collection;",
        "net.md_5.bungee.api.chat.BaseComponent[]": "[Lnet/md_5/bungee/api/chat/BaseComponent;",
        "net.md_5.bungee.protocol.DefinedPacket": "Lnet/md_5/bungee/protocol/DefinedPacket;",
        "[Ljava.lang.String;": "[Ljava/lang/String;",
    }
    for b in blocks:
        m = re.match(r"public ([^\s]+) ([A-Za-z0-9_$]+)\(\);", b)
        if not m:
            continue
        ret, mname = m.group(1), m.group(2)
        gm = re.search(r"getfield\s+#\d+\s+// Field ([A-Za-z0-9_$]+:[^ \n]+)", b)
        if not gm or b.count("getfield") != 1:
            continue
        fref = gm.group(1)
        for name, desc, base, key in entries:
            if fref == f"{name}:{desc}":
                d = ret_desc.get(ret)
                if d:
                    out.append(f"M y {mname} (){d} get{cap(base)}")
                break

    with open(f"{ROOT}/map-config.txt", "w", encoding="utf-8") as fh:
        fh.write("\n".join(out) + "\n")
    print(f"config lines: {len(out)}")


if __name__ == "__main__":
    main()
