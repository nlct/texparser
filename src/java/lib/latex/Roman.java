/*
    Copyright (C) 2020 Nicola L.C. Talbot
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

public class Roman extends Command
{
   public Roman()
   {
      this("Roman", CharacterCase.UPPER);
   }

   public Roman(String name, CharacterCase state)
   {
      super(name);

      if (state != CharacterCase.UPPER && state != CharacterCase.LOWER)
      {
         throw new IllegalArgumentException(String.format(
          "Invalid state '%s' for Roman ", state));
      }

      this.state = state;
   }

   @Override
   public Object clone()
   {
      return new Roman(getName(), state);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      String name = parser.popRequiredString(stack);

      TeXObjectList list = new TeXObjectList();

      if (state == CharacterCase.UPPER)
      {
         list.add(new TeXCsRef("@Roman"));
      }
      else
      {
         list.add(new TeXCsRef("@roman"));
      }

      list.add(new TeXCsRef("c@"+name));

      return list;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
    throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      String name = parser.popRequiredString(stack);

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      int value = listener.getcountervalue(name);

      String strValue = RomanNumeral.romannumeral(value);

      if (state == CharacterCase.UPPER)
      {
         strValue = strValue.toUpperCase();
      }

      return listener.createString(strValue);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
    throws IOException
   {
      return expandfully(parser, parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String name = parser.popRequiredString(stack);
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      int value = listener.getcountervalue(name);

      String strValue = RomanNumeral.romannumeral(value);

      if (state == CharacterCase.UPPER)
      {
         strValue = strValue.toUpperCase();
      }

      listener.getWriteable().write(strValue);
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private CharacterCase state = CharacterCase.UPPER;
}
