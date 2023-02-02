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

public class GlsGenAcFmt extends AbstractGlsCommand
{
   public GlsGenAcFmt(GlossariesSty sty)
   {
      this("glsgenacfmt", sty);
   }

   public GlsGenAcFmt(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlsGenAcFmt(getName(), getSty());
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

      // plural / no case change
      Group subsubgrp = listener.createGroup();
      subgrp.add(subsubgrp);

      subsubgrp.add(new TeXCsRef("ifglsused"));
      subsubgrp.add(new TeXCsRef("glslabel"));

      // plural / no case change / used
      Group subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("acronymfont"));

      Group subsubsubsubgrp = listener.createGroup();
      subsubsubgrp.add(subsubsubsubgrp);
      subsubsubsubgrp.add(new TeXCsRef("glsentryshortpl"));
      subsubsubsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubgrp.add(new TeXCsRef("glsinsert"));

      // plural / no case change / not used
      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("genplacrfullformat"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add(new TeXCsRef("glsinsert"));

      // plural / sentence case

      subsubgrp = listener.createGroup();
      subgrp.add(subsubgrp);

      subsubgrp.add(new TeXCsRef("ifglsused"));
      subsubgrp.add(new TeXCsRef("glslabel"));

      // plural / sentence case / used

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("acronymfont"));

      subsubsubsubgrp = listener.createGroup();
      subsubsubgrp.add(subsubsubsubgrp);
      subsubsubsubgrp.add(new TeXCsRef("Glsentryshortpl"));
      subsubsubsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubgrp.add(new TeXCsRef("glsinsert"));

      // plural / sentence case / not used

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("Genplacrfullformat"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add(new TeXCsRef("glsinsert"));

      // plural / all caps

      subsubgrp = listener.createGroup();
      subgrp.add(subsubgrp);

      subsubgrp.add(new TeXCsRef("ifglsused"));
      subsubgrp.add(new TeXCsRef("glslabel"));

      // plural / all caps / used

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("mfirstucMakeUppercase"));

      subsubsubsubgrp = listener.createGroup();
      subsubsubgrp.add(subsubsubsubgrp);

      subsubsubsubgrp.add(new TeXCsRef("acronymfont"));

      Group subsubsubsubsubgrp = listener.createGroup();
      subsubsubsubgrp.add(subsubsubsubsubgrp);

      subsubsubsubsubgrp.add(new TeXCsRef("glsentryshortpl"));
      subsubsubsubsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubsubgrp.add(new TeXCsRef("glsinsert"));

      // plural / all caps / not used

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      subsubsubgrp.add(new TeXCsRef("mfirstucMakeUppercase"));

      subsubsubsubgrp = listener.createGroup();
      subsubsubgrp.add(subsubsubsubgrp);

      subsubsubsubgrp.add(new TeXCsRef("genplacrfullformat"));
      subsubsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubsubgrp.add(new TeXCsRef("glsinsert"));

      // Singular

      subgrp = listener.createGroup();
      grp.add(subgrp);

      subgrp.add(new TeXCsRef("glscapscase"));

      // singular / no case change
      subsubgrp = listener.createGroup();
      subgrp.add(subsubgrp);

      subsubgrp.add(new TeXCsRef("ifglsused"));
      subsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      // singular / no case change / used

      subsubsubgrp.add(new TeXCsRef("acronymfont"));

      subsubsubsubgrp = listener.createGroup();
      subsubsubgrp.add(subsubsubsubgrp);

      subsubsubsubgrp.add(new TeXCsRef("glsentryshort"));
      subsubsubsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubgrp.add(new TeXCsRef("glsinsert"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      // singular / no case change / not used

      subsubsubgrp.add(new TeXCsRef("genacrfullformat"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add(new TeXCsRef("glsinsert"));

      // singular / sentence case

      subsubgrp = listener.createGroup();
      subgrp.add(subsubgrp);

      subsubgrp.add(new TeXCsRef("ifglsused"));
      subsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      // singular / sentence case / used

      subsubsubgrp.add(new TeXCsRef("acronymfont"));

      subsubsubsubgrp = listener.createGroup();
      subsubsubgrp.add(subsubsubsubgrp);

      subsubsubsubgrp.add(new TeXCsRef("Glsentryshort"));
      subsubsubsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubgrp.add(new TeXCsRef("glsinsert"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      // singular / sentence case / not used

      subsubsubgrp.add(new TeXCsRef("Genacrfullformat"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add(new TeXCsRef("glsinsert"));

      // singular / all caps

      subsubgrp = listener.createGroup();
      subgrp.add(subsubgrp);

      subsubgrp.add(new TeXCsRef("ifglsused"));
      subsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      // singular / all caps / used

      subsubsubgrp.add(new TeXCsRef("mfirstucMakeUppercase"));

      subsubsubsubgrp = listener.createGroup();
      subsubsubgrp.add(subsubsubsubgrp);

      subsubsubsubgrp.add(new TeXCsRef("acronymfont"));
      subsubsubsubsubgrp = listener.createGroup();
      subsubsubsubgrp.add(subsubsubsubsubgrp);

      subsubsubsubsubgrp.add(new TeXCsRef("glsentryshort"));
      subsubsubsubsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubsubgrp.add(new TeXCsRef("glsinsert"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      // singular / all caps / not used

      subsubsubgrp.add(new TeXCsRef("mfirstucMakeUppercase"));

      subsubsubsubgrp = listener.createGroup();
      subsubsubgrp.add(subsubsubsubgrp);

      subsubsubsubgrp.add(new TeXCsRef("genacrfullformat"));
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
