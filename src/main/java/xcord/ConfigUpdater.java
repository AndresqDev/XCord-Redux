package xcord;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConfigUpdater {
    private final String[] header;
    private final Map<String, String[]> replacements = new HashMap<String, String[]>();

    public ConfigUpdater(String ... stringArray) {
        this.header = stringArray;
    }

    public ConfigUpdater addReplacement(String string, String ... stringArray) {
        this.replacements.put(string, stringArray);
        return this;
    }

    public void update(File file) throws IOException {
        if (!file.isFile()) {
            return;
        }
        ArrayList<String> arrayList = new ArrayList<String>(250);
        arrayList.addAll(Arrays.asList(this.header));
        Path path = file.toPath();
        Files.lines(path).filter(string -> !string.startsWith("#")).forEach(arrayList::add);
        for (int i = this.header.length; i < arrayList.size(); ++i) {
            String string2 = (String)arrayList.get(i);
            for (Map.Entry<String, String[]> entry : this.replacements.entrySet()) {
                if (!string2.contains(entry.getKey())) continue;
                int n = 0;
                for (String string3 : entry.getValue()) {
                    arrayList.add(i + n++, string3);
                }
                i += n;
            }
        }
        Files.write(path, arrayList, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}

