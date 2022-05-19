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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class LongNewGlossaryEntry extends NewGlossaryEntry
{
   public LongNewGlossaryEntry(GlossariesSty sty)
   {
      this("longnewglossaryentry", NewCommand.OVERWRITE_FORBID, sty);
   }

   public LongNewGlossaryEntry(String name, byte overwrite, GlossariesSty sty)
   {
      super(name, overwrite, sty);
   }

   public Object clone()
   {
      return new LongNewGlossaryEntry(getName(), overwrite, getSty());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      boolean isStar = false;

      if (sty.isExtra())
      {
         TeXObject object = stack.peekStack();

         if (object instanceof CharObject
               && ((CharObject)object).getCharCode() == (int)'*')
         {
            isStar = true;
            stack.popStack(parser);
         }
      }

      String label = popLabelString(parser, stack);

      TeXObject options = popArg(parser, stack);

      KeyValList keyValList = KeyValList.getList(parser, options);

      TeXObject descArg = popArg(parser, stack);

      if (!isStar && descArg instanceof TeXObjectList)
      {
         descArg = ((TeXObjectList)descArg).trim();

         if (sty.isExtra())
         {
            ((TeXObjectList)descArg).add(new TeXCsRef("glsxtrpostlongdescription"));
         }
         else
         {
            ((TeXObjectList)descArg).add(new TeXCsRef("nopostdesc"));
         }
      }

      keyValList.put("description", descArg);

      defineEntry(label, keyValList, parser, stack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
