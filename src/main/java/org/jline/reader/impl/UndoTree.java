package org.jline.reader.impl;

import java.util.function.Consumer;

public class UndoTree<T> {
   private final Consumer<T> state;
   private final UndoTree<T>.Node parent;
   private UndoTree<T>.Node current;

   public UndoTree(Consumer<T> var1) {
      this.state = var1;
      this.parent = new UndoTree.Node(null);
      this.parent.left = this.parent;
      this.clear();
   }

   public void clear() {
      this.current = this.parent;
   }

   public void newState(T var1) {
      UndoTree.Node var2 = new UndoTree.Node(var1);
      this.current.right = var2;
      var2.left = this.current;
      this.current = var2;
   }

   public boolean canUndo() {
      return this.current.left != this.parent;
   }

   public boolean canRedo() {
      return this.current.right != null;
   }

   public void undo() {
      if (!this.canUndo()) {
         throw new IllegalStateException("Cannot undo.");
      } else {
         this.current = this.current.left;
         this.state.accept(this.current.state);
      }
   }

   public void redo() {
      if (!this.canRedo()) {
         throw new IllegalStateException("Cannot redo.");
      } else {
         this.current = this.current.right;
         this.state.accept(this.current.state);
      }
   }

   private class Node {
      private final T state;
      private UndoTree<T>.Node left = null;
      private UndoTree<T>.Node right = null;

      public Node(T var2) {
         this.state = (T)var2;
      }
   }
}
