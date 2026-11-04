package org.apache.logging.log4j.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Objects;
import java.util.Set;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public final class FileUtils {
   private static final String PROTOCOL_FILE = "file";
   private static final String JBOSS_FILE = "vfsfile";
   private static final Logger LOGGER = StatusLogger.getLogger();

   private FileUtils() {
   }

   public static File fileFromUri(URI var0) {
      if (var0 == null) {
         return null;
      } else {
         if (var0.isAbsolute()) {
            if ("vfsfile".equals(var0.getScheme())) {
               try {
                  var0 = new URI("file", var0.getSchemeSpecificPart(), var0.getFragment());
               } catch (URISyntaxException var4) {
               }
            }

            try {
               if ("file".equals(var0.getScheme())) {
                  return new File(var0);
               }
            } catch (Exception var3) {
               LOGGER.warn("Invalid URI {}", var0);
            }
         } else {
            File var1 = new File(var0.toString());

            try {
               if (var1.exists()) {
                  return var1;
               }

               String var2 = var0.getPath();
               return new File(var2);
            } catch (Exception var5) {
               LOGGER.warn("Invalid URI {}", var0);
            }
         }

         return null;
      }
   }

   public static boolean isFile(URL var0) {
      return var0 != null && (var0.getProtocol().equals("file") || var0.getProtocol().equals("vfsfile"));
   }

   public static String getFileExtension(File var0) {
      String var1 = var0.getName();
      return var1.lastIndexOf(".") != -1 && var1.lastIndexOf(".") != 0 ? var1.substring(var1.lastIndexOf(".") + 1) : null;
   }

   public static void mkdir(File var0, boolean var1) throws IOException {
      if (!var0.exists()) {
         if (!var1) {
            throw new IOException("The directory " + var0.getAbsolutePath() + " does not exist.");
         }

         if (!var0.mkdirs()) {
            throw new IOException("Could not create directory " + var0.getAbsolutePath());
         }
      }

      if (!var0.isDirectory()) {
         throw new IOException("File " + var0 + " exists and is not a directory. Unable to create directory.");
      }
   }

   public static void makeParentDirs(File var0) throws IOException {
      File var1 = Objects.requireNonNull(var0, "file").getCanonicalFile().getParentFile();
      if (var1 != null) {
         mkdir(var1, true);
      }
   }

   public static void defineFilePosixAttributeView(Path var0, Set<PosixFilePermission> var1, String var2, String var3) throws IOException {
      PosixFileAttributeView var4 = Files.getFileAttributeView(var0, PosixFileAttributeView.class);
      if (var4 != null) {
         UserPrincipalLookupService var5 = FileSystems.getDefault().getUserPrincipalLookupService();
         if (var2 != null) {
            UserPrincipal var6 = var5.lookupPrincipalByName(var2);
            if (var6 != null) {
               var4.setOwner(var6);
            }
         }

         if (var3 != null) {
            GroupPrincipal var7 = var5.lookupPrincipalByGroupName(var3);
            if (var7 != null) {
               var4.setGroup(var7);
            }
         }

         if (var1 != null) {
            var4.setPermissions(var1);
         }
      }
   }

   public static boolean isFilePosixAttributeViewSupported() {
      return FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
   }
}
