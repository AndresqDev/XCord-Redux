package org.apache.http.impl.execchain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.conn.EofSensorInputStream;
import org.apache.http.conn.EofSensorWatcher;
import org.apache.http.entity.HttpEntityWrapper;

class ResponseEntityProxy extends HttpEntityWrapper implements EofSensorWatcher {
   private final ConnectionHolder connHolder;

   public static void enchance(HttpResponse var0, ConnectionHolder var1) {
      HttpEntity var2 = var0.getEntity();
      if (var2 != null && var2.isStreaming() && var1 != null) {
         var0.setEntity(new ResponseEntityProxy(var2, var1));
      }
   }

   ResponseEntityProxy(HttpEntity var1, ConnectionHolder var2) {
      super(var1);
      this.connHolder = var2;
   }

   private void cleanup() throws IOException {
      if (this.connHolder != null) {
         this.connHolder.close();
      }
   }

   private void abortConnection() {
      if (this.connHolder != null) {
         this.connHolder.abortConnection();
      }
   }

   public void releaseConnection() {
      if (this.connHolder != null) {
         this.connHolder.releaseConnection();
      }
   }

   @Override
   public boolean isRepeatable() {
      return false;
   }

   @Override
   public InputStream getContent() throws IOException {
      return new EofSensorInputStream(this.wrappedEntity.getContent(), this);
   }

   @Override
   public void consumeContent() throws IOException {
      this.releaseConnection();
   }

   @Override
   public void writeTo(OutputStream var1) throws IOException {
      try {
         if (var1 != null) {
            this.wrappedEntity.writeTo(var1);
         }

         this.releaseConnection();
      } catch (IOException var7) {
         this.abortConnection();
         throw var7;
      } catch (RuntimeException var8) {
         this.abortConnection();
         throw var8;
      } finally {
         this.cleanup();
      }
   }

   @Override
   public boolean eofDetected(InputStream var1) throws IOException {
      try {
         if (var1 != null) {
            var1.close();
         }

         this.releaseConnection();
      } catch (IOException var7) {
         this.abortConnection();
         throw var7;
      } catch (RuntimeException var8) {
         this.abortConnection();
         throw var8;
      } finally {
         this.cleanup();
      }

      return false;
   }

   @Override
   public boolean streamClosed(InputStream var1) throws IOException {
      try {
         boolean var2 = this.connHolder != null && !this.connHolder.isReleased();

         try {
            if (var1 != null) {
               var1.close();
            }

            this.releaseConnection();
         } catch (SocketException var9) {
            if (var2) {
               throw var9;
            }
         }
      } catch (IOException var10) {
         this.abortConnection();
         throw var10;
      } catch (RuntimeException var11) {
         this.abortConnection();
         throw var11;
      } finally {
         this.cleanup();
      }

      return false;
   }

   @Override
   public boolean streamAbort(InputStream var1) throws IOException {
      this.cleanup();
      return false;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("ResponseEntityProxy{");
      var1.append(this.wrappedEntity);
      var1.append('}');
      return var1.toString();
   }
}
