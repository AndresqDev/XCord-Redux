package gnu.trove.list;

public abstract class TLinkableAdapter<T extends TLinkable> implements TLinkable<T> {
   private volatile T next;
   private volatile T prev;

   @Override
   public T getNext() {
      return this.next;
   }

   @Override
   public void setNext(T var1) {
      this.next = (T)var1;
   }

   @Override
   public T getPrevious() {
      return this.prev;
   }

   @Override
   public void setPrevious(T var1) {
      this.prev = (T)var1;
   }
}
