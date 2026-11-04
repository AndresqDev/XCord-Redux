package org.jline.terminal;

import java.util.EnumSet;

public class MouseEvent {
   private final MouseEvent.Type type;
   private final MouseEvent.Button button;
   private final EnumSet<MouseEvent.Modifier> modifiers;
   private final int x;
   private final int y;

   public MouseEvent(MouseEvent.Type var1, MouseEvent.Button var2, EnumSet<MouseEvent.Modifier> var3, int var4, int var5) {
      this.type = var1;
      this.button = var2;
      this.modifiers = var3;
      this.x = var4;
      this.y = var5;
   }

   public MouseEvent.Type getType() {
      return this.type;
   }

   public MouseEvent.Button getButton() {
      return this.button;
   }

   public EnumSet<MouseEvent.Modifier> getModifiers() {
      return this.modifiers;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   @Override
   public String toString() {
      return "MouseEvent[type=" + this.type + ", button=" + this.button + ", modifiers=" + this.modifiers + ", x=" + this.x + ", y=" + this.y + ']';
   }

   public static enum Button {
      NoButton,
      Button1,
      Button2,
      Button3,
      WheelUp,
      WheelDown;
   }

   public static enum Modifier {
      Shift,
      Alt,
      Control;
   }

   public static enum Type {
      Released,
      Pressed,
      Wheel,
      Moved,
      Dragged;
   }
}
