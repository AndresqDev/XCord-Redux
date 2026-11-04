package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.List;

public abstract class SslClientHelloHandler<T> extends ByteToMessageDecoder implements ChannelOutboundHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SslClientHelloHandler.class);
   private boolean handshakeFailed;
   private boolean suppressRead;
   private boolean readPending;
   private ByteBuf handshakeBuffer;

   @Override
   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (!this.suppressRead && !this.handshakeFailed) {
         try {
            int var4 = var2.readerIndex();
            int var5 = var2.readableBytes();
            int var6 = -1;

            while (var5 >= 5) {
               short var7 = var2.getUnsignedByte(var4);
               switch (var7) {
                  case 20:
                  case 21:
                     int var8 = SslUtils.getEncryptedPacketLength(var2, var4);
                     if (var8 == -2) {
                        this.handshakeFailed = true;
                        NotSslRecordException var16 = new NotSslRecordException("not an SSL/TLS record: " + ByteBufUtil.hexDump(var2));
                        var2.skipBytes(var2.readableBytes());
                        var1.fireUserEventTriggered(new SniCompletionEvent(var16));
                        SslUtils.handleHandshakeFailure(var1, var16, true);
                        throw var16;
                     }

                     if (var8 == -1) {
                        return;
                     }

                     this.select(var1, null);
                     return;
                  case 22:
                     short var9 = var2.getUnsignedByte(var4 + 1);
                     if (var9 == 3) {
                        int var10 = var2.getUnsignedShort(var4 + 3) + 5;
                        if (var5 < var10) {
                           return;
                        }

                        if (var10 == 5) {
                           this.select(var1, null);
                           return;
                        }

                        int var11 = var4 + var10;
                        if (var6 == -1) {
                           if (var4 + 4 > var11) {
                              return;
                           }

                           short var12 = var2.getUnsignedByte(var4 + 5);
                           if (var12 != 1) {
                              this.select(var1, null);
                              return;
                           }

                           var6 = var2.getUnsignedMedium(var4 + 5 + 1);
                           var4 += 4;
                           var10 -= 4;
                           if (var6 + 4 + 5 <= var10) {
                              var4 += 5;
                              this.select(var1, var2.retainedSlice(var4, var6));
                              return;
                           }

                           if (this.handshakeBuffer == null) {
                              this.handshakeBuffer = var1.alloc().buffer(var6);
                           } else {
                              this.handshakeBuffer.clear();
                           }
                        }

                        this.handshakeBuffer.writeBytes(var2, var4 + 5, var10 - 5);
                        var4 += var10;
                        var5 -= var10;
                        if (var6 <= this.handshakeBuffer.readableBytes()) {
                           ByteBuf var17 = this.handshakeBuffer.setIndex(0, var6);
                           this.handshakeBuffer = null;
                           this.select(var1, var17);
                           return;
                        }
                        break;
                     }
                  default:
                     this.select(var1, null);
                     return;
               }
            }
         } catch (NotSslRecordException var13) {
            throw var13;
         } catch (Exception var14) {
            if (logger.isDebugEnabled()) {
               logger.debug("Unexpected client hello packet: " + ByteBufUtil.hexDump(var2), (Throwable)var14);
            }

            this.select(var1, null);
         }
      }
   }

   private void releaseHandshakeBuffer() {
      releaseIfNotNull(this.handshakeBuffer);
      this.handshakeBuffer = null;
   }

   private static void releaseIfNotNull(ByteBuf var0) {
      if (var0 != null) {
         var0.release();
      }
   }

   private void select(final ChannelHandlerContext var1, final ByteBuf var2) throws Exception {
      try {
         Future var3 = this.lookup(var1, var2);
         if (var3.isDone()) {
            this.onLookupComplete(var1, var3);
         } else {
            this.suppressRead = true;
            var3.addListener(new FutureListener<T>() {
               @Override
               public void operationComplete(Future<T> var1x) {
                  SslClientHelloHandler.releaseIfNotNull(var2);

                  try {
                     SslClientHelloHandler.this.suppressRead = false;

                     try {
                        SslClientHelloHandler.this.onLookupComplete(var1, var1x);
                     } catch (DecoderException var8) {
                        var1.fireExceptionCaught(var8);
                     } catch (Exception var9) {
                        var1.fireExceptionCaught(new DecoderException(var9));
                     } catch (Throwable var10) {
                        var1.fireExceptionCaught(var10);
                     }
                  } finally {
                     if (SslClientHelloHandler.this.readPending) {
                        SslClientHelloHandler.this.readPending = false;
                        var1.read();
                     }
                  }
               }
            });
            var2 = null;
         }
      } catch (Throwable var8) {
         PlatformDependent.throwException(var8);
      } finally {
         releaseIfNotNull(var2);
      }
   }

   @Override
   protected void handlerRemoved0(ChannelHandlerContext var1) throws Exception {
      this.releaseHandshakeBuffer();
      super.handlerRemoved0(var1);
   }

   protected abstract Future<T> lookup(ChannelHandlerContext var1, ByteBuf var2) throws Exception;

   protected abstract void onLookupComplete(ChannelHandlerContext var1, Future<T> var2) throws Exception;

   @Override
   public void read(ChannelHandlerContext var1) throws Exception {
      if (this.suppressRead) {
         this.readPending = true;
      } else {
         var1.read();
      }
   }

   @Override
   public void bind(ChannelHandlerContext var1, SocketAddress var2, ChannelPromise var3) throws Exception {
      var1.bind(var2, var3);
   }

   @Override
   public void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) throws Exception {
      var1.connect(var2, var3, var4);
   }

   @Override
   public void disconnect(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.disconnect(var2);
   }

   @Override
   public void close(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.close(var2);
   }

   @Override
   public void deregister(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.deregister(var2);
   }

   @Override
   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      var1.write(var2, var3);
   }

   @Override
   public void flush(ChannelHandlerContext var1) throws Exception {
      var1.flush();
   }
}
