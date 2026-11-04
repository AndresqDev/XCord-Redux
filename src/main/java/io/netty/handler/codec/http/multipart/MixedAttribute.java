package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpConstants;
import java.io.IOException;
import java.nio.charset.Charset;

public class MixedAttribute extends AbstractMixedHttpData<Attribute> implements Attribute {
   public MixedAttribute(String var1, long var2) {
      this(var1, var2, HttpConstants.DEFAULT_CHARSET);
   }

   public MixedAttribute(String var1, long var2, long var4) {
      this(var1, var2, var4, HttpConstants.DEFAULT_CHARSET);
   }

   public MixedAttribute(String var1, long var2, Charset var4) {
      this(var1, var2, var4, DiskAttribute.baseDirectory, DiskAttribute.deleteOnExitTemporaryFile);
   }

   public MixedAttribute(String var1, long var2, Charset var4, String var5, boolean var6) {
      this(var1, 0L, var2, var4, var5, var6);
   }

   public MixedAttribute(String var1, long var2, long var4, Charset var6) {
      this(var1, var2, var4, var6, DiskAttribute.baseDirectory, DiskAttribute.deleteOnExitTemporaryFile);
   }

   public MixedAttribute(String var1, long var2, long var4, Charset var6, String var7, boolean var8) {
      super(var4, var7, var8, new MemoryAttribute(var1, var2, var6));
   }

   public MixedAttribute(String var1, String var2, long var3) {
      this(var1, var2, var3, HttpConstants.DEFAULT_CHARSET, DiskAttribute.baseDirectory, DiskFileUpload.deleteOnExitTemporaryFile);
   }

   public MixedAttribute(String var1, String var2, long var3, Charset var5) {
      this(var1, var2, var3, var5, DiskAttribute.baseDirectory, DiskFileUpload.deleteOnExitTemporaryFile);
   }

   private static Attribute makeInitialAttributeFromValue(String var0, String var1, long var2, Charset var4, String var5, boolean var6) {
      if ((long)var1.length() > var2) {
         try {
            return new DiskAttribute(var0, var1, var4, var5, var6);
         } catch (IOException var10) {
            try {
               return new MemoryAttribute(var0, var1, var4);
            } catch (IOException var9) {
               throw new IllegalArgumentException(var10);
            }
         }
      } else {
         try {
            return new MemoryAttribute(var0, var1, var4);
         } catch (IOException var11) {
            throw new IllegalArgumentException(var11);
         }
      }
   }

   public MixedAttribute(String var1, String var2, long var3, Charset var5, String var6, boolean var7) {
      super(var3, var6, var7, makeInitialAttributeFromValue(var1, var2, var3, var5, var6, var7));
   }

   @Override
   public String getValue() throws IOException {
      return this.wrapped.getValue();
   }

   @Override
   public void setValue(String var1) throws IOException {
      this.wrapped.setValue(var1);
   }

   Attribute makeDiskData() {
      DiskAttribute var1 = new DiskAttribute(this.getName(), this.definedLength(), this.baseDir, this.deleteOnExit);
      var1.setMaxSize(this.getMaxSize());
      return var1;
   }

   @Override
   public Attribute copy() {
      return (Attribute)super.copy();
   }

   @Override
   public Attribute duplicate() {
      return (Attribute)super.duplicate();
   }

   @Override
   public Attribute replace(ByteBuf var1) {
      return (Attribute)super.replace(var1);
   }

   @Override
   public Attribute retain() {
      return (Attribute)super.retain();
   }

   @Override
   public Attribute retain(int var1) {
      return (Attribute)super.retain(var1);
   }

   @Override
   public Attribute retainedDuplicate() {
      return (Attribute)super.retainedDuplicate();
   }

   @Override
   public Attribute touch() {
      return (Attribute)super.touch();
   }

   @Override
   public Attribute touch(Object var1) {
      return (Attribute)super.touch(var1);
   }
}
