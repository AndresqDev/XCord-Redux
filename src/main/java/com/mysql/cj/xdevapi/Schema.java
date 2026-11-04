package com.mysql.cj.xdevapi;

import java.util.List;

public interface Schema extends DatabaseObject {
   List<Collection> getCollections();

   List<Collection> getCollections(String var1);

   List<Table> getTables();

   List<Table> getTables(String var1);

   Collection getCollection(String var1);

   Collection getCollection(String var1, boolean var2);

   Table getCollectionAsTable(String var1);

   Table getTable(String var1);

   Table getTable(String var1, boolean var2);

   Collection createCollection(String var1);

   Collection createCollection(String var1, boolean var2);

   Collection createCollection(String var1, Schema.CreateCollectionOptions var2);

   void modifyCollection(String var1, Schema.ModifyCollectionOptions var2);

   void dropCollection(String var1);

   public static class CreateCollectionOptions {
      private Boolean reuseExisting = null;
      private Schema.Validation validation = null;

      public Schema.CreateCollectionOptions setReuseExisting(boolean reuse) {
         this.reuseExisting = reuse;
         return this;
      }

      public Boolean getReuseExisting() {
         return this.reuseExisting;
      }

      public Schema.CreateCollectionOptions setValidation(Schema.Validation validation) {
         this.validation = validation;
         return this;
      }

      public Schema.Validation getValidation() {
         return this.validation;
      }
   }

   public static class ModifyCollectionOptions {
      private Schema.Validation validation = null;

      public Schema.ModifyCollectionOptions setValidation(Schema.Validation validation) {
         this.validation = validation;
         return this;
      }

      public Schema.Validation getValidation() {
         return this.validation;
      }
   }

   public static class Validation {
      private Schema.Validation.ValidationLevel level = null;
      private String schema = null;

      public Schema.Validation setLevel(Schema.Validation.ValidationLevel level) {
         this.level = level;
         return this;
      }

      public Schema.Validation.ValidationLevel getLevel() {
         return this.level;
      }

      public Schema.Validation setSchema(String schema) {
         this.schema = schema;
         return this;
      }

      public String getSchema() {
         return this.schema;
      }

      public static enum ValidationLevel {
         STRICT,
         OFF;
      }
   }
}
