package gnu.trove.decorator;

import gnu.trove.list.TCharList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public class TCharListDecorator extends AbstractList<Character> implements Externalizable, Cloneable, List<Character> {
   static final long serialVersionUID = 1L;
   protected TCharList list;

   public TCharListDecorator() {
   }

   public TCharListDecorator(TCharList var1) {
      Objects.requireNonNull(var1);
      this.list = var1;
   }

   public TCharList getList() {
      return this.list;
   }

   @Override
   public int size() {
      return this.list.size();
   }

   public Character get(int var1) {
      char var2 = this.list.get(var1);
      return var2 == this.list.getNoEntryValue() ? null : var2;
   }

   public Character set(int var1, Character var2) {
      char var3 = this.list.set(var1, var2);
      return var3 == this.list.getNoEntryValue() ? null : var3;
   }

   public void add(int var1, Character var2) {
      this.list.insert(var1, var2);
   }

   public Character remove(int var1) {
      char var2 = this.list.removeAt(var1);
      return var2 == this.list.getNoEntryValue() ? null : var2;
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      this.list = (TCharList)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this.list);
   }
}
