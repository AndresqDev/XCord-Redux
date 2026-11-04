package org.apache.commons.lang3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ThreadUtils {
   public static final ThreadUtils.AlwaysTruePredicate ALWAYS_TRUE_PREDICATE = new ThreadUtils.AlwaysTruePredicate();

   public static Thread findThreadById(long var0, ThreadGroup var2) {
      Validate.isTrue(var2 != null, "The thread group must not be null");
      Thread var3 = findThreadById(var0);
      return var3 != null && var2.equals(var3.getThreadGroup()) ? var3 : null;
   }

   public static Thread findThreadById(long var0, String var2) {
      Validate.isTrue(var2 != null, "The thread group name must not be null");
      Thread var3 = findThreadById(var0);
      return var3 != null && var3.getThreadGroup() != null && var3.getThreadGroup().getName().equals(var2) ? var3 : null;
   }

   public static Collection<Thread> findThreadsByName(String var0, ThreadGroup var1) {
      return findThreads(var1, false, new ThreadUtils.NamePredicate(var0));
   }

   public static Collection<Thread> findThreadsByName(String var0, String var1) {
      Validate.isTrue(var0 != null, "The thread name must not be null");
      Validate.isTrue(var1 != null, "The thread group name must not be null");
      Collection var2 = findThreadGroups(new ThreadUtils.NamePredicate(var1));
      if (var2.isEmpty()) {
         return Collections.emptyList();
      } else {
         ArrayList var3 = new ArrayList();
         ThreadUtils.NamePredicate var4 = new ThreadUtils.NamePredicate(var0);

         for (ThreadGroup var6 : var2) {
            var3.addAll(findThreads(var6, false, var4));
         }

         return Collections.unmodifiableCollection(var3);
      }
   }

   public static Collection<ThreadGroup> findThreadGroupsByName(String var0) {
      return findThreadGroups(new ThreadUtils.NamePredicate(var0));
   }

   public static Collection<ThreadGroup> getAllThreadGroups() {
      return findThreadGroups(ALWAYS_TRUE_PREDICATE);
   }

   public static ThreadGroup getSystemThreadGroup() {
      ThreadGroup var0 = Thread.currentThread().getThreadGroup();

      while (var0.getParent() != null) {
         var0 = var0.getParent();
      }

      return var0;
   }

   public static Collection<Thread> getAllThreads() {
      return findThreads(ALWAYS_TRUE_PREDICATE);
   }

   public static Collection<Thread> findThreadsByName(String var0) {
      return findThreads(new ThreadUtils.NamePredicate(var0));
   }

   public static Thread findThreadById(long var0) {
      Collection var2 = findThreads(new ThreadUtils.ThreadIdPredicate(var0));
      return var2.isEmpty() ? null : (Thread)var2.iterator().next();
   }

   public static Collection<Thread> findThreads(ThreadUtils.ThreadPredicate var0) {
      return findThreads(getSystemThreadGroup(), true, var0);
   }

   public static Collection<ThreadGroup> findThreadGroups(ThreadUtils.ThreadGroupPredicate var0) {
      return findThreadGroups(getSystemThreadGroup(), true, var0);
   }

   public static Collection<Thread> findThreads(ThreadGroup var0, boolean var1, ThreadUtils.ThreadPredicate var2) {
      Validate.isTrue(var0 != null, "The group must not be null");
      Validate.isTrue(var2 != null, "The predicate must not be null");
      int var3 = var0.activeCount();

      Thread[] var4;
      do {
         var4 = new Thread[var3 + var3 / 2 + 1];
         var3 = var0.enumerate(var4, var1);
      } while (var3 >= var4.length);

      ArrayList var5 = new ArrayList(var3);

      for (int var6 = 0; var6 < var3; var6++) {
         if (var2.test(var4[var6])) {
            var5.add(var4[var6]);
         }
      }

      return Collections.unmodifiableCollection(var5);
   }

   public static Collection<ThreadGroup> findThreadGroups(ThreadGroup var0, boolean var1, ThreadUtils.ThreadGroupPredicate var2) {
      Validate.isTrue(var0 != null, "The group must not be null");
      Validate.isTrue(var2 != null, "The predicate must not be null");
      int var3 = var0.activeGroupCount();

      ThreadGroup[] var4;
      do {
         var4 = new ThreadGroup[var3 + var3 / 2 + 1];
         var3 = var0.enumerate(var4, var1);
      } while (var3 >= var4.length);

      ArrayList var5 = new ArrayList(var3);

      for (int var6 = 0; var6 < var3; var6++) {
         if (var2.test(var4[var6])) {
            var5.add(var4[var6]);
         }
      }

      return Collections.unmodifiableCollection(var5);
   }

   private static final class AlwaysTruePredicate implements ThreadUtils.ThreadGroupPredicate, ThreadUtils.ThreadPredicate {
      private AlwaysTruePredicate() {
      }

      @Override
      public boolean test(ThreadGroup var1) {
         return true;
      }

      @Override
      public boolean test(Thread var1) {
         return true;
      }
   }

   public static class NamePredicate implements ThreadUtils.ThreadGroupPredicate, ThreadUtils.ThreadPredicate {
      private final String name;

      public NamePredicate(String var1) {
         Validate.isTrue(var1 != null, "The name must not be null");
         this.name = var1;
      }

      @Override
      public boolean test(ThreadGroup var1) {
         return var1 != null && var1.getName().equals(this.name);
      }

      @Override
      public boolean test(Thread var1) {
         return var1 != null && var1.getName().equals(this.name);
      }
   }

   public interface ThreadGroupPredicate {
      boolean test(ThreadGroup var1);
   }

   public static class ThreadIdPredicate implements ThreadUtils.ThreadPredicate {
      private final long threadId;

      public ThreadIdPredicate(long var1) {
         if (var1 <= 0L) {
            throw new IllegalArgumentException("The thread id must be greater than zero");
         } else {
            this.threadId = var1;
         }
      }

      @Override
      public boolean test(Thread var1) {
         return var1 != null && var1.getId() == this.threadId;
      }
   }

   public interface ThreadPredicate {
      boolean test(Thread var1);
   }
}
