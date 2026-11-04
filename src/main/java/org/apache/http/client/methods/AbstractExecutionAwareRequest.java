package org.apache.http.client.methods;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicMarkableReference;
import org.apache.http.HttpRequest;
import org.apache.http.client.utils.CloneUtils;
import org.apache.http.concurrent.Cancellable;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionReleaseTrigger;
import org.apache.http.message.AbstractHttpMessage;

public abstract class AbstractExecutionAwareRequest extends AbstractHttpMessage implements Cloneable, HttpRequest, AbortableHttpRequest, HttpExecutionAware {
   private final AtomicMarkableReference<Cancellable> cancellableRef = new AtomicMarkableReference<>(null, false);

   protected AbstractExecutionAwareRequest() {
   }

   @Deprecated
   @Override
   public void setConnectionRequest(final ClientConnectionRequest var1) {
      this.setCancellable(new Cancellable() {
         @Override
         public boolean cancel() {
            var1.abortRequest();
            return true;
         }
      });
   }

   @Deprecated
   @Override
   public void setReleaseTrigger(final ConnectionReleaseTrigger var1) {
      this.setCancellable(new Cancellable() {
         @Override
         public boolean cancel() {
            try {
               var1.abortConnection();
               return true;
            } catch (IOException var2) {
               return false;
            }
         }
      });
   }

   @Override
   public void abort() {
      while (!this.cancellableRef.isMarked()) {
         Cancellable var1 = this.cancellableRef.getReference();
         if (this.cancellableRef.compareAndSet(var1, var1, false, true) && var1 != null) {
            var1.cancel();
         }
      }
   }

   @Override
   public boolean isAborted() {
      return this.cancellableRef.isMarked();
   }

   @Override
   public void setCancellable(Cancellable var1) {
      Cancellable var2 = this.cancellableRef.getReference();
      if (!this.cancellableRef.compareAndSet(var2, var1, false, false)) {
         var1.cancel();
      }
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      AbstractExecutionAwareRequest var1 = (AbstractExecutionAwareRequest)super.clone();
      var1.headergroup = CloneUtils.cloneObject(this.headergroup);
      var1.params = CloneUtils.cloneObject(this.params);
      return var1;
   }

   @Deprecated
   public void completed() {
      this.cancellableRef.set(null, false);
   }

   public void reset() {
      boolean var1;
      Cancellable var2;
      do {
         var1 = this.cancellableRef.isMarked();
         var2 = this.cancellableRef.getReference();
         if (var2 != null) {
            var2.cancel();
         }
      } while (!this.cancellableRef.compareAndSet(var2, null, var1, false));
   }
}
