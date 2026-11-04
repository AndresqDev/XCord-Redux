package org.apache.maven.model;

import java.io.Serializable;

public class PluginManagement extends PluginContainer implements Serializable, Cloneable {
   public PluginManagement clone() {
      try {
         return (PluginManagement)super.clone();
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }
}
