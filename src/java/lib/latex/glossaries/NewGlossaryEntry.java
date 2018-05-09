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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class NewGlossaryEntry extends ControlSequence
{
   public NewGlossaryEntry(GlossariesSty sty)
   {
      this("newglossaryentry", NewCommand.OVERWRITE_FORBID, sty);
   }

   public NewGlossaryEntry(String name, byte overwrite, GlossariesSty sty)
   {
      super(name);
      this.sty = sty;
      this.overwrite = overwrite;
   }

   public Object clone()
   {
      return new NewGlossaryEntry(getName(), overwrite, getSty());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
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

      TeXObject options;

      if (parser == stack)
      {
         options = parser.popNextArg();
      }
      else
      {
         options = stack.popArg(parser);
      }

      KeyValList keyValList = KeyValList.getList(parser, options);

      sty.addEntry(overwrite, new GlossaryEntry(sty, label, keyValList));
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
   private byte overwrite;
}
