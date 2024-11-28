/*
    Copyright (C) 2024 Nicola L.C. Talbot
    www.dickimaw-books.com

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package com.dickimawbooks.texparserlib.latex.datatool;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dickimawbooks.texparserlib.*;

/**
 * Approximate datatool-base.sty temporal data.
 */
public class Julian
{
   public static Julian create(String iso)
    throws IllegalArgumentException
   {
      Julian julian = null;

      Matcher matcher = DATE_PATTERN.matcher(iso);
      int offset = 0;

      if (matcher.find() && matcher.start() == 0)
      {
         julian = new Julian();

         julian.hasDate = true;

         julian.year = Integer.parseInt(matcher.group(1));
         julian.month = Integer.parseInt(matcher.group(2));
         julian.day = Integer.parseInt(matcher.group(3));

         offset = matcher.end();

         if (offset + 1 < iso.length())
         {
            char c = iso.charAt(offset);

            if (c == 'T' || c == ' ')
            {
               offset++;
            }
         }

         julian.calcJulianDate();
      }

      matcher = TIME_PATTERN.matcher(iso);

      if (matcher.find(offset))
      {
         int start = matcher.start();

         if (start != offset)
         {
            throw new IllegalArgumentException(
               String.format("Invalid argument (time) '%s'", iso));
         }

         if (julian == null)
         {
            julian = new Julian();
         }

         julian.hasTime = true;

         julian.hour = Integer.parseInt(matcher.group(1));
         julian.minute = Integer.parseInt(matcher.group(2));

         String secStr = matcher.group(3);

         if (secStr != null && !secStr.isEmpty())
         {
            julian.second = Integer.parseInt(secStr);
         }

         offset = matcher.end();

         if (julian.hasDate)
         {
            String tail = iso.substring(matcher.end());

            if (!tail.isEmpty())
            {
               if (tail.equals("Z"))
               {
                  julian.hasTimeZone = true;
               }
               else
               {
                  matcher = TIMEZONE_PATTERN.matcher(tail);

                  if (matcher.matches())
                  {
                     julian.hasTimeZone = true;

                     julian.tzh = Integer.parseInt(matcher.group(1));
                     julian.tzm = Integer.parseInt(matcher.group(2));
                  }
                  else
                  {
                    throw new IllegalArgumentException(
                      String.format("Invalid argument (time zone) '%s'", iso));
                  }
               }
            }
         }
         else if (offset != iso.length())
         {
            throw new IllegalArgumentException(
               String.format("Invalid argument (time) '%s'", iso));
         }
      }

      if (julian == null)
      {
         throw new IllegalArgumentException(
            String.format("Invalid date/time argument '%s'", iso));
      }

      if (julian.hasDate)
      {
         if (julian.hasTime)
         {
            julian.calcJulianDate();

            if (julian.hasTimeZone)
            {
               julian.timestamp = String.format((Locale)null,
                 "%d-%02d-%02dT%02d:%02d:%02d%s:%02d",
                 julian.localYear, julian.localMonth, julian.localDay,
                 julian.localHour, julian.localMinute, julian.localSecond,
                 signedTwoDigits(julian.tzh), julian.tzm);
            }
            else
            {
               julian.timestamp = String.format((Locale)null,
                 "%d-%02d-%02dT%02d:%02d:%02d",
                 julian.year, julian.month, julian.day,
                 julian.hour, julian.minute, julian.second);
            }
         }
         else
         {
            julian.calcJulianDay();

            julian.timestamp = iso;
         }
      }
      else
      {
         julian.calcJulianTime();

         julian.timestamp = iso;
      }

      return julian;
   }

   public static String signedTwoDigits(int num)
   {
      return num < 0 ? String.format((Locale)null, "%02d", num) :
        String.format((Locale)null, "+%02d", num);
   }

   public DatumElement toDatumElement(TeXParserListener listener,
      TeXObject original, boolean reformatOriginal)
   {
      TeXObject strVal = original;

      TeXNumber texNum;

      if (hasDate)
      {
         if (hasTime)
         {
            texNum = new TeXFloatingPoint(julianDate);
         }
         else
         {
            texNum = new UserNumber(julianDay);
         }
      }
      else
      {
         texNum = new TeXFloatingPoint(julianTime);
      }

      TeXObjectList valueList = listener.createStack();
      valueList.add(new TeXCsRef("DTLtemporalvalue"));
      valueList.add(TeXParserUtils.createGroup(listener,
        texNum));
      valueList.add(listener.createGroup(timestamp));

      if (reformatOriginal)
      {
         TeXObjectList list = listener.createStack();

         if (hasDate)
         {
            if (hasTime)
            {
               list.add(new TeXCsRef("DataToolTimeStampFmt"));

               Group grp = listener.createGroup();
               list.add(grp);

               grp.add(TeXParserUtils.createGroup(listener,
                 new UserNumber(localYear)));
               grp.add(listener.createGroup(String.format("%02d", localMonth)));
               grp.add(listener.createGroup(String.format("%02d", localDay)));
               grp.add(TeXParserUtils.createGroup(listener,
                 new UserNumber(localDow)));

               grp = listener.createGroup();
               list.add(grp);

               grp.add(listener.createGroup(String.format("%02d", localHour)));
               grp.add(listener.createGroup(String.format("%02d", localMinute)));
               grp.add(listener.createGroup(String.format("%02d", localSecond)));

               grp = listener.createGroup();
               list.add(grp);

               grp.add(listener.createGroup(signedTwoDigits(tzh)));
               grp.add(listener.createGroup(String.format("%02d", tzm)));
            }
            else
            {
               list.add(new TeXCsRef("DataToolDateFmt"));

               list.add(TeXParserUtils.createGroup(listener,
                 new UserNumber(localYear)));
               list.add(listener.createGroup(String.format("%02d", localMonth)));
               list.add(listener.createGroup(String.format("%02d", localDay)));
               list.add(TeXParserUtils.createGroup(listener,
                 new UserNumber(localDow)));

            }
         }
         else
         {
            list.add(new TeXCsRef("DataToolTimeFmt"));

            list.add(listener.createGroup(String.format("%02d", localHour)));
            list.add(listener.createGroup(String.format("%02d", localMinute)));
            list.add(listener.createGroup(String.format("%02d", localSecond)));
         }

         strVal = list;
      }

      return new DatumElement(strVal, 
        texNum, valueList, null, getDatumType());
   }

   public DataElement toDataElement(TeXParserListener listener,
      TeXObject original, boolean useDatum,
     boolean reformatOriginal)
   {
      if (useDatum)
      {
         return toDatumElement(listener, original, reformatOriginal);
      }
      else if (hasDate)
      {
         if (hasTime)
         {
            return new DataDateTimeElement(julianDate, original);
         }
         else
         {
            return new DataDateElement(julianDay, original);
         }
      }
      else
      {
         return new DataTimeElement(julianTime, original);
      }
   }

   public String getTimeStamp()
   {
      return timestamp;
   }

   public boolean hasDate()
   {
      return hasDate;
   }

   public boolean hasTime()
   {
      return hasTime;
   }

   public boolean hasTimeZone()
   {
      return hasTimeZone;
   }

   public int getJulianDay()
   {
      return julianDay;
   }

   public double getJulianTime()
   {
      return julianTime;
   }

   public double getJulianDate()
   {
      return julianDate;
   }

   public DatumType getDatumType()
   {
      if (hasDate && hasTime)
      {
         return DatumType.DATETIME;
      }
      else if (hasDate)
      {
         return DatumType.TIME;
      }

      return DatumType.TIME;
   }

   public int getYear(boolean local)
   {
      return local ? localYear : year;
   }

   public int getMonth(boolean local)
   {
      return local ? localMonth : month;
   }

   public int getDayOfMonth(boolean local)
   {
      return local ? localDay : day;
   }

   public int getDayOfWeek(boolean local)
   {
      return local ? localDow : dow;
   }

   public int getHour(boolean local)
   {
      return local ? localHour : hour;
   }

   public int getMinute(boolean local)
   {
      return local ? localMinute : minute;
   }

   public int getSecond(boolean local)
   {
      return local ? localSecond : second;
   }

   public int getTimeZoneHour()
   {
      return tzh;
   }

   public int getTimeZoneMinute()
   {
      return tzm;
   }

   protected void setLocals()
   {
      localYear = year;
      localMonth = month;
      localDay = day;
      localHour = hour;
      localMinute = minute;
      localSecond = second;
   }

   protected void calcJulianDate()
   {
      calcJulianDay();

      // implement time zone shift

      if (tzm != 0)
      {
         minute -= tzm;

         if (minute < 0)
         {
            minute += 60;
            hour--;
         }
      }

      if (tzh != 0)
      {
         hour -= tzh;
      }

      if (hour < 0)
      {
         hour += 23;
         julianDay--;
      }

      calcJulianTime();

      julianDate = julianDay + julianTime;

      dow = julianDay % 7;
   }

   protected void calcJulianDay()
   {
      setLocals();

      julianDay = day - 32075
       + 1461 * (year + 4800 + (month - 14 ) / 12 ) / 4
       + 367 * (month - 2 - (month - 14) / 12 * 12) / 12
       - 3 * ((year + 4900 + (month - 14) / 12) / 100) / 4;

      localDow = julianDay % 7;
      dow = localDow;
   }

   protected void calcJulianTime()
   {
      julianTime = ( (double)hour - 12.0 ) / 24.0
                 + (double)minute / 1440.0
                 + (double)second / 86400.0;
   }

   @Override
   public String toString()
   {
      return String.format("%s[timestamp=%s,hasDate=%s,hasTime=%s,hasTimeZone=%s,year=%d,month=%d,day=%d,dow=%d,hour=%d,minute=%d,second=%d,localYear=%d,localMonth=%d,localDay=%d,localDow=%d,localHour=%d,localMinute=%d,localSecond=%d,tzh=%d,tzm=%d,jdn=%d,jt=%f,jdt=%f]",
       getClass().getSimpleName(), timestamp, hasDate, hasTime, hasTimeZone,
        year, month, day, dow, hour, minute, second,
        localYear, localMonth, localDay, localDow, localHour, localMinute, localSecond,
        tzh, tzm, julianDay, julianTime, julianDate
       );
   }

   String timestamp;
   boolean hasDate=false, hasTime=false, hasTimeZone=false;
   int year=-4713, month=1, day=1, dow=1, hour=12, minute=0, second=0, 
    julianDay=0;
   double julianTime=0.0, julianDate=0.0;

   int localYear=-4713, localMonth=1, localDay=1, localDow=1, 
    localHour=12, localMinute=0, localSecond=0, tzh=0, tzm=0; 

   static final Pattern DATE_PATTERN
     = Pattern.compile("([+\\-]?\\d+)-(\\d{2})-(\\d{2})");

   static final Pattern TIME_PATTERN
     = Pattern.compile("(\\d{2}):(\\d{2})(?:\\:(\\d{2}))?");

   static final Pattern TIMEZONE_PATTERN
     = Pattern.compile("([+\\-]\\d{2}):?(\\d{2})");

}
