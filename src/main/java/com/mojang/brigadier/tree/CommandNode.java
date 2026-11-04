package com.mojang.brigadier.tree;

import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class CommandNode<S> implements Comparable<CommandNode<S>> {
   private Map<String, CommandNode<S>> children = new LinkedHashMap<>();
   private Map<String, LiteralCommandNode<S>> literals = new LinkedHashMap<>();
   private Map<String, ArgumentCommandNode<S, ?>> arguments = new LinkedHashMap<>();
   private final Predicate<S> requirement;
   private final CommandNode<S> redirect;
   private final RedirectModifier<S> modifier;
   private final boolean forks;
   private Command<S> command;

   protected CommandNode(Command<S> var1, Predicate<S> var2, CommandNode<S> var3, RedirectModifier<S> var4, boolean var5) {
      this.command = var1;
      this.requirement = var2;
      this.redirect = var3;
      this.modifier = var4;
      this.forks = var5;
   }

   public Command<S> getCommand() {
      return this.command;
   }

   public Collection<CommandNode<S>> getChildren() {
      return this.children.values();
   }

   public CommandNode<S> getChild(String var1) {
      return this.children.get(var1);
   }

   public CommandNode<S> getRedirect() {
      return this.redirect;
   }

   public RedirectModifier<S> getRedirectModifier() {
      return this.modifier;
   }

   public boolean canUse(S var1) {
      return this.requirement.test((S)var1);
   }

   public void addChild(CommandNode<S> var1) {
      if (var1 instanceof RootCommandNode) {
         throw new UnsupportedOperationException("Cannot add a RootCommandNode as a child to any other CommandNode");
      } else {
         CommandNode var2 = this.children.get(var1.getName());
         if (var2 != null) {
            if (var1.getCommand() != null) {
               var2.command = var1.getCommand();
            }

            for (CommandNode var4 : var1.getChildren()) {
               var2.addChild(var4);
            }
         } else {
            this.children.put(var1.getName(), var1);
            if (var1 instanceof LiteralCommandNode) {
               this.literals.put(var1.getName(), (LiteralCommandNode<S>)var1);
            } else if (var1 instanceof ArgumentCommandNode) {
               this.arguments.put(var1.getName(), (ArgumentCommandNode<S, ?>)var1);
            }
         }

         this.children = this.children
            .entrySet()
            .stream()
            .sorted(Entry.comparingByValue())
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (var0, var1x) -> var0, LinkedHashMap::new));
      }
   }

   public void findAmbiguities(AmbiguityConsumer<S> var1) {
      HashSet var2 = new HashSet();

      for (CommandNode var4 : this.children.values()) {
         for (CommandNode var6 : this.children.values()) {
            if (var4 != var6) {
               for (String var8 : var4.getExamples()) {
                  if (var6.isValidInput(var8)) {
                     var2.add(var8);
                  }
               }

               if (var2.size() > 0) {
                  var1.ambiguous(this, var4, var6, var2);
                  var2 = new HashSet();
               }
            }
         }

         var4.findAmbiguities(var1);
      }
   }

   protected abstract boolean isValidInput(String var1);

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CommandNode)) {
         return false;
      } else {
         CommandNode var2 = (CommandNode)var1;
         if (!this.children.equals(var2.children)) {
            return false;
         } else {
            return this.command != null ? this.command.equals(var2.command) : var2.command == null;
         }
      }
   }

   @Override
   public int hashCode() {
      return 31 * this.children.hashCode() + (this.command != null ? this.command.hashCode() : 0);
   }

   public Predicate<S> getRequirement() {
      return this.requirement;
   }

   public abstract String getName();

   public abstract String getUsageText();

   public abstract void parse(StringReader var1, CommandContextBuilder<S> var2) throws CommandSyntaxException;

   public abstract CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) throws CommandSyntaxException;

   public abstract ArgumentBuilder<S, ?> createBuilder();

   protected abstract String getSortedKey();

   public Collection<? extends CommandNode<S>> getRelevantNodes(StringReader var1) {
      if (this.literals.size() <= 0) {
         return this.arguments.values();
      } else {
         int var2 = var1.getCursor();

         while (var1.canRead() && var1.peek() != ' ') {
            var1.skip();
         }

         String var3 = var1.getString().substring(var2, var1.getCursor());
         var1.setCursor(var2);
         LiteralCommandNode var4 = this.literals.get(var3);
         return (Collection<? extends CommandNode<S>>)(var4 != null ? Collections.singleton(var4) : this.arguments.values());
      }
   }

   public int compareTo(CommandNode<S> var1) {
      if (this instanceof LiteralCommandNode == (var1 instanceof LiteralCommandNode)) {
         return this.getSortedKey().compareTo(var1.getSortedKey());
      } else {
         return var1 instanceof LiteralCommandNode ? 1 : -1;
      }
   }

   public boolean isFork() {
      return this.forks;
   }

   public abstract Collection<String> getExamples();
}
