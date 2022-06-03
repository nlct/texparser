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

public class GlsXtrGenAbbrvFmt extends AbstractGlsCommand
{
   public GlsXtrGenAbbrvFmt(GlossariesSty sty)
   {
      this("glsxtrgenabbrvfmt", sty);
   }

   public GlsXtrGenAbbrvFmt(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlsXtrGenAbbrvFmt(getName(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      Group insertGrp = listener.createGroup();
      ControlSequence cs = listener.getControlSequence("glsinsert");

      if (!cs.isEmpty())
      {
         insertGrp.add(TeXParserUtils.expandFully(cs, parser, stack), true);
      }

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

      Group subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      // plural / no case change / used

      subsubsubgrp.add(new TeXCsRef("glsxtrsubsequentplfmt"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubgrp.add(insertGrp);

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      // plural / no case change / not used

      subsubsubgrp.add(new TeXCsRef("glsxtrfullplformat"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add((TeXObject)insertGrp.clone());

      // plural / sentence case

      subsubgrp = listener.createGroup();
      subgrp.add(subsubgrp);

      subsubgrp.add(new TeXCsRef("ifglsused"));
      subsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      // plural / sentence case / used

      subsubsubgrp.add(new TeXCsRef("Glsxtrsubsequentplfmt"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add((TeXObject)insertGrp.clone());

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      // plural / sentence case / not used

      subsubsubgrp.add(new TeXCsRef("Glsxtrfullplformat"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add((TeXObject)insertGrp.clone());

      // plural / all caps

      subsubgrp = listener.createGroup();
      subgrp.add(subsubgrp);

      subsubgrp.add(new TeXCsRef("ifglsused"));
      subsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      // plural / all caps / used

      subsubsubgrp.add(new TeXCsRef("GLSxtrsubsequentplfmt"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add((TeXObject)insertGrp.clone());

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      // plural / all caps / not used

      subsubsubgrp.add(new TeXCsRef("GLSxtrfullplformat"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add((TeXObject)insertGrp.clone());

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

      subsubsubgrp.add(new TeXCsRef("glsxtrsubsequentfmt"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add((TeXObject)insertGrp.clone());

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      // singular / no case change / not used

      subsubsubgrp.add(new TeXCsRef("glsxtrfullformat"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add((TeXObject)insertGrp.clone());

      // singular / sentence case

      subsubgrp = listener.createGroup();
      subgrp.add(subsubgrp);

      subsubgrp.add(new TeXCsRef("ifglsused"));
      subsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      // singular / sentence case / used

      subsubsubgrp.add(new TeXCsRef("Glsxtrsubsequentfmt"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add((TeXObject)insertGrp.clone());

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      // singular / sentence case / not used

      subsubsubgrp.add(new TeXCsRef("Glsxtrfullformat"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add((TeXObject)insertGrp.clone());

      // singular / all caps

      subsubgrp = listener.createGroup();
      subgrp.add(subsubgrp);

      subsubgrp.add(new TeXCsRef("ifglsused"));
      subsubgrp.add(new TeXCsRef("glslabel"));

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      // singular / all caps / used

      subsubsubgrp.add(new TeXCsRef("GLSxtrsubsequentfmt"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add((TeXObject)insertGrp.clone());

      subsubsubgrp = listener.createGroup();
      subsubgrp.add(subsubsubgrp);

      // singular / all caps / not used

      subsubsubgrp.add(new TeXCsRef("GLSxtrfullformat"));
      subsubsubgrp.add(new TeXCsRef("glslabel"));
      subsubsubgrp.add((TeXObject)insertGrp.clone());

      // non-empty custom text

      grp = (Group)insertGrp.clone();
      list.add(grp);

      grp.push(new TeXCsRef("glscustomtext"));

      return list;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, parser);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack);
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser);
   }
}
