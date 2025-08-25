/*
    Copyright (C) 2025 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.flowfram;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.KeyValList;
import com.dickimawbooks.texparserlib.html.L2HConverter;

public class NoTeXShowContent extends ControlSequence
{
   public NoTeXShowContent(String name, FlowFrameType type, FlowFramSty sty)
   {
      super(name);
      this.type = type;
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new NoTeXShowContent(getName(), type, sty);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      boolean isStar = (popModifier(parser, stack, '*') == '*');
      KeyValList opts = TeXParserUtils.popOptKeyValList(parser, stack);

      if (parser.getListener() instanceof L2HConverter)
      {
         FlowFrameData data;

         if (isStar)
         {
            String label = popLabelString(parser, stack);
            data = sty.getFrame(type, label);
         }
         else
         {
            int id = popInt(parser, stack);
            data = sty.getFrame(type, id);
         }

         boolean l2hImg = false;
         String imgType = "image/png";
         TeXObject alt = null;
         String cssStyle = null;

         if (data.hasShape())
         {
            l2hImg = true;
         }

         if (opts != null)
         {
            alt = opts.getValue("alt");

            String val = opts.getString("mime-type", parser, stack);

            if (val != null)
            {
               imgType = val;
            }

            Boolean bool = opts.getBoolean("image", parser, stack);

            if (bool != null)
            {
               l2hImg = bool.booleanValue();
            }

            cssStyle = opts.getString("style", parser, stack);
         }

         data.process(parser, stack, l2hImg, alt, imgType, cssStyle);
      }
      else
      {
         TeXParserUtils.process(
           parser.getListener().getControlSequence("ignorespaces"),
           parser, stack);
      }
   }

   FlowFramSty sty;
   FlowFrameType type;
}
