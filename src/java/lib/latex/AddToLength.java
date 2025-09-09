/*
    Copyright (C) 2022-2025 Nicola L.C. Talbot
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

public class AddToLength extends ControlSequence
{
   public AddToLength()
   {
      this("addtolength");
   }

   public AddToLength(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new AddToLength(getName());
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

      TeXDimension incr = popDimensionArg(parser, stack);

      if (obj instanceof InternalQuantity)
      {
         TeXUnit unit = TeXUnit.PT;
         TeXObject value = ((InternalQuantity)obj).getQuantity(parser, stack);
         float f = 0.0f;

         if (value instanceof TeXDimension)
         {
            TeXDimension dim = (TeXDimension)value;
            unit = dim.getUnit();
            f = dim.getValue();
         }
         else if (value instanceof Numerical)
         {
            f = ((Numerical)value).number(parser);
         }

         f += incr.getUnit().toUnit(parser, incr.getValue(), unit);

         ((InternalQuantity)obj).setQuantity(parser, new UserDimension(f, unit));
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
            parser.getSettings().globalAdvanceRegister(csname, incr);
         }
         else
         {
            parser.getSettings().localAdvanceRegister(csname, incr);
         }
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
