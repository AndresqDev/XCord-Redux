package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public abstract class GeneratedMessage extends AbstractMessage implements Serializable {
   private static final long serialVersionUID = 1L;
   protected static boolean alwaysUseFieldBuilders = false;
   protected UnknownFieldSet unknownFields;

   protected GeneratedMessage() {
      this.unknownFields = UnknownFieldSet.getDefaultInstance();
   }

   protected GeneratedMessage(GeneratedMessage.Builder<?> var1) {
      this.unknownFields = var1.getUnknownFields();
   }

   @Override
   public Parser<? extends GeneratedMessage> getParserForType() {
      throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
   }

   static void enableAlwaysUseFieldBuildersForTesting() {
      alwaysUseFieldBuilders = true;
   }

   protected abstract GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable();

   @Override
   public Descriptors.Descriptor getDescriptorForType() {
      return this.internalGetFieldAccessorTable().descriptor;
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
      return var2.mergeFieldFrom(var4, var1);
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

   protected void makeExtensionsImmutable() {
   }

   protected abstract Message.Builder newBuilderForType(GeneratedMessage.BuilderParent var1);

   @Override
   protected Message.Builder newBuilderForType(final AbstractMessage.BuilderParent var1) {
      return this.newBuilderForType(new GeneratedMessage.BuilderParent() {
         @Override
         public void markDirty() {
            var1.markDirty();
         }
      });
   }

   public static <ContainingType extends Message, Type> GeneratedMessage.GeneratedExtension<ContainingType, Type> newMessageScopedGeneratedExtension(
      final Message var0, final int var1, Class var2, Message var3
   ) {
      return new GeneratedMessage.GeneratedExtension<>(new GeneratedMessage.CachedDescriptorRetriever() {
         @Override
         public Descriptors.FieldDescriptor loadDescriptor() {
            return var0.getDescriptorForType().getExtensions().get(var1);
         }
      }, var2, var3, Extension.ExtensionType.IMMUTABLE);
   }

   public static <ContainingType extends Message, Type> GeneratedMessage.GeneratedExtension<ContainingType, Type> newFileScopedGeneratedExtension(
      Class var0, Message var1
   ) {
      return new GeneratedMessage.GeneratedExtension<>(null, var0, var1, Extension.ExtensionType.IMMUTABLE);
   }

   public static <ContainingType extends Message, Type> GeneratedMessage.GeneratedExtension<ContainingType, Type> newMessageScopedGeneratedExtension(
      final Message var0, final String var1, Class var2, Message var3
   ) {
      return new GeneratedMessage.GeneratedExtension<>(new GeneratedMessage.CachedDescriptorRetriever() {
         @Override
         protected Descriptors.FieldDescriptor loadDescriptor() {
            return var0.getDescriptorForType().findFieldByName(var1);
         }
      }, var2, var3, Extension.ExtensionType.MUTABLE);
   }

   public static <ContainingType extends Message, Type> GeneratedMessage.GeneratedExtension<ContainingType, Type> newFileScopedGeneratedExtension(
      final Class var0, Message var1, final String var2, final String var3
   ) {
      return new GeneratedMessage.GeneratedExtension<>(new GeneratedMessage.CachedDescriptorRetriever() {
         @Override
         protected Descriptors.FieldDescriptor loadDescriptor() {
            try {
               Class var1 = var0.getClassLoader().loadClass(var2);
               Descriptors.FileDescriptor var2x = (Descriptors.FileDescriptor)var1.getField("descriptor").get(null);
               return var2x.findExtensionByName(var3);
            } catch (Exception var3x) {
               throw new RuntimeException("Cannot load descriptors: " + var2 + " is not a valid descriptor class name", var3x);
            }
         }
      }, var0, var1, Extension.ExtensionType.MUTABLE);
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

   private static <MessageType extends GeneratedMessage.ExtendableMessage<MessageType>, T> Extension<MessageType, T> checkNotLite(
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

   public abstract static class Builder<BuilderType extends GeneratedMessage.Builder<BuilderType>> extends AbstractMessage.Builder<BuilderType> {
      private GeneratedMessage.BuilderParent builderParent;
      private GeneratedMessage.Builder<BuilderType>.BuilderParentImpl meAsParent;
      private boolean isClean;
      private UnknownFieldSet unknownFields = UnknownFieldSet.getDefaultInstance();

      protected Builder() {
         this(null);
      }

      protected Builder(GeneratedMessage.BuilderParent var1) {
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
         GeneratedMessage.Builder var1 = (GeneratedMessage.Builder)this.getDefaultInstanceForType().newBuilderForType();
         var1.mergeFrom(this.buildPartial());
         return (BuilderType)var1;
      }

      public BuilderType clear() {
         this.unknownFields = UnknownFieldSet.getDefaultInstance();
         this.onChanged();
         return (BuilderType)this;
      }

      protected abstract GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable();

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

      public BuilderType setUnknownFields(UnknownFieldSet var1) {
         this.unknownFields = var1;
         this.onChanged();
         return (BuilderType)this;
      }

      public BuilderType mergeUnknownFields(UnknownFieldSet var1) {
         this.unknownFields = UnknownFieldSet.newBuilder(this.unknownFields).mergeFrom(var1).build();
         this.onChanged();
         return (BuilderType)this;
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

      protected boolean parseUnknownField(CodedInputStream var1, UnknownFieldSet.Builder var2, ExtensionRegistryLite var3, int var4) throws IOException {
         return var2.mergeFieldFrom(var4, var1);
      }

      protected GeneratedMessage.BuilderParent getParentForChildren() {
         if (this.meAsParent == null) {
            this.meAsParent = new GeneratedMessage.Builder.BuilderParentImpl();
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

      private class BuilderParentImpl implements GeneratedMessage.BuilderParent {
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

   private abstract static class CachedDescriptorRetriever implements GeneratedMessage.ExtensionDescriptorRetriever {
      private volatile Descriptors.FieldDescriptor descriptor;

      private CachedDescriptorRetriever() {
      }

      protected abstract Descriptors.FieldDescriptor loadDescriptor();

      @Override
      public Descriptors.FieldDescriptor getDescriptor() {
         if (this.descriptor == null) {
            synchronized (this) {
               if (this.descriptor == null) {
                  this.descriptor = this.loadDescriptor();
               }
            }
         }

         return this.descriptor;
      }
   }

   public abstract static class ExtendableBuilder<MessageType extends GeneratedMessage.ExtendableMessage, BuilderType extends GeneratedMessage.ExtendableBuilder<MessageType, BuilderType>>
      extends GeneratedMessage.Builder<BuilderType>
      implements GeneratedMessage.ExtendableMessageOrBuilder<MessageType> {
      private FieldSet<Descriptors.FieldDescriptor> extensions = FieldSet.emptySet();

      protected ExtendableBuilder() {
      }

      protected ExtendableBuilder(GeneratedMessage.BuilderParent var1) {
         super(var1);
      }

      void internalSetExtensionSet(FieldSet<Descriptors.FieldDescriptor> var1) {
         this.extensions = var1;
      }

      public BuilderType clear() {
         this.extensions = FieldSet.emptySet();
         return super.clear();
      }

      public BuilderType clone() {
         return super.clone();
      }

      private void ensureExtensionsIsMutable() {
         if (this.extensions.isImmutable()) {
            this.extensions = this.extensions.clone();
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
         Extension var2 = GeneratedMessage.checkNotLite(var1);
         this.verifyExtensionContainingType(var2);
         return this.extensions.hasField(var2.getDescriptor());
      }

      @Override
      public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> var1) {
         Extension var2 = GeneratedMessage.checkNotLite(var1);
         this.verifyExtensionContainingType(var2);
         Descriptors.FieldDescriptor var3 = var2.getDescriptor();
         return this.extensions.getRepeatedFieldCount(var3);
      }

      @Override
      public final <Type> Type getExtension(ExtensionLite<MessageType, Type> var1) {
         Extension var2 = GeneratedMessage.checkNotLite(var1);
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
         Extension var3 = GeneratedMessage.checkNotLite(var1);
         this.verifyExtensionContainingType(var3);
         Descriptors.FieldDescriptor var4 = var3.getDescriptor();
         return (Type)var3.singularFromReflectionType(this.extensions.getRepeatedField(var4, var2));
      }

      public final <Type> BuilderType setExtension(ExtensionLite<MessageType, Type> var1, Type var2) {
         Extension var3 = GeneratedMessage.checkNotLite(var1);
         this.verifyExtensionContainingType(var3);
         this.ensureExtensionsIsMutable();
         Descriptors.FieldDescriptor var4 = var3.getDescriptor();
         this.extensions.setField(var4, var3.toReflectionType(var2));
         this.onChanged();
         return (BuilderType)this;
      }

      public final <Type> BuilderType setExtension(ExtensionLite<MessageType, List<Type>> var1, int var2, Type var3) {
         Extension var4 = GeneratedMessage.checkNotLite(var1);
         this.verifyExtensionContainingType(var4);
         this.ensureExtensionsIsMutable();
         Descriptors.FieldDescriptor var5 = var4.getDescriptor();
         this.extensions.setRepeatedField(var5, var2, var4.singularToReflectionType(var3));
         this.onChanged();
         return (BuilderType)this;
      }

      public final <Type> BuilderType addExtension(ExtensionLite<MessageType, List<Type>> var1, Type var2) {
         Extension var3 = GeneratedMessage.checkNotLite(var1);
         this.verifyExtensionContainingType(var3);
         this.ensureExtensionsIsMutable();
         Descriptors.FieldDescriptor var4 = var3.getDescriptor();
         this.extensions.addRepeatedField(var4, var3.singularToReflectionType(var2));
         this.onChanged();
         return (BuilderType)this;
      }

      public final <Type> BuilderType clearExtension(ExtensionLite<MessageType, ?> var1) {
         Extension var2 = GeneratedMessage.checkNotLite(var1);
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
         return this.extensions.isInitialized();
      }

      private FieldSet<Descriptors.FieldDescriptor> buildExtensions() {
         this.extensions.makeImmutable();
         return this.extensions;
      }

      @Override
      public boolean isInitialized() {
         return super.isInitialized() && this.extensionsAreInitialized();
      }

      @Override
      protected boolean parseUnknownField(CodedInputStream var1, UnknownFieldSet.Builder var2, ExtensionRegistryLite var3, int var4) throws IOException {
         return MessageReflection.mergeFieldFrom(var1, var2, var3, this.getDescriptorForType(), new MessageReflection.BuilderAdapter(this), var4);
      }

      @Override
      public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
         Map var1 = super.getAllFieldsMutable();
         var1.putAll(this.extensions.getAllFields());
         return Collections.unmodifiableMap(var1);
      }

      @Override
      public Object getField(Descriptors.FieldDescriptor var1) {
         if (var1.isExtension()) {
            this.verifyContainingType(var1);
            Object var2 = this.extensions.getField(var1);
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

      @Override
      public boolean hasField(Descriptors.FieldDescriptor var1) {
         if (var1.isExtension()) {
            this.verifyContainingType(var1);
            return this.extensions.hasField(var1);
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

      protected final void mergeExtensionFields(GeneratedMessage.ExtendableMessage var1) {
         this.ensureExtensionsIsMutable();
         this.extensions.mergeFrom(var1.extensions);
         this.onChanged();
      }

      private void verifyContainingType(Descriptors.FieldDescriptor var1) {
         if (var1.getContainingType() != this.getDescriptorForType()) {
            throw new IllegalArgumentException("FieldDescriptor does not match message type.");
         }
      }
   }

   public abstract static class ExtendableMessage<MessageType extends GeneratedMessage.ExtendableMessage>
      extends GeneratedMessage
      implements GeneratedMessage.ExtendableMessageOrBuilder<MessageType> {
      private static final long serialVersionUID = 1L;
      private final FieldSet<Descriptors.FieldDescriptor> extensions;

      protected ExtendableMessage() {
         this.extensions = FieldSet.newFieldSet();
      }

      protected ExtendableMessage(GeneratedMessage.ExtendableBuilder<MessageType, ?> var1) {
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
         Extension var2 = GeneratedMessage.checkNotLite(var1);
         this.verifyExtensionContainingType(var2);
         return this.extensions.hasField(var2.getDescriptor());
      }

      @Override
      public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> var1) {
         Extension var2 = GeneratedMessage.checkNotLite(var1);
         this.verifyExtensionContainingType(var2);
         Descriptors.FieldDescriptor var3 = var2.getDescriptor();
         return this.extensions.getRepeatedFieldCount(var3);
      }

      @Override
      public final <Type> Type getExtension(ExtensionLite<MessageType, Type> var1) {
         Extension var2 = GeneratedMessage.checkNotLite(var1);
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
         Extension var3 = GeneratedMessage.checkNotLite(var1);
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
         return MessageReflection.mergeFieldFrom(var1, var2, var3, this.getDescriptorForType(), new MessageReflection.ExtensionAdapter(this.extensions), var4);
      }

      @Override
      protected void makeExtensionsImmutable() {
         this.extensions.makeImmutable();
      }

      protected GeneratedMessage.ExtendableMessage<MessageType>.ExtensionWriter newExtensionWriter() {
         return new GeneratedMessage.ExtendableMessage.ExtensionWriter(false);
      }

      protected GeneratedMessage.ExtendableMessage<MessageType>.ExtensionWriter newMessageSetExtensionWriter() {
         return new GeneratedMessage.ExtendableMessage.ExtensionWriter(true);
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

   public interface ExtendableMessageOrBuilder<MessageType extends GeneratedMessage.ExtendableMessage> extends MessageOrBuilder {
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
      private final GeneratedMessage.FieldAccessorTable.FieldAccessor[] fields;
      private String[] camelCaseNames;
      private final GeneratedMessage.FieldAccessorTable.OneofAccessor[] oneofs;
      private volatile boolean initialized;

      public FieldAccessorTable(
         Descriptors.Descriptor var1, String[] var2, Class<? extends GeneratedMessage> var3, Class<? extends GeneratedMessage.Builder> var4
      ) {
         this(var1, var2);
         this.ensureFieldAccessorsInitialized(var3, var4);
      }

      public FieldAccessorTable(Descriptors.Descriptor var1, String[] var2) {
         this.descriptor = var1;
         this.camelCaseNames = var2;
         this.fields = new GeneratedMessage.FieldAccessorTable.FieldAccessor[var1.getFields().size()];
         this.oneofs = new GeneratedMessage.FieldAccessorTable.OneofAccessor[var1.getOneofs().size()];
         this.initialized = false;
      }

      private boolean isMapFieldEnabled(Descriptors.FieldDescriptor var1) {
         return true;
      }

      public GeneratedMessage.FieldAccessorTable ensureFieldAccessorsInitialized(
         Class<? extends GeneratedMessage> var1, Class<? extends GeneratedMessage.Builder> var2
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
                           if (var6.isMapField() && this.isMapFieldEnabled(var6)) {
                              this.fields[var5] = new GeneratedMessage.FieldAccessorTable.MapFieldAccessor(var6, this.camelCaseNames[var5], var1, var2);
                           } else {
                              this.fields[var5] = new GeneratedMessage.FieldAccessorTable.RepeatedMessageFieldAccessor(
                                 var6, this.camelCaseNames[var5], var1, var2
                              );
                           }
                        } else if (var6.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
                           this.fields[var5] = new GeneratedMessage.FieldAccessorTable.RepeatedEnumFieldAccessor(var6, this.camelCaseNames[var5], var1, var2);
                        } else {
                           this.fields[var5] = new GeneratedMessage.FieldAccessorTable.RepeatedFieldAccessor(var6, this.camelCaseNames[var5], var1, var2);
                        }
                     } else if (var6.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                        this.fields[var5] = new GeneratedMessage.FieldAccessorTable.SingularMessageFieldAccessor(
                           var6, this.camelCaseNames[var5], var1, var2, var7
                        );
                     } else if (var6.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
                        this.fields[var5] = new GeneratedMessage.FieldAccessorTable.SingularEnumFieldAccessor(var6, this.camelCaseNames[var5], var1, var2, var7);
                     } else if (var6.getJavaType() == Descriptors.FieldDescriptor.JavaType.STRING) {
                        this.fields[var5] = new GeneratedMessage.FieldAccessorTable.SingularStringFieldAccessor(
                           var6, this.camelCaseNames[var5], var1, var2, var7
                        );
                     } else {
                        this.fields[var5] = new GeneratedMessage.FieldAccessorTable.SingularFieldAccessor(var6, this.camelCaseNames[var5], var1, var2, var7);
                     }
                  }

                  int var10 = this.oneofs.length;

                  for (int var11 = 0; var11 < var10; var11++) {
                     this.oneofs[var11] = new GeneratedMessage.FieldAccessorTable.OneofAccessor(this.descriptor, this.camelCaseNames[var11 + var4], var1, var2);
                  }

                  this.initialized = true;
                  this.camelCaseNames = null;
                  return this;
               }
            }
         }
      }

      private GeneratedMessage.FieldAccessorTable.FieldAccessor getField(Descriptors.FieldDescriptor var1) {
         if (var1.getContainingType() != this.descriptor) {
            throw new IllegalArgumentException("FieldDescriptor does not match message type.");
         } else if (var1.isExtension()) {
            throw new IllegalArgumentException("This type does not have extensions.");
         } else {
            return this.fields[var1.getIndex()];
         }
      }

      private GeneratedMessage.FieldAccessorTable.OneofAccessor getOneof(Descriptors.OneofDescriptor var1) {
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
         Object get(GeneratedMessage var1);

         Object get(GeneratedMessage.Builder var1);

         Object getRaw(GeneratedMessage var1);

         Object getRaw(GeneratedMessage.Builder var1);

         void set(GeneratedMessage.Builder var1, Object var2);

         Object getRepeated(GeneratedMessage var1, int var2);

         Object getRepeated(GeneratedMessage.Builder var1, int var2);

         Object getRepeatedRaw(GeneratedMessage var1, int var2);

         Object getRepeatedRaw(GeneratedMessage.Builder var1, int var2);

         void setRepeated(GeneratedMessage.Builder var1, int var2, Object var3);

         void addRepeated(GeneratedMessage.Builder var1, Object var2);

         boolean has(GeneratedMessage var1);

         boolean has(GeneratedMessage.Builder var1);

         int getRepeatedCount(GeneratedMessage var1);

         int getRepeatedCount(GeneratedMessage.Builder var1);

         void clear(GeneratedMessage.Builder var1);

         Message.Builder newBuilder();

         Message.Builder getBuilder(GeneratedMessage.Builder var1);

         Message.Builder getRepeatedBuilder(GeneratedMessage.Builder var1, int var2);
      }

      private static class MapFieldAccessor implements GeneratedMessage.FieldAccessorTable.FieldAccessor {
         private final Descriptors.FieldDescriptor field;
         private final Message mapEntryMessageDefaultInstance;

         MapFieldAccessor(Descriptors.FieldDescriptor var1, String var2, Class<? extends GeneratedMessage> var3, Class<? extends GeneratedMessage.Builder> var4) {
            this.field = var1;
            java.lang.reflect.Method var5 = GeneratedMessage.getMethodOrDie(var3, "getDefaultInstance");
            MapField var6 = this.getMapField((GeneratedMessage)GeneratedMessage.invokeOrDie(var5, null));
            this.mapEntryMessageDefaultInstance = var6.getMapEntryMessageDefaultInstance();
         }

         private MapField<?, ?> getMapField(GeneratedMessage var1) {
            return var1.internalGetMapField(this.field.getNumber());
         }

         private MapField<?, ?> getMapField(GeneratedMessage.Builder var1) {
            return var1.internalGetMapField(this.field.getNumber());
         }

         private MapField<?, ?> getMutableMapField(GeneratedMessage.Builder var1) {
            return var1.internalGetMutableMapField(this.field.getNumber());
         }

         @Override
         public Object get(GeneratedMessage var1) {
            ArrayList var2 = new ArrayList();

            for (int var3 = 0; var3 < this.getRepeatedCount(var1); var3++) {
               var2.add(this.getRepeated(var1, var3));
            }

            return Collections.unmodifiableList(var2);
         }

         @Override
         public Object get(GeneratedMessage.Builder var1) {
            ArrayList var2 = new ArrayList();

            for (int var3 = 0; var3 < this.getRepeatedCount(var1); var3++) {
               var2.add(this.getRepeated(var1, var3));
            }

            return Collections.unmodifiableList(var2);
         }

         @Override
         public Object getRaw(GeneratedMessage var1) {
            return this.get(var1);
         }

         @Override
         public Object getRaw(GeneratedMessage.Builder var1) {
            return this.get(var1);
         }

         @Override
         public void set(GeneratedMessage.Builder var1, Object var2) {
            this.clear(var1);

            for (Object var4 : (List)var2) {
               this.addRepeated(var1, var4);
            }
         }

         @Override
         public Object getRepeated(GeneratedMessage var1, int var2) {
            return this.getMapField(var1).getList().get(var2);
         }

         @Override
         public Object getRepeated(GeneratedMessage.Builder var1, int var2) {
            return this.getMapField(var1).getList().get(var2);
         }

         @Override
         public Object getRepeatedRaw(GeneratedMessage var1, int var2) {
            return this.getRepeated(var1, var2);
         }

         @Override
         public Object getRepeatedRaw(GeneratedMessage.Builder var1, int var2) {
            return this.getRepeated(var1, var2);
         }

         @Override
         public void setRepeated(GeneratedMessage.Builder var1, int var2, Object var3) {
            this.getMutableMapField(var1).getMutableList().set(var2, (Message)var3);
         }

         @Override
         public void addRepeated(GeneratedMessage.Builder var1, Object var2) {
            this.getMutableMapField(var1).getMutableList().add((Message)var2);
         }

         @Override
         public boolean has(GeneratedMessage var1) {
            throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
         }

         @Override
         public boolean has(GeneratedMessage.Builder var1) {
            throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
         }

         @Override
         public int getRepeatedCount(GeneratedMessage var1) {
            return this.getMapField(var1).getList().size();
         }

         @Override
         public int getRepeatedCount(GeneratedMessage.Builder var1) {
            return this.getMapField(var1).getList().size();
         }

         @Override
         public void clear(GeneratedMessage.Builder var1) {
            this.getMutableMapField(var1).getMutableList().clear();
         }

         @Override
         public Message.Builder newBuilder() {
            return this.mapEntryMessageDefaultInstance.newBuilderForType();
         }

         @Override
         public Message.Builder getBuilder(GeneratedMessage.Builder var1) {
            throw new UnsupportedOperationException("Nested builder not supported for map fields.");
         }

         @Override
         public Message.Builder getRepeatedBuilder(GeneratedMessage.Builder var1, int var2) {
            throw new UnsupportedOperationException("Nested builder not supported for map fields.");
         }
      }

      private static class OneofAccessor {
         private final Descriptors.Descriptor descriptor;
         private final java.lang.reflect.Method caseMethod;
         private final java.lang.reflect.Method caseMethodBuilder;
         private final java.lang.reflect.Method clearMethod;

         OneofAccessor(Descriptors.Descriptor var1, String var2, Class<? extends GeneratedMessage> var3, Class<? extends GeneratedMessage.Builder> var4) {
            this.descriptor = var1;
            this.caseMethod = GeneratedMessage.getMethodOrDie(var3, "get" + var2 + "Case");
            this.caseMethodBuilder = GeneratedMessage.getMethodOrDie(var4, "get" + var2 + "Case");
            this.clearMethod = GeneratedMessage.getMethodOrDie(var4, "clear" + var2);
         }

         public boolean has(GeneratedMessage var1) {
            return ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethod, var1)).getNumber() != 0;
         }

         public boolean has(GeneratedMessage.Builder var1) {
            return ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethodBuilder, var1)).getNumber() != 0;
         }

         public Descriptors.FieldDescriptor get(GeneratedMessage var1) {
            int var2 = ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethod, var1)).getNumber();
            return var2 > 0 ? this.descriptor.findFieldByNumber(var2) : null;
         }

         public Descriptors.FieldDescriptor get(GeneratedMessage.Builder var1) {
            int var2 = ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethodBuilder, var1)).getNumber();
            return var2 > 0 ? this.descriptor.findFieldByNumber(var2) : null;
         }

         public void clear(GeneratedMessage.Builder var1) {
            GeneratedMessage.invokeOrDie(this.clearMethod, var1);
         }
      }

      private static final class RepeatedEnumFieldAccessor extends GeneratedMessage.FieldAccessorTable.RepeatedFieldAccessor {
         private Descriptors.EnumDescriptor enumDescriptor;
         private final java.lang.reflect.Method valueOfMethod;
         private final java.lang.reflect.Method getValueDescriptorMethod;
         private boolean supportUnknownEnumValue;
         private java.lang.reflect.Method getRepeatedValueMethod;
         private java.lang.reflect.Method getRepeatedValueMethodBuilder;
         private java.lang.reflect.Method setRepeatedValueMethod;
         private java.lang.reflect.Method addRepeatedValueMethod;

         RepeatedEnumFieldAccessor(
            Descriptors.FieldDescriptor var1, String var2, Class<? extends GeneratedMessage> var3, Class<? extends GeneratedMessage.Builder> var4
         ) {
            super(var1, var2, var3, var4);
            this.enumDescriptor = var1.getEnumType();
            this.valueOfMethod = GeneratedMessage.getMethodOrDie(this.type, "valueOf", Descriptors.EnumValueDescriptor.class);
            this.getValueDescriptorMethod = GeneratedMessage.getMethodOrDie(this.type, "getValueDescriptor");
            this.supportUnknownEnumValue = var1.getFile().supportsUnknownEnumValue();
            if (this.supportUnknownEnumValue) {
               this.getRepeatedValueMethod = GeneratedMessage.getMethodOrDie(var3, "get" + var2 + "Value", int.class);
               this.getRepeatedValueMethodBuilder = GeneratedMessage.getMethodOrDie(var4, "get" + var2 + "Value", int.class);
               this.setRepeatedValueMethod = GeneratedMessage.getMethodOrDie(var4, "set" + var2 + "Value", int.class, int.class);
               this.addRepeatedValueMethod = GeneratedMessage.getMethodOrDie(var4, "add" + var2 + "Value", int.class);
            }
         }

         @Override
         public Object get(GeneratedMessage var1) {
            ArrayList var2 = new ArrayList();
            int var3 = this.getRepeatedCount(var1);

            for (int var4 = 0; var4 < var3; var4++) {
               var2.add(this.getRepeated(var1, var4));
            }

            return Collections.unmodifiableList(var2);
         }

         @Override
         public Object get(GeneratedMessage.Builder var1) {
            ArrayList var2 = new ArrayList();
            int var3 = this.getRepeatedCount(var1);

            for (int var4 = 0; var4 < var3; var4++) {
               var2.add(this.getRepeated(var1, var4));
            }

            return Collections.unmodifiableList(var2);
         }

         @Override
         public Object getRepeated(GeneratedMessage var1, int var2) {
            if (this.supportUnknownEnumValue) {
               int var3 = (Integer)GeneratedMessage.invokeOrDie(this.getRepeatedValueMethod, var1, var2);
               return this.enumDescriptor.findValueByNumberCreatingIfUnknown(var3);
            } else {
               return GeneratedMessage.invokeOrDie(this.getValueDescriptorMethod, super.getRepeated(var1, var2));
            }
         }

         @Override
         public Object getRepeated(GeneratedMessage.Builder var1, int var2) {
            if (this.supportUnknownEnumValue) {
               int var3 = (Integer)GeneratedMessage.invokeOrDie(this.getRepeatedValueMethodBuilder, var1, var2);
               return this.enumDescriptor.findValueByNumberCreatingIfUnknown(var3);
            } else {
               return GeneratedMessage.invokeOrDie(this.getValueDescriptorMethod, super.getRepeated(var1, var2));
            }
         }

         @Override
         public void setRepeated(GeneratedMessage.Builder var1, int var2, Object var3) {
            if (this.supportUnknownEnumValue) {
               GeneratedMessage.invokeOrDie(this.setRepeatedValueMethod, var1, var2, ((Descriptors.EnumValueDescriptor)var3).getNumber());
            } else {
               super.setRepeated(var1, var2, GeneratedMessage.invokeOrDie(this.valueOfMethod, null, var3));
            }
         }

         @Override
         public void addRepeated(GeneratedMessage.Builder var1, Object var2) {
            if (this.supportUnknownEnumValue) {
               GeneratedMessage.invokeOrDie(this.addRepeatedValueMethod, var1, ((Descriptors.EnumValueDescriptor)var2).getNumber());
            } else {
               super.addRepeated(var1, GeneratedMessage.invokeOrDie(this.valueOfMethod, null, var2));
            }
         }
      }

      private static class RepeatedFieldAccessor implements GeneratedMessage.FieldAccessorTable.FieldAccessor {
         protected final Class type;
         protected final java.lang.reflect.Method getMethod;
         protected final java.lang.reflect.Method getMethodBuilder;
         protected final java.lang.reflect.Method getRepeatedMethod;
         protected final java.lang.reflect.Method getRepeatedMethodBuilder;
         protected final java.lang.reflect.Method setRepeatedMethod;
         protected final java.lang.reflect.Method addRepeatedMethod;
         protected final java.lang.reflect.Method getCountMethod;
         protected final java.lang.reflect.Method getCountMethodBuilder;
         protected final java.lang.reflect.Method clearMethod;

         RepeatedFieldAccessor(
            Descriptors.FieldDescriptor var1, String var2, Class<? extends GeneratedMessage> var3, Class<? extends GeneratedMessage.Builder> var4
         ) {
            this.getMethod = GeneratedMessage.getMethodOrDie(var3, "get" + var2 + "List");
            this.getMethodBuilder = GeneratedMessage.getMethodOrDie(var4, "get" + var2 + "List");
            this.getRepeatedMethod = GeneratedMessage.getMethodOrDie(var3, "get" + var2, int.class);
            this.getRepeatedMethodBuilder = GeneratedMessage.getMethodOrDie(var4, "get" + var2, int.class);
            this.type = this.getRepeatedMethod.getReturnType();
            this.setRepeatedMethod = GeneratedMessage.getMethodOrDie(var4, "set" + var2, int.class, this.type);
            this.addRepeatedMethod = GeneratedMessage.getMethodOrDie(var4, "add" + var2, this.type);
            this.getCountMethod = GeneratedMessage.getMethodOrDie(var3, "get" + var2 + "Count");
            this.getCountMethodBuilder = GeneratedMessage.getMethodOrDie(var4, "get" + var2 + "Count");
            this.clearMethod = GeneratedMessage.getMethodOrDie(var4, "clear" + var2);
         }

         @Override
         public Object get(GeneratedMessage var1) {
            return GeneratedMessage.invokeOrDie(this.getMethod, var1);
         }

         @Override
         public Object get(GeneratedMessage.Builder var1) {
            return GeneratedMessage.invokeOrDie(this.getMethodBuilder, var1);
         }

         @Override
         public Object getRaw(GeneratedMessage var1) {
            return this.get(var1);
         }

         @Override
         public Object getRaw(GeneratedMessage.Builder var1) {
            return this.get(var1);
         }

         @Override
         public void set(GeneratedMessage.Builder var1, Object var2) {
            this.clear(var1);

            for (Object var4 : (List)var2) {
               this.addRepeated(var1, var4);
            }
         }

         @Override
         public Object getRepeated(GeneratedMessage var1, int var2) {
            return GeneratedMessage.invokeOrDie(this.getRepeatedMethod, var1, var2);
         }

         @Override
         public Object getRepeated(GeneratedMessage.Builder var1, int var2) {
            return GeneratedMessage.invokeOrDie(this.getRepeatedMethodBuilder, var1, var2);
         }

         @Override
         public Object getRepeatedRaw(GeneratedMessage var1, int var2) {
            return this.getRepeated(var1, var2);
         }

         @Override
         public Object getRepeatedRaw(GeneratedMessage.Builder var1, int var2) {
            return this.getRepeated(var1, var2);
         }

         @Override
         public void setRepeated(GeneratedMessage.Builder var1, int var2, Object var3) {
            GeneratedMessage.invokeOrDie(this.setRepeatedMethod, var1, var2, var3);
         }

         @Override
         public void addRepeated(GeneratedMessage.Builder var1, Object var2) {
            GeneratedMessage.invokeOrDie(this.addRepeatedMethod, var1, var2);
         }

         @Override
         public boolean has(GeneratedMessage var1) {
            throw new UnsupportedOperationException("hasField() called on a repeated field.");
         }

         @Override
         public boolean has(GeneratedMessage.Builder var1) {
            throw new UnsupportedOperationException("hasField() called on a repeated field.");
         }

         @Override
         public int getRepeatedCount(GeneratedMessage var1) {
            return (Integer)GeneratedMessage.invokeOrDie(this.getCountMethod, var1);
         }

         @Override
         public int getRepeatedCount(GeneratedMessage.Builder var1) {
            return (Integer)GeneratedMessage.invokeOrDie(this.getCountMethodBuilder, var1);
         }

         @Override
         public void clear(GeneratedMessage.Builder var1) {
            GeneratedMessage.invokeOrDie(this.clearMethod, var1);
         }

         @Override
         public Message.Builder newBuilder() {
            throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
         }

         @Override
         public Message.Builder getBuilder(GeneratedMessage.Builder var1) {
            throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
         }

         @Override
         public Message.Builder getRepeatedBuilder(GeneratedMessage.Builder var1, int var2) {
            throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
         }
      }

      private static final class RepeatedMessageFieldAccessor extends GeneratedMessage.FieldAccessorTable.RepeatedFieldAccessor {
         private final java.lang.reflect.Method newBuilderMethod = GeneratedMessage.getMethodOrDie(this.type, "newBuilder");
         private final java.lang.reflect.Method getBuilderMethodBuilder;

         RepeatedMessageFieldAccessor(
            Descriptors.FieldDescriptor var1, String var2, Class<? extends GeneratedMessage> var3, Class<? extends GeneratedMessage.Builder> var4
         ) {
            super(var1, var2, var3, var4);
            this.getBuilderMethodBuilder = GeneratedMessage.getMethodOrDie(var4, "get" + var2 + "Builder", int.class);
         }

         private Object coerceType(Object var1) {
            return this.type.isInstance(var1)
               ? var1
               : ((Message.Builder)GeneratedMessage.invokeOrDie(this.newBuilderMethod, null)).mergeFrom((Message)var1).build();
         }

         @Override
         public void setRepeated(GeneratedMessage.Builder var1, int var2, Object var3) {
            super.setRepeated(var1, var2, this.coerceType(var3));
         }

         @Override
         public void addRepeated(GeneratedMessage.Builder var1, Object var2) {
            super.addRepeated(var1, this.coerceType(var2));
         }

         @Override
         public Message.Builder newBuilder() {
            return (Message.Builder)GeneratedMessage.invokeOrDie(this.newBuilderMethod, null);
         }

         @Override
         public Message.Builder getRepeatedBuilder(GeneratedMessage.Builder var1, int var2) {
            return (Message.Builder)GeneratedMessage.invokeOrDie(this.getBuilderMethodBuilder, var1, var2);
         }
      }

      private static final class SingularEnumFieldAccessor extends GeneratedMessage.FieldAccessorTable.SingularFieldAccessor {
         private Descriptors.EnumDescriptor enumDescriptor;
         private java.lang.reflect.Method valueOfMethod;
         private java.lang.reflect.Method getValueDescriptorMethod;
         private boolean supportUnknownEnumValue;
         private java.lang.reflect.Method getValueMethod;
         private java.lang.reflect.Method getValueMethodBuilder;
         private java.lang.reflect.Method setValueMethod;

         SingularEnumFieldAccessor(
            Descriptors.FieldDescriptor var1, String var2, Class<? extends GeneratedMessage> var3, Class<? extends GeneratedMessage.Builder> var4, String var5
         ) {
            super(var1, var2, var3, var4, var5);
            this.enumDescriptor = var1.getEnumType();
            this.valueOfMethod = GeneratedMessage.getMethodOrDie(this.type, "valueOf", Descriptors.EnumValueDescriptor.class);
            this.getValueDescriptorMethod = GeneratedMessage.getMethodOrDie(this.type, "getValueDescriptor");
            this.supportUnknownEnumValue = var1.getFile().supportsUnknownEnumValue();
            if (this.supportUnknownEnumValue) {
               this.getValueMethod = GeneratedMessage.getMethodOrDie(var3, "get" + var2 + "Value");
               this.getValueMethodBuilder = GeneratedMessage.getMethodOrDie(var4, "get" + var2 + "Value");
               this.setValueMethod = GeneratedMessage.getMethodOrDie(var4, "set" + var2 + "Value", int.class);
            }
         }

         @Override
         public Object get(GeneratedMessage var1) {
            if (this.supportUnknownEnumValue) {
               int var2 = (Integer)GeneratedMessage.invokeOrDie(this.getValueMethod, var1);
               return this.enumDescriptor.findValueByNumberCreatingIfUnknown(var2);
            } else {
               return GeneratedMessage.invokeOrDie(this.getValueDescriptorMethod, super.get(var1));
            }
         }

         @Override
         public Object get(GeneratedMessage.Builder var1) {
            if (this.supportUnknownEnumValue) {
               int var2 = (Integer)GeneratedMessage.invokeOrDie(this.getValueMethodBuilder, var1);
               return this.enumDescriptor.findValueByNumberCreatingIfUnknown(var2);
            } else {
               return GeneratedMessage.invokeOrDie(this.getValueDescriptorMethod, super.get(var1));
            }
         }

         @Override
         public void set(GeneratedMessage.Builder var1, Object var2) {
            if (this.supportUnknownEnumValue) {
               GeneratedMessage.invokeOrDie(this.setValueMethod, var1, ((Descriptors.EnumValueDescriptor)var2).getNumber());
            } else {
               super.set(var1, GeneratedMessage.invokeOrDie(this.valueOfMethod, null, var2));
            }
         }
      }

      private static class SingularFieldAccessor implements GeneratedMessage.FieldAccessorTable.FieldAccessor {
         protected final Class<?> type;
         protected final java.lang.reflect.Method getMethod;
         protected final java.lang.reflect.Method getMethodBuilder;
         protected final java.lang.reflect.Method setMethod;
         protected final java.lang.reflect.Method hasMethod;
         protected final java.lang.reflect.Method hasMethodBuilder;
         protected final java.lang.reflect.Method clearMethod;
         protected final java.lang.reflect.Method caseMethod;
         protected final java.lang.reflect.Method caseMethodBuilder;
         protected final Descriptors.FieldDescriptor field;
         protected final boolean isOneofField;
         protected final boolean hasHasMethod;

         SingularFieldAccessor(
            Descriptors.FieldDescriptor var1, String var2, Class<? extends GeneratedMessage> var3, Class<? extends GeneratedMessage.Builder> var4, String var5
         ) {
            this.field = var1;
            this.isOneofField = var1.getContainingOneof() != null;
            this.hasHasMethod = GeneratedMessage.FieldAccessorTable.supportFieldPresence(var1.getFile())
               || !this.isOneofField && var1.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE;
            this.getMethod = GeneratedMessage.getMethodOrDie(var3, "get" + var2);
            this.getMethodBuilder = GeneratedMessage.getMethodOrDie(var4, "get" + var2);
            this.type = this.getMethod.getReturnType();
            this.setMethod = GeneratedMessage.getMethodOrDie(var4, "set" + var2, this.type);
            this.hasMethod = this.hasHasMethod ? GeneratedMessage.getMethodOrDie(var3, "has" + var2) : null;
            this.hasMethodBuilder = this.hasHasMethod ? GeneratedMessage.getMethodOrDie(var4, "has" + var2) : null;
            this.clearMethod = GeneratedMessage.getMethodOrDie(var4, "clear" + var2);
            this.caseMethod = this.isOneofField ? GeneratedMessage.getMethodOrDie(var3, "get" + var5 + "Case") : null;
            this.caseMethodBuilder = this.isOneofField ? GeneratedMessage.getMethodOrDie(var4, "get" + var5 + "Case") : null;
         }

         private int getOneofFieldNumber(GeneratedMessage var1) {
            return ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethod, var1)).getNumber();
         }

         private int getOneofFieldNumber(GeneratedMessage.Builder var1) {
            return ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethodBuilder, var1)).getNumber();
         }

         @Override
         public Object get(GeneratedMessage var1) {
            return GeneratedMessage.invokeOrDie(this.getMethod, var1);
         }

         @Override
         public Object get(GeneratedMessage.Builder var1) {
            return GeneratedMessage.invokeOrDie(this.getMethodBuilder, var1);
         }

         @Override
         public Object getRaw(GeneratedMessage var1) {
            return this.get(var1);
         }

         @Override
         public Object getRaw(GeneratedMessage.Builder var1) {
            return this.get(var1);
         }

         @Override
         public void set(GeneratedMessage.Builder var1, Object var2) {
            GeneratedMessage.invokeOrDie(this.setMethod, var1, var2);
         }

         @Override
         public Object getRepeated(GeneratedMessage var1, int var2) {
            throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
         }

         @Override
         public Object getRepeatedRaw(GeneratedMessage var1, int var2) {
            throw new UnsupportedOperationException("getRepeatedFieldRaw() called on a singular field.");
         }

         @Override
         public Object getRepeated(GeneratedMessage.Builder var1, int var2) {
            throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
         }

         @Override
         public Object getRepeatedRaw(GeneratedMessage.Builder var1, int var2) {
            throw new UnsupportedOperationException("getRepeatedFieldRaw() called on a singular field.");
         }

         @Override
         public void setRepeated(GeneratedMessage.Builder var1, int var2, Object var3) {
            throw new UnsupportedOperationException("setRepeatedField() called on a singular field.");
         }

         @Override
         public void addRepeated(GeneratedMessage.Builder var1, Object var2) {
            throw new UnsupportedOperationException("addRepeatedField() called on a singular field.");
         }

         @Override
         public boolean has(GeneratedMessage var1) {
            if (!this.hasHasMethod) {
               return this.isOneofField ? this.getOneofFieldNumber(var1) == this.field.getNumber() : !this.get(var1).equals(this.field.getDefaultValue());
            } else {
               return (Boolean)GeneratedMessage.invokeOrDie(this.hasMethod, var1);
            }
         }

         @Override
         public boolean has(GeneratedMessage.Builder var1) {
            if (!this.hasHasMethod) {
               return this.isOneofField ? this.getOneofFieldNumber(var1) == this.field.getNumber() : !this.get(var1).equals(this.field.getDefaultValue());
            } else {
               return (Boolean)GeneratedMessage.invokeOrDie(this.hasMethodBuilder, var1);
            }
         }

         @Override
         public int getRepeatedCount(GeneratedMessage var1) {
            throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
         }

         @Override
         public int getRepeatedCount(GeneratedMessage.Builder var1) {
            throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
         }

         @Override
         public void clear(GeneratedMessage.Builder var1) {
            GeneratedMessage.invokeOrDie(this.clearMethod, var1);
         }

         @Override
         public Message.Builder newBuilder() {
            throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
         }

         @Override
         public Message.Builder getBuilder(GeneratedMessage.Builder var1) {
            throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
         }

         @Override
         public Message.Builder getRepeatedBuilder(GeneratedMessage.Builder var1, int var2) {
            throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
         }
      }

      private static final class SingularMessageFieldAccessor extends GeneratedMessage.FieldAccessorTable.SingularFieldAccessor {
         private final java.lang.reflect.Method newBuilderMethod = GeneratedMessage.getMethodOrDie(this.type, "newBuilder");
         private final java.lang.reflect.Method getBuilderMethodBuilder;

         SingularMessageFieldAccessor(
            Descriptors.FieldDescriptor var1, String var2, Class<? extends GeneratedMessage> var3, Class<? extends GeneratedMessage.Builder> var4, String var5
         ) {
            super(var1, var2, var3, var4, var5);
            this.getBuilderMethodBuilder = GeneratedMessage.getMethodOrDie(var4, "get" + var2 + "Builder");
         }

         private Object coerceType(Object var1) {
            return this.type.isInstance(var1)
               ? var1
               : ((Message.Builder)GeneratedMessage.invokeOrDie(this.newBuilderMethod, null)).mergeFrom((Message)var1).buildPartial();
         }

         @Override
         public void set(GeneratedMessage.Builder var1, Object var2) {
            super.set(var1, this.coerceType(var2));
         }

         @Override
         public Message.Builder newBuilder() {
            return (Message.Builder)GeneratedMessage.invokeOrDie(this.newBuilderMethod, null);
         }

         @Override
         public Message.Builder getBuilder(GeneratedMessage.Builder var1) {
            return (Message.Builder)GeneratedMessage.invokeOrDie(this.getBuilderMethodBuilder, var1);
         }
      }

      private static final class SingularStringFieldAccessor extends GeneratedMessage.FieldAccessorTable.SingularFieldAccessor {
         private final java.lang.reflect.Method getBytesMethod;
         private final java.lang.reflect.Method getBytesMethodBuilder;
         private final java.lang.reflect.Method setBytesMethodBuilder;

         SingularStringFieldAccessor(
            Descriptors.FieldDescriptor var1, String var2, Class<? extends GeneratedMessage> var3, Class<? extends GeneratedMessage.Builder> var4, String var5
         ) {
            super(var1, var2, var3, var4, var5);
            this.getBytesMethod = GeneratedMessage.getMethodOrDie(var3, "get" + var2 + "Bytes");
            this.getBytesMethodBuilder = GeneratedMessage.getMethodOrDie(var4, "get" + var2 + "Bytes");
            this.setBytesMethodBuilder = GeneratedMessage.getMethodOrDie(var4, "set" + var2 + "Bytes", ByteString.class);
         }

         @Override
         public Object getRaw(GeneratedMessage var1) {
            return GeneratedMessage.invokeOrDie(this.getBytesMethod, var1);
         }

         @Override
         public Object getRaw(GeneratedMessage.Builder var1) {
            return GeneratedMessage.invokeOrDie(this.getBytesMethodBuilder, var1);
         }

         @Override
         public void set(GeneratedMessage.Builder var1, Object var2) {
            if (var2 instanceof ByteString) {
               GeneratedMessage.invokeOrDie(this.setBytesMethodBuilder, var1, var2);
            } else {
               super.set(var1, var2);
            }
         }
      }
   }

   public static class GeneratedExtension<ContainingType extends Message, Type> extends Extension<ContainingType, Type> {
      private GeneratedMessage.ExtensionDescriptorRetriever descriptorRetriever;
      private final Class singularType;
      private final Message messageDefaultInstance;
      private final java.lang.reflect.Method enumValueOf;
      private final java.lang.reflect.Method enumGetValueDescriptor;
      private final Extension.ExtensionType extensionType;

      GeneratedExtension(GeneratedMessage.ExtensionDescriptorRetriever var1, Class var2, Message var3, Extension.ExtensionType var4) {
         if (Message.class.isAssignableFrom(var2) && !var2.isInstance(var3)) {
            throw new IllegalArgumentException("Bad messageDefaultInstance for " + var2.getName());
         } else {
            this.descriptorRetriever = var1;
            this.singularType = var2;
            this.messageDefaultInstance = var3;
            if (ProtocolMessageEnum.class.isAssignableFrom(var2)) {
               this.enumValueOf = GeneratedMessage.getMethodOrDie(var2, "valueOf", Descriptors.EnumValueDescriptor.class);
               this.enumGetValueDescriptor = GeneratedMessage.getMethodOrDie(var2, "getValueDescriptor");
            } else {
               this.enumValueOf = null;
               this.enumGetValueDescriptor = null;
            }

            this.extensionType = var4;
         }
      }

      public void internalInit(final Descriptors.FieldDescriptor var1) {
         if (this.descriptorRetriever != null) {
            throw new IllegalStateException("Already initialized.");
         } else {
            this.descriptorRetriever = new GeneratedMessage.ExtensionDescriptorRetriever() {
               @Override
               public Descriptors.FieldDescriptor getDescriptor() {
                  return var1;
               }
            };
         }
      }

      @Override
      public Descriptors.FieldDescriptor getDescriptor() {
         if (this.descriptorRetriever == null) {
            throw new IllegalStateException("getDescriptor() called before internalInit()");
         } else {
            return this.descriptorRetriever.getDescriptor();
         }
      }

      @Override
      public Message getMessageDefaultInstance() {
         return this.messageDefaultInstance;
      }

      @Override
      protected Extension.ExtensionType getExtensionType() {
         return this.extensionType;
      }

      @Override
      protected Object fromReflectionType(Object var1) {
         Descriptors.FieldDescriptor var2 = this.getDescriptor();
         if (!var2.isRepeated()) {
            return this.singularFromReflectionType(var1);
         } else if (var2.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE && var2.getJavaType() != Descriptors.FieldDescriptor.JavaType.ENUM) {
            return var1;
         } else {
            ArrayList var3 = new ArrayList();

            for (Object var5 : (List)var1) {
               var3.add(this.singularFromReflectionType(var5));
            }

            return var3;
         }
      }

      @Override
      protected Object singularFromReflectionType(Object var1) {
         Descriptors.FieldDescriptor var2 = this.getDescriptor();
         switch (var2.getJavaType()) {
            case MESSAGE:
               if (this.singularType.isInstance(var1)) {
                  return var1;
               }

               return this.messageDefaultInstance.newBuilderForType().mergeFrom((Message)var1).build();
            case ENUM:
               return GeneratedMessage.invokeOrDie(this.enumValueOf, null, (Descriptors.EnumValueDescriptor)var1);
            default:
               return var1;
         }
      }

      @Override
      protected Object toReflectionType(Object var1) {
         Descriptors.FieldDescriptor var2 = this.getDescriptor();
         if (!var2.isRepeated()) {
            return this.singularToReflectionType(var1);
         } else if (var2.getJavaType() != Descriptors.FieldDescriptor.JavaType.ENUM) {
            return var1;
         } else {
            ArrayList var3 = new ArrayList();

            for (Object var5 : (List)var1) {
               var3.add(this.singularToReflectionType(var5));
            }

            return var3;
         }
      }

      @Override
      protected Object singularToReflectionType(Object var1) {
         Descriptors.FieldDescriptor var2 = this.getDescriptor();
         switch (var2.getJavaType()) {
            case ENUM:
               return GeneratedMessage.invokeOrDie(this.enumGetValueDescriptor, var1);
            default:
               return var1;
         }
      }

      @Override
      public int getNumber() {
         return this.getDescriptor().getNumber();
      }

      @Override
      public WireFormat.FieldType getLiteType() {
         return this.getDescriptor().getLiteType();
      }

      @Override
      public boolean isRepeated() {
         return this.getDescriptor().isRepeated();
      }

      @Override
      public Type getDefaultValue() {
         if (this.isRepeated()) {
            return (Type)Collections.emptyList();
         } else {
            return (Type)(this.getDescriptor().getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE
               ? this.messageDefaultInstance
               : this.singularFromReflectionType(this.getDescriptor().getDefaultValue()));
         }
      }
   }
}
