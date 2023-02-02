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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class IfNoValue extends IfBoolean
{
   public IfNoValue()
   {
      this("IfNoValueTF", true, true);
   }

   public IfNoValue(String name, boolean hasTrueArg, boolean hasFalseArg)
   {
      super(name, hasTrueArg, hasFalseArg);
   }

   public Object clone()
   {
      return new IfNoValue(getName(), hasTrueArg, hasFalseArg);
   }

   protected boolean isTrue(TeXObject arg, TeXParser parser, TeXObjectList stack)
     throws TeXSyntaxException
   {
      return L3Arg.isNoValue(arg, parser);
   }

}
