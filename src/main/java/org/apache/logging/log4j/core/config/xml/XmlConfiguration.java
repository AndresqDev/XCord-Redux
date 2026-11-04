package org.apache.logging.log4j.core.config.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
import org.apache.logging.log4j.core.config.status.StatusConfiguration;
import org.apache.logging.log4j.core.util.Closer;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.Patterns;
import org.apache.logging.log4j.core.util.Throwables;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlConfiguration extends AbstractConfiguration implements Reconfigurable {
   private static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
   private static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
   private static final String[] VERBOSE_CLASSES = new String[]{ResolverUtil.class.getName()};
   private static final String LOG4J_XSD = "Log4j-config.xsd";
   private final List<XmlConfiguration.Status> status = new ArrayList<>();
   private Element rootElement;
   private boolean strict;
   private String schemaResource;

   public XmlConfiguration(LoggerContext var1, ConfigurationSource var2) {
      super(var1, var2);
      File var3 = var2.getFile();
      byte[] var4 = null;

      try {
         InputStream var5 = var2.getInputStream();

         try {
            var4 = toByteArray(var5);
         } finally {
            Closer.closeSilently(var5);
         }

         InputSource var6 = new InputSource(new ByteArrayInputStream(var4));
         var6.setSystemId(var2.getLocation());
         DocumentBuilder var7 = newDocumentBuilder(true);

         Document var8;
         try {
            var8 = var7.parse(var6);
         } catch (Exception var47) {
            Throwable var10 = Throwables.getRootCause(var47);
            if (!(var10 instanceof UnsupportedOperationException)) {
               throw var47;
            }

            LOGGER.warn("The DocumentBuilder {} does not support an operation: {}.Trying again without XInclude...", var7, var47);
            var8 = newDocumentBuilder(false).parse(var6);
         }

         this.rootElement = var8.getDocumentElement();
         Map var9 = this.processAttributes(this.rootNode, this.rootElement);
         StatusConfiguration var54 = new StatusConfiguration().withVerboseClasses(VERBOSE_CLASSES).withStatus(this.getDefaultStatus());
         int var11 = 0;

         for (Entry var13 : var9.entrySet()) {
            String var14 = (String)var13.getKey();
            String var15 = this.getConfigurationStrSubstitutor().replace((String)var13.getValue());
            if ("status".equalsIgnoreCase(var14)) {
               var54.withStatus(var15);
            } else if ("dest".equalsIgnoreCase(var14)) {
               var54.withDestination(var15);
            } else if ("shutdownHook".equalsIgnoreCase(var14)) {
               this.isShutdownHookEnabled = !"disable".equalsIgnoreCase(var15);
            } else if ("shutdownTimeout".equalsIgnoreCase(var14)) {
               this.shutdownTimeoutMillis = Long.parseLong(var15);
            } else if ("verbose".equalsIgnoreCase(var14)) {
               var54.withVerbosity(var15);
            } else if ("packages".equalsIgnoreCase(var14)) {
               this.pluginPackages.addAll(Arrays.asList(var15.split(Patterns.COMMA_SEPARATOR)));
            } else if ("name".equalsIgnoreCase(var14)) {
               this.setName(var15);
            } else if ("strict".equalsIgnoreCase(var14)) {
               this.strict = Boolean.parseBoolean(var15);
            } else if ("schema".equalsIgnoreCase(var14)) {
               this.schemaResource = var15;
            } else if ("monitorInterval".equalsIgnoreCase(var14)) {
               var11 = Integer.parseInt(var15);
            } else if ("advertiser".equalsIgnoreCase(var14)) {
               this.createAdvertiser(var15, var2, var4, "text/xml");
            }
         }

         this.initializeWatchers(this, var2, var11);
         var54.initialize();
      } catch (IOException | ParserConfigurationException | SAXException var48) {
         LOGGER.error("Error parsing " + var2.getLocation(), (Throwable)var48);
      }

      if (this.strict && this.schemaResource != null && var4 != null) {
         try (InputStream var49 = Loader.getResourceAsStream(this.schemaResource, XmlConfiguration.class.getClassLoader())) {
            if (var49 != null) {
               StreamSource var51 = new StreamSource(var49, "Log4j-config.xsd");
               SchemaFactory var52 = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
               Schema var53 = null;

               try {
                  var53 = var52.newSchema(var51);
               } catch (SAXException var42) {
                  LOGGER.error("Error parsing Log4j schema", (Throwable)var42);
               }

               if (var53 != null) {
                  Validator var55 = var53.newValidator();

                  try {
                     var55.validate(new StreamSource(new ByteArrayInputStream(var4)));
                  } catch (IOException var40) {
                     LOGGER.error("Error reading configuration for validation", (Throwable)var40);
                  } catch (SAXException var41) {
                     LOGGER.error("Error validating configuration", (Throwable)var41);
                  }
               }
            }
         } catch (Exception var46) {
            LOGGER.error("Unable to access schema {}", this.schemaResource, var46);
         }
      }

      if (this.getName() == null) {
         this.setName(var2.getLocation());
      }
   }

   static DocumentBuilder newDocumentBuilder(boolean var0) throws ParserConfigurationException {
      DocumentBuilderFactory var1 = DocumentBuilderFactory.newInstance();
      var1.setNamespaceAware(true);
      disableDtdProcessing(var1);
      if (var0) {
         enableXInclude(var1);
      }

      return var1.newDocumentBuilder();
   }

   private static void disableDtdProcessing(DocumentBuilderFactory var0) {
      var0.setValidating(false);
      var0.setExpandEntityReferences(false);
      setFeature(var0, "http://xml.org/sax/features/external-general-entities", false);
      setFeature(var0, "http://xml.org/sax/features/external-parameter-entities", false);
      setFeature(var0, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
   }

   private static void setFeature(DocumentBuilderFactory var0, String var1, boolean var2) {
      try {
         var0.setFeature(var1, var2);
      } catch (LinkageError | Exception var4) {
         getStatusLogger()
            .error("Caught {} setting feature {} to {} on DocumentBuilderFactory {}: {}", var4.getClass().getCanonicalName(), var1, var2, var0, var4, var4);
      }
   }

   private static void enableXInclude(DocumentBuilderFactory var0) {
      try {
         var0.setXIncludeAware(true);
      } catch (UnsupportedOperationException var6) {
         LOGGER.warn("The DocumentBuilderFactory [{}] does not support XInclude: {}", var0, var6);
      } catch (NoSuchMethodError | AbstractMethodError var7) {
         LOGGER.warn("The DocumentBuilderFactory [{}] is out of date and does not support XInclude: {}", var0, var7);
      }

      try {
         var0.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", true);
      } catch (ParserConfigurationException var4) {
         LOGGER.warn(
            "The DocumentBuilderFactory [{}] does not support the feature [{}]: {}", var0, "http://apache.org/xml/features/xinclude/fixup-base-uris", var4
         );
      } catch (AbstractMethodError var5) {
         LOGGER.warn("The DocumentBuilderFactory [{}] is out of date and does not support setFeature: {}", var0, var5);
      }

      try {
         var0.setFeature("http://apache.org/xml/features/xinclude/fixup-language", true);
      } catch (ParserConfigurationException var2) {
         LOGGER.warn(
            "The DocumentBuilderFactory [{}] does not support the feature [{}]: {}", var0, "http://apache.org/xml/features/xinclude/fixup-language", var2
         );
      } catch (AbstractMethodError var3) {
         LOGGER.warn("The DocumentBuilderFactory [{}] is out of date and does not support setFeature: {}", var0, var3);
      }
   }

   @Override
   public void setup() {
      if (this.rootElement == null) {
         LOGGER.error("No logging configuration");
      } else {
         this.constructHierarchy(this.rootNode, this.rootElement);
         if (this.status.size() <= 0) {
            this.rootElement = null;
         } else {
            for (XmlConfiguration.Status var2 : this.status) {
               LOGGER.error("Error processing element {} ({}): {}", var2.name, var2.element, var2.errorType);
            }
         }
      }
   }

   @Override
   public Configuration reconfigure() {
      try {
         ConfigurationSource var1 = this.getConfigurationSource().resetInputStream();
         if (var1 == null) {
            return null;
         } else {
            XmlConfiguration var2 = new XmlConfiguration(this.getLoggerContext(), var1);
            return var2.rootElement == null ? null : var2;
         }
      } catch (IOException var3) {
         LOGGER.error("Cannot locate file {}", this.getConfigurationSource(), var3);
         return null;
      }
   }

   private void constructHierarchy(Node var1, Element var2) {
      this.processAttributes(var1, var2);
      StringBuilder var3 = new StringBuilder();
      NodeList var4 = var2.getChildNodes();
      List var5 = var1.getChildren();

      for (int var6 = 0; var6 < var4.getLength(); var6++) {
         org.w3c.dom.Node var7 = var4.item(var6);
         if (var7 instanceof Element) {
            Element var8 = (Element)var7;
            String var9 = this.getType(var8);
            PluginType var10 = this.pluginManager.getPluginType(var9);
            Node var11 = new Node(var1, var9, var10);
            this.constructHierarchy(var11, var8);
            if (var10 == null) {
               String var12 = var11.getValue();
               if (!var11.hasChildren() && var12 != null) {
                  var1.getAttributes().put(var9, var12);
               } else {
                  this.status.add(new XmlConfiguration.Status(var9, var2, XmlConfiguration.ErrorType.CLASS_NOT_FOUND));
               }
            } else {
               var5.add(var11);
            }
         } else if (var7 instanceof Text) {
            Text var14 = (Text)var7;
            var3.append(var14.getData());
         }
      }

      String var13 = var3.toString().trim();
      if (var13.length() > 0 || !var1.hasChildren() && !var1.isRoot()) {
         var1.setValue(var13);
      }
   }

   private String getType(Element var1) {
      if (this.strict) {
         NamedNodeMap var2 = var1.getAttributes();

         for (int var3 = 0; var3 < var2.getLength(); var3++) {
            org.w3c.dom.Node var4 = var2.item(var3);
            if (var4 instanceof Attr) {
               Attr var5 = (Attr)var4;
               if (var5.getName().equalsIgnoreCase("type")) {
                  String var6 = var5.getValue();
                  var2.removeNamedItem(var5.getName());
                  return var6;
               }
            }
         }
      }

      return var1.getTagName();
   }

   private Map<String, String> processAttributes(Node var1, Element var2) {
      NamedNodeMap var3 = var2.getAttributes();
      Map var4 = var1.getAttributes();

      for (int var5 = 0; var5 < var3.getLength(); var5++) {
         org.w3c.dom.Node var6 = var3.item(var5);
         if (var6 instanceof Attr) {
            Attr var7 = (Attr)var6;
            if (!var7.getName().equals("xml:base")) {
               var4.put(var7.getName(), var7.getValue());
            }
         }
      }

      return var4;
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + "[location=" + this.getConfigurationSource() + "]";
   }

   private static enum ErrorType {
      CLASS_NOT_FOUND;
   }

   private static class Status {
      private final Element element;
      private final String name;
      private final XmlConfiguration.ErrorType errorType;

      public Status(String var1, Element var2, XmlConfiguration.ErrorType var3) {
         this.name = var1;
         this.element = var2;
         this.errorType = var3;
      }

      @Override
      public String toString() {
         return "Status [name=" + this.name + ", element=" + this.element + ", errorType=" + this.errorType + "]";
      }
   }
}
