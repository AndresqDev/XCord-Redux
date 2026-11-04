package org.apache.logging.log4j.core.config.json;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
import org.apache.logging.log4j.core.config.status.StatusConfiguration;
import org.apache.logging.log4j.core.util.Patterns;

public class JsonConfiguration extends AbstractConfiguration implements Reconfigurable {
   private static final String[] VERBOSE_CLASSES = new String[]{ResolverUtil.class.getName()};
   private final List<JsonConfiguration.Status> status = new ArrayList<>();
   private JsonNode root;

   public JsonConfiguration(LoggerContext var1, ConfigurationSource var2) {
      super(var1, var2);
      File var3 = var2.getFile();

      try (InputStream var5 = var2.getInputStream()) {
         byte[] var4 = toByteArray(var5);
         ByteArrayInputStream var21 = new ByteArrayInputStream(var4);
         this.root = this.getObjectMapper().readTree(var21);
         if (this.root.size() == 1) {
            for (JsonNode var7 : this.root) {
               this.root = var7;
            }
         }

         this.processAttributes(this.rootNode, this.root);
         StatusConfiguration var23 = new StatusConfiguration().withVerboseClasses(VERBOSE_CLASSES).withStatus(this.getDefaultStatus());
         int var24 = 0;

         for (Entry var9 : this.rootNode.getAttributes().entrySet()) {
            String var10 = (String)var9.getKey();
            String var11 = this.getConfigurationStrSubstitutor().replace((String)var9.getValue());
            if ("status".equalsIgnoreCase(var10)) {
               var23.withStatus(var11);
            } else if ("dest".equalsIgnoreCase(var10)) {
               var23.withDestination(var11);
            } else if ("shutdownHook".equalsIgnoreCase(var10)) {
               this.isShutdownHookEnabled = !"disable".equalsIgnoreCase(var11);
            } else if ("shutdownTimeout".equalsIgnoreCase(var10)) {
               this.shutdownTimeoutMillis = Long.parseLong(var11);
            } else if ("verbose".equalsIgnoreCase((String)var9.getKey())) {
               var23.withVerbosity(var11);
            } else if ("packages".equalsIgnoreCase(var10)) {
               this.pluginPackages.addAll(Arrays.asList(var11.split(Patterns.COMMA_SEPARATOR)));
            } else if ("name".equalsIgnoreCase(var10)) {
               this.setName(var11);
            } else if ("monitorInterval".equalsIgnoreCase(var10)) {
               var24 = Integer.parseInt(var11);
            } else if ("advertiser".equalsIgnoreCase(var10)) {
               this.createAdvertiser(var11, var2, var4, "application/json");
            }
         }

         this.initializeWatchers(this, var2, var24);
         var23.initialize();
         if (this.getName() == null) {
            this.setName(var2.getLocation());
         }
      } catch (Exception var20) {
         LOGGER.error("Error parsing " + var2.getLocation(), (Throwable)var20);
      }
   }

   protected ObjectMapper getObjectMapper() {
      return new ObjectMapper().configure(Feature.ALLOW_COMMENTS, true);
   }

   @Override
   public void setup() {
      Iterator var1 = this.root.fields();
      List var2 = this.rootNode.getChildren();

      while (var1.hasNext()) {
         Entry var3 = (Entry)var1.next();
         JsonNode var4 = (JsonNode)var3.getValue();
         if (var4.isObject()) {
            LOGGER.debug("Processing node for object {}", var3.getKey());
            var2.add(this.constructNode((String)var3.getKey(), this.rootNode, var4));
         } else if (var4.isArray()) {
            LOGGER.error("Arrays are not supported at the root configuration.");
         }
      }

      LOGGER.debug("Completed parsing configuration");
      if (this.status.size() > 0) {
         for (JsonConfiguration.Status var6 : this.status) {
            LOGGER.error("Error processing element {}: {}", var6.name, var6.errorType);
         }
      }
   }

   @Override
   public Configuration reconfigure() {
      try {
         ConfigurationSource var1 = this.getConfigurationSource().resetInputStream();
         return var1 == null ? null : new JsonConfiguration(this.getLoggerContext(), var1);
      } catch (IOException var2) {
         LOGGER.error("Cannot locate file {}", this.getConfigurationSource(), var2);
         return null;
      }
   }

   private Node constructNode(String var1, Node var2, JsonNode var3) {
      PluginType var4 = this.pluginManager.getPluginType(var1);
      Node var5 = new Node(var2, var1, var4);
      this.processAttributes(var5, var3);
      Iterator var6 = var3.fields();
      List var7 = var5.getChildren();

      while (var6.hasNext()) {
         Entry var8 = (Entry)var6.next();
         JsonNode var9 = (JsonNode)var8.getValue();
         if (!var9.isArray() && !var9.isObject()) {
            LOGGER.debug("Node {} is of type {}", var8.getKey(), var9.getNodeType());
         } else {
            if (var4 == null) {
               this.status.add(new JsonConfiguration.Status(var1, var9, JsonConfiguration.ErrorType.CLASS_NOT_FOUND));
            }

            if (!var9.isArray()) {
               LOGGER.debug("Processing node for object {}", var8.getKey());
               var7.add(this.constructNode((String)var8.getKey(), var5, var9));
            } else {
               LOGGER.debug("Processing node for array {}", var8.getKey());

               for (int var10 = 0; var10 < var9.size(); var10++) {
                  String var11 = this.getType(var9.get(var10), (String)var8.getKey());
                  PluginType var12 = this.pluginManager.getPluginType(var11);
                  Node var13 = new Node(var5, (String)var8.getKey(), var12);
                  this.processAttributes(var13, var9.get(var10));
                  if (var11.equals(var8.getKey())) {
                     LOGGER.debug("Processing {}[{}]", var8.getKey(), var10);
                  } else {
                     LOGGER.debug("Processing {} {}[{}]", var11, var8.getKey(), var10);
                  }

                  Iterator var14 = var9.get(var10).fields();
                  List var15 = var13.getChildren();

                  while (var14.hasNext()) {
                     Entry var16 = (Entry)var14.next();
                     if (((JsonNode)var16.getValue()).isObject()) {
                        LOGGER.debug("Processing node for object {}", var16.getKey());
                        var15.add(this.constructNode((String)var16.getKey(), var13, (JsonNode)var16.getValue()));
                     } else if (((JsonNode)var16.getValue()).isArray()) {
                        JsonNode var17 = (JsonNode)var16.getValue();
                        String var18 = (String)var16.getKey();
                        LOGGER.debug("Processing array for object {}", var18);

                        for (int var19 = 0; var19 < var17.size(); var19++) {
                           var15.add(this.constructNode(var18, var13, var17.get(var19)));
                        }
                     }
                  }

                  var7.add(var13);
               }
            }
         }
      }

      String var20;
      if (var4 == null) {
         var20 = "null";
      } else {
         var20 = var4.getElementName() + ':' + var4.getPluginClass();
      }

      String var21 = var5.getParent() == null ? "null" : (var5.getParent().getName() == null ? "root" : var5.getParent().getName());
      LOGGER.debug("Returning {} with parent {} of type {}", var5.getName(), var21, var20);
      return var5;
   }

   private String getType(JsonNode var1, String var2) {
      Iterator var3 = var1.fields();

      while (var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if (((String)var4.getKey()).equalsIgnoreCase("type")) {
            JsonNode var5 = (JsonNode)var4.getValue();
            if (var5.isValueNode()) {
               return var5.asText();
            }
         }
      }

      return var2;
   }

   private void processAttributes(Node var1, JsonNode var2) {
      Map var3 = var1.getAttributes();
      Iterator var4 = var2.fields();

      while (var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         if (!((String)var5.getKey()).equalsIgnoreCase("type")) {
            JsonNode var6 = (JsonNode)var5.getValue();
            if (var6.isValueNode()) {
               var3.put(var5.getKey(), var6.asText());
            }
         }
      }
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + "[location=" + this.getConfigurationSource() + "]";
   }

   private static enum ErrorType {
      CLASS_NOT_FOUND;
   }

   private static class Status {
      private final JsonNode node;
      private final String name;
      private final JsonConfiguration.ErrorType errorType;

      public Status(String var1, JsonNode var2, JsonConfiguration.ErrorType var3) {
         this.name = var1;
         this.node = var2;
         this.errorType = var3;
      }

      @Override
      public String toString() {
         return "Status [name=" + this.name + ", errorType=" + this.errorType + ", node=" + this.node + "]";
      }
   }
}
