package org.apache.logging.log4j.core.filter;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LifeCycle2;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.PluginElement;

public abstract class AbstractFilterable extends AbstractLifeCycle implements Filterable {
   private volatile Filter filter;
   @PluginElement("Properties")
   private final Property[] propertyArray;

   protected AbstractFilterable() {
      this(null, Property.EMPTY_ARRAY);
   }

   protected AbstractFilterable(Filter var1) {
      this(var1, Property.EMPTY_ARRAY);
   }

   protected AbstractFilterable(Filter var1, Property[] var2) {
      this.filter = var1;
      this.propertyArray = var2 == null ? Property.EMPTY_ARRAY : var2;
   }

   @Override
   public synchronized void addFilter(Filter var1) {
      if (var1 != null) {
         if (this.filter == null) {
            this.filter = var1;
         } else if (this.filter instanceof CompositeFilter) {
            this.filter = ((CompositeFilter)this.filter).addFilter(var1);
         } else {
            Filter[] var2 = new Filter[]{this.filter, var1};
            this.filter = CompositeFilter.createFilters(var2);
         }
      }
   }

   @Override
   public Filter getFilter() {
      return this.filter;
   }

   @Override
   public boolean hasFilter() {
      return this.filter != null;
   }

   @Override
   public boolean isFiltered(LogEvent var1) {
      return this.filter != null && this.filter.filter(var1) == Filter.Result.DENY;
   }

   @Override
   public synchronized void removeFilter(Filter var1) {
      if (this.filter != null && var1 != null) {
         if (this.filter == var1 || this.filter.equals(var1)) {
            this.filter = null;
         } else if (this.filter instanceof CompositeFilter) {
            CompositeFilter var2 = (CompositeFilter)this.filter;
            var2 = var2.removeFilter(var1);
            if (var2.size() > 1) {
               this.filter = var2;
            } else if (var2.size() == 1) {
               Iterator var3 = var2.iterator();
               this.filter = (Filter)var3.next();
            } else {
               this.filter = null;
            }
         }
      }
   }

   @Override
   public void start() {
      this.setStarting();
      if (this.filter != null) {
         this.filter.start();
      }

      this.setStarted();
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      return this.stop(var1, var3, true);
   }

   protected boolean stop(long var1, TimeUnit var3, boolean var4) {
      if (var4) {
         this.setStopping();
      }

      boolean var5 = true;
      if (this.filter != null) {
         if (this.filter instanceof LifeCycle2) {
            var5 = ((LifeCycle2)this.filter).stop(var1, var3);
         } else {
            this.filter.stop();
            var5 = true;
         }
      }

      if (var4) {
         this.setStopped();
      }

      return var5;
   }

   public Property[] getPropertyArray() {
      return this.propertyArray;
   }

   public abstract static class Builder<B extends AbstractFilterable.Builder<B>> {
      @PluginElement("Filter")
      private Filter filter;
      @PluginElement("Properties")
      private Property[] propertyArray;

      public B asBuilder() {
         return (B)this;
      }

      public Filter getFilter() {
         return this.filter;
      }

      public Property[] getPropertyArray() {
         return this.propertyArray;
      }

      public B setFilter(Filter var1) {
         this.filter = var1;
         return this.asBuilder();
      }

      public B setPropertyArray(Property[] var1) {
         this.propertyArray = var1;
         return this.asBuilder();
      }

      @Deprecated
      public B withFilter(Filter var1) {
         return this.setFilter(var1);
      }
   }
}
