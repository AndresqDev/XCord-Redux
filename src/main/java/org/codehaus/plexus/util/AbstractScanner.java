package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

public abstract class AbstractScanner implements Scanner {
   public static final String[] DEFAULTEXCLUDES = new String[]{
      "**/*~",
      "**/#*#",
      "**/.#*",
      "**/%*%",
      "**/._*",
      "**/CVS",
      "**/CVS/**",
      "**/.cvsignore",
      "**/RCS",
      "**/RCS/**",
      "**/SCCS",
      "**/SCCS/**",
      "**/vssver.scc",
      "**/project.pj",
      "**/.svn",
      "**/.svn/**",
      "**/.arch-ids",
      "**/.arch-ids/**",
      "**/.bzr",
      "**/.bzr/**",
      "**/.MySCMServerInfo",
      "**/.DS_Store",
      "**/.metadata",
      "**/.metadata/**",
      "**/.hg",
      "**/.hgignore",
      "**/.hg/**",
      "**/.git",
      "**/.gitignore",
      "**/.gitattributes",
      "**/.git/**",
      "**/BitKeeper",
      "**/BitKeeper/**",
      "**/ChangeSet",
      "**/ChangeSet/**",
      "**/_darcs",
      "**/_darcs/**",
      "**/.darcsrepo",
      "**/.darcsrepo/**",
      "**/-darcs-backup*",
      "**/.darcs-temp-mail"
   };
   protected String[] includes;
   private MatchPatterns includesPatterns;
   protected String[] excludes;
   private MatchPatterns excludesPatterns;
   protected boolean isCaseSensitive = true;
   protected Comparator<String> filenameComparator;

   public void setCaseSensitive(boolean var1) {
      this.isCaseSensitive = var1;
   }

   protected static boolean matchPatternStart(String var0, String var1) {
      return SelectorUtils.matchPatternStart(var0, var1);
   }

   protected static boolean matchPatternStart(String var0, String var1, boolean var2) {
      return SelectorUtils.matchPatternStart(var0, var1, var2);
   }

   protected static boolean matchPath(String var0, String var1) {
      return SelectorUtils.matchPath(var0, var1);
   }

   protected static boolean matchPath(String var0, String var1, boolean var2) {
      return SelectorUtils.matchPath(var0, var1, var2);
   }

   public static boolean match(String var0, String var1) {
      return SelectorUtils.match(var0, var1);
   }

   protected static boolean match(String var0, String var1, boolean var2) {
      return SelectorUtils.match(var0, var1, var2);
   }

   @Override
   public void setIncludes(String[] var1) {
      if (var1 == null) {
         this.includes = null;
      } else {
         ArrayList var2 = new ArrayList(var1.length);

         for (String var6 : var1) {
            if (var6 != null) {
               var2.add(this.normalizePattern(var6));
            }
         }

         this.includes = var2.toArray(new String[var2.size()]);
      }
   }

   @Override
   public void setExcludes(String[] var1) {
      if (var1 == null) {
         this.excludes = null;
      } else {
         ArrayList var2 = new ArrayList(var1.length);

         for (String var6 : var1) {
            if (var6 != null) {
               var2.add(this.normalizePattern(var6));
            }
         }

         this.excludes = var2.toArray(new String[var2.size()]);
      }
   }

   private String normalizePattern(String var1) {
      var1 = var1.trim();
      if (var1.startsWith("%regex[")) {
         if (File.separatorChar == '\\') {
            var1 = StringUtils.replace(var1, "/", "\\\\");
         } else {
            var1 = StringUtils.replace(var1, "\\\\", "/");
         }
      } else {
         var1 = var1.replace((char)(File.separatorChar == '/' ? '\\' : '/'), File.separatorChar);
         if (var1.endsWith(File.separator)) {
            var1 = var1 + "**";
         }
      }

      return var1;
   }

   protected boolean isIncluded(String var1) {
      return this.includesPatterns.matches(var1, this.isCaseSensitive);
   }

   protected boolean isIncluded(String var1, String[] var2) {
      return this.includesPatterns.matches(var1, var2, this.isCaseSensitive);
   }

   protected boolean couldHoldIncluded(String var1) {
      return this.includesPatterns.matchesPatternStart(var1, this.isCaseSensitive);
   }

   protected boolean isExcluded(String var1) {
      return this.excludesPatterns.matches(var1, this.isCaseSensitive);
   }

   protected boolean isExcluded(String var1, String[] var2) {
      return this.excludesPatterns.matches(var1, var2, this.isCaseSensitive);
   }

   @Override
   public void addDefaultExcludes() {
      int var1 = this.excludes == null ? 0 : this.excludes.length;
      String[] var2 = new String[var1 + DEFAULTEXCLUDES.length];
      if (var1 > 0) {
         System.arraycopy(this.excludes, 0, var2, 0, var1);
      }

      for (int var3 = 0; var3 < DEFAULTEXCLUDES.length; var3++) {
         var2[var3 + var1] = DEFAULTEXCLUDES[var3].replace('/', File.separatorChar);
      }

      this.excludes = var2;
   }

   protected void setupDefaultFilters() {
      if (this.includes == null) {
         this.includes = new String[1];
         this.includes[0] = "**";
      }

      if (this.excludes == null) {
         this.excludes = new String[0];
      }
   }

   protected void setupMatchPatterns() {
      this.includesPatterns = MatchPatterns.from(this.includes);
      this.excludesPatterns = MatchPatterns.from(this.excludes);
   }

   @Override
   public void setFilenameComparator(Comparator<String> var1) {
      this.filenameComparator = var1;
   }
}
