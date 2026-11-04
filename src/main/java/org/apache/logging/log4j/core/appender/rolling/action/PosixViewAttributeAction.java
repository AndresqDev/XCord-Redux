package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "PosixViewAttribute",
   category = "Core",
   printObject = true
)
public class PosixViewAttributeAction extends AbstractPathAction {
   private final Set<PosixFilePermission> filePermissions;
   private final String fileOwner;
   private final String fileGroup;

   private PosixViewAttributeAction(
      String var1, boolean var2, int var3, PathCondition[] var4, StrSubstitutor var5, Set<PosixFilePermission> var6, String var7, String var8
   ) {
      super(var1, var2, var3, var4, var5);
      this.filePermissions = var6;
      this.fileOwner = var7;
      this.fileGroup = var8;
   }

   @PluginBuilderFactory
   public static PosixViewAttributeAction.Builder newBuilder() {
      return new PosixViewAttributeAction.Builder();
   }

   @Override
   protected FileVisitor<Path> createFileVisitor(final Path var1, final List<PathCondition> var2) {
      return new SimpleFileVisitor<Path>() {
         public FileVisitResult visitFile(Path var1x, BasicFileAttributes var2x) throws IOException {
            for (PathCondition var4 : var2) {
               Path var5 = var1.relativize(var1x);
               if (!var4.accept(var1, var5, var2x)) {
                  AbstractAction.LOGGER.trace("Not defining posix attribute base={}, relative={}", var1, var5);
                  return FileVisitResult.CONTINUE;
               }
            }

            FileUtils.defineFilePosixAttributeView(
               var1x, PosixViewAttributeAction.this.filePermissions, PosixViewAttributeAction.this.fileOwner, PosixViewAttributeAction.this.fileGroup
            );
            return FileVisitResult.CONTINUE;
         }
      };
   }

   public Set<PosixFilePermission> getFilePermissions() {
      return this.filePermissions;
   }

   public String getFileOwner() {
      return this.fileOwner;
   }

   public String getFileGroup() {
      return this.fileGroup;
   }

   @Override
   public String toString() {
      return "PosixViewAttributeAction [filePermissions="
         + this.filePermissions
         + ", fileOwner="
         + this.fileOwner
         + ", fileGroup="
         + this.fileGroup
         + ", getBasePath()="
         + this.getBasePath()
         + ", getMaxDepth()="
         + this.getMaxDepth()
         + ", getPathConditions()="
         + this.getPathConditions()
         + "]";
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<PosixViewAttributeAction> {
      @PluginConfiguration
      private Configuration configuration;
      private StrSubstitutor subst;
      @PluginBuilderAttribute
      @Required(
         message = "No base path provided"
      )
      private String basePath;
      @PluginBuilderAttribute
      private boolean followLinks = false;
      @PluginBuilderAttribute
      private int maxDepth = 1;
      @PluginElement("PathConditions")
      private PathCondition[] pathConditions;
      @PluginBuilderAttribute("filePermissions")
      private String filePermissionsString;
      private Set<PosixFilePermission> filePermissions;
      @PluginBuilderAttribute
      private String fileOwner;
      @PluginBuilderAttribute
      private String fileGroup;

      public PosixViewAttributeAction build() {
         if (Strings.isEmpty(this.basePath)) {
            AbstractAction.LOGGER.error("Posix file attribute view action not valid because base path is empty.");
            return null;
         } else if (this.filePermissions == null
            && Strings.isEmpty(this.filePermissionsString)
            && Strings.isEmpty(this.fileOwner)
            && Strings.isEmpty(this.fileGroup)) {
            AbstractAction.LOGGER.error("Posix file attribute view not valid because nor permissions, user or group defined.");
            return null;
         } else if (!FileUtils.isFilePosixAttributeViewSupported()) {
            AbstractAction.LOGGER.warn("Posix file attribute view defined but it is not supported by this files system.");
            return null;
         } else {
            return new PosixViewAttributeAction(
               this.basePath,
               this.followLinks,
               this.maxDepth,
               this.pathConditions,
               this.subst != null ? this.subst : this.configuration.getStrSubstitutor(),
               this.filePermissions != null
                  ? this.filePermissions
                  : (this.filePermissionsString != null ? PosixFilePermissions.fromString(this.filePermissionsString) : null),
               this.fileOwner,
               this.fileGroup
            );
         }
      }

      public PosixViewAttributeAction.Builder withConfiguration(Configuration var1) {
         this.configuration = var1;
         return this;
      }

      public PosixViewAttributeAction.Builder withSubst(StrSubstitutor var1) {
         this.subst = var1;
         return this;
      }

      public PosixViewAttributeAction.Builder withBasePath(String var1) {
         this.basePath = var1;
         return this;
      }

      public PosixViewAttributeAction.Builder withFollowLinks(boolean var1) {
         this.followLinks = var1;
         return this;
      }

      public PosixViewAttributeAction.Builder withMaxDepth(int var1) {
         this.maxDepth = var1;
         return this;
      }

      public PosixViewAttributeAction.Builder withPathConditions(PathCondition[] var1) {
         this.pathConditions = var1;
         return this;
      }

      public PosixViewAttributeAction.Builder withFilePermissionsString(String var1) {
         this.filePermissionsString = var1;
         return this;
      }

      public PosixViewAttributeAction.Builder withFilePermissions(Set<PosixFilePermission> var1) {
         this.filePermissions = var1;
         return this;
      }

      public PosixViewAttributeAction.Builder withFileOwner(String var1) {
         this.fileOwner = var1;
         return this;
      }

      public PosixViewAttributeAction.Builder withFileGroup(String var1) {
         this.fileGroup = var1;
         return this;
      }
   }
}
