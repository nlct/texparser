/*         
    Copyright (C) 2026 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.relsize;

import java.io.IOException;
         
import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.FontSizeDeclaration;
import com.dickimawbooks.texparserlib.html.L2HConverter;

public class RelSize extends FontSizeDeclaration
{
   public RelSize()
   {
      this("relsize", 0);
   }

   public RelSize(String name, int defStep)
   {
      super(name, defStep < 0 ? TeXFontSize.SMALLER : TeXFontSize.LARGER);
      this.defaultStep = defStep;
   }

   public RelSize(String name, TeXFontSize size, int defStep)
   {
      super(name, size);
      this.defaultStep = defStep;
   }

   @Override
   public Object clone()
   {  
      return new RelSize(getName(), getSize(), defaultStep);
   } 

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXParserListener listener = parser.getListener();

      Numerical stepArg;
      double step;

      if (defaultStep == 0)
      {
         stepArg = TeXParserUtils.popNumericalArg(parser, stack);
         step = TeXParserUtils.toDouble(stepArg, parser, stack);
      }
      else
      {
         stepArg = TeXParserUtils.popOptNumericalArg(parser, stack);

         if (stepArg == null)
         {
            step = 1;
         }
         else
         {
            step = TeXParserUtils.toDouble(stepArg, parser, stack);
         }

         if (size == TeXFontSize.SMALLER)
         {
            step = -step;
         }
      }

      TeXSettings settings = parser.getSettings();

      orgSize = settings.getCurrentFontSize();

      TeXFontSize newSize = size.deriveRelative((int)Math.round(step));

      TeXFontText font = new TeXFontText();

      if (newSize == null)
      {
         TeXDimension dim = settings.getFontDimension();

         if (dim == null)
         {
            dim = listener.getNormalFontDimension();
         }

         float pt = dim.getUnit().toPt(parser, dim.getValue());

         dim = new UserDimension(Math.max(1, pt+step), FixedUnit.PT);

         settings.setUserFontSize(dim);

         font.setSize(dim);
      }
      else
      {
         settings.setFontSize(newSize);
         font.setSize(newSize);
      }

      if (listener instanceof L2HConverter)
      {
         L2HConverter l2h = (L2HConverter)listener;

         l2h.writeliteral("<span");

         try
         {
            if (step == -1)
            {
               l2h.writeliteral(
                 l2h.getStyleOrClass("smaller", font.getCssAttributes(parser)));
            }
            else if (step == 1)
            {
               l2h.writeliteral(
                 l2h.getStyleOrClass("larger", font.getCssAttributes(parser)));
            }
            else
            {
               l2h.writeliteral(
                 l2h.getStyleOrClass(font.getCssAttributes(parser)));
            }
         }
         catch (TeXSyntaxException e)
         {
            l2h.getTeXApp().error(e);
         }

         l2h.writeliteral(">");
      }
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXParserListener listener = parser.getListener();

      if (listener instanceof L2HConverter)
      {
         L2HConverter l2h = (L2HConverter)listener;

         l2h.getWriteable().writeliteral("</span>");
      }

      super.end(parser, stack);
   }

   int defaultStep;
}

