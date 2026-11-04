package org.apache.http.impl;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.io.HttpTransportMetrics;

public class HttpConnectionMetricsImpl implements HttpConnectionMetrics {
   public static final String REQUEST_COUNT = "http.request-count";
   public static final String RESPONSE_COUNT = "http.response-count";
   public static final String SENT_BYTES_COUNT = "http.sent-bytes-count";
   public static final String RECEIVED_BYTES_COUNT = "http.received-bytes-count";
   private final HttpTransportMetrics inTransportMetric;
   private final HttpTransportMetrics outTransportMetric;
   private long requestCount = 0L;
   private long responseCount = 0L;
   private Map<String, Object> metricsCache;

   public HttpConnectionMetricsImpl(HttpTransportMetrics var1, HttpTransportMetrics var2) {
      this.inTransportMetric = var1;
      this.outTransportMetric = var2;
   }

   @Override
   public long getReceivedBytesCount() {
      return this.inTransportMetric != null ? this.inTransportMetric.getBytesTransferred() : -1L;
   }

   @Override
   public long getSentBytesCount() {
      return this.outTransportMetric != null ? this.outTransportMetric.getBytesTransferred() : -1L;
   }

   @Override
   public long getRequestCount() {
      return this.requestCount;
   }

   public void incrementRequestCount() {
      this.requestCount++;
   }

   @Override
   public long getResponseCount() {
      return this.responseCount;
   }

   public void incrementResponseCount() {
      this.responseCount++;
   }

   @Override
   public Object getMetric(String var1) {
      Object var2 = null;
      if (this.metricsCache != null) {
         var2 = this.metricsCache.get(var1);
      }

      if (var2 == null) {
         if ("http.request-count".equals(var1)) {
            var2 = this.requestCount;
         } else if ("http.response-count".equals(var1)) {
            var2 = this.responseCount;
         } else {
            if ("http.received-bytes-count".equals(var1)) {
               return this.inTransportMetric != null ? this.inTransportMetric.getBytesTransferred() : null;
            }

            if ("http.sent-bytes-count".equals(var1)) {
               return this.outTransportMetric != null ? this.outTransportMetric.getBytesTransferred() : null;
            }
         }
      }

      return var2;
   }

   public void setMetric(String var1, Object var2) {
      if (this.metricsCache == null) {
         this.metricsCache = new HashMap<>();
      }

      this.metricsCache.put(var1, var2);
   }

   @Override
   public void reset() {
      if (this.outTransportMetric != null) {
         this.outTransportMetric.reset();
      }

      if (this.inTransportMetric != null) {
         this.inTransportMetric.reset();
      }

      this.requestCount = 0L;
      this.responseCount = 0L;
      this.metricsCache = null;
   }
}
