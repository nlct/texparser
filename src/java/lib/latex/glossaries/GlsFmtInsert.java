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

public class GlsFmtInsert extends Command
{
   public GlsFmtInsert()
   {
      this("glsfmtinsert", CaseChange.NO_CHANGE);
   }

   public GlsFmtInsert(String name, CaseChange caseChange)
   {
      super(name);
      this.caseChange = caseChange;
   }

   @Override
   public Object clone()
   {
      return new GlsFmtInsert(getName(), caseChange);
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      ControlSequence cs = listener.getControlSequence("glsinsert");
      TeXObject insert = null;

      if (!cs.isEmpty() && cs.canExpand() && cs instanceof Expandable)
      {
         if (parser == stack || stack == null)
         {
            insert = ((Expandable)cs).expandonce(parser);
         }
         else
         {
            insert = ((Expandable)cs).expandonce(parser, stack);
         }
      }

      if (insert == null)
      {
         insert = cs;
      }

      if (!insert.isEmpty())
      {
         TeXObjectList sublist = list;

         if (caseChange == CaseChange.TO_UPPER)
         {
            list.add(listener.getControlSequence("mfirstucMakeUppercase"));
            Group grp = listener.createGroup();
            list.add(grp);

            sublist = grp;
         }

         sublist.add(listener.getControlSequence("glsxtrgenentrytextfmt"));
         Group grp = listener.createGroup();
         sublist.add(grp);
         grp.add(insert);
      }

      return list;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   protected CaseChange caseChange;
}
