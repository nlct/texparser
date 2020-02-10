/*
    Copyright (C) 2020 Nicola L.C. Talbot
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

import java.util.Iterator;
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.KeyValList;

public class GobbleOptMandOpt extends GobbleOpt
{
   public GobbleOptMandOpt(String name)
   {
      this(name, 1, 1, 1);
   }

   public GobbleOptMandOpt(String name, int numOptional, int numMandatory,
     int numOptional2, int... modifiers)
   {
      super(name, numOptional, numMandatory, modifiers);
      this.numOptional2 = numOptional2;
   }

   public Object clone()
   {
      return new GobbleOptMandOpt(getName(), getNumOptional(), getNumMandatory(),
       getNumOptional2(), getModifiers());
   }

   public int getNumOptional2()
   {
      return numOptional2;
   }

   public void process(TeXParser parser) throws IOException
   {
      super.process(parser);

      for (int i = 0; i < numOptional2; i++)
      {
         TeXObject obj = parser.popNextArg('[', ']');

         if (obj == null)
         {
            break;
         }
      }
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      super.process(parser, list);

      for (int i = 0; i < numOptional2; i++)
      {
         TeXObject obj = list.popArg(parser, '[', ']');

         if (obj == null)
         {
            break;
         }
      }
   }

   private int numOptional2;
}
