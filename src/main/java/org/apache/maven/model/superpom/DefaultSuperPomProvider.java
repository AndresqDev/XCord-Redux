package org.apache.maven.model.superpom;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import org.apache.maven.model.InputSource;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelProcessor;
import xcord.Inject;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultSuperPomProvider implements SuperPomProvider {
   private Model superModel;
   @Inject
   private ModelProcessor modelProcessor;

   public DefaultSuperPomProvider setModelProcessor(ModelProcessor var1) {
      this.modelProcessor = var1;
      return this;
   }

   @Override
   public Model getSuperModel(String var1) {
      if (this.superModel == null) {
         String var2 = "/org/apache/maven/model/pom-" + var1 + ".xml";
         InputStream var3 = this.getClass().getResourceAsStream(var2);
         if (var3 == null) {
            throw new IllegalStateException("The super POM " + var2 + " was not found, please verify the integrity of your Maven installation");
         }

         try {
            HashMap var4 = new HashMap();
            var4.put("xml:4.0.0", "xml:4.0.0");
            String var5 = "org.apache.maven:maven-model-builder:" + this.getClass().getPackage().getImplementationVersion() + ":super-pom";
            InputSource var6 = new InputSource();
            var6.setModelId(var5);
            var6.setLocation(this.getClass().getResource(var2).toExternalForm());
            var4.put("org.apache.maven.model.io.inputSource", var6);
            this.superModel = this.modelProcessor.read(var3, var4);
         } catch (IOException var7) {
            throw new IllegalStateException("The super POM " + var2 + " is damaged, please verify the integrity of your Maven installation", var7);
         }
      }

      return this.superModel;
   }
}
