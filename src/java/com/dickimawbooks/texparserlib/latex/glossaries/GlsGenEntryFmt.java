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

public class GlsGenEntryFmt extends AbstractGlsCommand
{
   public GlsGenEntryFmt(GlossariesSty sty)
   {
      this("glsgenentryfmt", sty);
   }

   public GlsGenEntryFmt(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlsGenEntryFmt(getName(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      TeXObjectList list = new TeXObjectList();

      list.add(new TeXCsRef("ifdefempty"));
      list.add(new TeXCsRef("glscustomtext"));

      // empty custom text

      Group grp = listener.createGroup();
      list.add(grp);

      grp.add(new TeXCsRef("glsifplural"));

      Group subgrp = listener.createGroup();
      grp.add(subgrp);

      subgrp.add(new TeXCsRef("glscapscase"));

      // plural / capscase / no case change
      Group subsubgrp = listener.createGroup();
      subgrp.add(subsubgrp);

      subsubgrp.add(new TeXCsRef("ifglsused"));
      subsubgrp.add(new TeXCsRef("glslabel"));

      Group subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("glsentryplural"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add(new TeXCsRef("glsinsert"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("glsentryfirstplural"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add(new TeXCsRef("glsinsert"));

      // plural / capscase / sentence case

      subsubgrp = listener.createGroup();
      subgrp.add(subsubgrp);

      subsubgrp.add(new TeXCsRef("ifglsused"));
      subsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("Glsentryplural"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add(new TeXCsRef("glsinsert"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("Glsentryfirstplural"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add(new TeXCsRef("glsinsert"));

      // plural / capscase / all caps

      subsubgrp = listener.createGroup();
      subgrp.add(subsubgrp);

      subsubgrp.add(new TeXCsRef("ifglsused"));
      subsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("mfirstucMakeUppercase"));

      Group subsubsubsubgrp = listener.createGroup();
      subsubsubgrp.add(subsubsubsubgrp);

      subsubsubsubgrp.add(new TeXCsRef("glsentryplural"));
      subsubsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubsubgrp.add(new TeXCsRef("glsinsert"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("mfirstucMakeUppercase"));

      subsubsubsubgrp = listener.createGroup();
      subsubsubgrp.add(subsubsubsubgrp);

      subsubsubsubgrp.add(new TeXCsRef("glsentryfirstplural"));
      subsubsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubsubgrp.add(new TeXCsRef("glsinsert"));

      // Singular

      subgrp = listener.createGroup();
      grp.add(subgrp);

      subgrp.add(new TeXCsRef("glscapscase"));

      // singular / capscase / no case change
      subsubgrp = listener.createGroup();
      subgrp.add(subsubgrp);

      subsubgrp.add(new TeXCsRef("ifglsused"));
      subsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("glsentrytext"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add(new TeXCsRef("glsinsert"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("glsentryfirst"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add(new TeXCsRef("glsinsert"));

      // singular / capscase / sentence case

      subsubgrp = listener.createGroup();
      subgrp.add(subsubgrp);

      subsubgrp.add(new TeXCsRef("ifglsused"));
      subsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("Glsentrytext"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add(new TeXCsRef("glsinsert"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("Glsentryfirst"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add(new TeXCsRef("glsinsert"));

      // singular / capscase / all caps

      subsubgrp = listener.createGroup();
      subgrp.add(subsubgrp);

      subsubgrp.add(new TeXCsRef("ifglsused"));
      subsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("mfirstucMakeUppercase"));

      subsubsubsubgrp = listener.createGroup();
      subsubsubgrp.add(subsubsubsubgrp);

      subsubsubsubgrp.add(new TeXCsRef("glsentrytext"));
      subsubsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubsubgrp.add(new TeXCsRef("glsinsert"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("mfirstucMakeUppercase"));

      subsubsubsubgrp = listener.createGroup();
      subsubsubgrp.add(subsubsubsubgrp);

      subsubsubsubgrp.add(new TeXCsRef("glsentryfirst"));
      subsubsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubsubgrp.add(new TeXCsRef("glsinsert"));

      // non-empty custom text

      grp = listener.createGroup();
      list.add(grp);

      grp.add(new TeXCsRef("glscustomtext"));
      grp.add(new TeXCsRef("glsinsert"));

      return list;
   }
}
