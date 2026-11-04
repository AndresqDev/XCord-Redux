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
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputSource;
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

public class MavenXpp3ReaderEx {
   private boolean addDefaultEntities = true;
   public final MavenXpp3ReaderEx.ContentTransformer contentTransformer;

   public MavenXpp3ReaderEx() {
      this(new MavenXpp3ReaderEx.ContentTransformer() {
         @Override
         public String transform(String var1, String var2) {
            return var1;
         }
      });
   }

   public MavenXpp3ReaderEx(MavenXpp3ReaderEx.ContentTransformer var1) {
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

   public Model read(Reader var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      MXParser var4 = this.addDefaultEntities ? new MXParser(EntityReplacementMap.defaultEntityReplacementMap) : new MXParser();
      var4.setInput(var1);
      return this.read(var4, var2, var3);
   }

   public Model read(InputStream var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      return this.read(ReaderFactory.newXmlReader(var1), var2, var3);
   }

   private Activation parseActivation(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Activation var5 = new Activation();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var12 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "activeByDefault", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("activeByDefault", var6);
            var5.setActiveByDefault(this.getBooleanValue(this.interpolatedTrimmed(var1.nextText(), "activeByDefault"), "activeByDefault", var1, "false"));
         } else if (this.checkFieldWithDuplicate(var1, "jdk", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("jdk", var6);
            var5.setJdk(this.interpolatedTrimmed(var1.nextText(), "jdk"));
         } else if (this.checkFieldWithDuplicate(var1, "os", null, var12)) {
            var5.setOs(this.parseActivationOS(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "property", null, var12)) {
            var5.setProperty(this.parseActivationProperty(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "file", null, var12)) {
            var5.setFile(this.parseActivationFile(var1, var2, var3));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private ActivationFile parseActivationFile(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      ActivationFile var5 = new ActivationFile();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var12 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "missing", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("missing", var6);
            var5.setMissing(this.interpolatedTrimmed(var1.nextText(), "missing"));
         } else if (this.checkFieldWithDuplicate(var1, "exists", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("exists", var6);
            var5.setExists(this.interpolatedTrimmed(var1.nextText(), "exists"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private ActivationOS parseActivationOS(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      ActivationOS var5 = new ActivationOS();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var14 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "name", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("name", var6);
            var5.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "family", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("family", var6);
            var5.setFamily(this.interpolatedTrimmed(var1.nextText(), "family"));
         } else if (this.checkFieldWithDuplicate(var1, "arch", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("arch", var6);
            var5.setArch(this.interpolatedTrimmed(var1.nextText(), "arch"));
         } else if (this.checkFieldWithDuplicate(var1, "version", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("version", var6);
            var5.setVersion(this.interpolatedTrimmed(var1.nextText(), "version"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private ActivationProperty parseActivationProperty(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      ActivationProperty var5 = new ActivationProperty();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var12 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "name", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("name", var6);
            var5.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "value", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("value", var6);
            var5.setValue(this.interpolatedTrimmed(var1.nextText(), "value"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Build parseBuild(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Build var5 = new Build();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var19 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "sourceDirectory", null, var19)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("sourceDirectory", var6);
            var5.setSourceDirectory(this.interpolatedTrimmed(var1.nextText(), "sourceDirectory"));
         } else if (this.checkFieldWithDuplicate(var1, "scriptSourceDirectory", null, var19)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("scriptSourceDirectory", var6);
            var5.setScriptSourceDirectory(this.interpolatedTrimmed(var1.nextText(), "scriptSourceDirectory"));
         } else if (this.checkFieldWithDuplicate(var1, "testSourceDirectory", null, var19)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("testSourceDirectory", var6);
            var5.setTestSourceDirectory(this.interpolatedTrimmed(var1.nextText(), "testSourceDirectory"));
         } else if (this.checkFieldWithDuplicate(var1, "outputDirectory", null, var19)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("outputDirectory", var6);
            var5.setOutputDirectory(this.interpolatedTrimmed(var1.nextText(), "outputDirectory"));
         } else if (this.checkFieldWithDuplicate(var1, "testOutputDirectory", null, var19)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("testOutputDirectory", var6);
            var5.setTestOutputDirectory(this.interpolatedTrimmed(var1.nextText(), "testOutputDirectory"));
         } else if (this.checkFieldWithDuplicate(var1, "extensions", null, var19)) {
            ArrayList var24 = new ArrayList();
            var5.setExtensions(var24);

            while (var1.nextTag() == 2) {
               if ("extension".equals(var1.getName())) {
                  var24.add(this.parseExtension(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "defaultGoal", null, var19)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("defaultGoal", var6);
            var5.setDefaultGoal(this.interpolatedTrimmed(var1.nextText(), "defaultGoal"));
         } else if (this.checkFieldWithDuplicate(var1, "resources", null, var19)) {
            ArrayList var23 = new ArrayList();
            var5.setResources(var23);

            while (var1.nextTag() == 2) {
               if ("resource".equals(var1.getName())) {
                  var23.add(this.parseResource(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "testResources", null, var19)) {
            ArrayList var22 = new ArrayList();
            var5.setTestResources(var22);

            while (var1.nextTag() == 2) {
               if ("testResource".equals(var1.getName())) {
                  var22.add(this.parseResource(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "directory", null, var19)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("directory", var6);
            var5.setDirectory(this.interpolatedTrimmed(var1.nextText(), "directory"));
         } else if (this.checkFieldWithDuplicate(var1, "finalName", null, var19)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("finalName", var6);
            var5.setFinalName(this.interpolatedTrimmed(var1.nextText(), "finalName"));
         } else if (this.checkFieldWithDuplicate(var1, "filters", null, var19)) {
            ArrayList var21 = new ArrayList();
            var5.setFilters(var21);
            InputLocation var25 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("filters", var25);

            while (var1.nextTag() == 2) {
               if ("filter".equals(var1.getName())) {
                  var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
                  var25.setLocation(var21.size(), var6);
                  var21.add(this.interpolatedTrimmed(var1.nextText(), "filters"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "pluginManagement", null, var19)) {
            var5.setPluginManagement(this.parsePluginManagement(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "plugins", null, var19)) {
            ArrayList var20 = new ArrayList();
            var5.setPlugins(var20);

            while (var1.nextTag() == 2) {
               if ("plugin".equals(var1.getName())) {
                  var20.add(this.parsePlugin(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private BuildBase parseBuildBase(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      BuildBase var5 = new BuildBase();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var14 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "defaultGoal", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("defaultGoal", var6);
            var5.setDefaultGoal(this.interpolatedTrimmed(var1.nextText(), "defaultGoal"));
         } else if (this.checkFieldWithDuplicate(var1, "resources", null, var14)) {
            ArrayList var18 = new ArrayList();
            var5.setResources(var18);

            while (var1.nextTag() == 2) {
               if ("resource".equals(var1.getName())) {
                  var18.add(this.parseResource(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "testResources", null, var14)) {
            ArrayList var17 = new ArrayList();
            var5.setTestResources(var17);

            while (var1.nextTag() == 2) {
               if ("testResource".equals(var1.getName())) {
                  var17.add(this.parseResource(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "directory", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("directory", var6);
            var5.setDirectory(this.interpolatedTrimmed(var1.nextText(), "directory"));
         } else if (this.checkFieldWithDuplicate(var1, "finalName", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("finalName", var6);
            var5.setFinalName(this.interpolatedTrimmed(var1.nextText(), "finalName"));
         } else if (this.checkFieldWithDuplicate(var1, "filters", null, var14)) {
            ArrayList var16 = new ArrayList();
            var5.setFilters(var16);
            InputLocation var19 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("filters", var19);

            while (var1.nextTag() == 2) {
               if ("filter".equals(var1.getName())) {
                  var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
                  var19.setLocation(var16.size(), var6);
                  var16.add(this.interpolatedTrimmed(var1.nextText(), "filters"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "pluginManagement", null, var14)) {
            var5.setPluginManagement(this.parsePluginManagement(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "plugins", null, var14)) {
            ArrayList var15 = new ArrayList();
            var5.setPlugins(var15);

            while (var1.nextTag() == 2) {
               if ("plugin".equals(var1.getName())) {
                  var15.add(this.parsePlugin(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private CiManagement parseCiManagement(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      CiManagement var5 = new CiManagement();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var12 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "system", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("system", var6);
            var5.setSystem(this.interpolatedTrimmed(var1.nextText(), "system"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("url", var6);
            var5.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else if (this.checkFieldWithDuplicate(var1, "notifiers", null, var12)) {
            ArrayList var13 = new ArrayList();
            var5.setNotifiers(var13);

            while (var1.nextTag() == 2) {
               if ("notifier".equals(var1.getName())) {
                  var13.add(this.parseNotifier(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private ConfigurationContainer parseConfigurationContainer(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      ConfigurationContainer var5 = new ConfigurationContainer();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var12 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "inherited", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("inherited", var6);
            var5.setInherited(this.interpolatedTrimmed(var1.nextText(), "inherited"));
         } else if (this.checkFieldWithDuplicate(var1, "configuration", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("configuration", var6);
            var5.setConfiguration(Xpp3DomBuilder.build(var1, true, new MavenXpp3ReaderEx.Xpp3DomBuilderInputLocationBuilder(var6)));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Contributor parseContributor(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Contributor var5 = new Contributor();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var19 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "name", null, var19)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("name", var6);
            var5.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "email", null, var19)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("email", var6);
            var5.setEmail(this.interpolatedTrimmed(var1.nextText(), "email"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var19)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("url", var6);
            var5.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else if (this.checkFieldWithDuplicate(var1, "organization", "organisation", var19)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("organization", var6);
            var5.setOrganization(this.interpolatedTrimmed(var1.nextText(), "organization"));
         } else if (this.checkFieldWithDuplicate(var1, "organizationUrl", "organisationUrl", var19)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("organizationUrl", var6);
            var5.setOrganizationUrl(this.interpolatedTrimmed(var1.nextText(), "organizationUrl"));
         } else if (this.checkFieldWithDuplicate(var1, "roles", null, var19)) {
            ArrayList var21 = new ArrayList();
            var5.setRoles(var21);
            InputLocation var23 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("roles", var23);

            while (var1.nextTag() == 2) {
               if ("role".equals(var1.getName())) {
                  var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
                  var23.setLocation(var21.size(), var6);
                  var21.add(this.interpolatedTrimmed(var1.nextText(), "roles"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "timezone", null, var19)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("timezone", var6);
            var5.setTimezone(this.interpolatedTrimmed(var1.nextText(), "timezone"));
         } else if (this.checkFieldWithDuplicate(var1, "properties", null, var19)) {
            InputLocation var20 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("properties", var20);

            while (var1.nextTag() == 2) {
               String var22 = var1.getName();
               var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
               var20.setLocation(var22, var6);
               String var10 = var1.nextText().trim();
               var5.addProperty(var22, var10);
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Dependency parseDependency(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Dependency var5 = new Dependency();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var18 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "groupId", null, var18)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("groupId", var6);
            var5.setGroupId(this.interpolatedTrimmed(var1.nextText(), "groupId"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var18)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("artifactId", var6);
            var5.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else if (this.checkFieldWithDuplicate(var1, "version", null, var18)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("version", var6);
            var5.setVersion(this.interpolatedTrimmed(var1.nextText(), "version"));
         } else if (this.checkFieldWithDuplicate(var1, "type", null, var18)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("type", var6);
            var5.setType(this.interpolatedTrimmed(var1.nextText(), "type"));
         } else if (this.checkFieldWithDuplicate(var1, "classifier", null, var18)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("classifier", var6);
            var5.setClassifier(this.interpolatedTrimmed(var1.nextText(), "classifier"));
         } else if (this.checkFieldWithDuplicate(var1, "scope", null, var18)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("scope", var6);
            var5.setScope(this.interpolatedTrimmed(var1.nextText(), "scope"));
         } else if (this.checkFieldWithDuplicate(var1, "systemPath", null, var18)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("systemPath", var6);
            var5.setSystemPath(this.interpolatedTrimmed(var1.nextText(), "systemPath"));
         } else if (this.checkFieldWithDuplicate(var1, "exclusions", null, var18)) {
            ArrayList var19 = new ArrayList();
            var5.setExclusions(var19);

            while (var1.nextTag() == 2) {
               if ("exclusion".equals(var1.getName())) {
                  var19.add(this.parseExclusion(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "optional", null, var18)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("optional", var6);
            var5.setOptional(this.interpolatedTrimmed(var1.nextText(), "optional"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private DependencyManagement parseDependencyManagement(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      DependencyManagement var5 = new DependencyManagement();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var10 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "dependencies", null, var10)) {
            ArrayList var11 = new ArrayList();
            var5.setDependencies(var11);

            while (var1.nextTag() == 2) {
               if ("dependency".equals(var1.getName())) {
                  var11.add(this.parseDependency(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private DeploymentRepository parseDeploymentRepository(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      DeploymentRepository var5 = new DeploymentRepository();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var15 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "uniqueVersion", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("uniqueVersion", var6);
            var5.setUniqueVersion(this.getBooleanValue(this.interpolatedTrimmed(var1.nextText(), "uniqueVersion"), "uniqueVersion", var1, "true"));
         } else if (this.checkFieldWithDuplicate(var1, "releases", null, var15)) {
            var5.setReleases(this.parseRepositoryPolicy(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "snapshots", null, var15)) {
            var5.setSnapshots(this.parseRepositoryPolicy(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "id", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("id", var6);
            var5.setId(this.interpolatedTrimmed(var1.nextText(), "id"));
         } else if (this.checkFieldWithDuplicate(var1, "name", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("name", var6);
            var5.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("url", var6);
            var5.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else if (this.checkFieldWithDuplicate(var1, "layout", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("layout", var6);
            var5.setLayout(this.interpolatedTrimmed(var1.nextText(), "layout"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Developer parseDeveloper(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Developer var5 = new Developer();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var20 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "id", null, var20)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("id", var6);
            var5.setId(this.interpolatedTrimmed(var1.nextText(), "id"));
         } else if (this.checkFieldWithDuplicate(var1, "name", null, var20)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("name", var6);
            var5.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "email", null, var20)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("email", var6);
            var5.setEmail(this.interpolatedTrimmed(var1.nextText(), "email"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var20)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("url", var6);
            var5.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else if (this.checkFieldWithDuplicate(var1, "organization", "organisation", var20)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("organization", var6);
            var5.setOrganization(this.interpolatedTrimmed(var1.nextText(), "organization"));
         } else if (this.checkFieldWithDuplicate(var1, "organizationUrl", "organisationUrl", var20)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("organizationUrl", var6);
            var5.setOrganizationUrl(this.interpolatedTrimmed(var1.nextText(), "organizationUrl"));
         } else if (this.checkFieldWithDuplicate(var1, "roles", null, var20)) {
            ArrayList var22 = new ArrayList();
            var5.setRoles(var22);
            InputLocation var24 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("roles", var24);

            while (var1.nextTag() == 2) {
               if ("role".equals(var1.getName())) {
                  var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
                  var24.setLocation(var22.size(), var6);
                  var22.add(this.interpolatedTrimmed(var1.nextText(), "roles"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "timezone", null, var20)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("timezone", var6);
            var5.setTimezone(this.interpolatedTrimmed(var1.nextText(), "timezone"));
         } else if (this.checkFieldWithDuplicate(var1, "properties", null, var20)) {
            InputLocation var21 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("properties", var21);

            while (var1.nextTag() == 2) {
               String var23 = var1.getName();
               var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
               var21.setLocation(var23, var6);
               String var10 = var1.nextText().trim();
               var5.addProperty(var23, var10);
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private DistributionManagement parseDistributionManagement(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      DistributionManagement var5 = new DistributionManagement();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var12 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "repository", null, var12)) {
            var5.setRepository(this.parseDeploymentRepository(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "snapshotRepository", null, var12)) {
            var5.setSnapshotRepository(this.parseDeploymentRepository(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "site", null, var12)) {
            var5.setSite(this.parseSite(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "downloadUrl", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("downloadUrl", var6);
            var5.setDownloadUrl(this.interpolatedTrimmed(var1.nextText(), "downloadUrl"));
         } else if (this.checkFieldWithDuplicate(var1, "relocation", null, var12)) {
            var5.setRelocation(this.parseRelocation(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "status", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("status", var6);
            var5.setStatus(this.interpolatedTrimmed(var1.nextText(), "status"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Exclusion parseExclusion(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Exclusion var5 = new Exclusion();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var12 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "groupId", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("groupId", var6);
            var5.setGroupId(this.interpolatedTrimmed(var1.nextText(), "groupId"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("artifactId", var6);
            var5.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Extension parseExtension(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Extension var5 = new Extension();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var13 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "groupId", null, var13)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("groupId", var6);
            var5.setGroupId(this.interpolatedTrimmed(var1.nextText(), "groupId"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var13)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("artifactId", var6);
            var5.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else if (this.checkFieldWithDuplicate(var1, "version", null, var13)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("version", var6);
            var5.setVersion(this.interpolatedTrimmed(var1.nextText(), "version"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private FileSet parseFileSet(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      FileSet var5 = new FileSet();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var13 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "directory", null, var13)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("directory", var6);
            var5.setDirectory(this.interpolatedTrimmed(var1.nextText(), "directory"));
         } else if (this.checkFieldWithDuplicate(var1, "includes", null, var13)) {
            ArrayList var15 = new ArrayList();
            var5.setIncludes(var15);
            InputLocation var17 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("includes", var17);

            while (var1.nextTag() == 2) {
               if ("include".equals(var1.getName())) {
                  var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
                  var17.setLocation(var15.size(), var6);
                  var15.add(this.interpolatedTrimmed(var1.nextText(), "includes"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "excludes", null, var13)) {
            ArrayList var14 = new ArrayList();
            var5.setExcludes(var14);
            InputLocation var16 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("excludes", var16);

            while (var1.nextTag() == 2) {
               if ("exclude".equals(var1.getName())) {
                  var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
                  var16.setLocation(var14.size(), var6);
                  var14.add(this.interpolatedTrimmed(var1.nextText(), "excludes"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private IssueManagement parseIssueManagement(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      IssueManagement var5 = new IssueManagement();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var12 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "system", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("system", var6);
            var5.setSystem(this.interpolatedTrimmed(var1.nextText(), "system"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("url", var6);
            var5.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private License parseLicense(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      License var5 = new License();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var14 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "name", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("name", var6);
            var5.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("url", var6);
            var5.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else if (this.checkFieldWithDuplicate(var1, "distribution", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("distribution", var6);
            var5.setDistribution(this.interpolatedTrimmed(var1.nextText(), "distribution"));
         } else if (this.checkFieldWithDuplicate(var1, "comments", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("comments", var6);
            var5.setComments(this.interpolatedTrimmed(var1.nextText(), "comments"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private MailingList parseMailingList(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      MailingList var5 = new MailingList();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var16 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "name", null, var16)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("name", var6);
            var5.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "subscribe", null, var16)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("subscribe", var6);
            var5.setSubscribe(this.interpolatedTrimmed(var1.nextText(), "subscribe"));
         } else if (this.checkFieldWithDuplicate(var1, "unsubscribe", null, var16)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("unsubscribe", var6);
            var5.setUnsubscribe(this.interpolatedTrimmed(var1.nextText(), "unsubscribe"));
         } else if (this.checkFieldWithDuplicate(var1, "post", null, var16)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("post", var6);
            var5.setPost(this.interpolatedTrimmed(var1.nextText(), "post"));
         } else if (this.checkFieldWithDuplicate(var1, "archive", null, var16)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("archive", var6);
            var5.setArchive(this.interpolatedTrimmed(var1.nextText(), "archive"));
         } else if (this.checkFieldWithDuplicate(var1, "otherArchives", null, var16)) {
            ArrayList var17 = new ArrayList();
            var5.setOtherArchives(var17);
            InputLocation var18 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("otherArchives", var18);

            while (var1.nextTag() == 2) {
               if ("otherArchive".equals(var1.getName())) {
                  var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
                  var18.setLocation(var17.size(), var6);
                  var17.add(this.interpolatedTrimmed(var1.nextText(), "otherArchives"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Model parseModel(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Model var5 = new Model();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0 && !"xmlns".equals(var8)) {
            if ("child.project.url.inherit.append.path".equals(var8)) {
               var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
               var5.setLocation("childProjectUrlInheritAppendPath", var6);
               var5.setChildProjectUrlInheritAppendPath(this.interpolatedTrimmed(var9, "child.project.url.inherit.append.path"));
            } else {
               this.checkUnknownAttribute(var1, var8, var4, var2);
            }
         }
      }

      HashSet var24 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "modelVersion", null, var24)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("modelVersion", var6);
            var5.setModelVersion(this.interpolatedTrimmed(var1.nextText(), "modelVersion"));
         } else if (this.checkFieldWithDuplicate(var1, "parent", null, var24)) {
            var5.setParent(this.parseParent(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "groupId", null, var24)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("groupId", var6);
            var5.setGroupId(this.interpolatedTrimmed(var1.nextText(), "groupId"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var24)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("artifactId", var6);
            var5.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else if (this.checkFieldWithDuplicate(var1, "version", null, var24)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("version", var6);
            var5.setVersion(this.interpolatedTrimmed(var1.nextText(), "version"));
         } else if (this.checkFieldWithDuplicate(var1, "packaging", null, var24)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("packaging", var6);
            var5.setPackaging(this.interpolatedTrimmed(var1.nextText(), "packaging"));
         } else if (this.checkFieldWithDuplicate(var1, "name", null, var24)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("name", var6);
            var5.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "description", null, var24)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("description", var6);
            var5.setDescription(this.interpolatedTrimmed(var1.nextText(), "description"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var24)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("url", var6);
            var5.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else if (this.checkFieldWithDuplicate(var1, "inceptionYear", null, var24)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("inceptionYear", var6);
            var5.setInceptionYear(this.interpolatedTrimmed(var1.nextText(), "inceptionYear"));
         } else if (this.checkFieldWithDuplicate(var1, "organization", "organisation", var24)) {
            var5.setOrganization(this.parseOrganization(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "licenses", null, var24)) {
            ArrayList var34 = new ArrayList();
            var5.setLicenses(var34);

            while (var1.nextTag() == 2) {
               if ("license".equals(var1.getName())) {
                  var34.add(this.parseLicense(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "developers", null, var24)) {
            ArrayList var33 = new ArrayList();
            var5.setDevelopers(var33);

            while (var1.nextTag() == 2) {
               if ("developer".equals(var1.getName())) {
                  var33.add(this.parseDeveloper(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "contributors", null, var24)) {
            ArrayList var32 = new ArrayList();
            var5.setContributors(var32);

            while (var1.nextTag() == 2) {
               if ("contributor".equals(var1.getName())) {
                  var32.add(this.parseContributor(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "mailingLists", null, var24)) {
            ArrayList var31 = new ArrayList();
            var5.setMailingLists(var31);

            while (var1.nextTag() == 2) {
               if ("mailingList".equals(var1.getName())) {
                  var31.add(this.parseMailingList(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "prerequisites", null, var24)) {
            var5.setPrerequisites(this.parsePrerequisites(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "modules", null, var24)) {
            ArrayList var30 = new ArrayList();
            var5.setModules(var30);
            InputLocation var36 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("modules", var36);

            while (var1.nextTag() == 2) {
               if ("module".equals(var1.getName())) {
                  var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
                  var36.setLocation(var30.size(), var6);
                  var30.add(this.interpolatedTrimmed(var1.nextText(), "modules"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "scm", null, var24)) {
            var5.setScm(this.parseScm(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "issueManagement", null, var24)) {
            var5.setIssueManagement(this.parseIssueManagement(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "ciManagement", null, var24)) {
            var5.setCiManagement(this.parseCiManagement(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "distributionManagement", null, var24)) {
            var5.setDistributionManagement(this.parseDistributionManagement(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "properties", null, var24)) {
            InputLocation var29 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("properties", var29);

            while (var1.nextTag() == 2) {
               String var35 = var1.getName();
               var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
               var29.setLocation(var35, var6);
               String var10 = var1.nextText().trim();
               var5.addProperty(var35, var10);
            }
         } else if (this.checkFieldWithDuplicate(var1, "dependencyManagement", null, var24)) {
            var5.setDependencyManagement(this.parseDependencyManagement(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "dependencies", null, var24)) {
            ArrayList var28 = new ArrayList();
            var5.setDependencies(var28);

            while (var1.nextTag() == 2) {
               if ("dependency".equals(var1.getName())) {
                  var28.add(this.parseDependency(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "repositories", null, var24)) {
            ArrayList var27 = new ArrayList();
            var5.setRepositories(var27);

            while (var1.nextTag() == 2) {
               if ("repository".equals(var1.getName())) {
                  var27.add(this.parseRepository(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "pluginRepositories", null, var24)) {
            ArrayList var26 = new ArrayList();
            var5.setPluginRepositories(var26);

            while (var1.nextTag() == 2) {
               if ("pluginRepository".equals(var1.getName())) {
                  var26.add(this.parseRepository(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "build", null, var24)) {
            var5.setBuild(this.parseBuild(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "reports", null, var24)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("reports", var6);
            var5.setReports(Xpp3DomBuilder.build(var1, true, new MavenXpp3ReaderEx.Xpp3DomBuilderInputLocationBuilder(var6)));
         } else if (this.checkFieldWithDuplicate(var1, "reporting", null, var24)) {
            var5.setReporting(this.parseReporting(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "profiles", null, var24)) {
            ArrayList var25 = new ArrayList();
            var5.setProfiles(var25);

            while (var1.nextTag() == 2) {
               if ("profile".equals(var1.getName())) {
                  var25.add(this.parseProfile(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private ModelBase parseModelBase(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      ModelBase var5 = new ModelBase();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var14 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "modules", null, var14)) {
            ArrayList var19 = new ArrayList();
            var5.setModules(var19);
            InputLocation var21 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("modules", var21);

            while (var1.nextTag() == 2) {
               if ("module".equals(var1.getName())) {
                  var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
                  var21.setLocation(var19.size(), var6);
                  var19.add(this.interpolatedTrimmed(var1.nextText(), "modules"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "distributionManagement", null, var14)) {
            var5.setDistributionManagement(this.parseDistributionManagement(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "properties", null, var14)) {
            InputLocation var18 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("properties", var18);

            while (var1.nextTag() == 2) {
               String var20 = var1.getName();
               var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
               var18.setLocation(var20, var6);
               String var10 = var1.nextText().trim();
               var5.addProperty(var20, var10);
            }
         } else if (this.checkFieldWithDuplicate(var1, "dependencyManagement", null, var14)) {
            var5.setDependencyManagement(this.parseDependencyManagement(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "dependencies", null, var14)) {
            ArrayList var17 = new ArrayList();
            var5.setDependencies(var17);

            while (var1.nextTag() == 2) {
               if ("dependency".equals(var1.getName())) {
                  var17.add(this.parseDependency(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "repositories", null, var14)) {
            ArrayList var16 = new ArrayList();
            var5.setRepositories(var16);

            while (var1.nextTag() == 2) {
               if ("repository".equals(var1.getName())) {
                  var16.add(this.parseRepository(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "pluginRepositories", null, var14)) {
            ArrayList var15 = new ArrayList();
            var5.setPluginRepositories(var15);

            while (var1.nextTag() == 2) {
               if ("pluginRepository".equals(var1.getName())) {
                  var15.add(this.parseRepository(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "reports", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("reports", var6);
            var5.setReports(Xpp3DomBuilder.build(var1, true, new MavenXpp3ReaderEx.Xpp3DomBuilderInputLocationBuilder(var6)));
         } else if (this.checkFieldWithDuplicate(var1, "reporting", null, var14)) {
            var5.setReporting(this.parseReporting(var1, var2, var3));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Notifier parseNotifier(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Notifier var5 = new Notifier();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var18 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "type", null, var18)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("type", var6);
            var5.setType(this.interpolatedTrimmed(var1.nextText(), "type"));
         } else if (this.checkFieldWithDuplicate(var1, "sendOnError", null, var18)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("sendOnError", var6);
            var5.setSendOnError(this.getBooleanValue(this.interpolatedTrimmed(var1.nextText(), "sendOnError"), "sendOnError", var1, "true"));
         } else if (this.checkFieldWithDuplicate(var1, "sendOnFailure", null, var18)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("sendOnFailure", var6);
            var5.setSendOnFailure(this.getBooleanValue(this.interpolatedTrimmed(var1.nextText(), "sendOnFailure"), "sendOnFailure", var1, "true"));
         } else if (this.checkFieldWithDuplicate(var1, "sendOnSuccess", null, var18)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("sendOnSuccess", var6);
            var5.setSendOnSuccess(this.getBooleanValue(this.interpolatedTrimmed(var1.nextText(), "sendOnSuccess"), "sendOnSuccess", var1, "true"));
         } else if (this.checkFieldWithDuplicate(var1, "sendOnWarning", null, var18)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("sendOnWarning", var6);
            var5.setSendOnWarning(this.getBooleanValue(this.interpolatedTrimmed(var1.nextText(), "sendOnWarning"), "sendOnWarning", var1, "true"));
         } else if (this.checkFieldWithDuplicate(var1, "address", null, var18)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("address", var6);
            var5.setAddress(this.interpolatedTrimmed(var1.nextText(), "address"));
         } else if (this.checkFieldWithDuplicate(var1, "configuration", null, var18)) {
            InputLocation var19 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("configuration", var19);

            while (var1.nextTag() == 2) {
               String var20 = var1.getName();
               var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
               var19.setLocation(var20, var6);
               String var10 = var1.nextText().trim();
               var5.addConfiguration(var20, var10);
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Organization parseOrganization(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Organization var5 = new Organization();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var12 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "name", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("name", var6);
            var5.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("url", var6);
            var5.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Parent parseParent(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Parent var5 = new Parent();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var14 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "groupId", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("groupId", var6);
            var5.setGroupId(this.interpolatedTrimmed(var1.nextText(), "groupId"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("artifactId", var6);
            var5.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else if (this.checkFieldWithDuplicate(var1, "version", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("version", var6);
            var5.setVersion(this.interpolatedTrimmed(var1.nextText(), "version"));
         } else if (this.checkFieldWithDuplicate(var1, "relativePath", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("relativePath", var6);
            var5.setRelativePath(this.interpolatedTrimmed(var1.nextText(), "relativePath"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private PatternSet parsePatternSet(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      PatternSet var5 = new PatternSet();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var12 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "includes", null, var12)) {
            ArrayList var14 = new ArrayList();
            var5.setIncludes(var14);
            InputLocation var16 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("includes", var16);

            while (var1.nextTag() == 2) {
               if ("include".equals(var1.getName())) {
                  var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
                  var16.setLocation(var14.size(), var6);
                  var14.add(this.interpolatedTrimmed(var1.nextText(), "includes"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "excludes", null, var12)) {
            ArrayList var13 = new ArrayList();
            var5.setExcludes(var13);
            InputLocation var15 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("excludes", var15);

            while (var1.nextTag() == 2) {
               if ("exclude".equals(var1.getName())) {
                  var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
                  var15.setLocation(var13.size(), var6);
                  var13.add(this.interpolatedTrimmed(var1.nextText(), "excludes"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Plugin parsePlugin(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Plugin var5 = new Plugin();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var17 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "groupId", null, var17)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("groupId", var6);
            var5.setGroupId(this.interpolatedTrimmed(var1.nextText(), "groupId"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var17)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("artifactId", var6);
            var5.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else if (this.checkFieldWithDuplicate(var1, "version", null, var17)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("version", var6);
            var5.setVersion(this.interpolatedTrimmed(var1.nextText(), "version"));
         } else if (this.checkFieldWithDuplicate(var1, "extensions", null, var17)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("extensions", var6);
            var5.setExtensions(this.interpolatedTrimmed(var1.nextText(), "extensions"));
         } else if (this.checkFieldWithDuplicate(var1, "executions", null, var17)) {
            ArrayList var19 = new ArrayList();
            var5.setExecutions(var19);

            while (var1.nextTag() == 2) {
               if ("execution".equals(var1.getName())) {
                  var19.add(this.parsePluginExecution(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "dependencies", null, var17)) {
            ArrayList var18 = new ArrayList();
            var5.setDependencies(var18);

            while (var1.nextTag() == 2) {
               if ("dependency".equals(var1.getName())) {
                  var18.add(this.parseDependency(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "goals", null, var17)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("goals", var6);
            var5.setGoals(Xpp3DomBuilder.build(var1, true, new MavenXpp3ReaderEx.Xpp3DomBuilderInputLocationBuilder(var6)));
         } else if (this.checkFieldWithDuplicate(var1, "inherited", null, var17)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("inherited", var6);
            var5.setInherited(this.interpolatedTrimmed(var1.nextText(), "inherited"));
         } else if (this.checkFieldWithDuplicate(var1, "configuration", null, var17)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("configuration", var6);
            var5.setConfiguration(Xpp3DomBuilder.build(var1, true, new MavenXpp3ReaderEx.Xpp3DomBuilderInputLocationBuilder(var6)));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private PluginConfiguration parsePluginConfiguration(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      PluginConfiguration var5 = new PluginConfiguration();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var10 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "pluginManagement", null, var10)) {
            var5.setPluginManagement(this.parsePluginManagement(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "plugins", null, var10)) {
            ArrayList var11 = new ArrayList();
            var5.setPlugins(var11);

            while (var1.nextTag() == 2) {
               if ("plugin".equals(var1.getName())) {
                  var11.add(this.parsePlugin(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private PluginContainer parsePluginContainer(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      PluginContainer var5 = new PluginContainer();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var10 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "plugins", null, var10)) {
            ArrayList var11 = new ArrayList();
            var5.setPlugins(var11);

            while (var1.nextTag() == 2) {
               if ("plugin".equals(var1.getName())) {
                  var11.add(this.parsePlugin(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private PluginExecution parsePluginExecution(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      PluginExecution var5 = new PluginExecution();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var15 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "id", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("id", var6);
            var5.setId(this.interpolatedTrimmed(var1.nextText(), "id"));
         } else if (this.checkFieldWithDuplicate(var1, "phase", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("phase", var6);
            var5.setPhase(this.interpolatedTrimmed(var1.nextText(), "phase"));
         } else if (this.checkFieldWithDuplicate(var1, "goals", null, var15)) {
            ArrayList var16 = new ArrayList();
            var5.setGoals(var16);
            InputLocation var17 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("goals", var17);

            while (var1.nextTag() == 2) {
               if ("goal".equals(var1.getName())) {
                  var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
                  var17.setLocation(var16.size(), var6);
                  var16.add(this.interpolatedTrimmed(var1.nextText(), "goals"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "inherited", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("inherited", var6);
            var5.setInherited(this.interpolatedTrimmed(var1.nextText(), "inherited"));
         } else if (this.checkFieldWithDuplicate(var1, "configuration", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("configuration", var6);
            var5.setConfiguration(Xpp3DomBuilder.build(var1, true, new MavenXpp3ReaderEx.Xpp3DomBuilderInputLocationBuilder(var6)));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private PluginManagement parsePluginManagement(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      PluginManagement var5 = new PluginManagement();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var10 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "plugins", null, var10)) {
            ArrayList var11 = new ArrayList();
            var5.setPlugins(var11);

            while (var1.nextTag() == 2) {
               if ("plugin".equals(var1.getName())) {
                  var11.add(this.parsePlugin(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Prerequisites parsePrerequisites(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Prerequisites var5 = new Prerequisites();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var11 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "maven", null, var11)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("maven", var6);
            var5.setMaven(this.interpolatedTrimmed(var1.nextText(), "maven"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Profile parseProfile(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Profile var5 = new Profile();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var15 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "id", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("id", var6);
            var5.setId(this.interpolatedTrimmed(var1.nextText(), "id"));
         } else if (this.checkFieldWithDuplicate(var1, "activation", null, var15)) {
            var5.setActivation(this.parseActivation(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "build", null, var15)) {
            var5.setBuild(this.parseBuildBase(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "modules", null, var15)) {
            ArrayList var20 = new ArrayList();
            var5.setModules(var20);
            InputLocation var22 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("modules", var22);

            while (var1.nextTag() == 2) {
               if ("module".equals(var1.getName())) {
                  var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
                  var22.setLocation(var20.size(), var6);
                  var20.add(this.interpolatedTrimmed(var1.nextText(), "modules"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "distributionManagement", null, var15)) {
            var5.setDistributionManagement(this.parseDistributionManagement(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "properties", null, var15)) {
            InputLocation var19 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("properties", var19);

            while (var1.nextTag() == 2) {
               String var21 = var1.getName();
               var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
               var19.setLocation(var21, var6);
               String var10 = var1.nextText().trim();
               var5.addProperty(var21, var10);
            }
         } else if (this.checkFieldWithDuplicate(var1, "dependencyManagement", null, var15)) {
            var5.setDependencyManagement(this.parseDependencyManagement(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "dependencies", null, var15)) {
            ArrayList var18 = new ArrayList();
            var5.setDependencies(var18);

            while (var1.nextTag() == 2) {
               if ("dependency".equals(var1.getName())) {
                  var18.add(this.parseDependency(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "repositories", null, var15)) {
            ArrayList var17 = new ArrayList();
            var5.setRepositories(var17);

            while (var1.nextTag() == 2) {
               if ("repository".equals(var1.getName())) {
                  var17.add(this.parseRepository(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "pluginRepositories", null, var15)) {
            ArrayList var16 = new ArrayList();
            var5.setPluginRepositories(var16);

            while (var1.nextTag() == 2) {
               if ("pluginRepository".equals(var1.getName())) {
                  var16.add(this.parseRepository(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "reports", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("reports", var6);
            var5.setReports(Xpp3DomBuilder.build(var1, true, new MavenXpp3ReaderEx.Xpp3DomBuilderInputLocationBuilder(var6)));
         } else if (this.checkFieldWithDuplicate(var1, "reporting", null, var15)) {
            var5.setReporting(this.parseReporting(var1, var2, var3));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Relocation parseRelocation(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Relocation var5 = new Relocation();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var14 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "groupId", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("groupId", var6);
            var5.setGroupId(this.interpolatedTrimmed(var1.nextText(), "groupId"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("artifactId", var6);
            var5.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else if (this.checkFieldWithDuplicate(var1, "version", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("version", var6);
            var5.setVersion(this.interpolatedTrimmed(var1.nextText(), "version"));
         } else if (this.checkFieldWithDuplicate(var1, "message", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("message", var6);
            var5.setMessage(this.interpolatedTrimmed(var1.nextText(), "message"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private ReportPlugin parseReportPlugin(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      ReportPlugin var5 = new ReportPlugin();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var15 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "groupId", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("groupId", var6);
            var5.setGroupId(this.interpolatedTrimmed(var1.nextText(), "groupId"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("artifactId", var6);
            var5.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else if (this.checkFieldWithDuplicate(var1, "version", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("version", var6);
            var5.setVersion(this.interpolatedTrimmed(var1.nextText(), "version"));
         } else if (this.checkFieldWithDuplicate(var1, "reportSets", null, var15)) {
            ArrayList var16 = new ArrayList();
            var5.setReportSets(var16);

            while (var1.nextTag() == 2) {
               if ("reportSet".equals(var1.getName())) {
                  var16.add(this.parseReportSet(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "inherited", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("inherited", var6);
            var5.setInherited(this.interpolatedTrimmed(var1.nextText(), "inherited"));
         } else if (this.checkFieldWithDuplicate(var1, "configuration", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("configuration", var6);
            var5.setConfiguration(Xpp3DomBuilder.build(var1, true, new MavenXpp3ReaderEx.Xpp3DomBuilderInputLocationBuilder(var6)));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private ReportSet parseReportSet(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      ReportSet var5 = new ReportSet();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var14 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "id", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("id", var6);
            var5.setId(this.interpolatedTrimmed(var1.nextText(), "id"));
         } else if (this.checkFieldWithDuplicate(var1, "reports", null, var14)) {
            ArrayList var15 = new ArrayList();
            var5.setReports(var15);
            InputLocation var16 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("reports", var16);

            while (var1.nextTag() == 2) {
               if ("report".equals(var1.getName())) {
                  var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
                  var16.setLocation(var15.size(), var6);
                  var15.add(this.interpolatedTrimmed(var1.nextText(), "reports"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "inherited", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("inherited", var6);
            var5.setInherited(this.interpolatedTrimmed(var1.nextText(), "inherited"));
         } else if (this.checkFieldWithDuplicate(var1, "configuration", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("configuration", var6);
            var5.setConfiguration(Xpp3DomBuilder.build(var1, true, new MavenXpp3ReaderEx.Xpp3DomBuilderInputLocationBuilder(var6)));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Reporting parseReporting(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Reporting var5 = new Reporting();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var12 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "excludeDefaults", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("excludeDefaults", var6);
            var5.setExcludeDefaults(this.interpolatedTrimmed(var1.nextText(), "excludeDefaults"));
         } else if (this.checkFieldWithDuplicate(var1, "outputDirectory", null, var12)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("outputDirectory", var6);
            var5.setOutputDirectory(this.interpolatedTrimmed(var1.nextText(), "outputDirectory"));
         } else if (this.checkFieldWithDuplicate(var1, "plugins", null, var12)) {
            ArrayList var13 = new ArrayList();
            var5.setPlugins(var13);

            while (var1.nextTag() == 2) {
               if ("plugin".equals(var1.getName())) {
                  var13.add(this.parseReportPlugin(var1, var2, var3));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Repository parseRepository(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Repository var5 = new Repository();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var14 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "releases", null, var14)) {
            var5.setReleases(this.parseRepositoryPolicy(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "snapshots", null, var14)) {
            var5.setSnapshots(this.parseRepositoryPolicy(var1, var2, var3));
         } else if (this.checkFieldWithDuplicate(var1, "id", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("id", var6);
            var5.setId(this.interpolatedTrimmed(var1.nextText(), "id"));
         } else if (this.checkFieldWithDuplicate(var1, "name", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("name", var6);
            var5.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("url", var6);
            var5.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else if (this.checkFieldWithDuplicate(var1, "layout", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("layout", var6);
            var5.setLayout(this.interpolatedTrimmed(var1.nextText(), "layout"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private RepositoryBase parseRepositoryBase(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      RepositoryBase var5 = new RepositoryBase();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var14 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "id", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("id", var6);
            var5.setId(this.interpolatedTrimmed(var1.nextText(), "id"));
         } else if (this.checkFieldWithDuplicate(var1, "name", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("name", var6);
            var5.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("url", var6);
            var5.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else if (this.checkFieldWithDuplicate(var1, "layout", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("layout", var6);
            var5.setLayout(this.interpolatedTrimmed(var1.nextText(), "layout"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private RepositoryPolicy parseRepositoryPolicy(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      RepositoryPolicy var5 = new RepositoryPolicy();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var13 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "enabled", null, var13)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("enabled", var6);
            var5.setEnabled(this.interpolatedTrimmed(var1.nextText(), "enabled"));
         } else if (this.checkFieldWithDuplicate(var1, "updatePolicy", null, var13)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("updatePolicy", var6);
            var5.setUpdatePolicy(this.interpolatedTrimmed(var1.nextText(), "updatePolicy"));
         } else if (this.checkFieldWithDuplicate(var1, "checksumPolicy", null, var13)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("checksumPolicy", var6);
            var5.setChecksumPolicy(this.interpolatedTrimmed(var1.nextText(), "checksumPolicy"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Resource parseResource(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Resource var5 = new Resource();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var8, var4, var2);
         }
      }

      HashSet var15 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "targetPath", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("targetPath", var6);
            var5.setTargetPath(this.interpolatedTrimmed(var1.nextText(), "targetPath"));
         } else if (this.checkFieldWithDuplicate(var1, "filtering", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("filtering", var6);
            var5.setFiltering(this.interpolatedTrimmed(var1.nextText(), "filtering"));
         } else if (this.checkFieldWithDuplicate(var1, "directory", null, var15)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("directory", var6);
            var5.setDirectory(this.interpolatedTrimmed(var1.nextText(), "directory"));
         } else if (this.checkFieldWithDuplicate(var1, "includes", null, var15)) {
            ArrayList var17 = new ArrayList();
            var5.setIncludes(var17);
            InputLocation var19 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("includes", var19);

            while (var1.nextTag() == 2) {
               if ("include".equals(var1.getName())) {
                  var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
                  var19.setLocation(var17.size(), var6);
                  var17.add(this.interpolatedTrimmed(var1.nextText(), "includes"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "excludes", null, var15)) {
            ArrayList var16 = new ArrayList();
            var5.setExcludes(var16);
            InputLocation var18 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("excludes", var18);

            while (var1.nextTag() == 2) {
               if ("exclude".equals(var1.getName())) {
                  var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
                  var18.setLocation(var16.size(), var6);
                  var16.add(this.interpolatedTrimmed(var1.nextText(), "excludes"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Scm parseScm(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Scm var5 = new Scm();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            if ("child.scm.connection.inherit.append.path".equals(var8)) {
               var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
               var5.setLocation("childScmConnectionInheritAppendPath", var6);
               var5.setChildScmConnectionInheritAppendPath(this.interpolatedTrimmed(var9, "child.scm.connection.inherit.append.path"));
            } else if ("child.scm.developerConnection.inherit.append.path".equals(var8)) {
               var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
               var5.setLocation("childScmDeveloperConnectionInheritAppendPath", var6);
               var5.setChildScmDeveloperConnectionInheritAppendPath(this.interpolatedTrimmed(var9, "child.scm.developerConnection.inherit.append.path"));
            } else if ("child.scm.url.inherit.append.path".equals(var8)) {
               var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
               var5.setLocation("childScmUrlInheritAppendPath", var6);
               var5.setChildScmUrlInheritAppendPath(this.interpolatedTrimmed(var9, "child.scm.url.inherit.append.path"));
            } else {
               this.checkUnknownAttribute(var1, var8, var4, var2);
            }
         }
      }

      HashSet var17 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "connection", null, var17)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("connection", var6);
            var5.setConnection(this.interpolatedTrimmed(var1.nextText(), "connection"));
         } else if (this.checkFieldWithDuplicate(var1, "developerConnection", null, var17)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("developerConnection", var6);
            var5.setDeveloperConnection(this.interpolatedTrimmed(var1.nextText(), "developerConnection"));
         } else if (this.checkFieldWithDuplicate(var1, "tag", null, var17)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("tag", var6);
            var5.setTag(this.interpolatedTrimmed(var1.nextText(), "tag"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var17)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("url", var6);
            var5.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Site parseSite(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      String var4 = var1.getName();
      Site var5 = new Site();
      InputLocation var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
      var5.setLocation("", var6);

      for (int var7 = var1.getAttributeCount() - 1; var7 >= 0; var7--) {
         String var8 = var1.getAttributeName(var7);
         String var9 = var1.getAttributeValue(var7);
         if (var8.indexOf(58) < 0) {
            if ("child.site.url.inherit.append.path".equals(var8)) {
               var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
               var5.setLocation("childSiteUrlInheritAppendPath", var6);
               var5.setChildSiteUrlInheritAppendPath(this.interpolatedTrimmed(var9, "child.site.url.inherit.append.path"));
            } else {
               this.checkUnknownAttribute(var1, var8, var4, var2);
            }
         }
      }

      HashSet var14 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "id", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("id", var6);
            var5.setId(this.interpolatedTrimmed(var1.nextText(), "id"));
         } else if (this.checkFieldWithDuplicate(var1, "name", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("name", var6);
            var5.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "url", null, var14)) {
            var6 = new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), var3);
            var5.setLocation("url", var6);
            var5.setUrl(this.interpolatedTrimmed(var1.nextText(), "url"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var5;
   }

   private Model read(XmlPullParser var1, boolean var2, InputSource var3) throws IOException, XmlPullParserException {
      Model var4 = null;
      int var5 = var1.getEventType();

      boolean var6;
      for (var6 = false; var5 != 1; var5 = var1.next()) {
         if (var5 == 2) {
            if (var2 && !"project".equals(var1.getName())) {
               throw new XmlPullParserException("Expected root element 'project' but found '" + var1.getName() + "'", var1, null);
            }

            if (var6) {
               throw new XmlPullParserException("Duplicated tag: 'project'", var1, null);
            }

            var4 = this.parseModel(var1, var2, var3);
            var4.setModelEncoding(var1.getInputEncoding());
            var6 = true;
         }
      }

      if (var6) {
         return var4;
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

   private static class Xpp3DomBuilderInputLocationBuilder implements Xpp3DomBuilder.InputLocationBuilder {
      private final InputLocation rootLocation;

      public Xpp3DomBuilderInputLocationBuilder(InputLocation var1) {
         this.rootLocation = var1;
      }

      @Override
      public Object toInputLocation(XmlPullParser var1) {
         return new InputLocation(var1.getLineNumber(), var1.getColumnNumber(), this.rootLocation.getSource());
      }
   }
}
