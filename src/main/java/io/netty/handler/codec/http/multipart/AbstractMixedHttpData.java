package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.util.AbstractReferenceCounted;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

abstract class AbstractMixedHttpData<D extends HttpData> extends AbstractReferenceCounted implements HttpData {
   final String baseDir;
   final boolean deleteOnExit;
   D wrapped;
   private final long limitSize;

   AbstractMixedHttpData(long var1, String var3, boolean var4, D var5) {
      this.limitSize = var1;
      this.wrapped = (D)var5;
      this.baseDir = var3;
      this.deleteOnExit = var4;
   }

   abstract D makeDiskData();

   @Override
   public long getMaxSize() {
      return this.wrapped.getMaxSize();
   }

   @Override
   public void setMaxSize(long var1) {
      this.wrapped.setMaxSize(var1);
   }

   @Override
   public ByteBuf content() {
      return this.wrapped.content();
   }

   @Override
   public void checkSize(long var1) throws IOException {
      this.wrapped.checkSize(var1);
   }

   @Override
   public long definedLength() {
      return this.wrapped.definedLength();
   }

   @Override
   public Charset getCharset() {
      return this.wrapped.getCharset();
   }

   @Override
   public String getName() {
      return this.wrapped.getName();
   }

   @Override
   public void addContent(ByteBuf var1, boolean var2) throws IOException {
      if (this.wrapped instanceof AbstractMemoryHttpData) {
         try {
            this.checkSize(this.wrapped.length() + (long)var1.readableBytes());
            if (this.wrapped.length() + (long)var1.readableBytes() > this.limitSize) {
               HttpData var3 = this.makeDiskData();
               ByteBuf var4 = ((AbstractMemoryHttpData)this.wrapped).getByteBuf();
               if (var4 != null && var4.isReadable()) {
                  var3.addContent(var4.retain(), false);
               }

               this.wrapped.release();
               this.wrapped = (D)var3;
            }
         } catch (IOException var5) {
            var1.release();
            throw var5;
         }
      }

      this.wrapped.addContent(var1, var2);
   }

   @Override
   protected void deallocate() {
      this.delete();
   }

   @Override
   public void delete() {
      this.wrapped.delete();
   }

   @Override
   public byte[] get() throws IOException {
      return this.wrapped.get();
   }

   @Override
   public ByteBuf getByteBuf() throws IOException {
      return this.wrapped.getByteBuf();
   }

   @Override
   public String getString() throws IOException {
      return this.wrapped.getString();
   }

   @Override
   public String getString(Charset var1) throws IOException {
      return this.wrapped.getString(var1);
   }

   @Override
   public boolean isInMemory() {
      return this.wrapped.isInMemory();
   }

   @Override
   public long length() {
      return this.wrapped.length();
   }

   @Override
   public boolean renameTo(File var1) throws IOException {
      return this.wrapped.renameTo(var1);
   }

   @Override
   public void setCharset(Charset var1) {
      this.wrapped.setCharset(var1);
   }

   @Override
   public void setContent(ByteBuf var1) throws IOException {
      try {
         this.checkSize((long)var1.readableBytes());
      } catch (IOException var3) {
         var1.release();
         throw var3;
      }

      if ((long)var1.readableBytes() > this.limitSize && this.wrapped instanceof AbstractMemoryHttpData) {
         this.wrapped.release();
         this.wrapped = this.makeDiskData();
      }

      this.wrapped.setContent(var1);
   }

   @Override
   public void setContent(File var1) throws IOException {
      this.checkSize(var1.length());
      if (var1.length() > this.limitSize && this.wrapped instanceof AbstractMemoryHttpData) {
         this.wrapped.release();
         this.wrapped = this.makeDiskData();
      }

      this.wrapped.setContent(var1);
   }

   @Override
   public void setContent(InputStream var1) throws IOException {
      if (this.wrapped instanceof AbstractMemoryHttpData) {
         this.wrapped.release();
         this.wrapped = this.makeDiskData();
      }

      this.wrapped.setContent(var1);
   }

   @Override
   public boolean isCompleted() {
      return this.wrapped.isCompleted();
   }

   @Override
   public InterfaceHttpData.HttpDataType getHttpDataType() {
      return this.wrapped.getHttpDataType();
   }

   @Override
   public int hashCode() {
      return this.wrapped.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      return this.wrapped.equals(var1);
   }

   public int compareTo(InterfaceHttpData var1) {
      return this.wrapped.compareTo(var1);
   }

   @Override
   public String toString() {
      return "Mixed: " + this.wrapped;
   }

   @Override
   public ByteBuf getChunk(int var1) throws IOException {
      return this.wrapped.getChunk(var1);
   }

   @Override
   public File getFile() throws IOException {
      return this.wrapped.getFile();
   }

   @Override
   public D copy() {
      return (D)this.wrapped.copy();
   }

   @Override
   public D duplicate() {
      return (D)this.wrapped.duplicate();
   }

   @Override
   public D retainedDuplicate() {
      return (D)this.wrapped.retainedDuplicate();
   }

   @Override
   public D replace(ByteBuf var1) {
      return (D)this.wrapped.replace(var1);
   }

   @Override
   public D touch() {
      this.wrapped.touch();
      return (D)this;
   }

   @Override
   public D touch(Object var1) {
      this.wrapped.touch(var1);
      return (D)this;
   }

   @Override
   public D retain() {
      return (D)super.retain();
   }

   @Override
   public D retain(int var1) {
      return (D)super.retain(var1);
   }
}
