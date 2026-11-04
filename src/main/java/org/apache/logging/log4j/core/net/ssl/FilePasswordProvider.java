package org.apache.logging.log4j.core.net.ssl;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

class FilePasswordProvider implements PasswordProvider {
   private final Path passwordPath;

   public FilePasswordProvider(String var1) throws NoSuchFileException {
      this.passwordPath = Paths.get(var1);
      if (!Files.exists(this.passwordPath)) {
         throw new NoSuchFileException("PasswordFile '" + var1 + "' does not exist");
      }
   }

   @Override
   public char[] getPassword() {
      byte[] var1 = null;

      char[] var5;
      try {
         var1 = Files.readAllBytes(this.passwordPath);
         ByteBuffer var2 = ByteBuffer.wrap(var1);
         CharBuffer var3 = Charset.defaultCharset().decode(var2);
         char[] var4 = new char[var3.limit()];
         var3.get(var4, 0, var4.length);
         ((Buffer)var3).rewind();
         var3.put(new char[var4.length]);
         var5 = var4;
      } catch (IOException var9) {
         throw new IllegalStateException("Could not read password from " + this.passwordPath + ": " + var9, var9);
      } finally {
         if (var1 != null) {
            Arrays.fill(var1, (byte)0);
         }
      }

      return var5;
   }
}
