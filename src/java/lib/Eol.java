/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public class Eol extends WhiteSpace
{
   public Eol()
   {
      this(false, String.format("%n"));
   }

   public Eol(String eol)
   {
      this(false, eol);
   }

   public Eol(boolean obey, String eol)
   {
      setEol(eol);
      this.obey = obey;
   }

   public Eol(boolean obey)
   {
      this(obey, String.format("%n"));
   }

   public void setEol(String eol)
   {
      this.eol = eol;
   }

   public String getEol()
   {
      return eol;
   }

   @Override
   public String toString(TeXParser parser)
   {
      return eol;
   }

   @Override
   public String toString()
   {
      return String.format("%s%s", getClass().getSimpleName(), format());
   }

   @Override
   public String format()
   {
      return eol;
   }

   @Override
   public Object clone()
   {
      return new Eol(obey, eol);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      if (parser.isDebugMode(TeXParser.DEBUG_PROCESSING))
      {
         parser.logMessage("PROCESSING "+toString()
          +" obey="+obey+" font family: "+parser.getSettings().getFontFamily());
      }

      if (obey || parser.getSettings().getFontFamily() == TeXFontFamily.VERB)
      {
          parser.getListener().getWriteable().write(eol);
      }
      else
      {
         super.process(parser, stack);
      }
   }

   private String eol;
   protected boolean obey = false;
}

