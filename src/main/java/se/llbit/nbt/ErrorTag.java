package se.llbit.nbt;

import java.io.DataOutputStream;

public class ErrorTag extends SpecificTag {
   public final String message;

   @Override
   public void write(DataOutputStream var1) {
      throw new RuntimeException("Cannot write an error tag to NBT stream (" + this.getError() + ")");
   }

   public ErrorTag(String var1) {
      this.message = var1;
   }

   public String getError() {
      return this.message != null ? this.message : "";
   }

   @Override
   public String extraInfo() {
      return ": \"" + this.getError() + '"';
   }

   public String type() {
      return "Tag.Error";
   }

   @Override
   public int tagType() {
      throw new Error("Cannot write an error tag to NBT stream (" + this.getError() + ")");
   }

   @Override
   public String tagName() {
      return "Tag.Error";
   }

   @Override
   public boolean isEnd() {
      return true;
   }

   @Override
   public boolean isError() {
      return true;
   }

   @Override
   public String error() {
      return this.getError();
   }

   @Override
   public boolean equals(Object var1) {
      return false;
   }
}
