package com.google.protobuf;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RepeatedFieldBuilder<MType extends GeneratedMessage, BType extends GeneratedMessage.Builder, IType extends MessageOrBuilder>
   implements GeneratedMessage.BuilderParent {
   private GeneratedMessage.BuilderParent parent;
   private List<MType> messages;
   private boolean isMessagesListMutable;
   private List<SingleFieldBuilder<MType, BType, IType>> builders;
   private boolean isClean;
   private RepeatedFieldBuilder.MessageExternalList<MType, BType, IType> externalMessageList;
   private RepeatedFieldBuilder.BuilderExternalList<MType, BType, IType> externalBuilderList;
   private RepeatedFieldBuilder.MessageOrBuilderExternalList<MType, BType, IType> externalMessageOrBuilderList;

   public RepeatedFieldBuilder(List<MType> var1, boolean var2, GeneratedMessage.BuilderParent var3, boolean var4) {
      this.messages = var1;
      this.isMessagesListMutable = var2;
      this.parent = var3;
      this.isClean = var4;
   }

   public void dispose() {
      this.parent = null;
   }

   private void ensureMutableMessageList() {
      if (!this.isMessagesListMutable) {
         this.messages = new ArrayList<>(this.messages);
         this.isMessagesListMutable = true;
      }
   }

   private void ensureBuilders() {
      if (this.builders == null) {
         this.builders = new ArrayList<>(this.messages.size());

         for (int var1 = 0; var1 < this.messages.size(); var1++) {
            this.builders.add(null);
         }
      }
   }

   public int getCount() {
      return this.messages.size();
   }

   public boolean isEmpty() {
      return this.messages.isEmpty();
   }

   public MType getMessage(int var1) {
      return this.getMessage(var1, false);
   }

   private MType getMessage(int var1, boolean var2) {
      if (this.builders == null) {
         return this.messages.get(var1);
      } else {
         SingleFieldBuilder var3 = this.builders.get(var1);
         if (var3 == null) {
            return this.messages.get(var1);
         } else {
            return (MType)(var2 ? var3.build() : var3.getMessage());
         }
      }
   }

   public BType getBuilder(int var1) {
      this.ensureBuilders();
      SingleFieldBuilder var2 = this.builders.get(var1);
      if (var2 == null) {
         GeneratedMessage var3 = this.messages.get(var1);
         var2 = new SingleFieldBuilder<>(var3, this, this.isClean);
         this.builders.set(var1, var2);
      }

      return (BType)var2.getBuilder();
   }

   public IType getMessageOrBuilder(int var1) {
      if (this.builders == null) {
         return (IType)this.messages.get(var1);
      } else {
         SingleFieldBuilder var2 = this.builders.get(var1);
         return (IType)(var2 == null ? this.messages.get(var1) : var2.getMessageOrBuilder());
      }
   }

   public RepeatedFieldBuilder<MType, BType, IType> setMessage(int var1, MType var2) {
      Internal.checkNotNull(var2);
      this.ensureMutableMessageList();
      this.messages.set(var1, (MType)var2);
      if (this.builders != null) {
         SingleFieldBuilder var3 = this.builders.set(var1, null);
         if (var3 != null) {
            var3.dispose();
         }
      }

      this.onChanged();
      this.incrementModCounts();
      return this;
   }

   public RepeatedFieldBuilder<MType, BType, IType> addMessage(MType var1) {
      Internal.checkNotNull(var1);
      this.ensureMutableMessageList();
      this.messages.add((MType)var1);
      if (this.builders != null) {
         this.builders.add(null);
      }

      this.onChanged();
      this.incrementModCounts();
      return this;
   }

   public RepeatedFieldBuilder<MType, BType, IType> addMessage(int var1, MType var2) {
      Internal.checkNotNull(var2);
      this.ensureMutableMessageList();
      this.messages.add(var1, (MType)var2);
      if (this.builders != null) {
         this.builders.add(var1, null);
      }

      this.onChanged();
      this.incrementModCounts();
      return this;
   }

   public RepeatedFieldBuilder<MType, BType, IType> addAllMessages(Iterable<? extends MType> var1) {
      for (GeneratedMessage var3 : var1) {
         Internal.checkNotNull(var3);
      }

      int var5 = -1;
      if (var1 instanceof Collection) {
         Collection var6 = (Collection)var1;
         if (var6.size() == 0) {
            return this;
         }

         var5 = var6.size();
      }

      this.ensureMutableMessageList();
      if (var5 >= 0 && this.messages instanceof ArrayList) {
         ((ArrayList)this.messages).ensureCapacity(this.messages.size() + var5);
      }

      for (GeneratedMessage var4 : var1) {
         this.addMessage((MType)var4);
      }

      this.onChanged();
      this.incrementModCounts();
      return this;
   }

   public BType addBuilder(MType var1) {
      this.ensureMutableMessageList();
      this.ensureBuilders();
      SingleFieldBuilder var2 = new SingleFieldBuilder<>(var1, this, this.isClean);
      this.messages.add(null);
      this.builders.add(var2);
      this.onChanged();
      this.incrementModCounts();
      return (BType)var2.getBuilder();
   }

   public BType addBuilder(int var1, MType var2) {
      this.ensureMutableMessageList();
      this.ensureBuilders();
      SingleFieldBuilder var3 = new SingleFieldBuilder<>(var2, this, this.isClean);
      this.messages.add(var1, null);
      this.builders.add(var1, var3);
      this.onChanged();
      this.incrementModCounts();
      return (BType)var3.getBuilder();
   }

   public void remove(int var1) {
      this.ensureMutableMessageList();
      this.messages.remove(var1);
      if (this.builders != null) {
         SingleFieldBuilder var2 = this.builders.remove(var1);
         if (var2 != null) {
            var2.dispose();
         }
      }

      this.onChanged();
      this.incrementModCounts();
   }

   public void clear() {
      this.messages = Collections.emptyList();
      this.isMessagesListMutable = false;
      if (this.builders != null) {
         for (SingleFieldBuilder var2 : this.builders) {
            if (var2 != null) {
               var2.dispose();
            }
         }

         this.builders = null;
      }

      this.onChanged();
      this.incrementModCounts();
   }

   public List<MType> build() {
      this.isClean = true;
      if (!this.isMessagesListMutable && this.builders == null) {
         return this.messages;
      } else {
         boolean var1 = true;
         if (!this.isMessagesListMutable) {
            for (int var2 = 0; var2 < this.messages.size(); var2++) {
               Message var3 = this.messages.get(var2);
               SingleFieldBuilder var4 = this.builders.get(var2);
               if (var4 != null && var4.build() != var3) {
                  var1 = false;
                  break;
               }
            }

            if (var1) {
               return this.messages;
            }
         }

         this.ensureMutableMessageList();

         for (int var5 = 0; var5 < this.messages.size(); var5++) {
            this.messages.set(var5, this.getMessage(var5, true));
         }

         this.messages = Collections.unmodifiableList(this.messages);
         this.isMessagesListMutable = false;
         return this.messages;
      }
   }

   public List<MType> getMessageList() {
      if (this.externalMessageList == null) {
         this.externalMessageList = new RepeatedFieldBuilder.MessageExternalList<>(this);
      }

      return this.externalMessageList;
   }

   public List<BType> getBuilderList() {
      if (this.externalBuilderList == null) {
         this.externalBuilderList = new RepeatedFieldBuilder.BuilderExternalList<>(this);
      }

      return this.externalBuilderList;
   }

   public List<IType> getMessageOrBuilderList() {
      if (this.externalMessageOrBuilderList == null) {
         this.externalMessageOrBuilderList = new RepeatedFieldBuilder.MessageOrBuilderExternalList<>(this);
      }

      return this.externalMessageOrBuilderList;
   }

   private void onChanged() {
      if (this.isClean && this.parent != null) {
         this.parent.markDirty();
         this.isClean = false;
      }
   }

   @Override
   public void markDirty() {
      this.onChanged();
   }

   private void incrementModCounts() {
      if (this.externalMessageList != null) {
         this.externalMessageList.incrementModCount();
      }

      if (this.externalBuilderList != null) {
         this.externalBuilderList.incrementModCount();
      }

      if (this.externalMessageOrBuilderList != null) {
         this.externalMessageOrBuilderList.incrementModCount();
      }
   }

   private static class BuilderExternalList<MType extends GeneratedMessage, BType extends GeneratedMessage.Builder, IType extends MessageOrBuilder>
      extends AbstractList<BType>
      implements List<BType> {
      RepeatedFieldBuilder<MType, BType, IType> builder;

      BuilderExternalList(RepeatedFieldBuilder<MType, BType, IType> var1) {
         this.builder = var1;
      }

      @Override
      public int size() {
         return this.builder.getCount();
      }

      public BType get(int var1) {
         return this.builder.getBuilder(var1);
      }

      void incrementModCount() {
         this.modCount++;
      }
   }

   private static class MessageExternalList<MType extends GeneratedMessage, BType extends GeneratedMessage.Builder, IType extends MessageOrBuilder>
      extends AbstractList<MType>
      implements List<MType> {
      RepeatedFieldBuilder<MType, BType, IType> builder;

      MessageExternalList(RepeatedFieldBuilder<MType, BType, IType> var1) {
         this.builder = var1;
      }

      @Override
      public int size() {
         return this.builder.getCount();
      }

      public MType get(int var1) {
         return this.builder.getMessage(var1);
      }

      void incrementModCount() {
         this.modCount++;
      }
   }

   private static class MessageOrBuilderExternalList<MType extends GeneratedMessage, BType extends GeneratedMessage.Builder, IType extends MessageOrBuilder>
      extends AbstractList<IType>
      implements List<IType> {
      RepeatedFieldBuilder<MType, BType, IType> builder;

      MessageOrBuilderExternalList(RepeatedFieldBuilder<MType, BType, IType> var1) {
         this.builder = var1;
      }

      @Override
      public int size() {
         return this.builder.getCount();
      }

      public IType get(int var1) {
         return this.builder.getMessageOrBuilder(var1);
      }

      void incrementModCount() {
         this.modCount++;
      }
   }
}
