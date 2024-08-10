/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.etoolbox;

import java.util.Hashtable;
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class EtoolboxSty extends LaTeXSty
{
   public EtoolboxSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
   throws IOException
   {
      super(options, "etoolbox", listener, loadParentOptions);
   }

   @Override
   public void addDefinitions()
   {
      // Definitions

      registerControlSequence(new NewCommand("newrobustcmd",
        Overwrite.FORBID, true));
      registerControlSequence(new NewCommand("renewrobustcmd",
        Overwrite.FORCE, true));
      registerControlSequence(new NewCommand("providerobustcmd",
        Overwrite.SKIP, true));

      registerControlSequence(new CsDef());
      registerControlSequence(new CsDef("csgdef", true, false, false));
      registerControlSequence(new CsDef("csedef", true, true, true));
      registerControlSequence(new CsDef("csxdef", true, false, true));
      registerControlSequence(new CsDef("protected@csedef", true, true, true));
      registerControlSequence(new CsDef("protected@csxdef", true, false, true));

      registerControlSequence(new CsLetCs());
      registerControlSequence(new CsLetCs("cslet", true, false));
      registerControlSequence(new CsLetCs("letcs", false, true));

      registerControlSequence(new AtNameUse("csuse"));

      registerControlSequence(new Undef());
      registerControlSequence(new Undef("gundef", true, false));
      registerControlSequence(new Undef("csundef", false, true));
      registerControlSequence(new Undef("csgundef", true, true));

      // Hooks

      registerControlSequence(new PreTo());
      registerControlSequence(new PreTo("gpreto", true,
        true, false, false));
      registerControlSequence(new PreTo("epreto", false,
        true, true, false));
      registerControlSequence(new PreTo("xpreto", true,
        true, true, false));
      registerControlSequence(new PreTo("cspreto", false,
        true, false, true));
      registerControlSequence(new PreTo("csgpreto", true,
        true, false, true));
      registerControlSequence(new PreTo("csepreto", false,
        true, true, true));
      registerControlSequence(new PreTo("csxpreto", true,
        true, true, true));
      registerControlSequence(new PreTo("protected@csepreto", false,
        true, true, true));
      registerControlSequence(new PreTo("protected@csxpreto", true,
        true, true, true));

      registerControlSequence(new PreTo("appto", false,
        false, false, false));
      registerControlSequence(new PreTo("gappto", true,
        false, false, false));
      registerControlSequence(new PreTo("eappto", false,
        false, true, false));
      registerControlSequence(new PreTo("xappto", true,
        false, true, false));
      registerControlSequence(new PreTo("csappto", false,
        false, false, true));
      registerControlSequence(new PreTo("csgappto", true,
        false, false, true));
      registerControlSequence(new PreTo("cseappto", false,
        false, true, true));
      registerControlSequence(new PreTo("csxappto", true,
        false, true, true));
      registerControlSequence(new PreTo("protected@cseappto", false,
        false, true, true));
      registerControlSequence(new PreTo("protected@csxappto", true,
        false, true, true));

      registerControlSequence(new PreTo("listadd", false,
        false, false, false, true));
      registerControlSequence(new PreTo("listgadd", true,
        false, false, false, true));
      registerControlSequence(new PreTo("listeadd", false,
        false, true, false, true));
      registerControlSequence(new PreTo("listxadd", true,
        false, true, false, true));

      registerControlSequence(new PreTo("listcsadd", false,
        false, false, true, true));
      registerControlSequence(new PreTo("listcsgadd", true,
        false, false, true, true));
      registerControlSequence(new PreTo("listcseadd", false,
        false, true, true, true));
      registerControlSequence(new PreTo("listcsxadd", true,
        false, true, true, true));

      registerControlSequence(new IfInList());
      registerControlSequence(new IfInList("xifinlist", true, false));
      registerControlSequence(new IfInList("ifinlistcs", false, true));
      registerControlSequence(new IfInList("xifinlistcs", true, true));

      registerControlSequence(new ListBreak());
      registerControlSequence(new AtFirstOfOne("do"));
      registerControlSequence(new DoListLoop());
      registerControlSequence(new DoListLoop("dolistcsloop", true, true));
      registerControlSequence(new DoListLoop("forlistloop", false, false));
      registerControlSequence(new DoListLoop("forlistcsloop", true, false));

      registerControlSequence(new DoCsvList());
      registerControlSequence(new DoCsvList("forcsvlist", true));

      registerControlSequence(new ListRemove());
      registerControlSequence(new ListRemove("listgremove", true, false));
      registerControlSequence(new ListRemove("listcsremove", false, true));
      registerControlSequence(new ListRemove("listcsgremove", true, true));

      registerControlSequence(new IfDefEmpty());
      registerControlSequence(new IfDefEmpty("ifcsempty", true));
      registerControlSequence(new IfStrEmpty());

      registerControlSequence(new IfDefString());
      registerControlSequence(new IfDefString("ifcsstring", true));

      registerControlSequence(new IfDef());
      registerControlSequence(new IfDef("ifcsdef", true));
      registerControlSequence(new IfUndef());
      registerControlSequence(new IfUndef("ifcsundef", true));

      registerControlSequence(new NewToggle());
      registerControlSequence(new SetToggleState(true));
      registerControlSequence(new SetToggleState(false));
      registerControlSequence(new IfToggle());
   }

   public static final String TOGGLE_ALREADY_DEFINED
     = "etoolbox.toggle_already_defined";
}
