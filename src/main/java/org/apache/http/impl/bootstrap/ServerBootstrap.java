package org.apache.http.impl.bootstrap;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnectionFactory;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.protocol.HttpExpectationVerifier;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerMapper;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;

public class ServerBootstrap {
   private int listenerPort;
   private InetAddress localAddress;
   private SocketConfig socketConfig;
   private ConnectionConfig connectionConfig;
   private LinkedList<HttpRequestInterceptor> requestFirst;
   private LinkedList<HttpRequestInterceptor> requestLast;
   private LinkedList<HttpResponseInterceptor> responseFirst;
   private LinkedList<HttpResponseInterceptor> responseLast;
   private String serverInfo;
   private HttpProcessor httpProcessor;
   private ConnectionReuseStrategy connStrategy;
   private HttpResponseFactory responseFactory;
   private HttpRequestHandlerMapper handlerMapper;
   private Map<String, HttpRequestHandler> handlerMap;
   private HttpExpectationVerifier expectationVerifier;
   private ServerSocketFactory serverSocketFactory;
   private SSLContext sslContext;
   private SSLServerSetupHandler sslSetupHandler;
   private HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory;
   private ExceptionLogger exceptionLogger;

   private ServerBootstrap() {
   }

   public static ServerBootstrap bootstrap() {
      return new ServerBootstrap();
   }

   public final ServerBootstrap setListenerPort(int var1) {
      this.listenerPort = var1;
      return this;
   }

   public final ServerBootstrap setLocalAddress(InetAddress var1) {
      this.localAddress = var1;
      return this;
   }

   public final ServerBootstrap setSocketConfig(SocketConfig var1) {
      this.socketConfig = var1;
      return this;
   }

   public final ServerBootstrap setConnectionConfig(ConnectionConfig var1) {
      this.connectionConfig = var1;
      return this;
   }

   public final ServerBootstrap setHttpProcessor(HttpProcessor var1) {
      this.httpProcessor = var1;
      return this;
   }

   public final ServerBootstrap addInterceptorFirst(HttpResponseInterceptor var1) {
      if (var1 == null) {
         return this;
      } else {
         if (this.responseFirst == null) {
            this.responseFirst = new LinkedList<>();
         }

         this.responseFirst.addFirst(var1);
         return this;
      }
   }

   public final ServerBootstrap addInterceptorLast(HttpResponseInterceptor var1) {
      if (var1 == null) {
         return this;
      } else {
         if (this.responseLast == null) {
            this.responseLast = new LinkedList<>();
         }

         this.responseLast.addLast(var1);
         return this;
      }
   }

   public final ServerBootstrap addInterceptorFirst(HttpRequestInterceptor var1) {
      if (var1 == null) {
         return this;
      } else {
         if (this.requestFirst == null) {
            this.requestFirst = new LinkedList<>();
         }

         this.requestFirst.addFirst(var1);
         return this;
      }
   }

   public final ServerBootstrap addInterceptorLast(HttpRequestInterceptor var1) {
      if (var1 == null) {
         return this;
      } else {
         if (this.requestLast == null) {
            this.requestLast = new LinkedList<>();
         }

         this.requestLast.addLast(var1);
         return this;
      }
   }

   public final ServerBootstrap setServerInfo(String var1) {
      this.serverInfo = var1;
      return this;
   }

   public final ServerBootstrap setConnectionReuseStrategy(ConnectionReuseStrategy var1) {
      this.connStrategy = var1;
      return this;
   }

   public final ServerBootstrap setResponseFactory(HttpResponseFactory var1) {
      this.responseFactory = var1;
      return this;
   }

   public final ServerBootstrap setHandlerMapper(HttpRequestHandlerMapper var1) {
      this.handlerMapper = var1;
      return this;
   }

   public final ServerBootstrap registerHandler(String var1, HttpRequestHandler var2) {
      if (var1 != null && var2 != null) {
         if (this.handlerMap == null) {
            this.handlerMap = new HashMap<>();
         }

         this.handlerMap.put(var1, var2);
         return this;
      } else {
         return this;
      }
   }

   public final ServerBootstrap setExpectationVerifier(HttpExpectationVerifier var1) {
      this.expectationVerifier = var1;
      return this;
   }

   public final ServerBootstrap setConnectionFactory(HttpConnectionFactory<? extends DefaultBHttpServerConnection> var1) {
      this.connectionFactory = var1;
      return this;
   }

   public final ServerBootstrap setSslSetupHandler(SSLServerSetupHandler var1) {
      this.sslSetupHandler = var1;
      return this;
   }

   public final ServerBootstrap setServerSocketFactory(ServerSocketFactory var1) {
      this.serverSocketFactory = var1;
      return this;
   }

   public final ServerBootstrap setSslContext(SSLContext var1) {
      this.sslContext = var1;
      return this;
   }

   public final ServerBootstrap setExceptionLogger(ExceptionLogger var1) {
      this.exceptionLogger = var1;
      return this;
   }

   public HttpServer create() {
      HttpProcessor var1 = this.httpProcessor;
      if (var1 == null) {
         HttpProcessorBuilder var2 = HttpProcessorBuilder.create();
         if (this.requestFirst != null) {
            for (HttpRequestInterceptor var4 : this.requestFirst) {
               var2.addFirst(var4);
            }
         }

         if (this.responseFirst != null) {
            for (HttpResponseInterceptor var14 : this.responseFirst) {
               var2.addFirst(var14);
            }
         }

         String var11 = this.serverInfo;
         if (var11 == null) {
            var11 = "Apache-HttpCore/1.1";
         }

         var2.addAll(new ResponseDate(), new ResponseServer(var11), new ResponseContent(), new ResponseConnControl());
         if (this.requestLast != null) {
            for (HttpRequestInterceptor var5 : this.requestLast) {
               var2.addLast(var5);
            }
         }

         if (this.responseLast != null) {
            for (HttpResponseInterceptor var19 : this.responseLast) {
               var2.addLast(var19);
            }
         }

         var1 = var2.build();
      }

      Object var9 = this.handlerMapper;
      if (var9 == null) {
         UriHttpRequestHandlerMapper var12 = new UriHttpRequestHandlerMapper();
         if (this.handlerMap != null) {
            for (Entry var20 : this.handlerMap.entrySet()) {
               var12.register((String)var20.getKey(), (HttpRequestHandler)var20.getValue());
            }
         }

         var9 = var12;
      }

      Object var13 = this.connStrategy;
      if (var13 == null) {
         var13 = DefaultConnectionReuseStrategy.INSTANCE;
      }

      Object var18 = this.responseFactory;
      if (var18 == null) {
         var18 = DefaultHttpResponseFactory.INSTANCE;
      }

      HttpService var21 = new HttpService(
         var1, (ConnectionReuseStrategy)var13, (HttpResponseFactory)var18, (HttpRequestHandlerMapper)var9, this.expectationVerifier
      );
      Object var6 = this.serverSocketFactory;
      if (var6 == null) {
         if (this.sslContext != null) {
            var6 = this.sslContext.getServerSocketFactory();
         } else {
            var6 = ServerSocketFactory.getDefault();
         }
      }

      Object var7 = this.connectionFactory;
      if (var7 == null) {
         if (this.connectionConfig != null) {
            var7 = new DefaultBHttpServerConnectionFactory(this.connectionConfig);
         } else {
            var7 = DefaultBHttpServerConnectionFactory.INSTANCE;
         }
      }

      ExceptionLogger var8 = this.exceptionLogger;
      if (var8 == null) {
         var8 = ExceptionLogger.NO_OP;
      }

      return new HttpServer(
         this.listenerPort > 0 ? this.listenerPort : 0,
         this.localAddress,
         this.socketConfig != null ? this.socketConfig : SocketConfig.DEFAULT,
         (ServerSocketFactory)var6,
         var21,
         (HttpConnectionFactory<? extends DefaultBHttpServerConnection>)var7,
         this.sslSetupHandler,
         var8
      );
   }
}
