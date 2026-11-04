package org.apache.logging.log4j.core.config;

import java.io.File;
import java.util.List;
import org.apache.logging.log4j.core.util.AbstractWatcher;
import org.apache.logging.log4j.core.util.FileWatcher;
import org.apache.logging.log4j.core.util.Source;
import org.apache.logging.log4j.core.util.Watcher;

public class ConfigurationFileWatcher extends AbstractWatcher implements FileWatcher {
   private File file;
   private long lastModifiedMillis;

   public ConfigurationFileWatcher(Configuration var1, Reconfigurable var2, List<ConfigurationListener> var3, long var4) {
      super(var1, var2, var3);
      this.lastModifiedMillis = var4;
   }

   @Override
   public long getLastModified() {
      return this.file != null ? this.file.lastModified() : 0L;
   }

   @Override
   public void fileModified(File var1) {
      this.lastModifiedMillis = var1.lastModified();
   }

   @Override
   public void watching(Source var1) {
      this.file = var1.getFile();
      this.lastModifiedMillis = this.file.lastModified();
      super.watching(var1);
   }

   @Override
   public boolean isModified() {
      return this.lastModifiedMillis != this.file.lastModified();
   }

   @Override
   public Watcher newWatcher(Reconfigurable var1, List<ConfigurationListener> var2, long var3) {
      ConfigurationFileWatcher var5 = new ConfigurationFileWatcher(this.getConfiguration(), var1, var2, var3);
      if (this.getSource() != null) {
         var5.watching(this.getSource());
      }

      return var5;
   }
}
