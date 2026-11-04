package net.md_5.bungee.api.score;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Scoreboard {
   private String name;
   private Position position;
   private final Map<String, Objective> objectives = new HashMap<>();
   private final Map<String, Score> scores = new HashMap<>();
   private final Map<String, Team> teams = new HashMap<>();

   public Collection<Objective> getObjectives() {
      return Collections.unmodifiableCollection(this.objectives.values());
   }

   public Collection<Score> getScores() {
      return Collections.unmodifiableCollection(this.scores.values());
   }

   public Collection<Team> getTeams() {
      return Collections.unmodifiableCollection(this.teams.values());
   }

   public void addObjective(Objective var1) {
      Preconditions.checkNotNull(var1, "objective");
      this.objectives.put(var1.getName(), var1);
   }

   public void addScore(Score var1) {
      Preconditions.checkNotNull(var1, "score");
      this.scores.put(var1.getItemName(), var1);
   }

   public Score getScore(String var1) {
      return this.scores.get(var1);
   }

   public void addTeam(Team var1) {
      Preconditions.checkNotNull(var1, "team");
      this.teams.put(var1.getName(), var1);
   }

   public Team getTeam(String var1) {
      return this.teams.get(var1);
   }

   public Objective getObjective(String var1) {
      return this.objectives.get(var1);
   }

   public void removeObjective(String var1) {
      this.objectives.remove(var1);
   }

   public void removeScore(String var1) {
      this.scores.remove(var1);
   }

   public void removeTeam(String var1) {
      this.teams.remove(var1);
   }

   public void clear() {
      this.name = null;
      this.position = null;
      this.objectives.clear();
      this.scores.clear();
      this.teams.clear();
   }

   public String getName() {
      return this.name;
   }

   public Position getPosition() {
      return this.position;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setPosition(Position var1) {
      this.position = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Scoreboard)) {
         return false;
      } else {
         Scoreboard var2 = (Scoreboard)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            String var3 = this.getName();
            String var4 = var2.getName();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               Position var5 = this.getPosition();
               Position var6 = var2.getPosition();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  Collection var7 = this.getObjectives();
                  Collection var8 = var2.getObjectives();
                  if (var7 == null ? var8 == null : var7.equals(var8)) {
                     Collection var9 = this.getScores();
                     Collection var10 = var2.getScores();
                     if (var9 == null ? var10 == null : var9.equals(var10)) {
                        Collection var11 = this.getTeams();
                        Collection var12 = var2.getTeams();
                        return var11 == null ? var12 == null : var11.equals(var12);
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof Scoreboard;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      String var3 = this.getName();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      Position var4 = this.getPosition();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      Collection var5 = this.getObjectives();
      var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
      Collection var6 = this.getScores();
      var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
      Collection var7 = this.getTeams();
      return var2 * 59 + (var7 == null ? 43 : var7.hashCode());
   }

   @Override
   public String toString() {
      return "Scoreboard(name="
         + this.getName()
         + ", position="
         + this.getPosition()
         + ", objectives="
         + this.getObjectives()
         + ", scores="
         + this.getScores()
         + ", teams="
         + this.getTeams()
         + ")";
   }
}
