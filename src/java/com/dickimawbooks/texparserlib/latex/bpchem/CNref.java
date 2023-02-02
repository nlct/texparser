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
package com.dickimawbooks.texparserlib.latex.bpchem;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class CNref extends ControlSequence
{
   public CNref()
   {
      this("CNref");
   }

   public CNref(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new CNref(getName());
   }

   private void processArg(TeXParser parser, TeXObjectList stack, 
      TeXObject arg) throws IOException
   {
      Group grp = parser.getListener().createGroup("cn:");

      if (arg instanceof TeXObjectList)
      {
         grp.addAll((TeXObjectList)arg);
      }
      else
      {
         grp.add(arg);
      }

      stack.push(grp);
      stack.push(new TeXCsRef("ref"));
   }

   public void process(TeXParser parser) throws IOException
   {
      processArg(parser, parser, parser.popNextArg());
   }

   public void process(TeXParser parser, TeXObjectList list) throws IOException
   {
      processArg(parser, list, list.popArg(parser));
   }

}
