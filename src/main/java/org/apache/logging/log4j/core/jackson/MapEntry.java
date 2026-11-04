package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonPropertyOrder({"key", "value"})
final class MapEntry {
   @JsonProperty
   @JacksonXmlProperty(
      isAttribute = true
   )
   private String key;
   @JsonProperty
   @JacksonXmlProperty(
      isAttribute = true
   )
   private String value;

   @JsonCreator
   public MapEntry(@JsonProperty("key") String var1, @JsonProperty("value") String var2) {
      this.setKey(var1);
      this.setValue(var2);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!(var1 instanceof MapEntry)) {
         return false;
      } else {
         MapEntry var2 = (MapEntry)var1;
         if (this.getKey() == null) {
            if (var2.getKey() != null) {
               return false;
            }
         } else if (!this.getKey().equals(var2.getKey())) {
            return false;
         }

         if (this.getValue() == null) {
            if (var2.getValue() != null) {
               return false;
            }
         } else if (!this.getValue().equals(var2.getValue())) {
            return false;
         }

         return true;
      }
   }

   public String getKey() {
      return this.key;
   }

   public String getValue() {
      return this.value;
   }

   @Override
   public int hashCode() {
      byte var1 = 31;
      int var2 = 1;
      var2 = 31 * var2 + (this.getKey() == null ? 0 : this.getKey().hashCode());
      return 31 * var2 + (this.getValue() == null ? 0 : this.getValue().hashCode());
   }

   public void setKey(String var1) {
      this.key = var1;
   }

   public void setValue(String var1) {
      this.value = var1;
   }

   @Override
   public String toString() {
      return "" + this.getKey() + "=" + this.getValue();
   }
}
