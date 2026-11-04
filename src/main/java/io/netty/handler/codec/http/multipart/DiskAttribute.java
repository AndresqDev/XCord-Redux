package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelException;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.nio.charset.Charset;

public class DiskAttribute extends AbstractDiskHttpData implements Attribute {
   public static String baseDirectory;
   public static boolean deleteOnExitTemporaryFile = true;
   public static final String prefix = "Attr_";
   public static final String postfix = ".att";
   private String baseDir;
   private boolean deleteOnExit;

   public DiskAttribute(String var1) {
      this(var1, HttpConstants.DEFAULT_CHARSET);
   }

   public DiskAttribute(String var1, String var2, boolean var3) {
      this(var1, HttpConstants.DEFAULT_CHARSET);
      this.baseDir = var2 == null ? baseDirectory : var2;
      this.deleteOnExit = var3;
   }

   public DiskAttribute(String var1, long var2) {
      this(var1, var2, HttpConstants.DEFAULT_CHARSET, baseDirectory, deleteOnExitTemporaryFile);
   }

   public DiskAttribute(String var1, long var2, String var4, boolean var5) {
      this(var1, var2, HttpConstants.DEFAULT_CHARSET);
      this.baseDir = var4 == null ? baseDirectory : var4;
      this.deleteOnExit = var5;
   }

   public DiskAttribute(String var1, Charset var2) {
      this(var1, var2, baseDirectory, deleteOnExitTemporaryFile);
   }

   public DiskAttribute(String var1, Charset var2, String var3, boolean var4) {
      super(var1, var2, 0L);
      this.baseDir = var3 == null ? baseDirectory : var3;
      this.deleteOnExit = var4;
   }

   public DiskAttribute(String var1, long var2, Charset var4) {
      this(var1, var2, var4, baseDirectory, deleteOnExitTemporaryFile);
   }

   public DiskAttribute(String var1, long var2, Charset var4, String var5, boolean var6) {
      super(var1, var4, var2);
      this.baseDir = var5 == null ? baseDirectory : var5;
      this.deleteOnExit = var6;
   }

   public DiskAttribute(String var1, String var2) throws IOException {
      this(var1, var2, HttpConstants.DEFAULT_CHARSET);
   }

   public DiskAttribute(String var1, String var2, Charset var3) throws IOException {
      this(var1, var2, var3, baseDirectory, deleteOnExitTemporaryFile);
   }

   public DiskAttribute(String var1, String var2, Charset var3, String var4, boolean var5) throws IOException {
      super(var1, var3, 0L);
      this.setValue(var2);
      this.baseDir = var4 == null ? baseDirectory : var4;
      this.deleteOnExit = var5;
   }

   @Override
   public InterfaceHttpData.HttpDataType getHttpDataType() {
      return InterfaceHttpData.HttpDataType.Attribute;
   }

   @Override
   public String getValue() throws IOException {
      byte[] var1 = this.get();
      return new String(var1, this.getCharset());
   }

   @Override
   public void setValue(String var1) throws IOException {
      ObjectUtil.checkNotNull(var1, "value");
      byte[] var2 = var1.getBytes(this.getCharset());
      this.checkSize((long)var2.length);
      ByteBuf var3 = Unpooled.wrappedBuffer(var2);
      if (this.definedSize > 0L) {
         this.definedSize = (long)var3.readableBytes();
      }

      this.setContent(var3);
   }

   @Override
   public void addContent(ByteBuf var1, boolean var2) throws IOException {
      long var3 = this.size + (long)var1.readableBytes();

      try {
         this.checkSize(var3);
      } catch (IOException var6) {
         var1.release();
         throw var6;
      }

      if (this.definedSize > 0L && this.definedSize < var3) {
         this.definedSize = var3;
      }

      super.addContent(var1, var2);
   }

   @Override
   public int hashCode() {
      return this.getName().hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof Attribute)) {
         return false;
      } else {
         Attribute var2 = (Attribute)var1;
         return this.getName().equalsIgnoreCase(var2.getName());
      }
   }

   public int compareTo(InterfaceHttpData var1) {
      if (!(var1 instanceof Attribute)) {
         throw new ClassCastException("Cannot compare " + this.getHttpDataType() + " with " + var1.getHttpDataType());
      } else {
         return this.compareTo((Attribute)var1);
      }
   }

   public int compareTo(Attribute var1) {
      return this.getName().compareToIgnoreCase(var1.getName());
   }

   @Override
   public String toString() {
      try {
         return this.getName() + '=' + this.getValue();
      } catch (IOException var2) {
         return this.getName() + '=' + var2;
      }
   }

   @Override
   protected boolean deleteOnExit() {
      return this.deleteOnExit;
   }

   @Override
   protected String getBaseDirectory() {
      return this.baseDir;
   }

   @Override
   protected String getDiskFilename() {
      return this.getName() + ".att";
   }

   @Override
   protected String getPostfix() {
      return ".att";
   }

   @Override
   protected String getPrefix() {
      return "Attr_";
   }

   @Override
   public Attribute copy() {
      ByteBuf var1 = this.content();
      return this.replace(var1 != null ? var1.copy() : null);
   }

   @Override
   public Attribute duplicate() {
      ByteBuf var1 = this.content();
      return this.replace(var1 != null ? var1.duplicate() : null);
   }

   @Override
   public Attribute retainedDuplicate() {
      ByteBuf var1 = this.content();
      if (var1 != null) {
         var1 = var1.retainedDuplicate();
         boolean var2 = false;

         Attribute var4;
         try {
            Attribute var3 = this.replace(var1);
            var2 = true;
            var4 = var3;
         } finally {
            if (!var2) {
               var1.release();
            }
         }

         return var4;
      } else {
         return this.replace(null);
      }
   }

   @Override
   public Attribute replace(ByteBuf var1) {
      DiskAttribute var2 = new DiskAttribute(this.getName(), this.baseDir, this.deleteOnExit);
      var2.setCharset(this.getCharset());
      if (var1 != null) {
         try {
            var2.setContent(var1);
         } catch (IOException var4) {
            throw new ChannelException(var4);
         }
      }

      var2.setCompleted(this.isCompleted());
      return var2;
   }

   @Override
   public Attribute retain(int var1) {
      super.retain(var1);
      return this;
   }

   @Override
   public Attribute retain() {
      super.retain();
      return this;
   }

   @Override
   public Attribute touch() {
      super.touch();
      return this;
   }

   @Override
   public Attribute touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
