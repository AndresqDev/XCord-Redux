package org.apache.maven.model.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import org.apache.maven.model.Model;

public interface ModelWriter {
   void write(File var1, Map<String, Object> var2, Model var3) throws IOException;

   void write(Writer var1, Map<String, Object> var2, Model var3) throws IOException;

   void write(OutputStream var1, Map<String, Object> var2, Model var3) throws IOException;
}
