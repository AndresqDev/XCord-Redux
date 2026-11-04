package org.apache.http.client.protocol;

import java.io.IOException;
import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.DecompressingEntity;
import org.apache.http.client.entity.DeflateInputStreamFactory;
import org.apache.http.client.entity.GZIPInputStreamFactory;
import org.apache.http.client.entity.InputStreamFactory;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.protocol.HttpContext;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class ResponseContentEncoding implements HttpResponseInterceptor {
   public static final String UNCOMPRESSED = "http.client.response.uncompressed";
   private final Lookup<InputStreamFactory> decoderRegistry;
   private final boolean ignoreUnknown;

   public ResponseContentEncoding(Lookup<InputStreamFactory> var1, boolean var2) {
      this.decoderRegistry = (Lookup<InputStreamFactory>)(var1 != null
         ? var1
         : RegistryBuilder.<InputStreamFactory>create()
            .register("gzip", GZIPInputStreamFactory.getInstance())
            .register("x-gzip", GZIPInputStreamFactory.getInstance())
            .register("deflate", DeflateInputStreamFactory.getInstance())
            .build());
      this.ignoreUnknown = var2;
   }

   public ResponseContentEncoding(boolean var1) {
      this(null, var1);
   }

   public ResponseContentEncoding(Lookup<InputStreamFactory> var1) {
      this(var1, true);
   }

   public ResponseContentEncoding() {
      this(null);
   }

   @Override
   public void process(HttpResponse var1, HttpContext var2) throws HttpException, IOException {
      HttpEntity var3 = var1.getEntity();
      HttpClientContext var4 = HttpClientContext.adapt(var2);
      RequestConfig var5 = var4.getRequestConfig();
      if (var5.isContentCompressionEnabled() && var3 != null && var3.getContentLength() != 0L) {
         Header var6 = var3.getContentEncoding();
         if (var6 != null) {
            HeaderElement[] var7 = var6.getElements();

            for (HeaderElement var11 : var7) {
               String var12 = var11.getName().toLowerCase(Locale.ROOT);
               InputStreamFactory var13 = this.decoderRegistry.lookup(var12);
               if (var13 != null) {
                  var1.setEntity(new DecompressingEntity(var1.getEntity(), var13));
                  var1.removeHeaders("Content-Length");
                  var1.removeHeaders("Content-Encoding");
                  var1.removeHeaders("Content-MD5");
               } else if (!"identity".equals(var12) && !this.ignoreUnknown) {
                  throw new HttpException("Unsupported Content-Encoding: " + var11.getName());
               }
            }
         }
      }
   }
}
