package org.eclipse.sisu.wire;

import com.google.inject.Singleton;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.sisu.Parameters;
import xcord.Inject;

@Singleton
final class StringProperties extends AbstractMap<String, String> {
   private final Map<?, ?> delegate;

   @Inject
   StringProperties(@Parameters Map var1) {
      this.delegate = var1;
   }

   public String get(Object var1) {
      Object var2 = this.delegate.get(var1);
      return var2 instanceof String ? (String)var2 : null;
   }

   @Override
   public boolean containsKey(Object var1) {
      Object var2 = this.delegate.get(var1);
      return var2 == null ? this.delegate.containsKey(var1) : var2 instanceof String;
   }

   @Override
   public Set<Entry<String, String>> entrySet() {
      HashSet var1 = new HashSet();

      for (Entry var2 : this.delegate.entrySet()) {
         if (var2.getKey() instanceof String) {
            Object var4 = var2.getValue();
            if (var4 == null || var4 instanceof String) {
               var1.add(var2);
            }
         }
      }

      return var1;
   }
}
