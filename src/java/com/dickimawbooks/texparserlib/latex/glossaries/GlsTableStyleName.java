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

public class GlsTableStyleName extends ControlSequence
{
   public GlsTableStyleName()
   {
      this("@glstable@style@name");
   }

   public GlsTableStyleName(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new GlsTableStyleName(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      parser.getSettings().localSetRegister("glstablecolsperblock", UserNumber.ONE);

      // \glstableinitlengthupdates

      parser.putControlSequence(true,
       new GenericCommand(true, "glstableinitlengthupdates"));

      // \glstablelengthupdate
      parser.putControlSequence(true,
       new AtGobble("glstablelengthupdate"));

      // \glstablefinishlengthupdates
      TeXObjectList def = listener.createStack();
      def.add(new TeXCsRef("setlength"));
      def.add(new TeXCsRef("glstablenamewidth"));
      def.add(new TeXCsRef("glstableblockwidth"));

      parser.putControlSequence(true,
       new GenericCommand(true, "glstablefinishlengthupdates", null, def));

      // \glstableblockentry
      def = listener.createStack();
      def.add(new TeXCsRef("glstableNameSingleFmt"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));
      def.add(new TeXCsRef("glstableChildEntries"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

      parser.putControlSequence(true,
        new LaTeXGenericCommand(true, "glstableblockentry", "m", def));

      // \glstableblocksubentry
      def = listener.createStack();
      def.add(new TeXCsRef("glstableSubNameSingleFmt"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

      parser.putControlSequence(true,
        new LaTeXGenericCommand(true, "glstableblocksubentry", "m", def));

      // \glstableblockheader
      def = listener.createStack();
      def.add(new TeXCsRef("glstableHeaderFmt"));
      def.add(new TeXCsRef("glstablenameheader"));

      parser.putControlSequence(true,
        new GenericCommand(true, "glstableblockheader", null, def));

      // \glstableblockalign
      def = listener.createStack();
      def.add(new TeXCsRef("glstablenamecolalign"));

      parser.putControlSequence(true,
        new GenericCommand(true, "glstableblockalign", null, def));
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
