package org.yaml.snakeyaml;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertySubstitute;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

public class TypeDescription {
   private static final Logger log = Logger.getLogger(TypeDescription.class.getPackage().getName());
   private final Class<? extends Object> type;
   private Class<?> impl;
   private Tag tag;
   private transient Set<Property> dumpProperties;
   private transient PropertyUtils propertyUtils;
   private transient boolean delegatesChecked;
   private Map<String, PropertySubstitute> properties = Collections.emptyMap();
   protected Set<String> excludes = Collections.emptySet();
   protected String[] includes = null;
   protected BeanAccess beanAccess;

   public TypeDescription(Class<? extends Object> var1, Tag var2) {
      this(var1, var2, null);
   }

   public TypeDescription(Class<? extends Object> var1, Tag var2, Class<?> var3) {
      this.type = var1;
      this.tag = var2;
      this.impl = var3;
      this.beanAccess = null;
   }

   public TypeDescription(Class<? extends Object> var1, String var2) {
      this(var1, new Tag(var2), null);
   }

   public TypeDescription(Class<? extends Object> var1) {
      this(var1, (Tag)null, null);
   }

   public TypeDescription(Class<? extends Object> var1, Class<?> var2) {
      this(var1, null, var2);
   }

   public Tag getTag() {
      return this.tag;
   }

   @Deprecated
   public void setTag(Tag var1) {
      this.tag = var1;
   }

   @Deprecated
   public void setTag(String var1) {
      this.setTag(new Tag(var1));
   }

   public Class<? extends Object> getType() {
      return this.type;
   }

   @Deprecated
   public void putListPropertyType(String var1, Class<? extends Object> var2) {
      this.addPropertyParameters(var1, var2);
   }

   @Deprecated
   public Class<? extends Object> getListPropertyType(String var1) {
      if (this.properties.containsKey(var1)) {
         Class[] var2 = this.properties.get(var1).getActualTypeArguments();
         if (var2 != null && var2.length > 0) {
            return var2[0];
         }
      }

      return null;
   }

   @Deprecated
   public void putMapPropertyType(String var1, Class<? extends Object> var2, Class<? extends Object> var3) {
      this.addPropertyParameters(var1, var2, var3);
   }

   @Deprecated
   public Class<? extends Object> getMapKeyType(String var1) {
      if (this.properties.containsKey(var1)) {
         Class[] var2 = this.properties.get(var1).getActualTypeArguments();
         if (var2 != null && var2.length > 0) {
            return var2[0];
         }
      }

      return null;
   }

   @Deprecated
   public Class<? extends Object> getMapValueType(String var1) {
      if (this.properties.containsKey(var1)) {
         Class[] var2 = this.properties.get(var1).getActualTypeArguments();
         if (var2 != null && var2.length > 1) {
            return var2[1];
         }
      }

      return null;
   }

   public void addPropertyParameters(String var1, Class<?>... var2) {
      if (!this.properties.containsKey(var1)) {
         this.substituteProperty(var1, null, null, null, var2);
      } else {
         PropertySubstitute var3 = this.properties.get(var1);
         var3.setActualTypeArguments(var2);
      }
   }

   @Override
   public String toString() {
      return "TypeDescription for " + this.getType() + " (tag='" + this.getTag() + "')";
   }

   private void checkDelegates() {
      for (PropertySubstitute var3 : this.properties.values()) {
         try {
            var3.setDelegate(this.discoverProperty(var3.getName()));
         } catch (YAMLException var5) {
         }
      }

      this.delegatesChecked = true;
   }

   private Property discoverProperty(String var1) {
      if (this.propertyUtils != null) {
         return this.beanAccess == null ? this.propertyUtils.getProperty(this.type, var1) : this.propertyUtils.getProperty(this.type, var1, this.beanAccess);
      } else {
         return null;
      }
   }

   public Property getProperty(String var1) {
      if (!this.delegatesChecked) {
         this.checkDelegates();
      }

      return this.properties.containsKey(var1) ? this.properties.get(var1) : this.discoverProperty(var1);
   }

   public void substituteProperty(String var1, Class<?> var2, String var3, String var4, Class<?>... var5) {
      this.substituteProperty(new PropertySubstitute(var1, var2, var3, var4, var5));
   }

   public void substituteProperty(PropertySubstitute var1) {
      if (Collections.EMPTY_MAP == this.properties) {
         this.properties = new LinkedHashMap<>();
      }

      var1.setTargetType(this.type);
      this.properties.put(var1.getName(), var1);
   }

   public void setPropertyUtils(PropertyUtils var1) {
      this.propertyUtils = var1;
   }

   public void setIncludes(String... var1) {
      this.includes = var1 != null && var1.length > 0 ? var1 : null;
   }

   public void setExcludes(String... var1) {
      if (var1 != null && var1.length > 0) {
         this.excludes = new HashSet<>();

         for (String var5 : var1) {
            this.excludes.add(var5);
         }
      } else {
         this.excludes = Collections.emptySet();
      }
   }

   public Set<Property> getProperties() {
      if (this.dumpProperties != null) {
         return this.dumpProperties;
      } else if (this.propertyUtils != null) {
         if (this.includes != null) {
            this.dumpProperties = new LinkedHashSet<>();

            for (String var4 : this.includes) {
               if (!this.excludes.contains(var4)) {
                  this.dumpProperties.add(this.getProperty(var4));
               }
            }

            return this.dumpProperties;
         } else {
            Set var1 = this.beanAccess == null ? this.propertyUtils.getProperties(this.type) : this.propertyUtils.getProperties(this.type, this.beanAccess);
            if (this.properties.isEmpty()) {
               if (this.excludes.isEmpty()) {
                  return this.dumpProperties = var1;
               } else {
                  this.dumpProperties = new LinkedHashSet<>();

                  for (Property var10 : var1) {
                     if (!this.excludes.contains(var10.getName())) {
                        this.dumpProperties.add(var10);
                     }
                  }

                  return this.dumpProperties;
               }
            } else {
               if (!this.delegatesChecked) {
                  this.checkDelegates();
               }

               this.dumpProperties = new LinkedHashSet<>();

               for (Property var3 : this.properties.values()) {
                  if (!this.excludes.contains(var3.getName()) && var3.isReadable()) {
                     this.dumpProperties.add(var3);
                  }
               }

               for (Property var9 : var1) {
                  if (!this.excludes.contains(var9.getName())) {
                     this.dumpProperties.add(var9);
                  }
               }

               return this.dumpProperties;
            }
         }
      } else {
         return null;
      }
   }

   public boolean setupPropertyType(String var1, Node var2) {
      return false;
   }

   public boolean setProperty(Object var1, String var2, Object var3) throws Exception {
      return false;
   }

   public Object newInstance(Node var1) {
      if (this.impl != null) {
         try {
            Constructor var2 = this.impl.getDeclaredConstructor();
            var2.setAccessible(true);
            return var2.newInstance();
         } catch (Exception var3) {
            log.fine(var3.getLocalizedMessage());
            this.impl = null;
         }
      }

      return null;
   }

   public Object newInstance(String var1, Node var2) {
      return null;
   }

   public Object finalizeConstruction(Object var1) {
      return var1;
   }
}
