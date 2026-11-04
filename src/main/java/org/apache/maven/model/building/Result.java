package org.apache.maven.model.building;

import java.util.ArrayList;
import java.util.Collections;

public class Result<T> {
   private final boolean errors;
   private final T value;
   private final Iterable<? extends ModelProblem> problems;

   public static <T> Result<T> success(T var0) {
      return success((T)var0, Collections.emptyList());
   }

   public static <T> Result<T> success(T var0, Iterable<? extends ModelProblem> var1) {
      assert !hasErrors(var1);

      return new Result<>(false, (T)var0, var1);
   }

   public static <T> Result<T> success(T var0, Result<?>... var1) {
      ArrayList var2 = new ArrayList();

      for (Result var6 : var1) {
         for (ModelProblem var8 : var6.getProblems()) {
            var2.add(var8);
         }
      }

      return success((T)var0, var2);
   }

   public static <T> Result<T> error(Iterable<? extends ModelProblem> var0) {
      return error(null, var0);
   }

   public static <T> Result<T> error(T var0) {
      return error((T)var0, Collections.emptyList());
   }

   public static <T> Result<T> error(Result<?> var0) {
      return error(var0.getProblems());
   }

   public static <T> Result<T> error(Result<?>... var0) {
      ArrayList var1 = new ArrayList();

      for (Result var5 : var0) {
         for (ModelProblem var7 : var5.getProblems()) {
            var1.add(var7);
         }
      }

      return error(var1);
   }

   public static <T> Result<T> error(T var0, Iterable<? extends ModelProblem> var1) {
      return new Result<>(true, (T)var0, var1);
   }

   public static <T> Result<T> newResult(T var0, Iterable<? extends ModelProblem> var1) {
      return new Result<>(hasErrors(var1), (T)var0, var1);
   }

   public static <T> Result<T> addProblem(Result<T> var0, ModelProblem var1) {
      return addProblems(var0, Collections.singleton(var1));
   }

   public static <T> Result<T> addProblems(Result<T> var0, Iterable<? extends ModelProblem> var1) {
      ArrayList var2 = new ArrayList();

      for (ModelProblem var4 : var1) {
         var2.add(var4);
      }

      for (ModelProblem var6 : var0.getProblems()) {
         var2.add(var6);
      }

      return new Result<>(var0.hasErrors() || hasErrors(var1), (T)var0.get(), var2);
   }

   public static <T> Result<T> addProblems(Result<T> var0, Result<?>... var1) {
      ArrayList var2 = new ArrayList();

      for (Result var6 : var1) {
         for (ModelProblem var8 : var6.getProblems()) {
            var2.add(var8);
         }
      }

      return addProblems(var0, var2);
   }

   public static <T> Result<Iterable<T>> newResultSet(Iterable<? extends Result<? extends T>> var0) {
      boolean var1 = false;
      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList();

      for (Result var5 : var0) {
         var2.add(var5.get());

         for (ModelProblem var7 : var5.getProblems()) {
            var3.add(var7);
         }

         if (var5.hasErrors()) {
            var1 = true;
         }
      }

      return new Result<>(var1, var2, var3);
   }

   private static boolean hasErrors(Iterable<? extends ModelProblem> var0) {
      for (ModelProblem var2 : var0) {
         if (var2.getSeverity().equals(ModelProblem.Severity.ERROR) || var2.getSeverity().equals(ModelProblem.Severity.FATAL)) {
            return true;
         }
      }

      return false;
   }

   private Result(boolean var1, T var2, Iterable<? extends ModelProblem> var3) {
      this.errors = var1;
      this.value = (T)var2;
      this.problems = var3;
   }

   public Iterable<? extends ModelProblem> getProblems() {
      return this.problems;
   }

   public T get() {
      return this.value;
   }

   public boolean hasErrors() {
      return this.errors;
   }
}
