package org.codehaus.plexus.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.codehaus.plexus.util.io.InputStreamFacade;
import org.codehaus.plexus.util.io.URLInputStreamFacade;

public class FileUtils {
   public static final int ONE_KB = 1024;
   public static final int ONE_MB = 1048576;
   public static final int ONE_GB = 1073741824;
   private static final long FILE_COPY_BUFFER_SIZE = 31457280L;
   public static String FS = System.getProperty("file.separator");
   private static final String[] INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME = new String[]{":", "*", "?", "\"", "<", ">", "|"};

   public static String[] getDefaultExcludes() {
      return DirectoryScanner.DEFAULTEXCLUDES;
   }

   public static List<String> getDefaultExcludesAsList() {
      return Arrays.asList(getDefaultExcludes());
   }

   public static String getDefaultExcludesAsString() {
      return StringUtils.join(DirectoryScanner.DEFAULTEXCLUDES, ",");
   }

   public static String byteCountToDisplaySize(int var0) {
      String var1;
      if (var0 / 1073741824 > 0) {
         var1 = var0 / 1073741824 + " GB";
      } else if (var0 / 1048576 > 0) {
         var1 = var0 / 1048576 + " MB";
      } else if (var0 / 1024 > 0) {
         var1 = var0 / 1024 + " KB";
      } else {
         var1 = var0 + " bytes";
      }

      return var1;
   }

   public static String dirname(String var0) {
      int var1 = var0.lastIndexOf(File.separator);
      return var1 >= 0 ? var0.substring(0, var1) : "";
   }

   public static String filename(String var0) {
      int var1 = var0.lastIndexOf(File.separator);
      return var1 >= 0 ? var0.substring(var1 + 1) : var0;
   }

   public static String basename(String var0) {
      return basename(var0, extension(var0));
   }

   public static String basename(String var0, String var1) {
      int var2 = var0.lastIndexOf(File.separator) + 1;
      int var3 = var1 != null && var1.length() > 0 ? var0.lastIndexOf(var1) : -1;
      if (var3 >= 0) {
         return var0.substring(var2, var3);
      } else {
         return var2 > 0 ? var0.substring(var2) : var0;
      }
   }

   public static String extension(String var0) {
      int var1 = var0.lastIndexOf(File.separatorChar);
      int var2;
      if (var1 < 0) {
         var2 = var0.lastIndexOf(46);
      } else {
         var2 = var0.substring(var1 + 1).lastIndexOf(46);
         if (var2 >= 0) {
            var2 += var1 + 1;
         }
      }

      return var2 >= 0 && var2 > var1 ? var0.substring(var2 + 1) : "";
   }

   public static boolean fileExists(String var0) {
      File var1 = new File(var0);
      return var1.exists();
   }

   public static String fileRead(String var0) throws IOException {
      return fileRead(var0, null);
   }

   public static String fileRead(String var0, String var1) throws IOException {
      return fileRead(new File(var0), var1);
   }

   public static String fileRead(File var0) throws IOException {
      return fileRead(var0, null);
   }

   public static String fileRead(File var0, String var1) throws IOException {
      StringBuilder var2 = new StringBuilder();
      InputStreamReader var3 = null;

      try {
         if (var1 != null) {
            var3 = new InputStreamReader(new FileInputStream(var0), var1);
         } else {
            var3 = new InputStreamReader(new FileInputStream(var0));
         }

         char[] var5 = new char[512];

         int var4;
         while ((var4 = var3.read(var5)) >= 0) {
            var2.append(var5, 0, var4);
         }

         var3.close();
         var3 = null;
      } finally {
         IOUtil.close(var3);
      }

      return var2.toString();
   }

   public static void fileAppend(String var0, String var1) throws IOException {
      fileAppend(var0, null, var1);
   }

   public static void fileAppend(String var0, String var1, String var2) throws IOException {
      FileOutputStream var3 = null;

      try {
         var3 = new FileOutputStream(var0, true);
         if (var1 != null) {
            var3.write(var2.getBytes(var1));
         } else {
            var3.write(var2.getBytes());
         }

         var3.close();
         var3 = null;
      } finally {
         IOUtil.close(var3);
      }
   }

   public static void fileWrite(String var0, String var1) throws IOException {
      fileWrite(var0, null, var1);
   }

   public static void fileWrite(String var0, String var1, String var2) throws IOException {
      File var3 = var0 == null ? null : new File(var0);
      fileWrite(var3, var1, var2);
   }

   public static void fileWrite(File var0, String var1) throws IOException {
      fileWrite(var0, null, var1);
   }

   public static void fileWrite(File var0, String var1, String var2) throws IOException {
      OutputStreamWriter var3 = null;

      try {
         FileOutputStream var4 = new FileOutputStream(var0);
         if (var1 != null) {
            var3 = new OutputStreamWriter(var4, var1);
         } else {
            var3 = new OutputStreamWriter(var4);
         }

         var3.write(var2);
         var3.close();
         var3 = null;
      } finally {
         IOUtil.close(var3);
      }
   }

   public static void fileDelete(String var0) {
      File var1 = new File(var0);
      if (Java7Detector.isJava7()) {
         try {
            NioFiles.deleteIfExists(var1);
         } catch (IOException var3) {
            throw new RuntimeException(var3);
         }
      } else {
         var1.delete();
      }
   }

   public static boolean waitFor(String var0, int var1) {
      return waitFor(new File(var0), var1);
   }

   public static boolean waitFor(File var0, int var1) {
      int var2 = 0;
      int var3 = 0;

      while (!var0.exists()) {
         if (var3++ >= 10) {
            var3 = 0;
            if (var2++ > var1) {
               return false;
            }
         }

         try {
            Thread.sleep(100L);
         } catch (InterruptedException var5) {
         }
      }

      return true;
   }

   public static File getFile(String var0) {
      return new File(var0);
   }

   public static String[] getFilesFromExtension(String var0, String[] var1) {
      Object var2 = new ArrayList();
      File var3 = new File(var0);
      String[] var4 = var3.list();
      if (var4 == null) {
         return new String[0];
      } else {
         for (String var8 : var4) {
            String var9 = var0 + System.getProperty("file.separator") + var8;
            File var10 = new File(var9);
            if (var10.isDirectory()) {
               if (!var10.getName().equals("CVS")) {
                  String[] var11 = getFilesFromExtension(var9, var1);
                  var2 = blendFilesToVector((List<String>)var2, var11);
               }
            } else {
               String var13 = var10.getAbsolutePath();
               if (isValidFile(var13, var1)) {
                  var2.add(var13);
               }
            }
         }

         String[] var12 = new String[var2.size()];
         var2.toArray(var12);
         return var12;
      }
   }

   private static List<String> blendFilesToVector(List<String> var0, String[] var1) {
      for (String var5 : var1) {
         var0.add(var5);
      }

      return var0;
   }

   private static boolean isValidFile(String var0, String[] var1) {
      String var2 = extension(var0);
      if (var2 == null) {
         var2 = "";
      }

      for (String var6 : var1) {
         if (var6.equals(var2)) {
            return true;
         }
      }

      return false;
   }

   public static void mkdir(String var0) {
      File var1 = new File(var0);
      if (Os.isFamily("windows") && !isValidWindowsFileName(var1)) {
         throw new IllegalArgumentException(
            "The file (" + var0 + ") cannot contain any of the following characters: \n" + StringUtils.join(INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME, " ")
         );
      } else {
         if (!var1.exists()) {
            var1.mkdirs();
         }
      }
   }

   public static boolean contentEquals(File var0, File var1) throws IOException {
      boolean var2 = var0.exists();
      if (var2 != var1.exists()) {
         return false;
      } else if (!var2) {
         return true;
      } else if (!var0.isDirectory() && !var1.isDirectory()) {
         FileInputStream var3 = null;
         FileInputStream var4 = null;
         boolean var5 = false;

         try {
            var3 = new FileInputStream(var0);
            var4 = new FileInputStream(var1);
            var5 = IOUtil.contentEquals(var3, var4);
            var3.close();
            var3 = null;
            var4.close();
            var4 = null;
         } finally {
            IOUtil.close(var3);
            IOUtil.close(var4);
         }

         return var5;
      } else {
         return false;
      }
   }

   public static File toFile(URL var0) {
      if (var0 != null && var0.getProtocol().equalsIgnoreCase("file")) {
         String var1 = var0.getFile().replace('/', File.separatorChar);
         int var2 = -1;

         while ((var2 = var1.indexOf(37, var2 + 1)) >= 0) {
            if (var2 + 2 < var1.length()) {
               String var3 = var1.substring(var2 + 1, var2 + 3);
               char var4 = (char)Integer.parseInt(var3, 16);
               var1 = var1.substring(0, var2) + var4 + var1.substring(var2 + 3);
            }
         }

         return new File(var1);
      } else {
         return null;
      }
   }

   public static URL[] toURLs(File[] var0) throws IOException {
      URL[] var1 = new URL[var0.length];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = var0[var2].toURI().toURL();
      }

      return var1;
   }

   public static String removeExtension(String var0) {
      String var1 = extension(var0);
      if ("".equals(var1)) {
         return var0;
      } else {
         int var2 = var0.lastIndexOf(var1) - 1;
         return var0.substring(0, var2);
      }
   }

   public static String getExtension(String var0) {
      return extension(var0);
   }

   public static String removePath(String var0) {
      return removePath(var0, File.separatorChar);
   }

   public static String removePath(String var0, char var1) {
      int var2 = var0.lastIndexOf(var1);
      return -1 == var2 ? var0 : var0.substring(var2 + 1);
   }

   public static String getPath(String var0) {
      return getPath(var0, File.separatorChar);
   }

   public static String getPath(String var0, char var1) {
      int var2 = var0.lastIndexOf(var1);
      return -1 == var2 ? "" : var0.substring(0, var2);
   }

   public static void copyFileToDirectory(String var0, String var1) throws IOException {
      copyFileToDirectory(new File(var0), new File(var1));
   }

   public static void copyFileToDirectoryIfModified(String var0, String var1) throws IOException {
      copyFileToDirectoryIfModified(new File(var0), new File(var1));
   }

   public static void copyFileToDirectory(File var0, File var1) throws IOException {
      if (var1.exists() && !var1.isDirectory()) {
         throw new IllegalArgumentException("Destination is not a directory");
      } else {
         copyFile(var0, new File(var1, var0.getName()));
      }
   }

   public static void copyFileToDirectoryIfModified(File var0, File var1) throws IOException {
      if (var1.exists() && !var1.isDirectory()) {
         throw new IllegalArgumentException("Destination is not a directory");
      } else {
         copyFileIfModified(var0, new File(var1, var0.getName()));
      }
   }

   public static void mkDirs(File var0, String[] var1, File var2) throws IOException {
      for (String var6 : var1) {
         File var7 = new File(var0, var6);
         File var8 = new File(var2, var6);
         if (Java7Detector.isJava7() && NioFiles.isSymbolicLink(var7)) {
            File var9 = NioFiles.readSymbolicLink(var7);
            NioFiles.createSymbolicLink(var8, var9);
         } else {
            var8.mkdirs();
         }
      }
   }

   public static void copyFile(File var0, File var1) throws IOException {
      if (!var0.exists()) {
         String var3 = "File " + var0 + " does not exist";
         throw new IOException(var3);
      } else if (!var0.getCanonicalPath().equals(var1.getCanonicalPath())) {
         mkdirsFor(var1);
         doCopyFile(var0, var1);
         if (var0.length() != var1.length()) {
            String var2 = "Failed to copy full contents from " + var0 + " to " + var1;
            throw new IOException(var2);
         }
      }
   }

   private static void doCopyFile(File var0, File var1) throws IOException {
      if (Java7Detector.isJava7()) {
         doCopyFileUsingNewIO(var0, var1);
      } else {
         doCopyFileUsingLegacyIO(var0, var1);
      }
   }

   private static void doCopyFileUsingLegacyIO(File var0, File var1) throws IOException {
      FileInputStream var2 = null;
      FileOutputStream var3 = null;
      FileChannel var4 = null;
      FileChannel var5 = null;

      try {
         var2 = new FileInputStream(var0);
         var3 = new FileOutputStream(var1);
         var4 = var2.getChannel();
         var5 = var3.getChannel();
         long var6 = var4.size();
         long var8 = 0L;
         long var10 = 0L;

         while (var8 < var6) {
            var10 = var6 - var8 > 31457280L ? 31457280L : var6 - var8;
            var8 += var5.transferFrom(var4, var8, var10);
         }

         var5.close();
         var5 = null;
         var3.close();
         var3 = null;
         var4.close();
         var4 = null;
         var2.close();
         var2 = null;
      } finally {
         IOUtil.close(var5);
         IOUtil.close(var3);
         IOUtil.close(var4);
         IOUtil.close(var2);
      }
   }

   private static void doCopyFileUsingNewIO(File var0, File var1) throws IOException {
      NioFiles.copy(var0, var1);
   }

   public static boolean copyFileIfModified(File var0, File var1) throws IOException {
      if (isSourceNewerThanDestination(var0, var1)) {
         copyFile(var0, var1);
         return true;
      } else {
         return false;
      }
   }

   public static void copyURLToFile(URL var0, File var1) throws IOException {
      copyStreamToFile(new URLInputStreamFacade(var0), var1);
   }

   public static void copyStreamToFile(InputStreamFacade var0, File var1) throws IOException {
      mkdirsFor(var1);
      checkCanWrite(var1);
      InputStream var2 = null;
      FileOutputStream var3 = null;

      try {
         var2 = var0.getInputStream();
         var3 = new FileOutputStream(var1);
         IOUtil.copy(var2, var3);
         var3.close();
         var3 = null;
         var2.close();
         var2 = null;
      } finally {
         IOUtil.close(var2);
         IOUtil.close(var3);
      }
   }

   private static void checkCanWrite(File var0) throws IOException {
      if (var0.exists() && !var0.canWrite()) {
         String var1 = "Unable to open file " + var0 + " for writing.";
         throw new IOException(var1);
      }
   }

   private static void mkdirsFor(File var0) {
      File var1 = var0.getParentFile();
      if (var1 != null && !var1.exists()) {
         var1.mkdirs();
      }
   }

   public static String normalize(String var0) {
      String var1 = var0;

      while (true) {
         int var2 = var1.indexOf("//");
         if (var2 < 0) {
            while (true) {
               var2 = var1.indexOf("/./");
               if (var2 < 0) {
                  while (true) {
                     var2 = var1.indexOf("/../");
                     if (var2 < 0) {
                        return var1;
                     }

                     if (var2 == 0) {
                        return null;
                     }

                     int var3 = var1.lastIndexOf(47, var2 - 1);
                     var1 = var1.substring(0, var3) + var1.substring(var2 + 3);
                  }
               }

               var1 = var1.substring(0, var2) + var1.substring(var2 + 2);
            }
         }

         var1 = var1.substring(0, var2) + var1.substring(var2 + 1);
      }
   }

   public static String catPath(String var0, String var1) {
      int var2 = var0.lastIndexOf("/");
      String var3 = var0.substring(0, var2);
      String var4 = var1;

      while (var4.startsWith("../")) {
         if (var3.length() <= 0) {
            return null;
         }

         var2 = var3.lastIndexOf("/");
         var3 = var3.substring(0, var2);
         var2 = var4.indexOf("../") + 3;
         var4 = var4.substring(var2);
      }

      return var3 + "/" + var4;
   }

   public static File resolveFile(File var0, String var1) {
      String var2 = var1;
      if ('/' != File.separatorChar) {
         var2 = var1.replace('/', File.separatorChar);
      }

      if ('\\' != File.separatorChar) {
         var2 = var1.replace('\\', File.separatorChar);
      }

      if (!var2.startsWith(File.separator) && (!Os.isFamily("windows") || var2.indexOf(":") <= 0)) {
         char[] var11 = var1.toCharArray();
         StringBuilder var4 = new StringBuilder();
         int var5 = 0;
         if ('\\' == File.separatorChar) {
            var4.append(var2.charAt(0));
            var5++;
         }

         for (int var6 = var5; var6 < var11.length; var6++) {
            boolean var7 = File.separatorChar == var11[var6] && File.separatorChar == var11[var6 - 1];
            if (!var7) {
               var4.append(var11[var6]);
            }
         }

         var2 = var4.toString();
         File var12 = new File(var0, var2).getAbsoluteFile();

         try {
            var12 = var12.getCanonicalFile();
         } catch (IOException var8) {
         }

         return var12;
      } else {
         File var3 = new File(var2);

         try {
            var3 = var3.getCanonicalFile();
         } catch (IOException var9) {
         }

         return var3;
      }
   }

   public static void forceDelete(String var0) throws IOException {
      forceDelete(new File(var0));
   }

   public static void forceDelete(File var0) throws IOException {
      if (var0.isDirectory()) {
         deleteDirectory(var0);
      } else {
         boolean var1 = var0.getCanonicalFile().exists();
         if (!deleteFile(var0) && var1) {
            String var2 = "File " + var0 + " unable to be deleted.";
            throw new IOException(var2);
         }
      }
   }

   private static boolean deleteFile(File var0) throws IOException {
      if (var0.isDirectory()) {
         throw new IOException("File " + var0 + " isn't a file.");
      } else if (!var0.delete()) {
         if (Os.isFamily("windows")) {
            var0 = var0.getCanonicalFile();
            System.gc();
         }

         try {
            Thread.sleep(10L);
            return var0.delete();
         } catch (InterruptedException var2) {
            return var0.delete();
         }
      } else {
         return true;
      }
   }

   public static void forceDeleteOnExit(File var0) throws IOException {
      if (var0.exists()) {
         if (var0.isDirectory()) {
            deleteDirectoryOnExit(var0);
         } else {
            var0.deleteOnExit();
         }
      }
   }

   private static void deleteDirectoryOnExit(File var0) throws IOException {
      if (var0.exists()) {
         var0.deleteOnExit();
         cleanDirectoryOnExit(var0);
      }
   }

   private static void cleanDirectoryOnExit(File var0) throws IOException {
      if (!var0.exists()) {
         String var10 = var0 + " does not exist";
         throw new IllegalArgumentException(var10);
      } else if (!var0.isDirectory()) {
         String var9 = var0 + " is not a directory";
         throw new IllegalArgumentException(var9);
      } else {
         IOException var1 = null;
         File[] var2 = var0.listFiles();

         for (File var6 : var2) {
            try {
               forceDeleteOnExit(var6);
            } catch (IOException var8) {
               var1 = var8;
            }
         }

         if (null != var1) {
            throw var1;
         }
      }
   }

   public static void forceMkdir(File var0) throws IOException {
      if (Os.isFamily("windows") && !isValidWindowsFileName(var0)) {
         throw new IllegalArgumentException(
            "The file ("
               + var0.getAbsolutePath()
               + ") cannot contain any of the following characters: \n"
               + StringUtils.join(INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME, " ")
         );
      } else {
         if (var0.exists()) {
            if (var0.isFile()) {
               String var1 = "File " + var0 + " exists and is " + "not a directory. Unable to create directory.";
               throw new IOException(var1);
            }
         } else if (!var0.mkdirs()) {
            String var2 = "Unable to create directory " + var0;
            throw new IOException(var2);
         }
      }
   }

   public static void deleteDirectory(String var0) throws IOException {
      deleteDirectory(new File(var0));
   }

   public static void deleteDirectory(File var0) throws IOException {
      if (var0.exists()) {
         if (!var0.delete()) {
            cleanDirectory(var0);
            if (!var0.delete()) {
               String var1 = "Directory " + var0 + " unable to be deleted.";
               throw new IOException(var1);
            }
         }
      }
   }

   public static void cleanDirectory(String var0) throws IOException {
      cleanDirectory(new File(var0));
   }

   public static void cleanDirectory(File var0) throws IOException {
      if (!var0.exists()) {
         String var10 = var0 + " does not exist";
         throw new IllegalArgumentException(var10);
      } else if (!var0.isDirectory()) {
         String var9 = var0 + " is not a directory";
         throw new IllegalArgumentException(var9);
      } else {
         IOException var1 = null;
         File[] var2 = var0.listFiles();
         if (var2 != null) {
            for (File var6 : var2) {
               try {
                  forceDelete(var6);
               } catch (IOException var8) {
                  var1 = var8;
               }
            }

            if (null != var1) {
               throw var1;
            }
         }
      }
   }

   public static long sizeOfDirectory(String var0) {
      return sizeOfDirectory(new File(var0));
   }

   public static long sizeOfDirectory(File var0) {
      if (!var0.exists()) {
         String var9 = var0 + " does not exist";
         throw new IllegalArgumentException(var9);
      } else if (!var0.isDirectory()) {
         String var8 = var0 + " is not a directory";
         throw new IllegalArgumentException(var8);
      } else {
         long var1 = 0L;
         File[] var3 = var0.listFiles();

         for (File var7 : var3) {
            if (var7.isDirectory()) {
               var1 += sizeOfDirectory(var7);
            } else {
               var1 += var7.length();
            }
         }

         return var1;
      }
   }

   public static List<File> getFiles(File var0, String var1, String var2) throws IOException {
      return getFiles(var0, var1, var2, true);
   }

   public static List<File> getFiles(File var0, String var1, String var2, boolean var3) throws IOException {
      List var4 = getFileNames(var0, var1, var2, var3);
      ArrayList var5 = new ArrayList();

      for (String var7 : var4) {
         var5.add(new File(var7));
      }

      return var5;
   }

   public static List<String> getFileNames(File var0, String var1, String var2, boolean var3) throws IOException {
      return getFileNames(var0, var1, var2, var3, true);
   }

   public static List<String> getFileNames(File var0, String var1, String var2, boolean var3, boolean var4) throws IOException {
      return getFileAndDirectoryNames(var0, var1, var2, var3, var4, true, false);
   }

   public static List<String> getDirectoryNames(File var0, String var1, String var2, boolean var3) throws IOException {
      return getDirectoryNames(var0, var1, var2, var3, true);
   }

   public static List<String> getDirectoryNames(File var0, String var1, String var2, boolean var3, boolean var4) throws IOException {
      return getFileAndDirectoryNames(var0, var1, var2, var3, var4, false, true);
   }

   public static List<String> getFileAndDirectoryNames(File var0, String var1, String var2, boolean var3, boolean var4, boolean var5, boolean var6) throws IOException {
      DirectoryScanner var7 = new DirectoryScanner();
      var7.setBasedir(var0);
      if (var1 != null) {
         var7.setIncludes(StringUtils.split(var1, ","));
      }

      if (var2 != null) {
         var7.setExcludes(StringUtils.split(var2, ","));
      }

      var7.setCaseSensitive(var4);
      var7.scan();
      ArrayList var8 = new ArrayList();
      if (var5) {
         String[] var9 = var7.getIncludedFiles();

         for (String var13 : var9) {
            if (var3) {
               var8.add(var0 + FS + var13);
            } else {
               var8.add(var13);
            }
         }
      }

      if (var6) {
         String[] var14 = var7.getIncludedDirectories();

         for (String var18 : var14) {
            if (var3) {
               var8.add(var0 + FS + var18);
            } else {
               var8.add(var18);
            }
         }
      }

      return var8;
   }

   public static void copyDirectory(File var0, File var1) throws IOException {
      copyDirectory(var0, var1, "**", null);
   }

   public static void copyDirectory(File var0, File var1, String var2, String var3) throws IOException {
      if (var0.exists()) {
         for (File var6 : getFiles(var0, var2, var3)) {
            copyFileToDirectory(var6, var1);
         }
      }
   }

   public static void copyDirectoryLayout(File var0, File var1, String[] var2, String[] var3) throws IOException {
      if (var0 == null) {
         throw new IOException("source directory can't be null.");
      } else if (var1 == null) {
         throw new IOException("destination directory can't be null.");
      } else if (var0.equals(var1)) {
         throw new IOException("source and destination are the same directory.");
      } else if (!var0.exists()) {
         throw new IOException("Source directory doesn't exists (" + var0.getAbsolutePath() + ").");
      } else {
         DirectoryScanner var4 = new DirectoryScanner();
         var4.setBasedir(var0);
         if (var2 != null && var2.length >= 1) {
            var4.setIncludes(var2);
         } else {
            var4.setIncludes(new String[]{"**"});
         }

         if (var3 != null && var3.length >= 1) {
            var4.setExcludes(var3);
         }

         var4.addDefaultExcludes();
         var4.scan();

         for (String var7 : Arrays.asList(var4.getIncludedDirectories())) {
            File var8 = new File(var0, var7);
            if (!var8.equals(var0)) {
               File var9 = new File(var1, var7);
               var9.mkdirs();
            }
         }
      }
   }

   public static void copyDirectoryStructure(File var0, File var1) throws IOException {
      copyDirectoryStructure(var0, var1, var1, false);
   }

   public static void copyDirectoryStructureIfModified(File var0, File var1) throws IOException {
      copyDirectoryStructure(var0, var1, var1, true);
   }

   private static void copyDirectoryStructure(File var0, File var1, File var2, boolean var3) throws IOException {
      if (var0 == null) {
         throw new IOException("source directory can't be null.");
      } else if (var1 == null) {
         throw new IOException("destination directory can't be null.");
      } else if (var0.equals(var1)) {
         throw new IOException("source and destination are the same directory.");
      } else if (!var0.exists()) {
         throw new IOException("Source directory doesn't exists (" + var0.getAbsolutePath() + ").");
      } else {
         File[] var4 = var0.listFiles();
         String var5 = var0.getAbsolutePath();

         for (File var9 : var4) {
            if (!var9.equals(var2)) {
               String var10 = var9.getAbsolutePath();
               var10 = var10.substring(var5.length() + 1);
               File var11 = new File(var1, var10);
               if (var9.isFile()) {
                  var11 = var11.getParentFile();
                  if (var3) {
                     copyFileToDirectoryIfModified(var9, var11);
                  } else {
                     copyFileToDirectory(var9, var11);
                  }
               } else {
                  if (!var9.isDirectory()) {
                     throw new IOException("Unknown file type: " + var9.getAbsolutePath());
                  }

                  if (!var11.exists() && !var11.mkdirs()) {
                     throw new IOException("Could not create destination directory '" + var11.getAbsolutePath() + "'.");
                  }

                  copyDirectoryStructure(var9, var11, var2, var3);
               }
            }
         }
      }
   }

   public static void rename(File var0, File var1) throws IOException {
      if (var1.exists() && !var1.delete()) {
         throw new IOException("Failed to delete " + var1 + " while trying to rename " + var0);
      } else {
         File var2 = var1.getParentFile();
         if (var2 != null && !var2.exists() && !var2.mkdirs()) {
            throw new IOException("Failed to create directory " + var2 + " while trying to rename " + var0);
         } else {
            if (!var0.renameTo(var1)) {
               copyFile(var0, var1);
               if (!var0.delete()) {
                  throw new IOException("Failed to delete " + var0 + " while trying to rename it.");
               }
            }
         }
      }
   }

   public static File createTempFile(String var0, String var1, File var2) {
      File var3 = null;
      String var4 = System.getProperty("java.io.tmpdir");
      if (var2 != null) {
         var4 = var2.getPath();
      }

      DecimalFormat var5 = new DecimalFormat("#####");
      SecureRandom var6 = new SecureRandom();
      long var7 = var6.nextLong();
      Random var9 = new Random(var7 + Runtime.getRuntime().freeMemory());
      synchronized (var9) {
         do {
            var3 = new File(var4, var0 + var5.format((long)Math.abs(var9.nextInt())) + var1);
         } while (var3.exists());

         return var3;
      }
   }

   public static void copyFile(File var0, File var1, String var2, FileUtils.FilterWrapper[] var3) throws IOException {
      copyFile(var0, var1, var2, var3, false);
   }

   public static void copyFile(File var0, File var1, String var2, FileUtils.FilterWrapper[] var3, boolean var4) throws IOException {
      if (var3 != null && var3.length > 0) {
         BufferedReader var5 = null;
         Object var6 = null;

         try {
            if (var2 != null && var2.length() >= 1) {
               FileInputStream var7 = new FileInputStream(var0);
               FileOutputStream var8 = new FileOutputStream(var1);
               var5 = new BufferedReader(new InputStreamReader(var7, var2));
               var6 = new OutputStreamWriter(var8, var2);
            } else {
               var5 = new BufferedReader(new FileReader(var0));
               var6 = new FileWriter(var1);
            }

            Object var15 = var5;

            for (FileUtils.FilterWrapper var11 : var3) {
               var15 = var11.getReader((Reader)var15);
            }

            IOUtil.copy((Reader)var15, (Writer)var6);
            var6.close();
            var6 = null;
            var5.close();
            var5 = null;
         } finally {
            IOUtil.close(var5);
            IOUtil.close((Writer)var6);
         }
      } else if (isSourceNewerThanDestination(var0, var1) || var4) {
         copyFile(var0, var1);
      }
   }

   private static boolean isSourceNewerThanDestination(File var0, File var1) {
      return var1.lastModified() == 0L && var0.lastModified() == 0L || var1.lastModified() < var0.lastModified();
   }

   public static List<String> loadFile(File var0) throws IOException {
      ArrayList var1 = new ArrayList();
      BufferedReader var2 = null;

      try {
         if (var0.exists()) {
            var2 = new BufferedReader(new FileReader(var0));

            for (String var3 = var2.readLine(); var3 != null; var3 = var2.readLine()) {
               var3 = var3.trim();
               if (!var3.startsWith("#") && var3.length() != 0) {
                  var1.add(var3);
               }
            }

            var2.close();
            var2 = null;
         }
      } finally {
         IOUtil.close(var2);
      }

      return var1;
   }

   public static boolean isValidWindowsFileName(File var0) {
      if (Os.isFamily("windows")) {
         if (StringUtils.indexOfAny(var0.getName(), INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME) != -1) {
            return false;
         }

         File var1 = var0.getParentFile();
         if (var1 != null) {
            return isValidWindowsFileName(var1);
         }
      }

      return true;
   }

   public abstract static class FilterWrapper {
      public abstract Reader getReader(Reader var1);
   }
}
