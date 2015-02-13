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
package com.dickimawbooks.texparserlib.primitives;

import java.io.IOException;
import java.io.Writer;

import com.dickimawbooks.texparserlib.*;

public abstract class Primitive extends ControlSequence
{
   public Primitive(String name)
   {
      super(name);
   }

   public Primitive(String name, boolean isShort)
   {
      super(name, isShort);
   }

   public boolean equals(Object other)
   {
      if (this == other) return true;

      if (other == null || !(other instanceof Primitive)) return false;

      return getName().equals(((Primitive)other).getName());
   }
}
