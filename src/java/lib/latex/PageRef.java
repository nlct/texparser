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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class PageRef extends Ref
{
   public PageRef()
   {
      this("pageref");
   }

   public PageRef(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new PageRef(getName());
   }

   protected TeXObjectList expandref(TeXParser parser, TeXObject arg)
   throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject ref = listener.getPageReference(arg);

      if (ref == null) return null;

      TeXObjectList list = new TeXObjectList();

      if (listener.isStyLoaded("hyperref"))
      {
         list.add(new TeXCsRef("hyperlink"));

         if (arg instanceof Group)
         {
            list.add(arg);
         }
         else
         {
            Group grp = listener.createGroup();

            if (arg instanceof TeXObjectList)
            {
               grp.addAll((TeXObjectList)arg);
            }
            else
            {
               grp.add(arg);
            }

            list.add(grp);
         }

         if (ref instanceof Group)
         {
            list.add(ref);
         }
         else
         {
            Group grp = listener.createGroup();

            if (ref instanceof TeXObjectList)
            {
               grp.addAll((TeXObjectList)ref);
            }
            else
            {
               grp.add(ref);
            }

            list.add(grp);
         }
      }
      else
      {
         if (ref instanceof TeXObjectList)
         {
            return (TeXObjectList)ref;
         }

         list.add(ref);
      }

      return list;
   }

}
