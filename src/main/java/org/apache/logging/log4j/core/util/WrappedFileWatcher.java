package org.apache.logging.log4j.core.util;

import java.io.File;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationListener;
import org.apache.logging.log4j.core.config.Reconfigurable;

public class WrappedFileWatcher extends AbstractWatcher implements FileWatcher {
   private final FileWatcher watcher;
   private volatile long lastModifiedMillis;

   public WrappedFileWatcher(FileWatcher var1, Configuration var2, Reconfigurable var3, List<ConfigurationListener> var4, long var5) {
      super(var2, var3, var4);
      this.watcher = var1;
      this.lastModifiedMillis = var5;
   }

   public WrappedFileWatcher(FileWatcher var1) {
      super(null, null, null);
      this.watcher = var1;
   }

   @Override
   public long getLastModified() {
      return this.lastModifiedMillis;
   }

   @Override
   public void fileModified(File var1) {
      this.watcher.fileModified(var1);
   }

   @Override
   public boolean isModified() {
      long var1 = this.getSource().getFile().lastModified();
      if (this.lastModifiedMillis != var1) {
         this.lastModifiedMillis = var1;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public List<ConfigurationListener> getListeners() {
      return super.getListeners() != null ? Collections.unmodifiableList(super.getListeners()) : null;
   }

   @Override
   public void modified() {
      if (this.getListeners() != null) {
         super.modified();
      }

      this.fileModified(this.getSource().getFile());
      this.lastModifiedMillis = this.getSource().getFile().lastModified();
   }

   @Override
   public void watching(Source var1) {
      this.lastModifiedMillis = var1.getFile().lastModified();
      super.watching(var1);
   }

   @Override
   public Watcher newWatcher(Reconfigurable var1, List<ConfigurationListener> var2, long var3) {
      WrappedFileWatcher var5 = new WrappedFileWatcher(this.watcher, this.getConfiguration(), var1, var2, var3);
      if (this.getSource() != null) {
         var5.watching(this.getSource());
      }

      return var5;
   }
}
