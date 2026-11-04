package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

final class InternalAttribute extends AbstractReferenceCounted implements InterfaceHttpData {
   private final List<ByteBuf> value = new ArrayList<>();
   private final Charset charset;
   private int size;

   InternalAttribute(Charset var1) {
      this.charset = var1;
   }

   @Override
   public InterfaceHttpData.HttpDataType getHttpDataType() {
      return InterfaceHttpData.HttpDataType.InternalAttribute;
   }

   public void addValue(String var1) {
      ObjectUtil.checkNotNull(var1, "value");
      ByteBuf var2 = Unpooled.copiedBuffer(var1, this.charset);
      this.value.add(var2);
      this.size = this.size + var2.readableBytes();
   }

   public void addValue(String var1, int var2) {
      ObjectUtil.checkNotNull(var1, "value");
      ByteBuf var3 = Unpooled.copiedBuffer(var1, this.charset);
      this.value.add(var2, var3);
      this.size = this.size + var3.readableBytes();
   }

   public void setValue(String var1, int var2) {
      ObjectUtil.checkNotNull(var1, "value");
      ByteBuf var3 = Unpooled.copiedBuffer(var1, this.charset);
      ByteBuf var4 = this.value.set(var2, var3);
      if (var4 != null) {
         this.size = this.size - var4.readableBytes();
         var4.release();
      }

      this.size = this.size + var3.readableBytes();
   }

   @Override
   public int hashCode() {
      return this.getName().hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof InternalAttribute)) {
         return false;
      } else {
         InternalAttribute var2 = (InternalAttribute)var1;
         return this.getName().equalsIgnoreCase(var2.getName());
      }
   }

   public int compareTo(InterfaceHttpData var1) {
      if (!(var1 instanceof InternalAttribute)) {
         throw new ClassCastException("Cannot compare " + this.getHttpDataType() + " with " + var1.getHttpDataType());
      } else {
         return this.compareTo((InternalAttribute)var1);
      }
   }

   public int compareTo(InternalAttribute var1) {
      return this.getName().compareToIgnoreCase(var1.getName());
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();

      for (ByteBuf var3 : this.value) {
         var1.append(var3.toString(this.charset));
      }

      return var1.toString();
   }

   public int size() {
      return this.size;
   }

   public ByteBuf toByteBuf() {
      return Unpooled.compositeBuffer().addComponents(this.value).writerIndex(this.size()).readerIndex(0);
   }

   @Override
   public String getName() {
      return "InternalAttribute";
   }

   @Override
   protected void deallocate() {
   }

   @Override
   public InterfaceHttpData retain() {
      for (ByteBuf var2 : this.value) {
         var2.retain();
      }

      return this;
   }

   @Override
   public InterfaceHttpData retain(int var1) {
      for (ByteBuf var3 : this.value) {
         var3.retain(var1);
      }

      return this;
   }

   @Override
   public InterfaceHttpData touch() {
      for (ByteBuf var2 : this.value) {
         var2.touch();
      }

      return this;
   }

   @Override
   public InterfaceHttpData touch(Object var1) {
      for (ByteBuf var3 : this.value) {
         var3.touch(var1);
      }

      return this;
   }
}
