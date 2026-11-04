package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.graph.SuccessorsFunction;
import com.google.common.graph.Traverser;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public final class Files {
   private static final int TEMP_DIR_ATTEMPTS = 10000;
   private static final SuccessorsFunction<File> FILE_TREE = new SuccessorsFunction<File>() {
      public Iterable<File> successors(File var1) {
         if (var1.isDirectory()) {
            File[] var2 = var1.listFiles();
            if (var2 != null) {
               return Collections.unmodifiableList(Arrays.asList(var2));
            }
         }

         return ImmutableList.of();
      }
   };

   private Files() {
   }

   @Beta
   public static BufferedReader newReader(File var0, Charset var1) throws FileNotFoundException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new BufferedReader(new InputStreamReader(new FileInputStream(var0), var1));
   }

   @Beta
   public static BufferedWriter newWriter(File var0, Charset var1) throws FileNotFoundException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(var0), var1));
   }

   public static ByteSource asByteSource(File var0) {
      return new Files.FileByteSource(var0);
   }

   public static ByteSink asByteSink(File var0, FileWriteMode... var1) {
      return new Files.FileByteSink(var0, var1);
   }

   public static CharSource asCharSource(File var0, Charset var1) {
      return asByteSource(var0).asCharSource(var1);
   }

   public static CharSink asCharSink(File var0, Charset var1, FileWriteMode... var2) {
      return asByteSink(var0, var2).asCharSink(var1);
   }

   @Beta
   public static byte[] toByteArray(File var0) throws IOException {
      return asByteSource(var0).read();
   }

   @Deprecated
   @Beta
   public static String toString(File var0, Charset var1) throws IOException {
      return asCharSource(var0, var1).read();
   }

   @Beta
   public static void write(byte[] var0, File var1) throws IOException {
      asByteSink(var1).write(var0);
   }

   @Deprecated
   @Beta
   public static void write(CharSequence var0, File var1, Charset var2) throws IOException {
      asCharSink(var1, var2).write(var0);
   }

   @Beta
   public static void copy(File var0, OutputStream var1) throws IOException {
      asByteSource(var0).copyTo(var1);
   }

   @Beta
   public static void copy(File var0, File var1) throws IOException {
      Preconditions.checkArgument(!var0.equals(var1), "Source %s and destination %s must be different", var0, var1);
      asByteSource(var0).copyTo(asByteSink(var1));
   }

   @Deprecated
   @Beta
   public static void copy(File var0, Charset var1, Appendable var2) throws IOException {
      asCharSource(var0, var1).copyTo(var2);
   }

   @Deprecated
   @Beta
   public static void append(CharSequence var0, File var1, Charset var2) throws IOException {
      asCharSink(var1, var2, FileWriteMode.APPEND).write(var0);
   }

   @Beta
   public static boolean equal(File var0, File var1) throws IOException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      if (var0 != var1 && !var0.equals(var1)) {
         long var2 = var0.length();
         long var4 = var1.length();
         return var2 != 0L && var4 != 0L && var2 != var4 ? false : asByteSource(var0).contentEquals(asByteSource(var1));
      } else {
         return true;
      }
   }

   @Deprecated
   @Beta
   public static File createTempDir() {
      File var0 = new File(System.getProperty("java.io.tmpdir"));
      long var2 = System.currentTimeMillis();
      String var1 = new StringBuilder(21).append(var2).append("-").toString();

      for (int var5 = 0; var5 < 10000; var5++) {
         File var3 = new File(var0, new StringBuilder(11 + String.valueOf(var1).length()).append(var1).append(var5).toString());
         if (var3.mkdir()) {
            return var3;
         }
      }

      throw new IllegalStateException(
         new StringBuilder(66 + String.valueOf(var1).length() + String.valueOf(var1).length())
            .append("Failed to create directory within 10000 attempts (tried ")
            .append(var1)
            .append("0 to ")
            .append(var1)
            .append(9999)
            .append(')')
            .toString()
      );
   }

   @Beta
   public static void touch(File var0) throws IOException {
      Preconditions.checkNotNull(var0);
      if (!var0.createNewFile() && !var0.setLastModified(System.currentTimeMillis())) {
         String var1 = String.valueOf(var0);
         throw new IOException(new StringBuilder(38 + String.valueOf(var1).length()).append("Unable to update modification time of ").append(var1).toString());
      }
   }

   @Beta
   public static void createParentDirs(File var0) throws IOException {
      Preconditions.checkNotNull(var0);
      File var1 = var0.getCanonicalFile().getParentFile();
      if (var1 != null) {
         var1.mkdirs();
         if (!var1.isDirectory()) {
            String var2 = String.valueOf(var0);
            throw new IOException(
               new StringBuilder(39 + String.valueOf(var2).length()).append("Unable to create parent directories of ").append(var2).toString()
            );
         }
      }
   }

   @Beta
   public static void move(File var0, File var1) throws IOException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkArgument(!var0.equals(var1), "Source %s and destination %s must be different", var0, var1);
      if (!var0.renameTo(var1)) {
         copy(var0, var1);
         if (!var0.delete()) {
            if (!var1.delete()) {
               String var3 = String.valueOf(var1);
               throw new IOException(new StringBuilder(17 + String.valueOf(var3).length()).append("Unable to delete ").append(var3).toString());
            }

            String var2 = String.valueOf(var0);
            throw new IOException(new StringBuilder(17 + String.valueOf(var2).length()).append("Unable to delete ").append(var2).toString());
         }
      }
   }

   @Deprecated
   @Beta
   public static String readFirstLine(File var0, Charset var1) throws IOException {
      return asCharSource(var0, var1).readFirstLine();
   }

   @Beta
   public static List<String> readLines(File var0, Charset var1) throws IOException {
      return asCharSource(var0, var1).readLines(new LineProcessor<List<String>>() {
         final List<String> result = Lists.newArrayList();

         @Override
         public boolean processLine(String var1) {
            this.result.add(var1);
            return true;
         }

         public List<String> getResult() {
            return this.result;
         }
      });
   }

   @Deprecated
   @ParametricNullness
   @Beta
   @CanIgnoreReturnValue
   public static <T> T readLines(File var0, Charset var1, LineProcessor<T> var2) throws IOException {
      return asCharSource(var0, var1).readLines(var2);
   }

   @Deprecated
   @ParametricNullness
   @Beta
   @CanIgnoreReturnValue
   public static <T> T readBytes(File var0, ByteProcessor<T> var1) throws IOException {
      return asByteSource(var0).read(var1);
   }

   @Deprecated
   @Beta
   public static HashCode hash(File var0, HashFunction var1) throws IOException {
      return asByteSource(var0).hash(var1);
   }

   @Beta
   public static MappedByteBuffer map(File var0) throws IOException {
      Preconditions.checkNotNull(var0);
      return map(var0, MapMode.READ_ONLY);
   }

   @Beta
   public static MappedByteBuffer map(File var0, MapMode var1) throws IOException {
      return mapInternal(var0, var1, -1L);
   }

   @Beta
   public static MappedByteBuffer map(File var0, MapMode var1, long var2) throws IOException {
      Preconditions.checkArgument(var2 >= 0L, "size (%s) may not be negative", var2);
      return mapInternal(var0, var1, var2);
   }

   private static MappedByteBuffer mapInternal(File var0, MapMode var1, long var2) throws IOException {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Closer var4 = Closer.create();

      MappedByteBuffer var7;
      try {
         RandomAccessFile var5 = var4.register(new RandomAccessFile(var0, var1 == MapMode.READ_ONLY ? "r" : "rw"));
         FileChannel var6 = var4.register(var5.getChannel());
         var7 = var6.map(var1, 0L, var2 == -1L ? var6.size() : var2);
      } catch (Throwable var11) {
         throw var4.rethrow(var11);
      } finally {
         var4.close();
      }

      return var7;
   }

   @Beta
   public static String simplifyPath(String var0) {
      Preconditions.checkNotNull(var0);
      if (var0.length() == 0) {
         return ".";
      } else {
         Iterable var1 = Splitter.on('/').omitEmptyStrings().split(var0);
         ArrayList var2 = new ArrayList();

         for (String var4 : var1) {
            switch (var4) {
               case ".":
                  break;
               case "..":
                  if (var2.size() > 0 && !((String)var2.get(var2.size() - 1)).equals("..")) {
                     var2.remove(var2.size() - 1);
                     break;
                  }

                  var2.add("..");
                  break;
               default:
                  var2.add(var4);
            }
         }

         String var7 = Joiner.on('/').join(var2);
         if (var0.charAt(0) == '/') {
            String var10001 = String.valueOf(var7);
            var7 = var10001.length() != 0 ? "/".concat(var10001) : new String("/");
         }

         while (var7.startsWith("/../")) {
            var7 = var7.substring(3);
         }

         if (var7.equals("/..")) {
            var7 = "/";
         } else if ("".equals(var7)) {
            var7 = ".";
         }

         return var7;
      }
   }

   @Beta
   public static String getFileExtension(String var0) {
      Preconditions.checkNotNull(var0);
      String var1 = new File(var0).getName();
      int var2 = var1.lastIndexOf(46);
      return var2 == -1 ? "" : var1.substring(var2 + 1);
   }

   @Beta
   public static String getNameWithoutExtension(String var0) {
      Preconditions.checkNotNull(var0);
      String var1 = new File(var0).getName();
      int var2 = var1.lastIndexOf(46);
      return var2 == -1 ? var1 : var1.substring(0, var2);
   }

   @Beta
   public static Traverser<File> fileTraverser() {
      return Traverser.forTree(FILE_TREE);
   }

   @Beta
   public static Predicate<File> isDirectory() {
      return Files.FilePredicate.IS_DIRECTORY;
   }

   @Beta
   public static Predicate<File> isFile() {
      return Files.FilePredicate.IS_FILE;
   }

   private static final class FileByteSink extends ByteSink {
      private final File file;
      private final ImmutableSet<FileWriteMode> modes;

      private FileByteSink(File var1, FileWriteMode... var2) {
         this.file = Preconditions.checkNotNull(var1);
         this.modes = ImmutableSet.copyOf(var2);
      }

      public FileOutputStream openStream() throws IOException {
         return new FileOutputStream(this.file, this.modes.contains(FileWriteMode.APPEND));
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.file);
         String var2 = String.valueOf(this.modes);
         return new StringBuilder(20 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append("Files.asByteSink(")
            .append(var1)
            .append(", ")
            .append(var2)
            .append(")")
            .toString();
      }
   }

   private static final class FileByteSource extends ByteSource {
      private final File file;

      private FileByteSource(File var1) {
         this.file = Preconditions.checkNotNull(var1);
      }

      public FileInputStream openStream() throws IOException {
         return new FileInputStream(this.file);
      }

      @Override
      public Optional<Long> sizeIfKnown() {
         return this.file.isFile() ? Optional.of(this.file.length()) : Optional.absent();
      }

      @Override
      public long size() throws IOException {
         if (!this.file.isFile()) {
            throw new FileNotFoundException(this.file.toString());
         } else {
            return this.file.length();
         }
      }

      @Override
      public byte[] read() throws IOException {
         Closer var1 = Closer.create();

         byte[] var3;
         try {
            FileInputStream var2 = var1.register(this.openStream());
            var3 = ByteStreams.toByteArray(var2, var2.getChannel().size());
         } catch (Throwable var7) {
            throw var1.rethrow(var7);
         } finally {
            var1.close();
         }

         return var3;
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.file);
         return new StringBuilder(20 + String.valueOf(var1).length()).append("Files.asByteSource(").append(var1).append(")").toString();
      }
   }

   private static enum FilePredicate implements Predicate<File> {
      IS_DIRECTORY {
         public boolean apply(File var1) {
            return var1.isDirectory();
         }

         @Override
         public String toString() {
            return "Files.isDirectory()";
         }
      },
      IS_FILE {
         public boolean apply(File var1) {
            return var1.isFile();
         }

         @Override
         public String toString() {
            return "Files.isFile()";
         }
      };

      private FilePredicate() {
      }
   }
}
