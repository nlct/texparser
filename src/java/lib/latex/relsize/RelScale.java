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

public class RelScale extends FontSizeDeclaration
{
   public RelScale()
   {
      this("relscale");
   }

   public RelScale(String name)
   {
      super(name, TeXFontSize.USER);
   }

   @Override
   public Object clone()
   {  
      return new RelScale(getName());
   } 

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject arg = popArg(parser, stack);
      float factor = TeXParserUtils.toFloat(arg, parser, stack);

      TeXParserListener listener = parser.getListener();
      Scoping scoping = parser.getScoping();
      orgSize = scoping.getCurrentSettings().getCurrentFontSize();

      TeXDimension dim = scoping.getFontDimension();

      if (dim == null)
      {
         dim = listener.getNormalFontDimension();
      }

      dim = new UserDimension(dim.getValue()*factor, dim.getUnit());

      scoping.setUserFontSize(dim);

      if (listener instanceof L2HConverter)
      {
         L2HConverter l2h = (L2HConverter)listener;

         l2h.writeliteral("<span");

         TeXFontText font = new TeXFontText();
         font.setSize(dim);

         try
         {
            l2h.writeliteral(
              l2h.getStyleOrClass(font.getCssAttributes(parser)));
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
}

