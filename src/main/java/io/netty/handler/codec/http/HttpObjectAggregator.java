package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageAggregator;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class HttpObjectAggregator extends MessageAggregator<HttpObject, HttpMessage, HttpContent, FullHttpMessage> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(HttpObjectAggregator.class);
   private static final FullHttpResponse CONTINUE = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE, Unpooled.EMPTY_BUFFER);
   private static final FullHttpResponse EXPECTATION_FAILED = new DefaultFullHttpResponse(
      HttpVersion.HTTP_1_1, HttpResponseStatus.EXPECTATION_FAILED, Unpooled.EMPTY_BUFFER
   );
   private static final FullHttpResponse TOO_LARGE_CLOSE = new DefaultFullHttpResponse(
      HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, Unpooled.EMPTY_BUFFER
   );
   private static final FullHttpResponse TOO_LARGE = new DefaultFullHttpResponse(
      HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, Unpooled.EMPTY_BUFFER
   );
   private final boolean closeOnExpectationFailed;

   public HttpObjectAggregator(int var1) {
      this(var1, false);
   }

   public HttpObjectAggregator(int var1, boolean var2) {
      super(var1);
      this.closeOnExpectationFailed = var2;
   }

   protected boolean isStartMessage(HttpObject var1) throws Exception {
      return var1 instanceof HttpMessage;
   }

   protected boolean isContentMessage(HttpObject var1) throws Exception {
      return var1 instanceof HttpContent;
   }

   protected boolean isLastContentMessage(HttpContent var1) throws Exception {
      return var1 instanceof LastHttpContent;
   }

   protected boolean isAggregated(HttpObject var1) throws Exception {
      return var1 instanceof FullHttpMessage;
   }

   protected boolean isContentLengthInvalid(HttpMessage var1, int var2) {
      try {
         return HttpUtil.getContentLength(var1, -1L) > (long)var2;
      } catch (NumberFormatException var4) {
         return false;
      }
   }

   private static Object continueResponse(HttpMessage var0, int var1, ChannelPipeline var2) {
      if (HttpUtil.isUnsupportedExpectation(var0)) {
         var2.fireUserEventTriggered(HttpExpectationFailedEvent.INSTANCE);
         return EXPECTATION_FAILED.retainedDuplicate();
      } else if (HttpUtil.is100ContinueExpected(var0)) {
         if (HttpUtil.getContentLength(var0, -1L) <= (long)var1) {
            return CONTINUE.retainedDuplicate();
         } else {
            var2.fireUserEventTriggered(HttpExpectationFailedEvent.INSTANCE);
            return TOO_LARGE.retainedDuplicate();
         }
      } else {
         return null;
      }
   }

   protected Object newContinueResponse(HttpMessage var1, int var2, ChannelPipeline var3) {
      Object var4 = continueResponse(var1, var2, var3);
      if (var4 != null) {
         var1.headers().remove(HttpHeaderNames.EXPECT);
      }

      return var4;
   }

   @Override
   protected boolean closeAfterContinueResponse(Object var1) {
      return this.closeOnExpectationFailed && this.ignoreContentAfterContinueResponse(var1);
   }

   @Override
   protected boolean ignoreContentAfterContinueResponse(Object var1) {
      if (var1 instanceof HttpResponse) {
         HttpResponse var2 = (HttpResponse)var1;
         return var2.status().codeClass().equals(HttpStatusClass.CLIENT_ERROR);
      } else {
         return false;
      }
   }

   protected FullHttpMessage beginAggregation(HttpMessage var1, ByteBuf var2) throws Exception {
      assert !(var1 instanceof FullHttpMessage);

      HttpUtil.setTransferEncodingChunked(var1, false);
      Object var3;
      if (var1 instanceof HttpRequest) {
         var3 = new HttpObjectAggregator.AggregatedFullHttpRequest((HttpRequest)var1, var2, null);
      } else {
         if (!(var1 instanceof HttpResponse)) {
            throw new Error();
         }

         var3 = new HttpObjectAggregator.AggregatedFullHttpResponse((HttpResponse)var1, var2, null);
      }

      return (FullHttpMessage)var3;
   }

   protected void aggregate(FullHttpMessage var1, HttpContent var2) throws Exception {
      if (var2 instanceof LastHttpContent) {
         ((HttpObjectAggregator.AggregatedFullHttpMessage)var1).setTrailingHeaders(((LastHttpContent)var2).trailingHeaders());
      }
   }

   protected void finishAggregation(FullHttpMessage var1) throws Exception {
      if (!HttpUtil.isContentLengthSet(var1)) {
         var1.headers().set(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(var1.content().readableBytes()));
      }
   }

   protected void handleOversizedMessage(final ChannelHandlerContext var1, HttpMessage var2) throws Exception {
      if (!(var2 instanceof HttpRequest)) {
         if (var2 instanceof HttpResponse) {
            var1.close();
            throw new TooLongHttpContentException("Response entity too large: " + var2);
         } else {
            throw new IllegalStateException();
         }
      } else {
         if (!(var2 instanceof FullHttpMessage) && (HttpUtil.is100ContinueExpected(var2) || HttpUtil.isKeepAlive(var2))) {
            var1.writeAndFlush(TOO_LARGE.retainedDuplicate()).addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1x) throws Exception {
                  if (!var1x.isSuccess()) {
                     HttpObjectAggregator.logger.debug("Failed to send a 413 Request Entity Too Large.", var1x.cause());
                     var1.close();
                  }
               }
            });
         } else {
            ChannelFuture var3 = var1.writeAndFlush(TOO_LARGE_CLOSE.retainedDuplicate());
            var3.addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1x) throws Exception {
                  if (!var1x.isSuccess()) {
                     HttpObjectAggregator.logger.debug("Failed to send a 413 Request Entity Too Large.", var1x.cause());
                  }

                  var1.close();
               }
            });
         }
      }
   }

   static {
      EXPECTATION_FAILED.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
      TOO_LARGE.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
      TOO_LARGE_CLOSE.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
      TOO_LARGE_CLOSE.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
   }

   private abstract static class AggregatedFullHttpMessage implements FullHttpMessage {
      protected final HttpMessage message;
      private final ByteBuf content;
      private HttpHeaders trailingHeaders;

      AggregatedFullHttpMessage(HttpMessage var1, ByteBuf var2, HttpHeaders var3) {
         this.message = var1;
         this.content = var2;
         this.trailingHeaders = var3;
      }

      @Override
      public HttpHeaders trailingHeaders() {
         HttpHeaders var1 = this.trailingHeaders;
         return (HttpHeaders)(var1 == null ? EmptyHttpHeaders.INSTANCE : var1);
      }

      void setTrailingHeaders(HttpHeaders var1) {
         this.trailingHeaders = var1;
      }

      @Override
      public HttpVersion getProtocolVersion() {
         return this.message.protocolVersion();
      }

      @Override
      public HttpVersion protocolVersion() {
         return this.message.protocolVersion();
      }

      public FullHttpMessage setProtocolVersion(HttpVersion var1) {
         this.message.setProtocolVersion(var1);
         return this;
      }

      @Override
      public HttpHeaders headers() {
         return this.message.headers();
      }

      @Override
      public DecoderResult decoderResult() {
         return this.message.decoderResult();
      }

      @Override
      public DecoderResult getDecoderResult() {
         return this.message.decoderResult();
      }

      @Override
      public void setDecoderResult(DecoderResult var1) {
         this.message.setDecoderResult(var1);
      }

      @Override
      public ByteBuf content() {
         return this.content;
      }

      @Override
      public int refCnt() {
         return this.content.refCnt();
      }

      @Override
      public FullHttpMessage retain() {
         this.content.retain();
         return this;
      }

      @Override
      public FullHttpMessage retain(int var1) {
         this.content.retain(var1);
         return this;
      }

      @Override
      public FullHttpMessage touch(Object var1) {
         this.content.touch(var1);
         return this;
      }

      @Override
      public FullHttpMessage touch() {
         this.content.touch();
         return this;
      }

      @Override
      public boolean release() {
         return this.content.release();
      }

      @Override
      public boolean release(int var1) {
         return this.content.release(var1);
      }

      @Override
      public abstract FullHttpMessage copy();

      @Override
      public abstract FullHttpMessage duplicate();

      @Override
      public abstract FullHttpMessage retainedDuplicate();
   }

   private static final class AggregatedFullHttpRequest extends HttpObjectAggregator.AggregatedFullHttpMessage implements FullHttpRequest {
      AggregatedFullHttpRequest(HttpRequest var1, ByteBuf var2, HttpHeaders var3) {
         super(var1, var2, var3);
      }

      @Override
      public FullHttpRequest copy() {
         return this.replace(this.content().copy());
      }

      @Override
      public FullHttpRequest duplicate() {
         return this.replace(this.content().duplicate());
      }

      @Override
      public FullHttpRequest retainedDuplicate() {
         return this.replace(this.content().retainedDuplicate());
      }

      @Override
      public FullHttpRequest replace(ByteBuf var1) {
         DefaultFullHttpRequest var2 = new DefaultFullHttpRequest(
            this.protocolVersion(), this.method(), this.uri(), var1, this.headers().copy(), this.trailingHeaders().copy()
         );
         var2.setDecoderResult(this.decoderResult());
         return var2;
      }

      @Override
      public FullHttpRequest retain(int var1) {
         super.retain(var1);
         return this;
      }

      @Override
      public FullHttpRequest retain() {
         super.retain();
         return this;
      }

      @Override
      public FullHttpRequest touch() {
         super.touch();
         return this;
      }

      @Override
      public FullHttpRequest touch(Object var1) {
         super.touch(var1);
         return this;
      }

      @Override
      public FullHttpRequest setMethod(HttpMethod var1) {
         ((HttpRequest)this.message).setMethod(var1);
         return this;
      }

      @Override
      public FullHttpRequest setUri(String var1) {
         ((HttpRequest)this.message).setUri(var1);
         return this;
      }

      @Override
      public HttpMethod getMethod() {
         return ((HttpRequest)this.message).method();
      }

      @Override
      public String getUri() {
         return ((HttpRequest)this.message).uri();
      }

      @Override
      public HttpMethod method() {
         return this.getMethod();
      }

      @Override
      public String uri() {
         return this.getUri();
      }

      @Override
      public FullHttpRequest setProtocolVersion(HttpVersion var1) {
         super.setProtocolVersion(var1);
         return this;
      }

      @Override
      public String toString() {
         return HttpMessageUtil.appendFullRequest(new StringBuilder(256), this).toString();
      }
   }

   private static final class AggregatedFullHttpResponse extends HttpObjectAggregator.AggregatedFullHttpMessage implements FullHttpResponse {
      AggregatedFullHttpResponse(HttpResponse var1, ByteBuf var2, HttpHeaders var3) {
         super(var1, var2, var3);
      }

      @Override
      public FullHttpResponse copy() {
         return this.replace(this.content().copy());
      }

      @Override
      public FullHttpResponse duplicate() {
         return this.replace(this.content().duplicate());
      }

      @Override
      public FullHttpResponse retainedDuplicate() {
         return this.replace(this.content().retainedDuplicate());
      }

      @Override
      public FullHttpResponse replace(ByteBuf var1) {
         DefaultFullHttpResponse var2 = new DefaultFullHttpResponse(
            this.getProtocolVersion(), this.getStatus(), var1, this.headers().copy(), this.trailingHeaders().copy()
         );
         var2.setDecoderResult(this.decoderResult());
         return var2;
      }

      @Override
      public FullHttpResponse setStatus(HttpResponseStatus var1) {
         ((HttpResponse)this.message).setStatus(var1);
         return this;
      }

      @Override
      public HttpResponseStatus getStatus() {
         return ((HttpResponse)this.message).status();
      }

      @Override
      public HttpResponseStatus status() {
         return this.getStatus();
      }

      @Override
      public FullHttpResponse setProtocolVersion(HttpVersion var1) {
         super.setProtocolVersion(var1);
         return this;
      }

      @Override
      public FullHttpResponse retain(int var1) {
         super.retain(var1);
         return this;
      }

      @Override
      public FullHttpResponse retain() {
         super.retain();
         return this;
      }

      @Override
      public FullHttpResponse touch(Object var1) {
         super.touch(var1);
         return this;
      }

      @Override
      public FullHttpResponse touch() {
         super.touch();
         return this;
      }

      @Override
      public String toString() {
         return HttpMessageUtil.appendFullResponse(new StringBuilder(256), this).toString();
      }
   }
}
