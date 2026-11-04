package org.apache.maven.model.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import org.apache.maven.model.Model;

public interface ModelReader {
   String IS_STRICT = "org.apache.maven.model.io.isStrict";
   String INPUT_SOURCE = "org.apache.maven.model.io.inputSource";

   Model read(File var1, Map<String, ?> var2) throws IOException, ModelParseException;

   Model read(Reader var1, Map<String, ?> var2) throws IOException, ModelParseException;

   Model read(InputStream var1, Map<String, ?> var2) throws IOException, ModelParseException;
}
