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

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dickimawbooks.texparserlib.*;

/**
 * Approximate datatool-base.sty temporal data.
 * Julian date calculations from https://aa.usno.navy.mil/faq/JD_formula
 */
public class Julian
{
   /**
    * Creates a new instance for the given time. The value 0.0
    * indicates midday. The value -0.5 indicates 00:00:00 and
    * the value 0.5 indicates 24:00:00.
    * @param jt the Julian time (-0.5 &lt;= jt &lt;= 0.5)
    */
   public static Julian createTime(double jt)
   {
      Julian julian = new Julian();

      julian.julianTime = jt;
      julian.hasTime = true;

      double dayfrac = 0.5 + jt;

      julian.hour = (int)(dayfrac * 24);

      julian.minute = (int)(dayfrac*1440) % 60;

      julian.second = (int)(dayfrac * 86400) % 60;

      julian.timestamp = String.format((Locale)null,
        "%02d:%02d:%02d", julian.hour, julian.minute, julian.second);

      julian.setLocals();

      return julian;
   }

   /**
    * Creates a new instance for the given day (no time).
    * @param jd the Julian day
    */
   public static Julian createDay(int jd)
   {
      Julian julian = new Julian();

      julian.julianDay = jd;
      julian.julianDate = (double)jd;

      julian.hasDate = true;

      julian.updateYMD(false, jd);

      julian.timestamp = String.format((Locale)null,
        "%d-%02d-%02d", julian.year, julian.month, julian.day);

      julian.setLocals();

      return julian;
   }

   /**
    * Creates a new instance for the given UTC datetime.
    * @param jdt the Julian date
    */
   public static Julian createDate(double jdt)
   {
      Julian julian = new Julian();

      julian.julianDate = jdt;
      julian.julianDay = (int)Math.round(jdt);
      julian.julianTime = jdt - julian.julianDay;

      julian.hasTime = true;
      julian.hasDate = true;

      julian.updateYMD(false, julian.julianDay);

      double dayfrac = 0.5 + julian.julianTime;

      julian.hour = (int)(dayfrac * 24);

      julian.minute = (int)(dayfrac*1440) % 60;

      julian.second = (int)(dayfrac * 86400) % 60;

      julian.timestamp = String.format((Locale)null,
        "%d-%02d-%02dT%02d:%02d:%02d", 
        julian.year, julian.month, julian.day,
        julian.hour, julian.minute, julian.second);

      julian.setLocals();

      return julian;
   }

   /**
    * Creates a new instance for the given UTC datetime with local
    * time set according to the given time zone offsets.
    * @param jdt the Julian date
    */
   public static Julian createDate(double jdt, int timeZoneHr, int timeZoneMin)
   {
      Julian julian = createDate(jdt);

      julian.adjustTimeZone(timeZoneHr, timeZoneMin);

      julian.timestamp += String.format((Locale)null, "%s:%02d",
        signedTwoDigits(julian.tzh), julian.tzm);

      return julian;
   }

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

   public Calendar toCalendar()
   {
      Calendar.Builder builder = new Calendar.Builder();

      builder.setCalendarType("iso8601");

      if (hasDate)
      {
         builder.setDate(localYear, localMonth, localDay);
      }

      if (hasTime)
      {
         builder.setTimeOfDay(localHour, localMinute, localSecond);
      }

      if (hasTimeZone)
      {
         String tzId;

         if (tzm == 0)
         {
            tzId = String.format("GMT%+2d", tzh);
         }
         else
         {
            tzId = String.format("GMT%s%02d", signedTwoDigits(tzh), tzm);
         }

         builder.setTimeZone(TimeZone.getTimeZone(tzId));
      }

      return builder.build();
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

   public long toUnixEpoch()
   {
      return DataToolBaseSty.unixEpochFromJulianDate(julianDate);
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

   /**
    * Sets the local time according to the given time zone relative
    * to the stored UTC information.
    */
   public void adjustTimeZone(int newTimeZoneHr, int newTimeZoneMin)
   {
      setLocals();

      int localJD = julianDay;

      if (newTimeZoneMin != 0)
      {
         localMinute += newTimeZoneMin;

         if (localMinute < 0)
         {
            localMinute += 60;
            localHour--;
         }
         else if (localMinute >= 60)
         {
            localMinute -= 60;
            localHour++;
         }
      }

      if (newTimeZoneHr != 0)
      {
         localHour += newTimeZoneHr;
      }

      if (localHour < 0)
      {
         localHour += 23;
         localDow--;
         localJD--;

         if (localDow < 0)
         {
            localDow += 7;
         }
      }
      else if (localHour >= 24)
      {
         localHour -= 24;
         localDow = (localDow+1)%7;
         localJD++;
      }

      if (localJD != julianDay)
      {
         updateYMD(true, localJD);
      }

      tzh = newTimeZoneHr;
      tzm = newTimeZoneMin;
   }

   public void adjustTimeZone(TimeZone timeZone)
   {
      int offset = timeZone.getOffset(toUnixEpoch());

      int offsetHr = offset / 3600000;
      int offsetMin = (((int)Math.abs(offset)) / 60000)
                    - (int)Math.abs(offsetHr * 60);

      adjustTimeZone(offsetHr, offsetMin);
   }

   protected void setLocals()
   {
      localYear = year;
      localMonth = month;
      localDay = day;
      localHour = hour;
      localMinute = minute;
      localSecond = second;
      localDow = dow;
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
         else if (minute >= 60)
         {
            minute -= 60;
            hour++;
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
      else if (hour >= 24)
      {
         hour -= 24;
         julianDay++;
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

   protected void updateYMD(boolean local, int jd)
   {
      int yr, mth, dy, l, n;

      l = jd + 68569;
      n = 4 * l / 146097;
      l = l - (146097 * n + 3) / 4;
      yr = 4000 * (l + 1) / 1461001;
      l = l - 1461 * yr/4 + 31;
      mth = 80 * l / 2447;
      dy = l - 2447 * mth / 80;
      l = mth / 11;
      mth = mth + 2 - 12 * l;
      yr = 100 * (n - 49) + yr + l;

      if (local)
      {
         localYear = yr;
         localMonth = mth;
         localDay = dy;
      }
      else
      {
         year = yr;
         month = mth;
         day = dy;
      }
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
