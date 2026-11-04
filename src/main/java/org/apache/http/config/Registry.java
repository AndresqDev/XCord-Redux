package org.apache.http.config;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public final class Registry<I> implements Lookup<I> {
   private final Map<String, I> map;

   Registry(Map<String, I> var1) {
      this.map = new ConcurrentHashMap<>(var1);
   }

   @Override
   public I lookup(String var1) {
      return var1 == null ? null : this.map.get(var1.toLowerCase(Locale.ROOT));
   }

   @Override
   public String toString() {
      return this.map.toString();
   }
}
