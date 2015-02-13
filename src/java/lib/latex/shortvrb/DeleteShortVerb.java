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
package com.dickimawbooks.texparserlib.latex.shortvrb;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DeleteShortVerb extends ControlSequence
{
   public DeleteShortVerb()
   {
      this("DeleteShortVerb");
   }

   public DeleteShortVerb(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new DeleteShortVerb(getName());
   }

   // \DeleteShortVerb has a global effect

   public void process(TeXParser parser) throws IOException
   {
      TeXObject obj = parser.popNextArg();

      if (obj instanceof TeXObjectList)
      {
         TeXObjectList list = (TeXObjectList)obj;
         obj = list.pop();

         if (list.size() > 0)
         {
            parser.addAll(0, list);
         }
      }

      if (!(obj instanceof ActiveChar)
       && !(obj instanceof CharObject))
      {
         throw new TeXSyntaxException(parser, 
          TeXSyntaxException.ERROR_IMPROPER_ALPHABETIC_CONSTANT,
          obj.toString(parser));
      }

      int code;

      if (obj instanceof ActiveChar)
      {
         code = ((ActiveChar)obj).getCharCode();
      }
      else
      {
         code = ((CharObject)obj).getCharCode();
      }

      parser.removeActiveChar(code);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      TeXObject obj = stack.popArg(parser);

      if (obj instanceof TeXObjectList)
      {
         TeXObjectList list = (TeXObjectList)obj;
         obj = list.pop();

         if (list.size() > 0)
         {
            stack.addAll(0, list);
         }
      }

      if (!(obj instanceof ActiveChar)
       && !(obj instanceof CharObject))
      {
         throw new TeXSyntaxException(parser, 
          TeXSyntaxException.ERROR_IMPROPER_ALPHABETIC_CONSTANT,
          obj.toString(parser));
      }

      int code;

      if (obj instanceof ActiveChar)
      {
         code = ((ActiveChar)obj).getCharCode();
      }
      else
      {
         code = ((CharObject)obj).getCharCode();
      }

      parser.removeActiveChar(code);
   }
}
