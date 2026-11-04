package io.netty.handler.ssl;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.ReflectionUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Field;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

public abstract class SslMasterKeyHandler extends ChannelInboundHandlerAdapter {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SslMasterKeyHandler.class);
   private static final Class<?> SSL_SESSIONIMPL_CLASS;
   private static final Field SSL_SESSIONIMPL_MASTER_SECRET_FIELD;
   public static final String SYSTEM_PROP_KEY = "io.netty.ssl.masterKeyHandler";
   private static final Throwable UNAVAILABILITY_CAUSE;

   protected SslMasterKeyHandler() {
   }

   public static void ensureSunSslEngineAvailability() {
      if (UNAVAILABILITY_CAUSE != null) {
         throw new IllegalStateException("Failed to find SSLSessionImpl on classpath", UNAVAILABILITY_CAUSE);
      }
   }

   public static Throwable sunSslEngineUnavailabilityCause() {
      return UNAVAILABILITY_CAUSE;
   }

   public static boolean isSunSslEngineAvailable() {
      return UNAVAILABILITY_CAUSE == null;
   }

   protected abstract void accept(SecretKey var1, SSLSession var2);

   @Override
   public final void userEventTriggered(ChannelHandlerContext var1, Object var2) {
      if (var2 == SslHandshakeCompletionEvent.SUCCESS && this.masterKeyHandlerEnabled()) {
         SslHandler var3 = var1.pipeline().get(SslHandler.class);
         SSLEngine var4 = var3.engine();
         SSLSession var5 = var4.getSession();
         if (isSunSslEngineAvailable() && var5.getClass().equals(SSL_SESSIONIMPL_CLASS)) {
            SecretKey var9;
            try {
               var9 = (SecretKey)SSL_SESSIONIMPL_MASTER_SECRET_FIELD.get(var5);
            } catch (IllegalAccessException var8) {
               throw new IllegalArgumentException("Failed to access the field 'masterSecret' via reflection.", var8);
            }

            this.accept(var9, var5);
         } else if (OpenSsl.isAvailable() && var4 instanceof ReferenceCountedOpenSslEngine) {
            SecretKeySpec var6 = ((ReferenceCountedOpenSslEngine)var4).masterKey();
            this.accept(var6, var5);
         }
      }

      var1.fireUserEventTriggered(var2);
   }

   protected boolean masterKeyHandlerEnabled() {
      return SystemPropertyUtil.getBoolean("io.netty.ssl.masterKeyHandler", false);
   }

   public static SslMasterKeyHandler newWireSharkSslMasterKeyHandler() {
      return new SslMasterKeyHandler.WiresharkSslMasterKeyHandler();
   }

   static {
      Class var1 = null;
      Field var2 = null;

      Throwable var0;
      try {
         var1 = Class.forName("sun.security.ssl.SSLSessionImpl");
         var2 = var1.getDeclaredField("masterSecret");
         var0 = ReflectionUtil.trySetAccessible(var2, true);
      } catch (Throwable var4) {
         var0 = var4;
         if (logger.isTraceEnabled()) {
            logger.debug("sun.security.ssl.SSLSessionImpl is unavailable.", var4);
         } else {
            logger.debug("sun.security.ssl.SSLSessionImpl is unavailable: {}", var4.getMessage());
         }
      }

      UNAVAILABILITY_CAUSE = var0;
      SSL_SESSIONIMPL_CLASS = var1;
      SSL_SESSIONIMPL_MASTER_SECRET_FIELD = var2;
   }

   private static final class WiresharkSslMasterKeyHandler extends SslMasterKeyHandler {
      private static final InternalLogger wireshark_logger = InternalLoggerFactory.getInstance("io.netty.wireshark");

      private WiresharkSslMasterKeyHandler() {
      }

      @Override
      protected void accept(SecretKey var1, SSLSession var2) {
         if (var1.getEncoded().length != 48) {
            throw new IllegalArgumentException("An invalid length master key was provided.");
         } else {
            byte[] var3 = var2.getId();
            wireshark_logger.warn(
               "RSA Session-ID:{} Master-Key:{}", ByteBufUtil.hexDump(var3).toLowerCase(), ByteBufUtil.hexDump(var1.getEncoded()).toLowerCase()
            );
         }
      }
   }
}
