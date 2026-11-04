package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelException;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public abstract class AbstractHttpData extends AbstractReferenceCounted implements HttpData {
   private static final Pattern STRIP_PATTERN = Pattern.compile("(?:^\\s+|\\s+$|\\n)");
   private static final Pattern REPLACE_PATTERN = Pattern.compile("[\\r\\t]");
   private final String name;
   protected long definedSize;
   protected long size;
   private Charset charset = HttpConstants.DEFAULT_CHARSET;
   private boolean completed;
   private long maxSize = -1L;

   protected AbstractHttpData(String var1, Charset var2, long var3) {
      ObjectUtil.checkNotNull(var1, "name");
      var1 = REPLACE_PATTERN.matcher(var1).replaceAll(" ");
      var1 = STRIP_PATTERN.matcher(var1).replaceAll("");
      this.name = ObjectUtil.checkNonEmpty(var1, "name");
      if (var2 != null) {
         this.setCharset(var2);
      }

      this.definedSize = var3;
   }

   @Override
   public long getMaxSize() {
      return this.maxSize;
   }

   @Override
   public void setMaxSize(long var1) {
      this.maxSize = var1;
   }

   @Override
   public void checkSize(long var1) throws IOException {
      if (this.maxSize >= 0L && var1 > this.maxSize) {
         throw new IOException("Size exceed allowed maximum capacity");
      }
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public boolean isCompleted() {
      return this.completed;
   }

   protected void setCompleted() {
      this.setCompleted(true);
   }

   protected void setCompleted(boolean var1) {
      this.completed = var1;
   }

   @Override
   public Charset getCharset() {
      return this.charset;
   }

   @Override
   public void setCharset(Charset var1) {
      this.charset = ObjectUtil.checkNotNull(var1, "charset");
   }

   @Override
   public long length() {
      return this.size;
   }

   @Override
   public long definedLength() {
      return this.definedSize;
   }

   @Override
   public ByteBuf content() {
      try {
         return this.getByteBuf();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   @Override
   protected void deallocate() {
      this.delete();
   }

   @Override
   public HttpData retain() {
      super.retain();
      return this;
   }

   @Override
   public HttpData retain(int var1) {
      super.retain(var1);
      return this;
   }

   @Override
   public abstract HttpData touch();

   @Override
   public abstract HttpData touch(Object var1);
}
