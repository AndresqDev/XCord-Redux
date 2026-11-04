import org.objectweb.asm.*;
import org.objectweb.asm.commons.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;


public final class RemapJar {

    static final class Spec {
        final Map<String, String> classes = new HashMap<>();
        final Map<String, String> fields = new HashMap<>();
        final Map<String, String> methods = new HashMap<>();
        final Map<String, String> globalFields = new HashMap<>();
        final Map<String, Set<String>> globalFieldValues = new HashMap<>();
        final Map<String, String> globalMethods = new HashMap<>();
        final Map<String, Set<String>> globalMethodValues = new HashMap<>();
    }

    static Spec readSpec(Path path) throws IOException {
        Spec spec = new Spec();
        for (String raw : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] p = line.split("\\s+");
            if (p[0].equals("C") && p.length >= 3) {
                spec.classes.put(p[1], p[2]);
            } else if (p[0].equals("F") && p.length >= 5) {
                spec.fields.put(p[1] + '\u0000' + p[2] + '\u0000' + p[3], p[4]);
                String gk = p[2] + '\u0000' + p[3];
                spec.globalFieldValues.computeIfAbsent(gk, k -> new HashSet<>()).add(p[4]);
            } else if (p[0].equals("M") && p.length >= 5) {
                spec.methods.put(p[1] + '\u0000' + p[2] + '\u0000' + p[3], p[4]);
                String gk = p[2] + '\u0000' + p[3];
                spec.globalMethodValues.computeIfAbsent(gk, k -> new HashSet<>()).add(p[4]);
            } else {
                throw new IllegalArgumentException("Bad mapping line: " + raw);
            }
        }
        for (Map.Entry<String, Set<String>> e : spec.globalFieldValues.entrySet()) {
            if (e.getValue().size() == 1) spec.globalFields.put(e.getKey(), e.getValue().iterator().next());
        }
        for (Map.Entry<String, Set<String>> e : spec.globalMethodValues.entrySet()) {
            if (e.getValue().size() == 1) spec.globalMethods.put(e.getKey(), e.getValue().iterator().next());
        }
        return spec;
    }

    static final class Hierarchy {
        final Map<String, String> superOf = new HashMap<>();
        final Map<String, List<String>> interfacesOf = new HashMap<>();

        void read(byte[] data) {
            ClassReader reader = new ClassReader(data);
            String name = reader.getClassName();
            superOf.put(name, reader.getSuperName());
            interfacesOf.put(name, Arrays.asList(reader.getInterfaces()));
        }
    }

    static final class Mapper extends Remapper {
        final Spec spec;
        final Hierarchy hierarchy;

        Mapper(Spec spec, Hierarchy hierarchy) { this.spec = spec; this.hierarchy = hierarchy; }

        @Override public String map(String internalName) {
            return spec.classes.getOrDefault(internalName, internalName);
        }

        private String find(Map<String, String> table, String owner, String name, String descriptor) {
            Deque<String> queue = new ArrayDeque<>();
            Set<String> visited = new HashSet<>();
            queue.add(owner);
            while (!queue.isEmpty()) {
                String cls = queue.poll();
                if (!visited.add(cls)) continue;
                String value = table.get(cls + '\u0000' + name + '\u0000' + descriptor);
                if (value != null) return value;
                String superName = hierarchy.superOf.get(cls);
                if (superName != null) queue.add(superName);
                List<String> interfaces = hierarchy.interfacesOf.get(cls);
                if (interfaces != null) queue.addAll(interfaces);
            }
            return null;
        }

        @Override public String mapFieldName(String owner, String name, String descriptor) {
            String value = find(spec.fields, owner, name, descriptor);
            if (value != null) return value;
            if (spec.classes.containsKey(owner)) {
                value = spec.globalFields.get(name + '\u0000' + descriptor);
                if (value != null) return value;
            }
            return name;
        }

        @Override public String mapMethodName(String owner, String name, String descriptor) {
            String value = find(spec.methods, owner, name, descriptor);
            if (value != null) return value;
            if (spec.classes.containsKey(owner)) {
                value = spec.globalMethods.get(name + '\u0000' + descriptor);
                if (value != null) return value;
            }
            return name;
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("usage: RemapJar <input.jar> <output.jar> <mapping.txt>");
            System.exit(2);
        }
        Path in = Path.of(args[0]);
        Path out = Path.of(args[1]);
        Spec spec = readSpec(Path.of(args[2]));

        Hierarchy hierarchy = new Hierarchy();
        try (ZipFile zip = new ZipFile(in.toFile())) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    try (InputStream is = zip.getInputStream(entry)) {
                        hierarchy.read(is.readAllBytes());
                    }
                }
            }
        }

        Mapper mapper = new Mapper(spec, hierarchy);

        try (ZipInputStream zin = new ZipInputStream(new BufferedInputStream(Files.newInputStream(in)));
             ZipOutputStream zout = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(out)))) {
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;
                String name = entry.getName();
                byte[] data = zin.readAllBytes();

                if (name.endsWith(".class")) {
                    String prefix = "";
                    String classPath = name;
                    if (name.startsWith("META-INF/versions/")) {
                        int idx = name.indexOf('/', "META-INF/versions/".length());
                        if (idx > 0) {
                            prefix = name.substring(0, idx + 1);
                            classPath = name.substring(idx + 1);
                        }
                    }
                    String internal = classPath.substring(0, classPath.length() - ".class".length());
                    String mapped = mapper.map(internal);
                    ClassReader reader = new ClassReader(new ByteArrayInputStream(data));
                    ClassWriter writer = new ClassWriter(0);
                    ClassVisitor visitor = new ClassRemapper(writer, mapper);
                    reader.accept(visitor, 0);
                    byte[] remapped = writer.toByteArray();
                    String outName = prefix + mapped + ".class";
                    ZipEntry ne = new ZipEntry(outName);
                    ne.setTime(entry.getTime());
                    zout.putNextEntry(ne);
                    zout.write(remapped);
                    zout.closeEntry();
                } else {
                    ZipEntry ne = new ZipEntry(name);
                    ne.setTime(entry.getTime());
                    zout.putNextEntry(ne);
                    zout.write(data);
                    zout.closeEntry();
                }
            }
        }
        System.out.println("remapped " + in + " -> " + out);
    }
}
