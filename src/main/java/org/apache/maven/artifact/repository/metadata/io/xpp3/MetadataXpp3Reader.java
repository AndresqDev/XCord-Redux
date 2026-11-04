package org.apache.maven.artifact.repository.metadata.io.xpp3;

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
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Plugin;
import org.apache.maven.artifact.repository.metadata.Snapshot;
import org.apache.maven.artifact.repository.metadata.SnapshotVersion;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.pull.EntityReplacementMap;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class MetadataXpp3Reader {
   private boolean addDefaultEntities = true;
   public final MetadataXpp3Reader.ContentTransformer contentTransformer;

   public MetadataXpp3Reader() {
      this(new MetadataXpp3Reader.ContentTransformer() {
         @Override
         public String transform(String var1, String var2) {
            return var1;
         }
      });
   }

   public MetadataXpp3Reader(MetadataXpp3Reader.ContentTransformer var1) {
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

   public Metadata read(Reader var1, boolean var2) throws IOException, XmlPullParserException {
      MXParser var3 = this.addDefaultEntities ? new MXParser(EntityReplacementMap.defaultEntityReplacementMap) : new MXParser();
      var3.setInput(var1);
      return this.read(var3, var2);
   }

   public Metadata read(Reader var1) throws IOException, XmlPullParserException {
      return this.read(var1, true);
   }

   public Metadata read(InputStream var1, boolean var2) throws IOException, XmlPullParserException {
      return this.read(ReaderFactory.newXmlReader(var1), var2);
   }

   public Metadata read(InputStream var1) throws IOException, XmlPullParserException {
      return this.read(ReaderFactory.newXmlReader(var1));
   }

   private Metadata parseMetadata(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Metadata var4 = new Metadata();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0 && !"xmlns".equals(var6)) {
            if ("modelVersion".equals(var6)) {
               var4.setModelVersion(this.interpolatedTrimmed(var7, "modelVersion"));
            } else {
               this.checkUnknownAttribute(var1, var6, var3, var2);
            }
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "groupId", null, var8)) {
            var4.setGroupId(this.interpolatedTrimmed(var1.nextText(), "groupId"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var8)) {
            var4.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else if (this.checkFieldWithDuplicate(var1, "versioning", null, var8)) {
            var4.setVersioning(this.parseVersioning(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "version", null, var8)) {
            var4.setVersion(this.interpolatedTrimmed(var1.nextText(), "version"));
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
         if (this.checkFieldWithDuplicate(var1, "name", null, var8)) {
            var4.setName(this.interpolatedTrimmed(var1.nextText(), "name"));
         } else if (this.checkFieldWithDuplicate(var1, "prefix", null, var8)) {
            var4.setPrefix(this.interpolatedTrimmed(var1.nextText(), "prefix"));
         } else if (this.checkFieldWithDuplicate(var1, "artifactId", null, var8)) {
            var4.setArtifactId(this.interpolatedTrimmed(var1.nextText(), "artifactId"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Snapshot parseSnapshot(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Snapshot var4 = new Snapshot();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "timestamp", null, var8)) {
            var4.setTimestamp(this.interpolatedTrimmed(var1.nextText(), "timestamp"));
         } else if (this.checkFieldWithDuplicate(var1, "buildNumber", null, var8)) {
            var4.setBuildNumber(this.getIntegerValue(this.interpolatedTrimmed(var1.nextText(), "buildNumber"), "buildNumber", var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "localCopy", null, var8)) {
            var4.setLocalCopy(this.getBooleanValue(this.interpolatedTrimmed(var1.nextText(), "localCopy"), "localCopy", var1, "false"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private SnapshotVersion parseSnapshotVersion(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      SnapshotVersion var4 = new SnapshotVersion();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "classifier", null, var8)) {
            var4.setClassifier(this.interpolatedTrimmed(var1.nextText(), "classifier"));
         } else if (this.checkFieldWithDuplicate(var1, "extension", null, var8)) {
            var4.setExtension(this.interpolatedTrimmed(var1.nextText(), "extension"));
         } else if (this.checkFieldWithDuplicate(var1, "value", null, var8)) {
            var4.setVersion(this.interpolatedTrimmed(var1.nextText(), "value"));
         } else if (this.checkFieldWithDuplicate(var1, "updated", null, var8)) {
            var4.setUpdated(this.interpolatedTrimmed(var1.nextText(), "updated"));
         } else {
            this.checkUnknownElement(var1, var2);
         }
      }

      return var4;
   }

   private Versioning parseVersioning(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      String var3 = var1.getName();
      Versioning var4 = new Versioning();

      for (int var5 = var1.getAttributeCount() - 1; var5 >= 0; var5--) {
         String var6 = var1.getAttributeName(var5);
         String var7 = var1.getAttributeValue(var5);
         if (var6.indexOf(58) < 0) {
            this.checkUnknownAttribute(var1, var6, var3, var2);
         }
      }

      HashSet var8 = new HashSet();

      while ((var2 ? var1.nextTag() : this.nextTag(var1)) == 2) {
         if (this.checkFieldWithDuplicate(var1, "latest", null, var8)) {
            var4.setLatest(this.interpolatedTrimmed(var1.nextText(), "latest"));
         } else if (this.checkFieldWithDuplicate(var1, "release", null, var8)) {
            var4.setRelease(this.interpolatedTrimmed(var1.nextText(), "release"));
         } else if (this.checkFieldWithDuplicate(var1, "versions", null, var8)) {
            ArrayList var10 = new ArrayList();
            var4.setVersions(var10);

            while (var1.nextTag() == 2) {
               if ("version".equals(var1.getName())) {
                  var10.add(this.interpolatedTrimmed(var1.nextText(), "versions"));
               } else {
                  this.checkUnknownElement(var1, var2);
               }
            }
         } else if (this.checkFieldWithDuplicate(var1, "lastUpdated", null, var8)) {
            var4.setLastUpdated(this.interpolatedTrimmed(var1.nextText(), "lastUpdated"));
         } else if (this.checkFieldWithDuplicate(var1, "snapshot", null, var8)) {
            var4.setSnapshot(this.parseSnapshot(var1, var2));
         } else if (this.checkFieldWithDuplicate(var1, "snapshotVersions", null, var8)) {
            ArrayList var9 = new ArrayList();
            var4.setSnapshotVersions(var9);

            while (var1.nextTag() == 2) {
               if ("snapshotVersion".equals(var1.getName())) {
                  var9.add(this.parseSnapshotVersion(var1, var2));
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

   private Metadata read(XmlPullParser var1, boolean var2) throws IOException, XmlPullParserException {
      Metadata var3 = null;
      int var4 = var1.getEventType();

      boolean var5;
      for (var5 = false; var4 != 1; var4 = var1.next()) {
         if (var4 == 2) {
            if (var2 && !"metadata".equals(var1.getName())) {
               throw new XmlPullParserException("Expected root element 'metadata' but found '" + var1.getName() + "'", var1, null);
            }

            if (var5) {
               throw new XmlPullParserException("Duplicated tag: 'metadata'", var1, null);
            }

            var3 = this.parseMetadata(var1, var2);
            var3.setModelEncoding(var1.getInputEncoding());
            var5 = true;
         }
      }

      if (var5) {
         return var3;
      } else {
         throw new XmlPullParserException("Expected root element 'metadata' but found no element at all: invalid XML document", var1, null);
      }
   }

   public void setAddDefaultEntities(boolean var1) {
      this.addDefaultEntities = var1;
   }

   public interface ContentTransformer {
      String transform(String var1, String var2);
   }
}
