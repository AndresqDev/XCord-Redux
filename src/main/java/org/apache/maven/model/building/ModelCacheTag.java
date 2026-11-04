package org.apache.maven.model.building;

import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;

interface ModelCacheTag<T> {
   ModelCacheTag<ModelData> RAW = new ModelCacheTag<ModelData>() {
      @Override
      public String getName() {
         return "raw";
      }

      @Override
      public Class<ModelData> getType() {
         return ModelData.class;
      }

      public ModelData intoCache(ModelData var1) {
         Model var2 = var1.getModel() != null ? var1.getModel().clone() : null;
         return new ModelData(var1.getSource(), var2, var1.getGroupId(), var1.getArtifactId(), var1.getVersion());
      }

      public ModelData fromCache(ModelData var1) {
         return this.intoCache(var1);
      }
   };
   ModelCacheTag<DependencyManagement> IMPORT = new ModelCacheTag<DependencyManagement>() {
      @Override
      public String getName() {
         return "import";
      }

      @Override
      public Class<DependencyManagement> getType() {
         return DependencyManagement.class;
      }

      public DependencyManagement intoCache(DependencyManagement var1) {
         return var1 != null ? var1.clone() : null;
      }

      public DependencyManagement fromCache(DependencyManagement var1) {
         return this.intoCache(var1);
      }
   };

   String getName();

   Class<T> getType();

   T intoCache(T var1);

   T fromCache(T var1);
}
