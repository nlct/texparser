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

public class L2HTabular extends Tabular
{
   public L2HTabular()
   {
      this("tabular");
   }

   public L2HTabular(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new L2HTabular(getName());
   }

   @Override
   protected void startTabular(TeXParser parser, TeXObjectList stack,
     int verticalAlignment, TeXObject columnSpecs)
     throws IOException
   {
      TeXObject obj = stack.peekStack();
      TeXObject caption = null;

      if (obj instanceof DataObjectList && !obj.isEmpty())
      {
         TeXObject firstObj = ((DataObjectList)obj).firstElement();

         if (firstObj instanceof StartElement 
              && ((StartElement)firstObj).getName().equals("caption"))
         {
            caption = stack.popStack(parser);
         }
      }

      super.startTabular(parser, stack, verticalAlignment, columnSpecs);

      Writeable writeable = parser.getListener().getWriteable();

      String cls = "tabular-";

      switch (parser.getSettings().getParAlign())
      {
         case TeXSettings.PAR_ALIGN_LEFT:
           cls += "l";
         break;
         case TeXSettings.PAR_ALIGN_RIGHT:
           cls += "r";
         break;
         case TeXSettings.PAR_ALIGN_CENTER:
           cls += "c";
         break;
      }

      switch (verticalAlignment)
      {
         case 'c': cls += "m"; break;
         case 'b': cls += "b"; break;
         case 't': cls += "t"; break;
      }

      writeable.writeln(String.format("<table class=\"%s\">", cls));

      if (caption != null)
      {
         if (stack == parser)
         {
            caption.process(parser);
         }
         else
         {
            caption.process(parser, stack);
         }
      }
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack) throws IOException
   {
      Writeable writeable = parser.getListener().getWriteable();

      writeable.writeln("</table>");

      super.end(parser, stack);
   }
}
