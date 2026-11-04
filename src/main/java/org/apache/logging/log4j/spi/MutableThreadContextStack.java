package org.apache.logging.log4j.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.util.StringBuilderFormattable;

public class MutableThreadContextStack implements ThreadContextStack, StringBuilderFormattable {
   private static final long serialVersionUID = 50505011L;
   private final List<String> list;
   private boolean frozen;

   public MutableThreadContextStack() {
      this(new ArrayList<>());
   }

   public MutableThreadContextStack(List<String> var1) {
      this.list = new ArrayList<>(var1);
   }

   private MutableThreadContextStack(MutableThreadContextStack var1) {
      this.list = new ArrayList<>(var1.list);
   }

   private void checkInvariants() {
      if (this.frozen) {
         throw new UnsupportedOperationException("context stack has been frozen");
      }
   }

   @Override
   public String pop() {
      this.checkInvariants();
      if (this.list.isEmpty()) {
         return null;
      } else {
         int var1 = this.list.size() - 1;
         return this.list.remove(var1);
      }
   }

   @Override
   public String peek() {
      if (this.list.isEmpty()) {
         return null;
      } else {
         int var1 = this.list.size() - 1;
         return this.list.get(var1);
      }
   }

   @Override
   public void push(String var1) {
      this.checkInvariants();
      this.list.add(var1);
   }

   @Override
   public int getDepth() {
      return this.list.size();
   }

   @Override
   public List<String> asList() {
      return this.list;
   }

   @Override
   public void trim(int var1) {
      this.checkInvariants();
      if (var1 < 0) {
         throw new IllegalArgumentException("Maximum stack depth cannot be negative");
      } else if (this.list != null) {
         ArrayList var2 = new ArrayList(this.list.size());
         int var3 = Math.min(var1, this.list.size());

         for (int var4 = 0; var4 < var3; var4++) {
            var2.add(this.list.get(var4));
         }

         this.list.clear();
         this.list.addAll(var2);
      }
   }

   public ThreadContextStack copy() {
      return new MutableThreadContextStack(this);
   }

   @Override
   public void clear() {
      this.checkInvariants();
      this.list.clear();
   }

   @Override
   public int size() {
      return this.list.size();
   }

   @Override
   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   @Override
   public boolean contains(Object var1) {
      return this.list.contains(var1);
   }

   @Override
   public Iterator<String> iterator() {
      return this.list.iterator();
   }

   @Override
   public Object[] toArray() {
      return this.list.toArray();
   }

   @Override
   public <T> T[] toArray(T[] var1) {
      return (T[])this.list.toArray(var1);
   }

   public boolean add(String var1) {
      this.checkInvariants();
      return this.list.add(var1);
   }

   @Override
   public boolean remove(Object var1) {
      this.checkInvariants();
      return this.list.remove(var1);
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      return this.list.containsAll(var1);
   }

   @Override
   public boolean addAll(Collection<? extends String> var1) {
      this.checkInvariants();
      return this.list.addAll(var1);
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      this.checkInvariants();
      return this.list.removeAll(var1);
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      this.checkInvariants();
      return this.list.retainAll(var1);
   }

   @Override
   public String toString() {
      return String.valueOf(this.list);
   }

   @Override
   public void formatTo(StringBuilder var1) {
      var1.append('[');

      for (int var2 = 0; var2 < this.list.size(); var2++) {
         if (var2 > 0) {
            var1.append(',').append(' ');
         }

         var1.append(this.list.get(var2));
      }

      var1.append(']');
   }

   @Override
   public int hashCode() {
      return 31 + Objects.hashCode(this.list);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!(var1 instanceof ThreadContextStack)) {
         return false;
      } else {
         ThreadContextStack var2 = (ThreadContextStack)var1;
         List var3 = var2.asList();
         return Objects.equals(this.list, var3);
      }
   }

   @Override
   public ThreadContext.ContextStack getImmutableStackOrNull() {
      return this.copy();
   }

   public void freeze() {
      this.frozen = true;
   }

   public boolean isFrozen() {
      return this.frozen;
   }
}
