package org.eclipse.sisu.bean;

import java.lang.reflect.Member;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class DeclaredMembers implements Iterable<Member> {
   private final Class<?> clazz;
   private final DeclaredMembers.View[] views;

   public DeclaredMembers(Class<?> var1, DeclaredMembers.View... var2) {
      this.clazz = var1;
      this.views = var2.length == 0 ? DeclaredMembers.View.values() : var2;
   }

   @Override
   public Iterator<Member> iterator() {
      return new DeclaredMembers.MemberIterator(this.clazz, this.views);
   }

   private static final class MemberIterator implements Iterator<Member> {
      private static final Member[] NO_MEMBERS = new Member[0];
      private Class<?> clazz;
      private final DeclaredMembers.View[] views;
      private int viewIndex;
      private Member[] members = NO_MEMBERS;
      private int memberIndex;

      MemberIterator(Class<?> var1, DeclaredMembers.View[] var2) {
         this.clazz = var1;
         this.views = var2;
      }

      @Override
      public boolean hasNext() {
         while (this.memberIndex <= 0) {
            if (this.viewIndex >= this.views.length) {
               this.clazz = this.clazz.getSuperclass();
               this.viewIndex = 0;
            }

            if (this.clazz == null || this.clazz == Object.class) {
               return false;
            }

            int var1 = this.viewIndex++;
            this.members = this.views[var1].members(this.clazz);
            this.memberIndex = this.members.length;
         }

         return true;
      }

      public Member next() {
         if (this.hasNext()) {
            return this.members[--this.memberIndex];
         } else {
            throw new NoSuchElementException();
         }
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   public static enum View {
      CONSTRUCTORS {
         @Override
         Member[] members(Class<?> var1) {
            return var1.getDeclaredConstructors();
         }
      },
      METHODS {
         @Override
         Member[] members(Class<?> var1) {
            return var1.getDeclaredMethods();
         }
      },
      FIELDS {
         @Override
         Member[] members(Class<?> var1) {
            return var1.getDeclaredFields();
         }
      };

      private View() {
      }

      abstract Member[] members(Class<?> var1);
   }
}
