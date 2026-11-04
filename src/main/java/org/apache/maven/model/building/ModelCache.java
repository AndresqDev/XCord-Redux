package org.apache.maven.model.building;

public interface ModelCache {
   void put(String var1, String var2, String var3, String var4, Object var5);

   Object get(String var1, String var2, String var3, String var4);
}
