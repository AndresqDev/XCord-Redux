package org.apache.maven.model;

import java.io.Serializable;

public class PluginConfiguration extends PluginContainer implements Serializable, Cloneable {
   private PluginManagement pluginManagement;

   public PluginConfiguration clone() {
      try {
         PluginConfiguration var1 = (PluginConfiguration)super.clone();
         if (this.pluginManagement != null) {
            var1.pluginManagement = this.pluginManagement.clone();
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public PluginManagement getPluginManagement() {
      return this.pluginManagement;
   }

   public void setPluginManagement(PluginManagement var1) {
      this.pluginManagement = var1;
   }
}
