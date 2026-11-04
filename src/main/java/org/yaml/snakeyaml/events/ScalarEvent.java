package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

public final class ScalarEvent extends NodeEvent {
   private final String tag;
   private final DumperOptions.ScalarStyle style;
   private final String value;
   private final ImplicitTuple implicit;

   public ScalarEvent(String var1, String var2, ImplicitTuple var3, String var4, Mark var5, Mark var6, DumperOptions.ScalarStyle var7) {
      super(var1, var5, var6);
      this.tag = var2;
      this.implicit = var3;
      if (var4 == null) {
         throw new NullPointerException("Value must be provided.");
      } else {
         this.value = var4;
         if (var7 == null) {
            throw new NullPointerException("Style must be provided.");
         } else {
            this.style = var7;
         }
      }
   }

   @Deprecated
   public ScalarEvent(String var1, String var2, ImplicitTuple var3, String var4, Mark var5, Mark var6, Character var7) {
      this(var1, var2, var3, var4, var5, var6, DumperOptions.ScalarStyle.createStyle(var7));
   }

   public String getTag() {
      return this.tag;
   }

   public DumperOptions.ScalarStyle getScalarStyle() {
      return this.style;
   }

   @Deprecated
   public Character getStyle() {
      return this.style.getChar();
   }

   public String getValue() {
      return this.value;
   }

   public ImplicitTuple getImplicit() {
      return this.implicit;
   }

   @Override
   protected String getArguments() {
      return super.getArguments() + ", tag=" + this.tag + ", " + this.implicit + ", value=" + this.value;
   }

   @Override
   public Event.ID getEventId() {
      return Event.ID.Scalar;
   }

   public boolean isPlain() {
      return this.style == DumperOptions.ScalarStyle.PLAIN;
   }
}
