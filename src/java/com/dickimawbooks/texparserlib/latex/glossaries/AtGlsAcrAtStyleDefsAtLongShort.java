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

public class AtGlsAcrAtStyleDefsAtLongShort extends AbstractGlsCommand
{
   public AtGlsAcrAtStyleDefsAtLongShort(GlossariesSty sty)
   {
      this("@glsacr@styledefs@long-short", sty);
   }

   public AtGlsAcrAtStyleDefsAtLongShort(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   @Override
   public Object clone()
   {
      return new AtGlsAcrAtStyleDefsAtLongShort(getName(), getSty());
   }

   @Override
   public boolean canExpand()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser) throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return null;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser) throws IOException
   {
      return null;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObjectList def = parser.getListener().createString("description");
      def.add(parser.getListener().getOther('='));
      def.add(new TeXCsRef("the"));
      def.add(new TeXCsRef("glslongtok"));

      parser.putControlSequence(true, new GenericCommand(true, 
        "GenericAcronymFields", null, def));

      // \glsacrfullformat

      def = parser.getListener().createStack();
      def.add(new TeXCsRef("glsentrylong"));

      Group grp = parser.getListener().createGroup();
      def.add(grp);
      grp.add(parser.getListener().getParam(1));

      def.add(parser.getListener().getParam(2));
      def.add(new TeXCsRef("space"));
      def.add(parser.getListener().getOther('('));
      def.add(new TeXCsRef("protect"));
      def.add(new TeXCsRef("firstacronymfont"));

      grp = parser.getListener().createGroup();
      def.add(grp);
      grp.add(new TeXCsRef("glsentryshort"));

      Group subgrp = parser.getListener().createGroup();
      grp.add(subgrp);
      subgrp.add(parser.getListener().getParam(1));

      def.add(parser.getListener().getOther(')'));

      parser.putControlSequence(true, new GenericCommand(parser.getListener(), 
        true, "genacrfullformat", 2, def));

      // \Genacrfullformat

      def = parser.getListener().createStack();
      def.add(new TeXCsRef("Glsentrylong"));

      grp = parser.getListener().createGroup();
      def.add(grp);
      grp.add(parser.getListener().getParam(1));

      def.add(parser.getListener().getParam(2));
      def.add(new TeXCsRef("space"));
      def.add(parser.getListener().getOther('('));
      def.add(new TeXCsRef("protect"));
      def.add(new TeXCsRef("firstacronymfont"));

      grp = parser.getListener().createGroup();
      def.add(grp);
      grp.add(new TeXCsRef("glsentryshort"));

      subgrp = parser.getListener().createGroup();
      grp.add(subgrp);
      subgrp.add(parser.getListener().getParam(1));

      def.add(parser.getListener().getOther(')'));

      parser.putControlSequence(true, new GenericCommand(parser.getListener(), 
        true, "Genacrfullformat", 2, def));

      // \genplacrfullformat

      def = parser.getListener().createStack();
      def.add(new TeXCsRef("glsentrylongpl"));

      grp = parser.getListener().createGroup();
      def.add(grp);
      grp.add(parser.getListener().getParam(1));

      def.add(parser.getListener().getParam(2));
      def.add(new TeXCsRef("space"));
      def.add(parser.getListener().getOther('('));
      def.add(new TeXCsRef("protect"));
      def.add(new TeXCsRef("firstacronymfont"));

      grp = parser.getListener().createGroup();
      def.add(grp);
      grp.add(new TeXCsRef("glsentryshortpl"));

      subgrp = parser.getListener().createGroup();
      grp.add(subgrp);
      subgrp.add(parser.getListener().getParam(1));

      def.add(parser.getListener().getOther(')'));

      parser.putControlSequence(true, new GenericCommand(parser.getListener(), 
        true, "genplacrfullformat", 2, def));

      // \Genplacrfullformat

      def = parser.getListener().createStack();
      def.add(new TeXCsRef("Glsentrylongpl"));

      grp = parser.getListener().createGroup();
      def.add(grp);
      grp.add(parser.getListener().getParam(1));

      def.add(parser.getListener().getParam(2));
      def.add(new TeXCsRef("space"));
      def.add(parser.getListener().getOther('('));
      def.add(new TeXCsRef("protect"));
      def.add(new TeXCsRef("firstacronymfont"));

      grp = parser.getListener().createGroup();
      def.add(grp);
      grp.add(new TeXCsRef("glsentryshortpl"));

      subgrp = parser.getListener().createGroup();
      grp.add(subgrp);
      subgrp.add(parser.getListener().getParam(1));

      def.add(parser.getListener().getOther(')'));

      parser.putControlSequence(true, new GenericCommand(parser.getListener(), 
        true, "Genplacrfullformat", 2, def));

      // \acronymentry
      def = parser.getListener().createStack();
      def.add(new TeXCsRef("acronymfont"));
      grp = parser.getListener().createGroup();
      def.add(grp);
      grp.add(new TeXCsRef("glsentryshort"));

      subgrp = parser.getListener().createGroup();
      grp.add(subgrp);

      subgrp.add(parser.getListener().getParam(1));

      parser.putControlSequence(true, new GenericCommand(parser.getListener(), 
        true, "acronymentry", 1, def));

      parser.putControlSequence(true, new AtFirstOfTwo("acronymsort"));
      parser.putControlSequence(true, new AtFirstOfOne("acronymfont"));

      parser.putControlSequence(true, new GenericCommand(true,
        "firstacronymfont", null, new TeXCsRef("acronymfont")));

      parser.putControlSequence(true, new GenericCommand(true,
        "acrpluralsuffix", null, new TeXCsRef("glspluralsuffix")));
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

}
