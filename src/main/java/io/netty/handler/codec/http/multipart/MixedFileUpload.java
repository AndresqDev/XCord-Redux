package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import java.nio.charset.Charset;

public class MixedFileUpload extends AbstractMixedHttpData<FileUpload> implements FileUpload {
   public MixedFileUpload(String var1, String var2, String var3, String var4, Charset var5, long var6, long var8) {
      this(var1, var2, var3, var4, var5, var6, var8, DiskFileUpload.baseDirectory, DiskFileUpload.deleteOnExitTemporaryFile);
   }

   public MixedFileUpload(String var1, String var2, String var3, String var4, Charset var5, long var6, long var8, String var10, boolean var11) {
      super(
         var8,
         var10,
         var11,
         (FileUpload)(var6 > var8 ? new DiskFileUpload(var1, var2, var3, var4, var5, var6) : new MemoryFileUpload(var1, var2, var3, var4, var5, var6))
      );
   }

   @Override
   public String getContentTransferEncoding() {
      return this.wrapped.getContentTransferEncoding();
   }

   @Override
   public String getFilename() {
      return this.wrapped.getFilename();
   }

   @Override
   public void setContentTransferEncoding(String var1) {
      this.wrapped.setContentTransferEncoding(var1);
   }

   @Override
   public void setFilename(String var1) {
      this.wrapped.setFilename(var1);
   }

   @Override
   public void setContentType(String var1) {
      this.wrapped.setContentType(var1);
   }

   @Override
   public String getContentType() {
      return this.wrapped.getContentType();
   }

   FileUpload makeDiskData() {
      DiskFileUpload var1 = new DiskFileUpload(
         this.getName(),
         this.getFilename(),
         this.getContentType(),
         this.getContentTransferEncoding(),
         this.getCharset(),
         this.definedLength(),
         this.baseDir,
         this.deleteOnExit
      );
      var1.setMaxSize(this.getMaxSize());
      return var1;
   }

   @Override
   public FileUpload copy() {
      return (FileUpload)super.copy();
   }

   @Override
   public FileUpload duplicate() {
      return (FileUpload)super.duplicate();
   }

   @Override
   public FileUpload retainedDuplicate() {
      return (FileUpload)super.retainedDuplicate();
   }

   @Override
   public FileUpload replace(ByteBuf var1) {
      return (FileUpload)super.replace(var1);
   }

   @Override
   public FileUpload touch() {
      return (FileUpload)super.touch();
   }

   @Override
   public FileUpload touch(Object var1) {
      return (FileUpload)super.touch(var1);
   }

   @Override
   public FileUpload retain() {
      return (FileUpload)super.retain();
   }

   @Override
   public FileUpload retain(int var1) {
      return (FileUpload)super.retain(var1);
   }
}
