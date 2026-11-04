package com.google.protobuf;

public class SingleFieldBuilderV3<MType extends AbstractMessage, BType extends AbstractMessage.Builder, IType extends MessageOrBuilder>
   implements AbstractMessage.BuilderParent {
   private AbstractMessage.BuilderParent parent;
   private BType builder;
   private MType message;
   private boolean isClean;

   public SingleFieldBuilderV3(MType var1, AbstractMessage.BuilderParent var2, boolean var3) {
      this.message = Internal.checkNotNull((MType)var1);
      this.parent = var2;
      this.isClean = var3;
   }

   public void dispose() {
      this.parent = null;
   }

   public MType getMessage() {
      if (this.message == null) {
         this.message = (MType)this.builder.buildPartial();
      }

      return this.message;
   }

   public MType build() {
      this.isClean = true;
      return this.getMessage();
   }

   public BType getBuilder() {
      if (this.builder == null) {
         this.builder = (BType)this.message.newBuilderForType(this);
         this.builder.mergeFrom(this.message);
         this.builder.markClean();
      }

      return this.builder;
   }

   public IType getMessageOrBuilder() {
      return (IType)(this.builder != null ? this.builder : this.message);
   }

   public SingleFieldBuilderV3<MType, BType, IType> setMessage(MType var1) {
      this.message = Internal.checkNotNull((MType)var1);
      if (this.builder != null) {
         this.builder.dispose();
         this.builder = null;
      }

      this.onChanged();
      return this;
   }

   public SingleFieldBuilderV3<MType, BType, IType> mergeFrom(MType var1) {
      if (this.builder == null && this.message == this.message.getDefaultInstanceForType()) {
         this.message = (MType)var1;
      } else {
         this.getBuilder().mergeFrom(var1);
      }

      this.onChanged();
      return this;
   }

   public SingleFieldBuilderV3<MType, BType, IType> clear() {
      this.message = (MType)(this.message != null ? this.message.getDefaultInstanceForType() : this.builder.getDefaultInstanceForType());
      if (this.builder != null) {
         this.builder.dispose();
         this.builder = null;
      }

      this.onChanged();
      return this;
   }

   private void onChanged() {
      if (this.builder != null) {
         this.message = null;
      }

      if (this.isClean && this.parent != null) {
         this.parent.markDirty();
         this.isClean = false;
      }
   }

   @Override
   public void markDirty() {
      this.onChanged();
   }
}
