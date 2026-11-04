package org.apache.maven.model.io.xpp3;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Extension;
import org.apache.maven.model.FileSet;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Parent;
import org.apache.maven.model.PatternSet;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginConfiguration;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Relocation;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryBase;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Site;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.MXSerializer;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class MavenXpp3Writer {
   private static final String NAMESPACE = null;
   private String fileComment = null;

   public void setFileComment(String var1) {
      this.fileComment = var1;
   }

   public void write(Writer var1, Model var2) throws IOException {
      MXSerializer var3 = new MXSerializer();
      var3.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
      var3.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
      var3.setOutput(var1);
      var3.startDocument(var2.getModelEncoding(), null);
      this.writeModel(var2, "project", var3);
      var3.endDocument();
   }

   public void write(OutputStream var1, Model var2) throws IOException {
      MXSerializer var3 = new MXSerializer();
      var3.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
      var3.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
      var3.setOutput(var1, var2.getModelEncoding());
      var3.startDocument(var2.getModelEncoding(), null);
      this.writeModel(var2, "project", var3);
      var3.endDocument();
   }

   private void writeActivation(Activation var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.isActiveByDefault()) {
         var3.startTag(NAMESPACE, "activeByDefault").text(String.valueOf(var1.isActiveByDefault())).endTag(NAMESPACE, "activeByDefault");
      }

      if (var1.getJdk() != null) {
         var3.startTag(NAMESPACE, "jdk").text(var1.getJdk()).endTag(NAMESPACE, "jdk");
      }

      if (var1.getOs() != null) {
         this.writeActivationOS(var1.getOs(), "os", var3);
      }

      if (var1.getProperty() != null) {
         this.writeActivationProperty(var1.getProperty(), "property", var3);
      }

      if (var1.getFile() != null) {
         this.writeActivationFile(var1.getFile(), "file", var3);
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeActivationFile(ActivationFile var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getMissing() != null) {
         var3.startTag(NAMESPACE, "missing").text(var1.getMissing()).endTag(NAMESPACE, "missing");
      }

      if (var1.getExists() != null) {
         var3.startTag(NAMESPACE, "exists").text(var1.getExists()).endTag(NAMESPACE, "exists");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeActivationOS(ActivationOS var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getName() != null) {
         var3.startTag(NAMESPACE, "name").text(var1.getName()).endTag(NAMESPACE, "name");
      }

      if (var1.getFamily() != null) {
         var3.startTag(NAMESPACE, "family").text(var1.getFamily()).endTag(NAMESPACE, "family");
      }

      if (var1.getArch() != null) {
         var3.startTag(NAMESPACE, "arch").text(var1.getArch()).endTag(NAMESPACE, "arch");
      }

      if (var1.getVersion() != null) {
         var3.startTag(NAMESPACE, "version").text(var1.getVersion()).endTag(NAMESPACE, "version");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeActivationProperty(ActivationProperty var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getName() != null) {
         var3.startTag(NAMESPACE, "name").text(var1.getName()).endTag(NAMESPACE, "name");
      }

      if (var1.getValue() != null) {
         var3.startTag(NAMESPACE, "value").text(var1.getValue()).endTag(NAMESPACE, "value");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeBuild(Build var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getSourceDirectory() != null) {
         var3.startTag(NAMESPACE, "sourceDirectory").text(var1.getSourceDirectory()).endTag(NAMESPACE, "sourceDirectory");
      }

      if (var1.getScriptSourceDirectory() != null) {
         var3.startTag(NAMESPACE, "scriptSourceDirectory").text(var1.getScriptSourceDirectory()).endTag(NAMESPACE, "scriptSourceDirectory");
      }

      if (var1.getTestSourceDirectory() != null) {
         var3.startTag(NAMESPACE, "testSourceDirectory").text(var1.getTestSourceDirectory()).endTag(NAMESPACE, "testSourceDirectory");
      }

      if (var1.getOutputDirectory() != null) {
         var3.startTag(NAMESPACE, "outputDirectory").text(var1.getOutputDirectory()).endTag(NAMESPACE, "outputDirectory");
      }

      if (var1.getTestOutputDirectory() != null) {
         var3.startTag(NAMESPACE, "testOutputDirectory").text(var1.getTestOutputDirectory()).endTag(NAMESPACE, "testOutputDirectory");
      }

      if (var1.getExtensions() != null && var1.getExtensions().size() > 0) {
         var3.startTag(NAMESPACE, "extensions");

         for (Extension var5 : var1.getExtensions()) {
            this.writeExtension(var5, "extension", var3);
         }

         var3.endTag(NAMESPACE, "extensions");
      }

      if (var1.getDefaultGoal() != null) {
         var3.startTag(NAMESPACE, "defaultGoal").text(var1.getDefaultGoal()).endTag(NAMESPACE, "defaultGoal");
      }

      if (var1.getResources() != null && var1.getResources().size() > 0) {
         var3.startTag(NAMESPACE, "resources");

         for (Resource var10 : var1.getResources()) {
            this.writeResource(var10, "resource", var3);
         }

         var3.endTag(NAMESPACE, "resources");
      }

      if (var1.getTestResources() != null && var1.getTestResources().size() > 0) {
         var3.startTag(NAMESPACE, "testResources");

         for (Resource var11 : var1.getTestResources()) {
            this.writeResource(var11, "testResource", var3);
         }

         var3.endTag(NAMESPACE, "testResources");
      }

      if (var1.getDirectory() != null) {
         var3.startTag(NAMESPACE, "directory").text(var1.getDirectory()).endTag(NAMESPACE, "directory");
      }

      if (var1.getFinalName() != null) {
         var3.startTag(NAMESPACE, "finalName").text(var1.getFinalName()).endTag(NAMESPACE, "finalName");
      }

      if (var1.getFilters() != null && var1.getFilters().size() > 0) {
         var3.startTag(NAMESPACE, "filters");

         for (String var12 : var1.getFilters()) {
            var3.startTag(NAMESPACE, "filter").text(var12).endTag(NAMESPACE, "filter");
         }

         var3.endTag(NAMESPACE, "filters");
      }

      if (var1.getPluginManagement() != null) {
         this.writePluginManagement(var1.getPluginManagement(), "pluginManagement", var3);
      }

      if (var1.getPlugins() != null && var1.getPlugins().size() > 0) {
         var3.startTag(NAMESPACE, "plugins");

         for (Plugin var13 : var1.getPlugins()) {
            this.writePlugin(var13, "plugin", var3);
         }

         var3.endTag(NAMESPACE, "plugins");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeBuildBase(BuildBase var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getDefaultGoal() != null) {
         var3.startTag(NAMESPACE, "defaultGoal").text(var1.getDefaultGoal()).endTag(NAMESPACE, "defaultGoal");
      }

      if (var1.getResources() != null && var1.getResources().size() > 0) {
         var3.startTag(NAMESPACE, "resources");

         for (Resource var5 : var1.getResources()) {
            this.writeResource(var5, "resource", var3);
         }

         var3.endTag(NAMESPACE, "resources");
      }

      if (var1.getTestResources() != null && var1.getTestResources().size() > 0) {
         var3.startTag(NAMESPACE, "testResources");

         for (Resource var9 : var1.getTestResources()) {
            this.writeResource(var9, "testResource", var3);
         }

         var3.endTag(NAMESPACE, "testResources");
      }

      if (var1.getDirectory() != null) {
         var3.startTag(NAMESPACE, "directory").text(var1.getDirectory()).endTag(NAMESPACE, "directory");
      }

      if (var1.getFinalName() != null) {
         var3.startTag(NAMESPACE, "finalName").text(var1.getFinalName()).endTag(NAMESPACE, "finalName");
      }

      if (var1.getFilters() != null && var1.getFilters().size() > 0) {
         var3.startTag(NAMESPACE, "filters");

         for (String var10 : var1.getFilters()) {
            var3.startTag(NAMESPACE, "filter").text(var10).endTag(NAMESPACE, "filter");
         }

         var3.endTag(NAMESPACE, "filters");
      }

      if (var1.getPluginManagement() != null) {
         this.writePluginManagement(var1.getPluginManagement(), "pluginManagement", var3);
      }

      if (var1.getPlugins() != null && var1.getPlugins().size() > 0) {
         var3.startTag(NAMESPACE, "plugins");

         for (Plugin var11 : var1.getPlugins()) {
            this.writePlugin(var11, "plugin", var3);
         }

         var3.endTag(NAMESPACE, "plugins");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeCiManagement(CiManagement var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getSystem() != null) {
         var3.startTag(NAMESPACE, "system").text(var1.getSystem()).endTag(NAMESPACE, "system");
      }

      if (var1.getUrl() != null) {
         var3.startTag(NAMESPACE, "url").text(var1.getUrl()).endTag(NAMESPACE, "url");
      }

      if (var1.getNotifiers() != null && var1.getNotifiers().size() > 0) {
         var3.startTag(NAMESPACE, "notifiers");

         for (Notifier var5 : var1.getNotifiers()) {
            this.writeNotifier(var5, "notifier", var3);
         }

         var3.endTag(NAMESPACE, "notifiers");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeConfigurationContainer(ConfigurationContainer var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getInherited() != null) {
         var3.startTag(NAMESPACE, "inherited").text(var1.getInherited()).endTag(NAMESPACE, "inherited");
      }

      if (var1.getConfiguration() != null) {
         ((Xpp3Dom)var1.getConfiguration()).writeToSerializer(NAMESPACE, var3);
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeContributor(Contributor var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getName() != null) {
         var3.startTag(NAMESPACE, "name").text(var1.getName()).endTag(NAMESPACE, "name");
      }

      if (var1.getEmail() != null) {
         var3.startTag(NAMESPACE, "email").text(var1.getEmail()).endTag(NAMESPACE, "email");
      }

      if (var1.getUrl() != null) {
         var3.startTag(NAMESPACE, "url").text(var1.getUrl()).endTag(NAMESPACE, "url");
      }

      if (var1.getOrganization() != null) {
         var3.startTag(NAMESPACE, "organization").text(var1.getOrganization()).endTag(NAMESPACE, "organization");
      }

      if (var1.getOrganizationUrl() != null) {
         var3.startTag(NAMESPACE, "organizationUrl").text(var1.getOrganizationUrl()).endTag(NAMESPACE, "organizationUrl");
      }

      if (var1.getRoles() != null && var1.getRoles().size() > 0) {
         var3.startTag(NAMESPACE, "roles");

         for (String var5 : var1.getRoles()) {
            var3.startTag(NAMESPACE, "role").text(var5).endTag(NAMESPACE, "role");
         }

         var3.endTag(NAMESPACE, "roles");
      }

      if (var1.getTimezone() != null) {
         var3.startTag(NAMESPACE, "timezone").text(var1.getTimezone()).endTag(NAMESPACE, "timezone");
      }

      if (var1.getProperties() != null && var1.getProperties().size() > 0) {
         var3.startTag(NAMESPACE, "properties");

         for (String var8 : var1.getProperties().keySet()) {
            String var6 = (String)var1.getProperties().get(var8);
            var3.startTag(NAMESPACE, var8).text(var6).endTag(NAMESPACE, var8);
         }

         var3.endTag(NAMESPACE, "properties");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeDependency(Dependency var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getGroupId() != null) {
         var3.startTag(NAMESPACE, "groupId").text(var1.getGroupId()).endTag(NAMESPACE, "groupId");
      }

      if (var1.getArtifactId() != null) {
         var3.startTag(NAMESPACE, "artifactId").text(var1.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      if (var1.getVersion() != null) {
         var3.startTag(NAMESPACE, "version").text(var1.getVersion()).endTag(NAMESPACE, "version");
      }

      if (var1.getType() != null && !var1.getType().equals("jar")) {
         var3.startTag(NAMESPACE, "type").text(var1.getType()).endTag(NAMESPACE, "type");
      }

      if (var1.getClassifier() != null) {
         var3.startTag(NAMESPACE, "classifier").text(var1.getClassifier()).endTag(NAMESPACE, "classifier");
      }

      if (var1.getScope() != null) {
         var3.startTag(NAMESPACE, "scope").text(var1.getScope()).endTag(NAMESPACE, "scope");
      }

      if (var1.getSystemPath() != null) {
         var3.startTag(NAMESPACE, "systemPath").text(var1.getSystemPath()).endTag(NAMESPACE, "systemPath");
      }

      if (var1.getExclusions() != null && var1.getExclusions().size() > 0) {
         var3.startTag(NAMESPACE, "exclusions");

         for (Exclusion var5 : var1.getExclusions()) {
            this.writeExclusion(var5, "exclusion", var3);
         }

         var3.endTag(NAMESPACE, "exclusions");
      }

      if (var1.getOptional() != null) {
         var3.startTag(NAMESPACE, "optional").text(var1.getOptional()).endTag(NAMESPACE, "optional");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeDependencyManagement(DependencyManagement var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getDependencies() != null && var1.getDependencies().size() > 0) {
         var3.startTag(NAMESPACE, "dependencies");

         for (Dependency var5 : var1.getDependencies()) {
            this.writeDependency(var5, "dependency", var3);
         }

         var3.endTag(NAMESPACE, "dependencies");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeDeploymentRepository(DeploymentRepository var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (!var1.isUniqueVersion()) {
         var3.startTag(NAMESPACE, "uniqueVersion").text(String.valueOf(var1.isUniqueVersion())).endTag(NAMESPACE, "uniqueVersion");
      }

      if (var1.getReleases() != null) {
         this.writeRepositoryPolicy(var1.getReleases(), "releases", var3);
      }

      if (var1.getSnapshots() != null) {
         this.writeRepositoryPolicy(var1.getSnapshots(), "snapshots", var3);
      }

      if (var1.getId() != null) {
         var3.startTag(NAMESPACE, "id").text(var1.getId()).endTag(NAMESPACE, "id");
      }

      if (var1.getName() != null) {
         var3.startTag(NAMESPACE, "name").text(var1.getName()).endTag(NAMESPACE, "name");
      }

      if (var1.getUrl() != null) {
         var3.startTag(NAMESPACE, "url").text(var1.getUrl()).endTag(NAMESPACE, "url");
      }

      if (var1.getLayout() != null && !var1.getLayout().equals("default")) {
         var3.startTag(NAMESPACE, "layout").text(var1.getLayout()).endTag(NAMESPACE, "layout");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeDeveloper(Developer var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getId() != null) {
         var3.startTag(NAMESPACE, "id").text(var1.getId()).endTag(NAMESPACE, "id");
      }

      if (var1.getName() != null) {
         var3.startTag(NAMESPACE, "name").text(var1.getName()).endTag(NAMESPACE, "name");
      }

      if (var1.getEmail() != null) {
         var3.startTag(NAMESPACE, "email").text(var1.getEmail()).endTag(NAMESPACE, "email");
      }

      if (var1.getUrl() != null) {
         var3.startTag(NAMESPACE, "url").text(var1.getUrl()).endTag(NAMESPACE, "url");
      }

      if (var1.getOrganization() != null) {
         var3.startTag(NAMESPACE, "organization").text(var1.getOrganization()).endTag(NAMESPACE, "organization");
      }

      if (var1.getOrganizationUrl() != null) {
         var3.startTag(NAMESPACE, "organizationUrl").text(var1.getOrganizationUrl()).endTag(NAMESPACE, "organizationUrl");
      }

      if (var1.getRoles() != null && var1.getRoles().size() > 0) {
         var3.startTag(NAMESPACE, "roles");

         for (String var5 : var1.getRoles()) {
            var3.startTag(NAMESPACE, "role").text(var5).endTag(NAMESPACE, "role");
         }

         var3.endTag(NAMESPACE, "roles");
      }

      if (var1.getTimezone() != null) {
         var3.startTag(NAMESPACE, "timezone").text(var1.getTimezone()).endTag(NAMESPACE, "timezone");
      }

      if (var1.getProperties() != null && var1.getProperties().size() > 0) {
         var3.startTag(NAMESPACE, "properties");

         for (String var8 : var1.getProperties().keySet()) {
            String var6 = (String)var1.getProperties().get(var8);
            var3.startTag(NAMESPACE, var8).text(var6).endTag(NAMESPACE, var8);
         }

         var3.endTag(NAMESPACE, "properties");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeDistributionManagement(DistributionManagement var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getRepository() != null) {
         this.writeDeploymentRepository(var1.getRepository(), "repository", var3);
      }

      if (var1.getSnapshotRepository() != null) {
         this.writeDeploymentRepository(var1.getSnapshotRepository(), "snapshotRepository", var3);
      }

      if (var1.getSite() != null) {
         this.writeSite(var1.getSite(), "site", var3);
      }

      if (var1.getDownloadUrl() != null) {
         var3.startTag(NAMESPACE, "downloadUrl").text(var1.getDownloadUrl()).endTag(NAMESPACE, "downloadUrl");
      }

      if (var1.getRelocation() != null) {
         this.writeRelocation(var1.getRelocation(), "relocation", var3);
      }

      if (var1.getStatus() != null) {
         var3.startTag(NAMESPACE, "status").text(var1.getStatus()).endTag(NAMESPACE, "status");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeExclusion(Exclusion var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getGroupId() != null) {
         var3.startTag(NAMESPACE, "groupId").text(var1.getGroupId()).endTag(NAMESPACE, "groupId");
      }

      if (var1.getArtifactId() != null) {
         var3.startTag(NAMESPACE, "artifactId").text(var1.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeExtension(Extension var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getGroupId() != null) {
         var3.startTag(NAMESPACE, "groupId").text(var1.getGroupId()).endTag(NAMESPACE, "groupId");
      }

      if (var1.getArtifactId() != null) {
         var3.startTag(NAMESPACE, "artifactId").text(var1.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      if (var1.getVersion() != null) {
         var3.startTag(NAMESPACE, "version").text(var1.getVersion()).endTag(NAMESPACE, "version");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeFileSet(FileSet var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getDirectory() != null) {
         var3.startTag(NAMESPACE, "directory").text(var1.getDirectory()).endTag(NAMESPACE, "directory");
      }

      if (var1.getIncludes() != null && var1.getIncludes().size() > 0) {
         var3.startTag(NAMESPACE, "includes");

         for (String var5 : var1.getIncludes()) {
            var3.startTag(NAMESPACE, "include").text(var5).endTag(NAMESPACE, "include");
         }

         var3.endTag(NAMESPACE, "includes");
      }

      if (var1.getExcludes() != null && var1.getExcludes().size() > 0) {
         var3.startTag(NAMESPACE, "excludes");

         for (String var7 : var1.getExcludes()) {
            var3.startTag(NAMESPACE, "exclude").text(var7).endTag(NAMESPACE, "exclude");
         }

         var3.endTag(NAMESPACE, "excludes");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeIssueManagement(IssueManagement var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getSystem() != null) {
         var3.startTag(NAMESPACE, "system").text(var1.getSystem()).endTag(NAMESPACE, "system");
      }

      if (var1.getUrl() != null) {
         var3.startTag(NAMESPACE, "url").text(var1.getUrl()).endTag(NAMESPACE, "url");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeLicense(License var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getName() != null) {
         var3.startTag(NAMESPACE, "name").text(var1.getName()).endTag(NAMESPACE, "name");
      }

      if (var1.getUrl() != null) {
         var3.startTag(NAMESPACE, "url").text(var1.getUrl()).endTag(NAMESPACE, "url");
      }

      if (var1.getDistribution() != null) {
         var3.startTag(NAMESPACE, "distribution").text(var1.getDistribution()).endTag(NAMESPACE, "distribution");
      }

      if (var1.getComments() != null) {
         var3.startTag(NAMESPACE, "comments").text(var1.getComments()).endTag(NAMESPACE, "comments");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeMailingList(MailingList var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getName() != null) {
         var3.startTag(NAMESPACE, "name").text(var1.getName()).endTag(NAMESPACE, "name");
      }

      if (var1.getSubscribe() != null) {
         var3.startTag(NAMESPACE, "subscribe").text(var1.getSubscribe()).endTag(NAMESPACE, "subscribe");
      }

      if (var1.getUnsubscribe() != null) {
         var3.startTag(NAMESPACE, "unsubscribe").text(var1.getUnsubscribe()).endTag(NAMESPACE, "unsubscribe");
      }

      if (var1.getPost() != null) {
         var3.startTag(NAMESPACE, "post").text(var1.getPost()).endTag(NAMESPACE, "post");
      }

      if (var1.getArchive() != null) {
         var3.startTag(NAMESPACE, "archive").text(var1.getArchive()).endTag(NAMESPACE, "archive");
      }

      if (var1.getOtherArchives() != null && var1.getOtherArchives().size() > 0) {
         var3.startTag(NAMESPACE, "otherArchives");

         for (String var5 : var1.getOtherArchives()) {
            var3.startTag(NAMESPACE, "otherArchive").text(var5).endTag(NAMESPACE, "otherArchive");
         }

         var3.endTag(NAMESPACE, "otherArchives");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeModel(Model var1, String var2, XmlSerializer var3) throws IOException {
      if (this.fileComment != null) {
         var3.comment(this.fileComment);
      }

      var3.setPrefix("", "http://maven.apache.org/POM/4.0.0");
      var3.setPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      var3.startTag(NAMESPACE, var2);
      var3.attribute("", "xsi:schemaLocation", "http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd");
      if (var1.getChildProjectUrlInheritAppendPath() != null) {
         var3.attribute(NAMESPACE, "child.project.url.inherit.append.path", var1.getChildProjectUrlInheritAppendPath());
      }

      if (var1.getModelVersion() != null) {
         var3.startTag(NAMESPACE, "modelVersion").text(var1.getModelVersion()).endTag(NAMESPACE, "modelVersion");
      }

      if (var1.getParent() != null) {
         this.writeParent(var1.getParent(), "parent", var3);
      }

      if (var1.getGroupId() != null) {
         var3.startTag(NAMESPACE, "groupId").text(var1.getGroupId()).endTag(NAMESPACE, "groupId");
      }

      if (var1.getArtifactId() != null) {
         var3.startTag(NAMESPACE, "artifactId").text(var1.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      if (var1.getVersion() != null) {
         var3.startTag(NAMESPACE, "version").text(var1.getVersion()).endTag(NAMESPACE, "version");
      }

      if (var1.getPackaging() != null && !var1.getPackaging().equals("jar")) {
         var3.startTag(NAMESPACE, "packaging").text(var1.getPackaging()).endTag(NAMESPACE, "packaging");
      }

      if (var1.getName() != null) {
         var3.startTag(NAMESPACE, "name").text(var1.getName()).endTag(NAMESPACE, "name");
      }

      if (var1.getDescription() != null) {
         var3.startTag(NAMESPACE, "description").text(var1.getDescription()).endTag(NAMESPACE, "description");
      }

      if (var1.getUrl() != null) {
         var3.startTag(NAMESPACE, "url").text(var1.getUrl()).endTag(NAMESPACE, "url");
      }

      if (var1.getInceptionYear() != null) {
         var3.startTag(NAMESPACE, "inceptionYear").text(var1.getInceptionYear()).endTag(NAMESPACE, "inceptionYear");
      }

      if (var1.getOrganization() != null) {
         this.writeOrganization(var1.getOrganization(), "organization", var3);
      }

      if (var1.getLicenses() != null && var1.getLicenses().size() > 0) {
         var3.startTag(NAMESPACE, "licenses");

         for (License var5 : var1.getLicenses()) {
            this.writeLicense(var5, "license", var3);
         }

         var3.endTag(NAMESPACE, "licenses");
      }

      if (var1.getDevelopers() != null && var1.getDevelopers().size() > 0) {
         var3.startTag(NAMESPACE, "developers");

         for (Developer var16 : var1.getDevelopers()) {
            this.writeDeveloper(var16, "developer", var3);
         }

         var3.endTag(NAMESPACE, "developers");
      }

      if (var1.getContributors() != null && var1.getContributors().size() > 0) {
         var3.startTag(NAMESPACE, "contributors");

         for (Contributor var17 : var1.getContributors()) {
            this.writeContributor(var17, "contributor", var3);
         }

         var3.endTag(NAMESPACE, "contributors");
      }

      if (var1.getMailingLists() != null && var1.getMailingLists().size() > 0) {
         var3.startTag(NAMESPACE, "mailingLists");

         for (MailingList var18 : var1.getMailingLists()) {
            this.writeMailingList(var18, "mailingList", var3);
         }

         var3.endTag(NAMESPACE, "mailingLists");
      }

      if (var1.getPrerequisites() != null) {
         this.writePrerequisites(var1.getPrerequisites(), "prerequisites", var3);
      }

      if (var1.getModules() != null && var1.getModules().size() > 0) {
         var3.startTag(NAMESPACE, "modules");

         for (String var19 : var1.getModules()) {
            var3.startTag(NAMESPACE, "module").text(var19).endTag(NAMESPACE, "module");
         }

         var3.endTag(NAMESPACE, "modules");
      }

      if (var1.getScm() != null) {
         this.writeScm(var1.getScm(), "scm", var3);
      }

      if (var1.getIssueManagement() != null) {
         this.writeIssueManagement(var1.getIssueManagement(), "issueManagement", var3);
      }

      if (var1.getCiManagement() != null) {
         this.writeCiManagement(var1.getCiManagement(), "ciManagement", var3);
      }

      if (var1.getDistributionManagement() != null) {
         this.writeDistributionManagement(var1.getDistributionManagement(), "distributionManagement", var3);
      }

      if (var1.getProperties() != null && var1.getProperties().size() > 0) {
         var3.startTag(NAMESPACE, "properties");

         for (String var20 : var1.getProperties().keySet()) {
            String var6 = (String)var1.getProperties().get(var20);
            var3.startTag(NAMESPACE, var20).text(var6).endTag(NAMESPACE, var20);
         }

         var3.endTag(NAMESPACE, "properties");
      }

      if (var1.getDependencyManagement() != null) {
         this.writeDependencyManagement(var1.getDependencyManagement(), "dependencyManagement", var3);
      }

      if (var1.getDependencies() != null && var1.getDependencies().size() > 0) {
         var3.startTag(NAMESPACE, "dependencies");

         for (Dependency var21 : var1.getDependencies()) {
            this.writeDependency(var21, "dependency", var3);
         }

         var3.endTag(NAMESPACE, "dependencies");
      }

      if (var1.getRepositories() != null && var1.getRepositories().size() > 0) {
         var3.startTag(NAMESPACE, "repositories");

         for (Repository var22 : var1.getRepositories()) {
            this.writeRepository(var22, "repository", var3);
         }

         var3.endTag(NAMESPACE, "repositories");
      }

      if (var1.getPluginRepositories() != null && var1.getPluginRepositories().size() > 0) {
         var3.startTag(NAMESPACE, "pluginRepositories");

         for (Repository var23 : var1.getPluginRepositories()) {
            this.writeRepository(var23, "pluginRepository", var3);
         }

         var3.endTag(NAMESPACE, "pluginRepositories");
      }

      if (var1.getBuild() != null) {
         this.writeBuild(var1.getBuild(), "build", var3);
      }

      if (var1.getReports() != null) {
         ((Xpp3Dom)var1.getReports()).writeToSerializer(NAMESPACE, var3);
      }

      if (var1.getReporting() != null) {
         this.writeReporting(var1.getReporting(), "reporting", var3);
      }

      if (var1.getProfiles() != null && var1.getProfiles().size() > 0) {
         var3.startTag(NAMESPACE, "profiles");

         for (Profile var24 : var1.getProfiles()) {
            this.writeProfile(var24, "profile", var3);
         }

         var3.endTag(NAMESPACE, "profiles");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeModelBase(ModelBase var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getModules() != null && var1.getModules().size() > 0) {
         var3.startTag(NAMESPACE, "modules");

         for (String var5 : var1.getModules()) {
            var3.startTag(NAMESPACE, "module").text(var5).endTag(NAMESPACE, "module");
         }

         var3.endTag(NAMESPACE, "modules");
      }

      if (var1.getDistributionManagement() != null) {
         this.writeDistributionManagement(var1.getDistributionManagement(), "distributionManagement", var3);
      }

      if (var1.getProperties() != null && var1.getProperties().size() > 0) {
         var3.startTag(NAMESPACE, "properties");

         for (String var11 : var1.getProperties().keySet()) {
            String var6 = (String)var1.getProperties().get(var11);
            var3.startTag(NAMESPACE, var11).text(var6).endTag(NAMESPACE, var11);
         }

         var3.endTag(NAMESPACE, "properties");
      }

      if (var1.getDependencyManagement() != null) {
         this.writeDependencyManagement(var1.getDependencyManagement(), "dependencyManagement", var3);
      }

      if (var1.getDependencies() != null && var1.getDependencies().size() > 0) {
         var3.startTag(NAMESPACE, "dependencies");

         for (Dependency var12 : var1.getDependencies()) {
            this.writeDependency(var12, "dependency", var3);
         }

         var3.endTag(NAMESPACE, "dependencies");
      }

      if (var1.getRepositories() != null && var1.getRepositories().size() > 0) {
         var3.startTag(NAMESPACE, "repositories");

         for (Repository var13 : var1.getRepositories()) {
            this.writeRepository(var13, "repository", var3);
         }

         var3.endTag(NAMESPACE, "repositories");
      }

      if (var1.getPluginRepositories() != null && var1.getPluginRepositories().size() > 0) {
         var3.startTag(NAMESPACE, "pluginRepositories");

         for (Repository var14 : var1.getPluginRepositories()) {
            this.writeRepository(var14, "pluginRepository", var3);
         }

         var3.endTag(NAMESPACE, "pluginRepositories");
      }

      if (var1.getReports() != null) {
         ((Xpp3Dom)var1.getReports()).writeToSerializer(NAMESPACE, var3);
      }

      if (var1.getReporting() != null) {
         this.writeReporting(var1.getReporting(), "reporting", var3);
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeNotifier(Notifier var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getType() != null && !var1.getType().equals("mail")) {
         var3.startTag(NAMESPACE, "type").text(var1.getType()).endTag(NAMESPACE, "type");
      }

      if (!var1.isSendOnError()) {
         var3.startTag(NAMESPACE, "sendOnError").text(String.valueOf(var1.isSendOnError())).endTag(NAMESPACE, "sendOnError");
      }

      if (!var1.isSendOnFailure()) {
         var3.startTag(NAMESPACE, "sendOnFailure").text(String.valueOf(var1.isSendOnFailure())).endTag(NAMESPACE, "sendOnFailure");
      }

      if (!var1.isSendOnSuccess()) {
         var3.startTag(NAMESPACE, "sendOnSuccess").text(String.valueOf(var1.isSendOnSuccess())).endTag(NAMESPACE, "sendOnSuccess");
      }

      if (!var1.isSendOnWarning()) {
         var3.startTag(NAMESPACE, "sendOnWarning").text(String.valueOf(var1.isSendOnWarning())).endTag(NAMESPACE, "sendOnWarning");
      }

      if (var1.getAddress() != null) {
         var3.startTag(NAMESPACE, "address").text(var1.getAddress()).endTag(NAMESPACE, "address");
      }

      if (var1.getConfiguration() != null && var1.getConfiguration().size() > 0) {
         var3.startTag(NAMESPACE, "configuration");

         for (String var5 : var1.getConfiguration().keySet()) {
            String var6 = (String)var1.getConfiguration().get(var5);
            var3.startTag(NAMESPACE, var5).text(var6).endTag(NAMESPACE, var5);
         }

         var3.endTag(NAMESPACE, "configuration");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeOrganization(Organization var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getName() != null) {
         var3.startTag(NAMESPACE, "name").text(var1.getName()).endTag(NAMESPACE, "name");
      }

      if (var1.getUrl() != null) {
         var3.startTag(NAMESPACE, "url").text(var1.getUrl()).endTag(NAMESPACE, "url");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeParent(Parent var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getGroupId() != null) {
         var3.startTag(NAMESPACE, "groupId").text(var1.getGroupId()).endTag(NAMESPACE, "groupId");
      }

      if (var1.getArtifactId() != null) {
         var3.startTag(NAMESPACE, "artifactId").text(var1.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      if (var1.getVersion() != null) {
         var3.startTag(NAMESPACE, "version").text(var1.getVersion()).endTag(NAMESPACE, "version");
      }

      if (var1.getRelativePath() != null && !var1.getRelativePath().equals("../pom.xml")) {
         var3.startTag(NAMESPACE, "relativePath").text(var1.getRelativePath()).endTag(NAMESPACE, "relativePath");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writePatternSet(PatternSet var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getIncludes() != null && var1.getIncludes().size() > 0) {
         var3.startTag(NAMESPACE, "includes");

         for (String var5 : var1.getIncludes()) {
            var3.startTag(NAMESPACE, "include").text(var5).endTag(NAMESPACE, "include");
         }

         var3.endTag(NAMESPACE, "includes");
      }

      if (var1.getExcludes() != null && var1.getExcludes().size() > 0) {
         var3.startTag(NAMESPACE, "excludes");

         for (String var7 : var1.getExcludes()) {
            var3.startTag(NAMESPACE, "exclude").text(var7).endTag(NAMESPACE, "exclude");
         }

         var3.endTag(NAMESPACE, "excludes");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writePlugin(Plugin var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getGroupId() != null && !var1.getGroupId().equals("org.apache.maven.plugins")) {
         var3.startTag(NAMESPACE, "groupId").text(var1.getGroupId()).endTag(NAMESPACE, "groupId");
      }

      if (var1.getArtifactId() != null) {
         var3.startTag(NAMESPACE, "artifactId").text(var1.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      if (var1.getVersion() != null) {
         var3.startTag(NAMESPACE, "version").text(var1.getVersion()).endTag(NAMESPACE, "version");
      }

      if (var1.getExtensions() != null) {
         var3.startTag(NAMESPACE, "extensions").text(var1.getExtensions()).endTag(NAMESPACE, "extensions");
      }

      if (var1.getExecutions() != null && var1.getExecutions().size() > 0) {
         var3.startTag(NAMESPACE, "executions");

         for (PluginExecution var5 : var1.getExecutions()) {
            this.writePluginExecution(var5, "execution", var3);
         }

         var3.endTag(NAMESPACE, "executions");
      }

      if (var1.getDependencies() != null && var1.getDependencies().size() > 0) {
         var3.startTag(NAMESPACE, "dependencies");

         for (Dependency var7 : var1.getDependencies()) {
            this.writeDependency(var7, "dependency", var3);
         }

         var3.endTag(NAMESPACE, "dependencies");
      }

      if (var1.getGoals() != null) {
         ((Xpp3Dom)var1.getGoals()).writeToSerializer(NAMESPACE, var3);
      }

      if (var1.getInherited() != null) {
         var3.startTag(NAMESPACE, "inherited").text(var1.getInherited()).endTag(NAMESPACE, "inherited");
      }

      if (var1.getConfiguration() != null) {
         ((Xpp3Dom)var1.getConfiguration()).writeToSerializer(NAMESPACE, var3);
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writePluginConfiguration(PluginConfiguration var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getPluginManagement() != null) {
         this.writePluginManagement(var1.getPluginManagement(), "pluginManagement", var3);
      }

      if (var1.getPlugins() != null && var1.getPlugins().size() > 0) {
         var3.startTag(NAMESPACE, "plugins");

         for (Plugin var5 : var1.getPlugins()) {
            this.writePlugin(var5, "plugin", var3);
         }

         var3.endTag(NAMESPACE, "plugins");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writePluginContainer(PluginContainer var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getPlugins() != null && var1.getPlugins().size() > 0) {
         var3.startTag(NAMESPACE, "plugins");

         for (Plugin var5 : var1.getPlugins()) {
            this.writePlugin(var5, "plugin", var3);
         }

         var3.endTag(NAMESPACE, "plugins");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writePluginExecution(PluginExecution var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getId() != null && !var1.getId().equals("default")) {
         var3.startTag(NAMESPACE, "id").text(var1.getId()).endTag(NAMESPACE, "id");
      }

      if (var1.getPhase() != null) {
         var3.startTag(NAMESPACE, "phase").text(var1.getPhase()).endTag(NAMESPACE, "phase");
      }

      if (var1.getGoals() != null && var1.getGoals().size() > 0) {
         var3.startTag(NAMESPACE, "goals");

         for (String var5 : var1.getGoals()) {
            var3.startTag(NAMESPACE, "goal").text(var5).endTag(NAMESPACE, "goal");
         }

         var3.endTag(NAMESPACE, "goals");
      }

      if (var1.getInherited() != null) {
         var3.startTag(NAMESPACE, "inherited").text(var1.getInherited()).endTag(NAMESPACE, "inherited");
      }

      if (var1.getConfiguration() != null) {
         ((Xpp3Dom)var1.getConfiguration()).writeToSerializer(NAMESPACE, var3);
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writePluginManagement(PluginManagement var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getPlugins() != null && var1.getPlugins().size() > 0) {
         var3.startTag(NAMESPACE, "plugins");

         for (Plugin var5 : var1.getPlugins()) {
            this.writePlugin(var5, "plugin", var3);
         }

         var3.endTag(NAMESPACE, "plugins");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writePrerequisites(Prerequisites var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getMaven() != null && !var1.getMaven().equals("2.0")) {
         var3.startTag(NAMESPACE, "maven").text(var1.getMaven()).endTag(NAMESPACE, "maven");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeProfile(Profile var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getId() != null && !var1.getId().equals("default")) {
         var3.startTag(NAMESPACE, "id").text(var1.getId()).endTag(NAMESPACE, "id");
      }

      if (var1.getActivation() != null) {
         this.writeActivation(var1.getActivation(), "activation", var3);
      }

      if (var1.getBuild() != null) {
         this.writeBuildBase(var1.getBuild(), "build", var3);
      }

      if (var1.getModules() != null && var1.getModules().size() > 0) {
         var3.startTag(NAMESPACE, "modules");

         for (String var5 : var1.getModules()) {
            var3.startTag(NAMESPACE, "module").text(var5).endTag(NAMESPACE, "module");
         }

         var3.endTag(NAMESPACE, "modules");
      }

      if (var1.getDistributionManagement() != null) {
         this.writeDistributionManagement(var1.getDistributionManagement(), "distributionManagement", var3);
      }

      if (var1.getProperties() != null && var1.getProperties().size() > 0) {
         var3.startTag(NAMESPACE, "properties");

         for (String var11 : var1.getProperties().keySet()) {
            String var6 = (String)var1.getProperties().get(var11);
            var3.startTag(NAMESPACE, var11).text(var6).endTag(NAMESPACE, var11);
         }

         var3.endTag(NAMESPACE, "properties");
      }

      if (var1.getDependencyManagement() != null) {
         this.writeDependencyManagement(var1.getDependencyManagement(), "dependencyManagement", var3);
      }

      if (var1.getDependencies() != null && var1.getDependencies().size() > 0) {
         var3.startTag(NAMESPACE, "dependencies");

         for (Dependency var12 : var1.getDependencies()) {
            this.writeDependency(var12, "dependency", var3);
         }

         var3.endTag(NAMESPACE, "dependencies");
      }

      if (var1.getRepositories() != null && var1.getRepositories().size() > 0) {
         var3.startTag(NAMESPACE, "repositories");

         for (Repository var13 : var1.getRepositories()) {
            this.writeRepository(var13, "repository", var3);
         }

         var3.endTag(NAMESPACE, "repositories");
      }

      if (var1.getPluginRepositories() != null && var1.getPluginRepositories().size() > 0) {
         var3.startTag(NAMESPACE, "pluginRepositories");

         for (Repository var14 : var1.getPluginRepositories()) {
            this.writeRepository(var14, "pluginRepository", var3);
         }

         var3.endTag(NAMESPACE, "pluginRepositories");
      }

      if (var1.getReports() != null) {
         ((Xpp3Dom)var1.getReports()).writeToSerializer(NAMESPACE, var3);
      }

      if (var1.getReporting() != null) {
         this.writeReporting(var1.getReporting(), "reporting", var3);
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeRelocation(Relocation var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getGroupId() != null) {
         var3.startTag(NAMESPACE, "groupId").text(var1.getGroupId()).endTag(NAMESPACE, "groupId");
      }

      if (var1.getArtifactId() != null) {
         var3.startTag(NAMESPACE, "artifactId").text(var1.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      if (var1.getVersion() != null) {
         var3.startTag(NAMESPACE, "version").text(var1.getVersion()).endTag(NAMESPACE, "version");
      }

      if (var1.getMessage() != null) {
         var3.startTag(NAMESPACE, "message").text(var1.getMessage()).endTag(NAMESPACE, "message");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeReportPlugin(ReportPlugin var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getGroupId() != null && !var1.getGroupId().equals("org.apache.maven.plugins")) {
         var3.startTag(NAMESPACE, "groupId").text(var1.getGroupId()).endTag(NAMESPACE, "groupId");
      }

      if (var1.getArtifactId() != null) {
         var3.startTag(NAMESPACE, "artifactId").text(var1.getArtifactId()).endTag(NAMESPACE, "artifactId");
      }

      if (var1.getVersion() != null) {
         var3.startTag(NAMESPACE, "version").text(var1.getVersion()).endTag(NAMESPACE, "version");
      }

      if (var1.getReportSets() != null && var1.getReportSets().size() > 0) {
         var3.startTag(NAMESPACE, "reportSets");

         for (ReportSet var5 : var1.getReportSets()) {
            this.writeReportSet(var5, "reportSet", var3);
         }

         var3.endTag(NAMESPACE, "reportSets");
      }

      if (var1.getInherited() != null) {
         var3.startTag(NAMESPACE, "inherited").text(var1.getInherited()).endTag(NAMESPACE, "inherited");
      }

      if (var1.getConfiguration() != null) {
         ((Xpp3Dom)var1.getConfiguration()).writeToSerializer(NAMESPACE, var3);
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeReportSet(ReportSet var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getId() != null && !var1.getId().equals("default")) {
         var3.startTag(NAMESPACE, "id").text(var1.getId()).endTag(NAMESPACE, "id");
      }

      if (var1.getReports() != null && var1.getReports().size() > 0) {
         var3.startTag(NAMESPACE, "reports");

         for (String var5 : var1.getReports()) {
            var3.startTag(NAMESPACE, "report").text(var5).endTag(NAMESPACE, "report");
         }

         var3.endTag(NAMESPACE, "reports");
      }

      if (var1.getInherited() != null) {
         var3.startTag(NAMESPACE, "inherited").text(var1.getInherited()).endTag(NAMESPACE, "inherited");
      }

      if (var1.getConfiguration() != null) {
         ((Xpp3Dom)var1.getConfiguration()).writeToSerializer(NAMESPACE, var3);
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeReporting(Reporting var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getExcludeDefaults() != null) {
         var3.startTag(NAMESPACE, "excludeDefaults").text(var1.getExcludeDefaults()).endTag(NAMESPACE, "excludeDefaults");
      }

      if (var1.getOutputDirectory() != null) {
         var3.startTag(NAMESPACE, "outputDirectory").text(var1.getOutputDirectory()).endTag(NAMESPACE, "outputDirectory");
      }

      if (var1.getPlugins() != null && var1.getPlugins().size() > 0) {
         var3.startTag(NAMESPACE, "plugins");

         for (ReportPlugin var5 : var1.getPlugins()) {
            this.writeReportPlugin(var5, "plugin", var3);
         }

         var3.endTag(NAMESPACE, "plugins");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeRepository(Repository var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getReleases() != null) {
         this.writeRepositoryPolicy(var1.getReleases(), "releases", var3);
      }

      if (var1.getSnapshots() != null) {
         this.writeRepositoryPolicy(var1.getSnapshots(), "snapshots", var3);
      }

      if (var1.getId() != null) {
         var3.startTag(NAMESPACE, "id").text(var1.getId()).endTag(NAMESPACE, "id");
      }

      if (var1.getName() != null) {
         var3.startTag(NAMESPACE, "name").text(var1.getName()).endTag(NAMESPACE, "name");
      }

      if (var1.getUrl() != null) {
         var3.startTag(NAMESPACE, "url").text(var1.getUrl()).endTag(NAMESPACE, "url");
      }

      if (var1.getLayout() != null && !var1.getLayout().equals("default")) {
         var3.startTag(NAMESPACE, "layout").text(var1.getLayout()).endTag(NAMESPACE, "layout");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeRepositoryBase(RepositoryBase var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getId() != null) {
         var3.startTag(NAMESPACE, "id").text(var1.getId()).endTag(NAMESPACE, "id");
      }

      if (var1.getName() != null) {
         var3.startTag(NAMESPACE, "name").text(var1.getName()).endTag(NAMESPACE, "name");
      }

      if (var1.getUrl() != null) {
         var3.startTag(NAMESPACE, "url").text(var1.getUrl()).endTag(NAMESPACE, "url");
      }

      if (var1.getLayout() != null && !var1.getLayout().equals("default")) {
         var3.startTag(NAMESPACE, "layout").text(var1.getLayout()).endTag(NAMESPACE, "layout");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeRepositoryPolicy(RepositoryPolicy var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getEnabled() != null) {
         var3.startTag(NAMESPACE, "enabled").text(var1.getEnabled()).endTag(NAMESPACE, "enabled");
      }

      if (var1.getUpdatePolicy() != null) {
         var3.startTag(NAMESPACE, "updatePolicy").text(var1.getUpdatePolicy()).endTag(NAMESPACE, "updatePolicy");
      }

      if (var1.getChecksumPolicy() != null) {
         var3.startTag(NAMESPACE, "checksumPolicy").text(var1.getChecksumPolicy()).endTag(NAMESPACE, "checksumPolicy");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeResource(Resource var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getTargetPath() != null) {
         var3.startTag(NAMESPACE, "targetPath").text(var1.getTargetPath()).endTag(NAMESPACE, "targetPath");
      }

      if (var1.getFiltering() != null) {
         var3.startTag(NAMESPACE, "filtering").text(var1.getFiltering()).endTag(NAMESPACE, "filtering");
      }

      if (var1.getDirectory() != null) {
         var3.startTag(NAMESPACE, "directory").text(var1.getDirectory()).endTag(NAMESPACE, "directory");
      }

      if (var1.getIncludes() != null && var1.getIncludes().size() > 0) {
         var3.startTag(NAMESPACE, "includes");

         for (String var5 : var1.getIncludes()) {
            var3.startTag(NAMESPACE, "include").text(var5).endTag(NAMESPACE, "include");
         }

         var3.endTag(NAMESPACE, "includes");
      }

      if (var1.getExcludes() != null && var1.getExcludes().size() > 0) {
         var3.startTag(NAMESPACE, "excludes");

         for (String var7 : var1.getExcludes()) {
            var3.startTag(NAMESPACE, "exclude").text(var7).endTag(NAMESPACE, "exclude");
         }

         var3.endTag(NAMESPACE, "excludes");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeScm(Scm var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getChildScmConnectionInheritAppendPath() != null) {
         var3.attribute(NAMESPACE, "child.scm.connection.inherit.append.path", var1.getChildScmConnectionInheritAppendPath());
      }

      if (var1.getChildScmDeveloperConnectionInheritAppendPath() != null) {
         var3.attribute(NAMESPACE, "child.scm.developerConnection.inherit.append.path", var1.getChildScmDeveloperConnectionInheritAppendPath());
      }

      if (var1.getChildScmUrlInheritAppendPath() != null) {
         var3.attribute(NAMESPACE, "child.scm.url.inherit.append.path", var1.getChildScmUrlInheritAppendPath());
      }

      if (var1.getConnection() != null) {
         var3.startTag(NAMESPACE, "connection").text(var1.getConnection()).endTag(NAMESPACE, "connection");
      }

      if (var1.getDeveloperConnection() != null) {
         var3.startTag(NAMESPACE, "developerConnection").text(var1.getDeveloperConnection()).endTag(NAMESPACE, "developerConnection");
      }

      if (var1.getTag() != null && !var1.getTag().equals("HEAD")) {
         var3.startTag(NAMESPACE, "tag").text(var1.getTag()).endTag(NAMESPACE, "tag");
      }

      if (var1.getUrl() != null) {
         var3.startTag(NAMESPACE, "url").text(var1.getUrl()).endTag(NAMESPACE, "url");
      }

      var3.endTag(NAMESPACE, var2);
   }

   private void writeSite(Site var1, String var2, XmlSerializer var3) throws IOException {
      var3.startTag(NAMESPACE, var2);
      if (var1.getChildSiteUrlInheritAppendPath() != null) {
         var3.attribute(NAMESPACE, "child.site.url.inherit.append.path", var1.getChildSiteUrlInheritAppendPath());
      }

      if (var1.getId() != null) {
         var3.startTag(NAMESPACE, "id").text(var1.getId()).endTag(NAMESPACE, "id");
      }

      if (var1.getName() != null) {
         var3.startTag(NAMESPACE, "name").text(var1.getName()).endTag(NAMESPACE, "name");
      }

      if (var1.getUrl() != null) {
         var3.startTag(NAMESPACE, "url").text(var1.getUrl()).endTag(NAMESPACE, "url");
      }

      var3.endTag(NAMESPACE, var2);
   }
}
