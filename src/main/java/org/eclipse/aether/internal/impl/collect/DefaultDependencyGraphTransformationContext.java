package org.eclipse.aether.internal.impl.collect;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.DependencyGraphTransformationContext;

class DefaultDependencyGraphTransformationContext implements DependencyGraphTransformationContext {
   private final RepositorySystemSession session;
   private final Map<Object, Object> map;

   DefaultDependencyGraphTransformationContext(RepositorySystemSession var1) {
      this.session = var1;
      this.map = new HashMap<>();
   }

   @Override
   public RepositorySystemSession getSession() {
      return this.session;
   }

   @Override
   public Object get(Object var1) {
      return this.map.get(Objects.requireNonNull(var1, "key cannot be null"));
   }

   @Override
   public Object put(Object var1, Object var2) {
      Objects.requireNonNull(var1, "key cannot be null");
      return var2 != null ? this.map.put(var1, var2) : this.map.remove(var1);
   }

   @Override
   public String toString() {
      return String.valueOf(this.map);
   }
}
