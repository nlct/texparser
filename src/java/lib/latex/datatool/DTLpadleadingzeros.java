/*
    Copyright (C) 2024 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class DTLpadleadingzeros extends Command
{
   public DTLpadleadingzeros()
   {
      this("dtlpadleadingzeros");
   }

   public DTLpadleadingzeros(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new DTLpadleadingzeros(getName());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      int numDigits = TeXParserUtils.popInt(parser, stack);

      String valStr = popLabelString(parser, stack).trim();

      TeXParserListener listener = parser.getListener();

      TeXObjectList expanded = listener.createStack();

      if (valStr.startsWith("-"))
      {
         expanded.add(listener.getControlSequence(
          "dtlpadleadingzerosminus"));

         valStr = valStr.substring(1);
      }
      else
      {
         expanded.add(listener.getControlSequence(
          "dtlpadleadingzerosplus"));

         if (valStr.startsWith("+"))
         {
            valStr = valStr.substring(1);
         }
      }

      String[] split = valStr.split("\\.", 2);

      for (int i = split[0].length() ; i < numDigits; i++)
      {
         expanded.add(listener.getOther('0'));
      }

      for (int i = 0; i < split[0].length(); )
      {
         int cp = split[0].codePointAt(i);
         i += Character.charCount(cp);

         expanded.add(listener.getOther(cp));
      }

      if (split.length > 1)
      {
         expanded.add(listener.getOther('.'));

         for (int i = 0; i < split[1].length(); )
         {
            int cp = split[1].codePointAt(i);
            i += Character.charCount(cp);

            expanded.add(listener.getOther(cp));
         }
      }

      return expanded;
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
      return expandonce(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
   throws IOException
   {
      return expandonce(parser);
   }

}
