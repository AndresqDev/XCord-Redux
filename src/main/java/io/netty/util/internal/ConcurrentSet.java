package io.netty.util.internal;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

@Deprecated
public final class ConcurrentSet<E> extends AbstractSet<E> implements Serializable {
   private static final long serialVersionUID = -6761513279741915432L;
   private final ConcurrentMap<E, Boolean> map = PlatformDependent.newConcurrentHashMap();

   @Override
   public int size() {
      return this.map.size();
   }

   @Override
   public boolean contains(Object var1) {
      return this.map.containsKey(var1);
   }

   @Override
   public boolean add(E var1) {
      return this.map.putIfAbsent((E)var1, Boolean.TRUE) == null;
   }

   @Override
   public boolean remove(Object var1) {
      return this.map.remove(var1) != null;
   }

   @Override
   public void clear() {
      this.map.clear();
   }

   @Override
   public Iterator<E> iterator() {
      return this.map.keySet().iterator();
   }
}
