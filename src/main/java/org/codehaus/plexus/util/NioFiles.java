package org.codehaus.plexus.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class NioFiles {
   public static boolean isSymbolicLink(File var0) {
      return Files.isSymbolicLink(var0.toPath());
   }

   public static void chmod(File var0, int var1) throws IOException {
      Path var2 = var0.toPath();
      if (!Files.isSymbolicLink(var2)) {
         Files.setPosixFilePermissions(var2, getPermissions(var1));
      }
   }

   private static Set<PosixFilePermission> getPermissions(int var0) {
      HashSet var1 = new HashSet();
      if ((var0 & 256) > 0) {
         var1.add(PosixFilePermission.OWNER_READ);
      }

      if ((var0 & 128) > 0) {
         var1.add(PosixFilePermission.OWNER_WRITE);
      }

      if ((var0 & 64) > 0) {
         var1.add(PosixFilePermission.OWNER_EXECUTE);
      }

      if ((var0 & 32) > 0) {
         var1.add(PosixFilePermission.GROUP_READ);
      }

      if ((var0 & 16) > 0) {
         var1.add(PosixFilePermission.GROUP_WRITE);
      }

      if ((var0 & 8) > 0) {
         var1.add(PosixFilePermission.GROUP_EXECUTE);
      }

      if ((var0 & 4) > 0) {
         var1.add(PosixFilePermission.OTHERS_READ);
      }

      if ((var0 & 2) > 0) {
         var1.add(PosixFilePermission.OTHERS_WRITE);
      }

      if ((var0 & 1) > 0) {
         var1.add(PosixFilePermission.OTHERS_EXECUTE);
      }

      return var1;
   }

   public static long getLastModified(File var0) throws IOException {
      BasicFileAttributes var1 = Files.readAttributes(var0.toPath(), BasicFileAttributes.class);
      return var1.lastModifiedTime().toMillis();
   }

   public static File readSymbolicLink(File var0) throws IOException {
      Path var1 = Files.readSymbolicLink(var0.toPath());
      return var1.toFile();
   }

   public static File createSymbolicLink(File var0, File var1) throws IOException {
      Path var2 = var0.toPath();
      if (Files.exists(var2, LinkOption.NOFOLLOW_LINKS)) {
         Files.delete(var2);
      }

      var2 = Files.createSymbolicLink(var2, var1.toPath());
      return var2.toFile();
   }

   public static boolean deleteIfExists(File var0) throws IOException {
      return Files.deleteIfExists(var0.toPath());
   }

   public static File copy(File var0, File var1) throws IOException {
      Path var2 = Files.copy(var0.toPath(), var1.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
      return var2.toFile();
   }
}
