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
package com.dickimawbooks.texparserlib.latex.twemojis;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class Twemoji extends Command
{
   public Twemoji()
   {
      this("twemoji");
   }

   public Twemoji(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new Twemoji(getName());
   }

   // support limited to codepoint identifiers
   // Character.codePointOf(String) introduced to Java 9
   protected String getSequence(String id)
   {
      String[] split = id.split("-");

      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < split.length; i++)
      {
         try
         {
            int cp = Integer.parseInt(split[i], 16);

            builder.appendCodePoint(cp);
         }
         catch (NumberFormatException e)
         {
            return id;
         }
      }

      return builder.toString();
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      popOptArg(parser, stack);

      String id = popLabelString(parser, stack);

      return parser.getListener().createString(getSequence(id));
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
      return expandonce(parser, parser);
   }
}
