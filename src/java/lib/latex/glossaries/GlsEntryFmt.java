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

public class GlsEntryFmt extends AbstractGlsCommand
{
   public GlsEntryFmt(GlossariesSty sty)
   {
      this("glsentryfmt", sty);
   }

   public GlsEntryFmt(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlsEntryFmt(getName(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList list = listener.createStack();

      if (sty.isExtra())
      {
         list.add(new TeXCsRef("ifglshasshort"));
         list.add(new TeXCsRef("glslabel"));

         Group grp = listener.createGroup();
         list.add(grp);

         grp.add(new TeXCsRef("glssetabbrvfmt"));
         Group subgrp = listener.createGroup();
         grp.add(subgrp);

         subgrp.add(new TeXCsRef("glscategory"));
         subgrp.add(new TeXCsRef("glslabel"));

         list.add(listener.createGroup());

         list.add(new TeXCsRef("glsifregular"));
         list.add(new TeXCsRef("glslabel"));

         grp = listener.createGroup();
         list.add(grp);

         grp.add(new TeXCsRef("glsxtrregularfont"));
         grp.add(new TeXCsRef("glsgenentryfmt"));

         grp = listener.createGroup();
         list.add(grp);

         grp.add(new TeXCsRef("ifglshasshort"));
         grp.add(new TeXCsRef("glslabel"));

         subgrp = listener.createGroup();
         grp.add(subgrp);

         subgrp.add(new TeXCsRef("glsxtrabbreviationfont"));
         subgrp.add(new TeXCsRef("glsxtrgenabbrvfmt"));

         subgrp = listener.createGroup();
         grp.add(subgrp);

         subgrp.add(new TeXCsRef("glsxtrregularfont"));
         subgrp.add(new TeXCsRef("glsgenentryfmt"));
      }
      else
      {
         list.add(new TeXCsRef("glsgenentryfmt"));
      }

      return list;
   }
}
