package org.codehaus.plexus.util.xml;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class Xpp3Dom implements Serializable {
   private static final long serialVersionUID = 2567894443061173996L;
   protected String name;
   protected String value;
   protected Map<String, String> attributes;
   protected final List<Xpp3Dom> childList;
   protected Xpp3Dom parent;
   protected Object inputLocation;
   private static final String[] EMPTY_STRING_ARRAY = new String[0];
   private static final Xpp3Dom[] EMPTY_DOM_ARRAY = new Xpp3Dom[0];
   public static final String CHILDREN_COMBINATION_MODE_ATTRIBUTE = "combine.children";
   public static final String CHILDREN_COMBINATION_MERGE = "merge";
   public static final String CHILDREN_COMBINATION_APPEND = "append";
   public static final String DEFAULT_CHILDREN_COMBINATION_MODE = "merge";
   public static final String SELF_COMBINATION_MODE_ATTRIBUTE = "combine.self";
   public static final String SELF_COMBINATION_OVERRIDE = "override";
   public static final String SELF_COMBINATION_MERGE = "merge";
   public static final String DEFAULT_SELF_COMBINATION_MODE = "merge";

   public Xpp3Dom(String var1) {
      this.name = var1;
      this.childList = new ArrayList<>();
   }

   public Xpp3Dom(String var1, Object var2) {
      this(var1);
      this.inputLocation = var2;
   }

   public Xpp3Dom(Xpp3Dom var1) {
      this(var1, var1.getName());
   }

   public Xpp3Dom(Xpp3Dom var1, String var2) {
      this.name = var2;
      this.inputLocation = var1.inputLocation;
      int var3 = var1.getChildCount();
      this.childList = new ArrayList<>(var3);
      this.setValue(var1.getValue());
      String[] var4 = var1.getAttributeNames();

      for (String var8 : var4) {
         this.setAttribute(var8, var1.getAttribute(var8));
      }

      for (int var9 = 0; var9 < var3; var9++) {
         this.addChild(new Xpp3Dom(var1.getChild(var9)));
      }
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String var1) {
      this.value = var1;
   }

   public String[] getAttributeNames() {
      return null != this.attributes && !this.attributes.isEmpty() ? this.attributes.keySet().toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY;
   }

   public String getAttribute(String var1) {
      return null != this.attributes ? this.attributes.get(var1) : null;
   }

   public void setAttribute(String var1, String var2) {
      if (null == var2) {
         throw new NullPointerException("Attribute value can not be null");
      } else if (null == var1) {
         throw new NullPointerException("Attribute name can not be null");
      } else {
         if (null == this.attributes) {
            this.attributes = new HashMap<>();
         }

         this.attributes.put(var1, var2);
      }
   }

   public Xpp3Dom getChild(int var1) {
      return this.childList.get(var1);
   }

   public Xpp3Dom getChild(String var1) {
      if (var1 != null) {
         ListIterator var2 = this.childList.listIterator(this.childList.size());

         while (var2.hasPrevious()) {
            Xpp3Dom var3 = (Xpp3Dom)var2.previous();
            if (var1.equals(var3.getName())) {
               return var3;
            }
         }
      }

      return null;
   }

   public void addChild(Xpp3Dom var1) {
      var1.setParent(this);
      this.childList.add(var1);
   }

   public Xpp3Dom[] getChildren() {
      return null != this.childList && !this.childList.isEmpty() ? this.childList.toArray(EMPTY_DOM_ARRAY) : EMPTY_DOM_ARRAY;
   }

   public Xpp3Dom[] getChildren(String var1) {
      return this.getChildrenAsList(var1).toArray(EMPTY_DOM_ARRAY);
   }

   private List<Xpp3Dom> getChildrenAsList(String var1) {
      if (null == this.childList) {
         return Collections.emptyList();
      } else {
         ArrayList var2 = null;

         for (Xpp3Dom var4 : this.childList) {
            if (var1.equals(var4.getName())) {
               if (var2 == null) {
                  var2 = new ArrayList();
               }

               var2.add(var4);
            }
         }

         return (List<Xpp3Dom>)(var2 != null ? var2 : Collections.emptyList());
      }
   }

   public int getChildCount() {
      return null == this.childList ? 0 : this.childList.size();
   }

   public void removeChild(int var1) {
      Xpp3Dom var2 = this.getChild(var1);
      this.childList.remove(var1);
      var2.setParent(null);
   }

   public Xpp3Dom getParent() {
      return this.parent;
   }

   public void setParent(Xpp3Dom var1) {
      this.parent = var1;
   }

   public Object getInputLocation() {
      return this.inputLocation;
   }

   public void setInputLocation(Object var1) {
      this.inputLocation = var1;
   }

   public void writeToSerializer(String var1, XmlSerializer var2) throws IOException {
      SerializerXMLWriter var3 = new SerializerXMLWriter(var1, var2);
      Xpp3DomWriter.write(var3, this);
      if (var3.getExceptions().size() > 0) {
         throw (IOException)var3.getExceptions().get(0);
      }
   }

   private static void mergeIntoXpp3Dom(Xpp3Dom var0, Xpp3Dom var1, Boolean var2) {
      if (var1 != null) {
         boolean var3 = true;
         String var4 = var0.getAttribute("combine.self");
         if ("override".equals(var4)) {
            var3 = false;
         }

         if (var3) {
            if (isEmpty(var0.getValue()) && !isEmpty(var1.getValue())) {
               var0.setValue(var1.getValue());
               var0.setInputLocation(var1.getInputLocation());
            }

            if (var1.attributes != null) {
               for (String var6 : var1.attributes.keySet()) {
                  if (isEmpty(var0.getAttribute(var6))) {
                     var0.setAttribute(var6, var1.getAttribute(var6));
                  }
               }
            }

            if (var1.getChildCount() > 0) {
               boolean var12 = true;
               if (var2 != null) {
                  var12 = var2;
               } else {
                  String var13 = var0.getAttribute("combine.children");
                  if ("append".equals(var13)) {
                     var12 = false;
                  }
               }

               if (!var12) {
                  Xpp3Dom[] var14 = var0.getChildren();
                  var0.childList.clear();
                  int var7 = 0;

                  for (int var8 = var1.getChildCount(); var7 < var8; var7++) {
                     Xpp3Dom var9 = var1.getChild(var7);
                     var0.addChild(new Xpp3Dom(var9));
                  }

                  for (Xpp3Dom var10 : var14) {
                     var0.addChild(var10);
                  }
               } else {
                  HashMap var15 = new HashMap();

                  for (Xpp3Dom var20 : var1.childList) {
                     if (!var15.containsKey(var20.name)) {
                        List var23 = var0.getChildrenAsList(var20.name);
                        if (var23.size() > 0) {
                           var15.put(var20.name, var23.iterator());
                        }
                     }
                  }

                  int var18 = 0;

                  for (int var21 = var1.getChildCount(); var18 < var21; var18++) {
                     Xpp3Dom var24 = var1.getChild(var18);
                     Iterator var25 = (Iterator)var15.get(var24.getName());
                     if (var25 == null) {
                        var0.addChild(new Xpp3Dom(var24));
                     } else if (var25.hasNext()) {
                        Xpp3Dom var11 = (Xpp3Dom)var25.next();
                        mergeIntoXpp3Dom(var11, var24, var2);
                     }
                  }
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

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Xpp3Dom)) {
         return false;
      } else {
         Xpp3Dom var2 = (Xpp3Dom)var1;
         if (this.name == null ? var2.name == null : this.name.equals(var2.name)) {
            if (this.value == null ? var2.value == null : this.value.equals(var2.value)) {
               if (this.attributes == null ? var2.attributes == null : this.attributes.equals(var2.attributes)) {
                  return this.childList == null ? var2.childList == null : this.childList.equals(var2.childList);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = 37 * var1 + (this.name != null ? this.name.hashCode() : 0);
      var1 = 37 * var1 + (this.value != null ? this.value.hashCode() : 0);
      var1 = 37 * var1 + (this.attributes != null ? this.attributes.hashCode() : 0);
      return 37 * var1 + (this.childList != null ? this.childList.hashCode() : 0);
   }

   @Override
   public String toString() {
      StringWriter var1 = new StringWriter();
      PrettyPrintXMLWriter var2 = new PrettyPrintXMLWriter(var1, "UTF-8", null);
      Xpp3DomWriter.write(var2, this);
      return var1.toString();
   }

   public String toUnescapedString() {
      StringWriter var1 = new StringWriter();
      PrettyPrintXMLWriter var2 = new PrettyPrintXMLWriter(var1, "UTF-8", null);
      Xpp3DomWriter.write(var2, this, false);
      return var1.toString();
   }

   public static boolean isNotEmpty(String var0) {
      return var0 != null && var0.length() > 0;
   }

   public static boolean isEmpty(String var0) {
      return var0 == null || var0.trim().length() == 0;
   }
}
