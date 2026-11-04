package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DirectoryWalker {
   private File baseDir;
   private int baseDirOffset;
   private Stack<DirectoryWalker.DirStackEntry> dirStack;
   private List<String> excludes;
   private List<String> includes;
   private boolean isCaseSensitive = true;
   private List<DirectoryWalkListener> listeners;
   private boolean debugEnabled = false;

   public DirectoryWalker() {
      this.includes = new ArrayList<>();
      this.excludes = new ArrayList<>();
      this.listeners = new ArrayList<>();
   }

   public void addDirectoryWalkListener(DirectoryWalkListener var1) {
      this.listeners.add(var1);
   }

   public void addExclude(String var1) {
      this.excludes.add(this.fixPattern(var1));
   }

   public void addInclude(String var1) {
      this.includes.add(this.fixPattern(var1));
   }

   public void addSCMExcludes() {
      String[] var1 = DirectoryScanner.DEFAULTEXCLUDES;

      for (String var5 : var1) {
         this.addExclude(var5);
      }
   }

   private void fireStep(File var1) {
      DirectoryWalker.DirStackEntry var2 = this.dirStack.peek();
      int var3 = var2.getPercentage();

      for (Object var5 : this.listeners) {
         DirectoryWalkListener var6 = (DirectoryWalkListener)var5;
         var6.directoryWalkStep(var3, var1);
      }
   }

   private void fireWalkFinished() {
      for (DirectoryWalkListener var2 : this.listeners) {
         var2.directoryWalkFinished();
      }
   }

   private void fireWalkStarting() {
      for (DirectoryWalkListener var2 : this.listeners) {
         var2.directoryWalkStarting(this.baseDir);
      }
   }

   private void fireDebugMessage(String var1) {
      for (DirectoryWalkListener var3 : this.listeners) {
         var3.debug(var1);
      }
   }

   private String fixPattern(String var1) {
      String var2 = var1;
      if (File.separatorChar != '/') {
         var2 = var1.replace('/', File.separatorChar);
      }

      if (File.separatorChar != '\\') {
         var2 = var2.replace('\\', File.separatorChar);
      }

      return var2;
   }

   public void setDebugMode(boolean var1) {
      this.debugEnabled = var1;
   }

   public File getBaseDir() {
      return this.baseDir;
   }

   public List<String> getExcludes() {
      return this.excludes;
   }

   public List<String> getIncludes() {
      return this.includes;
   }

   private boolean isExcluded(String var1) {
      return this.isMatch(this.excludes, var1);
   }

   private boolean isIncluded(String var1) {
      return this.isMatch(this.includes, var1);
   }

   private boolean isMatch(List<String> var1, String var2) {
      for (String var4 : var1) {
         if (SelectorUtils.matchPath(var4, var2, this.isCaseSensitive)) {
            return true;
         }
      }

      return false;
   }

   private String relativeToBaseDir(File var1) {
      return var1.getAbsolutePath().substring(this.baseDirOffset + 1);
   }

   public void removeDirectoryWalkListener(DirectoryWalkListener var1) {
      this.listeners.remove(var1);
   }

   public void scan() {
      if (this.baseDir == null) {
         throw new IllegalStateException("Scan Failure.  BaseDir not specified.");
      } else if (!this.baseDir.exists()) {
         throw new IllegalStateException("Scan Failure.  BaseDir does not exist.");
      } else if (!this.baseDir.isDirectory()) {
         throw new IllegalStateException("Scan Failure.  BaseDir is not a directory.");
      } else {
         if (this.includes.isEmpty()) {
            this.addInclude("**");
         }

         if (this.debugEnabled) {
            StringBuilder var2 = new StringBuilder();
            var2.append("DirectoryWalker Scan");
            var2.append("\n  Base Dir: ").append(this.baseDir.getAbsolutePath());
            var2.append("\n  Includes: ");

            for (String var3 : this.includes) {
               var2.append("\n    - \"").append(var3).append("\"");
            }

            var2.append("\n  Excludes: ");

            for (String var5 : this.excludes) {
               var2.append("\n    - \"").append(var5).append("\"");
            }

            this.fireDebugMessage(var2.toString());
         }

         this.fireWalkStarting();
         this.dirStack = new Stack<>();
         this.scanDir(this.baseDir);
         this.fireWalkFinished();
      }
   }

   private void scanDir(File var1) {
      File[] var2 = var1.listFiles();
      if (var2 != null) {
         DirectoryWalker.DirStackEntry var3 = new DirectoryWalker.DirStackEntry(var1, var2.length);
         if (this.dirStack.isEmpty()) {
            var3.percentageOffset = 0.0;
            var3.percentageSize = 100.0;
         } else {
            DirectoryWalker.DirStackEntry var4 = this.dirStack.peek();
            var3.percentageOffset = var4.getNextPercentageOffset();
            var3.percentageSize = var4.getNextPercentageSize();
         }

         this.dirStack.push(var3);

         for (int var6 = 0; var6 < var2.length; var6++) {
            var3.index = var6;
            String var5 = this.relativeToBaseDir(var2[var6]);
            if (this.isExcluded(var5)) {
               this.fireDebugMessage(var5 + " is excluded.");
            } else if (var2[var6].isDirectory()) {
               this.scanDir(var2[var6]);
            } else if (this.isIncluded(var5)) {
               this.fireStep(var2[var6]);
            }
         }

         this.dirStack.pop();
      }
   }

   public void setBaseDir(File var1) {
      this.baseDir = var1;
      this.baseDirOffset = var1.getAbsolutePath().length();
   }

   public void setExcludes(List<String> var1) {
      this.excludes.clear();
      if (var1 != null) {
         for (String var3 : var1) {
            this.excludes.add(this.fixPattern(var3));
         }
      }
   }

   public void setIncludes(List<String> var1) {
      this.includes.clear();
      if (var1 != null) {
         for (String var3 : var1) {
            this.includes.add(this.fixPattern(var3));
         }
      }
   }

   class DirStackEntry {
      public int count;
      public File dir;
      public int index;
      public double percentageOffset;
      public double percentageSize;

      public DirStackEntry(File var2, int var3) {
         this.dir = var2;
         this.count = var3;
      }

      public double getNextPercentageOffset() {
         return this.percentageOffset + (double)this.index * (this.percentageSize / (double)this.count);
      }

      public double getNextPercentageSize() {
         return this.percentageSize / (double)this.count;
      }

      public int getPercentage() {
         double var1 = (double)this.index / (double)this.count;
         return (int)Math.floor(this.percentageOffset + var1 * this.percentageSize);
      }

      @Override
      public String toString() {
         return "DirStackEntry[dir="
            + this.dir.getAbsolutePath()
            + ",count="
            + this.count
            + ",index="
            + this.index
            + ",percentageOffset="
            + this.percentageOffset
            + ",percentageSize="
            + this.percentageSize
            + ",percentage()="
            + this.getPercentage()
            + ",getNextPercentageOffset()="
            + this.getNextPercentageOffset()
            + ",getNextPercentageSize()="
            + this.getNextPercentageSize()
            + "]";
      }
   }
}
