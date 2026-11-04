package org.yaml.snakeyaml.env;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class EnvScalarConstructor extends Constructor {
   public static final Tag ENV_TAG = new Tag("!ENV");
   public static final Pattern ENV_FORMAT = Pattern.compile("^\\$\\{\\s*((?<name>\\w+)((?<separator>:?(-|\\?))(?<value>\\S+)?)?)\\s*\\}$");

   public EnvScalarConstructor() {
      this.yamlConstructors.put(ENV_TAG, new EnvScalarConstructor.ConstructEnv());
   }

   public String apply(String var1, String var2, String var3, String var4) {
      if (var4 != null && !var4.isEmpty()) {
         return var4;
      } else {
         if (var2 != null) {
            if (var2.equals("?") && var4 == null) {
               throw new MissingEnvironmentVariableException("Missing mandatory variable " + var1 + ": " + var3);
            }

            if (var2.equals(":?")) {
               if (var4 == null) {
                  throw new MissingEnvironmentVariableException("Missing mandatory variable " + var1 + ": " + var3);
               }

               if (var4.isEmpty()) {
                  throw new MissingEnvironmentVariableException("Empty mandatory variable " + var1 + ": " + var3);
               }
            }

            if (var2.startsWith(":")) {
               if (var4 == null || var4.isEmpty()) {
                  return var3;
               }
            } else if (var4 == null) {
               return var3;
            }
         }

         return "";
      }
   }

   public String getEnv(String var1) {
      return System.getenv(var1);
   }

   private class ConstructEnv extends AbstractConstruct {
      private ConstructEnv() {
      }

      @Override
      public Object construct(Node var1) {
         String var2 = EnvScalarConstructor.this.constructScalar((ScalarNode)var1);
         Matcher var3 = EnvScalarConstructor.ENV_FORMAT.matcher(var2);
         var3.matches();
         String var4 = var3.group("name");
         String var5 = var3.group("value");
         String var6 = var3.group("separator");
         return EnvScalarConstructor.this.apply(var4, var6, var5 != null ? var5 : "", EnvScalarConstructor.this.getEnv(var4));
      }
   }
}
