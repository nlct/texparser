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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HFontSizeDeclaration extends FontSizeDeclaration
{
   public L2HFontSizeDeclaration(String name, int size)
   {
      super(name, size);
   }

   public Object clone()
   {
      return new L2HFontSizeDeclaration(getName(), getSize());
   }

   public void process(TeXParser parser) throws IOException
   {
      super.process(parser);

      String style = "";

      switch (getSize())
      {
         case TeXSettings.SIZE_NORMAL:
            style = "font-size: medium; ";
         break;
         case TeXSettings.SIZE_LARGE:
            style = "font-size: large; ";
         break;
         case TeXSettings.SIZE_XLARGE:
            style = "font-size: x-large; ";
         break;
         case TeXSettings.SIZE_XXLARGE:
            style = "font-size: xx-large; ";
         break;
         case TeXSettings.SIZE_HUGE:
            style = "font-size: xx-large; ";
         break;
         case TeXSettings.SIZE_XHUGE:
            style = "font-size: xx-large; ";
         break;
         case TeXSettings.SIZE_XXHUGE:
            style = "font-size: xx-large; ";
         break;
         case TeXSettings.SIZE_SMALL:
            style = "font-size: small; ";
         break;
         case TeXSettings.SIZE_FOOTNOTE:
            style = "font-size: x-small; ";
         break;
         case TeXSettings.SIZE_SCRIPT:
            style = "font-size: xx-small; ";
         break;
         case TeXSettings.SIZE_TINY:
            style = "font-size: xx-small; ";
         break;
      }

      parser.getListener().getWriteable().write("<span style=\""+style+"\">");
   }

   public void end(TeXParser parser) throws IOException
   {
      parser.getListener().getWriteable().write("</span>");
      super.end(parser);
   }
}
