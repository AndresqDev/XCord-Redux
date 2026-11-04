package org.apache.maven.building;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public class UrlSource implements Source {
   private URL url;

   public UrlSource(URL var1) {
      this.url = Objects.requireNonNull(var1, "url cannot be null");
   }

   @Override
   public InputStream getInputStream() throws IOException {
      return this.url.openStream();
   }

   @Override
   public String getLocation() {
      return this.url.toString();
   }

   public URL getUrl() {
      return this.url;
   }

   @Override
   public String toString() {
      return this.getLocation();
   }
}
