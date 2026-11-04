package org.apache.http.protocol;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public class UriPatternMatcher<T> {
   private final Map<String, T> map = new LinkedHashMap<>();

   public synchronized Set<Entry<String, T>> entrySet() {
      return new HashSet<>(this.map.entrySet());
   }

   public synchronized void register(String var1, T var2) {
      Args.notNull(var1, "URI request pattern");
      this.map.put(var1, (T)var2);
   }

   public synchronized void unregister(String var1) {
      if (var1 != null) {
         this.map.remove(var1);
      }
   }

   @Deprecated
   public synchronized void setHandlers(Map<String, T> var1) {
      Args.notNull(var1, "Map of handlers");
      this.map.clear();
      this.map.putAll(var1);
   }

   @Deprecated
   public synchronized void setObjects(Map<String, T> var1) {
      Args.notNull(var1, "Map of handlers");
      this.map.clear();
      this.map.putAll(var1);
   }

   @Deprecated
   public synchronized Map<String, T> getObjects() {
      return this.map;
   }

   public synchronized T lookup(String var1) {
      Args.notNull(var1, "Request path");
      Object var2 = this.map.get(var1);
      if (var2 == null) {
         String var3 = null;

         for (String var5 : this.map.keySet()) {
            if (this.matchUriRequestPattern(var5, var1)
               && (var3 == null || var3.length() < var5.length() || var3.length() == var5.length() && var5.endsWith("*"))) {
               var2 = this.map.get(var5);
               var3 = var5;
            }
         }
      }

      return (T)var2;
   }

   protected boolean matchUriRequestPattern(String var1, String var2) {
      return var1.equals("*")
         ? true
         : var1.endsWith("*") && var2.startsWith(var1.substring(0, var1.length() - 1))
            || var1.startsWith("*") && var2.endsWith(var1.substring(1, var1.length()));
   }

   @Override
   public String toString() {
      return this.map.toString();
   }
}
