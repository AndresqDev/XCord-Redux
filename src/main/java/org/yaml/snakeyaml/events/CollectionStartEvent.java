package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

public abstract class CollectionStartEvent extends NodeEvent {
   private final String tag;
   private final boolean implicit;
   private final DumperOptions.FlowStyle flowStyle;

   public CollectionStartEvent(String var1, String var2, boolean var3, Mark var4, Mark var5, DumperOptions.FlowStyle var6) {
      super(var1, var4, var5);
      this.tag = var2;
      this.implicit = var3;
      if (var6 == null) {
         throw new NullPointerException("Flow style must be provided.");
      } else {
         this.flowStyle = var6;
      }
   }

   @Deprecated
   public CollectionStartEvent(String var1, String var2, boolean var3, Mark var4, Mark var5, Boolean var6) {
      this(var1, var2, var3, var4, var5, DumperOptions.FlowStyle.fromBoolean(var6));
   }

   public String getTag() {
      return this.tag;
   }

   public boolean getImplicit() {
      return this.implicit;
   }

   public DumperOptions.FlowStyle getFlowStyle() {
      return this.flowStyle;
   }

   @Override
   protected String getArguments() {
      return super.getArguments() + ", tag=" + this.tag + ", implicit=" + this.implicit;
   }

   public boolean isFlow() {
      return DumperOptions.FlowStyle.FLOW == this.flowStyle;
   }
}
