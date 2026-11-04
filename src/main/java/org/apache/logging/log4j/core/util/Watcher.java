package org.apache.logging.log4j.core.util;

import java.util.List;
import org.apache.logging.log4j.core.config.ConfigurationListener;
import org.apache.logging.log4j.core.config.Reconfigurable;

public interface Watcher {
   String CATEGORY = "Watcher";
   String ELEMENT_TYPE = "watcher";

   List<ConfigurationListener> getListeners();

   void modified();

   boolean isModified();

   long getLastModified();

   void watching(Source var1);

   Source getSource();

   Watcher newWatcher(Reconfigurable var1, List<ConfigurationListener> var2, long var3);
}
