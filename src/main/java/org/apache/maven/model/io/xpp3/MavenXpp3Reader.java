package org.apache.maven.model.io.xpp3;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
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
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.EntityReplacementMap;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class MavenXpp3Reader {
   private boolean addDefaultEntities = true;
   public final MavenXpp3Reader.ContentTransformer contentTransformer;

   public MavenXpp3Reader() {
      this(new MavenXpp3Reader.ContentTransformer() {
         @Override
         public String transform(String var1, String var2) {
            return var1;
         }
      });
   }

   public MavenXpp3Reader(MavenXpp3Reader.ContentTransformer var1) {
      this.contentTransformer = var1;
   }

   private boolean checkFieldWithDuplicate(XmlPullParser var1, String var2, String var3, Set var4) throws XmlPullParserException {
      if (!var1.getName().equals(var2) && !var1.getName().equals(var3)) {
         return false;
      } else if (!var4.add(var2)) {
         throw new XmlPullParserException("Duplicated tag: '" + var2 + "'", var1, null);
      } else {
         return true;
      }
   }

   private void checkUnknownAttribute(XmlPullParser var1, String var2, String var3, boolean var4) throws XmlPullParserException, IOException {
      if (var4) {
         throw new XmlPullParserException("Unknown attribute '" + var2 + "' for tag '" + var3 + "'", var1, null);
      }
   }

   private void checkUnknownElement(XmlPullParser var1, boolean var2) throws XmlPullParserException, IOException {
      if (var2) {
         throw new XmlPullParserException("Unrecognised tag: '" + var1.getName() + "'", var1, null);
      } else {
         int var3 = 1;

         while (var3 > 0) {
            int var4 = var1.next();
            if (var4 == 2) {
               var3++;
            } else if (var4 == 3) {
               var3--;
            }
         }
      }
   }

   public boolean getAddDefaultEntities() {
      return this.addDefaultEntities;
   }

   private boolean getBooleanValue(String var1, String var2, XmlPullParser var3) throws XmlPullParserException {
      return this.getBooleanValue(var1, var2, var3, null);
   }

   private boolean getBooleanValue(String var1, String var2, XmlPullParser var3, String var4) throws XmlPullParserException {
      if (var1 != null && var1.length() != 0) {
         return Boolean.valueOf(var1);
      } else {
         return var4 != null ? Boolean.valueOf(var4) : false;
      }
   }

   private byte getByteValue(String var1, String var2, XmlPullParser var3, boolean var4) throws XmlPullParserException {
      if (var1 != null) {
         try {
            return Byte.valueOf(var1);
         } catch (NumberFormatException var6) {
            if (var4) {
               throw new XmlPullParserException("Unable to parse element '" + var2 + "', must be a byte", var3, var6);
            }
         }
      }

      return 0;
   }

   private char getCharacterValue(String var1, String var2, XmlPullParser var3) throws XmlPullParserException {
      return var1 != null ? var1.charAt(0) : '\u0000';
   }

   private Date getDateValue(String var1, String var2, XmlPullParser var3) throws XmlPullParserException {
      return this.getDateValue(var1, var2, null, var3);
   }

   private Date getDateValue(String var1, String var2, String var3, XmlPullParser var4) throws XmlPullParserException {
      if (var1 != null) {
         String var5 = var3;
         if (var3 == null) {
            var5 = "yyyy-MM-dd'T'HH:mm:ss.SSS";
         }

         if ("long".equals(var5)) {
            try {
               return new Date(Long.parseLong(var1));
            } catch (NumberFormatException var7) {
               throw new XmlPullParserException(var7.getMessage(), var4, var7);
            }
         } else {
            try {
               SimpleDateFormat var6 = new SimpleDateFormat(var5, Locale.US);
               return var6.parse(var1);
            } catch (ParseException var8) {
               throw new XmlPullParserException(var8.getMessage(), var4, var8);
            }
         }
      } else {
         return null;
      }
   }

   private double getDoubleValue(String var1, String var2, XmlPullParser var3, boolean var4) throws XmlPullParserException {
      if (var1 != null) {
         try {
            return Double.valueOf(var1);
         } catch (NumberFormatException var6) {
            if (var4) {
               throw new XmlPullParserException("Unable to parse element '" + var2 + "', must be a floating point number", var3, var6);
            }
         }
      }

      return 0.0;
   }

   private float getFloatValue(String var1, String var2, XmlPullParser var3, boolean var4) throws XmlPullParserException {
      if (var1 != null) {
         try {
            return Float.valueOf(var1);
         } catch (NumberFormatException var6) {
            if (var4) {
               throw new XmlPullParserException("Unable to parse element '" + var2 + "', must be a floating point number", var3, var6);
            }
         }
      }

      return 0.0F;
   }

   private int getIntegerValue(String var1, String var2, XmlPullParser var3, boolean var4) throws XmlPullParserException {
      if (var1 != null) {
         try {
            return Integer.valueOf(var1);
         } catch (NumberFormatException var6) {
            if (var4) {
               throw new XmlPullParserException("Unable to parse element '" + var2 + "', must be an integer", var3, var6);
            }
         }
      }

      return 0;
   }

   private long getLongValue(String var1, String var2, XmlPullParser var3, boolean var4) throws XmlPullParserException {
      if (var1 != null) {
         try {
            return Long.valueOf(var1);
         } catch (NumberFormatException var6) {
            if (var4) {
               throw new XmlPullParserException("Unable to parse element '" + var2 + "', must be a long integer", var3, var6);
            }
         }
      }

      return 0L;
   }

   private String getRequiredAttributeValue(String var1, String var2, XmlPullParser var3, boolean var4) throws XmlPullParserException {
      if (var1 == null && var4) {
         throw new XmlPullParserException("Missing required value for attribute '" + var2 + "'", var3, null);
      } else {
         return var1;
      }
   }

   private short getShortValue(String var1, String var2, XmlPullParser var3, boolean var4) throws XmlPullParserException {
      if (var1 != null) {
         try {
            return Short.valueOf(var1);
         } catch (NumberFormatException var6) {
            if (var4) {
               throw new XmlPullParserException("Unable to parse element '" + var2 + "', must be a short integer", var3, var6);
            }
         }
      }

      return 0;
   }

   private String getTrimmedValue(String var1) {
      if (var1 != null) {
         var1 = var1.trim();
      }

      return var1;
   }

   private String interpolatedTrimmed(String var1, String var2) {
      return this.getTrimmedValue(this.contentTransformer.transform(var1, var2));
   }

   private int nextTag(XmlPullParser var1) throws IOException, XmlPullParserException {
      int var2 = var1.next();
      if (var2 == 4) {
         var2 = var1.next();
      }

      if (var2 != 2 && var2 != 3) {
         throw new XmlPullParserException("expected START_TAG or END_TAG not " + XmlPullParser.TYPES[var2], var1, null);
      } else {
         return var2;
      }
   }

   public Model read(Reader var1, boolean var2) throws IOException, XmlPullParserException {
      MXParser var3 = this.addDefaultEntities ? new MXParser(EntityReplacementMap.defaultEntityReplacementMap) : new MXParser();
      var3.setInput(var1);
      return this.read(var3, var2);
   }

   public Model read(Reader var1) throws IOException, XmlPullParserException {
      return this.read(var1, true);
   }

   public Model read(InputStream var1, boolean var2) throws IOException, XmlPullParserException {
      return this.read(ReaderFactory.newXmlReader(var1), var2);
   }

   public Model read(InputStream var1) throws IOException, XmlPullParserException {
      return this.read(ReaderFactory.newXmlReader(var1));
   }

   private Activation parseActivation(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Activation var4 = new Activation();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "activeByDefault", null, var8)) {
            var4.setActiveByDefault(this.getBooleanValue(this.interpolatedTrimmed(var1.nextText(), "activeByDefault"), "activeByDefault", var1, "false"));
         } else if (this.checkFieldWithDuplicate(var1, "jdk", null, var8)) {
            var4.setJdk(this.interpolatedTrimmed(var1.nextText(), "jdk"));
         } else if (this.checkFieldWithDuplicate(var1, "os", null, var8)) {
            var4.setOs(this.parseActivationOS(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "property", null, var8)) {
            var4.setProperty(this.parseActivationProperty(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "file", null, var8)) {
            var4.setFile(this.parseActivationFile(var1, var2));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private ActivationFile parseActivationFile(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      ActivationFile var4 = new ActivationFile();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "missing", null, var8)) {
            var4.setMissing(this.interpolatedTrimmed(var1.nextText(), "missing"));
         } else if (this.checkFieldWithDuplicate(var1, "exists", null, var8)) {
            var4.setExists(this.interpolatedTrimmed(var1.nextText(), "exists"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private ActivationOS parseActivationOS(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      ActivationOS var4 = new ActivationOS();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "name", null, var8)) {
            var4.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "family", null, var8)) {
            var4.setFamily(this.interpolatedTrimmed(var1.nextText(), "family"));
         } else if (this.checkFieldWithDuplicate(var1, "arch", null, var8)) {
            var4.setArch(this.interpolatedTrimmed(var1.nextText(), "arch"));
         } else if (this.checkFieldWithDuplicate(var1, "version", null, var8)) {
            var4.setVersion(this.interpolatedTrimmed(var1.nextText(), "version"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private ActivationProperty parseActivationProperty(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      ActivationProperty var4 = new ActivationProperty();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "name", null, var8)) {
            var4.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "value", null, var8)) {
            var4.setValue(this.interpolatedTrimmed(var1.nextText(), "value"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Build parseBuild(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Build var4 = new Build();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "sourceDirectory", null, var8)) {
            var4.setSourceDirectory(this.interpolatedTrimmed(var1.nextText(), "sourceDirectory"));
         } else if (this.checkFieldWithDuplicate(var1, "scriptSourceDirectory", null, var8)) {
            var4.setScriptSourceDirectory(this.interpolatedTrimmed(var1.nextText(), "scriptSourceDirectory"));
         } else if (this.checkFieldWithDuplicate(var1, "testSourceDirectory", null, var8)) {
            var4.setTestSourceDirectory(this.interpolatedTrimmed(var1.nextText(), "testSourceDirectory"));
         } else if (this.checkFieldWithDuplicate(var1, "outputDirectory", null, var8)) {
            var4.setOutputDirectory(this.interpolatedTrimmed(var1.nextText(), "outputDirectory"));
         } else if (this.checkFieldWithDuplicate(var1, "testOutputDirectory", null, var8)) {
            var4.setTestOutputDirectory(this.interpolatedTrimmed(var1.nextText(), "testOutputDirectory"));
         } else if (this.checkFieldWithDuplicate(var1, "extensions", null, var8)) {
            ArrayList var13 = new ArrayList();
            var4.setExtensions(var13);

            while (var1.nextTag() == 2) {
               if ("extension".equals(var1.getName())) {
                  var13.add(this.parseExtension(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "defaultGoal", null, var8)) {
            var4.setDefaultGoal(this.interpolatedTrimmed(var1.nextText(), "defaultGoal"));
         } else if (this.checkFieldWithDuplicate(var1, "resources", null, var8)) {
            ArrayList var12 = new ArrayList();
            var4.setResources(var12);

            while (var1.nextTag() == 2) {
               if ("resource".equals(var1.getName())) {
                  var12.add(this.parseResource(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "testResources", null, var8)) {
            ArrayList var11 = new ArrayList();
            var4.setTestResources(var11);

            while (var1.nextTag() == 2) {
               if ("testResource".equals(var1.getName())) {
                  var11.add(this.parseResource(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "directory", null, var8)) {
            var4.setDirectory(this.interpolatedTrimmed(var1.nextText(), "directory"));
         } else if (this.checkFieldWithDuplicate(var1, "finalName", null, var8)) {
            var4.setFinalName(this.interpolatedTrimmed(var1.nextText(), "finalName"));
         } else if (this.checkFieldWithDuplicate(var1, "filters", null, var8)) {
            ArrayList var10 = new ArrayList();
            var4.setFilters(var10);

            while (var1.nextTag() == 2) {
               if ("filter".equals(var1.getName())) {
                  var10.add(this.interpolatedTrimmed(var1.nextText(), "filters"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "pluginManagement", null, var8)) {
            var4.setPluginManagement(this.parsePluginManagement(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "plugins", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setPlugins(var9);

            while (var1.nextTag() == 2) {
               if ("plugin".equals(var1.getName())) {
                  var9.add(this.parsePlugin(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private BuildBase parseBuildBase(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      BuildBase var4 = new BuildBase();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "defaultGoal", null, var8)) {
            var4.setDefaultGoal(this.interpolatedTrimmed(var1.nextText(), "defaultGoal"));
         } else if (this.checkFieldWithDuplicate(var1, "resources", null, var8)) {
            ArrayList var12 = new ArrayList();
            var4.setResources(var12);

            while (var1.nextTag() == 2) {
               if ("resource".equals(var1.getName())) {
                  var12.add(this.parseResource(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "testResources", null, var8)) {
            ArrayList var11 = new ArrayList();
            var4.setTestResources(var11);

            while (var1.nextTag() == 2) {
               if ("testResource".equals(var1.getName())) {
                  var11.add(this.parseResource(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "directory", null, var8)) {
            var4.setDirectory(this.interpolatedTrimmed(var1.nextText(), "directory"));
         } else if (this.checkFieldWithDuplicate(var1, "finalName", null, var8)) {
            var4.setFinalName(this.interpolatedTrimmed(var1.nextText(), "finalName"));
         } else if (this.checkFieldWithDuplicate(var1, "filters", null, var8)) {
            ArrayList var10 = new ArrayList();
            var4.setFilters(var10);

            while (var1.nextTag() == 2) {
               if ("filter".equals(var1.getName())) {
                  var10.add(this.interpolatedTrimmed(var1.nextText(), "filters"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "pluginManagement", null, var8)) {
            var4.setPluginManagement(this.parsePluginManagement(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "plugins", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setPlugins(var9);

            while (var1.nextTag() == 2) {
               if ("plugin".equals(var1.getName())) {
                  var9.add(this.parsePlugin(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private CiManagement parseCiManagement(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      CiManagement var4 = new CiManagement();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "system", null, var8)) {
            var4.setSystem(this.interpolatedTrimmed(var1.nextText(), "system"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var8)) {
            var4.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else if (this.checkFieldWithDuplicate(var1, "notifiers", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setNotifiers(var9);

            while (var1.nextTag() == 2) {
               if ("notifier".equals(var1.getName())) {
                  var9.add(this.parseNotifier(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private ConfigurationContainer parseConfigurationContainer(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      ConfigurationContainer var4 = new ConfigurationContainer();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "inherited", null, var8)) {
            var4.setInherited(this.interpolatedTrimmed(var1.nextText(), "inherited"));
         } else if (this.checkFieldWithDuplicate(var1, "configuration", null, var8)) {
            var4.setConfiguration(Xpp3DomBuilder.build(var1, true));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Contributor parseContributor(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Contributor var4 = new Contributor();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "name", null, var8)) {
            var4.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "email", null, var8)) {
            var4.setEmail(this.interpolatedTrimmed(var1.nextText(), "email"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var8)) {
            var4.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else if (this.checkFieldWithDuplicate(var1, "organization", "organisation", var8)) {
            var4.setOrganization(this.interpolatedTrimmed(var1.nextText(), "organization"));
         } else if (this.checkFieldWithDuplicate(var1, "organizationUrl", "organisationUrl", var8)) {
            var4.setOrganizationUrl(this.interpolatedTrimmed(var1.nextText(), "organizationUrl"));
         } else if (this.checkFieldWithDuplicate(var1, "roles", null, var8)) {
            ArrayList var10 = new ArrayList();
            var4.setRoles(var10);

            while (var1.nextTag() == 2) {
               if ("role".equals(var1.getName())) {
                  var10.add(this.interpolatedTrimmed(var1.nextText(), "roles"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "timezone", null, var8)) {
            var4.setTimezone(this.interpolatedTrimmed(var1.nextText(), "timezone"));
         } else if (this.checkFieldWithDuplicate(var1, "properties", null, var8)) {
            while (var1.nextTag() == 2) {
               String var9 = var1.getName();
               String var11 = var1.nextText().trim();
               var4.addProperty(var9, var11);
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Dependency parseDependency(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Dependency var4 = new Dependency();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "groupId", null, var8)) {
            var4.setGroupId(this.interpolatedTrimmed(var1.nextText(), "groupId"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var8)) {
            var4.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else if (this.checkFieldWithDuplicate(var1, "version", null, var8)) {
            var4.setVersion(this.interpolatedTrimmed(var1.nextText(), "version"));
         } else if (this.checkFieldWithDuplicate(var1, "type", null, var8)) {
            var4.setType(this.interpolatedTrimmed(var1.nextText(), "type"));
         } else if (this.checkFieldWithDuplicate(var1, "classifier", null, var8)) {
            var4.setClassifier(this.interpolatedTrimmed(var1.nextText(), "classifier"));
         } else if (this.checkFieldWithDuplicate(var1, "scope", null, var8)) {
            var4.setScope(this.interpolatedTrimmed(var1.nextText(), "scope"));
         } else if (this.checkFieldWithDuplicate(var1, "systemPath", null, var8)) {
            var4.setSystemPath(this.interpolatedTrimmed(var1.nextText(), "systemPath"));
         } else if (this.checkFieldWithDuplicate(var1, "exclusions", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setExclusions(var9);

            while (var1.nextTag() == 2) {
               if ("exclusion".equals(var1.getName())) {
                  var9.add(this.parseExclusion(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "optional", null, var8)) {
            var4.setOptional(this.interpolatedTrimmed(var1.nextText(), "optional"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private DependencyManagement parseDependencyManagement(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      DependencyManagement var4 = new DependencyManagement();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "dependencies", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setDependencies(var9);

            while (var1.nextTag() == 2) {
               if ("dependency".equals(var1.getName())) {
                  var9.add(this.parseDependency(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private DeploymentRepository parseDeploymentRepository(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      DeploymentRepository var4 = new DeploymentRepository();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "uniqueVersion", null, var8)) {
            var4.setUniqueVersion(this.getBooleanValue(this.interpolatedTrimmed(var1.nextText(), "uniqueVersion"), "uniqueVersion", var1, "true"));
         } else if (this.checkFieldWithDuplicate(var1, "releases", null, var8)) {
            var4.setReleases(this.parseRepositoryPolicy(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "snapshots", null, var8)) {
            var4.setSnapshots(this.parseRepositoryPolicy(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "id", null, var8)) {
            var4.setId(this.interpolatedTrimmed(var1.nextText(), "id"));
         } else if (this.checkFieldWithDuplicate(var1, "name", null, var8)) {
            var4.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var8)) {
            var4.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else if (this.checkFieldWithDuplicate(var1, "layout", null, var8)) {
            var4.setLayout(this.interpolatedTrimmed(var1.nextText(), "layout"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Developer parseDeveloper(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Developer var4 = new Developer();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "id", null, var8)) {
            var4.setId(this.interpolatedTrimmed(var1.nextText(), "id"));
         } else if (this.checkFieldWithDuplicate(var1, "name", null, var8)) {
            var4.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "email", null, var8)) {
            var4.setEmail(this.interpolatedTrimmed(var1.nextText(), "email"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var8)) {
            var4.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else if (this.checkFieldWithDuplicate(var1, "organization", "organisation", var8)) {
            var4.setOrganization(this.interpolatedTrimmed(var1.nextText(), "organization"));
         } else if (this.checkFieldWithDuplicate(var1, "organizationUrl", "organisationUrl", var8)) {
            var4.setOrganizationUrl(this.interpolatedTrimmed(var1.nextText(), "organizationUrl"));
         } else if (this.checkFieldWithDuplicate(var1, "roles", null, var8)) {
            ArrayList var10 = new ArrayList();
            var4.setRoles(var10);

            while (var1.nextTag() == 2) {
               if ("role".equals(var1.getName())) {
                  var10.add(this.interpolatedTrimmed(var1.nextText(), "roles"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "timezone", null, var8)) {
            var4.setTimezone(this.interpolatedTrimmed(var1.nextText(), "timezone"));
         } else if (this.checkFieldWithDuplicate(var1, "properties", null, var8)) {
            while (var1.nextTag() == 2) {
               String var9 = var1.getName();
               String var11 = var1.nextText().trim();
               var4.addProperty(var9, var11);
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private DistributionManagement parseDistributionManagement(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      DistributionManagement var4 = new DistributionManagement();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "repository", null, var8)) {
            var4.setRepository(this.parseDeploymentRepository(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "snapshotRepository", null, var8)) {
            var4.setSnapshotRepository(this.parseDeploymentRepository(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "site", null, var8)) {
            var4.setSite(this.parseSite(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "downloadUrl", null, var8)) {
            var4.setDownloadUrl(this.interpolatedTrimmed(var1.nextText(), "downloadUrl"));
         } else if (this.checkFieldWithDuplicate(var1, "relocation", null, var8)) {
            var4.setRelocation(this.parseRelocation(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "status", null, var8)) {
            var4.setStatus(this.interpolatedTrimmed(var1.nextText(), "status"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Exclusion parseExclusion(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Exclusion var4 = new Exclusion();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "groupId", null, var8)) {
            var4.setGroupId(this.interpolatedTrimmed(var1.nextText(), "groupId"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var8)) {
            var4.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Extension parseExtension(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Extension var4 = new Extension();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "groupId", null, var8)) {
            var4.setGroupId(this.interpolatedTrimmed(var1.nextText(), "groupId"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var8)) {
            var4.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else if (this.checkFieldWithDuplicate(var1, "version", null, var8)) {
            var4.setVersion(this.interpolatedTrimmed(var1.nextText(), "version"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private FileSet parseFileSet(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      FileSet var4 = new FileSet();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "directory", null, var8)) {
            var4.setDirectory(this.interpolatedTrimmed(var1.nextText(), "directory"));
         } else if (this.checkFieldWithDuplicate(var1, "includes", null, var8)) {
            ArrayList var10 = new ArrayList();
            var4.setIncludes(var10);

            while (var1.nextTag() == 2) {
               if ("include".equals(var1.getName())) {
                  var10.add(this.interpolatedTrimmed(var1.nextText(), "includes"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "excludes", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setExcludes(var9);

            while (var1.nextTag() == 2) {
               if ("exclude".equals(var1.getName())) {
                  var9.add(this.interpolatedTrimmed(var1.nextText(), "excludes"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private IssueManagement parseIssueManagement(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      IssueManagement var4 = new IssueManagement();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "system", null, var8)) {
            var4.setSystem(this.interpolatedTrimmed(var1.nextText(), "system"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var8)) {
            var4.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private License parseLicense(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      License var4 = new License();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "name", null, var8)) {
            var4.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var8)) {
            var4.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else if (this.checkFieldWithDuplicate(var1, "distribution", null, var8)) {
            var4.setDistribution(this.interpolatedTrimmed(var1.nextText(), "distribution"));
         } else if (this.checkFieldWithDuplicate(var1, "comments", null, var8)) {
            var4.setComments(this.interpolatedTrimmed(var1.nextText(), "comments"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private MailingList parseMailingList(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      MailingList var4 = new MailingList();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "name", null, var8)) {
            var4.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "subscribe", null, var8)) {
            var4.setSubscribe(this.interpolatedTrimmed(var1.nextText(), "subscribe"));
         } else if (this.checkFieldWithDuplicate(var1, "unsubscribe", null, var8)) {
            var4.setUnsubscribe(this.interpolatedTrimmed(var1.nextText(), "unsubscribe"));
         } else if (this.checkFieldWithDuplicate(var1, "post", null, var8)) {
            var4.setPost(this.interpolatedTrimmed(var1.nextText(), "post"));
         } else if (this.checkFieldWithDuplicate(var1, "archive", null, var8)) {
            var4.setArchive(this.interpolatedTrimmed(var1.nextText(), "archive"));
         } else if (this.checkFieldWithDuplicate(var1, "otherArchives", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setOtherArchives(var9);

            while (var1.nextTag() == 2) {
               if ("otherArchive".equals(var1.getName())) {
                  var9.add(this.interpolatedTrimmed(var1.nextText(), "otherArchives"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Model parseModel(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Model var4 = new Model();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0 && !"xmlns".equals(var6)) {
            if ("child.project.url.inherit.append.path".equals(var6)) {
               var4.setChildProjectUrlInheritAppendPath(this.interpolatedTrimmed(var7, "child.project.url.inherit.append.path"));
            } else {
               this.checkUnknownAttribute(var1, var6, var3, var2);
            }
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "modelVersion", null, var8)) {
            var4.setModelVersion(this.interpolatedTrimmed(var1.nextText(), "modelVersion"));
         } else if (this.checkFieldWithDuplicate(var1, "parent", null, var8)) {
            var4.setParent(this.parseParent(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "groupId", null, var8)) {
            var4.setGroupId(this.interpolatedTrimmed(var1.nextText(), "groupId"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var8)) {
            var4.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else if (this.checkFieldWithDuplicate(var1, "version", null, var8)) {
            var4.setVersion(this.interpolatedTrimmed(var1.nextText(), "version"));
         } else if (this.checkFieldWithDuplicate(var1, "packaging", null, var8)) {
            var4.setPackaging(this.interpolatedTrimmed(var1.nextText(), "packaging"));
         } else if (this.checkFieldWithDuplicate(var1, "name", null, var8)) {
            var4.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "description", null, var8)) {
            var4.setDescription(this.interpolatedTrimmed(var1.nextText(), "description"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var8)) {
            var4.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else if (this.checkFieldWithDuplicate(var1, "inceptionYear", null, var8)) {
            var4.setInceptionYear(this.interpolatedTrimmed(var1.nextText(), "inceptionYear"));
         } else if (this.checkFieldWithDuplicate(var1, "organization", "organisation", var8)) {
            var4.setOrganization(this.parseOrganization(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "licenses", null, var8)) {
            ArrayList var18 = new ArrayList();
            var4.setLicenses(var18);

            while (var1.nextTag() == 2) {
               if ("license".equals(var1.getName())) {
                  var18.add(this.parseLicense(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "developers", null, var8)) {
            ArrayList var17 = new ArrayList();
            var4.setDevelopers(var17);

            while (var1.nextTag() == 2) {
               if ("developer".equals(var1.getName())) {
                  var17.add(this.parseDeveloper(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "contributors", null, var8)) {
            ArrayList var16 = new ArrayList();
            var4.setContributors(var16);

            while (var1.nextTag() == 2) {
               if ("contributor".equals(var1.getName())) {
                  var16.add(this.parseContributor(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "mailingLists", null, var8)) {
            ArrayList var15 = new ArrayList();
            var4.setMailingLists(var15);

            while (var1.nextTag() == 2) {
               if ("mailingList".equals(var1.getName())) {
                  var15.add(this.parseMailingList(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "prerequisites", null, var8)) {
            var4.setPrerequisites(this.parsePrerequisites(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "modules", null, var8)) {
            ArrayList var14 = new ArrayList();
            var4.setModules(var14);

            while (var1.nextTag() == 2) {
               if ("module".equals(var1.getName())) {
                  var14.add(this.interpolatedTrimmed(var1.nextText(), "modules"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "scm", null, var8)) {
            var4.setScm(this.parseScm(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "issueManagement", null, var8)) {
            var4.setIssueManagement(this.parseIssueManagement(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "ciManagement", null, var8)) {
            var4.setCiManagement(this.parseCiManagement(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "distributionManagement", null, var8)) {
            var4.setDistributionManagement(this.parseDistributionManagement(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "properties", null, var8)) {
            while (var1.nextTag() == 2) {
               String var13 = var1.getName();
               String var19 = var1.nextText().trim();
               var4.addProperty(var13, var19);
            }
         } else if (this.checkFieldWithDuplicate(var1, "dependencyManagement", null, var8)) {
            var4.setDependencyManagement(this.parseDependencyManagement(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "dependencies", null, var8)) {
            ArrayList var12 = new ArrayList();
            var4.setDependencies(var12);

            while (var1.nextTag() == 2) {
               if ("dependency".equals(var1.getName())) {
                  var12.add(this.parseDependency(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "repositories", null, var8)) {
            ArrayList var11 = new ArrayList();
            var4.setRepositories(var11);

            while (var1.nextTag() == 2) {
               if ("repository".equals(var1.getName())) {
                  var11.add(this.parseRepository(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "pluginRepositories", null, var8)) {
            ArrayList var10 = new ArrayList();
            var4.setPluginRepositories(var10);

            while (var1.nextTag() == 2) {
               if ("pluginRepository".equals(var1.getName())) {
                  var10.add(this.parseRepository(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "build", null, var8)) {
            var4.setBuild(this.parseBuild(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "reports", null, var8)) {
            var4.setReports(Xpp3DomBuilder.build(var1, true));
         } else if (this.checkFieldWithDuplicate(var1, "reporting", null, var8)) {
            var4.setReporting(this.parseReporting(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "profiles", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setProfiles(var9);

            while (var1.nextTag() == 2) {
               if ("profile".equals(var1.getName())) {
                  var9.add(this.parseProfile(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private ModelBase parseModelBase(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      ModelBase var4 = new ModelBase();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "modules", null, var8)) {
            ArrayList var13 = new ArrayList();
            var4.setModules(var13);

            while (var1.nextTag() == 2) {
               if ("module".equals(var1.getName())) {
                  var13.add(this.interpolatedTrimmed(var1.nextText(), "modules"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "distributionManagement", null, var8)) {
            var4.setDistributionManagement(this.parseDistributionManagement(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "properties", null, var8)) {
            while (var1.nextTag() == 2) {
               String var12 = var1.getName();
               String var14 = var1.nextText().trim();
               var4.addProperty(var12, var14);
            }
         } else if (this.checkFieldWithDuplicate(var1, "dependencyManagement", null, var8)) {
            var4.setDependencyManagement(this.parseDependencyManagement(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "dependencies", null, var8)) {
            ArrayList var11 = new ArrayList();
            var4.setDependencies(var11);

            while (var1.nextTag() == 2) {
               if ("dependency".equals(var1.getName())) {
                  var11.add(this.parseDependency(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "repositories", null, var8)) {
            ArrayList var10 = new ArrayList();
            var4.setRepositories(var10);

            while (var1.nextTag() == 2) {
               if ("repository".equals(var1.getName())) {
                  var10.add(this.parseRepository(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "pluginRepositories", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setPluginRepositories(var9);

            while (var1.nextTag() == 2) {
               if ("pluginRepository".equals(var1.getName())) {
                  var9.add(this.parseRepository(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "reports", null, var8)) {
            var4.setReports(Xpp3DomBuilder.build(var1, true));
         } else if (this.checkFieldWithDuplicate(var1, "reporting", null, var8)) {
            var4.setReporting(this.parseReporting(var1, var2));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Notifier parseNotifier(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Notifier var4 = new Notifier();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "type", null, var8)) {
            var4.setType(this.interpolatedTrimmed(var1.nextText(), "type"));
         } else if (this.checkFieldWithDuplicate(var1, "sendOnError", null, var8)) {
            var4.setSendOnError(this.getBooleanValue(this.interpolatedTrimmed(var1.nextText(), "sendOnError"), "sendOnError", var1, "true"));
         } else if (this.checkFieldWithDuplicate(var1, "sendOnFailure", null, var8)) {
            var4.setSendOnFailure(this.getBooleanValue(this.interpolatedTrimmed(var1.nextText(), "sendOnFailure"), "sendOnFailure", var1, "true"));
         } else if (this.checkFieldWithDuplicate(var1, "sendOnSuccess", null, var8)) {
            var4.setSendOnSuccess(this.getBooleanValue(this.interpolatedTrimmed(var1.nextText(), "sendOnSuccess"), "sendOnSuccess", var1, "true"));
         } else if (this.checkFieldWithDuplicate(var1, "sendOnWarning", null, var8)) {
            var4.setSendOnWarning(this.getBooleanValue(this.interpolatedTrimmed(var1.nextText(), "sendOnWarning"), "sendOnWarning", var1, "true"));
         } else if (this.checkFieldWithDuplicate(var1, "address", null, var8)) {
            var4.setAddress(this.interpolatedTrimmed(var1.nextText(), "address"));
         } else if (this.checkFieldWithDuplicate(var1, "configuration", null, var8)) {
            while (var1.nextTag() == 2) {
               String var9 = var1.getName();
               String var10 = var1.nextText().trim();
               var4.addConfiguration(var9, var10);
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Organization parseOrganization(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Organization var4 = new Organization();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "name", null, var8)) {
            var4.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var8)) {
            var4.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Parent parseParent(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Parent var4 = new Parent();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "groupId", null, var8)) {
            var4.setGroupId(this.interpolatedTrimmed(var1.nextText(), "groupId"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var8)) {
            var4.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else if (this.checkFieldWithDuplicate(var1, "version", null, var8)) {
            var4.setVersion(this.interpolatedTrimmed(var1.nextText(), "version"));
         } else if (this.checkFieldWithDuplicate(var1, "relativePath", null, var8)) {
            var4.setRelativePath(this.interpolatedTrimmed(var1.nextText(), "relativePath"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private PatternSet parsePatternSet(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      PatternSet var4 = new PatternSet();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "includes", null, var8)) {
            ArrayList var10 = new ArrayList();
            var4.setIncludes(var10);

            while (var1.nextTag() == 2) {
               if ("include".equals(var1.getName())) {
                  var10.add(this.interpolatedTrimmed(var1.nextText(), "includes"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "excludes", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setExcludes(var9);

            while (var1.nextTag() == 2) {
               if ("exclude".equals(var1.getName())) {
                  var9.add(this.interpolatedTrimmed(var1.nextText(), "excludes"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Plugin parsePlugin(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Plugin var4 = new Plugin();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "groupId", null, var8)) {
            var4.setGroupId(this.interpolatedTrimmed(var1.nextText(), "groupId"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var8)) {
            var4.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else if (this.checkFieldWithDuplicate(var1, "version", null, var8)) {
            var4.setVersion(this.interpolatedTrimmed(var1.nextText(), "version"));
         } else if (this.checkFieldWithDuplicate(var1, "extensions", null, var8)) {
            var4.setExtensions(this.interpolatedTrimmed(var1.nextText(), "extensions"));
         } else if (this.checkFieldWithDuplicate(var1, "executions", null, var8)) {
            ArrayList var10 = new ArrayList();
            var4.setExecutions(var10);

            while (var1.nextTag() == 2) {
               if ("execution".equals(var1.getName())) {
                  var10.add(this.parsePluginExecution(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "dependencies", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setDependencies(var9);

            while (var1.nextTag() == 2) {
               if ("dependency".equals(var1.getName())) {
                  var9.add(this.parseDependency(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "goals", null, var8)) {
            var4.setGoals(Xpp3DomBuilder.build(var1, true));
         } else if (this.checkFieldWithDuplicate(var1, "inherited", null, var8)) {
            var4.setInherited(this.interpolatedTrimmed(var1.nextText(), "inherited"));
         } else if (this.checkFieldWithDuplicate(var1, "configuration", null, var8)) {
            var4.setConfiguration(Xpp3DomBuilder.build(var1, true));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private PluginConfiguration parsePluginConfiguration(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      PluginConfiguration var4 = new PluginConfiguration();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "pluginManagement", null, var8)) {
            var4.setPluginManagement(this.parsePluginManagement(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "plugins", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setPlugins(var9);

            while (var1.nextTag() == 2) {
               if ("plugin".equals(var1.getName())) {
                  var9.add(this.parsePlugin(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private PluginContainer parsePluginContainer(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      PluginContainer var4 = new PluginContainer();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "plugins", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setPlugins(var9);

            while (var1.nextTag() == 2) {
               if ("plugin".equals(var1.getName())) {
                  var9.add(this.parsePlugin(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private PluginExecution parsePluginExecution(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      PluginExecution var4 = new PluginExecution();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "id", null, var8)) {
            var4.setId(this.interpolatedTrimmed(var1.nextText(), "id"));
         } else if (this.checkFieldWithDuplicate(var1, "phase", null, var8)) {
            var4.setPhase(this.interpolatedTrimmed(var1.nextText(), "phase"));
         } else if (this.checkFieldWithDuplicate(var1, "goals", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setGoals(var9);

            while (var1.nextTag() == 2) {
               if ("goal".equals(var1.getName())) {
                  var9.add(this.interpolatedTrimmed(var1.nextText(), "goals"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "inherited", null, var8)) {
            var4.setInherited(this.interpolatedTrimmed(var1.nextText(), "inherited"));
         } else if (this.checkFieldWithDuplicate(var1, "configuration", null, var8)) {
            var4.setConfiguration(Xpp3DomBuilder.build(var1, true));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private PluginManagement parsePluginManagement(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      PluginManagement var4 = new PluginManagement();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "plugins", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setPlugins(var9);

            while (var1.nextTag() == 2) {
               if ("plugin".equals(var1.getName())) {
                  var9.add(this.parsePlugin(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Prerequisites parsePrerequisites(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Prerequisites var4 = new Prerequisites();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "maven", null, var8)) {
            var4.setMaven(this.interpolatedTrimmed(var1.nextText(), "maven"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Profile parseProfile(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Profile var4 = new Profile();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "id", null, var8)) {
            var4.setId(this.interpolatedTrimmed(var1.nextText(), "id"));
         } else if (this.checkFieldWithDuplicate(var1, "activation", null, var8)) {
            var4.setActivation(this.parseActivation(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "build", null, var8)) {
            var4.setBuild(this.parseBuildBase(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "modules", null, var8)) {
            ArrayList var13 = new ArrayList();
            var4.setModules(var13);

            while (var1.nextTag() == 2) {
               if ("module".equals(var1.getName())) {
                  var13.add(this.interpolatedTrimmed(var1.nextText(), "modules"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "distributionManagement", null, var8)) {
            var4.setDistributionManagement(this.parseDistributionManagement(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "properties", null, var8)) {
            while (var1.nextTag() == 2) {
               String var12 = var1.getName();
               String var14 = var1.nextText().trim();
               var4.addProperty(var12, var14);
            }
         } else if (this.checkFieldWithDuplicate(var1, "dependencyManagement", null, var8)) {
            var4.setDependencyManagement(this.parseDependencyManagement(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "dependencies", null, var8)) {
            ArrayList var11 = new ArrayList();
            var4.setDependencies(var11);

            while (var1.nextTag() == 2) {
               if ("dependency".equals(var1.getName())) {
                  var11.add(this.parseDependency(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "repositories", null, var8)) {
            ArrayList var10 = new ArrayList();
            var4.setRepositories(var10);

            while (var1.nextTag() == 2) {
               if ("repository".equals(var1.getName())) {
                  var10.add(this.parseRepository(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "pluginRepositories", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setPluginRepositories(var9);

            while (var1.nextTag() == 2) {
               if ("pluginRepository".equals(var1.getName())) {
                  var9.add(this.parseRepository(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "reports", null, var8)) {
            var4.setReports(Xpp3DomBuilder.build(var1, true));
         } else if (this.checkFieldWithDuplicate(var1, "reporting", null, var8)) {
            var4.setReporting(this.parseReporting(var1, var2));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Relocation parseRelocation(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Relocation var4 = new Relocation();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "groupId", null, var8)) {
            var4.setGroupId(this.interpolatedTrimmed(var1.nextText(), "groupId"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var8)) {
            var4.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else if (this.checkFieldWithDuplicate(var1, "version", null, var8)) {
            var4.setVersion(this.interpolatedTrimmed(var1.nextText(), "version"));
         } else if (this.checkFieldWithDuplicate(var1, "message", null, var8)) {
            var4.setMessage(this.interpolatedTrimmed(var1.nextText(), "message"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private ReportPlugin parseReportPlugin(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      ReportPlugin var4 = new ReportPlugin();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "groupId", null, var8)) {
            var4.setGroupId(this.interpolatedTrimmed(var1.nextText(), "groupId"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var8)) {
            var4.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else if (this.checkFieldWithDuplicate(var1, "version", null, var8)) {
            var4.setVersion(this.interpolatedTrimmed(var1.nextText(), "version"));
         } else if (this.checkFieldWithDuplicate(var1, "reportSets", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setReportSets(var9);

            while (var1.nextTag() == 2) {
               if ("reportSet".equals(var1.getName())) {
                  var9.add(this.parseReportSet(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "inherited", null, var8)) {
            var4.setInherited(this.interpolatedTrimmed(var1.nextText(), "inherited"));
         } else if (this.checkFieldWithDuplicate(var1, "configuration", null, var8)) {
            var4.setConfiguration(Xpp3DomBuilder.build(var1, true));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private ReportSet parseReportSet(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      ReportSet var4 = new ReportSet();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "id", null, var8)) {
            var4.setId(this.interpolatedTrimmed(var1.nextText(), "id"));
         } else if (this.checkFieldWithDuplicate(var1, "reports", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setReports(var9);

            while (var1.nextTag() == 2) {
               if ("report".equals(var1.getName())) {
                  var9.add(this.interpolatedTrimmed(var1.nextText(), "reports"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "inherited", null, var8)) {
            var4.setInherited(this.interpolatedTrimmed(var1.nextText(), "inherited"));
         } else if (this.checkFieldWithDuplicate(var1, "configuration", null, var8)) {
            var4.setConfiguration(Xpp3DomBuilder.build(var1, true));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Reporting parseReporting(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Reporting var4 = new Reporting();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "excludeDefaults", null, var8)) {
            var4.setExcludeDefaults(this.interpolatedTrimmed(var1.nextText(), "excludeDefaults"));
         } else if (this.checkFieldWithDuplicate(var1, "outputDirectory", null, var8)) {
            var4.setOutputDirectory(this.interpolatedTrimmed(var1.nextText(), "outputDirectory"));
         } else if (this.checkFieldWithDuplicate(var1, "plugins", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setPlugins(var9);

            while (var1.nextTag() == 2) {
               if ("plugin".equals(var1.getName())) {
                  var9.add(this.parseReportPlugin(var1, var2));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Repository parseRepository(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Repository var4 = new Repository();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "releases", null, var8)) {
            var4.setReleases(this.parseRepositoryPolicy(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "snapshots", null, var8)) {
            var4.setSnapshots(this.parseRepositoryPolicy(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "id", null, var8)) {
            var4.setId(this.interpolatedTrimmed(var1.nextText(), "id"));
         } else if (this.checkFieldWithDuplicate(var1, "name", null, var8)) {
            var4.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var8)) {
            var4.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else if (this.checkFieldWithDuplicate(var1, "layout", null, var8)) {
            var4.setLayout(this.interpolatedTrimmed(var1.nextText(), "layout"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private RepositoryBase parseRepositoryBase(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      RepositoryBase var4 = new RepositoryBase();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "id", null, var8)) {
            var4.setId(this.interpolatedTrimmed(var1.nextText(), "id"));
         } else if (this.checkFieldWithDuplicate(var1, "name", null, var8)) {
            var4.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var8)) {
            var4.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else if (this.checkFieldWithDuplicate(var1, "layout", null, var8)) {
            var4.setLayout(this.interpolatedTrimmed(var1.nextText(), "layout"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private RepositoryPolicy parseRepositoryPolicy(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      RepositoryPolicy var4 = new RepositoryPolicy();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "enabled", null, var8)) {
            var4.setEnabled(this.interpolatedTrimmed(var1.nextText(), "enabled"));
         } else if (this.checkFieldWithDuplicate(var1, "updatePolicy", null, var8)) {
            var4.setUpdatePolicy(this.interpolatedTrimmed(var1.nextText(), "updatePolicy"));
         } else if (this.checkFieldWithDuplicate(var1, "checksumPolicy", null, var8)) {
            var4.setChecksumPolicy(this.interpolatedTrimmed(var1.nextText(), "checksumPolicy"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Resource parseResource(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Resource var4 = new Resource();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "targetPath", null, var8)) {
            var4.setTargetPath(this.interpolatedTrimmed(var1.nextText(), "targetPath"));
         } else if (this.checkFieldWithDuplicate(var1, "filtering", null, var8)) {
            var4.setFiltering(this.interpolatedTrimmed(var1.nextText(), "filtering"));
         } else if (this.checkFieldWithDuplicate(var1, "directory", null, var8)) {
            var4.setDirectory(this.interpolatedTrimmed(var1.nextText(), "directory"));
         } else if (this.checkFieldWithDuplicate(var1, "includes", null, var8)) {
            ArrayList var10 = new ArrayList();
            var4.setIncludes(var10);

            while (var1.nextTag() == 2) {
               if ("include".equals(var1.getName())) {
                  var10.add(this.interpolatedTrimmed(var1.nextText(), "includes"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "excludes", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setExcludes(var9);

            while (var1.nextTag() == 2) {
               if ("exclude".equals(var1.getName())) {
                  var9.add(this.interpolatedTrimmed(var1.nextText(), "excludes"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Scm parseScm(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Scm var4 = new Scm();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            if ("child.scm.connection.inherit.append.path".equals(var6)) {
               var4.setChildScmConnectionInheritAppendPath(this.interpolatedTrimmed(var7, "child.scm.connection.inherit.append.path"));
            } else if ("child.scm.developerConnection.inherit.append.path".equals(var6)) {
               var4.setChildScmDeveloperConnectionInheritAppendPath(this.interpolatedTrimmed(var7, "child.scm.developerConnection.inherit.append.path"));
            } else if ("child.scm.url.inherit.append.path".equals(var6)) {
               var4.setChildScmUrlInheritAppendPath(this.interpolatedTrimmed(var7, "child.scm.url.inherit.append.path"));
            } else {
               this.checkUnknownAttribute(var1, var6, var3, var2);
            }
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "connection", null, var8)) {
            var4.setConnection(this.interpolatedTrimmed(var1.nextText(), "connection"));
         } else if (this.checkFieldWithDuplicate(var1, "developerConnection", null, var8)) {
            var4.setDeveloperConnection(this.interpolatedTrimmed(var1.nextText(), "developerConnection"));
         } else if (this.checkFieldWithDuplicate(var1, "tag", null, var8)) {
            var4.setTag(this.interpolatedTrimmed(var1.nextText(), "tag"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var8)) {
            var4.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Site parseSite(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Site var4 = new Site();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            if ("child.site.url.inherit.append.path".equals(var6)) {
               var4.setChildSiteUrlInheritAppendPath(this.interpolatedTrimmed(var7, "child.site.url.inherit.append.path"));
            } else {
               this.checkUnknownAttribute(var1, var6, var3, var2);
            }
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "id", null, var8)) {
            var4.setId(this.interpolatedTrimmed(var1.nextText(), "id"));
         } else if (this.checkFieldWithDuplicate(var1, "name", null, var8)) {
            var4.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var8)) {
            var4.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Model read(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      Model var3 = null;
      int var4 = var1.getEventType();

      boolean var5;
      for (var5 = false; var4 != 1; var4 = var1.next()) {
         if (var4 == 2) {
            if (var2 && !"project".equals(var1.getName())) {
               throw new XmlPullParserException("Expected root element 'project' but found '" + var1.getName() + "'", var1, null);
            }

            if (var5) {
               throw new XmlPullParserException("Duplicated tag: 'project'", var1, null);
            }

            var3 = this.parseModel(var1, var2);
            var3.setModelEncoding(var1.getInputEncoding());
            var5 = true;
         }
      }

      if (var5) {
         return var3;
      } else {
         throw new XmlPullParserException("Expected root element 'project' but found no element at all: invalid XML document", var1, null);
      }
   }

   public void setAddDefaultEntities(boolean var1) {
      this.addDefaultEntities = var1;
   }

   public interface ContentTransformer {
      String transform(String var1, String var2);
   }
}
