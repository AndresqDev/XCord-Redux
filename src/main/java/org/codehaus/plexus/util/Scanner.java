package org.codehaus.plexus.util;

import java.io.File;
import java.util.Comparator;

public interface Scanner {
   void setIncludes(String[] var1);

   void setExcludes(String[] var1);

   void addDefaultExcludes();

   void scan();

   String[] getIncludedFiles();

   String[] getIncludedDirectories();

   File getBasedir();

   void setFilenameComparator(Comparator<String> var1);
}
