package io.netty.handler.codec.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HttpServerUpgradeHandler extends HttpObjectAggregator {
   private final HttpServerUpgradeHandler.SourceCodec sourceCodec;
   private final HttpServerUpgradeHandler.UpgradeCodecFactory upgradeCodecFactory;
   private final boolean validateHeaders;
   private boolean handlingUpgrade;

   public HttpServerUpgradeHandler(HttpServerUpgradeHandler.SourceCodec var1, HttpServerUpgradeHandler.UpgradeCodecFactory var2) {
      this(var1, var2, 0);
   }

   public HttpServerUpgradeHandler(HttpServerUpgradeHandler.SourceCodec var1, HttpServerUpgradeHandler.UpgradeCodecFactory var2, int var3) {
      this(var1, var2, var3, true);
   }

   public HttpServerUpgradeHandler(HttpServerUpgradeHandler.SourceCodec var1, HttpServerUpgradeHandler.UpgradeCodecFactory var2, int var3, boolean var4) {
      super(var3);
      this.sourceCodec = ObjectUtil.checkNotNull(var1, "sourceCodec");
      this.upgradeCodecFactory = ObjectUtil.checkNotNull(var2, "upgradeCodecFactory");
      this.validateHeaders = var4;
   }

   protected void decode(ChannelHandlerContext var1, HttpObject var2, List<Object> var3) throws Exception {
      if (!this.handlingUpgrade) {
         if (!(var2 instanceof HttpRequest)) {
            ReferenceCountUtil.retain(var2);
            var1.fireChannelRead(var2);
            return;
         }

         HttpRequest var4 = (HttpRequest)var2;
         if (!var4.headers().contains(HttpHeaderNames.UPGRADE) || !this.shouldHandleUpgradeRequest(var4)) {
            ReferenceCountUtil.retain(var2);
            var1.fireChannelRead(var2);
            return;
         }

         this.handlingUpgrade = true;
      }

      FullHttpRequest var5;
      if (var2 instanceof FullHttpRequest) {
         var5 = (FullHttpRequest)var2;
         ReferenceCountUtil.retain(var2);
         var3.add(var2);
      } else {
         super.decode(var1, var2, var3);
         if (var3.isEmpty()) {
            return;
         }

         assert var3.size() == 1;

         this.handlingUpgrade = false;
         var5 = (FullHttpRequest)var3.get(0);
      }

      if (this.upgrade(var1, var5)) {
         var3.clear();
      }
   }

   protected boolean shouldHandleUpgradeRequest(HttpRequest var1) {
      return true;
   }

   private boolean upgrade(ChannelHandlerContext var1, FullHttpRequest var2) {
      List var3 = splitHeader(var2.headers().get(HttpHeaderNames.UPGRADE));
      int var4 = var3.size();
      HttpServerUpgradeHandler.UpgradeCodec var5 = null;
      CharSequence var6 = null;

      for (int var7 = 0; var7 < var4; var7++) {
         CharSequence var8 = (CharSequence)var3.get(var7);
         HttpServerUpgradeHandler.UpgradeCodec var9 = this.upgradeCodecFactory.newUpgradeCodec(var8);
         if (var9 != null) {
            var6 = var8;
            var5 = var9;
            break;
         }
      }

      if (var5 == null) {
         return false;
      } else {
         List var17 = var2.headers().getAll(HttpHeaderNames.CONNECTION);
         if (var17 != null && !var17.isEmpty()) {
            StringBuilder var18 = new StringBuilder(var17.size() * 10);

            for (CharSequence var10 : var17) {
               var18.append(var10).append(',');
            }

            var18.setLength(var18.length() - 1);
            Collection var20 = var5.requiredUpgradeHeaders();
            List var21 = splitHeader(var18);
            if (AsciiString.containsContentEqualsIgnoreCase(var21, HttpHeaderNames.UPGRADE) && AsciiString.containsAllContentEqualsIgnoreCase(var21, var20)) {
               for (CharSequence var12 : var20) {
                  if (!var2.headers().contains(var12)) {
                     return false;
                  }
               }

               FullHttpResponse var22 = this.createUpgradeResponse(var6);
               if (!var5.prepareUpgradeResponse(var1, var2, var22.headers())) {
                  return false;
               } else {
                  HttpServerUpgradeHandler.UpgradeEvent var23 = new HttpServerUpgradeHandler.UpgradeEvent(var6, var2);

                  try {
                     ChannelFuture var13 = var1.writeAndFlush(var22);
                     this.sourceCodec.upgradeFrom(var1);
                     var5.upgradeTo(var1, var2);
                     var1.pipeline().remove(this);
                     var1.fireUserEventTriggered(var23.retain());
                     var13.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                  } finally {
                     var23.release();
                  }

                  return true;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   private FullHttpResponse createUpgradeResponse(CharSequence var1) {
      DefaultFullHttpResponse var2 = new DefaultFullHttpResponse(
         HttpVersion.HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS, Unpooled.EMPTY_BUFFER, this.validateHeaders
      );
      var2.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE);
      var2.headers().add(HttpHeaderNames.UPGRADE, var1);
      return var2;
   }

   private static List<CharSequence> splitHeader(CharSequence var0) {
      StringBuilder var1 = new StringBuilder(var0.length());
      ArrayList var2 = new ArrayList(4);

      for (int var3 = 0; var3 < var0.length(); var3++) {
         char var4 = var0.charAt(var3);
         if (!Character.isWhitespace(var4)) {
            if (var4 == ',') {
               var2.add(var1.toString());
               var1.setLength(0);
            } else {
               var1.append(var4);
            }
         }
      }

      if (var1.length() > 0) {
         var2.add(var1.toString());
      }

      return var2;
   }

   public interface SourceCodec {
      void upgradeFrom(ChannelHandlerContext var1);
   }

   public interface UpgradeCodec {
      Collection<CharSequence> requiredUpgradeHeaders();

      boolean prepareUpgradeResponse(ChannelHandlerContext var1, FullHttpRequest var2, HttpHeaders var3);

      void upgradeTo(ChannelHandlerContext var1, FullHttpRequest var2);
   }

   public interface UpgradeCodecFactory {
      HttpServerUpgradeHandler.UpgradeCodec newUpgradeCodec(CharSequence var1);
   }

   public static final class UpgradeEvent implements ReferenceCounted {
      private final CharSequence protocol;
      private final FullHttpRequest upgradeRequest;

      UpgradeEvent(CharSequence var1, FullHttpRequest var2) {
         this.protocol = var1;
         this.upgradeRequest = var2;
      }

      public CharSequence protocol() {
         return this.protocol;
      }

      public FullHttpRequest upgradeRequest() {
         return this.upgradeRequest;
      }

      @Override
      public int refCnt() {
         return this.upgradeRequest.refCnt();
      }

      public HttpServerUpgradeHandler.UpgradeEvent retain() {
         this.upgradeRequest.retain();
         return this;
      }

      public HttpServerUpgradeHandler.UpgradeEvent retain(int var1) {
         this.upgradeRequest.retain(var1);
         return this;
      }

      public HttpServerUpgradeHandler.UpgradeEvent touch() {
         this.upgradeRequest.touch();
         return this;
      }

      public HttpServerUpgradeHandler.UpgradeEvent touch(Object var1) {
         this.upgradeRequest.touch(var1);
         return this;
      }

      @Override
      public boolean release() {
         return this.upgradeRequest.release();
      }

      @Override
      public boolean release(int var1) {
         return this.upgradeRequest.release(var1);
      }

      @Override
      public String toString() {
         return "UpgradeEvent [protocol=" + this.protocol + ", upgradeRequest=" + this.upgradeRequest + ']';
      }
   }
}
