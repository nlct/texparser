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
package com.dickimawbooks.texparserlib.latex.jmlr;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class JmlrSubFloatRef extends JmlrObjectTypeRef
{
   public JmlrSubFloatRef(String name, String floatType)
   {
      super(floatType, name, new TeXCsRef("@empty"), new TeXCsRef("@empty"),
            false);
   }

   public Object clone()
   {
      return new JmlrSubFloatRef(getName(), getTag());
   }

   protected void expandLabel(TeXParser parser, TeXObjectList expanded,
      TeXObject label)
       throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject ref = listener.getReference(label);

      if (ref == null)
      {
         expanded.add(listener.getOther('?'));
         expanded.add(listener.getOther('?'));

         return;
      }

      Group grp = null;
      TeXObjectList list = expanded;

      if (listener.isStyLoaded("hyperref"))
      {
         expanded.add(new TeXCsRef("hyperlink"));

         if (label instanceof Group)
         {
            expanded.add(label);
         }
         else
         {
            grp = listener.createGroup();
            expanded.add(grp);

            if (label instanceof TeXObjectList)
            {
               grp.addAll((TeXObjectList)label);
            }
            else
            {
               grp.add(label);
            }
         }

         grp = listener.createGroup();
         expanded.add(grp);

         list = grp;
      }

      String floatType = getTag();

      if (ref instanceof TeXObjectList)
      {
         TeXObject arg = ((TeXObjectList)ref).popStack(parser);

         if (arg instanceof TeXCsRef 
               && ((TeXCsRef)arg).getName().equals(String.format("@sub%slabel",
                   floatType)))
         {
            ((TeXObjectList)ref).popArg(parser);

            list.add(new TeXCsRef(String.format("sub%slabel", floatType)));
         }
         else
         {
            list.add(arg);
         }

         list.addAll((TeXObjectList)ref);
      }
      else
      {
         list.add(ref);
      }
   }

}
