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

public class L2HFontShapeDeclaration extends FontShapeDeclaration
{
   public L2HFontShapeDeclaration(String name, int shape)
   {
      super(name, shape);
   }

   public L2HFontShapeDeclaration(String name, TeXFontShape shape)
   {
      super(name, shape);
   }

   @Override
   public Object clone()
   {
      return new L2HFontShapeDeclaration(getName(), getShape());
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      super.process(parser);

      String style = "";

      switch (getShape())
      {
         case UP:
            style = "font-style: normal; font-variant: normal; ";
         break;
         case IT:
            style = "font-style: italic; font-variant: normal; ";
         break;
         case SL:
            style = "font-style: oblique; font-variant: normal; ";
         break;
         case EM:
            TeXSettings settings = parser.getSettings();
            TeXSettings parent = settings.getParent();

            if (parent != null)
            {
               TeXFontShape parentStyle = parent.getFontShape();

               if (parentStyle == TeXFontShape.UP
                 ||parentStyle == TeXFontShape.INHERIT)
               {
                  if (settings.getFontFamily() == TeXFontFamily.SF)
                  {
                     style += "font-style: oblique; ";
                  }
                  else
                  {
                     style += "font-style: italic; ";
                  }
               }
               else
               {
                  style += "font-style: normal; ";
               }
            }
            else
            {
               if (settings.getFontFamily() == TeXFontFamily.SF)
               {
                  style += "font-style: oblique; ";
               }
               else
               {
                  style += "font-style: italic; ";
               }
            }

            style += "font-variant: normal; ";

         break;
         case SC:
            style += "font-style: normal; font-variant: small-caps; ";
         break;
      }

      parser.getListener().getWriteable().write("<span style=\""+style+"\">");
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack) throws IOException
   {
      parser.getListener().getWriteable().write("</span>");
      super.end(parser, stack);
   }

}
