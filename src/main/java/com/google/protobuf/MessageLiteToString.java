package com.google.protobuf;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

final class MessageLiteToString {
   private static final String LIST_SUFFIX = "List";
   private static final String BUILDER_LIST_SUFFIX = "OrBuilderList";
   private static final String MAP_SUFFIX = "Map";
   private static final String BYTES_SUFFIX = "Bytes";

   static String toString(MessageLite var0, String var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("# ").append(var1);
      reflectivePrintWithIndent(var0, var2, 0);
      return var2.toString();
   }

   private static void reflectivePrintWithIndent(MessageLite var0, StringBuilder var1, int var2) {
      HashMap var3 = new HashMap();
      HashMap var4 = new HashMap();
      TreeSet var5 = new TreeSet();

      for (java.lang.reflect.Method var9 : var0.getClass().getDeclaredMethods()) {
         var4.put(var9.getName(), var9);
         if (var9.getParameterTypes().length == 0) {
            var3.put(var9.getName(), var9);
            if (var9.getName().startsWith("get")) {
               var5.add(var9.getName());
            }
         }
      }

      for (String var17 : var5) {
         String var19 = var17.startsWith("get") ? var17.substring(3) : var17;
         if (var19.endsWith("List") && !var19.endsWith("OrBuilderList") && !var19.equals("List")) {
            String var20 = var19.substring(0, 1).toLowerCase() + var19.substring(1, var19.length() - "List".length());
            java.lang.reflect.Method var10 = (java.lang.reflect.Method)var3.get(var17);
            if (var10 != null && var10.getReturnType().equals(List.class)) {
               printField(var1, var2, camelCaseToSnakeCase(var20), GeneratedMessageLite.invokeOrDie(var10, var0));
               continue;
            }
         }

         if (var19.endsWith("Map") && !var19.equals("Map")) {
            String var21 = var19.substring(0, 1).toLowerCase() + var19.substring(1, var19.length() - "Map".length());
            java.lang.reflect.Method var23 = (java.lang.reflect.Method)var3.get(var17);
            if (var23 != null
               && var23.getReturnType().equals(Map.class)
               && !var23.isAnnotationPresent(Deprecated.class)
               && Modifier.isPublic(var23.getModifiers())) {
               printField(var1, var2, camelCaseToSnakeCase(var21), GeneratedMessageLite.invokeOrDie(var23, var0));
               continue;
            }
         }

         java.lang.reflect.Method var22 = (java.lang.reflect.Method)var4.get("set" + var19);
         if (var22 != null && (!var19.endsWith("Bytes") || !var3.containsKey("get" + var19.substring(0, var19.length() - "Bytes".length())))) {
            String var24 = var19.substring(0, 1).toLowerCase() + var19.substring(1);
            java.lang.reflect.Method var11 = (java.lang.reflect.Method)var3.get("get" + var19);
            java.lang.reflect.Method var12 = (java.lang.reflect.Method)var3.get("has" + var19);
            if (var11 != null) {
               Object var13 = GeneratedMessageLite.invokeOrDie(var11, var0);
               boolean var14 = var12 == null ? !isDefaultValue(var13) : (Boolean)GeneratedMessageLite.invokeOrDie(var12, var0);
               if (var14) {
                  printField(var1, var2, camelCaseToSnakeCase(var24), var13);
               }
            }
         }
      }

      if (var0 instanceof GeneratedMessageLite.ExtendableMessage) {
         for (Entry var18 : ((GeneratedMessageLite.ExtendableMessage)var0).extensions) {
            printField(var1, var2, "[" + ((GeneratedMessageLite.ExtensionDescriptor)var18.getKey()).getNumber() + "]", var18.getValue());
         }
      }

      if (((GeneratedMessageLite)var0).unknownFields != null) {
         ((GeneratedMessageLite)var0).unknownFields.printWithIndent(var1, var2);
      }
   }

   private static boolean isDefaultValue(Object var0) {
      if (var0 instanceof Boolean) {
         return !(Boolean)var0;
      } else if (var0 instanceof Integer) {
         return (Integer)var0 == 0;
      } else if (var0 instanceof Float) {
         return (Float)var0 == 0.0F;
      } else if (var0 instanceof Double) {
         return (Double)var0 == 0.0;
      } else if (var0 instanceof String) {
         return var0.equals("");
      } else if (var0 instanceof ByteString) {
         return var0.equals(ByteString.EMPTY);
      } else if (var0 instanceof MessageLite) {
         return var0 == ((MessageLite)var0).getDefaultInstanceForType();
      } else {
         return var0 instanceof java.lang.Enum ? ((java.lang.Enum)var0).ordinal() == 0 : false;
      }
   }

   static final void printField(StringBuilder var0, int var1, String var2, Object var3) {
      if (var3 instanceof List) {
         for (Object var13 : (List)var3) {
            printField(var0, var1, var2, var13);
         }
      } else if (var3 instanceof Map) {
         Map var9 = (Map)var3;

         for (Entry var6 : var9.entrySet()) {
            printField(var0, var1, var2, var6);
         }
      } else {
         var0.append('\n');

         for (int var4 = 0; var4 < var1; var4++) {
            var0.append(' ');
         }

         var0.append(var2);
         if (var3 instanceof String) {
            var0.append(": \"").append(TextFormatEscaper.escapeText((String)var3)).append('"');
         } else if (var3 instanceof ByteString) {
            var0.append(": \"").append(TextFormatEscaper.escapeBytes((ByteString)var3)).append('"');
         } else if (var3 instanceof GeneratedMessageLite) {
            var0.append(" {");
            reflectivePrintWithIndent((GeneratedMessageLite)var3, var0, var1 + 2);
            var0.append("\n");

            for (int var7 = 0; var7 < var1; var7++) {
               var0.append(' ');
            }

            var0.append("}");
         } else if (var3 instanceof Entry) {
            var0.append(" {");
            Entry var8 = (Entry)var3;
            printField(var0, var1 + 2, "key", var8.getKey());
            printField(var0, var1 + 2, "value", var8.getValue());
            var0.append("\n");

            for (int var5 = 0; var5 < var1; var5++) {
               var0.append(' ');
            }

            var0.append("}");
         } else {
            var0.append(": ").append(var3.toString());
         }
      }
   }

   private static final String camelCaseToSnakeCase(String var0) {
      StringBuilder var1 = new StringBuilder();

      for (int var2 = 0; var2 < var0.length(); var2++) {
         char var3 = var0.charAt(var2);
         if (Character.isUpperCase(var3)) {
            var1.append("_");
         }

         var1.append(Character.toLowerCase(var3));
      }

      return var1.toString();
   }
}
