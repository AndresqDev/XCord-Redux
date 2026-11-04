package org.apache.logging.log4j.core.appender.nosql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DefaultNoSqlObject implements NoSqlObject<Map<String, Object>> {
   private final Map<String, Object> map = new HashMap<>();

   @Override
   public void set(String var1, Object var2) {
      this.map.put(var1, var2);
   }

   @Override
   public void set(String var1, NoSqlObject<Map<String, Object>> var2) {
      this.map.put(var1, var2.unwrap());
   }

   @Override
   public void set(String var1, Object[] var2) {
      this.map.put(var1, Arrays.asList(var2));
   }

   @Override
   public void set(String var1, NoSqlObject<Map<String, Object>>[] var2) {
      ArrayList var3 = new ArrayList(var2.length);

      for (NoSqlObject var7 : var2) {
         var3.add(var7.unwrap());
      }

      this.map.put(var1, var3);
   }

   public Map<String, Object> unwrap() {
      return this.map;
   }
}
