package org.slf4j.event;

public enum Level {
   ERROR(40, "ERROR"),
   WARN(30, "WARN"),
   INFO(20, "INFO"),
   DEBUG(10, "DEBUG"),
   TRACE(0, "TRACE");

   private int levelInt;
   private String levelStr;

   private Level(int var3, String var4) {
      this.levelInt = var3;
      this.levelStr = var4;
   }

   public int toInt() {
      return this.levelInt;
   }

   @Override
   public String toString() {
      return this.levelStr;
   }
}
