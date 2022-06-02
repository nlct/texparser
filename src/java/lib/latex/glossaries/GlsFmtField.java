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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlsFmtField extends AbstractGlsCommand
{
   public GlsFmtField(GlossariesSty sty)
   {
      this("glsfmtfield", CaseChange.NO_CHANGE, sty);
   }

   public GlsFmtField(String name, GlossariesSty sty)
   {
      this(name, CaseChange.NO_CHANGE, sty);
   }

   public GlsFmtField(String name, CaseChange caseChange, GlossariesSty sty)
   {
      super(name, sty);
      this.caseChange = caseChange;
   }

   @Override
   public Object clone()
   {
      return new GlsFmtField(getName(), caseChange, sty);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject insert = popArg(parser, stack);
      TeXObject cs = popArg(parser, stack);
      GlsLabel glslabel = popEntryLabel(parser, stack);
      String field = popLabelString(parser, stack);

      GlossaryEntry entry = glslabel.getEntry();

      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      list.add(cs);

      Group grp = listener.createGroup();
      list.add(grp);

      TeXObject val = null;

      if (entry != null)
      {
         val = entry.get(field);
      }

      switch (caseChange)
      {
         case SENTENCE:
            grp.add(listener.getControlSequence("makefirstuc"));
            Group subgrp = listener.createGroup();
            grp.add(subgrp);

            if (val != null)
            {
               subgrp.add((TeXObject)val.clone(), true);
            }

            subgrp.add(insert, true);
         break;
         case TO_UPPER:
            grp.add(listener.getControlSequence("mfirstucMakeUppercase"));
            subgrp = listener.createGroup();
            grp.add(subgrp);

            if (val != null)
            {
               subgrp.add((TeXObject)val.clone(), true);
            }

            subgrp.add(insert, true);
         break;
         default:

            if (val != null)
            {
               grp.add((TeXObject)val.clone(), true);
            }

            grp.add(insert, true);
      }

      return list;
   }

   protected CaseChange caseChange;
}
