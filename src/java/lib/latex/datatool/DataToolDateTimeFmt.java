/*
    Copyright (C) 2024-2025 Nicola L.C. Talbot
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

import com.dickimawbooks.texparserlib.*;

public class DataToolDateTimeFmt extends Command
{
   public DataToolDateTimeFmt()
   {
      this("DataToolDateTimeFmt");
   }

   public DataToolDateTimeFmt(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new DataToolDateTimeFmt(getName());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();
      TeXObjectList expanded = listener.createStack();

      TeXObject dateArgs = popArg(parser, stack);
      TeXObject timeArgs = popArg(parser, stack);
      TeXObject timezoneArgs = popArg(parser, stack);

      if (dateArgs.isEmpty())
      {
         if (!timeArgs.isEmpty())
         {
            expanded.add(listener.getControlSequence("DataToolTimeFmt"));
            expanded.add(timeArgs, true);
         }
      }
      else
      {
         expanded.add(listener.getControlSequence("DataToolDateFmt"));
         expanded.add(dateArgs, true);

         if (!timeArgs.isEmpty())
         {
            expanded.add(listener.getControlSequence("DataToolTimeStampFmtSep"));
            expanded.add(listener.getControlSequence("DataToolTimeFmt"));
            expanded.add(timeArgs, true);
         }
      }

      if (!timezoneArgs.isEmpty())
      {
         expanded.add(listener.getControlSequence("DataToolTimeZoneFmt"));
         expanded.add(timezoneArgs, true);
      }

      return expanded;
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
