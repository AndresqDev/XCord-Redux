package org.yaml.snakeyaml.emitter;

public final class ScalarAnalysis {
   private String scalar;
   private boolean empty;
   private boolean multiline;
   private boolean allowFlowPlain;
   private boolean allowBlockPlain;
   private boolean allowSingleQuoted;
   private boolean allowBlock;

   public ScalarAnalysis(String var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6, boolean var7) {
      this.scalar = var1;
      this.empty = var2;
      this.multiline = var3;
      this.allowFlowPlain = var4;
      this.allowBlockPlain = var5;
      this.allowSingleQuoted = var6;
      this.allowBlock = var7;
   }

   public String getScalar() {
      return this.scalar;
   }

   public boolean isEmpty() {
      return this.empty;
   }

   public boolean isMultiline() {
      return this.multiline;
   }

   public boolean isAllowFlowPlain() {
      return this.allowFlowPlain;
   }

   public boolean isAllowBlockPlain() {
      return this.allowBlockPlain;
   }

   public boolean isAllowSingleQuoted() {
      return this.allowSingleQuoted;
   }

   public boolean isAllowBlock() {
      return this.allowBlock;
   }
}
