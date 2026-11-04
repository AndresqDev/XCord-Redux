package org.apache.logging.log4j.core.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeSet;

public final class CronExpression {
   protected static final int SECOND = 0;
   protected static final int MINUTE = 1;
   protected static final int HOUR = 2;
   protected static final int DAY_OF_MONTH = 3;
   protected static final int MONTH = 4;
   protected static final int DAY_OF_WEEK = 5;
   protected static final int YEAR = 6;
   protected static final int ALL_SPEC_INT = 99;
   protected static final int NO_SPEC_INT = 98;
   protected static final Integer ALL_SPEC = 99;
   protected static final Integer NO_SPEC = 98;
   protected static final Map<String, Integer> monthMap = new HashMap<>(20);
   protected static final Map<String, Integer> dayMap = new HashMap<>(60);
   private final String cronExpression;
   private TimeZone timeZone = null;
   protected transient TreeSet<Integer> seconds;
   protected transient TreeSet<Integer> minutes;
   protected transient TreeSet<Integer> hours;
   protected transient TreeSet<Integer> daysOfMonth;
   protected transient TreeSet<Integer> months;
   protected transient TreeSet<Integer> daysOfWeek;
   protected transient TreeSet<Integer> years;
   protected transient boolean lastdayOfWeek = false;
   protected transient int nthdayOfWeek = 0;
   protected transient boolean lastdayOfMonth = false;
   protected transient boolean nearestWeekday = false;
   protected transient int lastdayOffset = 0;
   protected transient boolean expressionParsed = false;
   public static final int MAX_YEAR = Calendar.getInstance().get(1) + 100;
   public static final Calendar MIN_CAL = Calendar.getInstance();
   public static final Date MIN_DATE = MIN_CAL.getTime();

   public CronExpression(String var1) throws ParseException {
      if (var1 == null) {
         throw new IllegalArgumentException("cronExpression cannot be null");
      } else {
         this.cronExpression = var1.toUpperCase(Locale.US);
         this.buildExpression(this.cronExpression);
      }
   }

   public boolean isSatisfiedBy(Date var1) {
      Calendar var2 = Calendar.getInstance(this.getTimeZone());
      var2.setTime(var1);
      var2.set(14, 0);
      Date var3 = var2.getTime();
      var2.add(13, -1);
      Date var4 = this.getTimeAfter(var2.getTime());
      return var4 != null && var4.equals(var3);
   }

   public Date getNextValidTimeAfter(Date var1) {
      return this.getTimeAfter(var1);
   }

   public Date getNextInvalidTimeAfter(Date var1) {
      long var2 = 1000L;
      Calendar var4 = Calendar.getInstance(this.getTimeZone());
      var4.setTime(var1);
      var4.set(14, 0);
      Date var5 = var4.getTime();

      while (var2 == 1000L) {
         Date var6 = this.getTimeAfter(var5);
         if (var6 == null) {
            break;
         }

         var2 = var6.getTime() - var5.getTime();
         if (var2 == 1000L) {
            var5 = var6;
         }
      }

      return new Date(var5.getTime() + 1000L);
   }

   public TimeZone getTimeZone() {
      if (this.timeZone == null) {
         this.timeZone = TimeZone.getDefault();
      }

      return this.timeZone;
   }

   public void setTimeZone(TimeZone var1) {
      this.timeZone = var1;
   }

   @Override
   public String toString() {
      return this.cronExpression;
   }

   public static boolean isValidExpression(String var0) {
      try {
         new CronExpression(var0);
         return true;
      } catch (ParseException var2) {
         return false;
      }
   }

   public static void validateExpression(String var0) throws ParseException {
      new CronExpression(var0);
   }

   protected void buildExpression(String var1) throws ParseException {
      this.expressionParsed = true;

      try {
         if (this.seconds == null) {
            this.seconds = new TreeSet<>();
         }

         if (this.minutes == null) {
            this.minutes = new TreeSet<>();
         }

         if (this.hours == null) {
            this.hours = new TreeSet<>();
         }

         if (this.daysOfMonth == null) {
            this.daysOfMonth = new TreeSet<>();
         }

         if (this.months == null) {
            this.months = new TreeSet<>();
         }

         if (this.daysOfWeek == null) {
            this.daysOfWeek = new TreeSet<>();
         }

         if (this.years == null) {
            this.years = new TreeSet<>();
         }

         int var2 = 0;

         for (StringTokenizer var3 = new StringTokenizer(var1, " \t", false); var3.hasMoreTokens() && var2 <= 6; var2++) {
            String var4 = var3.nextToken().trim();
            if (var2 == 3 && var4.indexOf(76) != -1 && var4.length() > 1 && var4.contains(",")) {
               throw new ParseException("Support for specifying 'L' and 'LW' with other days of the month is not implemented", -1);
            }

            if (var2 == 5 && var4.indexOf(76) != -1 && var4.length() > 1 && var4.contains(",")) {
               throw new ParseException("Support for specifying 'L' with other days of the week is not implemented", -1);
            }

            if (var2 == 5 && var4.indexOf(35) != -1 && var4.indexOf(35, var4.indexOf(35) + 1) != -1) {
               throw new ParseException("Support for specifying multiple \"nth\" days is not implemented.", -1);
            }

            StringTokenizer var5 = new StringTokenizer(var4, ",");

            while (var5.hasMoreTokens()) {
               String var6 = var5.nextToken();
               this.storeExpressionVals(0, var6, var2);
            }
         }

         if (var2 <= 5) {
            throw new ParseException("Unexpected end of expression.", var1.length());
         } else {
            if (var2 <= 6) {
               this.storeExpressionVals(0, "*", 6);
            }

            TreeSet var10 = this.getSet(5);
            TreeSet var11 = this.getSet(3);
            boolean var12 = !var11.contains(NO_SPEC);
            boolean var7 = !var10.contains(NO_SPEC);
            if ((!var12 || var7) && (!var7 || var12)) {
               throw new ParseException("Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.", 0);
            }
         }
      } catch (ParseException var8) {
         throw var8;
      } catch (Exception var9) {
         throw new ParseException("Illegal cron expression format (" + var9.toString() + ")", 0);
      }
   }

   protected int storeExpressionVals(int var1, String var2, int var3) throws ParseException {
      int var4 = 0;
      int var5 = this.skipWhiteSpace(var1, var2);
      if (var5 >= var2.length()) {
         return var5;
      } else {
         char var6 = var2.charAt(var5);
         if (var6 >= 'A' && var6 <= 'Z' && !var2.equals("L") && !var2.equals("LW") && !var2.matches("^L-[0-9]*[W]?")) {
            String var25 = var2.substring(var5, var5 + 3);
            int var28 = -1;
            int var9 = -1;
            if (var3 == 4) {
               var28 = this.getMonthNumber(var25) + 1;
               if (var28 <= 0) {
                  throw new ParseException("Invalid Month value: '" + var25 + "'", var5);
               }

               if (var2.length() > var5 + 3) {
                  var6 = var2.charAt(var5 + 3);
                  if (var6 == '-') {
                     var5 += 4;
                     var25 = var2.substring(var5, var5 + 3);
                     var9 = this.getMonthNumber(var25) + 1;
                     if (var9 <= 0) {
                        throw new ParseException("Invalid Month value: '" + var25 + "'", var5);
                     }
                  }
               }
            } else {
               if (var3 != 5) {
                  throw new ParseException("Illegal characters for this position: '" + var25 + "'", var5);
               }

               var28 = this.getDayOfWeekNumber(var25);
               if (var28 < 0) {
                  throw new ParseException("Invalid Day-of-Week value: '" + var25 + "'", var5);
               }

               if (var2.length() > var5 + 3) {
                  var6 = var2.charAt(var5 + 3);
                  switch (var6) {
                     case '#':
                        try {
                           var5 += 4;
                           this.nthdayOfWeek = Integer.parseInt(var2.substring(var5));
                           if (this.nthdayOfWeek >= 1 && this.nthdayOfWeek <= 5) {
                              break;
                           }

                           throw new Exception();
                        } catch (Exception var11) {
                           throw new ParseException("A numeric value between 1 and 5 must follow the '#' option", var5);
                        }
                     case '-':
                        var5 += 4;
                        var25 = var2.substring(var5, var5 + 3);
                        var9 = this.getDayOfWeekNumber(var25);
                        if (var9 < 0) {
                           throw new ParseException("Invalid Day-of-Week value: '" + var25 + "'", var5);
                        }
                        break;
                     case 'L':
                        this.lastdayOfWeek = true;
                        var5++;
                  }
               }
            }

            if (var9 != -1) {
               var4 = 1;
            }

            this.addToSet(var28, var9, var4, var3);
            return var5 + 3;
         } else {
            switch (var6) {
               case '*':
               case '/':
                  if (var6 == '*' && var5 + 1 >= var2.length()) {
                     this.addToSet(99, -1, var4, var3);
                     return var5 + 1;
                  } else {
                     if (var6 != '/' || var5 + 1 < var2.length() && var2.charAt(var5 + 1) != ' ' && var2.charAt(var5 + 1) != '\t') {
                        if (var6 == '*') {
                           var5++;
                        }

                        var6 = var2.charAt(var5);
                        if (var6 == '/') {
                           if (++var5 >= var2.length()) {
                              throw new ParseException("Unexpected end of string.", var5);
                           }

                           var4 = this.getNumericValue(var2, var5);
                           var5++;
                           if (var4 > 10) {
                              var5++;
                           }

                           if (var4 > 59 && (var3 == 0 || var3 == 1)) {
                              throw new ParseException("Increment > 60 : " + var4, var5);
                           }

                           if (var4 > 23 && var3 == 2) {
                              throw new ParseException("Increment > 24 : " + var4, var5);
                           }

                           if (var4 > 31 && var3 == 3) {
                              throw new ParseException("Increment > 31 : " + var4, var5);
                           }

                           if (var4 > 7 && var3 == 5) {
                              throw new ParseException("Increment > 7 : " + var4, var5);
                           }

                           if (var4 > 12 && var3 == 4) {
                              throw new ParseException("Increment > 12 : " + var4, var5);
                           }
                        } else {
                           var4 = 1;
                        }

                        this.addToSet(99, -1, var4, var3);
                        return var5;
                     }

                     throw new ParseException("'/' must be followed by an integer.", var5);
                  }
               case '?':
                  var5++;
                  if (var5 + 1 < var2.length() && var2.charAt(var5) != ' ' && var2.charAt(var5 + 1) != '\t') {
                     throw new ParseException("Illegal character after '?': " + var2.charAt(var5), var5);
                  } else if (var3 != 5 && var3 != 3) {
                     throw new ParseException("'?' can only be specfied for Day-of-Month or Day-of-Week.", var5);
                  } else {
                     if (var3 == 5 && !this.lastdayOfMonth) {
                        int var24 = this.daysOfMonth.last();
                        if (var24 == 98) {
                           throw new ParseException("'?' can only be specfied for Day-of-Month -OR- Day-of-Week.", var5);
                        }
                     }

                     this.addToSet(98, -1, 0, var3);
                     return var5;
                  }
               case 'L':
                  var5++;
                  if (var3 == 3) {
                     this.lastdayOfMonth = true;
                  }

                  if (var3 == 5) {
                     this.addToSet(7, 7, 0, var3);
                  }

                  if (var3 == 3 && var2.length() > var5) {
                     var6 = var2.charAt(var5);
                     if (var6 == '-') {
                        CronExpression.ValueSet var23 = this.getValue(0, var2, var5 + 1);
                        this.lastdayOffset = var23.value;
                        if (this.lastdayOffset > 30) {
                           throw new ParseException("Offset from last day must be <= 30", var5 + 1);
                        }

                        var5 = var23.pos;
                     }

                     if (var2.length() > var5) {
                        var6 = var2.charAt(var5);
                        if (var6 == 'W') {
                           this.nearestWeekday = true;
                           var5++;
                        }
                     }
                  }

                  return var5;
               default:
                  if (var6 >= '0' && var6 <= '9') {
                     int var7 = Integer.parseInt(String.valueOf(var6));
                     if (++var5 >= var2.length()) {
                        this.addToSet(var7, -1, -1, var3);
                        return var5;
                     } else {
                        var6 = var2.charAt(var5);
                        if (var6 >= '0' && var6 <= '9') {
                           CronExpression.ValueSet var8 = this.getValue(var7, var2, var5);
                           var7 = var8.value;
                           var5 = var8.pos;
                        }

                        return this.checkNext(var5, var2, var7, var3);
                     }
                  } else {
                     throw new ParseException("Unexpected character: " + var6, var5);
                  }
            }
         }
      }
   }

   protected int checkNext(int var1, String var2, int var3, int var4) throws ParseException {
      int var5 = -1;
      if (var1 >= var2.length()) {
         this.addToSet(var3, var5, -1, var4);
         return var1;
      } else {
         char var7 = var2.charAt(var1);
         if (var7 == 'L') {
            if (var4 == 5) {
               if (var3 >= 1 && var3 <= 7) {
                  this.lastdayOfWeek = true;
                  TreeSet var28 = this.getSet(var4);
                  var28.add(var3);
                  return var1 + 1;
               } else {
                  throw new ParseException("Day-of-Week values must be between 1 and 7", -1);
               }
            } else {
               throw new ParseException("'L' option is not valid here. (pos=" + var1 + ")", var1);
            }
         } else if (var7 == 'W') {
            if (var4 == 3) {
               this.nearestWeekday = true;
               if (var3 > 31) {
                  throw new ParseException("The 'W' option does not make sense with values larger than 31 (max number of days in a month)", var1);
               } else {
                  TreeSet var27 = this.getSet(var4);
                  var27.add(var3);
                  return var1 + 1;
               }
            } else {
               throw new ParseException("'W' option is not valid here. (pos=" + var1 + ")", var1);
            }
         } else {
            switch (var7) {
               case '#':
                  if (var4 != 5) {
                     throw new ParseException("'#' option is not valid here. (pos=" + var1 + ")", var1);
                  } else {
                     int var20 = var1 + 1;

                     try {
                        this.nthdayOfWeek = Integer.parseInt(var2.substring(var20));
                        if (this.nthdayOfWeek < 1 || this.nthdayOfWeek > 5) {
                           throw new Exception();
                        }
                     } catch (Exception var13) {
                        throw new ParseException("A numeric value between 1 and 5 must follow the '#' option", var20);
                     }

                     TreeSet var8 = this.getSet(var4);
                     var8.add(var3);
                     return var20 + 1;
                  }
               case '-':
                  int var16 = var1 + 1;
                  var7 = var2.charAt(var16);
                  int var9 = Integer.parseInt(String.valueOf(var7));
                  var5 = var9;
                  if (++var16 >= var2.length()) {
                     this.addToSet(var3, var9, 1, var4);
                     return var16;
                  } else {
                     var7 = var2.charAt(var16);
                     if (var7 >= '0' && var7 <= '9') {
                        CronExpression.ValueSet var29 = this.getValue(var9, var2, var16);
                        var5 = var29.value;
                        var16 = var29.pos;
                     }

                     if (var16 < var2.length() && var2.charAt(var16) == '/') {
                        var7 = var2.charAt(++var16);
                        int var30 = Integer.parseInt(String.valueOf(var7));
                        if (++var16 >= var2.length()) {
                           this.addToSet(var3, var5, var30, var4);
                           return var16;
                        }

                        var7 = var2.charAt(var16);
                        if (var7 >= '0' && var7 <= '9') {
                           CronExpression.ValueSet var31 = this.getValue(var30, var2, var16);
                           int var32 = var31.value;
                           this.addToSet(var3, var5, var32, var4);
                           var16 = var31.pos;
                        } else {
                           this.addToSet(var3, var5, var30, var4);
                        }

                        return var16;
                     }

                     this.addToSet(var3, var5, 1, var4);
                     return var16;
                  }
               case '/':
                  int var6 = var1 + 1;
                  var7 = var2.charAt(var6);
                  int var10 = Integer.parseInt(String.valueOf(var7));
                  if (++var6 >= var2.length()) {
                     this.addToSet(var3, var5, var10, var4);
                     return var6;
                  } else {
                     var7 = var2.charAt(var6);
                     if (var7 >= '0' && var7 <= '9') {
                        CronExpression.ValueSet var11 = this.getValue(var10, var2, var6);
                        int var12 = var11.value;
                        this.addToSet(var3, var5, var12, var4);
                        return var11.pos;
                     }

                     throw new ParseException("Unexpected character '" + var7 + "' after '/'", var6);
                  }
               default:
                  this.addToSet(var3, var5, 0, var4);
                  return var1 + 1;
            }
         }
      }
   }

   public String getCronExpression() {
      return this.cronExpression;
   }

   public String getExpressionSummary() {
      StringBuilder var1 = new StringBuilder();
      var1.append("seconds: ");
      var1.append(this.getExpressionSetSummary(this.seconds));
      var1.append("\n");
      var1.append("minutes: ");
      var1.append(this.getExpressionSetSummary(this.minutes));
      var1.append("\n");
      var1.append("hours: ");
      var1.append(this.getExpressionSetSummary(this.hours));
      var1.append("\n");
      var1.append("daysOfMonth: ");
      var1.append(this.getExpressionSetSummary(this.daysOfMonth));
      var1.append("\n");
      var1.append("months: ");
      var1.append(this.getExpressionSetSummary(this.months));
      var1.append("\n");
      var1.append("daysOfWeek: ");
      var1.append(this.getExpressionSetSummary(this.daysOfWeek));
      var1.append("\n");
      var1.append("lastdayOfWeek: ");
      var1.append(this.lastdayOfWeek);
      var1.append("\n");
      var1.append("nearestWeekday: ");
      var1.append(this.nearestWeekday);
      var1.append("\n");
      var1.append("NthDayOfWeek: ");
      var1.append(this.nthdayOfWeek);
      var1.append("\n");
      var1.append("lastdayOfMonth: ");
      var1.append(this.lastdayOfMonth);
      var1.append("\n");
      var1.append("years: ");
      var1.append(this.getExpressionSetSummary(this.years));
      var1.append("\n");
      return var1.toString();
   }

   protected String getExpressionSetSummary(Set<Integer> var1) {
      if (var1.contains(NO_SPEC)) {
         return "?";
      } else if (var1.contains(ALL_SPEC)) {
         return "*";
      } else {
         StringBuilder var2 = new StringBuilder();
         Iterator var3 = var1.iterator();

         for (boolean var4 = true; var3.hasNext(); var4 = false) {
            Integer var5 = (Integer)var3.next();
            String var6 = var5.toString();
            if (!var4) {
               var2.append(",");
            }

            var2.append(var6);
         }

         return var2.toString();
      }
   }

   protected String getExpressionSetSummary(ArrayList<Integer> var1) {
      if (var1.contains(NO_SPEC)) {
         return "?";
      } else if (var1.contains(ALL_SPEC)) {
         return "*";
      } else {
         StringBuilder var2 = new StringBuilder();
         Iterator var3 = var1.iterator();

         for (boolean var4 = true; var3.hasNext(); var4 = false) {
            Integer var5 = (Integer)var3.next();
            String var6 = var5.toString();
            if (!var4) {
               var2.append(",");
            }

            var2.append(var6);
         }

         return var2.toString();
      }
   }

   protected int skipWhiteSpace(int var1, String var2) {
      while (var1 < var2.length() && (var2.charAt(var1) == ' ' || var2.charAt(var1) == '\t')) {
         var1++;
      }

      return var1;
   }

   protected int findNextWhiteSpace(int var1, String var2) {
      while (var1 < var2.length() && (var2.charAt(var1) != ' ' || var2.charAt(var1) != '\t')) {
         var1++;
      }

      return var1;
   }

   protected void addToSet(int var1, int var2, int var3, int var4) throws ParseException {
      TreeSet var5 = this.getSet(var4);
      switch (var4) {
         case 0:
         case 1:
            if ((var1 < 0 || var1 > 59 || var2 > 59) && var1 != 99) {
               throw new ParseException("Minute and Second values must be between 0 and 59", -1);
            }
            break;
         case 2:
            if ((var1 < 0 || var1 > 23 || var2 > 23) && var1 != 99) {
               throw new ParseException("Hour values must be between 0 and 23", -1);
            }
            break;
         case 3:
            if ((var1 < 1 || var1 > 31 || var2 > 31) && var1 != 99 && var1 != 98) {
               throw new ParseException("Day of month values must be between 1 and 31", -1);
            }
            break;
         case 4:
            if ((var1 < 1 || var1 > 12 || var2 > 12) && var1 != 99) {
               throw new ParseException("Month values must be between 1 and 12", -1);
            }
            break;
         case 5:
            if ((var1 == 0 || var1 > 7 || var2 > 7) && var1 != 99 && var1 != 98) {
               throw new ParseException("Day-of-Week values must be between 1 and 7", -1);
            }
      }

      if ((var3 == 0 || var3 == -1) && var1 != 99) {
         if (var1 != -1) {
            var5.add(var1);
         } else {
            var5.add(NO_SPEC);
         }
      } else {
         int var6 = var1;
         int var7 = var2;
         if (var1 == 99 && var3 <= 0) {
            var3 = 1;
            var5.add(ALL_SPEC);
         }

         switch (var4) {
            case 0:
            case 1:
               if (var2 == -1) {
                  var7 = 59;
               }

               if (var1 == -1 || var1 == 99) {
                  var6 = 0;
               }
               break;
            case 2:
               if (var2 == -1) {
                  var7 = 23;
               }

               if (var1 == -1 || var1 == 99) {
                  var6 = 0;
               }
               break;
            case 3:
               if (var2 == -1) {
                  var7 = 31;
               }

               if (var1 == -1 || var1 == 99) {
                  var6 = 1;
               }
               break;
            case 4:
               if (var2 == -1) {
                  var7 = 12;
               }

               if (var1 == -1 || var1 == 99) {
                  var6 = 1;
               }
               break;
            case 5:
               if (var2 == -1) {
                  var7 = 7;
               }

               if (var1 == -1 || var1 == 99) {
                  var6 = 1;
               }
               break;
            case 6:
               if (var2 == -1) {
                  var7 = MAX_YEAR;
               }

               if (var1 == -1 || var1 == 99) {
                  var6 = 1970;
               }
         }

         byte var8 = -1;
         if (var7 < var6) {
            switch (var4) {
               case 0:
                  var8 = 60;
                  break;
               case 1:
                  var8 = 60;
                  break;
               case 2:
                  var8 = 24;
                  break;
               case 3:
                  var8 = 31;
                  break;
               case 4:
                  var8 = 12;
                  break;
               case 5:
                  var8 = 7;
                  break;
               case 6:
                  throw new IllegalArgumentException("Start year must be less than stop year");
               default:
                  throw new IllegalArgumentException("Unexpected type encountered");
            }

            var7 += var8;
         }

         for (int var9 = var6; var9 <= var7; var9 += var3) {
            if (var8 == -1) {
               var5.add(var9);
            } else {
               int var10 = var9 % var8;
               if (var10 == 0 && (var4 == 4 || var4 == 5 || var4 == 3)) {
                  var10 = var8;
               }

               var5.add(var10);
            }
         }
      }
   }

   TreeSet<Integer> getSet(int var1) {
      switch (var1) {
         case 0:
            return this.seconds;
         case 1:
            return this.minutes;
         case 2:
            return this.hours;
         case 3:
            return this.daysOfMonth;
         case 4:
            return this.months;
         case 5:
            return this.daysOfWeek;
         case 6:
            return this.years;
         default:
            return null;
      }
   }

   protected CronExpression.ValueSet getValue(int var1, String var2, int var3) {
      char var4 = var2.charAt(var3);

      StringBuilder var5;
      for (var5 = new StringBuilder(String.valueOf(var1)); var4 >= '0' && var4 <= '9'; var4 = var2.charAt(var3)) {
         var5.append(var4);
         if (++var3 >= var2.length()) {
            break;
         }
      }

      CronExpression.ValueSet var6 = new CronExpression.ValueSet();
      var6.pos = var3 < var2.length() ? var3 : var3 + 1;
      var6.value = Integer.parseInt(var5.toString());
      return var6;
   }

   protected int getNumericValue(String var1, int var2) {
      int var3 = this.findNextWhiteSpace(var2, var1);
      String var4 = var1.substring(var2, var3);
      return Integer.parseInt(var4);
   }

   protected int getMonthNumber(String var1) {
      Integer var2 = monthMap.get(var1);
      return var2 == null ? -1 : var2;
   }

   protected int getDayOfWeekNumber(String var1) {
      Integer var2 = dayMap.get(var1);
      return var2 == null ? -1 : var2;
   }

   public Date getTimeAfter(Date var1) {
      GregorianCalendar var2 = new GregorianCalendar(this.getTimeZone());
      var1 = new Date(var1.getTime() + 1000L);
      var2.setTime(var1);
      var2.set(14, 0);
      boolean var3 = false;

      while (!var3) {
         if (var2.get(1) > 2999) {
            return null;
         }

         int var4 = var2.get(13);
         int var5 = var2.get(12);
         SortedSet var6 = this.seconds.tailSet(var4);
         if (var6 != null && var6.size() != 0) {
            var4 = (Integer)var6.first();
         } else {
            var4 = this.seconds.first();
            var2.set(12, ++var5);
         }

         var2.set(13, var4);
         var5 = var2.get(12);
         int var7 = var2.get(11);
         int var8 = -1;
         var6 = this.minutes.tailSet(var5);
         if (var6 != null && var6.size() != 0) {
            var8 = var5;
            var5 = (Integer)var6.first();
         } else {
            var5 = this.minutes.first();
            var7++;
         }

         if (var5 == var8) {
            var2.set(12, var5);
            var7 = var2.get(11);
            int var9 = var2.get(5);
            var8 = -1;
            var6 = this.hours.tailSet(var7);
            if (var6 != null && var6.size() != 0) {
               var8 = var7;
               var7 = (Integer)var6.first();
            } else {
               var7 = this.hours.first();
               var9++;
            }

            if (var7 == var8) {
               var2.set(11, var7);
               var9 = var2.get(5);
               int var10 = var2.get(2) + 1;
               var8 = -1;
               int var11 = var10;
               boolean var12 = !this.daysOfMonth.contains(NO_SPEC);
               boolean var13 = !this.daysOfWeek.contains(NO_SPEC);
               if (var12 && !var13) {
                  var6 = this.daysOfMonth.tailSet(var9);
                  if (this.lastdayOfMonth) {
                     if (!this.nearestWeekday) {
                        var8 = var9;
                        var9 = this.getLastDayOfMonth(var10, var2.get(1));
                        var9 -= this.lastdayOffset;
                        if (var8 > var9) {
                           if (++var10 > 12) {
                              var10 = 1;
                              var11 = 3333;
                              var2.add(1, 1);
                           }

                           var9 = 1;
                        }
                     } else {
                        var8 = var9;
                        int var40 = this.getLastDayOfMonth(var10, var2.get(1));
                        var9 = var40 - this.lastdayOffset;
                        Calendar var45 = Calendar.getInstance(this.getTimeZone());
                        var45.set(13, 0);
                        var45.set(12, 0);
                        var45.set(11, 0);
                        var45.set(5, var9);
                        var45.set(2, var10 - 1);
                        var45.set(1, var2.get(1));
                        int var53 = this.getLastDayOfMonth(var10, var2.get(1));
                        int var58 = var45.get(7);
                        if (var58 == 7 && var9 == 1) {
                           var9 += 2;
                        } else if (var58 == 7) {
                           var9--;
                        } else if (var58 == 1 && var9 == var53) {
                           var9 -= 2;
                        } else if (var58 == 1) {
                           var9++;
                        }

                        var45.set(13, var4);
                        var45.set(12, var5);
                        var45.set(11, var7);
                        var45.set(5, var9);
                        var45.set(2, var10 - 1);
                        Date var62 = var45.getTime();
                        if (var62.before(var1)) {
                           var9 = 1;
                           var10++;
                        }
                     }
                  } else if (this.nearestWeekday) {
                     var8 = var9;
                     var9 = this.daysOfMonth.first();
                     Calendar var46 = Calendar.getInstance(this.getTimeZone());
                     var46.set(13, 0);
                     var46.set(12, 0);
                     var46.set(11, 0);
                     var46.set(5, var9);
                     var46.set(2, var10 - 1);
                     var46.set(1, var2.get(1));
                     int var54 = this.getLastDayOfMonth(var10, var2.get(1));
                     int var59 = var46.get(7);
                     if (var59 == 7 && var9 == 1) {
                        var9 += 2;
                     } else if (var59 == 7) {
                        var9--;
                     } else if (var59 == 1 && var9 == var54) {
                        var9 -= 2;
                     } else if (var59 == 1) {
                        var9++;
                     }

                     var46.set(13, var4);
                     var46.set(12, var5);
                     var46.set(11, var7);
                     var46.set(5, var9);
                     var46.set(2, var10 - 1);
                     Date var63 = var46.getTime();
                     if (var63.before(var1)) {
                        var9 = this.daysOfMonth.first();
                        var10++;
                     }
                  } else if (var6 != null && var6.size() != 0) {
                     var8 = var9;
                     var9 = (Integer)var6.first();
                     int var47 = this.getLastDayOfMonth(var10, var2.get(1));
                     if (var9 > var47) {
                        var9 = this.daysOfMonth.first();
                        var10++;
                     }
                  } else {
                     var9 = this.daysOfMonth.first();
                     var10++;
                  }

                  if (var9 != var8 || var10 != var11) {
                     var2.set(13, 0);
                     var2.set(12, 0);
                     var2.set(11, 0);
                     var2.set(5, var9);
                     var2.set(2, var10 - 1);
                     continue;
                  }
               } else {
                  if (!var13 || var12) {
                     throw new UnsupportedOperationException("Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.");
                  }

                  if (this.lastdayOfWeek) {
                     int var14 = this.daysOfWeek.first();
                     int var15 = var2.get(7);
                     int var16 = 0;
                     if (var15 < var14) {
                        var16 = var14 - var15;
                     }

                     if (var15 > var14) {
                        var16 = var14 + (7 - var15);
                     }

                     int var17 = this.getLastDayOfMonth(var10, var2.get(1));
                     if (var9 + var16 > var17) {
                        var2.set(13, 0);
                        var2.set(12, 0);
                        var2.set(11, 0);
                        var2.set(5, 1);
                        var2.set(2, var10);
                        continue;
                     }

                     while (var9 + var16 + 7 <= var17) {
                        var16 += 7;
                     }

                     var9 += var16;
                     if (var16 > 0) {
                        var2.set(13, 0);
                        var2.set(12, 0);
                        var2.set(11, 0);
                        var2.set(5, var9);
                        var2.set(2, var10 - 1);
                        continue;
                     }
                  } else if (this.nthdayOfWeek != 0) {
                     int var43 = this.daysOfWeek.first();
                     int var51 = var2.get(7);
                     int var55 = 0;
                     if (var51 < var43) {
                        var55 = var43 - var51;
                     } else if (var51 > var43) {
                        var55 = var43 + (7 - var51);
                     }

                     boolean var60 = false;
                     if (var55 > 0) {
                        var60 = true;
                     }

                     var9 += var55;
                     int var18 = var9 / 7;
                     if (var9 % 7 > 0) {
                        var18++;
                     }

                     var55 = (this.nthdayOfWeek - var18) * 7;
                     var9 += var55;
                     if (var55 < 0 || var9 > this.getLastDayOfMonth(var10, var2.get(1))) {
                        var2.set(13, 0);
                        var2.set(12, 0);
                        var2.set(11, 0);
                        var2.set(5, 1);
                        var2.set(2, var10);
                        continue;
                     }

                     if (var55 > 0 || var60) {
                        var2.set(13, 0);
                        var2.set(12, 0);
                        var2.set(11, 0);
                        var2.set(5, var9);
                        var2.set(2, var10 - 1);
                        continue;
                     }
                  } else {
                     int var44 = var2.get(7);
                     int var52 = this.daysOfWeek.first();
                     var6 = this.daysOfWeek.tailSet(var44);
                     if (var6 != null && var6.size() > 0) {
                        var52 = (Integer)var6.first();
                     }

                     int var57 = 0;
                     if (var44 < var52) {
                        var57 = var52 - var44;
                     }

                     if (var44 > var52) {
                        var57 = var52 + (7 - var44);
                     }

                     int var61 = this.getLastDayOfMonth(var10, var2.get(1));
                     if (var9 + var57 > var61) {
                        var2.set(13, 0);
                        var2.set(12, 0);
                        var2.set(11, 0);
                        var2.set(5, 1);
                        var2.set(2, var10);
                        continue;
                     }

                     if (var57 > 0) {
                        var2.set(13, 0);
                        var2.set(12, 0);
                        var2.set(11, 0);
                        var2.set(5, var9 + var57);
                        var2.set(2, var10 - 1);
                        continue;
                     }
                  }
               }

               var2.set(5, var9);
               var10 = var2.get(2) + 1;
               int var48 = var2.get(1);
               var8 = -1;
               if (var48 > MAX_YEAR) {
                  return null;
               }

               var6 = this.months.tailSet(var10);
               if (var6 != null && var6.size() != 0) {
                  var8 = var10;
                  var10 = (Integer)var6.first();
               } else {
                  var10 = this.months.first();
                  var48++;
               }

               if (var10 != var8) {
                  var2.set(13, 0);
                  var2.set(12, 0);
                  var2.set(11, 0);
                  var2.set(5, 1);
                  var2.set(2, var10 - 1);
                  var2.set(1, var48);
               } else {
                  var2.set(2, var10 - 1);
                  var48 = var2.get(1);
                  byte var35 = -1;
                  var6 = this.years.tailSet(var48);
                  if (var6 == null || var6.size() == 0) {
                     return null;
                  }

                  var48 = (Integer)var6.first();
                  if (var48 != var48) {
                     var2.set(13, 0);
                     var2.set(12, 0);
                     var2.set(11, 0);
                     var2.set(5, 1);
                     var2.set(2, 0);
                     var2.set(1, var48);
                  } else {
                     var2.set(1, var48);
                     var3 = true;
                  }
               }
            } else {
               var2.set(13, 0);
               var2.set(12, 0);
               var2.set(5, var9);
               this.setCalendarHour(var2, var7);
            }
         } else {
            var2.set(13, 0);
            var2.set(12, var5);
            this.setCalendarHour(var2, var7);
         }
      }

      return var2.getTime();
   }

   protected void setCalendarHour(Calendar var1, int var2) {
      var1.set(11, var2);
      if (var1.get(11) != var2 && var2 != 24) {
         var1.set(11, var2 + 1);
      }
   }

   protected Date getTimeBefore(Date var1) {
      Calendar var2 = Calendar.getInstance(this.getTimeZone());
      var2.setTime(var1);
      var2.set(14, 0);
      Date var3 = var2.getTime();
      Date var4 = var3;
      long var5 = this.findMinIncrement();

      Date var7;
      do {
         Date var8 = new Date(var4.getTime() - var5);
         var7 = this.getTimeAfter(var8);
         if (var7 == null || var7.before(MIN_DATE)) {
            return null;
         }

         var4 = var8;
      } while (var7.compareTo(var3) >= 0);

      return var7;
   }

   public Date getPrevFireTime(Date var1) {
      return this.getTimeBefore(var1);
   }

   private long findMinIncrement() {
      if (this.seconds.size() != 1) {
         return (long)(this.minInSet(this.seconds) * 1000);
      } else if (this.seconds.first() == 99) {
         return 1000L;
      } else if (this.minutes.size() != 1) {
         return (long)(this.minInSet(this.minutes) * 60000);
      } else if (this.minutes.first() == 99) {
         return 60000L;
      } else if (this.hours.size() != 1) {
         return (long)(this.minInSet(this.hours) * 3600000);
      } else {
         return this.hours.first() == 99 ? 3600000L : 86400000L;
      }
   }

   private int minInSet(TreeSet<Integer> var1) {
      int var2 = 0;
      int var3 = Integer.MAX_VALUE;
      boolean var4 = true;

      for (int var6 : var1) {
         if (var4) {
            var2 = var6;
            var4 = false;
         } else {
            int var7 = var6 - var2;
            if (var7 < var3) {
               var3 = var7;
            }
         }
      }

      return var3;
   }

   public Date getFinalFireTime() {
      return null;
   }

   protected boolean isLeapYear(int var1) {
      return var1 % 4 == 0 && var1 % 100 != 0 || var1 % 400 == 0;
   }

   protected int getLastDayOfMonth(int var1, int var2) {
      switch (var1) {
         case 1:
            return 31;
         case 2:
            return this.isLeapYear(var2) ? 29 : 28;
         case 3:
            return 31;
         case 4:
            return 30;
         case 5:
            return 31;
         case 6:
            return 30;
         case 7:
            return 31;
         case 8:
            return 31;
         case 9:
            return 30;
         case 10:
            return 31;
         case 11:
            return 30;
         case 12:
            return 31;
         default:
            throw new IllegalArgumentException("Illegal month number: " + var1);
      }
   }

   static {
      monthMap.put("JAN", 0);
      monthMap.put("FEB", 1);
      monthMap.put("MAR", 2);
      monthMap.put("APR", 3);
      monthMap.put("MAY", 4);
      monthMap.put("JUN", 5);
      monthMap.put("JUL", 6);
      monthMap.put("AUG", 7);
      monthMap.put("SEP", 8);
      monthMap.put("OCT", 9);
      monthMap.put("NOV", 10);
      monthMap.put("DEC", 11);
      dayMap.put("SUN", 1);
      dayMap.put("MON", 2);
      dayMap.put("TUE", 3);
      dayMap.put("WED", 4);
      dayMap.put("THU", 5);
      dayMap.put("FRI", 6);
      dayMap.put("SAT", 7);
      MIN_CAL.set(1970, 0, 1);
   }

   private class ValueSet {
      public int value;
      public int pos;

      private ValueSet() {
      }
   }
}
