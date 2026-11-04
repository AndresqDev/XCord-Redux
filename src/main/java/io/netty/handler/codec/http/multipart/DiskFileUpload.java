package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelException;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class DiskFileUpload extends AbstractDiskHttpData implements FileUpload {
   public static String baseDirectory;
   public static boolean deleteOnExitTemporaryFile = true;
   public static final String prefix = "FUp_";
   public static final String postfix = ".tmp";
   private final String baseDir;
   private final boolean deleteOnExit;
   private String filename;
   private String contentType;
   private String contentTransferEncoding;

   public DiskFileUpload(String var1, String var2, String var3, String var4, Charset var5, long var6, String var8, boolean var9) {
      super(var1, var5, var6);
      this.setFilename(var2);
      this.setContentType(var3);
      this.setContentTransferEncoding(var4);
      this.baseDir = var8 == null ? baseDirectory : var8;
      this.deleteOnExit = var9;
   }

   public DiskFileUpload(String var1, String var2, String var3, String var4, Charset var5, long var6) {
      this(var1, var2, var3, var4, var5, var6, baseDirectory, deleteOnExitTemporaryFile);
   }

   @Override
   public InterfaceHttpData.HttpDataType getHttpDataType() {
      return InterfaceHttpData.HttpDataType.FileUpload;
   }

   @Override
   public String getFilename() {
      return this.filename;
   }

   @Override
   public void setFilename(String var1) {
      this.filename = ObjectUtil.checkNotNull(var1, "filename");
   }

   @Override
   public int hashCode() {
      return FileUploadUtil.hashCode(this);
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof FileUpload && FileUploadUtil.equals(this, (FileUpload)var1);
   }

   public int compareTo(InterfaceHttpData var1) {
      if (!(var1 instanceof FileUpload)) {
         throw new ClassCastException("Cannot compare " + this.getHttpDataType() + " with " + var1.getHttpDataType());
      } else {
         return this.compareTo((FileUpload)var1);
      }
   }

   public int compareTo(FileUpload var1) {
      return FileUploadUtil.compareTo(this, var1);
   }

   @Override
   public void setContentType(String var1) {
      this.contentType = ObjectUtil.checkNotNull(var1, "contentType");
   }

   @Override
   public String getContentType() {
      return this.contentType;
   }

   @Override
   public String getContentTransferEncoding() {
      return this.contentTransferEncoding;
   }

   @Override
   public void setContentTransferEncoding(String var1) {
      this.contentTransferEncoding = var1;
   }

   @Override
   public String toString() {
      File var1 = null;

      try {
         var1 = this.getFile();
      } catch (IOException var3) {
      }

      return HttpHeaderNames.CONTENT_DISPOSITION
         + ": "
         + HttpHeaderValues.FORM_DATA
         + "; "
         + HttpHeaderValues.NAME
         + "=\""
         + this.getName()
         + "\"; "
         + HttpHeaderValues.FILENAME
         + "=\""
         + this.filename
         + "\"\r\n"
         + HttpHeaderNames.CONTENT_TYPE
         + ": "
         + this.contentType
         + (this.getCharset() != null ? "; " + HttpHeaderValues.CHARSET + '=' + this.getCharset().name() + "\r\n" : "\r\n")
         + HttpHeaderNames.CONTENT_LENGTH
         + ": "
         + this.length()
         + "\r\nCompleted: "
         + this.isCompleted()
         + "\r\nIsInMemory: "
         + this.isInMemory()
         + "\r\nRealFile: "
         + (var1 != null ? var1.getAbsolutePath() : "null")
         + " DeleteAfter: "
         + this.deleteOnExit;
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
      return "upload";
   }

   @Override
   protected String getPostfix() {
      return ".tmp";
   }

   @Override
   protected String getPrefix() {
      return "FUp_";
   }

   @Override
   public FileUpload copy() {
      ByteBuf var1 = this.content();
      return this.replace(var1 != null ? var1.copy() : null);
   }

   @Override
   public FileUpload duplicate() {
      ByteBuf var1 = this.content();
      return this.replace(var1 != null ? var1.duplicate() : null);
   }

   @Override
   public FileUpload retainedDuplicate() {
      ByteBuf var1 = this.content();
      if (var1 != null) {
         var1 = var1.retainedDuplicate();
         boolean var2 = false;

         FileUpload var4;
         try {
            FileUpload var3 = this.replace(var1);
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
   public FileUpload replace(ByteBuf var1) {
      DiskFileUpload var2 = new DiskFileUpload(
         this.getName(),
         this.getFilename(),
         this.getContentType(),
         this.getContentTransferEncoding(),
         this.getCharset(),
         this.size,
         this.baseDir,
         this.deleteOnExit
      );
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
   public FileUpload retain(int var1) {
      super.retain(var1);
      return this;
   }

   @Override
   public FileUpload retain() {
      super.retain();
      return this;
   }

   @Override
   public FileUpload touch() {
      super.touch();
      return this;
   }

   @Override
   public FileUpload touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
