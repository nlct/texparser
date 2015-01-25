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
package com.dickimawbooks.texparserlib.latex.natbib;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class Citet extends Cite
{
   public Citet(NatbibSty sty)
   {
      this("citet", sty);
   }

   public Citet(String name, NatbibSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new Citet(getName(), getSty());
   }

   public void addPreCite(TeXParser parser, TeXObjectList list,
       boolean isStar, TeXObject opt1, TeXObject opt2)
   throws IOException
   {
   }

   public void addPostCite(TeXParser parser, TeXObjectList list, 
      boolean isStar, TeXObject opt1, TeXObject opt2)
   throws IOException
   {
   }

   public void addCiteSep(TeXParser parser, TeXObjectList list,
       boolean isStar, int index, int numCites)
    throws IOException
   {
      if (index > 0)
      {
         list.add(sty.getSeparator());
         list.add(parser.getListener().getSpace());
      }
   }

   public TeXObject expandCitation(TeXParser parser, boolean isStar,
     TeXObject opt1, TeXObject opt2, TeXObject arg)
   throws IOException
   {
      TeXObject cite = super.expandCitation(parser, isStar, opt1, opt2, arg);

      if (cite instanceof UnknownReference
      || !(cite instanceof TeXObjectList))
      {
         return cite;
      }

      TeXObjectList stack = (TeXObjectList)cite;

      if (stack.size() < 4)
      {
         return stack;
      }

      TeXObjectList list = new TeXObjectList();

      list.add(stack.get(isStar ? 3 : 2));// author
      list.add(parser.getListener().getSpace());

      list.add(sty.getOpenBracket());

      if (opt1 != null && opt2 != null)
      {
         list.add(opt1);
         list.add(parser.getListener().getSpace());
      }

      switch (sty.getCiteStyle())
      {
         case NatbibSty.CITE_NUMBERS:
           list.add(stack.firstElement());// number
         break;
         case NatbibSty.CITE_AUTHORYEAR:
           list.add(stack.get(1));// year
         break;
      }

      TeXParserListener listener = parser.getListener();

      if (opt1 != null || opt2 != null)
      {
         list.add(listener.getOther(','));
         list.add(listener.getSpace());
         list.add(opt2 == null ? opt1 : opt2);
      }

      list.add(sty.getCloseBracket());

      return list;
   }

   public NatbibSty getSty()
   {
      return sty;
   }

   private NatbibSty sty;
}
