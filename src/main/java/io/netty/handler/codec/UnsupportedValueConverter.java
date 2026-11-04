package io.netty.handler.codec;

public final class UnsupportedValueConverter<V> implements ValueConverter<V> {
   private static final UnsupportedValueConverter INSTANCE = new UnsupportedValueConverter();

   private UnsupportedValueConverter() {
   }

   public static <V> UnsupportedValueConverter<V> instance() {
      return INSTANCE;
   }

   @Override
   public V convertObject(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V convertBoolean(boolean var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean convertToBoolean(V var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V convertByte(byte var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte convertToByte(V var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V convertChar(char var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public char convertToChar(V var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V convertShort(short var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public short convertToShort(V var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V convertInt(int var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int convertToInt(V var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V convertLong(long var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long convertToLong(V var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V convertTimeMillis(long var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long convertToTimeMillis(V var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V convertFloat(float var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float convertToFloat(V var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V convertDouble(double var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public double convertToDouble(V var1) {
      throw new UnsupportedOperationException();
   }
}
