/*
    Copyright (C) 2015 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.generic;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class NewDimen extends ControlSequence
{
   public NewDimen()
   {
      this("newdimen");
   }

   public NewDimen(String name)
   {
      super(name);
      setAllowsPrefix(true);
   }

   public Object clone()
   {
      return new NewDimen(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXObject object = stack.popToken();

      if (!(object instanceof ControlSequence))
      {
         throw new TeXSyntaxException(parser, 
            TeXSyntaxException.ERROR_CS_EXPECTED, object);
      }

      parser.getSettings().newdimen(getPrefix() != PREFIX_GLOBAL, 
        ((ControlSequence)object).getName());
   }

   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }
}
