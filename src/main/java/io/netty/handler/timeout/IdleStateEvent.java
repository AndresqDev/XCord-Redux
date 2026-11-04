package io.netty.handler.timeout;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class IdleStateEvent {
   public static final IdleStateEvent FIRST_READER_IDLE_STATE_EVENT = new IdleStateEvent.DefaultIdleStateEvent(IdleState.READER_IDLE, true);
   public static final IdleStateEvent READER_IDLE_STATE_EVENT = new IdleStateEvent.DefaultIdleStateEvent(IdleState.READER_IDLE, false);
   public static final IdleStateEvent FIRST_WRITER_IDLE_STATE_EVENT = new IdleStateEvent.DefaultIdleStateEvent(IdleState.WRITER_IDLE, true);
   public static final IdleStateEvent WRITER_IDLE_STATE_EVENT = new IdleStateEvent.DefaultIdleStateEvent(IdleState.WRITER_IDLE, false);
   public static final IdleStateEvent FIRST_ALL_IDLE_STATE_EVENT = new IdleStateEvent.DefaultIdleStateEvent(IdleState.ALL_IDLE, true);
   public static final IdleStateEvent ALL_IDLE_STATE_EVENT = new IdleStateEvent.DefaultIdleStateEvent(IdleState.ALL_IDLE, false);
   private final IdleState state;
   private final boolean first;

   protected IdleStateEvent(IdleState var1, boolean var2) {
      this.state = ObjectUtil.checkNotNull(var1, "state");
      this.first = var2;
   }

   public IdleState state() {
      return this.state;
   }

   public boolean isFirst() {
      return this.first;
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this) + '(' + this.state + (this.first ? ", first" : "") + ')';
   }

   private static final class DefaultIdleStateEvent extends IdleStateEvent {
      private final String representation;

      DefaultIdleStateEvent(IdleState var1, boolean var2) {
         super(var1, var2);
         this.representation = "IdleStateEvent(" + var1 + (var2 ? ", first" : "") + ')';
      }

      @Override
      public String toString() {
         return this.representation;
      }
   }
}
