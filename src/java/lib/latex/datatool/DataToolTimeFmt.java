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

import java.io.IOException;
import java.util.Locale;

import com.dickimawbooks.texparserlib.*;

public class DataToolTimeFmt extends Command
{
   public DataToolTimeFmt()
   {
      this("DataToolTimeFmt");
   }

   public DataToolTimeFmt(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new DataToolTimeFmt(getName());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      int hour = popInt(parser, stack);
      int min = popInt(parser, stack);
      String secStr = popLabelString(parser, stack).trim();

      if (secStr.isEmpty())
      {
         return parser.getListener().createString(
          String.format((Locale)null, "%02d:%02d",
          hour, min));
      }
      else
      {
         try
         {
            return parser.getListener().createString(
             String.format((Locale)null, "%02d:%02d:%02d",
             hour, min, Integer.parseInt(secStr)));
         }
         catch (NumberFormatException e)
         {
            throw new TeXSyntaxException(e, parser, 
              TeXSyntaxException.ERROR_NUMBER_EXPECTED, secStr);
         }
      }
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack).expandfully(parser, stack);
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser).expandfully(parser);
   }


}
