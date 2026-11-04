package org.apache.maven.building;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StringSource implements Source {
   private String content;
   private String location;

   public StringSource(CharSequence var1) {
      this(var1, null);
   }

   public StringSource(CharSequence var1, String var2) {
      this.content = var1 != null ? var1.toString() : "";
      this.location = var2 != null ? var2 : "(memory)";
   }

   @Override
   public InputStream getInputStream() throws IOException {
      return new ByteArrayInputStream(this.content.getBytes(StandardCharsets.UTF_8));
   }

   @Override
   public String getLocation() {
      return this.location;
   }

   public String getContent() {
      return this.content;
   }

   @Override
   public String toString() {
      return this.getLocation();
   }
}
