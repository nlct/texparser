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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class AddToHook extends ControlSequence
{
   public AddToHook(String name, String csname)
   {
      super(name);
      this.csname = csname;
   }

   public Object clone()
   {
      return new AddToHook(getName(), csname);
   }

   protected void addToHook(TeXParser parser, TeXObject code)
    throws IOException
   {
      ControlSequence cs = parser.getControlSequence(csname);

      if (cs == null)
      {
         cs = new GenericCommand(csname, null, code);
         parser.putControlSequence(cs);
         return;
      }

      if (cs instanceof GenericCommand)
      {
         ((GenericCommand)cs).getDefinition().add(code);
      }
      else if (cs instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)cs).expandonce(parser);

         if (expanded != null)
         {
            expanded.add(code);
            parser.putControlSequence(new GenericCommand(csname, null, expanded));
            return;
         }
      }

      TeXObjectList definition = new TeXObjectList();
      definition.add(cs);
      definition.add(code);
      parser.putControlSequence(new GenericCommand(csname, null, definition));
   }

   public void process(TeXParser parser) throws IOException
   {
      addToHook(parser, parser.popNextArg());
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      addToHook(parser, list.popArg(parser));
   }

   private String csname;
}
