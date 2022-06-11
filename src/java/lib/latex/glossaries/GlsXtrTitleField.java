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

public class GlsXtrTitleField extends GlsFieldLink
{
   public GlsXtrTitleField(String name, String field, GlossariesSty sty)
   {
      this(name, field, CaseChange.NO_CHANGE, false, sty);
   }

   public GlsXtrTitleField(String name, String field, boolean isPlural, GlossariesSty sty)
   {
      this(name, field, CaseChange.NO_CHANGE, isPlural, sty);
   }

   public GlsXtrTitleField(String name, String field, CaseChange caseChange, GlossariesSty sty)
   {
      this(name, field, caseChange, false, sty);
   }

   public GlsXtrTitleField(String name, String field, CaseChange caseChange, boolean isPlural, GlossariesSty sty)
   {
      super(name, field, caseChange, isPlural, sty);
   }

   @Override
   public Object clone()
   {
      return new GlsXtrTitleField(getName(), getField(), getCaseChange(), isPlural(), getSty());
   }

   @Override
   protected KeyValList createDefaultOptions(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      ControlSequence cs = parser.getControlSequence("glsxtrtitleopts");

      if (cs == null)
      {
         return null;
      }

      TeXObject opts = TeXParserUtils.expandOnce(cs, parser, stack);

      return KeyValList.getList(parser, opts);
   }

}
