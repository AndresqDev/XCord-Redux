package org.apache.maven.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Model extends ModelBase implements Serializable, Cloneable {
   private String modelVersion;
   private Parent parent;
   private String groupId;
   private String artifactId;
   private String version;
   private String packaging = "jar";
   private String name;
   private String description;
   private String url;
   private String childProjectUrlInheritAppendPath;
   private String inceptionYear;
   private Organization organization;
   private List<License> licenses;
   private List<Developer> developers;
   private List<Contributor> contributors;
   private List<MailingList> mailingLists;
   private Prerequisites prerequisites;
   private Scm scm;
   private IssueManagement issueManagement;
   private CiManagement ciManagement;
   private Build build;
   private List<Profile> profiles;
   private String modelEncoding = "UTF-8";
   private File pomFile;

   public void addContributor(Contributor var1) {
      this.getContributors().add(var1);
   }

   public void addDeveloper(Developer var1) {
      this.getDevelopers().add(var1);
   }

   public void addLicense(License var1) {
      this.getLicenses().add(var1);
   }

   public void addMailingList(MailingList var1) {
      this.getMailingLists().add(var1);
   }

   public void addProfile(Profile var1) {
      this.getProfiles().add(var1);
   }

   public Model clone() {
      try {
         Model var1 = (Model)super.clone();
         if (this.parent != null) {
            var1.parent = this.parent.clone();
         }

         if (this.organization != null) {
            var1.organization = this.organization.clone();
         }

         if (this.licenses != null) {
            var1.licenses = new ArrayList<>();

            for (License var3 : this.licenses) {
               var1.licenses.add(var3.clone());
            }
         }

         if (this.developers != null) {
            var1.developers = new ArrayList<>();

            for (Developer var9 : this.developers) {
               var1.developers.add(var9.clone());
            }
         }

         if (this.contributors != null) {
            var1.contributors = new ArrayList<>();

            for (Contributor var10 : this.contributors) {
               var1.contributors.add(var10.clone());
            }
         }

         if (this.mailingLists != null) {
            var1.mailingLists = new ArrayList<>();

            for (MailingList var11 : this.mailingLists) {
               var1.mailingLists.add(var11.clone());
            }
         }

         if (this.prerequisites != null) {
            var1.prerequisites = this.prerequisites.clone();
         }

         if (this.scm != null) {
            var1.scm = this.scm.clone();
         }

         if (this.issueManagement != null) {
            var1.issueManagement = this.issueManagement.clone();
         }

         if (this.ciManagement != null) {
            var1.ciManagement = this.ciManagement.clone();
         }

         if (this.build != null) {
            var1.build = this.build.clone();
         }

         if (this.profiles != null) {
            var1.profiles = new ArrayList<>();

            for (Profile var12 : this.profiles) {
               var1.profiles.add(var12.clone());
            }
         }

         this.cloneHook(var1);
         return var1;
      } catch (Exception var4) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var4);
      }
   }

   public String getArtifactId() {
      return this.artifactId;
   }

   public Build getBuild() {
      return this.build;
   }

   public String getChildProjectUrlInheritAppendPath() {
      return this.childProjectUrlInheritAppendPath;
   }

   public CiManagement getCiManagement() {
      return this.ciManagement;
   }

   public List<Contributor> getContributors() {
      if (this.contributors == null) {
         this.contributors = new ArrayList<>();
      }

      return this.contributors;
   }

   public String getDescription() {
      return this.description;
   }

   public List<Developer> getDevelopers() {
      if (this.developers == null) {
         this.developers = new ArrayList<>();
      }

      return this.developers;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public String getInceptionYear() {
      return this.inceptionYear;
   }

   public IssueManagement getIssueManagement() {
      return this.issueManagement;
   }

   public List<License> getLicenses() {
      if (this.licenses == null) {
         this.licenses = new ArrayList<>();
      }

      return this.licenses;
   }

   public List<MailingList> getMailingLists() {
      if (this.mailingLists == null) {
         this.mailingLists = new ArrayList<>();
      }

      return this.mailingLists;
   }

   public String getModelEncoding() {
      return this.modelEncoding;
   }

   public String getModelVersion() {
      return this.modelVersion;
   }

   public String getName() {
      return this.name;
   }

   public Organization getOrganization() {
      return this.organization;
   }

   public String getPackaging() {
      return this.packaging;
   }

   public Parent getParent() {
      return this.parent;
   }

   public Prerequisites getPrerequisites() {
      return this.prerequisites;
   }

   public List<Profile> getProfiles() {
      if (this.profiles == null) {
         this.profiles = new ArrayList<>();
      }

      return this.profiles;
   }

   public Scm getScm() {
      return this.scm;
   }

   public String getUrl() {
      return this.url;
   }

   public String getVersion() {
      return this.version;
   }

   public void removeContributor(Contributor var1) {
      this.getContributors().remove(var1);
   }

   public void removeDeveloper(Developer var1) {
      this.getDevelopers().remove(var1);
   }

   public void removeLicense(License var1) {
      this.getLicenses().remove(var1);
   }

   public void removeMailingList(MailingList var1) {
      this.getMailingLists().remove(var1);
   }

   public void removeProfile(Profile var1) {
      this.getProfiles().remove(var1);
   }

   public void setArtifactId(String var1) {
      this.artifactId = var1;
   }

   public void setBuild(Build var1) {
      this.build = var1;
   }

   public void setChildProjectUrlInheritAppendPath(String var1) {
      this.childProjectUrlInheritAppendPath = var1;
   }

   public void setCiManagement(CiManagement var1) {
      this.ciManagement = var1;
   }

   public void setContributors(List<Contributor> var1) {
      this.contributors = var1;
   }

   public void setDescription(String var1) {
      this.description = var1;
   }

   public void setDevelopers(List<Developer> var1) {
      this.developers = var1;
   }

   public void setGroupId(String var1) {
      this.groupId = var1;
   }

   public void setInceptionYear(String var1) {
      this.inceptionYear = var1;
   }

   public void setIssueManagement(IssueManagement var1) {
      this.issueManagement = var1;
   }

   public void setLicenses(List<License> var1) {
      this.licenses = var1;
   }

   public void setMailingLists(List<MailingList> var1) {
      this.mailingLists = var1;
   }

   public void setModelEncoding(String var1) {
      this.modelEncoding = var1;
   }

   public void setModelVersion(String var1) {
      this.modelVersion = var1;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setOrganization(Organization var1) {
      this.organization = var1;
   }

   public void setPackaging(String var1) {
      this.packaging = var1;
   }

   public void setParent(Parent var1) {
      this.parent = var1;
   }

   public void setPrerequisites(Prerequisites var1) {
      this.prerequisites = var1;
   }

   public void setProfiles(List<Profile> var1) {
      this.profiles = var1;
   }

   public void setScm(Scm var1) {
      this.scm = var1;
   }

   public void setUrl(String var1) {
      this.url = var1;
   }

   public void setVersion(String var1) {
      this.version = var1;
   }

   private void cloneHook(Model var1) {
      var1.pomFile = this.pomFile;
   }

   public File getPomFile() {
      return this.pomFile;
   }

   public void setPomFile(File var1) {
      this.pomFile = var1 != null ? var1.getAbsoluteFile() : null;
   }

   public File getProjectDirectory() {
      return this.pomFile != null ? this.pomFile.getParentFile() : null;
   }

   public String getId() {
      StringBuilder var1 = new StringBuilder(64);
      var1.append(this.getGroupId() == null ? "[inherited]" : this.getGroupId());
      var1.append(":");
      var1.append(this.getArtifactId());
      var1.append(":");
      var1.append(this.getPackaging());
      var1.append(":");
      var1.append(this.getVersion() == null ? "[inherited]" : this.getVersion());
      return var1.toString();
   }

   @Override
   public String toString() {
      return this.getId();
   }

   public boolean isChildProjectUrlInheritAppendPath() {
      return this.childProjectUrlInheritAppendPath != null ? Boolean.parseBoolean(this.childProjectUrlInheritAppendPath) : true;
   }

   public void setChildProjectUrlInheritAppendPath(boolean var1) {
      this.childProjectUrlInheritAppendPath = String.valueOf(var1);
   }
}
