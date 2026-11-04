package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MailingList implements Serializable, Cloneable, InputLocationTracker {
   private String name;
   private String subscribe;
   private String unsubscribe;
   private String post;
   private String archive;
   private List<String> otherArchives;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation nameLocation;
   private InputLocation subscribeLocation;
   private InputLocation unsubscribeLocation;
   private InputLocation postLocation;
   private InputLocation archiveLocation;
   private InputLocation otherArchivesLocation;

   public void addOtherArchive(String var1) {
      this.getOtherArchives().add(var1);
   }

   public MailingList clone() {
      try {
         MailingList var1 = (MailingList)super.clone();
         if (this.otherArchives != null) {
            var1.otherArchives = new ArrayList<>();
            var1.otherArchives.addAll(this.otherArchives);
         }

         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public String getArchive() {
      return this.archive;
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "name":
               return this.nameLocation;
            case "subscribe":
               return this.subscribeLocation;
            case "unsubscribe":
               return this.unsubscribeLocation;
            case "post":
               return this.postLocation;
            case "archive":
               return this.archiveLocation;
            case "otherArchives":
               return this.otherArchivesLocation;
            default:
               return this.getOtherLocation(var1);
         }
      } else {
         return this.getOtherLocation(var1);
      }
   }

   public String getName() {
      return this.name;
   }

   public List<String> getOtherArchives() {
      if (this.otherArchives == null) {
         this.otherArchives = new ArrayList<>();
      }

      return this.otherArchives;
   }

   @Override
   public void setLocation(Object var1, InputLocation var2) {
      if (var1 instanceof String) {
         String var3 = (String)var1;
         switch (var3) {
            case "":
               this.location = var2;
               return;
            case "name":
               this.nameLocation = var2;
               return;
            case "subscribe":
               this.subscribeLocation = var2;
               return;
            case "unsubscribe":
               this.unsubscribeLocation = var2;
               return;
            case "post":
               this.postLocation = var2;
               return;
            case "archive":
               this.archiveLocation = var2;
               return;
            case "otherArchives":
               this.otherArchivesLocation = var2;
               return;
            default:
               this.setOtherLocation(var1, var2);
         }
      } else {
         this.setOtherLocation(var1, var2);
      }
   }

   public void setOtherLocation(Object var1, InputLocation var2) {
      if (var2 != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap<>();
         }

         this.locations.put(var1, var2);
      }
   }

   private InputLocation getOtherLocation(Object var1) {
      return this.locations != null ? this.locations.get(var1) : null;
   }

   public String getPost() {
      return this.post;
   }

   public String getSubscribe() {
      return this.subscribe;
   }

   public String getUnsubscribe() {
      return this.unsubscribe;
   }

   public void removeOtherArchive(String var1) {
      this.getOtherArchives().remove(var1);
   }

   public void setArchive(String var1) {
      this.archive = var1;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setOtherArchives(List<String> var1) {
      this.otherArchives = var1;
   }

   public void setPost(String var1) {
      this.post = var1;
   }

   public void setSubscribe(String var1) {
      this.subscribe = var1;
   }

   public void setUnsubscribe(String var1) {
      this.unsubscribe = var1;
   }
}
