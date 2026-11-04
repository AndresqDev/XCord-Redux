package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LongTag extends SpecificTag {
   public final long value;

   public static SpecificTag read(DataInputStream var0) {
      try {
         return new LongTag(var0.readLong());
      } catch (IOException var2) {
         return new ErrorTag("IOException while reading TAG_Long:\n" + var2.getMessage());
      }
   }

   @Override
   public void write(DataOutputStream var1) throws IOException {
      var1.writeLong(this.getData());
   }

   public LongTag(long var1) {
      this.value = var1;
   }

   public long getData() {
      return this.value;
   }

   @Override
   public String extraInfo() {
      return ": " + this.getData();
   }

   public String type() {
      return "TAG_Long";
   }

   @Override
   public String tagName() {
      return "TAG_Long";
   }

   @Override
   public int tagType() {
      return 4;
   }

   @Override
   public boolean boolValue() {
      return this.getData() != 0L;
   }

   @Override
   public boolean boolValue(boolean var1) {
      return this.getData() != 0L;
   }

   @Override
   public long longValue() {
      return this.getData();
   }

   @Override
   public long longValue(long var1) {
      return this.getData();
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof LongTag && ((LongTag)var1).value == this.value;
   }

   @Override
   public int hashCode() {
      return (int)this.value;
   }
}
