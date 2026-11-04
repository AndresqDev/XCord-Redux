package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.module.SimpleModule;

class Log4jJsonModule extends SimpleModule {
   private static final long serialVersionUID = 1L;
   private final boolean encodeThreadContextAsList;
   private final boolean includeStacktrace;
   private final boolean stacktraceAsString;
   private final boolean objectMessageAsJsonObject;

   Log4jJsonModule(boolean var1, boolean var2, boolean var3, boolean var4) {
      super(Log4jJsonModule.class.getName(), new Version(2, 0, 0, null, null, null));
      this.encodeThreadContextAsList = var1;
      this.includeStacktrace = var2;
      this.stacktraceAsString = var3;
      this.objectMessageAsJsonObject = var4;
      new Initializers.SimpleModuleInitializer().initialize(this, var4);
   }

   public void setupModule(SetupContext var1) {
      super.setupModule(var1);
      if (this.encodeThreadContextAsList) {
         new Initializers.SetupContextInitializer().setupModule(var1, this.includeStacktrace, this.stacktraceAsString);
      } else {
         new Initializers.SetupContextJsonInitializer().setupModule(var1, this.includeStacktrace, this.stacktraceAsString);
      }
   }
}
