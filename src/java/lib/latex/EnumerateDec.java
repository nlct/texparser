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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.RomanNumeral;

public class EnumerateDec extends ListDec
{
   public EnumerateDec()
   {
      this("enumerate");
   }

   public EnumerateDec(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new EnumerateDec(getName());
   }

   public void process(TeXParser parser) throws IOException
   {
      setup(parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      setup(parser);
   }

   public void setup(TeXParser parser) throws IOException
   {
      super.setup(parser);

      TeXSettings settings = parser.getSettings();

      NumericRegister enumdepth = settings.globalAdvanceRegister("@enumdepth",
         LaTeXParserListener.ONE);

      String enumctrstr = "enum"
        +RomanNumeral.romannumeral(enumdepth.number(parser));

      ControlSequence enumctr = new GenericCommand(true, 
          "@enumctr", null,
          parser.getListener().createString(enumctrstr));

      parser.putControlSequence(true, enumctr);

      ControlSequence labelCs = parser.getControlSequence(
       "label"+enumctrstr);

      if (labelCs == null)
      {
         labelCs = parser.getListener().getControlSequence("relax");
      }

      TeXObjectList listsettings = new TeXObjectList();

      listsettings.add(parser.getListener().getControlSequence("usecounter"));
      listsettings.add(enumctr);

      setup(parser, labelCs, listsettings);

   }

   public void end(TeXParser parser)
    throws IOException
   {
      TeXSettings settings = parser.getSettings();

      Register enumdepth = settings.globalAdvanceRegister("@enumdepth",
         LaTeXParserListener.MINUS_ONE);

      super.end(parser);
   }

}
