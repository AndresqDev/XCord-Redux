package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public abstract class GeneratedMessageV3 extends AbstractMessage implements Serializable {
   private static final long serialVersionUID = 1L;
   protected static boolean alwaysUseFieldBuilders = false;
   protected UnknownFieldSet unknownFields;

   protected GeneratedMessageV3() {
      this.unknownFields = UnknownFieldSet.getDefaultInstance();
   }

   protected GeneratedMessageV3(GeneratedMessageV3.Builder<?> var1) {
      this.unknownFields = var1.getUnknownFields();
   }

   @Override
   public Parser<? extends GeneratedMessageV3> getParserForType() {
      throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
   }

   static void enableAlwaysUseFieldBuildersForTesting() {
      setAlwaysUseFieldBuildersForTesting(true);
   }

   static void setAlwaysUseFieldBuildersForTesting(boolean var0) {
      alwaysUseFieldBuilders = var0;
   }

   protected abstract GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable();

   @Override
   public Descriptors.Descriptor getDescriptorForType() {
      return this.internalGetFieldAccessorTable().descriptor;
   }

   protected void mergeFromAndMakeImmutableInternal(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
      Schema var3 = Protobuf.getInstance().schemaFor(this);

      try {
         var3.mergeFrom(this, CodedInputStreamReader.forCodedInput(var1), var2);
      } catch (InvalidProtocolBufferException var5) {
         throw var5.setUnfinishedMessage(this);
      } catch (IOException var6) {
         throw new InvalidProtocolBufferException(var6).setUnfinishedMessage(this);
      }

      var3.makeImmutable(this);
   }

   private Map<Descriptors.FieldDescriptor, Object> getAllFieldsMutable(boolean var1) {
      TreeMap var2 = new TreeMap();
      Descriptors.Descriptor var3 = this.internalGetFieldAccessorTable().descriptor;
      List var4 = var3.getFields();

      for (int var5 = 0; var5 < var4.size(); var5++) {
         Descriptors.FieldDescriptor var6 = (Descriptors.FieldDescriptor)var4.get(var5);
         Descriptors.OneofDescriptor var7 = var6.getContainingOneof();
         if (var7 != null) {
            var5 += var7.getFieldCount() - 1;
            if (!this.hasOneof(var7)) {
               continue;
            }

            var6 = this.getOneofFieldDescriptor(var7);
         } else {
            if (var6.isRepeated()) {
               List var8 = (List)this.getField(var6);
               if (!var8.isEmpty()) {
                  var2.put(var6, var8);
               }
               continue;
            }

            if (!this.hasField(var6)) {
               continue;
            }
         }

         if (var1 && var6.getJavaType() == Descriptors.FieldDescriptor.JavaType.STRING) {
            var2.put(var6, this.getFieldRaw(var6));
         } else {
            var2.put(var6, this.getField(var6));
         }
      }

      return var2;
   }

   @Override
   public boolean isInitialized() {
      for (Descriptors.FieldDescriptor var2 : this.getDescriptorForType().getFields()) {
         if (var2.isRequired() && !this.hasField(var2)) {
            return false;
         }

         if (var2.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            if (var2.isRepeated()) {
               for (Message var5 : (List)this.getField(var2)) {
                  if (!var5.isInitialized()) {
                     return false;
                  }
               }
            } else if (this.hasField(var2) && !((Message)this.getField(var2)).isInitialized()) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
      return Collections.unmodifiableMap(this.getAllFieldsMutable(false));
   }

   Map<Descriptors.FieldDescriptor, Object> getAllFieldsRaw() {
      return Collections.unmodifiableMap(this.getAllFieldsMutable(true));
   }

   @Override
   public boolean hasOneof(Descriptors.OneofDescriptor var1) {
      return this.internalGetFieldAccessorTable().getOneof(var1).has(this);
   }

   @Override
   public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor var1) {
      return this.internalGetFieldAccessorTable().getOneof(var1).get(this);
   }

   @Override
   public boolean hasField(Descriptors.FieldDescriptor var1) {
      return this.internalGetFieldAccessorTable().getField(var1).has(this);
   }

   @Override
   public Object getField(Descriptors.FieldDescriptor var1) {
      return this.internalGetFieldAccessorTable().getField(var1).get(this);
   }

   Object getFieldRaw(Descriptors.FieldDescriptor var1) {
      return this.internalGetFieldAccessorTable().getField(var1).getRaw(this);
   }

   @Override
   public int getRepeatedFieldCount(Descriptors.FieldDescriptor var1) {
      return this.internalGetFieldAccessorTable().getField(var1).getRepeatedCount(this);
   }

   @Override
   public Object getRepeatedField(Descriptors.FieldDescriptor var1, int var2) {
      return this.internalGetFieldAccessorTable().getField(var1).getRepeated(this, var2);
   }

   @Override
   public UnknownFieldSet getUnknownFields() {
      throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
   }

   protected boolean parseUnknownField(CodedInputStream var1, UnknownFieldSet.Builder var2, ExtensionRegistryLite var3, int var4) throws IOException {
      return var1.shouldDiscardUnknownFields() ? var1.skipField(var4) : var2.mergeFieldFrom(var4, var1);
   }

   protected boolean parseUnknownFieldProto3(CodedInputStream var1, UnknownFieldSet.Builder var2, ExtensionRegistryLite var3, int var4) throws IOException {
      return this.parseUnknownField(var1, var2, var3, var4);
   }

   protected static <M extends Message> M parseWithIOException(Parser<M> var0, InputStream var1) throws IOException {
      try {
         return (M)var0.parseFrom(var1);
      } catch (InvalidProtocolBufferException var3) {
         throw var3.unwrapIOException();
      }
   }

   protected static <M extends Message> M parseWithIOException(Parser<M> var0, InputStream var1, ExtensionRegistryLite var2) throws IOException {
      try {
         return (M)var0.parseFrom(var1, var2);
      } catch (InvalidProtocolBufferException var4) {
         throw var4.unwrapIOException();
      }
   }

   protected static <M extends Message> M parseWithIOException(Parser<M> var0, CodedInputStream var1) throws IOException {
      try {
         return (M)var0.parseFrom(var1);
      } catch (InvalidProtocolBufferException var3) {
         throw var3.unwrapIOException();
      }
   }

   protected static <M extends Message> M parseWithIOException(Parser<M> var0, CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
      try {
         return (M)var0.parseFrom(var1, var2);
      } catch (InvalidProtocolBufferException var4) {
         throw var4.unwrapIOException();
      }
   }

   protected static <M extends Message> M parseDelimitedWithIOException(Parser<M> var0, InputStream var1) throws IOException {
      try {
         return (M)var0.parseDelimitedFrom(var1);
      } catch (InvalidProtocolBufferException var3) {
         throw var3.unwrapIOException();
      }
   }

   protected static <M extends Message> M parseDelimitedWithIOException(Parser<M> var0, InputStream var1, ExtensionRegistryLite var2) throws IOException {
      try {
         return (M)var0.parseDelimitedFrom(var1, var2);
      } catch (InvalidProtocolBufferException var4) {
         throw var4.unwrapIOException();
      }
   }

   protected static boolean canUseUnsafe() {
      return UnsafeUtil.hasUnsafeArrayOperations() && UnsafeUtil.hasUnsafeByteBufferOperations();
   }

   protected static Internal.IntList emptyIntList() {
      return IntArrayList.emptyList();
   }

   protected static Internal.IntList newIntList() {
      return new IntArrayList();
   }

   protected static Internal.IntList mutableCopy(Internal.IntList var0) {
      int var1 = var0.size();
      return var0.mutableCopyWithCapacity(var1 == 0 ? 10 : var1 * 2);
   }

   protected static Internal.LongList emptyLongList() {
      return LongArrayList.emptyList();
   }

   protected static Internal.LongList newLongList() {
      return new LongArrayList();
   }

   protected static Internal.LongList mutableCopy(Internal.LongList var0) {
      int var1 = var0.size();
      return var0.mutableCopyWithCapacity(var1 == 0 ? 10 : var1 * 2);
   }

   protected static Internal.FloatList emptyFloatList() {
      return FloatArrayList.emptyList();
   }

   protected static Internal.FloatList newFloatList() {
      return new FloatArrayList();
   }

   protected static Internal.FloatList mutableCopy(Internal.FloatList var0) {
      int var1 = var0.size();
      return var0.mutableCopyWithCapacity(var1 == 0 ? 10 : var1 * 2);
   }

   protected static Internal.DoubleList emptyDoubleList() {
      return DoubleArrayList.emptyList();
   }

   protected static Internal.DoubleList newDoubleList() {
      return new DoubleArrayList();
   }

   protected static Internal.DoubleList mutableCopy(Internal.DoubleList var0) {
      int var1 = var0.size();
      return var0.mutableCopyWithCapacity(var1 == 0 ? 10 : var1 * 2);
   }

   protected static Internal.BooleanList emptyBooleanList() {
      return BooleanArrayList.emptyList();
   }

   protected static Internal.BooleanList newBooleanList() {
      return new BooleanArrayList();
   }

   protected static Internal.BooleanList mutableCopy(Internal.BooleanList var0) {
      int var1 = var0.size();
      return var0.mutableCopyWithCapacity(var1 == 0 ? 10 : var1 * 2);
   }

   @Override
   public void writeTo(CodedOutputStream var1) throws IOException {
      MessageReflection.writeMessageTo(this, this.getAllFieldsRaw(), var1, false);
   }

   @Override
   public int getSerializedSize() {
      int var1 = this.memoizedSize;
      if (var1 != -1) {
         return var1;
      } else {
         this.memoizedSize = MessageReflection.getSerializedSize(this, this.getAllFieldsRaw());
         return this.memoizedSize;
      }
   }

   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      throw new UnsupportedOperationException("This method must be overridden by the subclass.");
   }

   protected void makeExtensionsImmutable() {
   }

   protected abstract Message.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1);

   @Override
   protected Message.Builder newBuilderForType(final AbstractMessage.BuilderParent var1) {
      return this.newBuilderForType(new GeneratedMessageV3.BuilderParent() {
         @Override
         public void markDirty() {
            var1.markDirty();
         }
      });
   }

   private static java.lang.reflect.Method getMethodOrDie(Class var0, String var1, Class... var2) {
      try {
         return var0.getMethod(var1, var2);
      } catch (NoSuchMethodException var4) {
         throw new RuntimeException("Generated message class \"" + var0.getName() + "\" missing method \"" + var1 + "\".", var4);
      }
   }

   private static Object invokeOrDie(java.lang.reflect.Method var0, Object var1, Object... var2) {
      try {
         return var0.invoke(var1, var2);
      } catch (IllegalAccessException var5) {
         throw new RuntimeException("Couldn't use Java reflection to implement protocol message reflection.", var5);
      } catch (InvocationTargetException var6) {
         Throwable var4 = var6.getCause();
         if (var4 instanceof RuntimeException) {
            throw (RuntimeException)var4;
         } else if (var4 instanceof Error) {
            throw (Error)var4;
         } else {
            throw new RuntimeException("Unexpected exception thrown by generated accessor method.", var4);
         }
      }
   }

   protected MapField internalGetMapField(int var1) {
      throw new RuntimeException("No map fields found in " + this.getClass().getName());
   }

   protected Object writeReplace() throws ObjectStreamException {
      return new GeneratedMessageLite.SerializedForm(this);
   }

   private static <MessageType extends GeneratedMessageV3.ExtendableMessage<MessageType>, T> Extension<MessageType, T> checkNotLite(
      ExtensionLite<MessageType, T> var0
   ) {
      if (var0.isLite()) {
         throw new IllegalArgumentException("Expected non-lite extension.");
      } else {
         return (Extension<MessageType, T>)var0;
      }
   }

   protected static int computeStringSize(int var0, Object var1) {
      return var1 instanceof String ? CodedOutputStream.computeStringSize(var0, (String)var1) : CodedOutputStream.computeBytesSize(var0, (ByteString)var1);
   }

   protected static int computeStringSizeNoTag(Object var0) {
      return var0 instanceof String ? CodedOutputStream.computeStringSizeNoTag((String)var0) : CodedOutputStream.computeBytesSizeNoTag((ByteString)var0);
   }

   protected static void writeString(CodedOutputStream var0, int var1, Object var2) throws IOException {
      if (var2 instanceof String) {
         var0.writeString(var1, (String)var2);
      } else {
         var0.writeBytes(var1, (ByteString)var2);
      }
   }

   protected static void writeStringNoTag(CodedOutputStream var0, Object var1) throws IOException {
      if (var1 instanceof String) {
         var0.writeStringNoTag((String)var1);
      } else {
         var0.writeBytesNoTag((ByteString)var1);
      }
   }

   protected static <V> void serializeIntegerMapTo(CodedOutputStream var0, MapField<Integer, V> var1, MapEntry<Integer, V> var2, int var3) throws IOException {
      Map var4 = var1.getMap();
      if (!var0.isSerializationDeterministic()) {
         serializeMapTo(var0, var4, var2, var3);
      } else {
         int[] var5 = new int[var4.size()];
         int var6 = 0;

         for (int var8 : var4.keySet()) {
            var5[var6++] = var8;
         }

         Arrays.sort(var5);

         for (int var10 : var5) {
            var0.writeMessage(var3, var2.newBuilderForType().setKey(var10).setValue((V)var4.get(var10)).build());
         }
      }
   }

   protected static <V> void serializeLongMapTo(CodedOutputStream var0, MapField<Long, V> var1, MapEntry<Long, V> var2, int var3) throws IOException {
      Map var4 = var1.getMap();
      if (!var0.isSerializationDeterministic()) {
         serializeMapTo(var0, var4, var2, var3);
      } else {
         long[] var5 = new long[var4.size()];
         int var6 = 0;

         for (long var8 : var4.keySet()) {
            var5[var6++] = var8;
         }

         Arrays.sort(var5);

         for (long var10 : var5) {
            var0.writeMessage(var3, var2.newBuilderForType().setKey(var10).setValue((V)var4.get(var10)).build());
         }
      }
   }

   protected static <V> void serializeStringMapTo(CodedOutputStream var0, MapField<String, V> var1, MapEntry<String, V> var2, int var3) throws IOException {
      Map var4 = var1.getMap();
      if (!var0.isSerializationDeterministic()) {
         serializeMapTo(var0, var4, var2, var3);
      } else {
         String[] var5 = new String[var4.size()];
         var5 = var4.keySet().toArray(var5);
         Arrays.sort((Object[])var5);

         for (String var9 : var5) {
            var0.writeMessage(var3, var2.newBuilderForType().setKey(var9).setValue((V)var4.get(var9)).build());
         }
      }
   }

   protected static <V> void serializeBooleanMapTo(CodedOutputStream var0, MapField<Boolean, V> var1, MapEntry<Boolean, V> var2, int var3) throws IOException {
      Map var4 = var1.getMap();
      if (!var0.isSerializationDeterministic()) {
         serializeMapTo(var0, var4, var2, var3);
      } else {
         maybeSerializeBooleanEntryTo(var0, var4, var2, var3, false);
         maybeSerializeBooleanEntryTo(var0, var4, var2, var3, true);
      }
   }

   private static <V> void maybeSerializeBooleanEntryTo(CodedOutputStream var0, Map<Boolean, V> var1, MapEntry<Boolean, V> var2, int var3, boolean var4) throws IOException {
      if (var1.containsKey(var4)) {
         var0.writeMessage(var3, var2.newBuilderForType().setKey(var4).setValue((V)var1.get(var4)).build());
      }
   }

   private static <K, V> void serializeMapTo(CodedOutputStream var0, Map<K, V> var1, MapEntry<K, V> var2, int var3) throws IOException {
      for (Entry var5 : var1.entrySet()) {
         var0.writeMessage(var3, var2.newBuilderForType().setKey(var5.getKey()).setValue((V)var5.getValue()).build());
      }
   }

   public abstract static class Builder<BuilderType extends GeneratedMessageV3.Builder<BuilderType>> extends AbstractMessage.Builder<BuilderType> {
      private GeneratedMessageV3.BuilderParent builderParent;
      private GeneratedMessageV3.Builder<BuilderType>.BuilderParentImpl meAsParent;
      private boolean isClean;
      private UnknownFieldSet unknownFields = UnknownFieldSet.getDefaultInstance();

      protected Builder() {
         this(null);
      }

      protected Builder(GeneratedMessageV3.BuilderParent var1) {
         this.builderParent = var1;
      }

      @Override
      void dispose() {
         this.builderParent = null;
      }

      protected void onBuilt() {
         if (this.builderParent != null) {
            this.markClean();
         }
      }

      @Override
      protected void markClean() {
         this.isClean = true;
      }

      protected boolean isClean() {
         return this.isClean;
      }

      public BuilderType clone() {
         GeneratedMessageV3.Builder var1 = (GeneratedMessageV3.Builder)this.getDefaultInstanceForType().newBuilderForType();
         var1.mergeFrom(this.buildPartial());
         return (BuilderType)var1;
      }

      public BuilderType clear() {
         this.unknownFields = UnknownFieldSet.getDefaultInstance();
         this.onChanged();
         return (BuilderType)this;
      }

      protected abstract GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable();

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return this.internalGetFieldAccessorTable().descriptor;
      }

      @Override
      public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
         return Collections.unmodifiableMap(this.getAllFieldsMutable());
      }

      private Map<Descriptors.FieldDescriptor, Object> getAllFieldsMutable() {
         TreeMap var1 = new TreeMap();
         Descriptors.Descriptor var2 = this.internalGetFieldAccessorTable().descriptor;
         List var3 = var2.getFields();

         for (int var4 = 0; var4 < var3.size(); var4++) {
            Descriptors.FieldDescriptor var5 = (Descriptors.FieldDescriptor)var3.get(var4);
            Descriptors.OneofDescriptor var6 = var5.getContainingOneof();
            if (var6 != null) {
               var4 += var6.getFieldCount() - 1;
               if (!this.hasOneof(var6)) {
                  continue;
               }

               var5 = this.getOneofFieldDescriptor(var6);
            } else {
               if (var5.isRepeated()) {
                  List var7 = (List)this.getField(var5);
                  if (!var7.isEmpty()) {
                     var1.put(var5, var7);
                  }
                  continue;
               }

               if (!this.hasField(var5)) {
                  continue;
               }
            }

            var1.put(var5, this.getField(var5));
         }

         return var1;
      }

      @Override
      public Message.Builder newBuilderForField(Descriptors.FieldDescriptor var1) {
         return this.internalGetFieldAccessorTable().getField(var1).newBuilder();
      }

      @Override
      public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor var1) {
         return this.internalGetFieldAccessorTable().getField(var1).getBuilder(this);
      }

      @Override
      public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor var1, int var2) {
         return this.internalGetFieldAccessorTable().getField(var1).getRepeatedBuilder(this, var2);
      }

      @Override
      public boolean hasOneof(Descriptors.OneofDescriptor var1) {
         return this.internalGetFieldAccessorTable().getOneof(var1).has(this);
      }

      @Override
      public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor var1) {
         return this.internalGetFieldAccessorTable().getOneof(var1).get(this);
      }

      @Override
      public boolean hasField(Descriptors.FieldDescriptor var1) {
         return this.internalGetFieldAccessorTable().getField(var1).has(this);
      }

      @Override
      public Object getField(Descriptors.FieldDescriptor var1) {
         Object var2 = this.internalGetFieldAccessorTable().getField(var1).get(this);
         return var1.isRepeated() ? Collections.unmodifiableList((List)var2) : var2;
      }

      public BuilderType setField(Descriptors.FieldDescriptor var1, Object var2) {
         this.internalGetFieldAccessorTable().getField(var1).set(this, var2);
         return (BuilderType)this;
      }

      public BuilderType clearField(Descriptors.FieldDescriptor var1) {
         this.internalGetFieldAccessorTable().getField(var1).clear(this);
         return (BuilderType)this;
      }

      public BuilderType clearOneof(Descriptors.OneofDescriptor var1) {
         this.internalGetFieldAccessorTable().getOneof(var1).clear(this);
         return (BuilderType)this;
      }

      @Override
      public int getRepeatedFieldCount(Descriptors.FieldDescriptor var1) {
         return this.internalGetFieldAccessorTable().getField(var1).getRepeatedCount(this);
      }

      @Override
      public Object getRepeatedField(Descriptors.FieldDescriptor var1, int var2) {
         return this.internalGetFieldAccessorTable().getField(var1).getRepeated(this, var2);
      }

      public BuilderType setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         this.internalGetFieldAccessorTable().getField(var1).setRepeated(this, var2, var3);
         return (BuilderType)this;
      }

      public BuilderType addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         this.internalGetFieldAccessorTable().getField(var1).addRepeated(this, var2);
         return (BuilderType)this;
      }

      private BuilderType setUnknownFieldsInternal(UnknownFieldSet var1) {
         this.unknownFields = var1;
         this.onChanged();
         return (BuilderType)this;
      }

      public BuilderType setUnknownFields(UnknownFieldSet var1) {
         return this.setUnknownFieldsInternal(var1);
      }

      protected BuilderType setUnknownFieldsProto3(UnknownFieldSet var1) {
         return this.setUnknownFieldsInternal(var1);
      }

      public BuilderType mergeUnknownFields(UnknownFieldSet var1) {
         return this.setUnknownFields(UnknownFieldSet.newBuilder(this.unknownFields).mergeFrom(var1).build());
      }

      @Override
      public boolean isInitialized() {
         for (Descriptors.FieldDescriptor var2 : this.getDescriptorForType().getFields()) {
            if (var2.isRequired() && !this.hasField(var2)) {
               return false;
            }

            if (var2.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
               if (var2.isRepeated()) {
                  for (Message var5 : (List)this.getField(var2)) {
                     if (!var5.isInitialized()) {
                        return false;
                     }
                  }
               } else if (this.hasField(var2) && !((Message)this.getField(var2)).isInitialized()) {
                  return false;
               }
            }
         }

         return true;
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      protected GeneratedMessageV3.BuilderParent getParentForChildren() {
         if (this.meAsParent == null) {
            this.meAsParent = new GeneratedMessageV3.Builder.BuilderParentImpl();
         }

         return this.meAsParent;
      }

      protected final void onChanged() {
         if (this.isClean && this.builderParent != null) {
            this.builderParent.markDirty();
            this.isClean = false;
         }
      }

      protected MapField internalGetMapField(int var1) {
         throw new RuntimeException("No map fields found in " + this.getClass().getName());
      }

      protected MapField internalGetMutableMapField(int var1) {
         throw new RuntimeException("No map fields found in " + this.getClass().getName());
      }

      private class BuilderParentImpl implements GeneratedMessageV3.BuilderParent {
         private BuilderParentImpl() {
         }

         @Override
         public void markDirty() {
            Builder.this.onChanged();
         }
      }
   }

   protected interface BuilderParent extends AbstractMessage.BuilderParent {
   }

   public abstract static class ExtendableBuilder<MessageType extends GeneratedMessageV3.ExtendableMessage, BuilderType extends GeneratedMessageV3.ExtendableBuilder<MessageType, BuilderType>>
      extends GeneratedMessageV3.Builder<BuilderType>
      implements GeneratedMessageV3.ExtendableMessageOrBuilder<MessageType> {
      private FieldSet.Builder<Descriptors.FieldDescriptor> extensions;

      protected ExtendableBuilder() {
      }

      protected ExtendableBuilder(GeneratedMessageV3.BuilderParent var1) {
         super(var1);
      }

      void internalSetExtensionSet(FieldSet<Descriptors.FieldDescriptor> var1) {
         this.extensions = FieldSet.Builder.fromFieldSet(var1);
      }

      public BuilderType clear() {
         this.extensions = null;
         return super.clear();
      }

      private void ensureExtensionsIsMutable() {
         if (this.extensions == null) {
            this.extensions = FieldSet.newBuilder();
         }
      }

      private void verifyExtensionContainingType(Extension<MessageType, ?> var1) {
         if (var1.getDescriptor().getContainingType() != this.getDescriptorForType()) {
            throw new IllegalArgumentException(
               "Extension is for type \""
                  + var1.getDescriptor().getContainingType().getFullName()
                  + "\" which does not match message type \""
                  + this.getDescriptorForType().getFullName()
                  + "\"."
            );
         }
      }

      @Override
      public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> var1) {
         Extension var2 = GeneratedMessageV3.checkNotLite(var1);
         this.verifyExtensionContainingType(var2);
         return this.extensions == null ? false : this.extensions.hasField(var2.getDescriptor());
      }

      @Override
      public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> var1) {
         Extension var2 = GeneratedMessageV3.checkNotLite(var1);
         this.verifyExtensionContainingType(var2);
         Descriptors.FieldDescriptor var3 = var2.getDescriptor();
         return this.extensions == null ? 0 : this.extensions.getRepeatedFieldCount(var3);
      }

      @Override
      public final <Type> Type getExtension(ExtensionLite<MessageType, Type> var1) {
         Extension var2 = GeneratedMessageV3.checkNotLite(var1);
         this.verifyExtensionContainingType(var2);
         Descriptors.FieldDescriptor var3 = var2.getDescriptor();
         Object var4 = this.extensions == null ? null : this.extensions.getField(var3);
         if (var4 == null) {
            if (var3.isRepeated()) {
               return (Type)Collections.emptyList();
            } else {
               return (Type)(var3.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE
                  ? var2.getMessageDefaultInstance()
                  : var2.fromReflectionType(var3.getDefaultValue()));
            }
         } else {
            return (Type)var2.fromReflectionType(var4);
         }
      }

      @Override
      public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> var1, int var2) {
         Extension var3 = GeneratedMessageV3.checkNotLite(var1);
         this.verifyExtensionContainingType(var3);
         Descriptors.FieldDescriptor var4 = var3.getDescriptor();
         if (this.extensions == null) {
            throw new IndexOutOfBoundsException();
         } else {
            return (Type)var3.singularFromReflectionType(this.extensions.getRepeatedField(var4, var2));
         }
      }

      public final <Type> BuilderType setExtension(ExtensionLite<MessageType, Type> var1, Type var2) {
         Extension var3 = GeneratedMessageV3.checkNotLite(var1);
         this.verifyExtensionContainingType(var3);
         this.ensureExtensionsIsMutable();
         Descriptors.FieldDescriptor var4 = var3.getDescriptor();
         this.extensions.setField(var4, var3.toReflectionType(var2));
         this.onChanged();
         return (BuilderType)this;
      }

      public final <Type> BuilderType setExtension(ExtensionLite<MessageType, List<Type>> var1, int var2, Type var3) {
         Extension var4 = GeneratedMessageV3.checkNotLite(var1);
         this.verifyExtensionContainingType(var4);
         this.ensureExtensionsIsMutable();
         Descriptors.FieldDescriptor var5 = var4.getDescriptor();
         this.extensions.setRepeatedField(var5, var2, var4.singularToReflectionType(var3));
         this.onChanged();
         return (BuilderType)this;
      }

      public final <Type> BuilderType addExtension(ExtensionLite<MessageType, List<Type>> var1, Type var2) {
         Extension var3 = GeneratedMessageV3.checkNotLite(var1);
         this.verifyExtensionContainingType(var3);
         this.ensureExtensionsIsMutable();
         Descriptors.FieldDescriptor var4 = var3.getDescriptor();
         this.extensions.addRepeatedField(var4, var3.singularToReflectionType(var2));
         this.onChanged();
         return (BuilderType)this;
      }

      public final BuilderType clearExtension(ExtensionLite<MessageType, ?> var1) {
         Extension var2 = GeneratedMessageV3.checkNotLite(var1);
         this.verifyExtensionContainingType(var2);
         this.ensureExtensionsIsMutable();
         this.extensions.clearField(var2.getDescriptor());
         this.onChanged();
         return (BuilderType)this;
      }

      @Override
      public final <Type> boolean hasExtension(Extension<MessageType, Type> var1) {
         return this.hasExtension(var1);
      }

      @Override
      public final <Type> boolean hasExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> var1) {
         return this.hasExtension(var1);
      }

      @Override
      public final <Type> int getExtensionCount(Extension<MessageType, List<Type>> var1) {
         return this.getExtensionCount(var1);
      }

      @Override
      public final <Type> int getExtensionCount(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> var1) {
         return this.getExtensionCount(var1);
      }

      @Override
      public final <Type> Type getExtension(Extension<MessageType, Type> var1) {
         return this.getExtension(var1);
      }

      @Override
      public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> var1) {
         return this.getExtension(var1);
      }

      @Override
      public final <Type> Type getExtension(Extension<MessageType, List<Type>> var1, int var2) {
         return this.getExtension(var1, var2);
      }

      @Override
      public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> var1, int var2) {
         return this.getExtension(var1, var2);
      }

      public final <Type> BuilderType setExtension(Extension<MessageType, Type> var1, Type var2) {
         return this.setExtension(var1, (Type)var2);
      }

      public <Type> BuilderType setExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> var1, Type var2) {
         return this.setExtension(var1, (Type)var2);
      }

      public final <Type> BuilderType setExtension(Extension<MessageType, List<Type>> var1, int var2, Type var3) {
         return this.setExtension(var1, var2, var3);
      }

      public <Type> BuilderType setExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> var1, int var2, Type var3) {
         return this.setExtension(var1, var2, var3);
      }

      public final <Type> BuilderType addExtension(Extension<MessageType, List<Type>> var1, Type var2) {
         return this.addExtension(var1, var2);
      }

      public <Type> BuilderType addExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> var1, Type var2) {
         return this.addExtension(var1, var2);
      }

      public final <Type> BuilderType clearExtension(Extension<MessageType, ?> var1) {
         return this.clearExtension(var1);
      }

      public <Type> BuilderType clearExtension(GeneratedMessage.GeneratedExtension<MessageType, ?> var1) {
         return this.clearExtension(var1);
      }

      protected boolean extensionsAreInitialized() {
         return this.extensions == null ? true : this.extensions.isInitialized();
      }

      private FieldSet<Descriptors.FieldDescriptor> buildExtensions() {
         return this.extensions == null ? FieldSet.emptySet() : this.extensions.build();
      }

      @Override
      public boolean isInitialized() {
         return super.isInitialized() && this.extensionsAreInitialized();
      }

      @Override
      public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
         Map var1 = super.getAllFieldsMutable();
         if (this.extensions != null) {
            var1.putAll(this.extensions.getAllFields());
         }

         return Collections.unmodifiableMap(var1);
      }

      @Override
      public Object getField(Descriptors.FieldDescriptor var1) {
         if (var1.isExtension()) {
            this.verifyContainingType(var1);
            Object var2 = this.extensions == null ? null : this.extensions.getField(var1);
            if (var2 == null) {
               return var1.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE
                  ? DynamicMessage.getDefaultInstance(var1.getMessageType())
                  : var1.getDefaultValue();
            } else {
               return var2;
            }
         } else {
            return super.getField(var1);
         }
      }

      @Override
      public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor var1) {
         if (var1.isExtension()) {
            this.verifyContainingType(var1);
            if (var1.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
               throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
            } else {
               this.ensureExtensionsIsMutable();
               Object var2 = this.extensions.getFieldAllowBuilders(var1);
               if (var2 == null) {
                  DynamicMessage.Builder var4 = DynamicMessage.newBuilder(var1.getMessageType());
                  this.extensions.setField(var1, var4);
                  this.onChanged();
                  return var4;
               } else if (var2 instanceof Message.Builder) {
                  return (Message.Builder)var2;
               } else if (var2 instanceof Message) {
                  Message.Builder var3 = ((Message)var2).toBuilder();
                  this.extensions.setField(var1, var3);
                  this.onChanged();
                  return var3;
               } else {
                  throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
               }
            }
         } else {
            return super.getFieldBuilder(var1);
         }
      }

      @Override
      public int getRepeatedFieldCount(Descriptors.FieldDescriptor var1) {
         if (var1.isExtension()) {
            this.verifyContainingType(var1);
            return this.extensions == null ? 0 : this.extensions.getRepeatedFieldCount(var1);
         } else {
            return super.getRepeatedFieldCount(var1);
         }
      }

      @Override
      public Object getRepeatedField(Descriptors.FieldDescriptor var1, int var2) {
         if (var1.isExtension()) {
            this.verifyContainingType(var1);
            if (this.extensions == null) {
               throw new IndexOutOfBoundsException();
            } else {
               return this.extensions.getRepeatedField(var1, var2);
            }
         } else {
            return super.getRepeatedField(var1, var2);
         }
      }

      @Override
      public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor var1, int var2) {
         if (var1.isExtension()) {
            this.verifyContainingType(var1);
            this.ensureExtensionsIsMutable();
            if (var1.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
               throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
            } else {
               Object var3 = this.extensions.getRepeatedFieldAllowBuilders(var1, var2);
               if (var3 instanceof Message.Builder) {
                  return (Message.Builder)var3;
               } else if (var3 instanceof Message) {
                  Message.Builder var4 = ((Message)var3).toBuilder();
                  this.extensions.setRepeatedField(var1, var2, var4);
                  this.onChanged();
                  return var4;
               } else {
                  throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
               }
            }
         } else {
            return super.getRepeatedFieldBuilder(var1, var2);
         }
      }

      @Override
      public boolean hasField(Descriptors.FieldDescriptor var1) {
         if (var1.isExtension()) {
            this.verifyContainingType(var1);
            return this.extensions == null ? false : this.extensions.hasField(var1);
         } else {
            return super.hasField(var1);
         }
      }

      public BuilderType setField(Descriptors.FieldDescriptor var1, Object var2) {
         if (var1.isExtension()) {
            this.verifyContainingType(var1);
            this.ensureExtensionsIsMutable();
            this.extensions.setField(var1, var2);
            this.onChanged();
            return (BuilderType)this;
         } else {
            return super.setField(var1, var2);
         }
      }

      public BuilderType clearField(Descriptors.FieldDescriptor var1) {
         if (var1.isExtension()) {
            this.verifyContainingType(var1);
            this.ensureExtensionsIsMutable();
            this.extensions.clearField(var1);
            this.onChanged();
            return (BuilderType)this;
         } else {
            return super.clearField(var1);
         }
      }

      public BuilderType setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         if (var1.isExtension()) {
            this.verifyContainingType(var1);
            this.ensureExtensionsIsMutable();
            this.extensions.setRepeatedField(var1, var2, var3);
            this.onChanged();
            return (BuilderType)this;
         } else {
            return super.setRepeatedField(var1, var2, var3);
         }
      }

      public BuilderType addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         if (var1.isExtension()) {
            this.verifyContainingType(var1);
            this.ensureExtensionsIsMutable();
            this.extensions.addRepeatedField(var1, var2);
            this.onChanged();
            return (BuilderType)this;
         } else {
            return super.addRepeatedField(var1, var2);
         }
      }

      @Override
      public Message.Builder newBuilderForField(Descriptors.FieldDescriptor var1) {
         return (Message.Builder)(var1.isExtension() ? DynamicMessage.newBuilder(var1.getMessageType()) : super.newBuilderForField(var1));
      }

      protected final void mergeExtensionFields(GeneratedMessageV3.ExtendableMessage var1) {
         if (var1.extensions != null) {
            this.ensureExtensionsIsMutable();
            this.extensions.mergeFrom(var1.extensions);
            this.onChanged();
         }
      }

      private void verifyContainingType(Descriptors.FieldDescriptor var1) {
         if (var1.getContainingType() != this.getDescriptorForType()) {
            throw new IllegalArgumentException("FieldDescriptor does not match message type.");
         }
      }
   }

   public abstract static class ExtendableMessage<MessageType extends GeneratedMessageV3.ExtendableMessage>
      extends GeneratedMessageV3
      implements GeneratedMessageV3.ExtendableMessageOrBuilder<MessageType> {
      private static final long serialVersionUID = 1L;
      private final FieldSet<Descriptors.FieldDescriptor> extensions;

      protected ExtendableMessage() {
         this.extensions = FieldSet.newFieldSet();
      }

      protected ExtendableMessage(GeneratedMessageV3.ExtendableBuilder<MessageType, ?> var1) {
         super(var1);
         this.extensions = var1.buildExtensions();
      }

      private void verifyExtensionContainingType(Extension<MessageType, ?> var1) {
         if (var1.getDescriptor().getContainingType() != this.getDescriptorForType()) {
            throw new IllegalArgumentException(
               "Extension is for type \""
                  + var1.getDescriptor().getContainingType().getFullName()
                  + "\" which does not match message type \""
                  + this.getDescriptorForType().getFullName()
                  + "\"."
            );
         }
      }

      @Override
      public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> var1) {
         Extension var2 = GeneratedMessageV3.checkNotLite(var1);
         this.verifyExtensionContainingType(var2);
         return this.extensions.hasField(var2.getDescriptor());
      }

      @Override
      public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> var1) {
         Extension var2 = GeneratedMessageV3.checkNotLite(var1);
         this.verifyExtensionContainingType(var2);
         Descriptors.FieldDescriptor var3 = var2.getDescriptor();
         return this.extensions.getRepeatedFieldCount(var3);
      }

      @Override
      public final <Type> Type getExtension(ExtensionLite<MessageType, Type> var1) {
         Extension var2 = GeneratedMessageV3.checkNotLite(var1);
         this.verifyExtensionContainingType(var2);
         Descriptors.FieldDescriptor var3 = var2.getDescriptor();
         Object var4 = this.extensions.getField(var3);
         if (var4 == null) {
            if (var3.isRepeated()) {
               return (Type)Collections.emptyList();
            } else {
               return (Type)(var3.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE
                  ? var2.getMessageDefaultInstance()
                  : var2.fromReflectionType(var3.getDefaultValue()));
            }
         } else {
            return (Type)var2.fromReflectionType(var4);
         }
      }

      @Override
      public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> var1, int var2) {
         Extension var3 = GeneratedMessageV3.checkNotLite(var1);
         this.verifyExtensionContainingType(var3);
         Descriptors.FieldDescriptor var4 = var3.getDescriptor();
         return (Type)var3.singularFromReflectionType(this.extensions.getRepeatedField(var4, var2));
      }

      @Override
      public final <Type> boolean hasExtension(Extension<MessageType, Type> var1) {
         return this.hasExtension(var1);
      }

      @Override
      public final <Type> boolean hasExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> var1) {
         return this.hasExtension(var1);
      }

      @Override
      public final <Type> int getExtensionCount(Extension<MessageType, List<Type>> var1) {
         return this.getExtensionCount(var1);
      }

      @Override
      public final <Type> int getExtensionCount(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> var1) {
         return this.getExtensionCount(var1);
      }

      @Override
      public final <Type> Type getExtension(Extension<MessageType, Type> var1) {
         return this.getExtension(var1);
      }

      @Override
      public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> var1) {
         return this.getExtension(var1);
      }

      @Override
      public final <Type> Type getExtension(Extension<MessageType, List<Type>> var1, int var2) {
         return this.getExtension(var1, var2);
      }

      @Override
      public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> var1, int var2) {
         return this.getExtension(var1, var2);
      }

      protected boolean extensionsAreInitialized() {
         return this.extensions.isInitialized();
      }

      @Override
      public boolean isInitialized() {
         return super.isInitialized() && this.extensionsAreInitialized();
      }

      @Override
      protected boolean parseUnknownField(CodedInputStream var1, UnknownFieldSet.Builder var2, ExtensionRegistryLite var3, int var4) throws IOException {
         return MessageReflection.mergeFieldFrom(
            var1,
            var1.shouldDiscardUnknownFields() ? null : var2,
            var3,
            this.getDescriptorForType(),
            new MessageReflection.ExtensionAdapter(this.extensions),
            var4
         );
      }

      @Override
      protected boolean parseUnknownFieldProto3(CodedInputStream var1, UnknownFieldSet.Builder var2, ExtensionRegistryLite var3, int var4) throws IOException {
         return this.parseUnknownField(var1, var2, var3, var4);
      }

      @Override
      protected void makeExtensionsImmutable() {
         this.extensions.makeImmutable();
      }

      protected GeneratedMessageV3.ExtendableMessage<MessageType>.ExtensionWriter newExtensionWriter() {
         return new GeneratedMessageV3.ExtendableMessage.ExtensionWriter(false);
      }

      protected GeneratedMessageV3.ExtendableMessage<MessageType>.ExtensionWriter newMessageSetExtensionWriter() {
         return new GeneratedMessageV3.ExtendableMessage.ExtensionWriter(true);
      }

      protected int extensionsSerializedSize() {
         return this.extensions.getSerializedSize();
      }

      protected int extensionsSerializedSizeAsMessageSet() {
         return this.extensions.getMessageSetSerializedSize();
      }

      protected Map<Descriptors.FieldDescriptor, Object> getExtensionFields() {
         return this.extensions.getAllFields();
      }

      @Override
      public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
         Map var1 = super.getAllFieldsMutable(false);
         var1.putAll(this.getExtensionFields());
         return Collections.unmodifiableMap(var1);
      }

      @Override
      public Map<Descriptors.FieldDescriptor, Object> getAllFieldsRaw() {
         Map var1 = super.getAllFieldsMutable(false);
         var1.putAll(this.getExtensionFields());
         return Collections.unmodifiableMap(var1);
      }

      @Override
      public boolean hasField(Descriptors.FieldDescriptor var1) {
         if (var1.isExtension()) {
            this.verifyContainingType(var1);
            return this.extensions.hasField(var1);
         } else {
            return super.hasField(var1);
         }
      }

      @Override
      public Object getField(Descriptors.FieldDescriptor var1) {
         if (var1.isExtension()) {
            this.verifyContainingType(var1);
            Object var2 = this.extensions.getField(var1);
            if (var2 == null) {
               if (var1.isRepeated()) {
                  return Collections.emptyList();
               } else {
                  return var1.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE
                     ? DynamicMessage.getDefaultInstance(var1.getMessageType())
                     : var1.getDefaultValue();
               }
            } else {
               return var2;
            }
         } else {
            return super.getField(var1);
         }
      }

      @Override
      public int getRepeatedFieldCount(Descriptors.FieldDescriptor var1) {
         if (var1.isExtension()) {
            this.verifyContainingType(var1);
            return this.extensions.getRepeatedFieldCount(var1);
         } else {
            return super.getRepeatedFieldCount(var1);
         }
      }

      @Override
      public Object getRepeatedField(Descriptors.FieldDescriptor var1, int var2) {
         if (var1.isExtension()) {
            this.verifyContainingType(var1);
            return this.extensions.getRepeatedField(var1, var2);
         } else {
            return super.getRepeatedField(var1, var2);
         }
      }

      private void verifyContainingType(Descriptors.FieldDescriptor var1) {
         if (var1.getContainingType() != this.getDescriptorForType()) {
            throw new IllegalArgumentException("FieldDescriptor does not match message type.");
         }
      }

      protected class ExtensionWriter {
         private final Iterator<Entry<Descriptors.FieldDescriptor, Object>> iter = ExtendableMessage.this.extensions.iterator();
         private Entry<Descriptors.FieldDescriptor, Object> next;
         private final boolean messageSetWireFormat;

         private ExtensionWriter(boolean var2) {
            if (this.iter.hasNext()) {
               this.next = this.iter.next();
            }

            this.messageSetWireFormat = var2;
         }

         public void writeUntil(int var1, CodedOutputStream var2) throws IOException {
            while (this.next != null && this.next.getKey().getNumber() < var1) {
               Descriptors.FieldDescriptor var3 = this.next.getKey();
               if (!this.messageSetWireFormat || var3.getLiteJavaType() != WireFormat.JavaType.MESSAGE || var3.isRepeated()) {
                  FieldSet.writeField(var3, this.next.getValue(), var2);
               } else if (this.next instanceof LazyField.LazyEntry) {
                  var2.writeRawMessageSetExtension(var3.getNumber(), ((LazyField.LazyEntry)this.next).getField().toByteString());
               } else {
                  var2.writeMessageSetExtension(var3.getNumber(), (Message)this.next.getValue());
               }

               if (this.iter.hasNext()) {
                  this.next = this.iter.next();
               } else {
                  this.next = null;
               }
            }
         }
      }
   }

   public interface ExtendableMessageOrBuilder<MessageType extends GeneratedMessageV3.ExtendableMessage> extends MessageOrBuilder {
      @Override
      Message getDefaultInstanceForType();

      <Type> boolean hasExtension(ExtensionLite<MessageType, Type> var1);

      <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> var1);

      <Type> Type getExtension(ExtensionLite<MessageType, Type> var1);

      <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> var1, int var2);

      <Type> boolean hasExtension(Extension<MessageType, Type> var1);

      <Type> boolean hasExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> var1);

      <Type> int getExtensionCount(Extension<MessageType, List<Type>> var1);

      <Type> int getExtensionCount(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> var1);

      <Type> Type getExtension(Extension<MessageType, Type> var1);

      <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> var1);

      <Type> Type getExtension(Extension<MessageType, List<Type>> var1, int var2);

      <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> var1, int var2);
   }

   interface ExtensionDescriptorRetriever {
      Descriptors.FieldDescriptor getDescriptor();
   }

   public static final class FieldAccessorTable {
      private final Descriptors.Descriptor descriptor;
      private final GeneratedMessageV3.FieldAccessorTable.FieldAccessor[] fields;
      private String[] camelCaseNames;
      private final GeneratedMessageV3.FieldAccessorTable.OneofAccessor[] oneofs;
      private volatile boolean initialized;

      public FieldAccessorTable(
         Descriptors.Descriptor var1, String[] var2, Class<? extends GeneratedMessageV3> var3, Class<? extends GeneratedMessageV3.Builder> var4
      ) {
         this(var1, var2);
         this.ensureFieldAccessorsInitialized(var3, var4);
      }

      public FieldAccessorTable(Descriptors.Descriptor var1, String[] var2) {
         this.descriptor = var1;
         this.camelCaseNames = var2;
         this.fields = new GeneratedMessageV3.FieldAccessorTable.FieldAccessor[var1.getFields().size()];
         this.oneofs = new GeneratedMessageV3.FieldAccessorTable.OneofAccessor[var1.getOneofs().size()];
         this.initialized = false;
      }

      public GeneratedMessageV3.FieldAccessorTable ensureFieldAccessorsInitialized(
         Class<? extends GeneratedMessageV3> var1, Class<? extends GeneratedMessageV3.Builder> var2
      ) {
         if (this.initialized) {
            return this;
         } else {
            synchronized (this) {
               if (this.initialized) {
                  return this;
               } else {
                  int var4 = this.fields.length;

                  for (int var5 = 0; var5 < var4; var5++) {
                     Descriptors.FieldDescriptor var6 = this.descriptor.getFields().get(var5);
                     String var7 = null;
                     if (var6.getContainingOneof() != null) {
                        var7 = this.camelCaseNames[var4 + var6.getContainingOneof().getIndex()];
                     }

                     if (var6.isRepeated()) {
                        if (var6.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                           if (var6.isMapField()) {
                              this.fields[var5] = new GeneratedMessageV3.FieldAccessorTable.MapFieldAccessor(var6, this.camelCaseNames[var5], var1, var2);
                           } else {
                              this.fields[var5] = new GeneratedMessageV3.FieldAccessorTable.RepeatedMessageFieldAccessor(
                                 var6, this.camelCaseNames[var5], var1, var2
                              );
                           }
                        } else if (var6.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
                           this.fields[var5] = new GeneratedMessageV3.FieldAccessorTable.RepeatedEnumFieldAccessor(var6, this.camelCaseNames[var5], var1, var2);
                        } else {
                           this.fields[var5] = new GeneratedMessageV3.FieldAccessorTable.RepeatedFieldAccessor(var6, this.camelCaseNames[var5], var1, var2);
                        }
                     } else if (var6.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                        this.fields[var5] = new GeneratedMessageV3.FieldAccessorTable.SingularMessageFieldAccessor(
                           var6, this.camelCaseNames[var5], var1, var2, var7
                        );
                     } else if (var6.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
                        this.fields[var5] = new GeneratedMessageV3.FieldAccessorTable.SingularEnumFieldAccessor(
                           var6, this.camelCaseNames[var5], var1, var2, var7
                        );
                     } else if (var6.getJavaType() == Descriptors.FieldDescriptor.JavaType.STRING) {
                        this.fields[var5] = new GeneratedMessageV3.FieldAccessorTable.SingularStringFieldAccessor(
                           var6, this.camelCaseNames[var5], var1, var2, var7
                        );
                     } else {
                        this.fields[var5] = new GeneratedMessageV3.FieldAccessorTable.SingularFieldAccessor(var6, this.camelCaseNames[var5], var1, var2, var7);
                     }
                  }

                  int var10 = this.oneofs.length;

                  for (int var11 = 0; var11 < var10; var11++) {
                     this.oneofs[var11] = new GeneratedMessageV3.FieldAccessorTable.OneofAccessor(
                        this.descriptor, this.camelCaseNames[var11 + var4], var1, var2
                     );
                  }

                  this.initialized = true;
                  this.camelCaseNames = null;
                  return this;
               }
            }
         }
      }

      private GeneratedMessageV3.FieldAccessorTable.FieldAccessor getField(Descriptors.FieldDescriptor var1) {
         if (var1.getContainingType() != this.descriptor) {
            throw new IllegalArgumentException("FieldDescriptor does not match message type.");
         } else if (var1.isExtension()) {
            throw new IllegalArgumentException("This type does not have extensions.");
         } else {
            return this.fields[var1.getIndex()];
         }
      }

      private GeneratedMessageV3.FieldAccessorTable.OneofAccessor getOneof(Descriptors.OneofDescriptor var1) {
         if (var1.getContainingType() != this.descriptor) {
            throw new IllegalArgumentException("OneofDescriptor does not match message type.");
         } else {
            return this.oneofs[var1.getIndex()];
         }
      }

      private static boolean supportFieldPresence(Descriptors.FileDescriptor var0) {
         return var0.getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO2;
      }

      private interface FieldAccessor {
         Object get(GeneratedMessageV3 var1);

         Object get(GeneratedMessageV3.Builder var1);

         Object getRaw(GeneratedMessageV3 var1);

         Object getRaw(GeneratedMessageV3.Builder var1);

         void set(GeneratedMessageV3.Builder var1, Object var2);

         Object getRepeated(GeneratedMessageV3 var1, int var2);

         Object getRepeated(GeneratedMessageV3.Builder var1, int var2);

         Object getRepeatedRaw(GeneratedMessageV3 var1, int var2);

         Object getRepeatedRaw(GeneratedMessageV3.Builder var1, int var2);

         void setRepeated(GeneratedMessageV3.Builder var1, int var2, Object var3);

         void addRepeated(GeneratedMessageV3.Builder var1, Object var2);

         boolean has(GeneratedMessageV3 var1);

         boolean has(GeneratedMessageV3.Builder var1);

         int getRepeatedCount(GeneratedMessageV3 var1);

         int getRepeatedCount(GeneratedMessageV3.Builder var1);

         void clear(GeneratedMessageV3.Builder var1);

         Message.Builder newBuilder();

         Message.Builder getBuilder(GeneratedMessageV3.Builder var1);

         Message.Builder getRepeatedBuilder(GeneratedMessageV3.Builder var1, int var2);
      }

      private static class MapFieldAccessor implements GeneratedMessageV3.FieldAccessorTable.FieldAccessor {
         private final Descriptors.FieldDescriptor field;
         private final Message mapEntryMessageDefaultInstance;

         MapFieldAccessor(
            Descriptors.FieldDescriptor var1, String var2, Class<? extends GeneratedMessageV3> var3, Class<? extends GeneratedMessageV3.Builder> var4
         ) {
            this.field = var1;
            java.lang.reflect.Method var5 = GeneratedMessageV3.getMethodOrDie(var3, "getDefaultInstance");
            MapField var6 = this.getMapField((GeneratedMessageV3)GeneratedMessageV3.invokeOrDie(var5, null));
            this.mapEntryMessageDefaultInstance = var6.getMapEntryMessageDefaultInstance();
         }

         private MapField<?, ?> getMapField(GeneratedMessageV3 var1) {
            return var1.internalGetMapField(this.field.getNumber());
         }

         private MapField<?, ?> getMapField(GeneratedMessageV3.Builder var1) {
            return var1.internalGetMapField(this.field.getNumber());
         }

         private MapField<?, ?> getMutableMapField(GeneratedMessageV3.Builder var1) {
            return var1.internalGetMutableMapField(this.field.getNumber());
         }

         private Message coerceType(Message var1) {
            if (var1 == null) {
               return null;
            } else {
               return this.mapEntryMessageDefaultInstance.getClass().isInstance(var1)
                  ? var1
                  : this.mapEntryMessageDefaultInstance.toBuilder().mergeFrom(var1).build();
            }
         }

         @Override
         public Object get(GeneratedMessageV3 var1) {
            ArrayList var2 = new ArrayList();

            for (int var3 = 0; var3 < this.getRepeatedCount(var1); var3++) {
               var2.add(this.getRepeated(var1, var3));
            }

            return Collections.unmodifiableList(var2);
         }

         @Override
         public Object get(GeneratedMessageV3.Builder var1) {
            ArrayList var2 = new ArrayList();

            for (int var3 = 0; var3 < this.getRepeatedCount(var1); var3++) {
               var2.add(this.getRepeated(var1, var3));
            }

            return Collections.unmodifiableList(var2);
         }

         @Override
         public Object getRaw(GeneratedMessageV3 var1) {
            return this.get(var1);
         }

         @Override
         public Object getRaw(GeneratedMessageV3.Builder var1) {
            return this.get(var1);
         }

         @Override
         public void set(GeneratedMessageV3.Builder var1, Object var2) {
            this.clear(var1);

            for (Object var4 : (List)var2) {
               this.addRepeated(var1, var4);
            }
         }

         @Override
         public Object getRepeated(GeneratedMessageV3 var1, int var2) {
            return this.getMapField(var1).getList().get(var2);
         }

         @Override
         public Object getRepeated(GeneratedMessageV3.Builder var1, int var2) {
            return this.getMapField(var1).getList().get(var2);
         }

         @Override
         public Object getRepeatedRaw(GeneratedMessageV3 var1, int var2) {
            return this.getRepeated(var1, var2);
         }

         @Override
         public Object getRepeatedRaw(GeneratedMessageV3.Builder var1, int var2) {
            return this.getRepeated(var1, var2);
         }

         @Override
         public void setRepeated(GeneratedMessageV3.Builder var1, int var2, Object var3) {
            this.getMutableMapField(var1).getMutableList().set(var2, this.coerceType((Message)var3));
         }

         @Override
         public void addRepeated(GeneratedMessageV3.Builder var1, Object var2) {
            this.getMutableMapField(var1).getMutableList().add(this.coerceType((Message)var2));
         }

         @Override
         public boolean has(GeneratedMessageV3 var1) {
            throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
         }

         @Override
         public boolean has(GeneratedMessageV3.Builder var1) {
            throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
         }

         @Override
         public int getRepeatedCount(GeneratedMessageV3 var1) {
            return this.getMapField(var1).getList().size();
         }

         @Override
         public int getRepeatedCount(GeneratedMessageV3.Builder var1) {
            return this.getMapField(var1).getList().size();
         }

         @Override
         public void clear(GeneratedMessageV3.Builder var1) {
            this.getMutableMapField(var1).getMutableList().clear();
         }

         @Override
         public Message.Builder newBuilder() {
            return this.mapEntryMessageDefaultInstance.newBuilderForType();
         }

         @Override
         public Message.Builder getBuilder(GeneratedMessageV3.Builder var1) {
            throw new UnsupportedOperationException("Nested builder not supported for map fields.");
         }

         @Override
         public Message.Builder getRepeatedBuilder(GeneratedMessageV3.Builder var1, int var2) {
            throw new UnsupportedOperationException("Nested builder not supported for map fields.");
         }
      }

      private static class OneofAccessor {
         private final Descriptors.Descriptor descriptor;
         private final java.lang.reflect.Method caseMethod;
         private final java.lang.reflect.Method caseMethodBuilder;
         private final java.lang.reflect.Method clearMethod;

         OneofAccessor(Descriptors.Descriptor var1, String var2, Class<? extends GeneratedMessageV3> var3, Class<? extends GeneratedMessageV3.Builder> var4) {
            this.descriptor = var1;
            this.caseMethod = GeneratedMessageV3.getMethodOrDie(var3, "get" + var2 + "Case");
            this.caseMethodBuilder = GeneratedMessageV3.getMethodOrDie(var4, "get" + var2 + "Case");
            this.clearMethod = GeneratedMessageV3.getMethodOrDie(var4, "clear" + var2);
         }

         public boolean has(GeneratedMessageV3 var1) {
            return ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, var1)).getNumber() != 0;
         }

         public boolean has(GeneratedMessageV3.Builder var1) {
            return ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, var1)).getNumber() != 0;
         }

         public Descriptors.FieldDescriptor get(GeneratedMessageV3 var1) {
            int var2 = ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, var1)).getNumber();
            return var2 > 0 ? this.descriptor.findFieldByNumber(var2) : null;
         }

         public Descriptors.FieldDescriptor get(GeneratedMessageV3.Builder var1) {
            int var2 = ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, var1)).getNumber();
            return var2 > 0 ? this.descriptor.findFieldByNumber(var2) : null;
         }

         public void clear(GeneratedMessageV3.Builder var1) {
            GeneratedMessageV3.invokeOrDie(this.clearMethod, var1);
         }
      }

      private static final class RepeatedEnumFieldAccessor extends GeneratedMessageV3.FieldAccessorTable.RepeatedFieldAccessor {
         private Descriptors.EnumDescriptor enumDescriptor;
         private final java.lang.reflect.Method valueOfMethod;
         private final java.lang.reflect.Method getValueDescriptorMethod;
         private boolean supportUnknownEnumValue;
         private java.lang.reflect.Method getRepeatedValueMethod;
         private java.lang.reflect.Method getRepeatedValueMethodBuilder;
         private java.lang.reflect.Method setRepeatedValueMethod;
         private java.lang.reflect.Method addRepeatedValueMethod;

         RepeatedEnumFieldAccessor(
            Descriptors.FieldDescriptor var1, String var2, Class<? extends GeneratedMessageV3> var3, Class<? extends GeneratedMessageV3.Builder> var4
         ) {
            super(var1, var2, var3, var4);
            this.enumDescriptor = var1.getEnumType();
            this.valueOfMethod = GeneratedMessageV3.getMethodOrDie(this.type, "valueOf", Descriptors.EnumValueDescriptor.class);
            this.getValueDescriptorMethod = GeneratedMessageV3.getMethodOrDie(this.type, "getValueDescriptor");
            this.supportUnknownEnumValue = var1.getFile().supportsUnknownEnumValue();
            if (this.supportUnknownEnumValue) {
               this.getRepeatedValueMethod = GeneratedMessageV3.getMethodOrDie(var3, "get" + var2 + "Value", int.class);
               this.getRepeatedValueMethodBuilder = GeneratedMessageV3.getMethodOrDie(var4, "get" + var2 + "Value", int.class);
               this.setRepeatedValueMethod = GeneratedMessageV3.getMethodOrDie(var4, "set" + var2 + "Value", int.class, int.class);
               this.addRepeatedValueMethod = GeneratedMessageV3.getMethodOrDie(var4, "add" + var2 + "Value", int.class);
            }
         }

         @Override
         public Object get(GeneratedMessageV3 var1) {
            ArrayList var2 = new ArrayList();
            int var3 = this.getRepeatedCount(var1);

            for (int var4 = 0; var4 < var3; var4++) {
               var2.add(this.getRepeated(var1, var4));
            }

            return Collections.unmodifiableList(var2);
         }

         @Override
         public Object get(GeneratedMessageV3.Builder var1) {
            ArrayList var2 = new ArrayList();
            int var3 = this.getRepeatedCount(var1);

            for (int var4 = 0; var4 < var3; var4++) {
               var2.add(this.getRepeated(var1, var4));
            }

            return Collections.unmodifiableList(var2);
         }

         @Override
         public Object getRepeated(GeneratedMessageV3 var1, int var2) {
            if (this.supportUnknownEnumValue) {
               int var3 = (Integer)GeneratedMessageV3.invokeOrDie(this.getRepeatedValueMethod, var1, var2);
               return this.enumDescriptor.findValueByNumberCreatingIfUnknown(var3);
            } else {
               return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.getRepeated(var1, var2));
            }
         }

         @Override
         public Object getRepeated(GeneratedMessageV3.Builder var1, int var2) {
            if (this.supportUnknownEnumValue) {
               int var3 = (Integer)GeneratedMessageV3.invokeOrDie(this.getRepeatedValueMethodBuilder, var1, var2);
               return this.enumDescriptor.findValueByNumberCreatingIfUnknown(var3);
            } else {
               return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.getRepeated(var1, var2));
            }
         }

         @Override
         public void setRepeated(GeneratedMessageV3.Builder var1, int var2, Object var3) {
            if (this.supportUnknownEnumValue) {
               GeneratedMessageV3.invokeOrDie(this.setRepeatedValueMethod, var1, var2, ((Descriptors.EnumValueDescriptor)var3).getNumber());
            } else {
               super.setRepeated(var1, var2, GeneratedMessageV3.invokeOrDie(this.valueOfMethod, null, var3));
            }
         }

         @Override
         public void addRepeated(GeneratedMessageV3.Builder var1, Object var2) {
            if (this.supportUnknownEnumValue) {
               GeneratedMessageV3.invokeOrDie(this.addRepeatedValueMethod, var1, ((Descriptors.EnumValueDescriptor)var2).getNumber());
            } else {
               super.addRepeated(var1, GeneratedMessageV3.invokeOrDie(this.valueOfMethod, null, var2));
            }
         }
      }

      private static class RepeatedFieldAccessor implements GeneratedMessageV3.FieldAccessorTable.FieldAccessor {
         protected final Class type;
         protected final GeneratedMessageV3.FieldAccessorTable.RepeatedFieldAccessor.MethodInvoker invoker;

         RepeatedFieldAccessor(
            Descriptors.FieldDescriptor var1, String var2, Class<? extends GeneratedMessageV3> var3, Class<? extends GeneratedMessageV3.Builder> var4
         ) {
            GeneratedMessageV3.FieldAccessorTable.RepeatedFieldAccessor.ReflectionInvoker var5 = new GeneratedMessageV3.FieldAccessorTable.RepeatedFieldAccessor.ReflectionInvoker(
               var1, var2, var3, var4
            );
            this.type = var5.getRepeatedMethod.getReturnType();
            this.invoker = getMethodInvoker(var5);
         }

         static GeneratedMessageV3.FieldAccessorTable.RepeatedFieldAccessor.MethodInvoker getMethodInvoker(
            GeneratedMessageV3.FieldAccessorTable.RepeatedFieldAccessor.ReflectionInvoker var0
         ) {
            return var0;
         }

         @Override
         public Object get(GeneratedMessageV3 var1) {
            return this.invoker.get(var1);
         }

         @Override
         public Object get(GeneratedMessageV3.Builder var1) {
            return this.invoker.get(var1);
         }

         @Override
         public Object getRaw(GeneratedMessageV3 var1) {
            return this.get(var1);
         }

         @Override
         public Object getRaw(GeneratedMessageV3.Builder var1) {
            return this.get(var1);
         }

         @Override
         public void set(GeneratedMessageV3.Builder var1, Object var2) {
            this.clear(var1);

            for (Object var4 : (List)var2) {
               this.addRepeated(var1, var4);
            }
         }

         @Override
         public Object getRepeated(GeneratedMessageV3 var1, int var2) {
            return this.invoker.getRepeated(var1, var2);
         }

         @Override
         public Object getRepeated(GeneratedMessageV3.Builder var1, int var2) {
            return this.invoker.getRepeated(var1, var2);
         }

         @Override
         public Object getRepeatedRaw(GeneratedMessageV3 var1, int var2) {
            return this.getRepeated(var1, var2);
         }

         @Override
         public Object getRepeatedRaw(GeneratedMessageV3.Builder var1, int var2) {
            return this.getRepeated(var1, var2);
         }

         @Override
         public void setRepeated(GeneratedMessageV3.Builder var1, int var2, Object var3) {
            this.invoker.setRepeated(var1, var2, var3);
         }

         @Override
         public void addRepeated(GeneratedMessageV3.Builder var1, Object var2) {
            this.invoker.addRepeated(var1, var2);
         }

         @Override
         public boolean has(GeneratedMessageV3 var1) {
            throw new UnsupportedOperationException("hasField() called on a repeated field.");
         }

         @Override
         public boolean has(GeneratedMessageV3.Builder var1) {
            throw new UnsupportedOperationException("hasField() called on a repeated field.");
         }

         @Override
         public int getRepeatedCount(GeneratedMessageV3 var1) {
            return this.invoker.getRepeatedCount(var1);
         }

         @Override
         public int getRepeatedCount(GeneratedMessageV3.Builder var1) {
            return this.invoker.getRepeatedCount(var1);
         }

         @Override
         public void clear(GeneratedMessageV3.Builder var1) {
            this.invoker.clear(var1);
         }

         @Override
         public Message.Builder newBuilder() {
            throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
         }

         @Override
         public Message.Builder getBuilder(GeneratedMessageV3.Builder var1) {
            throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
         }

         @Override
         public Message.Builder getRepeatedBuilder(GeneratedMessageV3.Builder var1, int var2) {
            throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
         }

         interface MethodInvoker {
            Object get(GeneratedMessageV3 var1);

            Object get(GeneratedMessageV3.Builder<?> var1);

            Object getRepeated(GeneratedMessageV3 var1, int var2);

            Object getRepeated(GeneratedMessageV3.Builder<?> var1, int var2);

            void setRepeated(GeneratedMessageV3.Builder<?> var1, int var2, Object var3);

            void addRepeated(GeneratedMessageV3.Builder<?> var1, Object var2);

            int getRepeatedCount(GeneratedMessageV3 var1);

            int getRepeatedCount(GeneratedMessageV3.Builder<?> var1);

            void clear(GeneratedMessageV3.Builder<?> var1);
         }

         private static final class ReflectionInvoker implements GeneratedMessageV3.FieldAccessorTable.RepeatedFieldAccessor.MethodInvoker {
            protected final java.lang.reflect.Method getMethod;
            protected final java.lang.reflect.Method getMethodBuilder;
            protected final java.lang.reflect.Method getRepeatedMethod;
            protected final java.lang.reflect.Method getRepeatedMethodBuilder;
            protected final java.lang.reflect.Method setRepeatedMethod;
            protected final java.lang.reflect.Method addRepeatedMethod;
            protected final java.lang.reflect.Method getCountMethod;
            protected final java.lang.reflect.Method getCountMethodBuilder;
            protected final java.lang.reflect.Method clearMethod;

            ReflectionInvoker(
               Descriptors.FieldDescriptor var1, String var2, Class<? extends GeneratedMessageV3> var3, Class<? extends GeneratedMessageV3.Builder> var4
            ) {
               this.getMethod = GeneratedMessageV3.getMethodOrDie(var3, "get" + var2 + "List");
               this.getMethodBuilder = GeneratedMessageV3.getMethodOrDie(var4, "get" + var2 + "List");
               this.getRepeatedMethod = GeneratedMessageV3.getMethodOrDie(var3, "get" + var2, int.class);
               this.getRepeatedMethodBuilder = GeneratedMessageV3.getMethodOrDie(var4, "get" + var2, int.class);
               Class var5 = this.getRepeatedMethod.getReturnType();
               this.setRepeatedMethod = GeneratedMessageV3.getMethodOrDie(var4, "set" + var2, int.class, var5);
               this.addRepeatedMethod = GeneratedMessageV3.getMethodOrDie(var4, "add" + var2, var5);
               this.getCountMethod = GeneratedMessageV3.getMethodOrDie(var3, "get" + var2 + "Count");
               this.getCountMethodBuilder = GeneratedMessageV3.getMethodOrDie(var4, "get" + var2 + "Count");
               this.clearMethod = GeneratedMessageV3.getMethodOrDie(var4, "clear" + var2);
            }

            @Override
            public Object get(GeneratedMessageV3 var1) {
               return GeneratedMessageV3.invokeOrDie(this.getMethod, var1);
            }

            @Override
            public Object get(GeneratedMessageV3.Builder<?> var1) {
               return GeneratedMessageV3.invokeOrDie(this.getMethodBuilder, var1);
            }

            @Override
            public Object getRepeated(GeneratedMessageV3 var1, int var2) {
               return GeneratedMessageV3.invokeOrDie(this.getRepeatedMethod, var1, var2);
            }

            @Override
            public Object getRepeated(GeneratedMessageV3.Builder<?> var1, int var2) {
               return GeneratedMessageV3.invokeOrDie(this.getRepeatedMethodBuilder, var1, var2);
            }

            @Override
            public void setRepeated(GeneratedMessageV3.Builder<?> var1, int var2, Object var3) {
               GeneratedMessageV3.invokeOrDie(this.setRepeatedMethod, var1, var2, var3);
            }

            @Override
            public void addRepeated(GeneratedMessageV3.Builder<?> var1, Object var2) {
               GeneratedMessageV3.invokeOrDie(this.addRepeatedMethod, var1, var2);
            }

            @Override
            public int getRepeatedCount(GeneratedMessageV3 var1) {
               return (Integer)GeneratedMessageV3.invokeOrDie(this.getCountMethod, var1);
            }

            @Override
            public int getRepeatedCount(GeneratedMessageV3.Builder<?> var1) {
               return (Integer)GeneratedMessageV3.invokeOrDie(this.getCountMethodBuilder, var1);
            }

            @Override
            public void clear(GeneratedMessageV3.Builder<?> var1) {
               GeneratedMessageV3.invokeOrDie(this.clearMethod, var1);
            }
         }
      }

      private static final class RepeatedMessageFieldAccessor extends GeneratedMessageV3.FieldAccessorTable.RepeatedFieldAccessor {
         private final java.lang.reflect.Method newBuilderMethod = GeneratedMessageV3.getMethodOrDie(this.type, "newBuilder");
         private final java.lang.reflect.Method getBuilderMethodBuilder;

         RepeatedMessageFieldAccessor(
            Descriptors.FieldDescriptor var1, String var2, Class<? extends GeneratedMessageV3> var3, Class<? extends GeneratedMessageV3.Builder> var4
         ) {
            super(var1, var2, var3, var4);
            this.getBuilderMethodBuilder = GeneratedMessageV3.getMethodOrDie(var4, "get" + var2 + "Builder", int.class);
         }

         private Object coerceType(Object var1) {
            return this.type.isInstance(var1)
               ? var1
               : ((Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, null)).mergeFrom((Message)var1).build();
         }

         @Override
         public void setRepeated(GeneratedMessageV3.Builder var1, int var2, Object var3) {
            super.setRepeated(var1, var2, this.coerceType(var3));
         }

         @Override
         public void addRepeated(GeneratedMessageV3.Builder var1, Object var2) {
            super.addRepeated(var1, this.coerceType(var2));
         }

         @Override
         public Message.Builder newBuilder() {
            return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, null);
         }

         @Override
         public Message.Builder getRepeatedBuilder(GeneratedMessageV3.Builder var1, int var2) {
            return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.getBuilderMethodBuilder, var1, var2);
         }
      }

      private static final class SingularEnumFieldAccessor extends GeneratedMessageV3.FieldAccessorTable.SingularFieldAccessor {
         private Descriptors.EnumDescriptor enumDescriptor;
         private java.lang.reflect.Method valueOfMethod;
         private java.lang.reflect.Method getValueDescriptorMethod;
         private boolean supportUnknownEnumValue;
         private java.lang.reflect.Method getValueMethod;
         private java.lang.reflect.Method getValueMethodBuilder;
         private java.lang.reflect.Method setValueMethod;

         SingularEnumFieldAccessor(
            Descriptors.FieldDescriptor var1,
            String var2,
            Class<? extends GeneratedMessageV3> var3,
            Class<? extends GeneratedMessageV3.Builder> var4,
            String var5
         ) {
            super(var1, var2, var3, var4, var5);
            this.enumDescriptor = var1.getEnumType();
            this.valueOfMethod = GeneratedMessageV3.getMethodOrDie(this.type, "valueOf", Descriptors.EnumValueDescriptor.class);
            this.getValueDescriptorMethod = GeneratedMessageV3.getMethodOrDie(this.type, "getValueDescriptor");
            this.supportUnknownEnumValue = var1.getFile().supportsUnknownEnumValue();
            if (this.supportUnknownEnumValue) {
               this.getValueMethod = GeneratedMessageV3.getMethodOrDie(var3, "get" + var2 + "Value");
               this.getValueMethodBuilder = GeneratedMessageV3.getMethodOrDie(var4, "get" + var2 + "Value");
               this.setValueMethod = GeneratedMessageV3.getMethodOrDie(var4, "set" + var2 + "Value", int.class);
            }
         }

         @Override
         public Object get(GeneratedMessageV3 var1) {
            if (this.supportUnknownEnumValue) {
               int var2 = (Integer)GeneratedMessageV3.invokeOrDie(this.getValueMethod, var1);
               return this.enumDescriptor.findValueByNumberCreatingIfUnknown(var2);
            } else {
               return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.get(var1));
            }
         }

         @Override
         public Object get(GeneratedMessageV3.Builder var1) {
            if (this.supportUnknownEnumValue) {
               int var2 = (Integer)GeneratedMessageV3.invokeOrDie(this.getValueMethodBuilder, var1);
               return this.enumDescriptor.findValueByNumberCreatingIfUnknown(var2);
            } else {
               return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.get(var1));
            }
         }

         @Override
         public void set(GeneratedMessageV3.Builder var1, Object var2) {
            if (this.supportUnknownEnumValue) {
               GeneratedMessageV3.invokeOrDie(this.setValueMethod, var1, ((Descriptors.EnumValueDescriptor)var2).getNumber());
            } else {
               super.set(var1, GeneratedMessageV3.invokeOrDie(this.valueOfMethod, null, var2));
            }
         }
      }

      private static class SingularFieldAccessor implements GeneratedMessageV3.FieldAccessorTable.FieldAccessor {
         protected final Class<?> type;
         protected final Descriptors.FieldDescriptor field;
         protected final boolean isOneofField;
         protected final boolean hasHasMethod;
         protected final GeneratedMessageV3.FieldAccessorTable.SingularFieldAccessor.MethodInvoker invoker;

         SingularFieldAccessor(
            Descriptors.FieldDescriptor var1,
            String var2,
            Class<? extends GeneratedMessageV3> var3,
            Class<? extends GeneratedMessageV3.Builder> var4,
            String var5
         ) {
            this.isOneofField = var1.getContainingOneof() != null;
            this.hasHasMethod = GeneratedMessageV3.FieldAccessorTable.supportFieldPresence(var1.getFile())
               || !this.isOneofField && var1.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE;
            GeneratedMessageV3.FieldAccessorTable.SingularFieldAccessor.ReflectionInvoker var6 = new GeneratedMessageV3.FieldAccessorTable.SingularFieldAccessor.ReflectionInvoker(
               var1, var2, var3, var4, var5, this.isOneofField, this.hasHasMethod
            );
            this.field = var1;
            this.type = var6.getMethod.getReturnType();
            this.invoker = getMethodInvoker(var6);
         }

         static GeneratedMessageV3.FieldAccessorTable.SingularFieldAccessor.MethodInvoker getMethodInvoker(
            GeneratedMessageV3.FieldAccessorTable.SingularFieldAccessor.ReflectionInvoker var0
         ) {
            return var0;
         }

         @Override
         public Object get(GeneratedMessageV3 var1) {
            return this.invoker.get(var1);
         }

         @Override
         public Object get(GeneratedMessageV3.Builder var1) {
            return this.invoker.get(var1);
         }

         @Override
         public Object getRaw(GeneratedMessageV3 var1) {
            return this.get(var1);
         }

         @Override
         public Object getRaw(GeneratedMessageV3.Builder var1) {
            return this.get(var1);
         }

         @Override
         public void set(GeneratedMessageV3.Builder var1, Object var2) {
            this.invoker.set(var1, var2);
         }

         @Override
         public Object getRepeated(GeneratedMessageV3 var1, int var2) {
            throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
         }

         @Override
         public Object getRepeatedRaw(GeneratedMessageV3 var1, int var2) {
            throw new UnsupportedOperationException("getRepeatedFieldRaw() called on a singular field.");
         }

         @Override
         public Object getRepeated(GeneratedMessageV3.Builder var1, int var2) {
            throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
         }

         @Override
         public Object getRepeatedRaw(GeneratedMessageV3.Builder var1, int var2) {
            throw new UnsupportedOperationException("getRepeatedFieldRaw() called on a singular field.");
         }

         @Override
         public void setRepeated(GeneratedMessageV3.Builder var1, int var2, Object var3) {
            throw new UnsupportedOperationException("setRepeatedField() called on a singular field.");
         }

         @Override
         public void addRepeated(GeneratedMessageV3.Builder var1, Object var2) {
            throw new UnsupportedOperationException("addRepeatedField() called on a singular field.");
         }

         @Override
         public boolean has(GeneratedMessageV3 var1) {
            if (!this.hasHasMethod) {
               return this.isOneofField
                  ? this.invoker.getOneofFieldNumber(var1) == this.field.getNumber()
                  : !this.get(var1).equals(this.field.getDefaultValue());
            } else {
               return this.invoker.has(var1);
            }
         }

         @Override
         public boolean has(GeneratedMessageV3.Builder var1) {
            if (!this.hasHasMethod) {
               return this.isOneofField
                  ? this.invoker.getOneofFieldNumber(var1) == this.field.getNumber()
                  : !this.get(var1).equals(this.field.getDefaultValue());
            } else {
               return this.invoker.has(var1);
            }
         }

         @Override
         public int getRepeatedCount(GeneratedMessageV3 var1) {
            throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
         }

         @Override
         public int getRepeatedCount(GeneratedMessageV3.Builder var1) {
            throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
         }

         @Override
         public void clear(GeneratedMessageV3.Builder var1) {
            this.invoker.clear(var1);
         }

         @Override
         public Message.Builder newBuilder() {
            throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
         }

         @Override
         public Message.Builder getBuilder(GeneratedMessageV3.Builder var1) {
            throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
         }

         @Override
         public Message.Builder getRepeatedBuilder(GeneratedMessageV3.Builder var1, int var2) {
            throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
         }

         private interface MethodInvoker {
            Object get(GeneratedMessageV3 var1);

            Object get(GeneratedMessageV3.Builder<?> var1);

            int getOneofFieldNumber(GeneratedMessageV3 var1);

            int getOneofFieldNumber(GeneratedMessageV3.Builder<?> var1);

            void set(GeneratedMessageV3.Builder<?> var1, Object var2);

            boolean has(GeneratedMessageV3 var1);

            boolean has(GeneratedMessageV3.Builder<?> var1);

            void clear(GeneratedMessageV3.Builder<?> var1);
         }

         private static final class ReflectionInvoker implements GeneratedMessageV3.FieldAccessorTable.SingularFieldAccessor.MethodInvoker {
            protected final java.lang.reflect.Method getMethod;
            protected final java.lang.reflect.Method getMethodBuilder;
            protected final java.lang.reflect.Method setMethod;
            protected final java.lang.reflect.Method hasMethod;
            protected final java.lang.reflect.Method hasMethodBuilder;
            protected final java.lang.reflect.Method clearMethod;
            protected final java.lang.reflect.Method caseMethod;
            protected final java.lang.reflect.Method caseMethodBuilder;

            ReflectionInvoker(
               Descriptors.FieldDescriptor var1,
               String var2,
               Class<? extends GeneratedMessageV3> var3,
               Class<? extends GeneratedMessageV3.Builder> var4,
               String var5,
               boolean var6,
               boolean var7
            ) {
               this.getMethod = GeneratedMessageV3.getMethodOrDie(var3, "get" + var2);
               this.getMethodBuilder = GeneratedMessageV3.getMethodOrDie(var4, "get" + var2);
               Class var8 = this.getMethod.getReturnType();
               this.setMethod = GeneratedMessageV3.getMethodOrDie(var4, "set" + var2, var8);
               this.hasMethod = var7 ? GeneratedMessageV3.getMethodOrDie(var3, "has" + var2) : null;
               this.hasMethodBuilder = var7 ? GeneratedMessageV3.getMethodOrDie(var4, "has" + var2) : null;
               this.clearMethod = GeneratedMessageV3.getMethodOrDie(var4, "clear" + var2);
               this.caseMethod = var6 ? GeneratedMessageV3.getMethodOrDie(var3, "get" + var5 + "Case") : null;
               this.caseMethodBuilder = var6 ? GeneratedMessageV3.getMethodOrDie(var4, "get" + var5 + "Case") : null;
            }

            @Override
            public Object get(GeneratedMessageV3 var1) {
               return GeneratedMessageV3.invokeOrDie(this.getMethod, var1);
            }

            @Override
            public Object get(GeneratedMessageV3.Builder<?> var1) {
               return GeneratedMessageV3.invokeOrDie(this.getMethodBuilder, var1);
            }

            @Override
            public int getOneofFieldNumber(GeneratedMessageV3 var1) {
               return ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, var1)).getNumber();
            }

            @Override
            public int getOneofFieldNumber(GeneratedMessageV3.Builder<?> var1) {
               return ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, var1)).getNumber();
            }

            @Override
            public void set(GeneratedMessageV3.Builder<?> var1, Object var2) {
               GeneratedMessageV3.invokeOrDie(this.setMethod, var1, var2);
            }

            @Override
            public boolean has(GeneratedMessageV3 var1) {
               return (Boolean)GeneratedMessageV3.invokeOrDie(this.hasMethod, var1);
            }

            @Override
            public boolean has(GeneratedMessageV3.Builder<?> var1) {
               return (Boolean)GeneratedMessageV3.invokeOrDie(this.hasMethodBuilder, var1);
            }

            @Override
            public void clear(GeneratedMessageV3.Builder<?> var1) {
               GeneratedMessageV3.invokeOrDie(this.clearMethod, var1);
            }
         }
      }

      private static final class SingularMessageFieldAccessor extends GeneratedMessageV3.FieldAccessorTable.SingularFieldAccessor {
         private final java.lang.reflect.Method newBuilderMethod = GeneratedMessageV3.getMethodOrDie(this.type, "newBuilder");
         private final java.lang.reflect.Method getBuilderMethodBuilder;

         SingularMessageFieldAccessor(
            Descriptors.FieldDescriptor var1,
            String var2,
            Class<? extends GeneratedMessageV3> var3,
            Class<? extends GeneratedMessageV3.Builder> var4,
            String var5
         ) {
            super(var1, var2, var3, var4, var5);
            this.getBuilderMethodBuilder = GeneratedMessageV3.getMethodOrDie(var4, "get" + var2 + "Builder");
         }

         private Object coerceType(Object var1) {
            return this.type.isInstance(var1)
               ? var1
               : ((Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, null)).mergeFrom((Message)var1).buildPartial();
         }

         @Override
         public void set(GeneratedMessageV3.Builder var1, Object var2) {
            super.set(var1, this.coerceType(var2));
         }

         @Override
         public Message.Builder newBuilder() {
            return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, null);
         }

         @Override
         public Message.Builder getBuilder(GeneratedMessageV3.Builder var1) {
            return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.getBuilderMethodBuilder, var1);
         }
      }

      private static final class SingularStringFieldAccessor extends GeneratedMessageV3.FieldAccessorTable.SingularFieldAccessor {
         private final java.lang.reflect.Method getBytesMethod;
         private final java.lang.reflect.Method getBytesMethodBuilder;
         private final java.lang.reflect.Method setBytesMethodBuilder;

         SingularStringFieldAccessor(
            Descriptors.FieldDescriptor var1,
            String var2,
            Class<? extends GeneratedMessageV3> var3,
            Class<? extends GeneratedMessageV3.Builder> var4,
            String var5
         ) {
            super(var1, var2, var3, var4, var5);
            this.getBytesMethod = GeneratedMessageV3.getMethodOrDie(var3, "get" + var2 + "Bytes");
            this.getBytesMethodBuilder = GeneratedMessageV3.getMethodOrDie(var4, "get" + var2 + "Bytes");
            this.setBytesMethodBuilder = GeneratedMessageV3.getMethodOrDie(var4, "set" + var2 + "Bytes", ByteString.class);
         }

         @Override
         public Object getRaw(GeneratedMessageV3 var1) {
            return GeneratedMessageV3.invokeOrDie(this.getBytesMethod, var1);
         }

         @Override
         public Object getRaw(GeneratedMessageV3.Builder var1) {
            return GeneratedMessageV3.invokeOrDie(this.getBytesMethodBuilder, var1);
         }

         @Override
         public void set(GeneratedMessageV3.Builder var1, Object var2) {
            if (var2 instanceof ByteString) {
               GeneratedMessageV3.invokeOrDie(this.setBytesMethodBuilder, var1, var2);
            } else {
               super.set(var1, var2);
            }
         }
      }
   }

   protected static final class UnusedPrivateParameter {
      static final GeneratedMessageV3.UnusedPrivateParameter INSTANCE = new GeneratedMessageV3.UnusedPrivateParameter();

      private UnusedPrivateParameter() {
      }
   }
}
