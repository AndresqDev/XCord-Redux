package org.apache.maven.model.building;

import java.util.List;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;

class ModelData {
   private final ModelSource source;
   private Model model;
   private Model rawModel;
   private List<Profile> activeProfiles;
   private String groupId;
   private String artifactId;
   private String version;

   ModelData(ModelSource var1, Model var2) {
      this.source = var1;
      this.model = var2;
   }

   ModelData(ModelSource var1, Model var2, String var3, String var4, String var5) {
      this.source = var1;
      this.model = var2;
      this.setGroupId(var3);
      this.setArtifactId(var4);
      this.setVersion(var5);
   }

   public ModelSource getSource() {
      return this.source;
   }

   public Model getModel() {
      return this.model;
   }

   public void setModel(Model var1) {
      this.model = var1;
   }

   public Model getRawModel() {
      return this.rawModel;
   }

   public void setRawModel(Model var1) {
      this.rawModel = var1;
   }

   public List<Profile> getActiveProfiles() {
      return this.activeProfiles;
   }

   public void setActiveProfiles(List<Profile> var1) {
      this.activeProfiles = var1;
   }

   public String getGroupId() {
      return this.groupId != null ? this.groupId : "";
   }

   public void setGroupId(String var1) {
      this.groupId = var1;
   }

   public String getArtifactId() {
      return this.artifactId != null ? this.artifactId : "";
   }

   public void setArtifactId(String var1) {
      this.artifactId = var1;
   }

   public String getVersion() {
      return this.version != null ? this.version : "";
   }

   public void setVersion(String var1) {
      this.version = var1;
   }

   public String getId() {
      StringBuilder var1 = new StringBuilder(128);
      var1.append(this.getGroupId()).append(':').append(this.getArtifactId()).append(':').append(this.getVersion());
      return var1.toString();
   }

   @Override
   public String toString() {
      return String.valueOf(this.model);
   }
}
