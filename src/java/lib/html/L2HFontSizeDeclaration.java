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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HFontSizeDeclaration extends FontSizeDeclaration
{
   public L2HFontSizeDeclaration(String name, TeXFontSize size)
   {
      super(name, size);
   }

   public L2HFontSizeDeclaration(String name, int size)
   {
      super(name, size);
   }

   @Override
   public Object clone()
   {
      return new L2HFontSizeDeclaration(getName(), getSize());
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      super.process(parser);

      String style = "";

      switch (getSize())
      {
         case NORMAL:
            style = "font-size: medium; ";
         break;
         case LARGE:
            style = "font-size: large; ";
         break;
         case XLARGE:
            style = "font-size: x-large; ";
         break;
         case XXLARGE:
            style = "font-size: xx-large; ";
         break;
         case HUGE:
            style = "font-size: xx-large; ";
         break;
         case XHUGE:
            style = "font-size: xx-large; ";
         break;
         case XXHUGE:
            style = "font-size: xx-large; ";
         break;
         case SMALL:
            style = "font-size: small; ";
         break;
         case FOOTNOTE:
            style = "font-size: x-small; ";
         break;
         case SCRIPT:
            style = "font-size: xx-small; ";
         break;
         case TINY:
            style = "font-size: xx-small; ";
         break;
      }

      if (!style.isEmpty())
      {
         parser.getListener().getWriteable().write("<span style=\""+style+"\">");
      }
      else
      {
         parser.getListener().getWriteable().write("<span>");
      }
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack) throws IOException
   {
      parser.getListener().getWriteable().write("</span>");
      super.end(parser, stack);
   }
}
