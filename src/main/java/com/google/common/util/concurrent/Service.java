package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotMock;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@DoNotMock("Create an AbstractIdleService")
@ElementTypesAreNonnullByDefault
@GwtIncompatible
public interface Service {
   @CanIgnoreReturnValue
   Service startAsync();

   boolean isRunning();

   Service.State state();

   @CanIgnoreReturnValue
   Service stopAsync();

   void awaitRunning();

   default void awaitRunning(Duration var1) throws TimeoutException {
      this.awaitRunning(Internal.toNanosSaturated(var1), TimeUnit.NANOSECONDS);
   }

   void awaitRunning(long var1, TimeUnit var3) throws TimeoutException;

   void awaitTerminated();

   default void awaitTerminated(Duration var1) throws TimeoutException {
      this.awaitTerminated(Internal.toNanosSaturated(var1), TimeUnit.NANOSECONDS);
   }

   void awaitTerminated(long var1, TimeUnit var3) throws TimeoutException;

   Throwable failureCause();

   void addListener(Service.Listener var1, Executor var2);

   public abstract static class Listener {
      public void starting() {
      }

      public void running() {
      }

      public void stopping(Service.State var1) {
      }

      public void terminated(Service.State var1) {
      }

      public void failed(Service.State var1, Throwable var2) {
      }
   }

   public static enum State {
      NEW,
      STARTING,
      RUNNING,
      STOPPING,
      TERMINATED,
      FAILED;
   }
}
