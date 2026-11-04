package com.google.protobuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TextFormatParseInfoTree {
   private Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locationsFromField;
   Map<Descriptors.FieldDescriptor, List<TextFormatParseInfoTree>> subtreesFromField;

   private TextFormatParseInfoTree(
      Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> var1, Map<Descriptors.FieldDescriptor, List<TextFormatParseInfoTree.Builder>> var2
   ) {
      HashMap var3 = new HashMap();

      for (Entry var5 : var1.entrySet()) {
         var3.put(var5.getKey(), Collections.unmodifiableList((List)var5.getValue()));
      }

      this.locationsFromField = Collections.unmodifiableMap(var3);
      HashMap var10 = new HashMap();

      for (Entry var6 : var2.entrySet()) {
         ArrayList var7 = new ArrayList();

         for (TextFormatParseInfoTree.Builder var9 : (List)var6.getValue()) {
            var7.add(var9.build());
         }

         var10.put(var6.getKey(), Collections.unmodifiableList(var7));
      }

      this.subtreesFromField = Collections.unmodifiableMap(var10);
   }

   public List<TextFormatParseLocation> getLocations(Descriptors.FieldDescriptor var1) {
      List var2 = this.locationsFromField.get(var1);
      return var2 == null ? Collections.emptyList() : var2;
   }

   public TextFormatParseLocation getLocation(Descriptors.FieldDescriptor var1, int var2) {
      return getFromList(this.getLocations(var1), var2, var1);
   }

   public List<TextFormatParseInfoTree> getNestedTrees(Descriptors.FieldDescriptor var1) {
      List var2 = this.subtreesFromField.get(var1);
      return var2 == null ? Collections.emptyList() : var2;
   }

   public TextFormatParseInfoTree getNestedTree(Descriptors.FieldDescriptor var1, int var2) {
      return getFromList(this.getNestedTrees(var1), var2, var1);
   }

   public static TextFormatParseInfoTree.Builder builder() {
      return new TextFormatParseInfoTree.Builder();
   }

   private static <T> T getFromList(List<T> var0, int var1, Descriptors.FieldDescriptor var2) {
      if (var1 < var0.size() && var1 >= 0) {
         return (T)var0.get(var1);
      } else {
         throw new IllegalArgumentException(String.format("Illegal index field: %s, index %d", var2 == null ? "<null>" : var2.getName(), var1));
      }
   }

   public static class Builder {
      private Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locationsFromField = new HashMap<>();
      private Map<Descriptors.FieldDescriptor, List<TextFormatParseInfoTree.Builder>> subtreeBuildersFromField = new HashMap<>();

      private Builder() {
      }

      public TextFormatParseInfoTree.Builder setLocation(Descriptors.FieldDescriptor var1, TextFormatParseLocation var2) {
         Object var3 = this.locationsFromField.get(var1);
         if (var3 == null) {
            var3 = new ArrayList();
            this.locationsFromField.put(var1, (List<TextFormatParseLocation>)var3);
         }

         var3.add(var2);
         return this;
      }

      public TextFormatParseInfoTree.Builder getBuilderForSubMessageField(Descriptors.FieldDescriptor var1) {
         Object var2 = this.subtreeBuildersFromField.get(var1);
         if (var2 == null) {
            var2 = new ArrayList();
            this.subtreeBuildersFromField.put(var1, (List<TextFormatParseInfoTree.Builder>)var2);
         }

         TextFormatParseInfoTree.Builder var3 = new TextFormatParseInfoTree.Builder();
         var2.add(var3);
         return var3;
      }

      public TextFormatParseInfoTree build() {
         return new TextFormatParseInfoTree(this.locationsFromField, this.subtreeBuildersFromField);
      }
   }
}
