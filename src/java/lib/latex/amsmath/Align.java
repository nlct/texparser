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
package com.dickimawbooks.texparserlib.latex.amsmath;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class Align extends MathDeclaration
{
   public Align()
   {
      this("align", TeXSettings.MODE_DISPLAY_MATH, true);
   }

   public Align(String name, boolean numbered)
   {
      this(name, TeXSettings.MODE_DISPLAY_MATH, numbered);
   }

   public Align(String name, int mode, boolean numbered)
   {
      super(name, mode, numbered);
   }

   public Object clone()
   {
      return new Align(getName(), getMode(), isNumbered());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      super.process(parser, stack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      super.process(parser);

   }

}
