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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlsAccessField extends GlsEntryField
{
   public GlsAccessField(String name, String field, GlossariesSty sty)
   {
      this(name, field, CaseChange.NO_CHANGE, sty);
   }

   public GlsAccessField(String name, String field, CaseChange caseChange, GlossariesSty sty)
   {
      super(name, field, caseChange, sty);
   }

   @Override
   public Object clone()
   {
      return new GlsAccessField(getName(), getField(), getCaseChange(), getSty());
   }

   protected TeXObjectList expand(GlsLabel glslabel, String fieldLabel,
     CaseChange caseChange, TeXParser parser, TeXObjectList stack)
   throws IOException
   {
      TeXObjectList list = super.expand(glslabel, fieldLabel, 
        caseChange, parser, stack);

      AccSupp accsupp = getAccSupp(glslabel, field);

      if (accsupp != null)
      {
         AccSuppObject obj = new AccSuppObject(accsupp, list);

         list = parser.getListener().createStack();
         list.add(obj);
      }

/*
      if (sty.isAccSupp())
      {
         String csname = "gls" + sty.getInternalFieldName(fieldLabel) 
           + "accessdisplay";

         ControlSequence cs = parser.getControlSequence(csname);

         if (cs == null)
         {
            csname = "gls" + fieldLabel + "accessdisplay";
            cs = parser.getControlSequence(csname);
         }

         if (cs != null)
         {
            Group grp = parser.getListener().createGroup();
            grp.add(list, true);

            list = parser.getListener().createStack();
            list.add(cs);
            list.add(grp);

            list.add(glslabel);
         }
      }
*/

      return list;
   }

}
