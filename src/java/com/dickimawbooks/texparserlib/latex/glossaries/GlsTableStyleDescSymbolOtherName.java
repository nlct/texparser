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

public class GlsTableStyleDescSymbolOtherName extends ControlSequence
{
   public GlsTableStyleDescSymbolOtherName()
   {
      this("@glstable@style@desc-symbol-other-name");
   }

   public GlsTableStyleDescSymbolOtherName(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new GlsTableStyleDescSymbolOtherName(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXParserListener listener = parser.getListener();

      parser.getSettings().localSetRegister("glstablecolsperblock", UserNumber.FOUR);

      // \glstableinitlengthupdates
      TeXObjectList def = listener.createStack();
      def.add(new TeXCsRef("ifKV@printglosstable@header"));

      def.add(new TeXCsRef("settowidth"));
      def.add(new TeXCsRef("glstablenamewidth"));
      def.add(TeXParserUtils.createGroup(listener,
         new TeXCsRef("glstableHeaderFmt"), new TeXCsRef("glstablenameheader")));

      def.add(new TeXCsRef("settowidth"));
      def.add(new TeXCsRef("glstablesymbolwidth"));
      def.add(TeXParserUtils.createGroup(listener,
         new TeXCsRef("glstableHeaderFmt"), new TeXCsRef("glstablesymbolheader")));

      def.add(new TeXCsRef("settowidth"));
      def.add(new TeXCsRef("glstableotherwidth"));
      def.add(TeXParserUtils.createGroup(listener,
         new TeXCsRef("glstableHeaderFmt"), new TeXCsRef("glstableotherheader")));

      def.add(new TeXCsRef("else"));

      def.add(new TeXCsRef("setlength"));
      def.add(new TeXCsRef("glstablenamewidth"));
      def.add(new UserDimension());

      def.add(new TeXCsRef("setlength"));
      def.add(new TeXCsRef("glstablesymbolwidth"));
      def.add(new UserDimension());

      def.add(new TeXCsRef("setlength"));
      def.add(new TeXCsRef("glstableotherwidth"));
      def.add(new UserDimension());

      def.add(new TeXCsRef("fi"));

      def.add(new TeXCsRef("setlength"));
      def.add(new TeXCsRef("glstabledescwidth"));
      def.add(new UserDimension());

      parser.putControlSequence(true,
       new GenericCommand(true, "glstableinitlengthupdates", null, def));

      // \glstablelengthupdate
      def = listener.createStack();
      def.add(new TeXCsRef("glstablemeasureandupdate"));
      def.add(new TeXCsRef("glstablenamewidth"));
      def.add(TeXParserUtils.createGroup(listener,
       new TeXCsRef("glstableName"), TeXParserUtils.createGroup(
        listener, listener.getParam(1))));

      def.add(new TeXCsRef("glstablemeasureandupdate"));
      def.add(new TeXCsRef("glstablesymbolwidth"));
      def.add(TeXParserUtils.createGroup(listener,
       new TeXCsRef("glstableSymbol"), TeXParserUtils.createGroup(
        listener, listener.getParam(1))));

      def.add(new TeXCsRef("glstablemeasureandupdate"));
      def.add(new TeXCsRef("glstableotherwidth"));
      def.add(TeXParserUtils.createGroup(listener,
       new TeXCsRef("glstableOther"), TeXParserUtils.createGroup(
        listener, listener.getParam(1))));

      parser.putControlSequence(true,
       new LaTeXGenericCommand(true, "glstablelengthupdate", "m", def));

      // \glstablefinishlengthupdates
      def = listener.createStack();
      def.add(new TeXCsRef("setlength"));
      def.add(new TeXCsRef("glstabledescwidth"));
      def.add(TeXParserUtils.createGroup(listener,
        new TeXCsRef("dimexpr"), new TeXCsRef("glstableblockwidth"),
        listener.getOther('-'), new TeXCsRef("glstablenamewidth"),
        listener.getOther('-'), new TeXCsRef("glstablesymbolwidth"),
        listener.getOther('-'), new TeXCsRef("glstableotherwidth")
      ));

      def.add(new TeXCsRef("ifdim"));
      def.add(new TeXCsRef("glstabledescwidth"));
      def.add(new UserDimension());

      def.add(new TeXCsRef("setlength"));
      def.add(new TeXCsRef("glstablenamewidth"));

      Group grp = listener.createGroup();
      def.add(grp);

      grp.add(new TeXCsRef("dimexpr"));
      grp.add(listener.getOther('0'));
      grp.add(listener.getOther('.'));
      grp.add(listener.getOther('2'));
      grp.add(listener.getOther('5'));
      grp.add(new TeXCsRef("glstableblockwidth"));

      def.add(new TeXCsRef("setlength"));
      def.add(new TeXCsRef("glstablesymbolwidth"));
      def.add(new TeXCsRef("glstablenamewidth"));

      def.add(new TeXCsRef("setlength"));
      def.add(new TeXCsRef("glstableotherwidth"));
      def.add(new TeXCsRef("glstablenamewidth"));

      def.add(new TeXCsRef("setlength"));
      def.add(new TeXCsRef("glstabledescwidth"));
      def.add(new TeXCsRef("glstablenamewidth"));

      def.add(new TeXCsRef("fi"));

      parser.putControlSequence(true,
       new GenericCommand(true, "glstablefinishlengthupdates", null, def));

      // \glstableblockentry
      def = listener.createStack();

      def.add(new TeXCsRef("glstableDesc"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));
      def.add(new TeXCsRef("glstableChildEntries"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

      def.add(listener.getTab());

      def.add(new TeXCsRef("glstableSymbol"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

      def.add(listener.getTab());

      def.add(new TeXCsRef("glstableOther"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

      def.add(listener.getTab());

      def.add(new TeXCsRef("glstableNameTarget"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

      parser.putControlSequence(true,
        new LaTeXGenericCommand(true, "glstableblockentry", "m", def));

      // \glstableblocksubentry
      def = listener.createStack();

      def.add(new TeXCsRef("glstableSubDesc"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

      def.add(new TeXCsRef("glstableSubSymbolPreSep"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

      def.add(new TeXCsRef("glstableSubOtherPreSep"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

      def.add(new TeXCsRef("glstableSubNameSep"));

      def.add(new TeXCsRef("glstableSubNameTarget"));
      def.add(TeXParserUtils.createGroup(listener, listener.getParam(1)));

      parser.putControlSequence(true,
        new LaTeXGenericCommand(true, "glstableblocksubentry", "m", def));

      // \glstableblockheader
      def = listener.createStack();
      def.add(new TeXCsRef("glstableHeaderFmt"));
      def.add(new TeXCsRef("glstabledescheader"));
      def.add(listener.getTab());
      def.add(new TeXCsRef("glstableHeaderFmt"));
      def.add(new TeXCsRef("glstablesymbolheader"));
      def.add(listener.getTab());
      def.add(new TeXCsRef("glstableHeaderFmt"));
      def.add(new TeXCsRef("glstableotherheader"));
      def.add(listener.getTab());
      def.add(new TeXCsRef("glstableHeaderFmt"));
      def.add(new TeXCsRef("glstablenameheader"));

      parser.putControlSequence(true,
        new GenericCommand(true, "glstableblockheader", null, def));

      // \glstableblockalign
      def = listener.createStack();
      def.add(new TeXCsRef("glstabledesccolalign"));
      def.add(new TeXCsRef("glstablesymbolcolalign"));
      def.add(new TeXCsRef("glstableothercolalign"));
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
