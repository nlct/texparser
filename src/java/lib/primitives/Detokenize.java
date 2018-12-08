/*
    Copyright (C) 2018 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.primitives;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class Detokenize extends Primitive implements Expandable
{
   public Detokenize()
   {
      this("detokenize");
   }

   public Detokenize(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Detokenize(getName());
   }

   public TeXObjectList detokenize(TeXParser parser)
     throws IOException
   {
      return detokenize(parser, parser);
   }

   private TeXObjectList detokenize(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      parser.startGroup();

      TeXSettings settings = parser.getSettings();

      settings.clearCatCode(TeXParser.TYPE_ESC);
      settings.clearCatCode(TeXParser.TYPE_MATH);
      settings.clearCatCode(TeXParser.TYPE_TAB);
      settings.clearCatCode(TeXParser.TYPE_EOL);
      settings.clearCatCode(TeXParser.TYPE_PARAM);
      settings.clearCatCode(TeXParser.TYPE_SP);
      settings.clearCatCode(TeXParser.TYPE_SB);
      settings.clearCatCode(TeXParser.TYPE_SPACE);
      settings.clearCatCode(TeXParser.TYPE_ACTIVE);
      settings.clearCatCode(TeXParser.TYPE_COMMENT);

      TeXObjectList arg;

      if (parser == stack)
      {
         arg = parser.popNextArg().string(parser);
      }
      else
      {
         arg = stack.popArg(parser).string(parser);
      }

      parser.endGroup();

      return arg;
   } 

   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      return detokenize(parser, parser);
   }

   public TeXObjectList expandfully(TeXParser parser) throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return detokenize(parser, stack);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      return expandonce(parser, stack);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      stack.addAll(0, detokenize(parser, stack));
   }

   public void process(TeXParser parser)
      throws IOException
   {
      parser.addAll(0, detokenize(parser));
   }
}
