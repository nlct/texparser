/*
    Copyright (C) 2013-20 Nicola L.C. Talbot
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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.RomanNumeral;

public class AtRoman extends Command
{
   public AtRoman(String name, CharacterCase state)
   {
      super(name);

      if (state != CharacterCase.UPPER && state != CharacterCase.LOWER)
      {
         throw new IllegalArgumentException(String.format(
          "Invalid state '%s' for AtRoman ", state));
      }

      this.state = state;
   }

   @Override
   public Object clone()
   {
      return new AtRoman(getName(), state);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
   throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObject obj = getSymbol(parser, parser.popRequiredNumber(stack));

      if (obj instanceof TeXObjectList)
      {
         return (TeXObjectList)obj;
      }

      TeXObjectList list = new TeXObjectList();
      list.add(obj);

      return list;
   }

   protected TeXObject getSymbol(TeXParser parser, TeXNumber arg)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      String roman = RomanNumeral.romannumeral(arg.number(parser));

      return parser.getListener().createString(state == CharacterCase.UPPER ?
        roman.toUpperCase() : roman);
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      TeXObject obj = getSymbol(parser, parser.popNumber());

      obj.process(parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject obj = getSymbol(parser, stack.popNumber(parser));

      obj.process(parser, stack);
   }

   private CharacterCase state;
}
