package xcord.blacklist;

import java.io.File;

public interface FileBlacklist extends Blacklist {
    public void load(File file, String tableName);
}
