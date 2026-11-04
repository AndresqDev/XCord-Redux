package io.netty.channel.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

final class SelectedSelectionKeySetSelector extends Selector {
   private final SelectedSelectionKeySet selectionKeys;
   private final Selector delegate;

   SelectedSelectionKeySetSelector(Selector var1, SelectedSelectionKeySet var2) {
      this.delegate = var1;
      this.selectionKeys = var2;
   }

   @Override
   public boolean isOpen() {
      return this.delegate.isOpen();
   }

   @Override
   public SelectorProvider provider() {
      return this.delegate.provider();
   }

   @Override
   public Set<SelectionKey> keys() {
      return this.delegate.keys();
   }

   @Override
   public Set<SelectionKey> selectedKeys() {
      return this.delegate.selectedKeys();
   }

   @Override
   public int selectNow() throws IOException {
      this.selectionKeys.reset();
      return this.delegate.selectNow();
   }

   @Override
   public int select(long var1) throws IOException {
      this.selectionKeys.reset();
      return this.delegate.select(var1);
   }

   @Override
   public int select() throws IOException {
      this.selectionKeys.reset();
      return this.delegate.select();
   }

   @Override
   public Selector wakeup() {
      return this.delegate.wakeup();
   }

   @Override
   public void close() throws IOException {
      this.delegate.close();
   }
}
