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

public class ClearL3Object extends ControlSequence
{
   public ClearL3Object(String name)
   {
      this(name, name.contains("_gclear:"), name.charAt(name.length()-1));
   }

   public ClearL3Object(String name, boolean global, char suffix)
   {
      super(name);
      this.global = global;
      this.suffix = suffix;
   }

   public Object clone()
   {
      return new ClearL3Object(getName(), global, suffix);
   }

   public void process(TeXParser parser) throws IOException
   {
      process(parser, parser);
   }

   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      ControlSequence cs;
      String csname;

      if (suffix=='c')
      {
         csname = popLabelString(parser, stack);
         cs = parser.getControlSequence(csname);
      }
      else
      {
         cs = popControlSequence(parser, stack);
         csname = cs.getName()
      }

      if (name.startsWith("tl_"))
      {
         parser.putControlSequence(!global, new TokenListCommand(csname));
      }
      else if (name.startsWith("seq_"))
      {
         parser.putControlSequence(!global, new SequenceCommand(csname));
      }
      else if (name.startsWith("prop_"))
      {
         parser.putControlSequence(!global, new PropertyCommand(csname));
      }
      else
      {
         parser.putControlSequence(!global, new GenericCommand(true, csname));
      }
   }

   protected boolean global=false;
   protected char suffix = 'N';
}
