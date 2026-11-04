package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PluginExecution extends ConfigurationContainer implements Serializable, Cloneable {
   private String id = "default";
   private String phase;
   private int priority = 0;
   private List<String> goals;
   public static final String DEFAULT_EXECUTION_ID = "default";

   public void addGoal(String var1) {
      this.getGoals().add(var1);
   }

   public PluginExecution clone() {
      try {
         PluginExecution var1 = (PluginExecution)super.clone();
         if (this.goals != null) {
            var1.goals = new ArrayList<>();
            var1.goals.addAll(this.goals);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public List<String> getGoals() {
      if (this.goals == null) {
         this.goals = new ArrayList<>();
      }

      return this.goals;
   }

   public String getId() {
      return this.id;
   }

   public String getPhase() {
      return this.phase;
   }

   public int getPriority() {
      return this.priority;
   }

   public void removeGoal(String var1) {
      this.getGoals().remove(var1);
   }

   public void setGoals(List<String> var1) {
      this.goals = var1;
   }

   public void setId(String var1) {
      this.id = var1;
   }

   public void setPhase(String var1) {
      this.phase = var1;
   }

   public void setPriority(int var1) {
      this.priority = var1;
   }

   @Override
   public String toString() {
      return this.getId();
   }
}
