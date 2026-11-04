package gnu.trove.decorator;

import gnu.trove.iterator.TFloatDoubleIterator;
import gnu.trove.map.TFloatDoubleMap;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;

public class TFloatDoubleMapDecorator extends AbstractMap<Float, Double> implements Externalizable, Cloneable, Map<Float, Double> {
   static final long serialVersionUID = 1L;
   protected TFloatDoubleMap _map;

   public TFloatDoubleMapDecorator() {
   }

   public TFloatDoubleMapDecorator(TFloatDoubleMap var1) {
      Objects.requireNonNull(var1);
      this._map = var1;
   }

   public TFloatDoubleMap getMap() {
      return this._map;
   }

   public Double put(Float var1, Double var2) {
      float var3;
      if (var1 == null) {
         var3 = this._map.getNoEntryKey();
      } else {
         var3 = this.unwrapKey(var1);
      }

      double var4;
      if (var2 == null) {
         var4 = this._map.getNoEntryValue();
      } else {
         var4 = this.unwrapValue(var2);
      }

      double var6 = this._map.put(var3, var4);
      return var6 == this._map.getNoEntryValue() ? null : this.wrapValue(var6);
   }

   public Double get(Object var1) {
      float var2;
      if (var1 != null) {
         if (!(var1 instanceof Float)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      double var3 = this._map.get(var2);
      return var3 == this._map.getNoEntryValue() ? null : this.wrapValue(var3);
   }

   @Override
   public void clear() {
      this._map.clear();
   }

   public Double remove(Object var1) {
      float var2;
      if (var1 != null) {
         if (!(var1 instanceof Float)) {
            return null;
         }

         var2 = this.unwrapKey(var1);
      } else {
         var2 = this._map.getNoEntryKey();
      }

      double var3 = this._map.remove(var2);
      return var3 == this._map.getNoEntryValue() ? null : this.wrapValue(var3);
   }

   @Override
   public Set<Entry<Float, Double>> entrySet() {
      return new AbstractSet<Entry<Float, Double>>() {
         @Override
         public int size() {
            return TFloatDoubleMapDecorator.this._map.size();
         }

         @Override
         public boolean isEmpty() {
            return TFloatDoubleMapDecorator.this.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Object var2 = ((Entry)var1).getKey();
               Object var3 = ((Entry)var1).getValue();
               return TFloatDoubleMapDecorator.this.containsKey(var2) && TFloatDoubleMapDecorator.this.get(var2).equals(var3);
            }
         }

         @Override
         public Iterator<Entry<Float, Double>> iterator() {
            return new Iterator<Entry<Float, Double>>() {
               private final TFloatDoubleIterator it = TFloatDoubleMapDecorator.this._map.iterator();

               public Entry<Float, Double> next() {
                  this.it.advance();
                  float var1 = this.it.key();
                  final Float var2 = var1 == TFloatDoubleMapDecorator.this._map.getNoEntryKey() ? null : TFloatDoubleMapDecorator.this.wrapKey(var1);
                  double var3 = this.it.value();
                  final Double var5 = var3 == TFloatDoubleMapDecorator.this._map.getNoEntryValue() ? null : TFloatDoubleMapDecorator.this.wrapValue(var3);
                  return new Entry<Float, Double>() {
                     private Double val = var5;

                     @Override
                     public boolean equals(Object var1) {
                        return var1 instanceof Entry && ((Entry)var1).getKey().equals(var2) && ((Entry)var1).getValue().equals(this.val);
                     }

                     public Float getKey() {
                        return var2;
                     }

                     public Double getValue() {
                        return this.val;
                     }

                     @Override
                     public int hashCode() {
                        return var2.hashCode() + this.val.hashCode();
                     }

                     public Double setValue(Double var1) {
                        this.val = var1;
                        return TFloatDoubleMapDecorator.this.put(var2, var1);
                     }
                  };
               }

               @Override
               public boolean hasNext() {
                  return this.it.hasNext();
               }

               @Override
               public void remove() {
                  this.it.remove();
               }
            };
         }

         public boolean add(Entry<Float, Double> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            boolean var2 = false;
            if (this.contains(var1)) {
               Float var3 = (Float)((Entry)var1).getKey();
               TFloatDoubleMapDecorator.this._map.remove(TFloatDoubleMapDecorator.this.unwrapKey(var3));
               var2 = true;
            }

            return var2;
         }

         @Override
         public boolean addAll(Collection<? extends Entry<Float, Double>> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            TFloatDoubleMapDecorator.this.clear();
         }
      };
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 instanceof Double && this._map.containsValue(this.unwrapValue(var1));
   }

   @Override
   public boolean containsKey(Object var1) {
      return var1 == null ? this._map.containsKey(this._map.getNoEntryKey()) : var1 instanceof Float && this._map.containsKey(this.unwrapKey(var1));
   }

   @Override
   public int size() {
      return this._map.size();
   }

   @Override
   public boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public void putAll(Map<? extends Float, ? extends Double> var1) {
      Iterator var2 = var1.entrySet().iterator();
      int var3 = var1.size();

      while (var3-- > 0) {
         Entry var4 = (Entry)var2.next();
         this.put((Float)var4.getKey(), (Double)var4.getValue());
      }
   }

   protected Float wrapKey(float var1) {
      return var1;
   }

   protected float unwrapKey(Object var1) {
      return (Float)var1;
   }

   protected Double wrapValue(double var1) {
      return var1;
   }

   protected double unwrapValue(Object var1) {
      return (Double)var1;
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      this._map = (TFloatDoubleMap)var1.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeObject(this._map);
   }
}
