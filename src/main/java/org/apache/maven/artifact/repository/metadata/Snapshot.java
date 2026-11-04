package org.apache.maven.artifact.repository.metadata;

import java.io.Serializable;

public class Snapshot implements Serializable, Cloneable {
   private String timestamp;
   private int buildNumber = 0;
   private boolean localCopy = false;

   public Snapshot clone() {
      try {
         return (Snapshot)super.clone();
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public int getBuildNumber() {
      return this.buildNumber;
   }

   public String getTimestamp() {
      return this.timestamp;
   }

   public boolean isLocalCopy() {
      return this.localCopy;
   }

   public void setBuildNumber(int var1) {
      this.buildNumber = var1;
   }

   public void setLocalCopy(boolean var1) {
      this.localCopy = var1;
   }

   public void setTimestamp(String var1) {
      this.timestamp = var1;
   }
}
