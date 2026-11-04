package org.apache.logging.log4j.core.config.arbiters;

import javax.script.SimpleBindings;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginNode;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.script.AbstractScript;
import org.apache.logging.log4j.core.script.ScriptRef;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "ScriptArbiter",
   category = "Core",
   elementType = "Arbiter",
   deferChildren = true,
   printObject = true
)
public class ScriptArbiter implements Arbiter {
   private final AbstractScript script;
   private final Configuration configuration;

   private ScriptArbiter(Configuration var1, AbstractScript var2) {
      this.configuration = var1;
      this.script = var2;
      if (!(var2 instanceof ScriptRef)) {
         var1.getScriptManager().addScript(var2);
      }
   }

   @Override
   public boolean isCondition() {
      SimpleBindings var1 = new SimpleBindings();
      var1.putAll(this.configuration.getProperties());
      var1.put("substitutor", this.configuration.getStrSubstitutor());
      Object var2 = this.configuration.getScriptManager().execute(this.script.getName(), var1);
      return Boolean.parseBoolean(var2.toString());
   }

   @PluginBuilderFactory
   public static ScriptArbiter.Builder newBuilder() {
      return new ScriptArbiter.Builder();
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<ScriptArbiter> {
      private static final Logger LOGGER = StatusLogger.getLogger();
      @PluginConfiguration
      private AbstractConfiguration configuration;
      @PluginNode
      private Node node;

      public ScriptArbiter.Builder setConfiguration(AbstractConfiguration var1) {
         this.configuration = var1;
         return this.asBuilder();
      }

      public ScriptArbiter.Builder setNode(Node var1) {
         this.node = var1;
         return this.asBuilder();
      }

      public ScriptArbiter.Builder asBuilder() {
         return this;
      }

      public ScriptArbiter build() {
         AbstractScript var1 = null;

         for (Node var3 : this.node.getChildren()) {
            PluginType var4 = var3.getType();
            if (var4 == null) {
               LOGGER.error("Node {} is missing a Plugintype", var3.getName());
            } else if (AbstractScript.class.isAssignableFrom(var4.getPluginClass())) {
               var1 = (AbstractScript)this.configuration.createPluginObject(var4, var3);
               this.node.getChildren().remove(var3);
               break;
            }
         }

         if (var1 == null) {
            LOGGER.error("A Script, ScriptFile or ScriptRef element must be provided for this ScriptFilter");
            return null;
         } else if (var1 instanceof ScriptRef && this.configuration.getScriptManager().getScript(var1.getName()) == null) {
            LOGGER.error("No script with name {} has been declared.", var1.getName());
            return null;
         } else {
            return new ScriptArbiter(this.configuration, var1);
         }
      }
   }
}
