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
package com.dickimawbooks.texparserlib;

import java.io.IOException;
import java.util.Vector;
import java.util.ArrayDeque;

import com.dickimawbooks.texparserlib.primitives.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.Begin;
import com.dickimawbooks.texparserlib.latex.End;

/**
 * An object with associated accessibility support.
 */

public class AccSuppObject extends AbstractTeXObject
{
   public AccSuppObject(AccSupp accsupp, TeXObject object)
   {
      if (accsupp == null || object == null)
      {
         throw new NullPointerException();
      }

      this.accsupp = accsupp;
      this.object = object;
   }

   @Override
   public Object clone()
   {
      return new AccSuppObject(accsupp, (TeXObject)object.clone());
   }

   @Override
   public boolean isDataObject()
   {
      return true;
   }

   @Override
   public String format()
   {
      return object.format();
   }

   @Override
   public String toString(TeXParser parser)
   {
      return object.toString(parser);
   }

   @Override
   public TeXObjectList string(TeXParser parser)
    throws IOException
   {
      return object.string(parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObject accSuppObject = parser.getListener().applyAccSupp(accsupp, object);

      accSuppObject.process(parser, stack);
   }

   @Override
   public void process(TeXParser parser)
    throws IOException
   {
      TeXObject accSuppObject = parser.getListener().applyAccSupp(accsupp, object);

      accSuppObject.process(parser);
   }

   public AccSupp getAccSupp()
   {
      return accsupp;
   }

   public TeXObject getObject()
   {
      return object;
   }

   protected AccSupp accsupp;
   protected TeXObject object;
}
