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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;
import java.awt.Color;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.html.L2HConverter;

public class DefSemanticCmd extends ControlSequence
{
   public DefSemanticCmd(UserGuideSty sty)
   {
      this("defsemanticcmd", sty);
   }

   public DefSemanticCmd(String name, UserGuideSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new DefSemanticCmd(getName(), sty);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      String colSpec = popOptLabelString(parser, stack);
      Color fg = null;

      if (colSpec != null && !colSpec.isEmpty())
      {
         String model = colSpec.contains(",") ? "rgb" : "named";

         fg = sty.getColorSty().getColor(parser, model, colSpec);
      }

      ControlSequence cs = popControlSequence(parser, stack);

      TeXObject csArg = popArg(parser, stack);

      ControlSequence fontCs = null;

      if (!csArg.isEmpty())
      {
         if (csArg instanceof ControlSequence)
         {
            fontCs = (ControlSequence)csArg;
         }
         else if (parser.isStack(csArg))
         {
            fontCs = popControlSequence(parser, (TeXObjectList)csArg);
         }
      }

      TeXObject prefix = popArg(parser, stack);

      if (parser.getListener() instanceof L2HConverter)
      {
         L2HConverter l2h = (L2HConverter)parser.getListener();

         String code = "\\defsemanticcmd";

         if (colSpec != null)
         {
            code += "[" + colSpec + "]";
         }

         code += "{" + cs.toString(parser) + "}{";

         if (fontCs != null)
         {
            code += fontCs.toString(parser);
         }

         code += "}{" + prefix.toString(parser) + "}";

         l2h.addToImagePreamble(code);
      }

      TeXFontText font = null;

      if (fontCs != null)
      {
         font = new TeXFontText();

         if (fontCs.getName().equals("texttt"))
         {
            font.setFamily(TeXFontFamily.VERB);
         }
         else if (fontCs.getName().equals("textsf"))
         {
            font.setFamily(TeXFontFamily.SF);
         }
         else if (fontCs.getName().equals("textit"))
         {
            font.setShape(TeXFontShape.IT);
         }
         else if (fontCs.getName().equals("textsc"))
         {
            font.setShape(TeXFontShape.SC);
         }
         else if (fontCs.getName().equals("emph"))
         {
            font.setShape(TeXFontShape.EM);
         }
         else if (fontCs.getName().equals("textbf"))
         {
            font.setWeight(TeXFontWeight.BF);
         }
      }

      sty.addSemanticCommand(cs.getName(), font, fg, prefix, null);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   private UserGuideSty sty;
}
