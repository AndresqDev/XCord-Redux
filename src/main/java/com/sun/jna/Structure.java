package com.sun.jna;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.Buffer;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class Structure {
   public static final int ALIGN_DEFAULT = 0;
   public static final int ALIGN_NONE = 1;
   public static final int ALIGN_GNUC = 2;
   public static final int ALIGN_MSVC = 3;
   protected static final int CALCULATE_SIZE = -1;
   static final Map<Class<?>, Structure.LayoutInfo> layoutInfo = new WeakHashMap<>();
   static final Map<Class<?>, List<String>> fieldOrder = new WeakHashMap<>();
   private Pointer memory;
   private int size = -1;
   private int alignType;
   private String encoding;
   private int actualAlignType;
   private int structAlignment;
   private Map<String, Structure.StructField> structFields;
   private final Map<String, Object> nativeStrings = new HashMap<>();
   private TypeMapper typeMapper;
   private long typeInfo;
   private boolean autoRead = true;
   private boolean autoWrite = true;
   private Structure[] array;
   private boolean readCalled;
   private static final ThreadLocal<Map<Pointer, Structure>> reads = new ThreadLocal<Map<Pointer, Structure>>() {
      protected synchronized Map<Pointer, Structure> initialValue() {
         return new HashMap<>();
      }
   };
   private static final ThreadLocal<Set<Structure>> busy = new ThreadLocal<Set<Structure>>() {
      protected synchronized Set<Structure> initialValue() {
         return new Structure.StructureSet();
      }
   };
   private static final Pointer PLACEHOLDER_MEMORY = new Pointer(0L) {
      @Override
      public Pointer share(long var1, long var3) {
         return this;
      }
   };

   protected Structure() {
      this(0);
   }

   protected Structure(TypeMapper var1) {
      this(null, 0, var1);
   }

   protected Structure(int var1) {
      this(null, var1);
   }

   protected Structure(int var1, TypeMapper var2) {
      this(null, var1, var2);
   }

   protected Structure(Pointer var1) {
      this(var1, 0);
   }

   protected Structure(Pointer var1, int var2) {
      this(var1, var2, null);
   }

   protected Structure(Pointer var1, int var2, TypeMapper var3) {
      this.setAlignType(var2);
      this.setStringEncoding(Native.getStringEncoding(this.getClass()));
      this.initializeTypeMapper(var3);
      this.validateFields();
      if (var1 != null) {
         this.useMemory(var1, 0, true);
      } else {
         this.allocateMemory(-1);
      }

      this.initializeFields();
   }

   Map<String, Structure.StructField> fields() {
      return this.structFields;
   }

   TypeMapper getTypeMapper() {
      return this.typeMapper;
   }

   private void initializeTypeMapper(TypeMapper var1) {
      if (var1 == null) {
         var1 = Native.getTypeMapper(this.getClass());
      }

      this.typeMapper = var1;
      this.layoutChanged();
   }

   private void layoutChanged() {
      if (this.size != -1) {
         this.size = -1;
         if (this.memory instanceof Structure.AutoAllocated) {
            this.memory = null;
         }

         this.ensureAllocated();
      }
   }

   protected void setStringEncoding(String var1) {
      this.encoding = var1;
   }

   protected String getStringEncoding() {
      return this.encoding;
   }

   protected void setAlignType(int var1) {
      this.alignType = var1;
      if (var1 == 0) {
         var1 = Native.getStructureAlignment(this.getClass());
         if (var1 == 0) {
            if (Platform.isWindows()) {
               var1 = 3;
            } else {
               var1 = 2;
            }
         }
      }

      this.actualAlignType = var1;
      this.layoutChanged();
   }

   protected Memory autoAllocate(int var1) {
      return new Structure.AutoAllocated(var1);
   }

   protected void useMemory(Pointer var1) {
      this.useMemory(var1, 0);
   }

   protected void useMemory(Pointer var1, int var2) {
      this.useMemory(var1, var2, false);
   }

   void useMemory(Pointer var1, int var2, boolean var3) {
      try {
         this.nativeStrings.clear();
         if (this instanceof Structure.ByValue && !var3) {
            byte[] var4 = new byte[this.size()];
            var1.read(0L, var4, 0, var4.length);
            this.memory.write(0L, var4, 0, var4.length);
         } else {
            this.memory = var1.share((long)var2);
            if (this.size == -1) {
               this.size = this.calculateSize(false);
            }

            if (this.size != -1) {
               this.memory = var1.share((long)var2, (long)this.size);
            }
         }

         this.array = null;
         this.readCalled = false;
      } catch (IndexOutOfBoundsException var5) {
         throw new IllegalArgumentException("Structure exceeds provided memory bounds", var5);
      }
   }

   protected void ensureAllocated() {
      this.ensureAllocated(false);
   }

   private void ensureAllocated(boolean var1) {
      if (this.memory == null) {
         this.allocateMemory(var1);
      } else if (this.size == -1) {
         this.size = this.calculateSize(true, var1);
         if (!(this.memory instanceof Structure.AutoAllocated)) {
            try {
               this.memory = this.memory.share(0L, (long)this.size);
            } catch (IndexOutOfBoundsException var3) {
               throw new IllegalArgumentException("Structure exceeds provided memory bounds", var3);
            }
         }
      }
   }

   protected void allocateMemory() {
      this.allocateMemory(false);
   }

   private void allocateMemory(boolean var1) {
      this.allocateMemory(this.calculateSize(true, var1));
   }

   protected void allocateMemory(int var1) {
      if (var1 == -1) {
         var1 = this.calculateSize(false);
      } else if (var1 <= 0) {
         throw new IllegalArgumentException("Structure size must be greater than zero: " + var1);
      }

      if (var1 != -1) {
         if (this.memory == null || this.memory instanceof Structure.AutoAllocated) {
            this.memory = this.autoAllocate(var1);
         }

         this.size = var1;
      }
   }

   public int size() {
      this.ensureAllocated();
      return this.size;
   }

   public void clear() {
      this.ensureAllocated();
      this.memory.clear((long)this.size());
   }

   public Pointer getPointer() {
      this.ensureAllocated();
      return this.memory;
   }

   static Set<Structure> busy() {
      return busy.get();
   }

   static Map<Pointer, Structure> reading() {
      return reads.get();
   }

   void conditionalAutoRead() {
      if (!this.readCalled) {
         this.autoRead();
      }
   }

   public void read() {
      if (this.memory != PLACEHOLDER_MEMORY) {
         this.readCalled = true;
         this.ensureAllocated();
         if (!busy().contains(this)) {
            busy().add(this);
            if (this instanceof Structure.ByReference) {
               reading().put(this.getPointer(), this);
            }

            try {
               for (Structure.StructField var2 : this.fields().values()) {
                  this.readField(var2);
               }
            } finally {
               busy().remove(this);
               if (reading().get(this.getPointer()) == this) {
                  reading().remove(this.getPointer());
               }
            }
         }
      }
   }

   protected int fieldOffset(String var1) {
      this.ensureAllocated();
      Structure.StructField var2 = this.fields().get(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("No such field: " + var1);
      } else {
         return var2.offset;
      }
   }

   public Object readField(String var1) {
      this.ensureAllocated();
      Structure.StructField var2 = this.fields().get(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("No such field: " + var1);
      } else {
         return this.readField(var2);
      }
   }

   Object getFieldValue(Field var1) {
      try {
         return var1.get(this);
      } catch (Exception var3) {
         throw new Error("Exception reading field '" + var1.getName() + "' in " + this.getClass(), var3);
      }
   }

   void setFieldValue(Field var1, Object var2) {
      this.setFieldValue(var1, var2, false);
   }

   private void setFieldValue(Field var1, Object var2, boolean var3) {
      try {
         var1.set(this, var2);
      } catch (IllegalAccessException var6) {
         int var5 = var1.getModifiers();
         if (Modifier.isFinal(var5)) {
            if (var3) {
               throw new UnsupportedOperationException(
                  "This VM does not support Structures with final fields (field '" + var1.getName() + "' within " + this.getClass() + ")", var6
               );
            } else {
               throw new UnsupportedOperationException("Attempt to write to read-only field '" + var1.getName() + "' within " + this.getClass(), var6);
            }
         } else {
            throw new Error("Unexpectedly unable to write to field '" + var1.getName() + "' within " + this.getClass(), var6);
         }
      }
   }

   static Structure updateStructureByReference(Class<?> var0, Structure var1, Pointer var2) {
      if (var2 == null) {
         var1 = null;
      } else if (var1 != null && var2.equals(var1.getPointer())) {
         var1.autoRead();
      } else {
         Structure var3 = reading().get(var2);
         if (var3 != null && var0.equals(var3.getClass())) {
            var1 = var3;
            var3.autoRead();
         } else {
            var1 = newInstance(var0, var2);
            var1.conditionalAutoRead();
         }
      }

      return var1;
   }

   protected Object readField(Structure.StructField var1) {
      int var2 = var1.offset;
      Class var3 = var1.type;
      FromNativeConverter var4 = var1.readConverter;
      if (var4 != null) {
         var3 = var4.nativeType();
      }

      Object var5 = !Structure.class.isAssignableFrom(var3)
            && !Callback.class.isAssignableFrom(var3)
            && (!Platform.HAS_BUFFERS || !Buffer.class.isAssignableFrom(var3))
            && !Pointer.class.isAssignableFrom(var3)
            && !NativeMapped.class.isAssignableFrom(var3)
            && !var3.isArray()
         ? null
         : this.getFieldValue(var1.field);
      Object var6;
      if (var3 == String.class) {
         Pointer var7 = this.memory.getPointer((long)var2);
         var6 = var7 == null ? null : var7.getString(0L, this.encoding);
      } else {
         var6 = this.memory.getValue((long)var2, var3, var5);
      }

      if (var4 != null) {
         var6 = var4.fromNative(var6, var1.context);
         if (var5 != null && var5.equals(var6)) {
            var6 = var5;
         }
      }

      if (var3.equals(String.class) || var3.equals(WString.class)) {
         this.nativeStrings.put(var1.name + ".ptr", this.memory.getPointer((long)var2));
         this.nativeStrings.put(var1.name + ".val", var6);
      }

      this.setFieldValue(var1.field, var6, true);
      return var6;
   }

   public void write() {
      if (this.memory != PLACEHOLDER_MEMORY) {
         this.ensureAllocated();
         if (this instanceof Structure.ByValue) {
            this.getTypeInfo();
         }

         if (!busy().contains(this)) {
            busy().add(this);

            try {
               for (Structure.StructField var2 : this.fields().values()) {
                  if (!var2.isVolatile) {
                     this.writeField(var2);
                  }
               }
            } finally {
               busy().remove(this);
            }
         }
      }
   }

   public void writeField(String var1) {
      this.ensureAllocated();
      Structure.StructField var2 = this.fields().get(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("No such field: " + var1);
      } else {
         this.writeField(var2);
      }
   }

   public void writeField(String var1, Object var2) {
      this.ensureAllocated();
      Structure.StructField var3 = this.fields().get(var1);
      if (var3 == null) {
         throw new IllegalArgumentException("No such field: " + var1);
      } else {
         this.setFieldValue(var3.field, var2);
         this.writeField(var3);
      }
   }

   protected void writeField(Structure.StructField var1) {
      if (!var1.isReadOnly) {
         int var2 = var1.offset;
         Object var3 = this.getFieldValue(var1.field);
         Class var4 = var1.type;
         ToNativeConverter var5 = var1.writeConverter;
         if (var5 != null) {
            var3 = var5.toNative(var3, new StructureWriteContext(this, var1.field));
            var4 = var5.nativeType();
         }

         if (String.class == var4 || WString.class == var4) {
            boolean var6 = var4 == WString.class;
            if (var3 != null) {
               if (this.nativeStrings.containsKey(var1.name + ".ptr") && var3.equals(this.nativeStrings.get(var1.name + ".val"))) {
                  return;
               }

               NativeString var7 = var6 ? new NativeString(var3.toString(), true) : new NativeString(var3.toString(), this.encoding);
               this.nativeStrings.put(var1.name, var7);
               var3 = var7.getPointer();
            } else {
               this.nativeStrings.remove(var1.name);
            }

            this.nativeStrings.remove(var1.name + ".ptr");
            this.nativeStrings.remove(var1.name + ".val");
         }

         try {
            this.memory.setValue((long)var2, var3, var4);
         } catch (IllegalArgumentException var8) {
            String var9 = "Structure field \""
               + var1.name
               + "\" was declared as "
               + var1.type
               + (var1.type == var4 ? "" : " (native type " + var4 + ")")
               + ", which is not supported within a Structure";
            throw new IllegalArgumentException(var9, var8);
         }
      }
   }

   protected abstract List<String> getFieldOrder();

   @Deprecated
   protected final void setFieldOrder(String[] var1) {
      throw new Error("This method is obsolete, use getFieldOrder() instead");
   }

   protected void sortFields(List<Field> var1, List<String> var2) {
      for (int var3 = 0; var3 < var2.size(); var3++) {
         String var4 = (String)var2.get(var3);

         for (int var5 = 0; var5 < var1.size(); var5++) {
            Field var6 = (Field)var1.get(var5);
            if (var4.equals(var6.getName())) {
               Collections.swap(var1, var3, var5);
               break;
            }
         }
      }
   }

   protected List<Field> getFieldList() {
      ArrayList var1 = new ArrayList();

      for (Class var2 = this.getClass(); !var2.equals(Structure.class); var2 = var2.getSuperclass()) {
         ArrayList var3 = new ArrayList();
         Field[] var4 = var2.getDeclaredFields();

         for (int var5 = 0; var5 < var4.length; var5++) {
            int var6 = var4[var5].getModifiers();
            if (!Modifier.isStatic(var6) && Modifier.isPublic(var6)) {
               var3.add(var4[var5]);
            }
         }

         var1.addAll(0, var3);
      }

      return var1;
   }

   private List<String> fieldOrder() {
      Class var1 = this.getClass();
      synchronized (fieldOrder) {
         List var3 = fieldOrder.get(var1);
         if (var3 == null) {
            var3 = this.getFieldOrder();
            fieldOrder.put(var1, var3);
         }

         return var3;
      }
   }

   public static List<String> createFieldsOrder(List<String> var0, String... var1) {
      return createFieldsOrder(var0, Arrays.asList(var1));
   }

   public static List<String> createFieldsOrder(List<String> var0, List<String> var1) {
      ArrayList var2 = new ArrayList(var0.size() + var1.size());
      var2.addAll(var0);
      var2.addAll(var1);
      return Collections.unmodifiableList(var2);
   }

   public static List<String> createFieldsOrder(String var0) {
      return Collections.unmodifiableList(Collections.singletonList(var0));
   }

   public static List<String> createFieldsOrder(String... var0) {
      return Collections.unmodifiableList(Arrays.asList(var0));
   }

   private static <T extends Comparable<T>> List<T> sort(Collection<? extends T> var0) {
      ArrayList var1 = new ArrayList(var0);
      Collections.sort(var1);
      return var1;
   }

   protected List<Field> getFields(boolean var1) {
      List var2 = this.getFieldList();
      HashSet var3 = new HashSet();

      for (Field var5 : var2) {
         var3.add(var5.getName());
      }

      List var6 = this.fieldOrder();
      if (var6.size() == var2.size() || var2.size() <= 1) {
         HashSet var7 = new HashSet(var6);
         if (!var7.equals(var3)) {
            throw new Error(
               "Structure.getFieldOrder() on "
                  + this.getClass()
                  + " returns names ("
                  + sort(var6)
                  + ") which do not match declared field names ("
                  + sort(var3)
                  + ")"
            );
         } else {
            this.sortFields(var2, var6);
            return var2;
         }
      } else if (var1) {
         throw new Error(
            "Structure.getFieldOrder() on "
               + this.getClass()
               + " does not provide enough names ["
               + var6.size()
               + "] ("
               + sort(var6)
               + ") to match declared fields ["
               + var2.size()
               + "] ("
               + sort(var3)
               + ")"
         );
      } else {
         return null;
      }
   }

   protected int calculateSize(boolean var1) {
      return this.calculateSize(var1, false);
   }

   static int size(Class<?> var0) {
      return size(var0, null);
   }

   static int size(Class<?> var0, Structure var1) {
      Structure.LayoutInfo var2;
      synchronized (layoutInfo) {
         var2 = layoutInfo.get(var0);
      }

      int var6 = var2 != null && !var2.variable ? var2.size : -1;
      if (var6 == -1) {
         if (var1 == null) {
            var1 = newInstance(var0, PLACEHOLDER_MEMORY);
         }

         var6 = var1.size();
      }

      return var6;
   }

   int calculateSize(boolean var1, boolean var2) {
      int var3 = -1;
      Class var4 = this.getClass();
      Structure.LayoutInfo var5;
      synchronized (layoutInfo) {
         var5 = layoutInfo.get(var4);
      }

      if (var5 == null || this.alignType != var5.alignType || this.typeMapper != var5.typeMapper) {
         var5 = this.deriveLayout(var1, var2);
      }

      if (var5 != null) {
         this.structAlignment = var5.alignment;
         this.structFields = var5.fields;
         if (!var5.variable) {
            synchronized (layoutInfo) {
               if (!layoutInfo.containsKey(var4) || this.alignType != 0 || this.typeMapper != null) {
                  layoutInfo.put(var4, var5);
               }
            }
         }

         var3 = var5.size;
      }

      return var3;
   }

   private void validateField(String var1, Class<?> var2) {
      if (this.typeMapper != null) {
         ToNativeConverter var3 = this.typeMapper.getToNativeConverter(var2);
         if (var3 != null) {
            this.validateField(var1, var3.nativeType());
            return;
         }
      }

      if (var2.isArray()) {
         this.validateField(var1, var2.getComponentType());
      } else {
         try {
            this.getNativeSize(var2);
         } catch (IllegalArgumentException var5) {
            String var4 = "Invalid Structure field in " + this.getClass() + ", field name '" + var1 + "' (" + var2 + "): " + var5.getMessage();
            throw new IllegalArgumentException(var4, var5);
         }
      }
   }

   private void validateFields() {
      for (Field var3 : this.getFieldList()) {
         this.validateField(var3.getName(), var3.getType());
      }
   }

   private Structure.LayoutInfo deriveLayout(boolean var1, boolean var2) {
      int var3 = 0;
      List var4 = this.getFields(var1);
      if (var4 == null) {
         return null;
      } else {
         Structure.LayoutInfo var5 = new Structure.LayoutInfo();
         var5.alignType = this.alignType;
         var5.typeMapper = this.typeMapper;
         boolean var6 = true;

         for (Field var8 : var4) {
            int var9 = var8.getModifiers();
            Class var10 = var8.getType();
            if (var10.isArray()) {
               var5.variable = true;
            }

            Structure.StructField var11 = new Structure.StructField();
            var11.isVolatile = Modifier.isVolatile(var9);
            var11.isReadOnly = Modifier.isFinal(var9);
            if (var11.isReadOnly) {
               if (!Platform.RO_FIELDS) {
                  throw new IllegalArgumentException(
                     "This VM does not support read-only fields (field '" + var8.getName() + "' within " + this.getClass() + ")"
                  );
               }

               var8.setAccessible(true);
            }

            var11.field = var8;
            var11.name = var8.getName();
            var11.type = var10;
            if (Callback.class.isAssignableFrom(var10) && !var10.isInterface()) {
               throw new IllegalArgumentException("Structure Callback field '" + var8.getName() + "' must be an interface");
            }

            if (var10.isArray() && Structure.class.equals(var10.getComponentType())) {
               String var21 = "Nested Structure arrays must use a derived Structure type so that the size of the elements can be determined";
               throw new IllegalArgumentException(var21);
            }

            int var12 = 1;
            if (Modifier.isPublic(var8.getModifiers())) {
               Object var13 = this.getFieldValue(var11.field);
               if (var13 == null && var10.isArray()) {
                  if (var1) {
                     throw new IllegalStateException("Array fields must be initialized");
                  }

                  return null;
               }

               Class var14 = var10;
               if (NativeMapped.class.isAssignableFrom(var10)) {
                  NativeMappedConverter var22 = NativeMappedConverter.getInstance(var10);
                  var14 = var22.nativeType();
                  var11.writeConverter = var22;
                  var11.readConverter = var22;
                  var11.context = new StructureReadContext(this, var8);
               } else if (this.typeMapper != null) {
                  ToNativeConverter var15 = this.typeMapper.getToNativeConverter(var10);
                  FromNativeConverter var16 = this.typeMapper.getFromNativeConverter(var10);
                  if (var15 != null && var16 != null) {
                     var13 = var15.toNative(var13, new StructureWriteContext(this, var11.field));
                     var14 = var13 != null ? var13.getClass() : Pointer.class;
                     var11.writeConverter = var15;
                     var11.readConverter = var16;
                     var11.context = new StructureReadContext(this, var8);
                  } else if (var15 != null || var16 != null) {
                     String var17 = "Structures require bidirectional type conversion for " + var10;
                     throw new IllegalArgumentException(var17);
                  }
               }

               if (var13 == null) {
                  var13 = this.initializeField(var11.field, var10);
               }

               try {
                  var11.size = this.getNativeSize(var14, var13);
                  var12 = this.getNativeAlignment(var14, var13, var6);
               } catch (IllegalArgumentException var18) {
                  if (!var1 && this.typeMapper == null) {
                     return null;
                  }

                  String var23 = "Invalid Structure field in "
                     + this.getClass()
                     + ", field name '"
                     + var11.name
                     + "' ("
                     + var11.type
                     + "): "
                     + var18.getMessage();
                  throw new IllegalArgumentException(var23, var18);
               }

               if (var12 == 0) {
                  throw new Error("Field alignment is zero for field '" + var11.name + "' within " + this.getClass());
               }

               var5.alignment = Math.max(var5.alignment, var12);
               if (var3 % var12 != 0) {
                  var3 += var12 - var3 % var12;
               }

               if (this instanceof Union) {
                  var11.offset = 0;
                  var3 = Math.max(var3, var11.size);
               } else {
                  var11.offset = var3;
                  var3 += var11.size;
               }

               var5.fields.put(var11.name, var11);
               if (var5.typeInfoField == null
                  || var5.typeInfoField.size < var11.size
                  || var5.typeInfoField.size == var11.size && Structure.class.isAssignableFrom(var11.type)) {
                  var5.typeInfoField = var11;
               }
            }

            var6 = false;
         }

         if (var3 > 0) {
            int var19 = this.addPadding(var3, var5.alignment);
            if (this instanceof Structure.ByValue && !var2) {
               this.getTypeInfo();
            }

            var5.size = var19;
            return var5;
         } else {
            throw new IllegalArgumentException("Structure " + this.getClass() + " has unknown or zero size (ensure all fields are public)");
         }
      }
   }

   private void initializeFields() {
      for (Field var3 : this.getFieldList()) {
         try {
            Object var4 = var3.get(this);
            if (var4 == null) {
               this.initializeField(var3, var3.getType());
            }
         } catch (Exception var5) {
            throw new Error("Exception reading field '" + var3.getName() + "' in " + this.getClass(), var5);
         }
      }
   }

   private Object initializeField(Field var1, Class<?> var2) {
      Object var3 = null;
      if (Structure.class.isAssignableFrom(var2) && !Structure.ByReference.class.isAssignableFrom(var2)) {
         try {
            var3 = newInstance(var2, PLACEHOLDER_MEMORY);
            this.setFieldValue(var1, var3);
         } catch (IllegalArgumentException var6) {
            String var5 = "Can't determine size of nested structure";
            throw new IllegalArgumentException(var5, var6);
         }
      } else if (NativeMapped.class.isAssignableFrom(var2)) {
         NativeMappedConverter var4 = NativeMappedConverter.getInstance(var2);
         var3 = var4.defaultValue();
         this.setFieldValue(var1, var3);
      }

      return var3;
   }

   private int addPadding(int var1) {
      return this.addPadding(var1, this.structAlignment);
   }

   private int addPadding(int var1, int var2) {
      if (this.actualAlignType != 1 && var1 % var2 != 0) {
         var1 += var2 - var1 % var2;
      }

      return var1;
   }

   protected int getStructAlignment() {
      if (this.size == -1) {
         this.calculateSize(true);
      }

      return this.structAlignment;
   }

   protected int getNativeAlignment(Class<?> var1, Object var2, boolean var3) {
      int var4 = 1;
      if (NativeMapped.class.isAssignableFrom(var1)) {
         NativeMappedConverter var5 = NativeMappedConverter.getInstance(var1);
         var1 = var5.nativeType();
         var2 = var5.toNative(var2, new ToNativeContext());
      }

      int var7 = Native.getNativeSize(var1, var2);
      if (!var1.isPrimitive()
         && Long.class != var1
         && Integer.class != var1
         && Short.class != var1
         && Character.class != var1
         && Byte.class != var1
         && Boolean.class != var1
         && Float.class != var1
         && Double.class != var1) {
         if ((!Pointer.class.isAssignableFrom(var1) || Function.class.isAssignableFrom(var1))
            && (!Platform.HAS_BUFFERS || !Buffer.class.isAssignableFrom(var1))
            && !Callback.class.isAssignableFrom(var1)
            && WString.class != var1
            && String.class != var1) {
            if (Structure.class.isAssignableFrom(var1)) {
               if (Structure.ByReference.class.isAssignableFrom(var1)) {
                  var4 = Pointer.SIZE;
               } else {
                  if (var2 == null) {
                     var2 = newInstance(var1, PLACEHOLDER_MEMORY);
                  }

                  var4 = ((Structure)var2).getStructAlignment();
               }
            } else {
               if (!var1.isArray()) {
                  throw new IllegalArgumentException("Type " + var1 + " has unknown native alignment");
               }

               var4 = this.getNativeAlignment(var1.getComponentType(), null, var3);
            }
         } else {
            var4 = Pointer.SIZE;
         }
      } else {
         var4 = var7;
      }

      if (this.actualAlignType == 1) {
         var4 = 1;
      } else if (this.actualAlignType == 3) {
         var4 = Math.min(8, var4);
      } else if (this.actualAlignType == 2) {
         if (!var3 || !Platform.isMac() || !Platform.isPPC()) {
            var4 = Math.min(Native.MAX_ALIGNMENT, var4);
         }

         if (!var3 && Platform.isAIX() && (var1 == double.class || var1 == Double.class)) {
            var4 = 4;
         }
      }

      return var4;
   }

   @Override
   public String toString() {
      return this.toString(Boolean.getBoolean("jna.dump_memory"));
   }

   public String toString(boolean var1) {
      return this.toString(0, true, var1);
   }

   private String format(Class<?> var1) {
      String var2 = var1.getName();
      int var3 = var2.lastIndexOf(".");
      return var2.substring(var3 + 1);
   }

   private String toString(int var1, boolean var2, boolean var3) {
      this.ensureAllocated();
      String var4 = System.getProperty("line.separator");
      String var5 = this.format(this.getClass()) + "(" + this.getPointer() + ")";
      if (!(this.getPointer() instanceof Memory)) {
         var5 = var5 + " (" + this.size() + " bytes)";
      }

      String var6 = "";

      for (int var7 = 0; var7 < var1; var7++) {
         var6 = var6 + "  ";
      }

      String var13 = var4;
      if (!var2) {
         var13 = "...}";
      } else {
         Iterator var8 = this.fields().values().iterator();

         while (var8.hasNext()) {
            Structure.StructField var9 = (Structure.StructField)var8.next();
            Object var10 = this.getFieldValue(var9.field);
            String var11 = this.format(var9.type);
            String var12 = "";
            var13 = var13 + var6;
            if (var9.type.isArray() && var10 != null) {
               var11 = this.format(var9.type.getComponentType());
               var12 = "[" + Array.getLength(var10) + "]";
            }

            var13 = var13 + "  " + var11 + " " + var9.name + var12 + "@" + Integer.toHexString(var9.offset);
            if (var10 instanceof Structure) {
               var10 = ((Structure)var10).toString(var1 + 1, !(var10 instanceof Structure.ByReference), var3);
            }

            var13 = var13 + "=";
            if (var10 instanceof Long) {
               var13 = var13 + Long.toHexString((Long)var10);
            } else if (var10 instanceof Integer) {
               var13 = var13 + Integer.toHexString((Integer)var10);
            } else if (var10 instanceof Short) {
               var13 = var13 + Integer.toHexString((Short)var10);
            } else if (var10 instanceof Byte) {
               var13 = var13 + Integer.toHexString((Byte)var10);
            } else {
               var13 = var13 + String.valueOf(var10).trim();
            }

            var13 = var13 + var4;
            if (!var8.hasNext()) {
               var13 = var13 + var6 + "}";
            }
         }
      }

      if (var1 == 0 && var3) {
         byte var19 = 4;
         var13 = var13 + var4 + "memory dump" + var4;
         byte[] var20 = this.getPointer().getByteArray(0L, this.size());

         for (int var21 = 0; var21 < var20.length; var21++) {
            if (var21 % 4 == 0) {
               var13 = var13 + "[";
            }

            if (var20[var21] >= 0 && var20[var21] < 16) {
               var13 = var13 + "0";
            }

            var13 = var13 + Integer.toHexString(var20[var21] & 255);
            if (var21 % 4 == 3 && var21 < var20.length - 1) {
               var13 = var13 + "]" + var4;
            }
         }

         var13 = var13 + "]";
      }

      return var5 + " {" + var13;
   }

   public Structure[] toArray(Structure[] var1) {
      this.ensureAllocated();
      if (this.memory instanceof Structure.AutoAllocated) {
         Memory var2 = (Memory)this.memory;
         int var3 = var1.length * this.size();
         if (var2.size() < (long)var3) {
            this.useMemory(this.autoAllocate(var3));
         }
      }

      var1[0] = this;
      int var4 = this.size();

      for (int var5 = 1; var5 < var1.length; var5++) {
         var1[var5] = newInstance(this.getClass(), this.memory.share((long)(var5 * var4), (long)var4));
         var1[var5].conditionalAutoRead();
      }

      if (!(this instanceof Structure.ByValue)) {
         this.array = var1;
      }

      return var1;
   }

   public Structure[] toArray(int var1) {
      return this.toArray((Structure[])Array.newInstance(this.getClass(), var1));
   }

   private Class<?> baseClass() {
      return (this instanceof Structure.ByReference || this instanceof Structure.ByValue) && Structure.class.isAssignableFrom(this.getClass().getSuperclass())
         ? this.getClass().getSuperclass()
         : this.getClass();
   }

   public boolean dataEquals(Structure var1) {
      return this.dataEquals(var1, false);
   }

   public boolean dataEquals(Structure var1, boolean var2) {
      if (var2) {
         var1.getPointer().clear((long)var1.size());
         var1.write();
         this.getPointer().clear((long)this.size());
         this.write();
      }

      byte[] var3 = var1.getPointer().getByteArray(0L, var1.size());
      byte[] var4 = this.getPointer().getByteArray(0L, this.size());
      if (var3.length == var4.length) {
         for (int var5 = 0; var5 < var3.length; var5++) {
            if (var3[var5] != var4[var5]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof Structure && var1.getClass() == this.getClass() && ((Structure)var1).getPointer().equals(this.getPointer());
   }

   @Override
   public int hashCode() {
      Pointer var1 = this.getPointer();
      return var1 != null ? this.getPointer().hashCode() : this.getClass().hashCode();
   }

   protected void cacheTypeInfo(Pointer var1) {
      this.typeInfo = var1.peer;
   }

   Pointer getFieldTypeInfo(Structure.StructField var1) {
      Class var2 = var1.type;
      Object var3 = this.getFieldValue(var1.field);
      if (this.typeMapper != null) {
         ToNativeConverter var4 = this.typeMapper.getToNativeConverter(var2);
         if (var4 != null) {
            var2 = var4.nativeType();
            var3 = var4.toNative(var3, new ToNativeContext());
         }
      }

      return Structure.FFIType.get(var3, var2);
   }

   Pointer getTypeInfo() {
      Pointer var1 = getTypeInfo(this);
      this.cacheTypeInfo(var1);
      return var1;
   }

   public void setAutoSynch(boolean var1) {
      this.setAutoRead(var1);
      this.setAutoWrite(var1);
   }

   public void setAutoRead(boolean var1) {
      this.autoRead = var1;
   }

   public boolean getAutoRead() {
      return this.autoRead;
   }

   public void setAutoWrite(boolean var1) {
      this.autoWrite = var1;
   }

   public boolean getAutoWrite() {
      return this.autoWrite;
   }

   static Pointer getTypeInfo(Object var0) {
      return Structure.FFIType.get(var0);
   }

   private static Structure newInstance(Class<?> var0, long var1) {
      try {
         Structure var3 = newInstance(var0, var1 == 0L ? PLACEHOLDER_MEMORY : new Pointer(var1));
         if (var1 != 0L) {
            var3.conditionalAutoRead();
         }

         return var3;
      } catch (Throwable var4) {
         System.err.println("JNA: Error creating structure: " + var4);
         return null;
      }
   }

   public static Structure newInstance(Class<?> var0, Pointer var1) throws IllegalArgumentException {
      try {
         Constructor var9 = var0.getConstructor(Pointer.class);
         return (Structure)var9.newInstance(var1);
      } catch (NoSuchMethodException var4) {
      } catch (SecurityException var5) {
      } catch (InstantiationException var6) {
         String var11 = "Can't instantiate " + var0;
         throw new IllegalArgumentException(var11, var6);
      } catch (IllegalAccessException var7) {
         String var10 = "Instantiation of " + var0 + " (Pointer) not allowed, is it public?";
         throw new IllegalArgumentException(var10, var7);
      } catch (InvocationTargetException var8) {
         String var3 = "Exception thrown while instantiating an instance of " + var0;
         var8.printStackTrace();
         throw new IllegalArgumentException(var3, var8);
      }

      Structure var2 = newInstance(var0);
      if (var1 != PLACEHOLDER_MEMORY) {
         var2.useMemory(var1);
      }

      return var2;
   }

   public static Structure newInstance(Class<?> var0) throws IllegalArgumentException {
      try {
         Structure var1 = (Structure)var0.newInstance();
         if (var1 instanceof Structure.ByValue) {
            var1.allocateMemory();
         }

         return var1;
      } catch (InstantiationException var3) {
         String var5 = "Can't instantiate " + var0;
         throw new IllegalArgumentException(var5, var3);
      } catch (IllegalAccessException var4) {
         String var2 = "Instantiation of " + var0 + " not allowed, is it public?";
         throw new IllegalArgumentException(var2, var4);
      }
   }

   Structure.StructField typeInfoField() {
      Structure.LayoutInfo var1;
      synchronized (layoutInfo) {
         var1 = layoutInfo.get(this.getClass());
      }

      return var1 != null ? var1.typeInfoField : null;
   }

   private static void structureArrayCheck(Structure[] var0) {
      if (!Structure.ByReference[].class.isAssignableFrom(var0.getClass())) {
         Pointer var1 = var0[0].getPointer();
         int var2 = var0[0].size();

         for (int var3 = 1; var3 < var0.length; var3++) {
            if (var0[var3].getPointer().peer != var1.peer + (long)(var2 * var3)) {
               String var4 = "Structure array elements must use contiguous memory (bad backing address at Structure array index " + var3 + ")";
               throw new IllegalArgumentException(var4);
            }
         }
      }
   }

   public static void autoRead(Structure[] var0) {
      structureArrayCheck(var0);
      if (var0[0].array == var0) {
         var0[0].autoRead();
      } else {
         for (int var1 = 0; var1 < var0.length; var1++) {
            if (var0[var1] != null) {
               var0[var1].autoRead();
            }
         }
      }
   }

   public void autoRead() {
      if (this.getAutoRead()) {
         this.read();
         if (this.array != null) {
            for (int var1 = 1; var1 < this.array.length; var1++) {
               this.array[var1].autoRead();
            }
         }
      }
   }

   public static void autoWrite(Structure[] var0) {
      structureArrayCheck(var0);
      if (var0[0].array == var0) {
         var0[0].autoWrite();
      } else {
         for (int var1 = 0; var1 < var0.length; var1++) {
            if (var0[var1] != null) {
               var0[var1].autoWrite();
            }
         }
      }
   }

   public void autoWrite() {
      if (this.getAutoWrite()) {
         this.write();
         if (this.array != null) {
            for (int var1 = 1; var1 < this.array.length; var1++) {
               this.array[var1].autoWrite();
            }
         }
      }
   }

   protected int getNativeSize(Class<?> var1) {
      return this.getNativeSize(var1, null);
   }

   protected int getNativeSize(Class<?> var1, Object var2) {
      return Native.getNativeSize(var1, var2);
   }

   static void validate(Class<?> var0) {
      newInstance(var0, PLACEHOLDER_MEMORY);
   }

   private static class AutoAllocated extends Memory {
      public AutoAllocated(int var1) {
         super((long)var1);
         super.clear();
      }

      @Override
      public String toString() {
         return "auto-" + super.toString();
      }
   }

   public interface ByReference {
   }

   public interface ByValue {
   }

   static class FFIType extends Structure {
      private static final Map<Object, Object> typeInfoMap = new WeakHashMap<>();
      private static final int FFI_TYPE_STRUCT = 13;
      public Structure.FFIType.size_t size;
      public short alignment;
      public short type = 13;
      public Pointer elements;

      private FFIType(Structure var1) {
         var1.ensureAllocated(true);
         Pointer[] var2;
         if (var1 instanceof Union) {
            Structure.StructField var3 = ((Union)var1).typeInfoField();
            var2 = new Pointer[]{get(var1.getFieldValue(var3.field), var3.type), null};
         } else {
            var2 = new Pointer[var1.fields().size() + 1];
            int var6 = 0;

            for (Structure.StructField var5 : var1.fields().values()) {
               var2[var6++] = var1.getFieldTypeInfo(var5);
            }
         }

         this.init(var2);
      }

      private FFIType(Object var1, Class<?> var2) {
         int var3 = Array.getLength(var1);
         Pointer[] var4 = new Pointer[var3 + 1];
         Pointer var5 = get(null, var2.getComponentType());

         for (int var6 = 0; var6 < var3; var6++) {
            var4[var6] = var5;
         }

         this.init(var4);
      }

      @Override
      protected List<String> getFieldOrder() {
         return Arrays.asList("size", "alignment", "type", "elements");
      }

      private void init(Pointer[] var1) {
         this.elements = new Memory((long)(Pointer.SIZE * var1.length));
         this.elements.write(0L, var1, 0, var1.length);
         this.write();
      }

      static Pointer get(Object var0) {
         if (var0 == null) {
            return Structure.FFIType.FFITypes.ffi_type_pointer;
         } else {
            return var0 instanceof Class ? get(null, (Class<?>)var0) : get(var0, var0.getClass());
         }
      }

      private static Pointer get(Object var0, Class<?> var1) {
         TypeMapper var2 = Native.getTypeMapper(var1);
         if (var2 != null) {
            ToNativeConverter var3 = var2.getToNativeConverter(var1);
            if (var3 != null) {
               var1 = var3.nativeType();
            }
         }

         synchronized (typeInfoMap) {
            Object var4 = typeInfoMap.get(var1);
            if (var4 instanceof Pointer) {
               return (Pointer)var4;
            } else if (var4 instanceof Structure.FFIType) {
               return ((Structure.FFIType)var4).getPointer();
            } else if ((!Platform.HAS_BUFFERS || !Buffer.class.isAssignableFrom(var1)) && !Callback.class.isAssignableFrom(var1)) {
               if (Structure.class.isAssignableFrom(var1)) {
                  if (var0 == null) {
                     var0 = newInstance(var1, Structure.PLACEHOLDER_MEMORY);
                  }

                  if (Structure.ByReference.class.isAssignableFrom(var1)) {
                     typeInfoMap.put(var1, Structure.FFIType.FFITypes.ffi_type_pointer);
                     return Structure.FFIType.FFITypes.ffi_type_pointer;
                  } else {
                     Structure.FFIType var10 = new Structure.FFIType((Structure)var0);
                     typeInfoMap.put(var1, var10);
                     return var10.getPointer();
                  }
               } else if (NativeMapped.class.isAssignableFrom(var1)) {
                  NativeMappedConverter var9 = NativeMappedConverter.getInstance(var1);
                  return get(var9.toNative(var0, new ToNativeContext()), var9.nativeType());
               } else if (var1.isArray()) {
                  Structure.FFIType var5 = new Structure.FFIType(var0, var1);
                  typeInfoMap.put(var0, var5);
                  return var5.getPointer();
               } else {
                  throw new IllegalArgumentException("Unsupported type " + var1);
               }
            } else {
               typeInfoMap.put(var1, Structure.FFIType.FFITypes.ffi_type_pointer);
               return Structure.FFIType.FFITypes.ffi_type_pointer;
            }
         }
      }

      static {
         if (Native.POINTER_SIZE == 0) {
            throw new Error("Native library not initialized");
         } else if (Structure.FFIType.FFITypes.ffi_type_void == null) {
            throw new Error("FFI types not initialized");
         } else {
            typeInfoMap.put(void.class, Structure.FFIType.FFITypes.ffi_type_void);
            typeInfoMap.put(Void.class, Structure.FFIType.FFITypes.ffi_type_void);
            typeInfoMap.put(float.class, Structure.FFIType.FFITypes.ffi_type_float);
            typeInfoMap.put(Float.class, Structure.FFIType.FFITypes.ffi_type_float);
            typeInfoMap.put(double.class, Structure.FFIType.FFITypes.ffi_type_double);
            typeInfoMap.put(Double.class, Structure.FFIType.FFITypes.ffi_type_double);
            typeInfoMap.put(long.class, Structure.FFIType.FFITypes.ffi_type_sint64);
            typeInfoMap.put(Long.class, Structure.FFIType.FFITypes.ffi_type_sint64);
            typeInfoMap.put(int.class, Structure.FFIType.FFITypes.ffi_type_sint32);
            typeInfoMap.put(Integer.class, Structure.FFIType.FFITypes.ffi_type_sint32);
            typeInfoMap.put(short.class, Structure.FFIType.FFITypes.ffi_type_sint16);
            typeInfoMap.put(Short.class, Structure.FFIType.FFITypes.ffi_type_sint16);
            Pointer var0 = Native.WCHAR_SIZE == 2 ? Structure.FFIType.FFITypes.ffi_type_uint16 : Structure.FFIType.FFITypes.ffi_type_uint32;
            typeInfoMap.put(char.class, var0);
            typeInfoMap.put(Character.class, var0);
            typeInfoMap.put(byte.class, Structure.FFIType.FFITypes.ffi_type_sint8);
            typeInfoMap.put(Byte.class, Structure.FFIType.FFITypes.ffi_type_sint8);
            typeInfoMap.put(Pointer.class, Structure.FFIType.FFITypes.ffi_type_pointer);
            typeInfoMap.put(String.class, Structure.FFIType.FFITypes.ffi_type_pointer);
            typeInfoMap.put(WString.class, Structure.FFIType.FFITypes.ffi_type_pointer);
            typeInfoMap.put(boolean.class, Structure.FFIType.FFITypes.ffi_type_uint32);
            typeInfoMap.put(Boolean.class, Structure.FFIType.FFITypes.ffi_type_uint32);
         }
      }

      private static class FFITypes {
         private static Pointer ffi_type_void;
         private static Pointer ffi_type_float;
         private static Pointer ffi_type_double;
         private static Pointer ffi_type_longdouble;
         private static Pointer ffi_type_uint8;
         private static Pointer ffi_type_sint8;
         private static Pointer ffi_type_uint16;
         private static Pointer ffi_type_sint16;
         private static Pointer ffi_type_uint32;
         private static Pointer ffi_type_sint32;
         private static Pointer ffi_type_uint64;
         private static Pointer ffi_type_sint64;
         private static Pointer ffi_type_pointer;
      }

      public static class size_t extends IntegerType {
         private static final long serialVersionUID = 1L;

         public size_t() {
            this(0L);
         }

         public size_t(long var1) {
            super(Native.SIZE_T_SIZE, var1);
         }
      }
   }

   private static class LayoutInfo {
      private int size = -1;
      private int alignment = 1;
      private final Map<String, Structure.StructField> fields = Collections.synchronizedMap(new LinkedHashMap<>());
      private int alignType = 0;
      private TypeMapper typeMapper;
      private boolean variable;
      private Structure.StructField typeInfoField;

      private LayoutInfo() {
      }
   }

   protected static class StructField {
      public String name;
      public Class<?> type;
      public Field field;
      public int size = -1;
      public int offset = -1;
      public boolean isVolatile;
      public boolean isReadOnly;
      public FromNativeConverter readConverter;
      public ToNativeConverter writeConverter;
      public FromNativeContext context;

      @Override
      public String toString() {
         return this.name + "@" + this.offset + "[" + this.size + "] (" + this.type + ")";
      }
   }

   static class StructureSet extends AbstractCollection<Structure> implements Set<Structure> {
      Structure[] elements;
      private int count;

      private void ensureCapacity(int var1) {
         if (this.elements == null) {
            this.elements = new Structure[var1 * 3 / 2];
         } else if (this.elements.length < var1) {
            Structure[] var2 = new Structure[var1 * 3 / 2];
            System.arraycopy(this.elements, 0, var2, 0, this.elements.length);
            this.elements = var2;
         }
      }

      public Structure[] getElements() {
         return this.elements;
      }

      @Override
      public int size() {
         return this.count;
      }

      @Override
      public boolean contains(Object var1) {
         return this.indexOf((Structure)var1) != -1;
      }

      public boolean add(Structure var1) {
         if (!this.contains(var1)) {
            this.ensureCapacity(this.count + 1);
            this.elements[this.count++] = var1;
         }

         return true;
      }

      private int indexOf(Structure var1) {
         for (int var2 = 0; var2 < this.count; var2++) {
            Structure var3 = this.elements[var2];
            if (var1 == var3 || var1.getClass() == var3.getClass() && var1.size() == var3.size() && var1.getPointer().equals(var3.getPointer())) {
               return var2;
            }
         }

         return -1;
      }

      @Override
      public boolean remove(Object var1) {
         int var2 = this.indexOf((Structure)var1);
         if (var2 != -1) {
            if (--this.count >= 0) {
               this.elements[var2] = this.elements[this.count];
               this.elements[this.count] = null;
            }

            return true;
         } else {
            return false;
         }
      }

      @Override
      public Iterator<Structure> iterator() {
         Structure[] var1 = new Structure[this.count];
         if (this.count > 0) {
            System.arraycopy(this.elements, 0, var1, 0, this.count);
         }

         return Arrays.asList(var1).iterator();
      }
   }
}
