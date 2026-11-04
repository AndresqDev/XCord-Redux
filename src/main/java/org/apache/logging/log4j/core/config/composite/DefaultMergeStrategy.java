package org.apache.logging.log4j.core.config.composite;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.filter.CompositeFilter;

public class DefaultMergeStrategy implements MergeStrategy {
   private static final String APPENDERS = "appenders";
   private static final String PROPERTIES = "properties";
   private static final String LOGGERS = "loggers";
   private static final String SCRIPTS = "scripts";
   private static final String FILTERS = "filters";
   private static final String STATUS = "status";
   private static final String NAME = "name";
   private static final String REF = "ref";

   @Override
   public void mergeRootProperties(Node var1, AbstractConfiguration var2) {
      for (Entry var4 : var2.getRootNode().getAttributes().entrySet()) {
         boolean var5 = false;

         for (Entry var7 : var1.getAttributes().entrySet()) {
            if (((String)var7.getKey()).equalsIgnoreCase((String)var4.getKey())) {
               if (((String)var4.getKey()).equalsIgnoreCase("status")) {
                  Level var8 = Level.getLevel(((String)var7.getValue()).toUpperCase());
                  Level var9 = Level.getLevel(((String)var4.getValue()).toUpperCase());
                  if (var8 != null && var9 != null) {
                     if (var9.isLessSpecificThan(var8)) {
                        var7.setValue(var4.getValue());
                     }
                  } else if (var9 != null) {
                     var7.setValue(var4.getValue());
                  }
               } else if (((String)var4.getKey()).equalsIgnoreCase("monitorInterval")) {
                  int var10 = Integer.parseInt((String)var4.getValue());
                  int var12 = Integer.parseInt((String)var7.getValue());
                  if (var12 == 0 || var10 < var12) {
                     var7.setValue(var4.getValue());
                  }
               } else if (((String)var4.getKey()).equalsIgnoreCase("packages")) {
                  String var11 = (String)var4.getValue();
                  String var13 = (String)var7.getValue();
                  if (var11 != null) {
                     if (var13 != null) {
                        var7.setValue(var13 + "," + var11);
                     } else {
                        var7.setValue(var11);
                     }
                  }
               } else {
                  var7.setValue(var4.getValue());
               }

               var5 = true;
            }
         }

         if (!var5) {
            var1.getAttributes().put((String)var4.getKey(), (String)var4.getValue());
         }
      }
   }

   @Override
   public void mergConfigurations(Node var1, Node var2, PluginManager var3) {
      for (Node var5 : var2.getChildren()) {
         boolean var6 = this.isFilterNode(var5);
         boolean var7 = false;

         for (Node var9 : var1.getChildren()) {
            if (var6) {
               if (this.isFilterNode(var9)) {
                  this.updateFilterNode(var1, var9, var5, var3);
                  var7 = true;
                  break;
               }
            } else if (var9.getName().equalsIgnoreCase(var5.getName())) {
               String var10 = var9.getName().toLowerCase();
               switch (var10) {
                  case "properties":
                  case "scripts":
                  case "appenders":
                     for (Node var24 : var5.getChildren()) {
                        for (Node var27 : var9.getChildren()) {
                           if (Objects.equals(var27.getAttributes().get("name"), var24.getAttributes().get("name"))) {
                              var9.getChildren().remove(var27);
                              break;
                           }
                        }

                        var9.getChildren().add(var24);
                     }

                     var7 = true;
                     break;
                  case "loggers":
                     HashMap var12 = new HashMap();

                     for (Node var14 : var9.getChildren()) {
                        var12.put(var14.getName(), var14);
                     }

                     for (Node var25 : var5.getChildren()) {
                        Node var15 = this.getLoggerNode(var9, var25.getAttributes().get("name"));
                        Node var16 = new Node(var9, var25.getName(), var25.getType());
                        if (var15 != null) {
                           var15.getAttributes().putAll(var25.getAttributes());

                           for (Node var18 : var25.getChildren()) {
                              if (this.isFilterNode(var18)) {
                                 boolean var28 = false;

                                 for (Node var33 : var15.getChildren()) {
                                    if (this.isFilterNode(var33)) {
                                       this.updateFilterNode(var16, var33, var18, var3);
                                       var28 = true;
                                       break;
                                    }
                                 }

                                 if (!var28) {
                                    Node var31 = new Node(var16, var18.getName(), var18.getType());
                                    var31.getAttributes().putAll(var18.getAttributes());
                                    var31.getChildren().addAll(var18.getChildren());
                                    var15.getChildren().add(var31);
                                 }
                              } else {
                                 Node var19 = new Node(var16, var18.getName(), var18.getType());
                                 var19.getAttributes().putAll(var18.getAttributes());
                                 var19.getChildren().addAll(var18.getChildren());
                                 if (var19.getName().equalsIgnoreCase("AppenderRef")) {
                                    for (Node var21 : var15.getChildren()) {
                                       if (this.isSameReference(var21, var19)) {
                                          var15.getChildren().remove(var21);
                                          break;
                                       }
                                    }
                                 } else {
                                    for (Node var32 : var15.getChildren()) {
                                       if (this.isSameName(var32, var19)) {
                                          var15.getChildren().remove(var32);
                                          break;
                                       }
                                    }
                                 }

                                 var15.getChildren().add(var19);
                              }
                           }
                        } else {
                           var16.getAttributes().putAll(var25.getAttributes());
                           var16.getChildren().addAll(var25.getChildren());
                           var9.getChildren().add(var16);
                        }
                     }

                     var7 = true;
                     break;
                  default:
                     var9.getChildren().addAll(var5.getChildren());
                     var7 = true;
               }
            }
         }

         if (!var7) {
            if (var5.getName().equalsIgnoreCase("Properties")) {
               var1.getChildren().add(0, var5);
            } else {
               var1.getChildren().add(var5);
            }
         }
      }
   }

   private Node getLoggerNode(Node var1, String var2) {
      for (Node var4 : var1.getChildren()) {
         String var5 = var4.getAttributes().get("name");
         if (var2 == null && var5 == null) {
            return var4;
         }

         if (var5 != null && var5.equals(var2)) {
            return var4;
         }
      }

      return null;
   }

   private void updateFilterNode(Node var1, Node var2, Node var3, PluginManager var4) {
      if (CompositeFilter.class.isAssignableFrom(var2.getType().getPluginClass())) {
         Node var5 = new Node(var2, var3.getName(), var3.getType());
         var5.getChildren().addAll(var3.getChildren());
         var5.getAttributes().putAll(var3.getAttributes());
         var2.getChildren().add(var5);
      } else {
         PluginType var10 = var4.getPluginType("filters");
         Node var6 = new Node(var2, "filters", var10);
         Node var7 = new Node(var6, var3.getName(), var3.getType());
         var7.getAttributes().putAll(var3.getAttributes());
         List var8 = var6.getChildren();
         var8.add(var2);
         var8.add(var7);
         List var9 = var1.getChildren();
         var9.remove(var2);
         var9.add(var6);
      }
   }

   private boolean isFilterNode(Node var1) {
      return Filter.class.isAssignableFrom(var1.getType().getPluginClass());
   }

   private boolean isSameName(Node var1, Node var2) {
      String var3 = var1.getAttributes().get("name");
      return var3 != null && var3.toLowerCase().equals(var2.getAttributes().get("name").toLowerCase());
   }

   private boolean isSameReference(Node var1, Node var2) {
      String var3 = var1.getAttributes().get("ref");
      return var3 != null && var3.toLowerCase().equals(var2.getAttributes().get("ref").toLowerCase());
   }
}
