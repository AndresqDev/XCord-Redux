package org.codehaus.plexus.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class DirectoryScanner extends AbstractScanner {
   protected File basedir;
   protected Vector<String> filesIncluded;
   protected Vector<String> filesNotIncluded;
   protected Vector<String> filesExcluded;
   protected Vector<String> dirsIncluded;
   protected Vector<String> dirsNotIncluded;
   protected Vector<String> dirsExcluded;
   protected Vector<String> filesDeselected;
   protected Vector<String> dirsDeselected;
   protected boolean haveSlowResults = false;
   private boolean followSymlinks = true;
   protected boolean everythingIncluded = true;
   private final String[] tokenizedEmpty = MatchPattern.tokenizePathToString("", File.separator);

   public void setBasedir(String var1) {
      this.setBasedir(new File(var1.replace('/', File.separatorChar).replace('\\', File.separatorChar)));
   }

   public void setBasedir(File var1) {
      this.basedir = var1;
   }

   @Override
   public File getBasedir() {
      return this.basedir;
   }

   public void setFollowSymlinks(boolean var1) {
      this.followSymlinks = var1;
   }

   public boolean isEverythingIncluded() {
      return this.everythingIncluded;
   }

   @Override
   public void scan() throws IllegalStateException {
      if (this.basedir == null) {
         throw new IllegalStateException("No basedir set");
      } else if (!this.basedir.exists()) {
         throw new IllegalStateException("basedir " + this.basedir + " does not exist");
      } else if (!this.basedir.isDirectory()) {
         throw new IllegalStateException("basedir " + this.basedir + " is not a directory");
      } else {
         this.setupDefaultFilters();
         this.setupMatchPatterns();
         this.filesIncluded = new Vector<>();
         this.filesNotIncluded = new Vector<>();
         this.filesExcluded = new Vector<>();
         this.filesDeselected = new Vector<>();
         this.dirsIncluded = new Vector<>();
         this.dirsNotIncluded = new Vector<>();
         this.dirsExcluded = new Vector<>();
         this.dirsDeselected = new Vector<>();
         if (this.isIncluded("", this.tokenizedEmpty)) {
            if (!this.isExcluded("", this.tokenizedEmpty)) {
               if (this.isSelected("", this.basedir)) {
                  this.dirsIncluded.addElement("");
               } else {
                  this.dirsDeselected.addElement("");
               }
            } else {
               this.dirsExcluded.addElement("");
            }
         } else {
            this.dirsNotIncluded.addElement("");
         }

         this.scandir(this.basedir, "", true);
      }
   }

   protected void slowScan() {
      if (!this.haveSlowResults) {
         String[] var1 = new String[this.dirsExcluded.size()];
         this.dirsExcluded.copyInto(var1);
         String[] var2 = new String[this.dirsNotIncluded.size()];
         this.dirsNotIncluded.copyInto(var2);

         for (String var6 : var1) {
            if (!this.couldHoldIncluded(var6)) {
               this.scandir(new File(this.basedir, var6), var6 + File.separator, false);
            }
         }

         for (String var10 : var2) {
            if (!this.couldHoldIncluded(var10)) {
               this.scandir(new File(this.basedir, var10), var10 + File.separator, false);
            }
         }

         this.haveSlowResults = true;
      }
   }

   protected void scandir(File var1, String var2, boolean var3) {
      String[] var4 = var1.list();
      if (var4 == null) {
         var4 = new String[0];
      }

      if (!this.followSymlinks) {
         ArrayList var5 = new ArrayList();

         for (String var9 : var4) {
            try {
               if (this.isParentSymbolicLink(var1, var9)) {
                  String var10 = var2 + var9;
                  File var19 = new File(var1, var9);
                  if (var19.isDirectory()) {
                     this.dirsExcluded.addElement(var10);
                  } else {
                     this.filesExcluded.addElement(var10);
                  }
               } else {
                  var5.add(var9);
               }
            } catch (IOException var12) {
               String var11 = "IOException caught while checking for links, couldn't get canonical path!";
               System.err.println(var11);
               var5.add(var9);
            }
         }

         var4 = var5.toArray(new String[var5.size()]);
      }

      if (this.filenameComparator != null) {
         Arrays.sort(var4, this.filenameComparator);
      }

      for (String var16 : var4) {
         String var17 = var2 + var16;
         String[] var18 = MatchPattern.tokenizePathToString(var17, File.separator);
         File var20 = new File(var1, var16);
         if (var20.isDirectory()) {
            if (this.isIncluded(var17, var18)) {
               if (!this.isExcluded(var17, var18)) {
                  if (this.isSelected(var17, var20)) {
                     this.dirsIncluded.addElement(var17);
                     if (var3) {
                        this.scandir(var20, var17 + File.separator, var3);
                     }
                  } else {
                     this.everythingIncluded = false;
                     this.dirsDeselected.addElement(var17);
                     if (var3 && this.couldHoldIncluded(var17)) {
                        this.scandir(var20, var17 + File.separator, var3);
                     }
                  }
               } else {
                  this.everythingIncluded = false;
                  this.dirsExcluded.addElement(var17);
                  if (var3 && this.couldHoldIncluded(var17)) {
                     this.scandir(var20, var17 + File.separator, var3);
                  }
               }
            } else {
               this.everythingIncluded = false;
               this.dirsNotIncluded.addElement(var17);
               if (var3 && this.couldHoldIncluded(var17)) {
                  this.scandir(var20, var17 + File.separator, var3);
               }
            }

            if (!var3) {
               this.scandir(var20, var17 + File.separator, var3);
            }
         } else if (var20.isFile()) {
            if (this.isIncluded(var17, var18)) {
               if (!this.isExcluded(var17, var18)) {
                  if (this.isSelected(var17, var20)) {
                     this.filesIncluded.addElement(var17);
                  } else {
                     this.everythingIncluded = false;
                     this.filesDeselected.addElement(var17);
                  }
               } else {
                  this.everythingIncluded = false;
                  this.filesExcluded.addElement(var17);
               }
            } else {
               this.everythingIncluded = false;
               this.filesNotIncluded.addElement(var17);
            }
         }
      }
   }

   protected boolean isSelected(String var1, File var2) {
      return true;
   }

   @Override
   public String[] getIncludedFiles() {
      String[] var1 = new String[this.filesIncluded.size()];
      this.filesIncluded.copyInto(var1);
      return var1;
   }

   public String[] getNotIncludedFiles() {
      this.slowScan();
      String[] var1 = new String[this.filesNotIncluded.size()];
      this.filesNotIncluded.copyInto(var1);
      return var1;
   }

   public String[] getExcludedFiles() {
      this.slowScan();
      String[] var1 = new String[this.filesExcluded.size()];
      this.filesExcluded.copyInto(var1);
      return var1;
   }

   public String[] getDeselectedFiles() {
      this.slowScan();
      String[] var1 = new String[this.filesDeselected.size()];
      this.filesDeselected.copyInto(var1);
      return var1;
   }

   @Override
   public String[] getIncludedDirectories() {
      String[] var1 = new String[this.dirsIncluded.size()];
      this.dirsIncluded.copyInto(var1);
      return var1;
   }

   public String[] getNotIncludedDirectories() {
      this.slowScan();
      String[] var1 = new String[this.dirsNotIncluded.size()];
      this.dirsNotIncluded.copyInto(var1);
      return var1;
   }

   public String[] getExcludedDirectories() {
      this.slowScan();
      String[] var1 = new String[this.dirsExcluded.size()];
      this.dirsExcluded.copyInto(var1);
      return var1;
   }

   public String[] getDeselectedDirectories() {
      this.slowScan();
      String[] var1 = new String[this.dirsDeselected.size()];
      this.dirsDeselected.copyInto(var1);
      return var1;
   }

   public boolean isSymbolicLink(File var1, String var2) throws IOException {
      if (Java7Detector.isJava7()) {
         return NioFiles.isSymbolicLink(new File(var1, var2));
      } else {
         File var3 = new File(var1.getCanonicalPath());
         File var4 = new File(var3, var2);
         return !var4.getAbsolutePath().equals(var4.getCanonicalPath());
      }
   }

   public boolean isParentSymbolicLink(File var1, String var2) throws IOException {
      if (Java7Detector.isJava7()) {
         return NioFiles.isSymbolicLink(var1);
      } else {
         File var3 = new File(var1.getCanonicalPath());
         File var4 = new File(var3, var2);
         return !var4.getAbsolutePath().equals(var4.getCanonicalPath());
      }
   }
}
