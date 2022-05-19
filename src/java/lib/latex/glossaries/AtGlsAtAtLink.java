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
import com.dickimawbooks.texparserlib.primitives.Relax;
import com.dickimawbooks.texparserlib.latex.*;

public class AtGlsAtAtLink extends ControlSequence
{
   public AtGlsAtAtLink(GlossariesSty sty)
   {
      this("@gls@@link", sty);
   }

   public AtGlsAtAtLink(String name, GlossariesSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new AtGlsAtAtLink(getName(), getSty());
   }

   // leave indexing/recording to TeX
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject options;

      if (parser == stack)
      {
         options = parser.popNextArg('[', ']');
      }
      else
      {
         options = stack.popArg(parser, '[', ']');
      }

      TeXObject labelArg;

      if (parser == stack)
      {
         labelArg = parser.popNextArg();
      }
      else
      {
         labelArg = stack.popArg(parser);
      }

      if (labelArg instanceof Expandable)
      {
         TeXObjectList expanded;

         if (parser == stack)
         {
            expanded = ((Expandable)labelArg).expandfully(parser);
         }
         else
         {
            expanded = ((Expandable)labelArg).expandfully(parser, stack);
         }

         if (expanded != null)
         {
            labelArg = expanded;
         }
      }

      String label = labelArg.toString(parser);

      TeXObject linkText;

      if (parser == stack)
      {
         linkText = parser.popNextArg();
      }
      else
      {
         linkText = stack.popArg(parser);
      }

      if (sty.isEntryDefined(label))
      {
         // \let\do@gls@link@checkfirsthyper\relax
         parser.putControlSequence(true, 
            new AssignedControlSequence("do@gls@link@checkfirsthyper", new Relax()));

         if (sty.isExtra())
         {
            parser.putControlSequence(true,
              new GenericCommand(true, "glscustomtext", null, linkText));

            stack.push(listener.getControlSequence("@glsxtr@field@linkdefs"));
         }

         Group grp = listener.createGroup();
         grp.add(linkText);

         stack.push(grp);
         stack.push(listener.createGroup(label));
         stack.push(listener.getOther(']'));

         if (options != null)
         {
            stack.push(options);
         }

         stack.push(listener.getOther('['));

         stack.push(listener.getControlSequence("@gls@link"));
      }
      else
      {
         sty.undefWarnOrError(parser, stack, GlossariesSty.ENTRY_NOT_DEFINED, label);
      }

      stack.push(new TeXCsRef("glspostlinkhook"));
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   public GlossariesSty getSty()
   {
      return sty;
   }

   private GlossariesSty sty;
}
