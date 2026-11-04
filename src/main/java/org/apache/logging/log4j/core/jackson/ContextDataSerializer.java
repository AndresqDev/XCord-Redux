package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Map;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.TriConsumer;

public class ContextDataSerializer extends StdSerializer<ReadOnlyStringMap> {
   private static final long serialVersionUID = 1L;
   private static final TriConsumer<String, Object, JsonGenerator> WRITE_STRING_FIELD_INTO = (var0, var1, var2) -> {
      try {
         if (var1 == null) {
            var2.writeNullField(var0);
         } else {
            var2.writeStringField(var0, String.valueOf(var1));
         }
      } catch (Exception var4) {
         throw new IllegalStateException("Problem with key " + var0, var4);
      }
   };

   protected ContextDataSerializer() {
      super(Map.class, false);
   }

   public void serialize(ReadOnlyStringMap var1, JsonGenerator var2, SerializerProvider var3) throws IOException, JsonGenerationException {
      var2.writeStartObject();
      var1.forEach(WRITE_STRING_FIELD_INTO, var2);
      var2.writeEndObject();
   }
}
