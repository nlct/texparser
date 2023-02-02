/*
    Copyright (C) 2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlsXpFont extends AbstractGlsCommand
{
   public GlsXpFont(String name, GlossariesSty sty)
   {
      this(name, name.contains("first"), name.contains("long"), sty);
   }

   public GlsXpFont(String name, boolean isFirst, boolean isLong, GlossariesSty sty)
   {
      super(name, sty);
      this.isFirst = isFirst;
      this.isLong = isLong;
   }

   public Object clone()
   {
      return new GlsXpFont(getName(), isFirst, isLong, getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      TeXObject text = popArg(parser, stack);

      String category = popLabelString(parser, stack);

      String attribute;

      if (isLong)
      {
         attribute = "markwords";
      }
      else
      {
         attribute = "markshortwords";
      }

      if (sty.isAttributeTrue(category, attribute))
      {
         list.add(listener.getControlSequence("protect"));

         String csname;

         if (isFirst)
         {
            csname = "glsfirst";
         }
         else
         {
            csname = "gls";
         }

         if (isLong)
         {
            csname += "longfont";
         }
         else
         {
            csname += "abbrvfont";
         }

         list.add(listener.getControlSequence(csname));
      }
      else
      {
         String csname;

         if (isFirst)
         {
            csname = "glsfirst";
         }
         else
         {
            csname = "gls";
         }

         csname += "innerfmt";

         if (isLong)
         {
            csname += "longfont";
         }
         else
         {
            csname += "abbrvfont";
         }

         list.add(listener.getControlSequence(csname));
      }

      Group grp = listener.createGroup();
      list.add(grp);

      grp.add(text, true);

      return list;
   }

   protected boolean isFirst=false, isLong=false;
}
