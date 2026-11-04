package gnu.trove.decorator;

import gnu.trove.list.TShortList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

public class TShortListDecorator extends AbstractList<Short> implements Externalizable, Cloneable, List<Short> {
   static final long serialVersionUID = 1L;
   protected TShortList list;

   public TShortListDecorator() {
   }

   public TShortListDecorator(TShortList var1) {
      Objects.requireNonNull(var1);
      this.list = var1;
   }

   public TShortList getList() {
      return this.list;
   }

   @Override
   public int size() {
      return this.list.size();
   }

   public Short get(int var1) {
      short var2 = this.list.get(var1);
      return var2 == this.list.getNoEntryValue() ? null : var2;
   }

   public Short set(int var1, Short var2) {
      short var3 = this.list.set(var1, var2);
      return var3 == this.list.getNoEntryValue() ? null : var3;
   }

   public void add(int var1, Short var2) {
      this.list.insert(var1, var2);
   }

   public Short remove(int var1) {
      short var2 = this.list.removeAt(var1);
      return var2 == this.list.getNoEntryValue() ? null : var2;
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      this.list = (TShortList)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this.list);
   }
}
