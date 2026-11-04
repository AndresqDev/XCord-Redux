package org.apache.logging.log4j.core.appender.routing;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.script.Bindings;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LifeCycle2;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.script.AbstractScript;
import org.apache.logging.log4j.core.script.ScriptManager;
import org.apache.logging.log4j.core.util.Booleans;

@Plugin(
   name = "Routing",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class RoutingAppender extends AbstractAppender {
   public static final String STATIC_VARIABLES_KEY = "staticVariables";
   private static final String DEFAULT_KEY = "ROUTING_APPENDER_DEFAULT";
   private final Routes routes;
   private Route defaultRoute;
   private final Configuration configuration;
   private final ConcurrentMap<String, RoutingAppender.CreatedRouteAppenderControl> createdAppenders = new ConcurrentHashMap<>();
   private final Map<String, AppenderControl> createdAppendersUnmodifiableView = Collections.unmodifiableMap(this.createdAppenders);
   private final ConcurrentMap<String, RoutingAppender.RouteAppenderControl> referencedAppenders = new ConcurrentHashMap<>();
   private final RewritePolicy rewritePolicy;
   private final PurgePolicy purgePolicy;
   private final AbstractScript defaultRouteScript;
   private final ConcurrentMap<Object, Object> scriptStaticVariables = new ConcurrentHashMap<>();

   @PluginBuilderFactory
   public static <B extends RoutingAppender.Builder<B>> B newBuilder() {
      return new RoutingAppender.Builder<B>().asBuilder();
   }

   private RoutingAppender(
      String var1, Filter var2, boolean var3, Routes var4, RewritePolicy var5, Configuration var6, PurgePolicy var7, AbstractScript var8, Property[] var9
   ) {
      super(var1, var2, null, var3, var9);
      this.routes = var4;
      this.configuration = var6;
      this.rewritePolicy = var5;
      this.purgePolicy = var7;
      if (this.purgePolicy != null) {
         this.purgePolicy.initialize(this);
      }

      this.defaultRouteScript = var8;
      Route var10 = null;

      for (Route var14 : var4.getRoutes()) {
         if (var14.getKey() == null) {
            if (var10 == null) {
               var10 = var14;
            } else {
               this.error("Multiple default routes. Route " + var14.toString() + " will be ignored");
            }
         }
      }

      this.defaultRoute = var10;
   }

   @Override
   public void start() {
      if (this.defaultRouteScript != null) {
         if (this.configuration == null) {
            this.error("No Configuration defined for RoutingAppender; required for Script element.");
         } else {
            ScriptManager var1 = this.configuration.getScriptManager();
            var1.addScript(this.defaultRouteScript);
            Bindings var2 = var1.createBindings(this.defaultRouteScript);
            var2.put("staticVariables", this.scriptStaticVariables);
            Object var3 = var1.execute(this.defaultRouteScript.getName(), var2);
            Route var4 = this.routes.getRoute(Objects.toString(var3, null));
            if (var4 != null) {
               this.defaultRoute = var4;
            }
         }
      }

      for (Route var10 : this.routes.getRoutes()) {
         if (var10.getAppenderRef() != null) {
            Appender var5 = this.configuration.getAppender(var10.getAppenderRef());
            if (var5 != null) {
               String var6 = var10 == this.defaultRoute ? "ROUTING_APPENDER_DEFAULT" : var10.getKey();
               this.referencedAppenders.put(var6, new RoutingAppender.ReferencedRouteAppenderControl(var5));
            } else {
               this.error("Appender " + var10.getAppenderRef() + " cannot be located. Route ignored");
            }
         }
      }

      super.start();
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      super.stop(var1, var3, false);

      for (Entry var5 : this.createdAppenders.entrySet()) {
         Appender var6 = ((RoutingAppender.CreatedRouteAppenderControl)var5.getValue()).getAppender();
         if (var6 instanceof LifeCycle2) {
            ((LifeCycle2)var6).stop(var1, var3);
         } else {
            var6.stop();
         }
      }

      this.setStopped();
      return true;
   }

   @Override
   public void append(LogEvent var1) {
      if (this.rewritePolicy != null) {
         var1 = this.rewritePolicy.rewrite(var1);
      }

      String var2 = this.routes.getPattern(var1, this.scriptStaticVariables);
      String var3 = var2 != null
         ? this.configuration.getStrSubstitutor().replace(var1, var2)
         : (this.defaultRoute.getKey() != null ? this.defaultRoute.getKey() : "ROUTING_APPENDER_DEFAULT");
      RoutingAppender.RouteAppenderControl var4 = this.getControl(var3, var1);
      if (var4 != null) {
         try {
            var4.callAppender(var1);
         } finally {
            var4.release();
         }
      }

      this.updatePurgePolicy(var3, var1);
   }

   private void updatePurgePolicy(String var1, LogEvent var2) {
      if (this.purgePolicy != null && !this.referencedAppenders.containsKey(var1)) {
         this.purgePolicy.update(var1, var2);
      }
   }

   private synchronized RoutingAppender.RouteAppenderControl getControl(String var1, LogEvent var2) {
      Object var3 = this.getAppender(var1);
      if (var3 != null) {
         ((RoutingAppender.RouteAppenderControl)var3).checkout();
         return (RoutingAppender.RouteAppenderControl)var3;
      } else {
         Route var4 = null;

         for (Route var8 : this.routes.getRoutes()) {
            if (var8.getAppenderRef() == null && var1.equals(var8.getKey())) {
               var4 = var8;
               break;
            }
         }

         if (var4 == null) {
            var4 = this.defaultRoute;
            var3 = this.getAppender("ROUTING_APPENDER_DEFAULT");
            if (var3 != null) {
               ((RoutingAppender.RouteAppenderControl)var3).checkout();
               return (RoutingAppender.RouteAppenderControl)var3;
            }
         }

         if (var4 != null) {
            Appender var9 = this.createAppender(var4, var2);
            if (var9 == null) {
               return null;
            }

            RoutingAppender.CreatedRouteAppenderControl var10 = new RoutingAppender.CreatedRouteAppenderControl(var9);
            var3 = var10;
            this.createdAppenders.put(var1, var10);
         }

         if (var3 != null) {
            ((RoutingAppender.RouteAppenderControl)var3).checkout();
         }

         return (RoutingAppender.RouteAppenderControl)var3;
      }
   }

   private RoutingAppender.RouteAppenderControl getAppender(String var1) {
      RoutingAppender.RouteAppenderControl var2 = this.referencedAppenders.get(var1);
      return var2 == null ? this.createdAppenders.get(var1) : var2;
   }

   private Appender createAppender(Route var1, LogEvent var2) {
      Node var3 = var1.getNode();

      for (Node var5 : var3.getChildren()) {
         if (var5.getType().getElementName().equals("appender")) {
            Node var6 = new Node(var5);
            this.configuration.createConfiguration(var6, var2);
            if (var6.getObject() instanceof Appender) {
               Appender var7 = var6.getObject();
               var7.start();
               return var7;
            }

            this.error("Unable to create Appender of type " + var5.getName());
            return null;
         }
      }

      this.error("No Appender was configured for route " + var1.getKey());
      return null;
   }

   public Map<String, AppenderControl> getAppenders() {
      return this.createdAppendersUnmodifiableView;
   }

   public void deleteAppender(String var1) {
      LOGGER.debug("Deleting route with {} key ", var1);
      RoutingAppender.CreatedRouteAppenderControl var2 = this.createdAppenders.remove(var1);
      if (null != var2) {
         LOGGER.debug("Stopping route with {} key", var1);
         synchronized (this) {
            var2.pendingDeletion = true;
         }

         var2.tryStopAppender();
      } else if (this.referencedAppenders.containsKey(var1)) {
         LOGGER.debug("Route {} using an appender reference may not be removed because the appender may be used outside of the RoutingAppender", var1);
      } else {
         LOGGER.debug("Route with {} key already deleted", var1);
      }
   }

   @Deprecated
   public static RoutingAppender createAppender(String var0, String var1, Routes var2, Configuration var3, RewritePolicy var4, PurgePolicy var5, Filter var6) {
      boolean var7 = Booleans.parseBoolean(var1, true);
      if (var0 == null) {
         LOGGER.error("No name provided for RoutingAppender");
         return null;
      } else if (var2 == null) {
         LOGGER.error("No routes defined for RoutingAppender");
         return null;
      } else {
         return new RoutingAppender(var0, var6, var7, var2, var4, var3, var5, null, null);
      }
   }

   public Route getDefaultRoute() {
      return this.defaultRoute;
   }

   public AbstractScript getDefaultRouteScript() {
      return this.defaultRouteScript;
   }

   public PurgePolicy getPurgePolicy() {
      return this.purgePolicy;
   }

   public RewritePolicy getRewritePolicy() {
      return this.rewritePolicy;
   }

   public Routes getRoutes() {
      return this.routes;
   }

   public Configuration getConfiguration() {
      return this.configuration;
   }

   public ConcurrentMap<Object, Object> getScriptStaticVariables() {
      return this.scriptStaticVariables;
   }

   public static class Builder<B extends RoutingAppender.Builder<B>>
      extends AbstractAppender.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<RoutingAppender> {
      @PluginElement("Script")
      private AbstractScript defaultRouteScript;
      @PluginElement("Routes")
      private Routes routes;
      @PluginElement("RewritePolicy")
      private RewritePolicy rewritePolicy;
      @PluginElement("PurgePolicy")
      private PurgePolicy purgePolicy;

      public RoutingAppender build() {
         String var1 = this.getName();
         if (var1 == null) {
            RoutingAppender.LOGGER.error("No name defined for this RoutingAppender");
            return null;
         } else if (this.routes == null) {
            RoutingAppender.LOGGER.error("No routes defined for RoutingAppender {}", var1);
            return null;
         } else {
            return new RoutingAppender(
               var1,
               this.getFilter(),
               this.isIgnoreExceptions(),
               this.routes,
               this.rewritePolicy,
               this.getConfiguration(),
               this.purgePolicy,
               this.defaultRouteScript,
               this.getPropertyArray()
            );
         }
      }

      public Routes getRoutes() {
         return this.routes;
      }

      public AbstractScript getDefaultRouteScript() {
         return this.defaultRouteScript;
      }

      public RewritePolicy getRewritePolicy() {
         return this.rewritePolicy;
      }

      public PurgePolicy getPurgePolicy() {
         return this.purgePolicy;
      }

      public B withRoutes(Routes var1) {
         this.routes = var1;
         return this.asBuilder();
      }

      public B withDefaultRouteScript(AbstractScript var1) {
         this.defaultRouteScript = var1;
         return this.asBuilder();
      }

      public B withRewritePolicy(RewritePolicy var1) {
         this.rewritePolicy = var1;
         return this.asBuilder();
      }

      public void withPurgePolicy(PurgePolicy var1) {
         this.purgePolicy = var1;
      }
   }

   private static final class CreatedRouteAppenderControl extends RoutingAppender.RouteAppenderControl {
      private volatile boolean pendingDeletion;
      private final AtomicInteger depth = new AtomicInteger();

      CreatedRouteAppenderControl(Appender var1) {
         super(var1);
      }

      @Override
      void checkout() {
         if (this.pendingDeletion) {
            LOGGER.warn("CreatedRouteAppenderControl.checkout invoked on a RouteAppenderControl that is pending deletion");
         }

         this.depth.incrementAndGet();
      }

      @Override
      void release() {
         this.depth.decrementAndGet();
         this.tryStopAppender();
      }

      void tryStopAppender() {
         if (this.pendingDeletion && this.depth.compareAndSet(0, -100000)) {
            Appender var1 = this.getAppender();
            LOGGER.debug("Stopping appender {}", var1);
            var1.stop();
         }
      }
   }

   private static final class ReferencedRouteAppenderControl extends RoutingAppender.RouteAppenderControl {
      ReferencedRouteAppenderControl(Appender var1) {
         super(var1);
      }

      @Override
      void checkout() {
      }

      @Override
      void release() {
      }
   }

   private abstract static class RouteAppenderControl extends AppenderControl {
      RouteAppenderControl(Appender var1) {
         super(var1, null, null);
      }

      abstract void checkout();

      abstract void release();
   }
}
