package org.apache.commons.lang3.builder;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.Validate;

public class DiffResult implements Iterable<Diff<?>> {
   public static final String OBJECTS_SAME_STRING = "";
   private static final String DIFFERS_STRING = "differs from";
   private final List<Diff<?>> diffs;
   private final Object lhs;
   private final Object rhs;
   private final ToStringStyle style;

   DiffResult(Object var1, Object var2, List<Diff<?>> var3, ToStringStyle var4) {
      Validate.isTrue(var1 != null, "Left hand object cannot be null");
      Validate.isTrue(var2 != null, "Right hand object cannot be null");
      Validate.isTrue(var3 != null, "List of differences cannot be null");
      this.diffs = var3;
      this.lhs = var1;
      this.rhs = var2;
      if (var4 == null) {
         this.style = ToStringStyle.DEFAULT_STYLE;
      } else {
         this.style = var4;
      }
   }

   public List<Diff<?>> getDiffs() {
      return Collections.unmodifiableList(this.diffs);
   }

   public int getNumberOfDiffs() {
      return this.diffs.size();
   }

   public ToStringStyle getToStringStyle() {
      return this.style;
   }

   @Override
   public String toString() {
      return this.toString(this.style);
   }

   public String toString(ToStringStyle var1) {
      if (this.diffs.isEmpty()) {
         return "";
      } else {
         ToStringBuilder var2 = new ToStringBuilder(this.lhs, var1);
         ToStringBuilder var3 = new ToStringBuilder(this.rhs, var1);

         for (Diff var5 : this.diffs) {
            var2.append(var5.getFieldName(), var5.getLeft());
            var3.append(var5.getFieldName(), var5.getRight());
         }

         return String.format("%s %s %s", var2.build(), "differs from", var3.build());
      }
   }

   @Override
   public Iterator<Diff<?>> iterator() {
      return this.diffs.iterator();
   }
}
