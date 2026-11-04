package xcord.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ConfigUpdater {
    private final String[] header;
    private final Map<String, String[]> replacements = new HashMap<>();

    public ConfigUpdater(String... header) {
        this.header = header;
    }

    public ConfigUpdater addReplacement(String key, String... replacement) {
        this.replacements.put(key, replacement);
        return this;
    }

    public void update(File file) throws IOException {
        if (!file.isFile()) {
            return;
        }
        List<String> lines = new ArrayList<>(250);
        lines.addAll(Arrays.asList(this.header));
        Path path = file.toPath();
        try (Stream<String> stream = Files.lines(path)) {
            stream.filter(line -> !line.startsWith("#")).forEach(lines::add);
        }
        for (int i = this.header.length; i < lines.size(); ++i) {
            String line = lines.get(i);
            for (Map.Entry<String, String[]> entry : this.replacements.entrySet()) {
                if (!line.contains(entry.getKey())) {
                    continue;
                }
                int inserted = 0;
                for (String replacement : entry.getValue()) {
                    lines.add(i + inserted++, replacement);
                }
                i += inserted;
            }
        }
        Files.write(path, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
