/*
    Copyright (C) 2013 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.tcilatex;

import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.*;

public class SWSpecialListener implements SpecialListener
{
   public SWSpecialListener()
   {
   }

   public TeXObjectList process(TeXParser parser, String param)
     throws IOException
   {
      if (!param.contains("language \"Scientific Word\""))
      {
         return null;
      }

      String[] split = param.split(";");

      KeyValList keyValList = new KeyValList();

      String fileName = null;

      for (int i = 0; i < split.length; i++)
      {
         Matcher m = HEIGHT_PATTERN.matcher(split[i]);

         if (m.matches())
         {
            try
            {
               float num = Float.parseFloat(m.group(1));
               TeXUnit unit = parser.getListener().createUnit(m.group(2));

               keyValList.put("height", new UserDimension(num, unit));
            }
            catch (NumberFormatException e)
            {
               // This shouldn't happen
            }

            continue;
         }

         m = WIDTH_PATTERN.matcher(split[i]);

         if (m.matches())
         {
            try
            {
               float num = Float.parseFloat(m.group(1));
               TeXUnit unit = parser.getListener().createUnit(m.group(2));

               keyValList.put("width", new UserDimension(num, unit));
            }
            catch (NumberFormatException e)
            {
               // This shouldn't happen
            }

            continue;
         }

         m = FILE_PATTERN.matcher(split[i]);

         if (m.matches())
         {
            fileName = m.group(1);
         }
      }

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObjectList stack = new TeXObjectList();

      stack.add(listener.getControlSequence("includegraphics"));
      stack.add(listener.getOther((int)'['));
      stack.add(keyValList);
      stack.add(listener.getOther((int)']'));
      stack.add(listener.createGroup(fileName));

      return stack;
   }

   private static final Pattern HEIGHT_PATTERN = 
      Pattern.compile(" *height +(\\d*\\.?\\d+)([a-z]+) *");

   private static final Pattern WIDTH_PATTERN = 
      Pattern.compile(" *width +(\\d*\\.?\\d+)([a-z]+) *");

   private static final Pattern FILE_PATTERN = 
      Pattern.compile(" *tempfilename\\s+'(.+)'\\s*");
}
