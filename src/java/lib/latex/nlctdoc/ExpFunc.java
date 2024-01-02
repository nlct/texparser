/*
    Copyright (C) 2024 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.nlctdoc;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.glossaries.*;

public class ExpFunc extends AbstractGlsCommand
{
   public ExpFunc(GlossariesSty sty)
   {
      this("expfunc", sty);
   }

   public ExpFunc(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new ExpFunc(getName(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObject options = popOptArg(parser, stack);

      GlsLabel glslabel = popEntryLabel(parser, stack);

      String suffix = popLabelString(parser, stack);

      GlossaryEntry entry = glslabel.getEntry(sty);

      TeXObjectList expanded = listener.createStack();

      if (entry != null)
      {
         TeXObject value = entry.get("base");

         if (value != null && !value.isEmpty())
         {
            expanded.add(new TeXCsRef("glslink"));

            if (options != null && !options.isEmpty())
            {
               expanded.add(listener.getOther('['));
               expanded.add(options, true);
               expanded.add(listener.getOther(']'));
            }

            expanded.add(glslabel);

            Group grp = listener.createGroup();
            expanded.add(grp);

            grp.add(new TeXCsRef("csfmt"));

            grp.add(listener.createGroup(
              parser.expandToString(value, stack)+":"+suffix
             ));
         }
         else
         {
            expanded.add(new TeXCsRef("gls"));

            if (options != null && !options.isEmpty())
            {
               expanded.add(listener.getOther('['));
               expanded.add(options, true);
               expanded.add(listener.getOther(']'));
            }

            expanded.add(glslabel);
         }
      }
      else
      {
         expanded.add(new TeXCsRef("gls"));

         if (options != null && !options.isEmpty())
         {
            expanded.add(listener.getOther('['));
            expanded.add(options, true);
            expanded.add(listener.getOther(']'));
         }

         expanded.add(listener.createGroup(glslabel.getLabel()+":"+suffix));
      }


      return expanded;
   }

}
