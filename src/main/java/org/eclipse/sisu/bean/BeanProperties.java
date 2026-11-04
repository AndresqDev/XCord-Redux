package org.eclipse.sisu.bean;

import java.lang.reflect.Member;
import java.util.Iterator;

public final class BeanProperties implements Iterable<BeanProperty<Object>> {
   private final Iterable<Member> members;

   public BeanProperties(Class<?> var1) {
      if (var1.isAnnotationPresent(IgnoreSetters.class)) {
         this.members = new DeclaredMembers(var1, DeclaredMembers.View.FIELDS);
      } else {
         this.members = new DeclaredMembers(var1, DeclaredMembers.View.METHODS, DeclaredMembers.View.FIELDS);
      }
   }

   BeanProperties(Iterable<Member> var1) {
      this.members = var1;
   }

   @Override
   public Iterator<BeanProperty<Object>> iterator() {
      return new BeanPropertyIterator<>(this.members);
   }
}
