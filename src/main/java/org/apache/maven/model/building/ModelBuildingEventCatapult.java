package org.apache.maven.model.building;

interface ModelBuildingEventCatapult {
   ModelBuildingEventCatapult BUILD_EXTENSIONS_ASSEMBLED = new ModelBuildingEventCatapult() {
      @Override
      public void fire(ModelBuildingListener var1, ModelBuildingEvent var2) {
         var1.buildExtensionsAssembled(var2);
      }
   };

   void fire(ModelBuildingListener var1, ModelBuildingEvent var2);
}
