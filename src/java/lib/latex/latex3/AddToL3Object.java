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
package com.dickimawbooks.texparserlib.latex.latex3;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class AddToL3Object extends ControlSequence
{
   public AddToL3Object(String name)
   {
      this(name,
            name.contains("_gput_"),
            name.contains("_right"),
            name.charAt(name.length()-2),
            name.charAt(name.length()-1)
          );
   }

   public AddToL3Object(String name, boolean global,
     boolean append, char suffix1, char suffix2)
   {
      super(name);
      this.global = global;
      this.append = append;
      this.suffix1 = suffix1;
      this.suffix2 = suffix2;
   }

   public Object clone()
   {
      return new AddToL3Object(getName(), global, append, suffix1, suffix2);
   }

   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      ControlSequence cs;
      String csname;

      if (suffix1=='c')
      {
         csname = popLabelString(parser, stack);
         cs = parser.getControlSequence(csname);
      }
      else
      {
         cs = popControlSequence(parser, stack);
         csname = cs.getName();
      }

      TeXObject obj = TeXParserUtils.popL3Arg(parser, stack, suffix2);

      L3StorageCommand l3cs;

      if (cs instanceof L3StorageCommand)
      {
         l3cs = (L3StorageCommand)cs;

         if (!global)
         {
            l3cs = (L3StorageCommand)cs.clone();
         }
      }
      else if (name.startsWith("seq_"))
      {
         l3cs = new SequenceCommand(csname);
      }
      else if (name.startsWith("clist_"))
      {
         l3cs = new ClistCommand(csname);
      }
      else
      {
         l3cs = new TokenListCommand(csname);
      }

      if (append)
      {
         l3cs.append(obj);
      }
      else
      {
         l3cs.prepend(obj);
      }

      parser.putControlSequence(!global, (ControlSequence)l3cs);
   }

   protected boolean global=false, append=true;
   protected char suffix1 = 'N', suffix2='n';
}
