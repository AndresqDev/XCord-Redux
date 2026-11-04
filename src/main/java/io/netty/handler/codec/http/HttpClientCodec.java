package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.util.ReferenceCountUtil;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

public final class HttpClientCodec extends CombinedChannelDuplexHandler<HttpResponseDecoder, HttpRequestEncoder> implements HttpClientUpgradeHandler.SourceCodec {
   public static final boolean DEFAULT_FAIL_ON_MISSING_RESPONSE = false;
   public static final boolean DEFAULT_PARSE_HTTP_AFTER_CONNECT_REQUEST = false;
   private final Queue<HttpMethod> queue = new ArrayDeque<>();
   private final boolean parseHttpAfterConnectRequest;
   private boolean done;
   private final AtomicLong requestResponseCounter = new AtomicLong();
   private final boolean failOnMissingResponse;

   public HttpClientCodec() {
      this(4096, 8192, 8192, false);
   }

   public HttpClientCodec(int var1, int var2, int var3) {
      this(var1, var2, var3, false);
   }

   public HttpClientCodec(int var1, int var2, int var3, boolean var4) {
      this(var1, var2, var3, var4, true);
   }

   public HttpClientCodec(int var1, int var2, int var3, boolean var4, boolean var5) {
      this(var1, var2, var3, var4, var5, false);
   }

   public HttpClientCodec(int var1, int var2, int var3, boolean var4, boolean var5, boolean var6) {
      this.init(new HttpClientCodec.Decoder(var1, var2, var3, var5), new HttpClientCodec.Encoder());
      this.failOnMissingResponse = var4;
      this.parseHttpAfterConnectRequest = var6;
   }

   public HttpClientCodec(int var1, int var2, int var3, boolean var4, boolean var5, int var6) {
      this(var1, var2, var3, var4, var5, var6, false);
   }

   public HttpClientCodec(int var1, int var2, int var3, boolean var4, boolean var5, int var6, boolean var7) {
      this(var1, var2, var3, var4, var5, var6, var7, false);
   }

   public HttpClientCodec(int var1, int var2, int var3, boolean var4, boolean var5, int var6, boolean var7, boolean var8) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, true);
   }

   public HttpClientCodec(int var1, int var2, int var3, boolean var4, boolean var5, int var6, boolean var7, boolean var8, boolean var9) {
      this.init(new HttpClientCodec.Decoder(var1, var2, var3, var5, var6, var8, var9), new HttpClientCodec.Encoder());
      this.parseHttpAfterConnectRequest = var7;
      this.failOnMissingResponse = var4;
   }

   @Override
   public void prepareUpgradeFrom(ChannelHandlerContext var1) {
      ((HttpClientCodec.Encoder)this.outboundHandler()).upgraded = true;
   }

   @Override
   public void upgradeFrom(ChannelHandlerContext var1) {
      ChannelPipeline var2 = var1.pipeline();
      var2.remove(this);
   }

   public void setSingleDecode(boolean var1) {
      this.inboundHandler().setSingleDecode(var1);
   }

   public boolean isSingleDecode() {
      return this.inboundHandler().isSingleDecode();
   }

   private final class Decoder extends HttpResponseDecoder {
      Decoder(int var2, int var3, int var4, boolean var5) {
         super(var2, var3, var4, var5);
      }

      Decoder(int var2, int var3, int var4, boolean var5, int var6, boolean var7, boolean var8) {
         super(var2, var3, var4, var5, var6, var7, var8);
      }

      @Override
      protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
         if (HttpClientCodec.this.done) {
            int var4 = this.actualReadableBytes();
            if (var4 == 0) {
               return;
            }

            var3.add(var2.readBytes(var4));
         } else {
            int var7 = var3.size();
            super.decode(var1, var2, var3);
            if (HttpClientCodec.this.failOnMissingResponse) {
               int var5 = var3.size();

               for (int var6 = var7; var6 < var5; var6++) {
                  this.decrement(var3.get(var6));
               }
            }
         }
      }

      private void decrement(Object var1) {
         if (var1 != null) {
            if (var1 instanceof LastHttpContent) {
               HttpClientCodec.this.requestResponseCounter.decrementAndGet();
            }
         }
      }

      @Override
      protected boolean isContentAlwaysEmpty(HttpMessage var1) {
         HttpMethod var2 = HttpClientCodec.this.queue.poll();
         int var3 = ((HttpResponse)var1).status().code();
         if (var3 >= 100 && var3 < 200) {
            return super.isContentAlwaysEmpty(var1);
         } else {
            if (var2 != null) {
               char var4 = var2.name().charAt(0);
               switch (var4) {
                  case 'C':
                     if (var3 == 200 && HttpMethod.CONNECT.equals(var2)) {
                        if (!HttpClientCodec.this.parseHttpAfterConnectRequest) {
                           HttpClientCodec.this.done = true;
                           HttpClientCodec.this.queue.clear();
                        }

                        return true;
                     }
                     break;
                  case 'H':
                     if (HttpMethod.HEAD.equals(var2)) {
                        return true;
                     }
               }
            }

            return super.isContentAlwaysEmpty(var1);
         }
      }

      @Override
      public void channelInactive(ChannelHandlerContext var1) throws Exception {
         super.channelInactive(var1);
         if (HttpClientCodec.this.failOnMissingResponse) {
            long var2 = HttpClientCodec.this.requestResponseCounter.get();
            if (var2 > 0L) {
               var1.fireExceptionCaught(new PrematureChannelClosureException("channel gone inactive with " + var2 + " missing response(s)"));
            }
         }
      }
   }

   private final class Encoder extends HttpRequestEncoder {
      boolean upgraded;

      private Encoder() {
      }

      @Override
      protected void encode(ChannelHandlerContext var1, Object var2, List<Object> var3) throws Exception {
         if (this.upgraded) {
            var3.add(ReferenceCountUtil.retain(var2));
         } else {
            if (var2 instanceof HttpRequest) {
               HttpClientCodec.this.queue.offer(((HttpRequest)var2).method());
            }

            super.encode(var1, var2, var3);
            if (HttpClientCodec.this.failOnMissingResponse && !HttpClientCodec.this.done && var2 instanceof LastHttpContent) {
               HttpClientCodec.this.requestResponseCounter.incrementAndGet();
            }
         }
      }
   }
}
