package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

public final class SequenceStartEvent extends CollectionStartEvent {
   public SequenceStartEvent(String var1, String var2, boolean var3, Mark var4, Mark var5, DumperOptions.FlowStyle var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   @Deprecated
   public SequenceStartEvent(String var1, String var2, boolean var3, Mark var4, Mark var5, Boolean var6) {
      this(var1, var2, var3, var4, var5, DumperOptions.FlowStyle.fromBoolean(var6));
   }

   @Override
   public Event.ID getEventId() {
      return Event.ID.SequenceStart;
   }
}
