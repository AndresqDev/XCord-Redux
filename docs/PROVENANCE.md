# Provenance and Licensing

## Source of this tree

This repository is a reconstruction of a third-party XCord jar supplied for
review. The process is:

1. Inventory the jar and identify obfuscated application classes.
2. Generate a class, field, and method mapping.
3. Remap the jar with ASM.
4. Decompile with Vineflower and CFR.
5. Reconstruct BungeeCord hooks and modified classes manually.
6. Rebuild the application classes against the remapped library jar.

The reconstruction is not an official XCord source release and is not endorsed
by the original author.

## Original license

The original jar includes this notice:

```text
© 2019 bob7l. All rights reserved.
```

That file is preserved as [LICENSE](../LICENSE). It does not grant a license to
redistribute the original XCord code. Anyone publishing or distributing this
reconstruction is responsible for obtaining the necessary permission from the
rights holder.

## Third-party components

The jar contains shaded libraries and BungeeCord/Travertine code. Their licenses
and notices are preserved under [licenses/](../licenses/) and in the original
`META-INF` resources. Do not remove those notices when redistributing the jar.

The build also uses:

- Vineflower 1.10.1
- CFR 0.152
- ASM 9.7.1
- BungeeCord, Travertine, and Waterfall components
- Netty, Guava, Log4j, JLine, Jansi, and other shaded libraries

## No affiliation

This project is not affiliated with Mojang, Microsoft, SpigotMC, PaperMC,
BungeeCord, Travertine, Waterfall, or the original XCord author. Minecraft is a
trademark of Microsoft.
