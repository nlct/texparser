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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class SetLength extends ControlSequence
{
   public SetLength()
   {
      this("setlength");
   }

   public SetLength(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new SetLength(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject obj = popArg(parser, stack);

      if (parser.isStack(obj))
      {
         stack.push(obj, true);

         obj = popArg(parser, stack);
      }

      obj = TeXParserUtils.resolve(obj, parser);

      TeXDimension value = popDimensionArg(parser, stack);

      if (obj instanceof InternalQuantity)
      {
         ((InternalQuantity)obj).setQuantity(parser, value);
      }
      else
      {
         if (!(obj instanceof ControlSequence))
         {
            throw new TeXSyntaxException(parser,
             TeXSyntaxException.ERROR_CS_EXPECTED, obj.toString(parser),
               obj.getClass());
         }

         String csname = ((ControlSequence)obj).getName();

         if (getPrefix() == PREFIX_GLOBAL)
         {
            parser.getSettings().globalSetRegister(csname, value);
         }
         else
         {
            parser.getSettings().localSetRegister(csname, value);
         }
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
