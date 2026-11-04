package org.apache.http.impl.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieIdentityComparator;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public class BasicCookieStore implements Serializable, CookieStore {
   private static final long serialVersionUID = -7581093305228232025L;
   private final TreeSet<Cookie> cookies = new TreeSet<>(new CookieIdentityComparator());
   private transient ReadWriteLock lock = new ReentrantReadWriteLock();

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.lock = new ReentrantReadWriteLock();
   }

   @Override
   public void addCookie(Cookie var1) {
      if (var1 != null) {
         this.lock.writeLock().lock();

         try {
            this.cookies.remove(var1);
            if (!var1.isExpired(new Date())) {
               this.cookies.add(var1);
            }
         } finally {
            this.lock.writeLock().unlock();
         }
      }
   }

   public void addCookies(Cookie[] var1) {
      if (var1 != null) {
         for (Cookie var5 : var1) {
            this.addCookie(var5);
         }
      }
   }

   @Override
   public List<Cookie> getCookies() {
      this.lock.readLock().lock();

      ArrayList var1;
      try {
         var1 = new ArrayList<>(this.cookies);
      } finally {
         this.lock.readLock().unlock();
      }

      return var1;
   }

   @Override
   public boolean clearExpired(Date var1) {
      if (var1 == null) {
         return false;
      } else {
         this.lock.writeLock().lock();

         boolean var7;
         try {
            boolean var2 = false;
            Iterator var3 = this.cookies.iterator();

            while (var3.hasNext()) {
               if (((Cookie)var3.next()).isExpired(var1)) {
                  var3.remove();
                  var2 = true;
               }
            }

            var7 = var2;
         } finally {
            this.lock.writeLock().unlock();
         }

         return var7;
      }
   }

   @Override
   public void clear() {
      this.lock.writeLock().lock();

      try {
         this.cookies.clear();
      } finally {
         this.lock.writeLock().unlock();
      }
   }

   @Override
   public String toString() {
      this.lock.readLock().lock();

      String var1;
      try {
         var1 = this.cookies.toString();
      } finally {
         this.lock.readLock().unlock();
      }

      return var1;
   }
}
