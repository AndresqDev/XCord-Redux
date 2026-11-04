package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.graph.SuccessorsFunction;
import com.google.common.graph.Traverser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.SecureDirectoryStream;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public final class MoreFiles {
   private static final SuccessorsFunction<Path> FILE_TREE = new SuccessorsFunction<Path>() {
      public Iterable<Path> successors(Path var1) {
         return MoreFiles.fileTreeChildren(var1);
      }
   };

   private MoreFiles() {
   }

   public static ByteSource asByteSource(Path var0, OpenOption... var1) {
      return new MoreFiles.PathByteSource(var0, var1);
   }

   public static ByteSink asByteSink(Path var0, OpenOption... var1) {
      return new MoreFiles.PathByteSink(var0, var1);
   }

   public static CharSource asCharSource(Path var0, Charset var1, OpenOption... var2) {
      return asByteSource(var0, var2).asCharSource(var1);
   }

   public static CharSink asCharSink(Path var0, Charset var1, OpenOption... var2) {
      return asByteSink(var0, var2).asCharSink(var1);
   }

   public static ImmutableList<Path> listFiles(Path var0) throws IOException {
      try {
         DirectoryStream var1 = java.nio.file.Files.newDirectoryStream(var0);

         ImmutableList var2;
         try {
            var2 = ImmutableList.copyOf(var1);
         } catch (Throwable var5) {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (var1 != null) {
            var1.close();
         }

         return var2;
      } catch (DirectoryIteratorException var6) {
         throw var6.getCause();
      }
   }

   public static Traverser<Path> fileTraverser() {
      return Traverser.forTree(FILE_TREE);
   }

   private static Iterable<Path> fileTreeChildren(Path var0) {
      if (java.nio.file.Files.isDirectory(var0, LinkOption.NOFOLLOW_LINKS)) {
         try {
            return listFiles(var0);
         } catch (IOException var2) {
            throw new DirectoryIteratorException(var2);
         }
      } else {
         return ImmutableList.of();
      }
   }

   public static Predicate<Path> isDirectory(LinkOption... var0) {
      final LinkOption[] var1 = (LinkOption[])var0.clone();
      return new Predicate<Path>() {
         public boolean apply(Path var1x) {
            return java.nio.file.Files.isDirectory(var1x, var1);
         }

         @Override
         public String toString() {
            String var1x = Arrays.toString((Object[])var1);
            return new StringBuilder(23 + String.valueOf(var1x).length()).append("MoreFiles.isDirectory(").append(var1x).append(")").toString();
         }
      };
   }

   private static boolean isDirectory(SecureDirectoryStream<Path> var0, Path var1, LinkOption... var2) throws IOException {
      return var0.getFileAttributeView(var1, BasicFileAttributeView.class, var2).readAttributes().isDirectory();
   }

   public static Predicate<Path> isRegularFile(LinkOption... var0) {
      final LinkOption[] var1 = (LinkOption[])var0.clone();
      return new Predicate<Path>() {
         public boolean apply(Path var1x) {
            return java.nio.file.Files.isRegularFile(var1x, var1);
         }

         @Override
         public String toString() {
            String var1x = Arrays.toString((Object[])var1);
            return new StringBuilder(25 + String.valueOf(var1x).length()).append("MoreFiles.isRegularFile(").append(var1x).append(")").toString();
         }
      };
   }

   public static boolean equal(Path var0, Path var1) throws IOException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      if (java.nio.file.Files.isSameFile(var0, var1)) {
         return true;
      } else {
         ByteSource var2 = asByteSource(var0);
         ByteSource var3 = asByteSource(var1);
         long var4 = var2.sizeIfKnown().or(0L);
         long var6 = var3.sizeIfKnown().or(0L);
         return var4 != 0L && var6 != 0L && var4 != var6 ? false : var2.contentEquals(var3);
      }
   }

   public static void touch(Path var0) throws IOException {
      Preconditions.checkNotNull(var0);

      try {
         java.nio.file.Files.setLastModifiedTime(var0, FileTime.fromMillis(System.currentTimeMillis()));
      } catch (NoSuchFileException var4) {
         try {
            java.nio.file.Files.createFile(var0);
         } catch (FileAlreadyExistsException var3) {
         }
      }
   }

   public static void createParentDirectories(Path var0, FileAttribute<?>... var1) throws IOException {
      Path var2 = var0.toAbsolutePath().normalize();
      Path var3 = var2.getParent();
      if (var3 != null) {
         if (!java.nio.file.Files.isDirectory(var3)) {
            java.nio.file.Files.createDirectories(var3, var1);
            if (!java.nio.file.Files.isDirectory(var3)) {
               String var4 = String.valueOf(var0);
               throw new IOException(
                  new StringBuilder(39 + String.valueOf(var4).length()).append("Unable to create parent directories of ").append(var4).toString()
               );
            }
         }
      }
   }

   public static String getFileExtension(Path var0) {
      Path var1 = var0.getFileName();
      if (var1 == null) {
         return "";
      } else {
         String var2 = var1.toString();
         int var3 = var2.lastIndexOf(46);
         return var3 == -1 ? "" : var2.substring(var3 + 1);
      }
   }

   public static String getNameWithoutExtension(Path var0) {
      Path var1 = var0.getFileName();
      if (var1 == null) {
         return "";
      } else {
         String var2 = var1.toString();
         int var3 = var2.lastIndexOf(46);
         return var3 == -1 ? var2 : var2.substring(0, var3);
      }
   }

   public static void deleteRecursively(Path var0, RecursiveDeleteOption... var1) throws IOException {
      Path var2 = getParentPath(var0);
      if (var2 == null) {
         throw new FileSystemException(var0.toString(), null, "can't delete recursively");
      } else {
         Collection var3 = null;

         try {
            boolean var4 = false;
            DirectoryStream var5 = java.nio.file.Files.newDirectoryStream(var2);

            try {
               if (var5 instanceof SecureDirectoryStream) {
                  var4 = true;
                  var3 = deleteRecursivelySecure((SecureDirectoryStream<Path>)var5, Objects.requireNonNull(var0.getFileName()));
               }
            } catch (Throwable var9) {
               if (var5 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (var5 != null) {
               var5.close();
            }

            if (!var4) {
               checkAllowsInsecure(var0, var1);
               var3 = deleteRecursivelyInsecure(var0);
            }
         } catch (IOException var10) {
            if (var3 == null) {
               throw var10;
            }

            var3.add(var10);
         }

         if (var3 != null) {
            throwDeleteFailed(var0, var3);
         }
      }
   }

   public static void deleteDirectoryContents(Path var0, RecursiveDeleteOption... var1) throws IOException {
      Collection var2 = null;

      try {
         DirectoryStream var3 = java.nio.file.Files.newDirectoryStream(var0);

         try {
            if (var3 instanceof SecureDirectoryStream) {
               SecureDirectoryStream var4 = (SecureDirectoryStream)var3;
               var2 = deleteDirectoryContentsSecure(var4);
            } else {
               checkAllowsInsecure(var0, var1);
               var2 = deleteDirectoryContentsInsecure(var3);
            }
         } catch (Throwable var7) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var3 != null) {
            var3.close();
         }
      } catch (IOException var8) {
         if (var2 == null) {
            throw var8;
         }

         var2.add(var8);
      }

      if (var2 != null) {
         throwDeleteFailed(var0, var2);
      }
   }

   private static Collection<IOException> deleteRecursivelySecure(SecureDirectoryStream<Path> var0, Path var1) {
      Collection var2 = null;

      try {
         if (isDirectory(var0, var1, LinkOption.NOFOLLOW_LINKS)) {
            SecureDirectoryStream var3 = var0.newDirectoryStream(var1, LinkOption.NOFOLLOW_LINKS);

            try {
               var2 = deleteDirectoryContentsSecure(var3);
            } catch (Throwable var7) {
               if (var3 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (var3 != null) {
               var3.close();
            }

            if (var2 == null) {
               var0.deleteDirectory(var1);
            }
         } else {
            var0.deleteFile(var1);
         }

         return var2;
      } catch (IOException var8) {
         return addException(var2, var8);
      }
   }

   private static Collection<IOException> deleteDirectoryContentsSecure(SecureDirectoryStream<Path> var0) {
      Collection var1 = null;

      try {
         for (Path var3 : var0) {
            var1 = concat(var1, deleteRecursivelySecure(var0, var3.getFileName()));
         }

         return var1;
      } catch (DirectoryIteratorException var4) {
         return addException(var1, var4.getCause());
      }
   }

   private static Collection<IOException> deleteRecursivelyInsecure(Path var0) {
      Collection var1 = null;

      try {
         if (java.nio.file.Files.isDirectory(var0, LinkOption.NOFOLLOW_LINKS)) {
            DirectoryStream var2 = java.nio.file.Files.newDirectoryStream(var0);

            try {
               var1 = deleteDirectoryContentsInsecure(var2);
            } catch (Throwable var6) {
               if (var2 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (var2 != null) {
               var2.close();
            }
         }

         if (var1 == null) {
            java.nio.file.Files.delete(var0);
         }

         return var1;
      } catch (IOException var7) {
         return addException(var1, var7);
      }
   }

   private static Collection<IOException> deleteDirectoryContentsInsecure(DirectoryStream<Path> var0) {
      Collection var1 = null;

      try {
         for (Path var3 : var0) {
            var1 = concat(var1, deleteRecursivelyInsecure(var3));
         }

         return var1;
      } catch (DirectoryIteratorException var4) {
         return addException(var1, var4.getCause());
      }
   }

   private static Path getParentPath(Path var0) {
      Path var1 = var0.getParent();
      if (var1 != null) {
         return var1;
      } else {
         return var0.getNameCount() == 0 ? null : var0.getFileSystem().getPath(".");
      }
   }

   private static void checkAllowsInsecure(Path var0, RecursiveDeleteOption[] var1) throws InsecureRecursiveDeleteException {
      if (!Arrays.asList(var1).contains(RecursiveDeleteOption.ALLOW_INSECURE)) {
         throw new InsecureRecursiveDeleteException(var0.toString());
      }
   }

   private static Collection<IOException> addException(Collection<IOException> var0, IOException var1) {
      if (var0 == null) {
         var0 = new ArrayList();
      }

      var0.add(var1);
      return (Collection<IOException>)var0;
   }

   private static Collection<IOException> concat(Collection<IOException> var0, Collection<IOException> var1) {
      if (var0 == null) {
         return var1;
      } else {
         if (var1 != null) {
            var0.addAll(var1);
         }

         return var0;
      }
   }

   private static void throwDeleteFailed(Path var0, Collection<IOException> var1) throws FileSystemException {
      NoSuchFileException var2 = pathNotFound(var0, var1);
      if (var2 != null) {
         throw var2;
      } else {
         FileSystemException var3 = new FileSystemException(var0.toString(), null, "failed to delete one or more files; see suppressed exceptions for details");

         for (IOException var5 : var1) {
            var3.addSuppressed(var5);
         }

         throw var3;
      }
   }

   private static NoSuchFileException pathNotFound(Path var0, Collection<IOException> var1) {
      if (var1.size() != 1) {
         return null;
      } else {
         IOException var2 = Iterables.getOnlyElement(var1);
         if (!(var2 instanceof NoSuchFileException)) {
            return null;
         } else {
            NoSuchFileException var3 = (NoSuchFileException)var2;
            String var4 = var3.getFile();
            if (var4 == null) {
               return null;
            } else {
               Path var5 = getParentPath(var0);
               if (var5 == null) {
                  return null;
               } else {
                  Path var6 = var5.resolve(Objects.requireNonNull(var0.getFileName()));
                  return var4.equals(var6.toString()) ? var3 : null;
               }
            }
         }
      }
   }

   private static final class PathByteSink extends ByteSink {
      private final Path path;
      private final OpenOption[] options;

      private PathByteSink(Path var1, OpenOption... var2) {
         this.path = Preconditions.checkNotNull(var1);
         this.options = (OpenOption[])var2.clone();
      }

      @Override
      public OutputStream openStream() throws IOException {
         return java.nio.file.Files.newOutputStream(this.path, this.options);
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.path);
         String var2 = Arrays.toString((Object[])this.options);
         return new StringBuilder(24 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append("MoreFiles.asByteSink(")
            .append(var1)
            .append(", ")
            .append(var2)
            .append(")")
            .toString();
      }
   }

   private static final class PathByteSource extends ByteSource {
      private static final LinkOption[] FOLLOW_LINKS = new LinkOption[0];
      private final Path path;
      private final OpenOption[] options;
      private final boolean followLinks;

      private PathByteSource(Path var1, OpenOption... var2) {
         this.path = Preconditions.checkNotNull(var1);
         this.options = (OpenOption[])var2.clone();
         this.followLinks = followLinks(this.options);
      }

      private static boolean followLinks(OpenOption[] var0) {
         for (OpenOption var4 : var0) {
            if (var4 == LinkOption.NOFOLLOW_LINKS) {
               return false;
            }
         }

         return true;
      }

      @Override
      public InputStream openStream() throws IOException {
         return java.nio.file.Files.newInputStream(this.path, this.options);
      }

      private BasicFileAttributes readAttributes() throws IOException {
         return java.nio.file.Files.readAttributes(
            this.path, BasicFileAttributes.class, this.followLinks ? FOLLOW_LINKS : new LinkOption[]{LinkOption.NOFOLLOW_LINKS}
         );
      }

      @Override
      public Optional<Long> sizeIfKnown() {
         BasicFileAttributes var1;
         try {
            var1 = this.readAttributes();
         } catch (IOException var3) {
            return Optional.absent();
         }

         return !var1.isDirectory() && !var1.isSymbolicLink() ? Optional.of(var1.size()) : Optional.absent();
      }

      @Override
      public long size() throws IOException {
         BasicFileAttributes var1 = this.readAttributes();
         if (var1.isDirectory()) {
            throw new IOException("can't read: is a directory");
         } else if (var1.isSymbolicLink()) {
            throw new IOException("can't read: is a symbolic link");
         } else {
            return var1.size();
         }
      }

      @Override
      public byte[] read() throws IOException {
         SeekableByteChannel var1 = java.nio.file.Files.newByteChannel(this.path, this.options);

         byte[] var2;
         try {
            var2 = ByteStreams.toByteArray(Channels.newInputStream(var1), var1.size());
         } catch (Throwable var5) {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (var1 != null) {
            var1.close();
         }

         return var2;
      }

      @Override
      public CharSource asCharSource(Charset var1) {
         return (CharSource)(this.options.length == 0 ? new ByteSource.AsCharSource(var1) {
            @Override
            public Stream<String> lines() throws IOException {
               return java.nio.file.Files.lines(PathByteSource.this.path, this.charset);
            }
         } : super.asCharSource(var1));
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.path);
         String var2 = Arrays.toString((Object[])this.options);
         return new StringBuilder(26 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append("MoreFiles.asByteSource(")
            .append(var1)
            .append(", ")
            .append(var2)
            .append(")")
            .toString();
      }
   }
}
