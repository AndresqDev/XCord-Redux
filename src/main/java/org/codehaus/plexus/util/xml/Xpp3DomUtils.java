package org.codehaus.plexus.util.xml;

import java.io.IOException;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class Xpp3DomUtils {
   public static final String CHILDREN_COMBINATION_MODE_ATTRIBUTE = "combine.children";
   public static final String CHILDREN_COMBINATION_MERGE = "merge";
   public static final String CHILDREN_COMBINATION_APPEND = "append";
   public static final String DEFAULT_CHILDREN_COMBINATION_MODE = "merge";
   public static final String SELF_COMBINATION_MODE_ATTRIBUTE = "combine.self";
   public static final String SELF_COMBINATION_OVERRIDE = "override";
   public static final String SELF_COMBINATION_MERGE = "merge";
   public static final String ID_COMBINATION_MODE_ATTRIBUTE = "combine.id";
   public static final String DEFAULT_SELF_COMBINATION_MODE = "merge";

   public void writeToSerializer(String var1, XmlSerializer var2, Xpp3Dom var3) throws IOException {
      SerializerXMLWriter var4 = new SerializerXMLWriter(var1, var2);
      Xpp3DomWriter.write(var4, var3);
      if (var4.getExceptions().size() > 0) {
         throw (IOException)var4.getExceptions().get(0);
      }
   }

   private static void mergeIntoXpp3Dom(Xpp3Dom var0, Xpp3Dom var1, Boolean var2) {
      if (var1 != null) {
         boolean var3 = true;
         String var4 = var0.getAttribute("combine.self");
         if (isNotEmpty(var4) && "override".equals(var4)) {
            var3 = false;
         }

         if (var3) {
            if (isEmpty(var0.getValue()) && !isEmpty(var1.getValue())) {
               var0.setValue(var1.getValue());
               var0.setInputLocation(var1.getInputLocation());
            }

            String[] var5 = var1.getAttributeNames();

            for (String var9 : var5) {
               if (isEmpty(var0.getAttribute(var9))) {
                  var0.setAttribute(var9, var1.getAttribute(var9));
               }
            }

            boolean var18 = true;
            if (var2 != null) {
               var18 = var2;
            } else {
               String var19 = var0.getAttribute("combine.children");
               if (isNotEmpty(var19) && "append".equals(var19)) {
                  var18 = false;
               }
            }

            Xpp3Dom[] var20 = var1.getChildren();

            for (Xpp3Dom var11 : var20) {
               String var12 = var11.getAttribute("combine.id");
               Xpp3Dom var13 = null;
               if (isNotEmpty(var12)) {
                  for (Xpp3Dom var17 : var0.getChildren()) {
                     if (var12.equals(var17.getAttribute("combine.id"))) {
                        var13 = var17;
                        var18 = true;
                     }
                  }
               } else {
                  var13 = var0.getChild(var11.getName());
               }

               if (var18 && var13 != null) {
                  mergeIntoXpp3Dom(var13, var11, var2);
               } else {
                  var0.addChild(new Xpp3Dom(var11));
               }
            }
         }
      }
   }

   public static Xpp3Dom mergeXpp3Dom(Xpp3Dom var0, Xpp3Dom var1, Boolean var2) {
      if (var0 != null) {
         mergeIntoXpp3Dom(var0, var1, var2);
         return var0;
      } else {
         return var1;
      }
   }

   public static Xpp3Dom mergeXpp3Dom(Xpp3Dom var0, Xpp3Dom var1) {
      if (var0 != null) {
         mergeIntoXpp3Dom(var0, var1, null);
         return var0;
      } else {
         return var1;
      }
   }

   public static boolean isNotEmpty(String var0) {
      return var0 != null && var0.length() > 0;
   }

   public static boolean isEmpty(String var0) {
      return var0 == null || var0.trim().length() == 0;
   }
}
