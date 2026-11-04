package org.apache.logging.log4j.core.script;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.util.FileWatcher;
import org.apache.logging.log4j.core.util.WatchManager;
import org.apache.logging.log4j.status.StatusLogger;

public class ScriptManager implements Serializable, FileWatcher {
   private static final long serialVersionUID = -2534169384971965196L;
   private static final String KEY_THREADING = "THREADING";
   private static final Logger logger = StatusLogger.getLogger();
   private final Configuration configuration;
   private final ScriptEngineManager manager = new ScriptEngineManager();
   private final ConcurrentMap<String, ScriptManager.ScriptRunner> scriptRunners = new ConcurrentHashMap<>();
   private final String languages;
   private final WatchManager watchManager;

   public ScriptManager(Configuration var1, WatchManager var2) {
      this.configuration = var1;
      this.watchManager = var2;
      List var3 = this.manager.getEngineFactories();
      if (logger.isDebugEnabled()) {
         StringBuilder var4 = new StringBuilder();
         int var5 = var3.size();
         logger.debug("Installed {} script engine{}", var5, var5 != 1 ? "s" : "");

         for (ScriptEngineFactory var7 : var3) {
            String var8 = Objects.toString(var7.getParameter("THREADING"), null);
            if (var8 == null) {
               var8 = "Not Thread Safe";
            }

            StringBuilder var9 = new StringBuilder();
            List var10 = var7.getNames();

            for (String var12 : var10) {
               if (var9.length() > 0) {
                  var9.append(", ");
               }

               var9.append(var12);
            }

            if (var4.length() > 0) {
               var4.append(", ");
            }

            var4.append((CharSequence)var9);
            boolean var18 = var7.getScriptEngine() instanceof Compilable;
            logger.debug(
               "{} version: {}, language: {}, threading: {}, compile: {}, names: {}, factory class: {}",
               var7.getEngineName(),
               var7.getEngineVersion(),
               var7.getLanguageName(),
               var8,
               var18,
               var10,
               var7.getClass().getName()
            );
         }

         this.languages = var4.toString();
      } else {
         StringBuilder var13 = new StringBuilder();

         for (ScriptEngineFactory var15 : var3) {
            for (String var17 : var15.getNames()) {
               if (var13.length() > 0) {
                  var13.append(", ");
               }

               var13.append(var17);
            }
         }

         this.languages = var13.toString();
      }
   }

   public void addScript(AbstractScript var1) {
      ScriptEngine var2 = this.manager.getEngineByName(var1.getLanguage());
      if (var2 == null) {
         logger.error("No ScriptEngine found for language " + var1.getLanguage() + ". Available languages are: " + this.languages);
      } else {
         if (var2.getFactory().getParameter("THREADING") == null) {
            this.scriptRunners.put(var1.getName(), new ScriptManager.ThreadLocalScriptRunner(var1));
         } else {
            this.scriptRunners.put(var1.getName(), new ScriptManager.MainScriptRunner(var2, var1));
         }

         if (var1 instanceof ScriptFile) {
            ScriptFile var3 = (ScriptFile)var1;
            Path var4 = var3.getPath();
            if (var3.isWatched() && var4 != null) {
               this.watchManager.watchFile(var4.toFile(), this);
            }
         }
      }
   }

   public Bindings createBindings(AbstractScript var1) {
      return this.getScriptRunner(var1).createBindings();
   }

   public AbstractScript getScript(String var1) {
      ScriptManager.ScriptRunner var2 = this.scriptRunners.get(var1);
      return var2 != null ? var2.getScript() : null;
   }

   @Override
   public void fileModified(File var1) {
      ScriptManager.ScriptRunner var2 = this.scriptRunners.get(var1.toString());
      if (var2 == null) {
         logger.info("{} is not a running script", var1.getName());
      } else {
         ScriptEngine var3 = var2.getScriptEngine();
         AbstractScript var4 = var2.getScript();
         if (var3.getFactory().getParameter("THREADING") == null) {
            this.scriptRunners.put(var4.getName(), new ScriptManager.ThreadLocalScriptRunner(var4));
         } else {
            this.scriptRunners.put(var4.getName(), new ScriptManager.MainScriptRunner(var3, var4));
         }
      }
   }

   public Object execute(String var1, Bindings var2) {
      ScriptManager.ScriptRunner var3 = this.scriptRunners.get(var1);
      if (var3 == null) {
         logger.warn("No script named {} could be found", var1);
         return null;
      } else {
         return AccessController.doPrivileged((PrivilegedAction)(() -> var3.execute(var2)));
      }
   }

   private ScriptManager.ScriptRunner getScriptRunner(AbstractScript var1) {
      return this.scriptRunners.get(var1.getName());
   }

   private abstract class AbstractScriptRunner implements ScriptManager.ScriptRunner {
      private static final String KEY_STATUS_LOGGER = "statusLogger";
      private static final String KEY_CONFIGURATION = "configuration";

      private AbstractScriptRunner() {
      }

      @Override
      public Bindings createBindings() {
         SimpleBindings var1 = new SimpleBindings();
         var1.put("configuration", ScriptManager.this.configuration);
         var1.put("statusLogger", ScriptManager.logger);
         return var1;
      }
   }

   private class MainScriptRunner extends ScriptManager.AbstractScriptRunner {
      private final AbstractScript script;
      private final CompiledScript compiledScript;
      private final ScriptEngine scriptEngine;

      public MainScriptRunner(ScriptEngine var2, AbstractScript var3) {
         this.script = var3;
         this.scriptEngine = var2;
         CompiledScript var4 = null;
         if (var2 instanceof Compilable) {
            ScriptManager.logger.debug("Script {} is compilable", var3.getName());
            var4 = AccessController.doPrivileged((PrivilegedAction<CompiledScript>)(() -> {
               try {
                  return ((Compilable)var2).compile(var3.getScriptText());
               } catch (Throwable var3x) {
                  ScriptManager.logger.warn("Error compiling script", var3x);
                  return null;
               }
            }));
         }

         this.compiledScript = var4;
      }

      @Override
      public ScriptEngine getScriptEngine() {
         return this.scriptEngine;
      }

      @Override
      public Object execute(Bindings var1) {
         if (this.compiledScript != null) {
            try {
               return this.compiledScript.eval(var1);
            } catch (ScriptException var3) {
               ScriptManager.logger.error("Error running script " + this.script.getName(), (Throwable)var3);
               return null;
            }
         } else {
            try {
               return this.scriptEngine.eval(this.script.getScriptText(), var1);
            } catch (ScriptException var4) {
               ScriptManager.logger.error("Error running script " + this.script.getName(), (Throwable)var4);
               return null;
            }
         }
      }

      @Override
      public AbstractScript getScript() {
         return this.script;
      }
   }

   private interface ScriptRunner {
      Bindings createBindings();

      Object execute(Bindings var1);

      AbstractScript getScript();

      ScriptEngine getScriptEngine();
   }

   private class ThreadLocalScriptRunner extends ScriptManager.AbstractScriptRunner {
      private final AbstractScript script;
      private final ThreadLocal<ScriptManager.MainScriptRunner> runners = new ThreadLocal<ScriptManager.MainScriptRunner>() {
         protected ScriptManager.MainScriptRunner initialValue() {
            ScriptEngine var1 = ScriptManager.this.manager.getEngineByName(ThreadLocalScriptRunner.this.script.getLanguage());
            return ScriptManager.this.new MainScriptRunner(var1, ThreadLocalScriptRunner.this.script);
         }
      };

      public ThreadLocalScriptRunner(AbstractScript var2) {
         this.script = var2;
      }

      @Override
      public Object execute(Bindings var1) {
         return this.runners.get().execute(var1);
      }

      @Override
      public AbstractScript getScript() {
         return this.script;
      }

      @Override
      public ScriptEngine getScriptEngine() {
         return this.runners.get().getScriptEngine();
      }
   }
}
