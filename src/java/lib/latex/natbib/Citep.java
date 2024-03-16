/*
    Copyright (C) 2013-2024 Nicola L.C. Talbot
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
import com.dickimawbooks.texparserlib.auxfile.CiteInfo;
import com.dickimawbooks.texparserlib.latex.*;

public class Citep extends Cite
{
   public Citep(NatbibSty sty)
   {
      this("citep", sty);
   }

   public Citep(String name, NatbibSty sty)
   {
      super(name);
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new Citep(getName(), getSty());
   }

   @Override
   public void addPreCite(TeXParser parser, TeXObjectList list,
       boolean isStar, TeXObject opt1, TeXObject opt2)
   throws IOException
   {
      list.add(sty.getOpenBracket());

      if (opt1 != null && opt2 != null)
      {
         list.add(opt1);
         list.add(parser.getListener().getSpace());
      }
   }

   @Override
   public void addPostCite(TeXParser parser, TeXObjectList list, 
      boolean isStar, TeXObject opt1, TeXObject opt2)
   throws IOException
   {
      TeXParserListener listener = parser.getListener();

      if (opt1 != null || opt2 != null)
      {
         list.add(listener.getOther(','));
         list.add(listener.getSpace());
         list.add(opt2 == null ? opt1 : opt2);
      }

      list.add(sty.getCloseBracket());
   }

   @Override
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

   @Override
   public TeXObject expandCitation(TeXParser parser, boolean isStar,
      TeXObject opt1, TeXObject opt2, CiteInfo info)
   throws IOException
   {
      if (info.getParameterCount() < 4)
      {
         return super.expandCitation(parser, isStar, opt1, opt2, info);
      }

      switch (sty.getCiteStyle())
      {
         case NatbibSty.CITE_NUMBERS:

           return info.getParameter(0);

         case NatbibSty.CITE_AUTHORYEAR:

           TeXObjectList list = new TeXObjectList();

           list.add((TeXObject)info.getParameter(isStar ? 3 : 2).clone(), true);
           list.add(parser.getListener().getOther(','));
           list.add(parser.getListener().getSpace());
           list.add((TeXObject)info.getParameter(1).clone(), true);

           return list;
      }

      return super.expandCitation(parser, isStar, opt1, opt2, info);
   }

   @Override
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

      switch (sty.getCiteStyle())
      {
         case NatbibSty.CITE_NUMBERS:
           return stack.firstElement();
         case NatbibSty.CITE_AUTHORYEAR:
           TeXObjectList list = new TeXObjectList();

           list.add(stack.get(isStar ? 3 : 2));
           list.add(parser.getListener().getOther(','));
           list.add(parser.getListener().getSpace());
           list.add(stack.get(1));

           return list;
      }

      return cite;
   }


   public NatbibSty getSty()
   {
      return sty;
   }

   private NatbibSty sty;
}
