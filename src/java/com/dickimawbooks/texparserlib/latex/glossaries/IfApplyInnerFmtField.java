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

public class IfApplyInnerFmtField extends AbstractGlsCommand
{
   public IfApplyInnerFmtField(GlossariesSty sty)
   {
      this("glsifapplyinnerfmtfield", sty);
   }

   public IfApplyInnerFmtField(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new IfApplyInnerFmtField(getName(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String label = popLabelString(parser, stack);
      String field = popLabelString(parser, stack);
      TeXObject trueArg = popArg(parser, stack);
      TeXObject falseArg = popArg(parser, stack);

      TeXObjectList list = parser.getListener().createStack();

      if (sty.isApplyInnerFmt(label, field))
      {
         list.add(trueArg, true);
      }
      else
      {
         list.add(falseArg, true);
      }

      return list;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      return expandonce(parser, parser);
   }
}
