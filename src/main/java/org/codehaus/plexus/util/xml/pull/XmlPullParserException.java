package org.codehaus.plexus.util.xml.pull;

public class XmlPullParserException extends Exception {
      protected Throwable detail;
   protected int row = -1;
   protected int column = -1;

   public XmlPullParserException(String var1) {
      super(var1);
   }

   public XmlPullParserException(String var1, XmlPullParser var2, Throwable var3) {
      super(
         (var1 == null ? "" : var1 + " ")
            + (var2 == null ? "" : "(position:" + var2.getPositionDescription() + ") ")
            + (var3 == null ? "" : "caused by: " + var3),
         var3
      );
      if (var2 != null) {
         this.row = var2.getLineNumber();
         this.column = var2.getColumnNumber();
      }

      this.detail = var3;
   }

      public Throwable getDetail() {
      return this.getCause();
   }

   public int getLineNumber() {
      return this.row;
   }

   public int getColumnNumber() {
      return this.column;
   }

   @Override
   public void printStackTrace() {
      if (this.getCause() == null) {
         super.printStackTrace();
      } else {
         synchronized (System.err) {
            System.err.println(super.getMessage() + "; nested exception is:");
            this.getCause().printStackTrace();
         }
      }
   }
}
