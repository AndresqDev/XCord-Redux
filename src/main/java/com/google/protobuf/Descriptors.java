package com.google.protobuf;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Logger;

public final class Descriptors {
   private static final Logger logger = Logger.getLogger(Descriptors.class.getName());

   private static String computeFullName(Descriptors.FileDescriptor var0, Descriptors.Descriptor var1, String var2) {
      if (var1 != null) {
         return var1.getFullName() + '.' + var2;
      } else {
         String var3 = var0.getPackage();
         return !var3.isEmpty() ? var3 + '.' + var2 : var2;
      }
   }

   public static final class Descriptor extends Descriptors.GenericDescriptor {
      private final int index;
      private DescriptorProtos.DescriptorProto proto;
      private final String fullName;
      private final Descriptors.FileDescriptor file;
      private final Descriptors.Descriptor containingType;
      private final Descriptors.Descriptor[] nestedTypes;
      private final Descriptors.EnumDescriptor[] enumTypes;
      private final Descriptors.FieldDescriptor[] fields;
      private final Descriptors.FieldDescriptor[] extensions;
      private final Descriptors.OneofDescriptor[] oneofs;

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.DescriptorProto toProto() {
         return this.proto;
      }

      @Override
      public String getName() {
         return this.proto.getName();
      }

      @Override
      public String getFullName() {
         return this.fullName;
      }

      @Override
      public Descriptors.FileDescriptor getFile() {
         return this.file;
      }

      public Descriptors.Descriptor getContainingType() {
         return this.containingType;
      }

      public DescriptorProtos.MessageOptions getOptions() {
         return this.proto.getOptions();
      }

      public List<Descriptors.FieldDescriptor> getFields() {
         return Collections.unmodifiableList(Arrays.asList(this.fields));
      }

      public List<Descriptors.OneofDescriptor> getOneofs() {
         return Collections.unmodifiableList(Arrays.asList(this.oneofs));
      }

      public List<Descriptors.FieldDescriptor> getExtensions() {
         return Collections.unmodifiableList(Arrays.asList(this.extensions));
      }

      public List<Descriptors.Descriptor> getNestedTypes() {
         return Collections.unmodifiableList(Arrays.asList(this.nestedTypes));
      }

      public List<Descriptors.EnumDescriptor> getEnumTypes() {
         return Collections.unmodifiableList(Arrays.asList(this.enumTypes));
      }

      public boolean isExtensionNumber(int var1) {
         for (DescriptorProtos.DescriptorProto.ExtensionRange var3 : this.proto.getExtensionRangeList()) {
            if (var3.getStart() <= var1 && var1 < var3.getEnd()) {
               return true;
            }
         }

         return false;
      }

      public boolean isReservedNumber(int var1) {
         for (DescriptorProtos.DescriptorProto.ReservedRange var3 : this.proto.getReservedRangeList()) {
            if (var3.getStart() <= var1 && var1 < var3.getEnd()) {
               return true;
            }
         }

         return false;
      }

      public boolean isReservedName(String var1) {
         Internal.checkNotNull(var1);

         for (String var3 : this.proto.getReservedNameList()) {
            if (var3.equals(var1)) {
               return true;
            }
         }

         return false;
      }

      public boolean isExtendable() {
         return this.proto.getExtensionRangeList().size() != 0;
      }

      public Descriptors.FieldDescriptor findFieldByName(String var1) {
         Descriptors.GenericDescriptor var2 = this.file.pool.findSymbol(this.fullName + '.' + var1);
         return var2 != null && var2 instanceof Descriptors.FieldDescriptor ? (Descriptors.FieldDescriptor)var2 : null;
      }

      public Descriptors.FieldDescriptor findFieldByNumber(int var1) {
         return this.file.pool.fieldsByNumber.get(new Descriptors.DescriptorPool.DescriptorIntPair(this, var1));
      }

      public Descriptors.Descriptor findNestedTypeByName(String var1) {
         Descriptors.GenericDescriptor var2 = this.file.pool.findSymbol(this.fullName + '.' + var1);
         return var2 != null && var2 instanceof Descriptors.Descriptor ? (Descriptors.Descriptor)var2 : null;
      }

      public Descriptors.EnumDescriptor findEnumTypeByName(String var1) {
         Descriptors.GenericDescriptor var2 = this.file.pool.findSymbol(this.fullName + '.' + var1);
         return var2 != null && var2 instanceof Descriptors.EnumDescriptor ? (Descriptors.EnumDescriptor)var2 : null;
      }

      Descriptor(String var1) throws Descriptors.DescriptorValidationException {
         String var2 = var1;
         String var3 = "";
         int var4 = var1.lastIndexOf(46);
         if (var4 != -1) {
            var2 = var1.substring(var4 + 1);
            var3 = var1.substring(0, var4);
         }

         this.index = 0;
         this.proto = DescriptorProtos.DescriptorProto.newBuilder()
            .setName(var2)
            .addExtensionRange(DescriptorProtos.DescriptorProto.ExtensionRange.newBuilder().setStart(1).setEnd(536870912).build())
            .build();
         this.fullName = var1;
         this.containingType = null;
         this.nestedTypes = new Descriptors.Descriptor[0];
         this.enumTypes = new Descriptors.EnumDescriptor[0];
         this.fields = new Descriptors.FieldDescriptor[0];
         this.extensions = new Descriptors.FieldDescriptor[0];
         this.oneofs = new Descriptors.OneofDescriptor[0];
         this.file = new Descriptors.FileDescriptor(var3, this);
      }

      private Descriptor(DescriptorProtos.DescriptorProto var1, Descriptors.FileDescriptor var2, Descriptors.Descriptor var3, int var4) throws Descriptors.DescriptorValidationException {
         this.index = var4;
         this.proto = var1;
         this.fullName = Descriptors.computeFullName(var2, var3, var1.getName());
         this.file = var2;
         this.containingType = var3;
         this.oneofs = new Descriptors.OneofDescriptor[var1.getOneofDeclCount()];

         for (int var5 = 0; var5 < var1.getOneofDeclCount(); var5++) {
            this.oneofs[var5] = new Descriptors.OneofDescriptor(var1.getOneofDecl(var5), var2, this, var5);
         }

         this.nestedTypes = new Descriptors.Descriptor[var1.getNestedTypeCount()];

         for (int var7 = 0; var7 < var1.getNestedTypeCount(); var7++) {
            this.nestedTypes[var7] = new Descriptors.Descriptor(var1.getNestedType(var7), var2, this, var7);
         }

         this.enumTypes = new Descriptors.EnumDescriptor[var1.getEnumTypeCount()];

         for (int var8 = 0; var8 < var1.getEnumTypeCount(); var8++) {
            this.enumTypes[var8] = new Descriptors.EnumDescriptor(var1.getEnumType(var8), var2, this, var8);
         }

         this.fields = new Descriptors.FieldDescriptor[var1.getFieldCount()];

         for (int var9 = 0; var9 < var1.getFieldCount(); var9++) {
            this.fields[var9] = new Descriptors.FieldDescriptor(var1.getField(var9), var2, this, var9, false);
         }

         this.extensions = new Descriptors.FieldDescriptor[var1.getExtensionCount()];

         for (int var10 = 0; var10 < var1.getExtensionCount(); var10++) {
            this.extensions[var10] = new Descriptors.FieldDescriptor(var1.getExtension(var10), var2, this, var10, true);
         }

         for (int var11 = 0; var11 < var1.getOneofDeclCount(); var11++) {
            this.oneofs[var11].fields = new Descriptors.FieldDescriptor[this.oneofs[var11].getFieldCount()];
            this.oneofs[var11].fieldCount = 0;
         }

         for (int var12 = 0; var12 < var1.getFieldCount(); var12++) {
            Descriptors.OneofDescriptor var6 = this.fields[var12].getContainingOneof();
            if (var6 != null) {
               var6.fields[var6.fieldCount++] = this.fields[var12];
            }
         }

         var2.pool.addSymbol(this);
      }

      private void crossLink() throws Descriptors.DescriptorValidationException {
         for (Descriptors.Descriptor var4 : this.nestedTypes) {
            var4.crossLink();
         }

         for (Descriptors.FieldDescriptor var11 : this.fields) {
            var11.crossLink();
         }

         for (Descriptors.FieldDescriptor var12 : this.extensions) {
            var12.crossLink();
         }
      }

      private void setProto(DescriptorProtos.DescriptorProto var1) {
         this.proto = var1;

         for (int var2 = 0; var2 < this.nestedTypes.length; var2++) {
            this.nestedTypes[var2].setProto(var1.getNestedType(var2));
         }

         for (int var3 = 0; var3 < this.oneofs.length; var3++) {
            this.oneofs[var3].setProto(var1.getOneofDecl(var3));
         }

         for (int var4 = 0; var4 < this.enumTypes.length; var4++) {
            this.enumTypes[var4].setProto(var1.getEnumType(var4));
         }

         for (int var5 = 0; var5 < this.fields.length; var5++) {
            this.fields[var5].setProto(var1.getField(var5));
         }

         for (int var6 = 0; var6 < this.extensions.length; var6++) {
            this.extensions[var6].setProto(var1.getExtension(var6));
         }
      }
   }

   private static final class DescriptorPool {
      private final Set<Descriptors.FileDescriptor> dependencies;
      private boolean allowUnknownDependencies;
      private final Map<String, Descriptors.GenericDescriptor> descriptorsByName = new HashMap<>();
      private final Map<Descriptors.DescriptorPool.DescriptorIntPair, Descriptors.FieldDescriptor> fieldsByNumber = new HashMap<>();
      private final Map<Descriptors.DescriptorPool.DescriptorIntPair, Descriptors.EnumValueDescriptor> enumValuesByNumber = new HashMap<>();

      DescriptorPool(Descriptors.FileDescriptor[] var1, boolean var2) {
         this.dependencies = new HashSet<>();
         this.allowUnknownDependencies = var2;

         for (int var3 = 0; var3 < var1.length; var3++) {
            this.dependencies.add(var1[var3]);
            this.importPublicDependencies(var1[var3]);
         }

         for (Descriptors.FileDescriptor var4 : this.dependencies) {
            try {
               this.addPackage(var4.getPackage(), var4);
            } catch (Descriptors.DescriptorValidationException var6) {
               throw new AssertionError(var6);
            }
         }
      }

      private void importPublicDependencies(Descriptors.FileDescriptor var1) {
         for (Descriptors.FileDescriptor var3 : var1.getPublicDependencies()) {
            if (this.dependencies.add(var3)) {
               this.importPublicDependencies(var3);
            }
         }
      }

      Descriptors.GenericDescriptor findSymbol(String var1) {
         return this.findSymbol(var1, Descriptors.DescriptorPool.SearchFilter.ALL_SYMBOLS);
      }

      Descriptors.GenericDescriptor findSymbol(String var1, Descriptors.DescriptorPool.SearchFilter var2) {
         Descriptors.GenericDescriptor var3 = this.descriptorsByName.get(var1);
         if (var3 == null
            || var2 != Descriptors.DescriptorPool.SearchFilter.ALL_SYMBOLS
               && (var2 != Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY || !this.isType(var3))
               && (var2 != Descriptors.DescriptorPool.SearchFilter.AGGREGATES_ONLY || !this.isAggregate(var3))) {
            for (Descriptors.FileDescriptor var5 : this.dependencies) {
               var3 = var5.pool.descriptorsByName.get(var1);
               if (var3 != null
                  && (
                     var2 == Descriptors.DescriptorPool.SearchFilter.ALL_SYMBOLS
                        || var2 == Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY && this.isType(var3)
                        || var2 == Descriptors.DescriptorPool.SearchFilter.AGGREGATES_ONLY && this.isAggregate(var3)
                  )) {
                  return var3;
               }
            }

            return null;
         } else {
            return var3;
         }
      }

      boolean isType(Descriptors.GenericDescriptor var1) {
         return var1 instanceof Descriptors.Descriptor || var1 instanceof Descriptors.EnumDescriptor;
      }

      boolean isAggregate(Descriptors.GenericDescriptor var1) {
         return var1 instanceof Descriptors.Descriptor
            || var1 instanceof Descriptors.EnumDescriptor
            || var1 instanceof Descriptors.DescriptorPool.PackageDescriptor
            || var1 instanceof Descriptors.ServiceDescriptor;
      }

      Descriptors.GenericDescriptor lookupSymbol(String var1, Descriptors.GenericDescriptor var2, Descriptors.DescriptorPool.SearchFilter var3) throws Descriptors.DescriptorValidationException {
         Descriptors.GenericDescriptor var4;
         String var5;
         if (var1.startsWith(".")) {
            var5 = var1.substring(1);
            var4 = this.findSymbol(var5, var3);
         } else {
            int var6 = var1.indexOf(46);
            String var7;
            if (var6 == -1) {
               var7 = var1;
            } else {
               var7 = var1.substring(0, var6);
            }

            StringBuilder var8 = new StringBuilder(var2.getFullName());

            while (true) {
               int var9 = var8.lastIndexOf(".");
               if (var9 == -1) {
                  var5 = var1;
                  var4 = this.findSymbol(var1, var3);
                  break;
               }

               var8.setLength(var9 + 1);
               var8.append(var7);
               var4 = this.findSymbol(var8.toString(), Descriptors.DescriptorPool.SearchFilter.AGGREGATES_ONLY);
               if (var4 != null) {
                  if (var6 != -1) {
                     var8.setLength(var9 + 1);
                     var8.append(var1);
                     var4 = this.findSymbol(var8.toString(), var3);
                  }

                  var5 = var8.toString();
                  break;
               }

               var8.setLength(var9);
            }
         }

         if (var4 == null) {
            if (this.allowUnknownDependencies && var3 == Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY) {
               Descriptors.logger.warning("The descriptor for message type \"" + var1 + "\" can not be found and a placeholder is created for it");
               Descriptors.Descriptor var10 = new Descriptors.Descriptor(var5);
               this.dependencies.add(var10.getFile());
               return var10;
            } else {
               throw new Descriptors.DescriptorValidationException(var2, '"' + var1 + "\" is not defined.");
            }
         } else {
            return var4;
         }
      }

      void addSymbol(Descriptors.GenericDescriptor var1) throws Descriptors.DescriptorValidationException {
         validateSymbolName(var1);
         String var2 = var1.getFullName();
         Descriptors.GenericDescriptor var3 = this.descriptorsByName.put(var2, var1);
         if (var3 != null) {
            this.descriptorsByName.put(var2, var3);
            if (var1.getFile() == var3.getFile()) {
               int var4 = var2.lastIndexOf(46);
               if (var4 == -1) {
                  throw new Descriptors.DescriptorValidationException(var1, '"' + var2 + "\" is already defined.");
               } else {
                  throw new Descriptors.DescriptorValidationException(
                     var1, '"' + var2.substring(var4 + 1) + "\" is already defined in \"" + var2.substring(0, var4) + "\"."
                  );
               }
            } else {
               throw new Descriptors.DescriptorValidationException(var1, '"' + var2 + "\" is already defined in file \"" + var3.getFile().getName() + "\".");
            }
         }
      }

      void addPackage(String var1, Descriptors.FileDescriptor var2) throws Descriptors.DescriptorValidationException {
         int var3 = var1.lastIndexOf(46);
         String var4;
         if (var3 == -1) {
            var4 = var1;
         } else {
            this.addPackage(var1.substring(0, var3), var2);
            var4 = var1.substring(var3 + 1);
         }

         Descriptors.GenericDescriptor var5 = this.descriptorsByName.put(var1, new Descriptors.DescriptorPool.PackageDescriptor(var4, var1, var2));
         if (var5 != null) {
            this.descriptorsByName.put(var1, var5);
            if (!(var5 instanceof Descriptors.DescriptorPool.PackageDescriptor)) {
               throw new Descriptors.DescriptorValidationException(
                  var2, '"' + var4 + "\" is already defined (as something other than a package) in file \"" + var5.getFile().getName() + "\"."
               );
            }
         }
      }

      void addFieldByNumber(Descriptors.FieldDescriptor var1) throws Descriptors.DescriptorValidationException {
         Descriptors.DescriptorPool.DescriptorIntPair var2 = new Descriptors.DescriptorPool.DescriptorIntPair(var1.getContainingType(), var1.getNumber());
         Descriptors.FieldDescriptor var3 = this.fieldsByNumber.put(var2, var1);
         if (var3 != null) {
            this.fieldsByNumber.put(var2, var3);
            throw new Descriptors.DescriptorValidationException(
               var1,
               "Field number "
                  + var1.getNumber()
                  + " has already been used in \""
                  + var1.getContainingType().getFullName()
                  + "\" by field \""
                  + var3.getName()
                  + "\"."
            );
         }
      }

      void addEnumValueByNumber(Descriptors.EnumValueDescriptor var1) {
         Descriptors.DescriptorPool.DescriptorIntPair var2 = new Descriptors.DescriptorPool.DescriptorIntPair(var1.getType(), var1.getNumber());
         Descriptors.EnumValueDescriptor var3 = this.enumValuesByNumber.put(var2, var1);
         if (var3 != null) {
            this.enumValuesByNumber.put(var2, var3);
         }
      }

      static void validateSymbolName(Descriptors.GenericDescriptor var0) throws Descriptors.DescriptorValidationException {
         String var1 = var0.getName();
         if (var1.length() == 0) {
            throw new Descriptors.DescriptorValidationException(var0, "Missing name.");
         } else {
            for (int var2 = 0; var2 < var1.length(); var2++) {
               char var3 = var1.charAt(var2);
               if (('a' > var3 || var3 > 'z') && ('A' > var3 || var3 > 'Z') && var3 != '_' && ('0' > var3 || var3 > '9' || var2 <= 0)) {
                  throw new Descriptors.DescriptorValidationException(var0, '"' + var1 + "\" is not a valid identifier.");
               }
            }
         }
      }

      private static final class DescriptorIntPair {
         private final Descriptors.GenericDescriptor descriptor;
         private final int number;

         DescriptorIntPair(Descriptors.GenericDescriptor var1, int var2) {
            this.descriptor = var1;
            this.number = var2;
         }

         @Override
         public int hashCode() {
            return this.descriptor.hashCode() * 65535 + this.number;
         }

         @Override
         public boolean equals(Object var1) {
            if (!(var1 instanceof Descriptors.DescriptorPool.DescriptorIntPair)) {
               return false;
            } else {
               Descriptors.DescriptorPool.DescriptorIntPair var2 = (Descriptors.DescriptorPool.DescriptorIntPair)var1;
               return this.descriptor == var2.descriptor && this.number == var2.number;
            }
         }
      }

      private static final class PackageDescriptor extends Descriptors.GenericDescriptor {
         private final String name;
         private final String fullName;
         private final Descriptors.FileDescriptor file;

         @Override
         public Message toProto() {
            return this.file.toProto();
         }

         @Override
         public String getName() {
            return this.name;
         }

         @Override
         public String getFullName() {
            return this.fullName;
         }

         @Override
         public Descriptors.FileDescriptor getFile() {
            return this.file;
         }

         PackageDescriptor(String var1, String var2, Descriptors.FileDescriptor var3) {
            this.file = var3;
            this.fullName = var2;
            this.name = var1;
         }
      }

      static enum SearchFilter {
         TYPES_ONLY,
         AGGREGATES_ONLY,
         ALL_SYMBOLS;
      }
   }

   public static class DescriptorValidationException extends Exception {
      private static final long serialVersionUID = 5750205775490483148L;
      private final String name;
      private final Message proto;
      private final String description;

      public String getProblemSymbolName() {
         return this.name;
      }

      public Message getProblemProto() {
         return this.proto;
      }

      public String getDescription() {
         return this.description;
      }

      private DescriptorValidationException(Descriptors.GenericDescriptor var1, String var2) {
         super(var1.getFullName() + ": " + var2);
         this.name = var1.getFullName();
         this.proto = var1.toProto();
         this.description = var2;
      }

      private DescriptorValidationException(Descriptors.GenericDescriptor var1, String var2, Throwable var3) {
         this(var1, var2);
         this.initCause(var3);
      }

      private DescriptorValidationException(Descriptors.FileDescriptor var1, String var2) {
         super(var1.getName() + ": " + var2);
         this.name = var1.getName();
         this.proto = var1.toProto();
         this.description = var2;
      }
   }

   public static final class EnumDescriptor extends Descriptors.GenericDescriptor implements Internal.EnumLiteMap<Descriptors.EnumValueDescriptor> {
      private final int index;
      private DescriptorProtos.EnumDescriptorProto proto;
      private final String fullName;
      private final Descriptors.FileDescriptor file;
      private final Descriptors.Descriptor containingType;
      private Descriptors.EnumValueDescriptor[] values;
      private final WeakHashMap<Integer, WeakReference<Descriptors.EnumValueDescriptor>> unknownValues = new WeakHashMap<>();

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.EnumDescriptorProto toProto() {
         return this.proto;
      }

      @Override
      public String getName() {
         return this.proto.getName();
      }

      @Override
      public String getFullName() {
         return this.fullName;
      }

      @Override
      public Descriptors.FileDescriptor getFile() {
         return this.file;
      }

      public Descriptors.Descriptor getContainingType() {
         return this.containingType;
      }

      public DescriptorProtos.EnumOptions getOptions() {
         return this.proto.getOptions();
      }

      public List<Descriptors.EnumValueDescriptor> getValues() {
         return Collections.unmodifiableList(Arrays.asList(this.values));
      }

      public Descriptors.EnumValueDescriptor findValueByName(String var1) {
         Descriptors.GenericDescriptor var2 = this.file.pool.findSymbol(this.fullName + '.' + var1);
         return var2 != null && var2 instanceof Descriptors.EnumValueDescriptor ? (Descriptors.EnumValueDescriptor)var2 : null;
      }

      public Descriptors.EnumValueDescriptor findValueByNumber(int var1) {
         return this.file.pool.enumValuesByNumber.get(new Descriptors.DescriptorPool.DescriptorIntPair(this, var1));
      }

      public Descriptors.EnumValueDescriptor findValueByNumberCreatingIfUnknown(int var1) {
         Descriptors.EnumValueDescriptor var2 = this.findValueByNumber(var1);
         if (var2 != null) {
            return var2;
         } else {
            synchronized (this) {
               Integer var4 = new Integer(var1);
               WeakReference var5 = this.unknownValues.get(var4);
               if (var5 != null) {
                  var2 = (Descriptors.EnumValueDescriptor)var5.get();
               }

               if (var2 == null) {
                  var2 = new Descriptors.EnumValueDescriptor(this.file, this, var4);
                  this.unknownValues.put(var4, new WeakReference<>(var2));
               }

               return var2;
            }
         }
      }

      int getUnknownEnumValueDescriptorCount() {
         return this.unknownValues.size();
      }

      private EnumDescriptor(DescriptorProtos.EnumDescriptorProto var1, Descriptors.FileDescriptor var2, Descriptors.Descriptor var3, int var4) throws Descriptors.DescriptorValidationException {
         this.index = var4;
         this.proto = var1;
         this.fullName = Descriptors.computeFullName(var2, var3, var1.getName());
         this.file = var2;
         this.containingType = var3;
         if (var1.getValueCount() == 0) {
            throw new Descriptors.DescriptorValidationException(this, "Enums must contain at least one value.");
         } else {
            this.values = new Descriptors.EnumValueDescriptor[var1.getValueCount()];

            for (int var5 = 0; var5 < var1.getValueCount(); var5++) {
               this.values[var5] = new Descriptors.EnumValueDescriptor(var1.getValue(var5), var2, this, var5);
            }

            var2.pool.addSymbol(this);
         }
      }

      private void setProto(DescriptorProtos.EnumDescriptorProto var1) {
         this.proto = var1;

         for (int var2 = 0; var2 < this.values.length; var2++) {
            this.values[var2].setProto(var1.getValue(var2));
         }
      }
   }

   public static final class EnumValueDescriptor extends Descriptors.GenericDescriptor implements Internal.EnumLite {
      private final int index;
      private DescriptorProtos.EnumValueDescriptorProto proto;
      private final String fullName;
      private final Descriptors.FileDescriptor file;
      private final Descriptors.EnumDescriptor type;

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.EnumValueDescriptorProto toProto() {
         return this.proto;
      }

      @Override
      public String getName() {
         return this.proto.getName();
      }

      @Override
      public int getNumber() {
         return this.proto.getNumber();
      }

      @Override
      public String toString() {
         return this.proto.getName();
      }

      @Override
      public String getFullName() {
         return this.fullName;
      }

      @Override
      public Descriptors.FileDescriptor getFile() {
         return this.file;
      }

      public Descriptors.EnumDescriptor getType() {
         return this.type;
      }

      public DescriptorProtos.EnumValueOptions getOptions() {
         return this.proto.getOptions();
      }

      private EnumValueDescriptor(DescriptorProtos.EnumValueDescriptorProto var1, Descriptors.FileDescriptor var2, Descriptors.EnumDescriptor var3, int var4) throws Descriptors.DescriptorValidationException {
         this.index = var4;
         this.proto = var1;
         this.file = var2;
         this.type = var3;
         this.fullName = var3.getFullName() + '.' + var1.getName();
         var2.pool.addSymbol(this);
         var2.pool.addEnumValueByNumber(this);
      }

      private EnumValueDescriptor(Descriptors.FileDescriptor var1, Descriptors.EnumDescriptor var2, Integer var3) {
         String var4 = "UNKNOWN_ENUM_VALUE_" + var2.getName() + "_" + var3;
         DescriptorProtos.EnumValueDescriptorProto var5 = DescriptorProtos.EnumValueDescriptorProto.newBuilder().setName(var4).setNumber(var3).build();
         this.index = -1;
         this.proto = var5;
         this.file = var1;
         this.type = var2;
         this.fullName = var2.getFullName() + '.' + var5.getName();
      }

      private void setProto(DescriptorProtos.EnumValueDescriptorProto var1) {
         this.proto = var1;
      }
   }

   public static final class FieldDescriptor
      extends Descriptors.GenericDescriptor
      implements FieldSet.FieldDescriptorLite<Descriptors.FieldDescriptor>,
      Comparable<Descriptors.FieldDescriptor> {
      private static final WireFormat.FieldType[] table = WireFormat.FieldType.values();
      private final int index;
      private DescriptorProtos.FieldDescriptorProto proto;
      private final String fullName;
      private final String jsonName;
      private final Descriptors.FileDescriptor file;
      private final Descriptors.Descriptor extensionScope;
      private Descriptors.FieldDescriptor.Type type;
      private Descriptors.Descriptor containingType;
      private Descriptors.Descriptor messageType;
      private Descriptors.OneofDescriptor containingOneof;
      private Descriptors.EnumDescriptor enumType;
      private Object defaultValue;

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.FieldDescriptorProto toProto() {
         return this.proto;
      }

      @Override
      public String getName() {
         return this.proto.getName();
      }

      @Override
      public int getNumber() {
         return this.proto.getNumber();
      }

      @Override
      public String getFullName() {
         return this.fullName;
      }

      public String getJsonName() {
         return this.jsonName;
      }

      public Descriptors.FieldDescriptor.JavaType getJavaType() {
         return this.type.getJavaType();
      }

      @Override
      public WireFormat.JavaType getLiteJavaType() {
         return this.getLiteType().getJavaType();
      }

      @Override
      public Descriptors.FileDescriptor getFile() {
         return this.file;
      }

      public Descriptors.FieldDescriptor.Type getType() {
         return this.type;
      }

      @Override
      public WireFormat.FieldType getLiteType() {
         return table[this.type.ordinal()];
      }

      public boolean needsUtf8Check() {
         if (this.type != Descriptors.FieldDescriptor.Type.STRING) {
            return false;
         } else if (this.getContainingType().getOptions().getMapEntry()) {
            return true;
         } else {
            return this.getFile().getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO3 ? true : this.getFile().getOptions().getJavaStringCheckUtf8();
         }
      }

      public boolean isMapField() {
         return this.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && this.isRepeated() && this.getMessageType().getOptions().getMapEntry();
      }

      public boolean isRequired() {
         return this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REQUIRED;
      }

      public boolean isOptional() {
         return this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL;
      }

      @Override
      public boolean isRepeated() {
         return this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;
      }

      @Override
      public boolean isPacked() {
         if (!this.isPackable()) {
            return false;
         } else {
            return this.getFile().getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO2
               ? this.getOptions().getPacked()
               : !this.getOptions().hasPacked() || this.getOptions().getPacked();
         }
      }

      public boolean isPackable() {
         return this.isRepeated() && this.getLiteType().isPackable();
      }

      public boolean hasDefaultValue() {
         return this.proto.hasDefaultValue();
      }

      public Object getDefaultValue() {
         if (this.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            throw new UnsupportedOperationException("FieldDescriptor.getDefaultValue() called on an embedded message field.");
         } else {
            return this.defaultValue;
         }
      }

      public DescriptorProtos.FieldOptions getOptions() {
         return this.proto.getOptions();
      }

      public boolean isExtension() {
         return this.proto.hasExtendee();
      }

      public Descriptors.Descriptor getContainingType() {
         return this.containingType;
      }

      public Descriptors.OneofDescriptor getContainingOneof() {
         return this.containingOneof;
      }

      public Descriptors.Descriptor getExtensionScope() {
         if (!this.isExtension()) {
            throw new UnsupportedOperationException(String.format("This field is not an extension. (%s)", this.fullName));
         } else {
            return this.extensionScope;
         }
      }

      public Descriptors.Descriptor getMessageType() {
         if (this.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            throw new UnsupportedOperationException(String.format("This field is not of message type. (%s)", this.fullName));
         } else {
            return this.messageType;
         }
      }

      public Descriptors.EnumDescriptor getEnumType() {
         if (this.getJavaType() != Descriptors.FieldDescriptor.JavaType.ENUM) {
            throw new UnsupportedOperationException(String.format("This field is not of enum type. (%s)", this.fullName));
         } else {
            return this.enumType;
         }
      }

      public int compareTo(Descriptors.FieldDescriptor var1) {
         if (var1.containingType != this.containingType) {
            throw new IllegalArgumentException("FieldDescriptors can only be compared to other FieldDescriptors for fields of the same message type.");
         } else {
            return this.getNumber() - var1.getNumber();
         }
      }

      @Override
      public String toString() {
         return this.getFullName();
      }

      private static String fieldNameToJsonName(String var0) {
         int var1 = var0.length();
         StringBuilder var2 = new StringBuilder(var1);
         boolean var3 = false;

         for (int var4 = 0; var4 < var1; var4++) {
            char var5 = var0.charAt(var4);
            if (var5 == '_') {
               var3 = true;
            } else if (var3) {
               if ('a' <= var5 && var5 <= 'z') {
                  var5 = (char)(var5 - 'a' + 65);
               }

               var2.append(var5);
               var3 = false;
            } else {
               var2.append(var5);
            }
         }

         return var2.toString();
      }

      private FieldDescriptor(DescriptorProtos.FieldDescriptorProto var1, Descriptors.FileDescriptor var2, Descriptors.Descriptor var3, int var4, boolean var5) throws Descriptors.DescriptorValidationException {
         this.index = var4;
         this.proto = var1;
         this.fullName = Descriptors.computeFullName(var2, var3, var1.getName());
         this.file = var2;
         if (var1.hasJsonName()) {
            this.jsonName = var1.getJsonName();
         } else {
            this.jsonName = fieldNameToJsonName(var1.getName());
         }

         if (var1.hasType()) {
            this.type = Descriptors.FieldDescriptor.Type.valueOf(var1.getType());
         }

         if (this.getNumber() <= 0) {
            throw new Descriptors.DescriptorValidationException(this, "Field numbers must be positive integers.");
         } else {
            if (var5) {
               if (!var1.hasExtendee()) {
                  throw new Descriptors.DescriptorValidationException(this, "FieldDescriptorProto.extendee not set for extension field.");
               }

               this.containingType = null;
               if (var3 != null) {
                  this.extensionScope = var3;
               } else {
                  this.extensionScope = null;
               }

               if (var1.hasOneofIndex()) {
                  throw new Descriptors.DescriptorValidationException(this, "FieldDescriptorProto.oneof_index set for extension field.");
               }

               this.containingOneof = null;
            } else {
               if (var1.hasExtendee()) {
                  throw new Descriptors.DescriptorValidationException(this, "FieldDescriptorProto.extendee set for non-extension field.");
               }

               this.containingType = var3;
               if (var1.hasOneofIndex()) {
                  if (var1.getOneofIndex() < 0 || var1.getOneofIndex() >= var3.toProto().getOneofDeclCount()) {
                     throw new Descriptors.DescriptorValidationException(this, "FieldDescriptorProto.oneof_index is out of range for type " + var3.getName());
                  }

                  this.containingOneof = var3.getOneofs().get(var1.getOneofIndex());
                  this.containingOneof.fieldCount++;
               } else {
                  this.containingOneof = null;
               }

               this.extensionScope = null;
            }

            var2.pool.addSymbol(this);
         }
      }

      private void crossLink() throws Descriptors.DescriptorValidationException {
         if (this.proto.hasExtendee()) {
            Descriptors.GenericDescriptor var1 = this.file
               .pool
               .lookupSymbol(this.proto.getExtendee(), this, Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY);
            if (!(var1 instanceof Descriptors.Descriptor)) {
               throw new Descriptors.DescriptorValidationException(this, '"' + this.proto.getExtendee() + "\" is not a message type.");
            }

            this.containingType = (Descriptors.Descriptor)var1;
            if (!this.getContainingType().isExtensionNumber(this.getNumber())) {
               throw new Descriptors.DescriptorValidationException(
                  this, '"' + this.getContainingType().getFullName() + "\" does not declare " + this.getNumber() + " as an extension number."
               );
            }
         }

         if (this.proto.hasTypeName()) {
            Descriptors.GenericDescriptor var4 = this.file
               .pool
               .lookupSymbol(this.proto.getTypeName(), this, Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY);
            if (!this.proto.hasType()) {
               if (var4 instanceof Descriptors.Descriptor) {
                  this.type = Descriptors.FieldDescriptor.Type.MESSAGE;
               } else {
                  if (!(var4 instanceof Descriptors.EnumDescriptor)) {
                     throw new Descriptors.DescriptorValidationException(this, '"' + this.proto.getTypeName() + "\" is not a type.");
                  }

                  this.type = Descriptors.FieldDescriptor.Type.ENUM;
               }
            }

            if (this.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
               if (!(var4 instanceof Descriptors.Descriptor)) {
                  throw new Descriptors.DescriptorValidationException(this, '"' + this.proto.getTypeName() + "\" is not a message type.");
               }

               this.messageType = (Descriptors.Descriptor)var4;
               if (this.proto.hasDefaultValue()) {
                  throw new Descriptors.DescriptorValidationException(this, "Messages can't have default values.");
               }
            } else {
               if (this.getJavaType() != Descriptors.FieldDescriptor.JavaType.ENUM) {
                  throw new Descriptors.DescriptorValidationException(this, "Field with primitive type has type_name.");
               }

               if (!(var4 instanceof Descriptors.EnumDescriptor)) {
                  throw new Descriptors.DescriptorValidationException(this, '"' + this.proto.getTypeName() + "\" is not an enum type.");
               }

               this.enumType = (Descriptors.EnumDescriptor)var4;
            }
         } else if (this.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE || this.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
            throw new Descriptors.DescriptorValidationException(this, "Field with message or enum type missing type_name.");
         }

         if (this.proto.getOptions().getPacked() && !this.isPackable()) {
            throw new Descriptors.DescriptorValidationException(this, "[packed = true] can only be specified for repeated primitive fields.");
         } else {
            if (this.proto.hasDefaultValue()) {
               if (this.isRepeated()) {
                  throw new Descriptors.DescriptorValidationException(this, "Repeated fields cannot have default values.");
               }

               try {
                  switch (this.getType()) {
                     case INT32:
                     case SINT32:
                     case SFIXED32:
                        this.defaultValue = TextFormat.parseInt32(this.proto.getDefaultValue());
                        break;
                     case UINT32:
                     case FIXED32:
                        this.defaultValue = TextFormat.parseUInt32(this.proto.getDefaultValue());
                        break;
                     case INT64:
                     case SINT64:
                     case SFIXED64:
                        this.defaultValue = TextFormat.parseInt64(this.proto.getDefaultValue());
                        break;
                     case UINT64:
                     case FIXED64:
                        this.defaultValue = TextFormat.parseUInt64(this.proto.getDefaultValue());
                        break;
                     case FLOAT:
                        if (this.proto.getDefaultValue().equals("inf")) {
                           this.defaultValue = Float.POSITIVE_INFINITY;
                        } else if (this.proto.getDefaultValue().equals("-inf")) {
                           this.defaultValue = Float.NEGATIVE_INFINITY;
                        } else if (this.proto.getDefaultValue().equals("nan")) {
                           this.defaultValue = Float.NaN;
                        } else {
                           this.defaultValue = Float.valueOf(this.proto.getDefaultValue());
                        }
                        break;
                     case DOUBLE:
                        if (this.proto.getDefaultValue().equals("inf")) {
                           this.defaultValue = Double.POSITIVE_INFINITY;
                        } else if (this.proto.getDefaultValue().equals("-inf")) {
                           this.defaultValue = Double.NEGATIVE_INFINITY;
                        } else if (this.proto.getDefaultValue().equals("nan")) {
                           this.defaultValue = Double.NaN;
                        } else {
                           this.defaultValue = Double.valueOf(this.proto.getDefaultValue());
                        }
                        break;
                     case BOOL:
                        this.defaultValue = Boolean.valueOf(this.proto.getDefaultValue());
                        break;
                     case STRING:
                        this.defaultValue = this.proto.getDefaultValue();
                        break;
                     case BYTES:
                        try {
                           this.defaultValue = TextFormat.unescapeBytes(this.proto.getDefaultValue());
                           break;
                        } catch (TextFormat.InvalidEscapeSequenceException var2) {
                           throw new Descriptors.DescriptorValidationException(this, "Couldn't parse default value: " + var2.getMessage(), var2);
                        }
                     case ENUM:
                        this.defaultValue = this.enumType.findValueByName(this.proto.getDefaultValue());
                        if (this.defaultValue == null) {
                           throw new Descriptors.DescriptorValidationException(this, "Unknown enum default value: \"" + this.proto.getDefaultValue() + '"');
                        }
                        break;
                     case MESSAGE:
                     case GROUP:
                        throw new Descriptors.DescriptorValidationException(this, "Message type had default value.");
                  }
               } catch (NumberFormatException var3) {
                  throw new Descriptors.DescriptorValidationException(this, "Could not parse default value: \"" + this.proto.getDefaultValue() + '"', var3);
               }
            } else if (this.isRepeated()) {
               this.defaultValue = Collections.emptyList();
            } else {
               switch (this.getJavaType()) {
                  case ENUM:
                     this.defaultValue = this.enumType.getValues().get(0);
                     break;
                  case MESSAGE:
                     this.defaultValue = null;
                     break;
                  default:
                     this.defaultValue = this.getJavaType().defaultDefault;
               }
            }

            if (!this.isExtension()) {
               this.file.pool.addFieldByNumber(this);
            }

            if (this.containingType != null && this.containingType.getOptions().getMessageSetWireFormat()) {
               if (!this.isExtension()) {
                  throw new Descriptors.DescriptorValidationException(this, "MessageSets cannot have fields, only extensions.");
               }

               if (!this.isOptional() || this.getType() != Descriptors.FieldDescriptor.Type.MESSAGE) {
                  throw new Descriptors.DescriptorValidationException(this, "Extensions of MessageSets must be optional messages.");
               }
            }
         }
      }

      private void setProto(DescriptorProtos.FieldDescriptorProto var1) {
         this.proto = var1;
      }

      @Override
      public MessageLite.Builder internalMergeFrom(MessageLite.Builder var1, MessageLite var2) {
         return ((Message.Builder)var1).mergeFrom((Message)var2);
      }

      static {
         if (Descriptors.FieldDescriptor.Type.values().length != DescriptorProtos.FieldDescriptorProto.Type.values().length) {
            throw new RuntimeException("descriptor.proto has a new declared type but Descriptors.java wasn't updated.");
         }
      }

      public static enum JavaType {
         INT(0),
         LONG(0L),
         FLOAT(0.0F),
         DOUBLE(0.0),
         BOOLEAN(false),
         STRING(""),
         BYTE_STRING(ByteString.EMPTY),
         ENUM(null),
         MESSAGE(null);

         private final Object defaultDefault;

         private JavaType(Object var3) {
            this.defaultDefault = var3;
         }
      }

      public static enum Type {
         DOUBLE(Descriptors.FieldDescriptor.JavaType.DOUBLE),
         FLOAT(Descriptors.FieldDescriptor.JavaType.FLOAT),
         INT64(Descriptors.FieldDescriptor.JavaType.LONG),
         UINT64(Descriptors.FieldDescriptor.JavaType.LONG),
         INT32(Descriptors.FieldDescriptor.JavaType.INT),
         FIXED64(Descriptors.FieldDescriptor.JavaType.LONG),
         FIXED32(Descriptors.FieldDescriptor.JavaType.INT),
         BOOL(Descriptors.FieldDescriptor.JavaType.BOOLEAN),
         STRING(Descriptors.FieldDescriptor.JavaType.STRING),
         GROUP(Descriptors.FieldDescriptor.JavaType.MESSAGE),
         MESSAGE(Descriptors.FieldDescriptor.JavaType.MESSAGE),
         BYTES(Descriptors.FieldDescriptor.JavaType.BYTE_STRING),
         UINT32(Descriptors.FieldDescriptor.JavaType.INT),
         ENUM(Descriptors.FieldDescriptor.JavaType.ENUM),
         SFIXED32(Descriptors.FieldDescriptor.JavaType.INT),
         SFIXED64(Descriptors.FieldDescriptor.JavaType.LONG),
         SINT32(Descriptors.FieldDescriptor.JavaType.INT),
         SINT64(Descriptors.FieldDescriptor.JavaType.LONG);

         private Descriptors.FieldDescriptor.JavaType javaType;

         private Type(Descriptors.FieldDescriptor.JavaType var3) {
            this.javaType = var3;
         }

         public DescriptorProtos.FieldDescriptorProto.Type toProto() {
            return DescriptorProtos.FieldDescriptorProto.Type.forNumber(this.ordinal() + 1);
         }

         public Descriptors.FieldDescriptor.JavaType getJavaType() {
            return this.javaType;
         }

         public static Descriptors.FieldDescriptor.Type valueOf(DescriptorProtos.FieldDescriptorProto.Type var0) {
            return values()[var0.getNumber() - 1];
         }
      }
   }

   public static final class FileDescriptor extends Descriptors.GenericDescriptor {
      private DescriptorProtos.FileDescriptorProto proto;
      private final Descriptors.Descriptor[] messageTypes;
      private final Descriptors.EnumDescriptor[] enumTypes;
      private final Descriptors.ServiceDescriptor[] services;
      private final Descriptors.FieldDescriptor[] extensions;
      private final Descriptors.FileDescriptor[] dependencies;
      private final Descriptors.FileDescriptor[] publicDependencies;
      private final Descriptors.DescriptorPool pool;

      public DescriptorProtos.FileDescriptorProto toProto() {
         return this.proto;
      }

      @Override
      public String getName() {
         return this.proto.getName();
      }

      @Override
      public Descriptors.FileDescriptor getFile() {
         return this;
      }

      @Override
      public String getFullName() {
         return this.proto.getName();
      }

      public String getPackage() {
         return this.proto.getPackage();
      }

      public DescriptorProtos.FileOptions getOptions() {
         return this.proto.getOptions();
      }

      public List<Descriptors.Descriptor> getMessageTypes() {
         return Collections.unmodifiableList(Arrays.asList(this.messageTypes));
      }

      public List<Descriptors.EnumDescriptor> getEnumTypes() {
         return Collections.unmodifiableList(Arrays.asList(this.enumTypes));
      }

      public List<Descriptors.ServiceDescriptor> getServices() {
         return Collections.unmodifiableList(Arrays.asList(this.services));
      }

      public List<Descriptors.FieldDescriptor> getExtensions() {
         return Collections.unmodifiableList(Arrays.asList(this.extensions));
      }

      public List<Descriptors.FileDescriptor> getDependencies() {
         return Collections.unmodifiableList(Arrays.asList(this.dependencies));
      }

      public List<Descriptors.FileDescriptor> getPublicDependencies() {
         return Collections.unmodifiableList(Arrays.asList(this.publicDependencies));
      }

      public Descriptors.FileDescriptor.Syntax getSyntax() {
         return Descriptors.FileDescriptor.Syntax.PROTO3.name.equals(this.proto.getSyntax())
            ? Descriptors.FileDescriptor.Syntax.PROTO3
            : Descriptors.FileDescriptor.Syntax.PROTO2;
      }

      public Descriptors.Descriptor findMessageTypeByName(String var1) {
         if (var1.indexOf(46) != -1) {
            return null;
         } else {
            String var2 = this.getPackage();
            if (!var2.isEmpty()) {
               var1 = var2 + '.' + var1;
            }

            Descriptors.GenericDescriptor var3 = this.pool.findSymbol(var1);
            return var3 != null && var3 instanceof Descriptors.Descriptor && var3.getFile() == this ? (Descriptors.Descriptor)var3 : null;
         }
      }

      public Descriptors.EnumDescriptor findEnumTypeByName(String var1) {
         if (var1.indexOf(46) != -1) {
            return null;
         } else {
            String var2 = this.getPackage();
            if (!var2.isEmpty()) {
               var1 = var2 + '.' + var1;
            }

            Descriptors.GenericDescriptor var3 = this.pool.findSymbol(var1);
            return var3 != null && var3 instanceof Descriptors.EnumDescriptor && var3.getFile() == this ? (Descriptors.EnumDescriptor)var3 : null;
         }
      }

      public Descriptors.ServiceDescriptor findServiceByName(String var1) {
         if (var1.indexOf(46) != -1) {
            return null;
         } else {
            String var2 = this.getPackage();
            if (!var2.isEmpty()) {
               var1 = var2 + '.' + var1;
            }

            Descriptors.GenericDescriptor var3 = this.pool.findSymbol(var1);
            return var3 != null && var3 instanceof Descriptors.ServiceDescriptor && var3.getFile() == this ? (Descriptors.ServiceDescriptor)var3 : null;
         }
      }

      public Descriptors.FieldDescriptor findExtensionByName(String var1) {
         if (var1.indexOf(46) != -1) {
            return null;
         } else {
            String var2 = this.getPackage();
            if (!var2.isEmpty()) {
               var1 = var2 + '.' + var1;
            }

            Descriptors.GenericDescriptor var3 = this.pool.findSymbol(var1);
            return var3 != null && var3 instanceof Descriptors.FieldDescriptor && var3.getFile() == this ? (Descriptors.FieldDescriptor)var3 : null;
         }
      }

      public static Descriptors.FileDescriptor buildFrom(DescriptorProtos.FileDescriptorProto var0, Descriptors.FileDescriptor[] var1) throws Descriptors.DescriptorValidationException {
         return buildFrom(var0, var1, false);
      }

      public static Descriptors.FileDescriptor buildFrom(DescriptorProtos.FileDescriptorProto var0, Descriptors.FileDescriptor[] var1, boolean var2) throws Descriptors.DescriptorValidationException {
         Descriptors.DescriptorPool var3 = new Descriptors.DescriptorPool(var1, var2);
         Descriptors.FileDescriptor var4 = new Descriptors.FileDescriptor(var0, var1, var3, var2);
         var4.crossLink();
         return var4;
      }

      private static byte[] latin1Cat(String[] var0) {
         if (var0.length == 1) {
            return var0[0].getBytes(Internal.ISO_8859_1);
         } else {
            StringBuilder var1 = new StringBuilder();

            for (String var5 : var0) {
               var1.append(var5);
            }

            return var1.toString().getBytes(Internal.ISO_8859_1);
         }
      }

      private static Descriptors.FileDescriptor[] findDescriptors(Class<?> var0, String[] var1, String[] var2) {
         ArrayList var3 = new ArrayList();

         for (int var4 = 0; var4 < var1.length; var4++) {
            try {
               Class var5 = var0.getClassLoader().loadClass(var1[var4]);
               var3.add((Descriptors.FileDescriptor)var5.getField("descriptor").get(null));
            } catch (Exception var6) {
               Descriptors.logger.warning("Descriptors for \"" + var2[var4] + "\" can not be found.");
            }
         }

         return var3.toArray(new Descriptors.FileDescriptor[0]);
      }

      @Deprecated
      public static void internalBuildGeneratedFileFrom(
         String[] var0, Descriptors.FileDescriptor[] var1, Descriptors.FileDescriptor.InternalDescriptorAssigner var2
      ) {
         byte[] var3 = latin1Cat(var0);

         DescriptorProtos.FileDescriptorProto var4;
         try {
            var4 = DescriptorProtos.FileDescriptorProto.parseFrom(var3);
         } catch (InvalidProtocolBufferException var10) {
            throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", var10);
         }

         Descriptors.FileDescriptor var5;
         try {
            var5 = buildFrom(var4, var1, true);
         } catch (Descriptors.DescriptorValidationException var9) {
            throw new IllegalArgumentException("Invalid embedded descriptor for \"" + var4.getName() + "\".", var9);
         }

         ExtensionRegistry var6 = var2.assignDescriptors(var5);
         if (var6 != null) {
            try {
               var4 = DescriptorProtos.FileDescriptorProto.parseFrom(var3, var6);
            } catch (InvalidProtocolBufferException var8) {
               throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", var8);
            }

            var5.setProto(var4);
         }
      }

      public static Descriptors.FileDescriptor internalBuildGeneratedFileFrom(String[] var0, Descriptors.FileDescriptor[] var1) {
         byte[] var2 = latin1Cat(var0);

         DescriptorProtos.FileDescriptorProto var3;
         try {
            var3 = DescriptorProtos.FileDescriptorProto.parseFrom(var2);
         } catch (InvalidProtocolBufferException var6) {
            throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", var6);
         }

         try {
            return buildFrom(var3, var1, true);
         } catch (Descriptors.DescriptorValidationException var5) {
            throw new IllegalArgumentException("Invalid embedded descriptor for \"" + var3.getName() + "\".", var5);
         }
      }

      @Deprecated
      public static void internalBuildGeneratedFileFrom(
         String[] var0, Class<?> var1, String[] var2, String[] var3, Descriptors.FileDescriptor.InternalDescriptorAssigner var4
      ) {
         Descriptors.FileDescriptor[] var5 = findDescriptors(var1, var2, var3);
         internalBuildGeneratedFileFrom(var0, var5, var4);
      }

      public static Descriptors.FileDescriptor internalBuildGeneratedFileFrom(String[] var0, Class<?> var1, String[] var2, String[] var3) {
         Descriptors.FileDescriptor[] var4 = findDescriptors(var1, var2, var3);
         return internalBuildGeneratedFileFrom(var0, var4);
      }

      public static void internalUpdateFileDescriptor(Descriptors.FileDescriptor var0, ExtensionRegistry var1) {
         ByteString var2 = var0.proto.toByteString();

         DescriptorProtos.FileDescriptorProto var3;
         try {
            var3 = DescriptorProtos.FileDescriptorProto.parseFrom(var2, var1);
         } catch (InvalidProtocolBufferException var5) {
            throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", var5);
         }

         var0.setProto(var3);
      }

      private FileDescriptor(DescriptorProtos.FileDescriptorProto var1, Descriptors.FileDescriptor[] var2, Descriptors.DescriptorPool var3, boolean var4) throws Descriptors.DescriptorValidationException {
         this.pool = var3;
         this.proto = var1;
         this.dependencies = (Descriptors.FileDescriptor[])var2.clone();
         HashMap var5 = new HashMap();

         for (Descriptors.FileDescriptor var9 : var2) {
            var5.put(var9.getName(), var9);
         }

         ArrayList var11 = new ArrayList();

         for (int var12 = 0; var12 < var1.getPublicDependencyCount(); var12++) {
            int var17 = var1.getPublicDependency(var12);
            if (var17 < 0 || var17 >= var1.getDependencyCount()) {
               throw new Descriptors.DescriptorValidationException(this, "Invalid public dependency index.");
            }

            String var18 = var1.getDependency(var17);
            Descriptors.FileDescriptor var10 = (Descriptors.FileDescriptor)var5.get(var18);
            if (var10 == null) {
               if (!var4) {
                  throw new Descriptors.DescriptorValidationException(this, "Invalid public dependency: " + var18);
               }
            } else {
               var11.add(var10);
            }
         }

         this.publicDependencies = new Descriptors.FileDescriptor[var11.size()];
         var11.toArray(this.publicDependencies);
         var3.addPackage(this.getPackage(), this);
         this.messageTypes = new Descriptors.Descriptor[var1.getMessageTypeCount()];

         for (int var13 = 0; var13 < var1.getMessageTypeCount(); var13++) {
            this.messageTypes[var13] = new Descriptors.Descriptor(var1.getMessageType(var13), this, null, var13);
         }

         this.enumTypes = new Descriptors.EnumDescriptor[var1.getEnumTypeCount()];

         for (int var14 = 0; var14 < var1.getEnumTypeCount(); var14++) {
            this.enumTypes[var14] = new Descriptors.EnumDescriptor(var1.getEnumType(var14), this, null, var14);
         }

         this.services = new Descriptors.ServiceDescriptor[var1.getServiceCount()];

         for (int var15 = 0; var15 < var1.getServiceCount(); var15++) {
            this.services[var15] = new Descriptors.ServiceDescriptor(var1.getService(var15), this, var15);
         }

         this.extensions = new Descriptors.FieldDescriptor[var1.getExtensionCount()];

         for (int var16 = 0; var16 < var1.getExtensionCount(); var16++) {
            this.extensions[var16] = new Descriptors.FieldDescriptor(var1.getExtension(var16), this, null, var16, true);
         }
      }

      FileDescriptor(String var1, Descriptors.Descriptor var2) throws Descriptors.DescriptorValidationException {
         this.pool = new Descriptors.DescriptorPool(new Descriptors.FileDescriptor[0], true);
         this.proto = DescriptorProtos.FileDescriptorProto.newBuilder()
            .setName(var2.getFullName() + ".placeholder.proto")
            .setPackage(var1)
            .addMessageType(var2.toProto())
            .build();
         this.dependencies = new Descriptors.FileDescriptor[0];
         this.publicDependencies = new Descriptors.FileDescriptor[0];
         this.messageTypes = new Descriptors.Descriptor[]{var2};
         this.enumTypes = new Descriptors.EnumDescriptor[0];
         this.services = new Descriptors.ServiceDescriptor[0];
         this.extensions = new Descriptors.FieldDescriptor[0];
         this.pool.addPackage(var1, this);
         this.pool.addSymbol(var2);
      }

      private void crossLink() throws Descriptors.DescriptorValidationException {
         for (Descriptors.Descriptor var4 : this.messageTypes) {
            var4.crossLink();
         }

         for (Descriptors.ServiceDescriptor var11 : this.services) {
            var11.crossLink();
         }

         for (Descriptors.FieldDescriptor var12 : this.extensions) {
            var12.crossLink();
         }
      }

      private void setProto(DescriptorProtos.FileDescriptorProto var1) {
         this.proto = var1;

         for (int var2 = 0; var2 < this.messageTypes.length; var2++) {
            this.messageTypes[var2].setProto(var1.getMessageType(var2));
         }

         for (int var3 = 0; var3 < this.enumTypes.length; var3++) {
            this.enumTypes[var3].setProto(var1.getEnumType(var3));
         }

         for (int var4 = 0; var4 < this.services.length; var4++) {
            this.services[var4].setProto(var1.getService(var4));
         }

         for (int var5 = 0; var5 < this.extensions.length; var5++) {
            this.extensions[var5].setProto(var1.getExtension(var5));
         }
      }

      boolean supportsUnknownEnumValue() {
         return this.getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO3;
      }

      @Deprecated
      public interface InternalDescriptorAssigner {
         ExtensionRegistry assignDescriptors(Descriptors.FileDescriptor var1);
      }

      public static enum Syntax {
         UNKNOWN("unknown"),
         PROTO2("proto2"),
         PROTO3("proto3");

         private final String name;

         private Syntax(String var3) {
            this.name = var3;
         }
      }
   }

   public abstract static class GenericDescriptor {
      private GenericDescriptor() {
      }

      public abstract Message toProto();

      public abstract String getName();

      public abstract String getFullName();

      public abstract Descriptors.FileDescriptor getFile();
   }

   public static final class MethodDescriptor extends Descriptors.GenericDescriptor {
      private final int index;
      private DescriptorProtos.MethodDescriptorProto proto;
      private final String fullName;
      private final Descriptors.FileDescriptor file;
      private final Descriptors.ServiceDescriptor service;
      private Descriptors.Descriptor inputType;
      private Descriptors.Descriptor outputType;

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.MethodDescriptorProto toProto() {
         return this.proto;
      }

      @Override
      public String getName() {
         return this.proto.getName();
      }

      @Override
      public String getFullName() {
         return this.fullName;
      }

      @Override
      public Descriptors.FileDescriptor getFile() {
         return this.file;
      }

      public Descriptors.ServiceDescriptor getService() {
         return this.service;
      }

      public Descriptors.Descriptor getInputType() {
         return this.inputType;
      }

      public Descriptors.Descriptor getOutputType() {
         return this.outputType;
      }

      public boolean isClientStreaming() {
         return this.proto.getClientStreaming();
      }

      public boolean isServerStreaming() {
         return this.proto.getServerStreaming();
      }

      public DescriptorProtos.MethodOptions getOptions() {
         return this.proto.getOptions();
      }

      private MethodDescriptor(DescriptorProtos.MethodDescriptorProto var1, Descriptors.FileDescriptor var2, Descriptors.ServiceDescriptor var3, int var4) throws Descriptors.DescriptorValidationException {
         this.index = var4;
         this.proto = var1;
         this.file = var2;
         this.service = var3;
         this.fullName = var3.getFullName() + '.' + var1.getName();
         var2.pool.addSymbol(this);
      }

      private void crossLink() throws Descriptors.DescriptorValidationException {
         Descriptors.GenericDescriptor var1 = this.file.pool.lookupSymbol(this.proto.getInputType(), this, Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY);
         if (!(var1 instanceof Descriptors.Descriptor)) {
            throw new Descriptors.DescriptorValidationException(this, '"' + this.proto.getInputType() + "\" is not a message type.");
         } else {
            this.inputType = (Descriptors.Descriptor)var1;
            Descriptors.GenericDescriptor var2 = this.file
               .pool
               .lookupSymbol(this.proto.getOutputType(), this, Descriptors.DescriptorPool.SearchFilter.TYPES_ONLY);
            if (!(var2 instanceof Descriptors.Descriptor)) {
               throw new Descriptors.DescriptorValidationException(this, '"' + this.proto.getOutputType() + "\" is not a message type.");
            } else {
               this.outputType = (Descriptors.Descriptor)var2;
            }
         }
      }

      private void setProto(DescriptorProtos.MethodDescriptorProto var1) {
         this.proto = var1;
      }
   }

   public static final class OneofDescriptor extends Descriptors.GenericDescriptor {
      private final int index;
      private DescriptorProtos.OneofDescriptorProto proto;
      private final String fullName;
      private final Descriptors.FileDescriptor file;
      private Descriptors.Descriptor containingType;
      private int fieldCount;
      private Descriptors.FieldDescriptor[] fields;

      public int getIndex() {
         return this.index;
      }

      @Override
      public String getName() {
         return this.proto.getName();
      }

      @Override
      public Descriptors.FileDescriptor getFile() {
         return this.file;
      }

      @Override
      public String getFullName() {
         return this.fullName;
      }

      public Descriptors.Descriptor getContainingType() {
         return this.containingType;
      }

      public int getFieldCount() {
         return this.fieldCount;
      }

      public DescriptorProtos.OneofOptions getOptions() {
         return this.proto.getOptions();
      }

      public List<Descriptors.FieldDescriptor> getFields() {
         return Collections.unmodifiableList(Arrays.asList(this.fields));
      }

      public Descriptors.FieldDescriptor getField(int var1) {
         return this.fields[var1];
      }

      public DescriptorProtos.OneofDescriptorProto toProto() {
         return this.proto;
      }

      private void setProto(DescriptorProtos.OneofDescriptorProto var1) {
         this.proto = var1;
      }

      private OneofDescriptor(DescriptorProtos.OneofDescriptorProto var1, Descriptors.FileDescriptor var2, Descriptors.Descriptor var3, int var4) throws Descriptors.DescriptorValidationException {
         this.proto = var1;
         this.fullName = Descriptors.computeFullName(var2, var3, var1.getName());
         this.file = var2;
         this.index = var4;
         this.containingType = var3;
         this.fieldCount = 0;
      }
   }

   public static final class ServiceDescriptor extends Descriptors.GenericDescriptor {
      private final int index;
      private DescriptorProtos.ServiceDescriptorProto proto;
      private final String fullName;
      private final Descriptors.FileDescriptor file;
      private Descriptors.MethodDescriptor[] methods;

      public int getIndex() {
         return this.index;
      }

      public DescriptorProtos.ServiceDescriptorProto toProto() {
         return this.proto;
      }

      @Override
      public String getName() {
         return this.proto.getName();
      }

      @Override
      public String getFullName() {
         return this.fullName;
      }

      @Override
      public Descriptors.FileDescriptor getFile() {
         return this.file;
      }

      public DescriptorProtos.ServiceOptions getOptions() {
         return this.proto.getOptions();
      }

      public List<Descriptors.MethodDescriptor> getMethods() {
         return Collections.unmodifiableList(Arrays.asList(this.methods));
      }

      public Descriptors.MethodDescriptor findMethodByName(String var1) {
         Descriptors.GenericDescriptor var2 = this.file.pool.findSymbol(this.fullName + '.' + var1);
         return var2 != null && var2 instanceof Descriptors.MethodDescriptor ? (Descriptors.MethodDescriptor)var2 : null;
      }

      private ServiceDescriptor(DescriptorProtos.ServiceDescriptorProto var1, Descriptors.FileDescriptor var2, int var3) throws Descriptors.DescriptorValidationException {
         this.index = var3;
         this.proto = var1;
         this.fullName = Descriptors.computeFullName(var2, null, var1.getName());
         this.file = var2;
         this.methods = new Descriptors.MethodDescriptor[var1.getMethodCount()];

         for (int var4 = 0; var4 < var1.getMethodCount(); var4++) {
            this.methods[var4] = new Descriptors.MethodDescriptor(var1.getMethod(var4), var2, this, var4);
         }

         var2.pool.addSymbol(this);
      }

      private void crossLink() throws Descriptors.DescriptorValidationException {
         for (Descriptors.MethodDescriptor var4 : this.methods) {
            var4.crossLink();
         }
      }

      private void setProto(DescriptorProtos.ServiceDescriptorProto var1) {
         this.proto = var1;

         for (int var2 = 0; var2 < this.methods.length; var2++) {
            this.methods[var2].setProto(var1.getMethod(var2));
         }
      }
   }
}
