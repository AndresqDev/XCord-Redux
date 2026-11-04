package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DefaultHttpDataFactory implements HttpDataFactory {
   public static final long MINSIZE = 16384L;
   public static final long MAXSIZE = -1L;
   private final boolean useDisk;
   private final boolean checkSize;
   private long minSize;
   private long maxSize = -1L;
   private Charset charset = HttpConstants.DEFAULT_CHARSET;
   private String baseDir;
   private boolean deleteOnExit;
   private final Map<HttpRequest, List<HttpData>> requestFileDeleteMap = Collections.synchronizedMap(new IdentityHashMap<>());

   public DefaultHttpDataFactory() {
      this.useDisk = false;
      this.checkSize = true;
      this.minSize = 16384L;
   }

   public DefaultHttpDataFactory(Charset var1) {
      this();
      this.charset = var1;
   }

   public DefaultHttpDataFactory(boolean var1) {
      this.useDisk = var1;
      this.checkSize = false;
   }

   public DefaultHttpDataFactory(boolean var1, Charset var2) {
      this(var1);
      this.charset = var2;
   }

   public DefaultHttpDataFactory(long var1) {
      this.useDisk = false;
      this.checkSize = true;
      this.minSize = var1;
   }

   public DefaultHttpDataFactory(long var1, Charset var3) {
      this(var1);
      this.charset = var3;
   }

   public void setBaseDir(String var1) {
      this.baseDir = var1;
   }

   public void setDeleteOnExit(boolean var1) {
      this.deleteOnExit = var1;
   }

   @Override
   public void setMaxLimit(long var1) {
      this.maxSize = var1;
   }

   private List<HttpData> getList(HttpRequest var1) {
      Object var2 = this.requestFileDeleteMap.get(var1);
      if (var2 == null) {
         var2 = new ArrayList();
         this.requestFileDeleteMap.put(var1, (List<HttpData>)var2);
      }

      return (List<HttpData>)var2;
   }

   @Override
   public Attribute createAttribute(HttpRequest var1, String var2) {
      if (this.useDisk) {
         DiskAttribute var6 = new DiskAttribute(var2, this.charset, this.baseDir, this.deleteOnExit);
         var6.setMaxSize(this.maxSize);
         List var7 = this.getList(var1);
         var7.add(var6);
         return var6;
      } else if (this.checkSize) {
         MixedAttribute var5 = new MixedAttribute(var2, this.minSize, this.charset, this.baseDir, this.deleteOnExit);
         var5.setMaxSize(this.maxSize);
         List var4 = this.getList(var1);
         var4.add(var5);
         return var5;
      } else {
         MemoryAttribute var3 = new MemoryAttribute(var2);
         var3.setMaxSize(this.maxSize);
         return var3;
      }
   }

   @Override
   public Attribute createAttribute(HttpRequest var1, String var2, long var3) {
      if (this.useDisk) {
         DiskAttribute var8 = new DiskAttribute(var2, var3, this.charset, this.baseDir, this.deleteOnExit);
         var8.setMaxSize(this.maxSize);
         List var9 = this.getList(var1);
         var9.add(var8);
         return var8;
      } else if (this.checkSize) {
         MixedAttribute var7 = new MixedAttribute(var2, var3, this.minSize, this.charset, this.baseDir, this.deleteOnExit);
         var7.setMaxSize(this.maxSize);
         List var6 = this.getList(var1);
         var6.add(var7);
         return var7;
      } else {
         MemoryAttribute var5 = new MemoryAttribute(var2, var3);
         var5.setMaxSize(this.maxSize);
         return var5;
      }
   }

   private static void checkHttpDataSize(HttpData var0) {
      try {
         var0.checkSize(var0.length());
      } catch (IOException var2) {
         throw new IllegalArgumentException("Attribute bigger than maxSize allowed");
      }
   }

   @Override
   public Attribute createAttribute(HttpRequest var1, String var2, String var3) {
      if (this.useDisk) {
         Object var9;
         try {
            var9 = new DiskAttribute(var2, var3, this.charset, this.baseDir, this.deleteOnExit);
            ((Attribute)var9).setMaxSize(this.maxSize);
         } catch (IOException var6) {
            var9 = new MixedAttribute(var2, var3, this.minSize, this.charset, this.baseDir, this.deleteOnExit);
            ((Attribute)var9).setMaxSize(this.maxSize);
         }

         checkHttpDataSize((HttpData)var9);
         List var10 = this.getList(var1);
         var10.add(var9);
         return (Attribute)var9;
      } else if (this.checkSize) {
         MixedAttribute var8 = new MixedAttribute(var2, var3, this.minSize, this.charset, this.baseDir, this.deleteOnExit);
         var8.setMaxSize(this.maxSize);
         checkHttpDataSize(var8);
         List var5 = this.getList(var1);
         var5.add(var8);
         return var8;
      } else {
         try {
            MemoryAttribute var4 = new MemoryAttribute(var2, var3, this.charset);
            var4.setMaxSize(this.maxSize);
            checkHttpDataSize(var4);
            return var4;
         } catch (IOException var7) {
            throw new IllegalArgumentException(var7);
         }
      }
   }

   @Override
   public FileUpload createFileUpload(HttpRequest var1, String var2, String var3, String var4, String var5, Charset var6, long var7) {
      if (this.useDisk) {
         DiskFileUpload var12 = new DiskFileUpload(var2, var3, var4, var5, var6, var7, this.baseDir, this.deleteOnExit);
         var12.setMaxSize(this.maxSize);
         checkHttpDataSize(var12);
         List var13 = this.getList(var1);
         var13.add(var12);
         return var12;
      } else if (this.checkSize) {
         MixedFileUpload var11 = new MixedFileUpload(var2, var3, var4, var5, var6, var7, this.minSize, this.baseDir, this.deleteOnExit);
         var11.setMaxSize(this.maxSize);
         checkHttpDataSize(var11);
         List var10 = this.getList(var1);
         var10.add(var11);
         return var11;
      } else {
         MemoryFileUpload var9 = new MemoryFileUpload(var2, var3, var4, var5, var6, var7);
         var9.setMaxSize(this.maxSize);
         checkHttpDataSize(var9);
         return var9;
      }
   }

   @Override
   public void removeHttpDataFromClean(HttpRequest var1, InterfaceHttpData var2) {
      if (var2 instanceof HttpData) {
         List var3 = this.requestFileDeleteMap.get(var1);
         if (var3 != null) {
            Iterator var4 = var3.iterator();

            while (var4.hasNext()) {
               HttpData var5 = (HttpData)var4.next();
               if (var5 == var2) {
                  var4.remove();
                  if (var3.isEmpty()) {
                     this.requestFileDeleteMap.remove(var1);
                  }

                  return;
               }
            }
         }
      }
   }

   @Override
   public void cleanRequestHttpData(HttpRequest var1) {
      List var2 = this.requestFileDeleteMap.remove(var1);
      if (var2 != null) {
         for (HttpData var4 : var2) {
            var4.release();
         }
      }
   }

   @Override
   public void cleanAllHttpData() {
      Iterator var1 = this.requestFileDeleteMap.entrySet().iterator();

      while (var1.hasNext()) {
         Entry var2 = (Entry)var1.next();

         for (HttpData var5 : (List)var2.getValue()) {
            var5.release();
         }

         var1.remove();
      }
   }

   @Override
   public void cleanRequestHttpDatas(HttpRequest var1) {
      this.cleanRequestHttpData(var1);
   }

   @Override
   public void cleanAllHttpDatas() {
      this.cleanAllHttpData();
   }
}
