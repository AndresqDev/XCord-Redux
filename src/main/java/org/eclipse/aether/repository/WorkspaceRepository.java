package org.eclipse.aether.repository;

import java.util.UUID;

public final class WorkspaceRepository implements ArtifactRepository {
   private final String type;
   private final Object key;

   public WorkspaceRepository() {
      this("workspace");
   }

   public WorkspaceRepository(String var1) {
      this(var1, null);
   }

   public WorkspaceRepository(String var1, Object var2) {
      this.type = var1 != null ? var1 : "";
      this.key = var2 != null ? var2 : UUID.randomUUID().toString().replace("-", "");
   }

   @Override
   public String getContentType() {
      return this.type;
   }

   @Override
   public String getId() {
      return "workspace";
   }

   public Object getKey() {
      return this.key;
   }

   @Override
   public String toString() {
      return "(" + this.getContentType() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         WorkspaceRepository var2 = (WorkspaceRepository)var1;
         return this.getContentType().equals(var2.getContentType()) && this.getKey().equals(var2.getKey());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = var1 * 31 + this.getKey().hashCode();
      return var1 * 31 + this.getContentType().hashCode();
   }
}
