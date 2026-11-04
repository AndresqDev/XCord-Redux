package org.apache.logging.log4j.message;

import java.util.Map;
import org.apache.logging.log4j.util.EnglishEnums;
import org.apache.logging.log4j.util.StringBuilders;

@AsynchronouslyFormattable
public class StructuredDataMessage extends MapMessage<StructuredDataMessage, String> {
   private static final long serialVersionUID = 1703221292892071920L;
   private static final int MAX_LENGTH = 32;
   private static final int HASHVAL = 31;
   private StructuredDataId id;
   private String message;
   private String type;
   private final int maxLength;

   public StructuredDataMessage(String var1, String var2, String var3) {
      this(var1, var2, var3, 32);
   }

   public StructuredDataMessage(String var1, String var2, String var3, int var4) {
      this.id = new StructuredDataId(var1, null, null, var4);
      this.message = var2;
      this.type = var3;
      this.maxLength = var4;
   }

   public StructuredDataMessage(String var1, String var2, String var3, Map<String, String> var4) {
      this(var1, var2, var3, var4, 32);
   }

   public StructuredDataMessage(String var1, String var2, String var3, Map<String, String> var4, int var5) {
      super(var4);
      this.id = new StructuredDataId(var1, null, null, var5);
      this.message = var2;
      this.type = var3;
      this.maxLength = var5;
   }

   public StructuredDataMessage(StructuredDataId var1, String var2, String var3) {
      this(var1, var2, var3, 32);
   }

   public StructuredDataMessage(StructuredDataId var1, String var2, String var3, int var4) {
      this.id = var1;
      this.message = var2;
      this.type = var3;
      this.maxLength = var4;
   }

   public StructuredDataMessage(StructuredDataId var1, String var2, String var3, Map<String, String> var4) {
      this(var1, var2, var3, var4, 32);
   }

   public StructuredDataMessage(StructuredDataId var1, String var2, String var3, Map<String, String> var4, int var5) {
      super(var4);
      this.id = var1;
      this.message = var2;
      this.type = var3;
      this.maxLength = var5;
   }

   private StructuredDataMessage(StructuredDataMessage var1, Map<String, String> var2) {
      super(var2);
      this.id = var1.id;
      this.message = var1.message;
      this.type = var1.type;
      this.maxLength = 32;
   }

   protected StructuredDataMessage() {
      this.maxLength = 32;
   }

   @Override
   public String[] getFormats() {
      String[] var1 = new String[StructuredDataMessage.Format.values().length];
      int var2 = 0;

      for (StructuredDataMessage.Format var6 : StructuredDataMessage.Format.values()) {
         var1[var2++] = var6.name();
      }

      return var1;
   }

   public StructuredDataId getId() {
      return this.id;
   }

   protected void setId(String var1) {
      this.id = new StructuredDataId(var1, null, null);
   }

   protected void setId(StructuredDataId var1) {
      this.id = var1;
   }

   public String getType() {
      return this.type;
   }

   protected void setType(String var1) {
      if (var1.length() > 32) {
         throw new IllegalArgumentException("structured data type exceeds maximum length of 32 characters: " + var1);
      } else {
         this.type = var1;
      }
   }

   @Override
   public void formatTo(StringBuilder var1) {
      this.asString(StructuredDataMessage.Format.FULL, null, var1);
   }

   @Override
   public void formatTo(String[] var1, StringBuilder var2) {
      this.asString(this.getFormat(var1), null, var2);
   }

   @Override
   public String getFormat() {
      return this.message;
   }

   protected void setMessageFormat(String var1) {
      this.message = var1;
   }

   @Override
   public String asString() {
      return this.asString(StructuredDataMessage.Format.FULL, null);
   }

   @Override
   public String asString(String var1) {
      try {
         return this.asString(EnglishEnums.valueOf(StructuredDataMessage.Format.class, var1), null);
      } catch (IllegalArgumentException var3) {
         return this.asString();
      }
   }

   public final String asString(StructuredDataMessage.Format var1, StructuredDataId var2) {
      StringBuilder var3 = new StringBuilder();
      this.asString(var1, var2, var3);
      return var3.toString();
   }

   public final void asString(StructuredDataMessage.Format var1, StructuredDataId var2, StringBuilder var3) {
      boolean var4 = StructuredDataMessage.Format.FULL.equals(var1);
      if (var4) {
         String var5 = this.getType();
         if (var5 == null) {
            return;
         }

         var3.append(this.getType()).append(' ');
      }

      StructuredDataId var7 = this.getId();
      if (var7 != null) {
         var7 = var7.makeId(var2);
      } else {
         var7 = var2;
      }

      if (var7 != null && var7.getName() != null) {
         if (StructuredDataMessage.Format.XML.equals(var1)) {
            this.asXml(var7, var3);
         } else {
            var3.append('[');
            StringBuilders.appendValue(var3, var7);
            var3.append(' ');
            this.appendMap(var3);
            var3.append(']');
            if (var4) {
               String var6 = this.getFormat();
               if (var6 != null) {
                  var3.append(' ').append(var6);
               }
            }
         }
      }
   }

   private void asXml(StructuredDataId var1, StringBuilder var2) {
      var2.append("<StructuredData>\n");
      var2.append("<type>").append(this.type).append("</type>\n");
      var2.append("<id>").append(var1).append("</id>\n");
      super.asXml(var2);
      var2.append("\n</StructuredData>\n");
   }

   @Override
   public String getFormattedMessage() {
      return this.asString(StructuredDataMessage.Format.FULL, null);
   }

   @Override
   public String getFormattedMessage(String[] var1) {
      return this.asString(this.getFormat(var1), null);
   }

   private StructuredDataMessage.Format getFormat(String[] var1) {
      if (var1 != null && var1.length > 0) {
         for (int var2 = 0; var2 < var1.length; var2++) {
            String var3 = var1[var2];
            if (StructuredDataMessage.Format.XML.name().equalsIgnoreCase(var3)) {
               return StructuredDataMessage.Format.XML;
            }

            if (StructuredDataMessage.Format.FULL.name().equalsIgnoreCase(var3)) {
               return StructuredDataMessage.Format.FULL;
            }
         }

         return null;
      } else {
         return StructuredDataMessage.Format.FULL;
      }
   }

   @Override
   public String toString() {
      return this.asString(null, null);
   }

   public StructuredDataMessage newInstance(Map<String, String> var1) {
      return new StructuredDataMessage(this, var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         StructuredDataMessage var2 = (StructuredDataMessage)var1;
         if (!super.equals(var1)) {
            return false;
         } else if (this.type != null ? this.type.equals(var2.type) : var2.type == null) {
            if (this.id != null ? this.id.equals(var2.id) : var2.id == null) {
               return this.message != null ? this.message.equals(var2.message) : var2.message == null;
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

   @Override
   public int hashCode() {
      int var1 = super.hashCode();
      var1 = 31 * var1 + (this.type != null ? this.type.hashCode() : 0);
      var1 = 31 * var1 + (this.id != null ? this.id.hashCode() : 0);
      return 31 * var1 + (this.message != null ? this.message.hashCode() : 0);
   }

   @Override
   protected void validate(String var1, boolean var2) {
      this.validateKey(var1);
   }

   @Override
   protected void validate(String var1, byte var2) {
      this.validateKey(var1);
   }

   @Override
   protected void validate(String var1, char var2) {
      this.validateKey(var1);
   }

   @Override
   protected void validate(String var1, double var2) {
      this.validateKey(var1);
   }

   @Override
   protected void validate(String var1, float var2) {
      this.validateKey(var1);
   }

   @Override
   protected void validate(String var1, int var2) {
      this.validateKey(var1);
   }

   @Override
   protected void validate(String var1, long var2) {
      this.validateKey(var1);
   }

   @Override
   protected void validate(String var1, Object var2) {
      this.validateKey(var1);
   }

   @Override
   protected void validate(String var1, short var2) {
      this.validateKey(var1);
   }

   @Override
   protected void validate(String var1, String var2) {
      this.validateKey(var1);
   }

   protected void validateKey(String var1) {
      if (this.maxLength > 0 && var1.length() > this.maxLength) {
         throw new IllegalArgumentException("Structured data keys are limited to " + this.maxLength + " characters. key: " + var1);
      } else {
         for (int var2 = 0; var2 < var1.length(); var2++) {
            char var3 = var1.charAt(var2);
            if (var3 < '!' || var3 > '~' || var3 == '=' || var3 == ']' || var3 == '"') {
               throw new IllegalArgumentException("Structured data keys must contain printable US ASCII charactersand may not contain a space, =, ], or \"");
            }
         }
      }
   }

   public static enum Format {
      XML,
      FULL;
   }
}
