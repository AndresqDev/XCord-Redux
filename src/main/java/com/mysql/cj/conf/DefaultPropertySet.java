package com.mysql.cj.conf;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DefaultPropertySet implements PropertySet, Serializable {
   private static final long serialVersionUID = -5156024634430650528L;
   private final Map<PropertyKey, RuntimeProperty<?>> PROPERTY_KEY_TO_RUNTIME_PROPERTY = new HashMap<>();
   private final Map<String, RuntimeProperty<?>> PROPERTY_NAME_TO_RUNTIME_PROPERTY = new HashMap<>();

   public DefaultPropertySet() {
      for (PropertyDefinition<?> pdef : PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.values()) {
         this.addProperty(pdef.createRuntimeProperty());
      }
   }

   @Override
   public void addProperty(RuntimeProperty<?> prop) {
      PropertyDefinition<?> def = prop.getPropertyDefinition();
      if (def.getPropertyKey() != null) {
         this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.put(def.getPropertyKey(), prop);
      } else {
         this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.put(def.getName(), prop);
         if (def.hasCcAlias()) {
            this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.put(def.getCcAlias(), prop);
         }
      }
   }

   @Override
   public void removeProperty(String name) {
      PropertyKey key = PropertyKey.fromValue(name);
      if (key != null) {
         this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.remove(key);
      } else {
         RuntimeProperty<?> prop = this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.remove(name);
         if (prop != null) {
            if (!name.equals(prop.getPropertyDefinition().getName())) {
               this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.remove(prop.getPropertyDefinition().getName());
            } else if (prop.getPropertyDefinition().hasCcAlias()) {
               this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.remove(prop.getPropertyDefinition().getCcAlias());
            }
         }
      }
   }

   @Override
   public void removeProperty(PropertyKey key) {
      this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.remove(key);
   }

   @Override
   public <T> RuntimeProperty<T> getProperty(String name) {
      try {
         PropertyKey key = PropertyKey.fromValue(name);
         return key != null ? this.getProperty(key) : (RuntimeProperty)this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.get(name);
      } catch (ClassCastException var3) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, var3.getMessage(), var3);
      }
   }

   @Override
   public <T> RuntimeProperty<T> getProperty(PropertyKey key) {
      try {
         RuntimeProperty<T> prop = (RuntimeProperty<T>)this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.get(key);
         if (prop == null) {
            prop = (RuntimeProperty<T>)this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.get(key.getKeyName());
         }

         return prop;
      } catch (ClassCastException var3) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, var3.getMessage(), var3);
      }
   }

   @Override
   public RuntimeProperty<Boolean> getBooleanProperty(String name) {
      return this.getProperty(name);
   }

   @Override
   public RuntimeProperty<Boolean> getBooleanProperty(PropertyKey key) {
      return this.getProperty(key);
   }

   @Override
   public RuntimeProperty<Integer> getIntegerProperty(String name) {
      return this.getProperty(name);
   }

   @Override
   public RuntimeProperty<Integer> getIntegerProperty(PropertyKey key) {
      return this.getProperty(key);
   }

   @Override
   public RuntimeProperty<Long> getLongProperty(String name) {
      return this.getProperty(name);
   }

   @Override
   public RuntimeProperty<Long> getLongProperty(PropertyKey key) {
      return this.getProperty(key);
   }

   @Override
   public RuntimeProperty<Integer> getMemorySizeProperty(String name) {
      return this.getProperty(name);
   }

   @Override
   public RuntimeProperty<Integer> getMemorySizeProperty(PropertyKey key) {
      return this.getProperty(key);
   }

   @Override
   public RuntimeProperty<String> getStringProperty(String name) {
      return this.getProperty(name);
   }

   @Override
   public RuntimeProperty<String> getStringProperty(PropertyKey key) {
      return this.getProperty(key);
   }

   @Override
   public <T extends Enum<T>> RuntimeProperty<T> getEnumProperty(String name) {
      return this.getProperty(name);
   }

   @Override
   public <T extends Enum<T>> RuntimeProperty<T> getEnumProperty(PropertyKey key) {
      return this.getProperty(key);
   }

   @Override
   public void initializeProperties(Properties props) {
      if (props != null) {
         Properties infoCopy = (Properties)props.clone();
         infoCopy.remove(PropertyKey.HOST.getKeyName());
         infoCopy.remove(PropertyKey.PORT.getKeyName());
         infoCopy.remove(PropertyKey.USER.getKeyName());
         infoCopy.remove(PropertyKey.PASSWORD.getKeyName());
         infoCopy.remove(PropertyKey.DBNAME.getKeyName());

         for (PropertyKey propKey : PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.keySet()) {
            try {
               RuntimeProperty<?> propToSet = this.getProperty(propKey);
               propToSet.initializeFrom(infoCopy, null);
            } catch (CJException var9) {
               throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, var9.getMessage(), var9);
            }
         }

         RuntimeProperty<PropertyDefinitions.SslMode> sslMode = this.getEnumProperty(PropertyKey.sslMode);
         if (!sslMode.isExplicitlySet()) {
            RuntimeProperty<Boolean> useSSL = this.getBooleanProperty(PropertyKey.useSSL);
            RuntimeProperty<Boolean> verifyServerCertificate = this.getBooleanProperty(PropertyKey.verifyServerCertificate);
            RuntimeProperty<Boolean> requireSSL = this.getBooleanProperty(PropertyKey.requireSSL);
            if (useSSL.isExplicitlySet() || verifyServerCertificate.isExplicitlySet() || requireSSL.isExplicitlySet()) {
               if (!useSSL.getValue()) {
                  sslMode.setValue(PropertyDefinitions.SslMode.DISABLED);
               } else if (verifyServerCertificate.getValue()) {
                  sslMode.setValue(PropertyDefinitions.SslMode.VERIFY_CA);
               } else if (requireSSL.getValue()) {
                  sslMode.setValue(PropertyDefinitions.SslMode.REQUIRED);
               }
            }
         }

         for (Object key : infoCopy.keySet()) {
            String val = infoCopy.getProperty((String)key);
            PropertyDefinition<String> def = new StringPropertyDefinition(
               (String)key,
               null,
               val,
               true,
               Messages.getString("ConnectionProperties.unknown"),
               "8.0.10",
               PropertyDefinitions.CATEGORY_USER_DEFINED,
               Integer.MIN_VALUE
            );
            RuntimeProperty<String> p = new StringProperty(def);
            this.addProperty(p);
         }

         this.postInitialization();
      }
   }

   @Override
   public void postInitialization() {
   }

   @Override
   public Properties exposeAsProperties() {
      Properties props = new Properties();

      for (PropertyKey propKey : this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.keySet()) {
         if (!props.containsKey(propKey.getKeyName())) {
            RuntimeProperty<?> propToGet = this.getProperty(propKey);
            String propValue = propToGet.getStringValue();
            if (propValue != null) {
               props.setProperty(propToGet.getPropertyDefinition().getName(), propValue);
            }
         }
      }

      for (String propName : this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.keySet()) {
         if (!props.containsKey(propName)) {
            RuntimeProperty<?> propToGet = this.getProperty(propName);
            String propValue = propToGet.getStringValue();
            if (propValue != null) {
               props.setProperty(propToGet.getPropertyDefinition().getName(), propValue);
            }
         }
      }

      return props;
   }

   @Override
   public void reset() {
      this.PROPERTY_KEY_TO_RUNTIME_PROPERTY.values().forEach(p -> p.resetValue());
      this.PROPERTY_NAME_TO_RUNTIME_PROPERTY.values().forEach(p -> p.resetValue());
      this.postInitialization();
   }
}
