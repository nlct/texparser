/*
    Copyright (C) 2013 Nicola L.C. Talbot
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
   public EtoolboxSty(KeyValList options, LaTeXParserListener listener)
   throws IOException
   {
      super(options, "etoolbox", listener);
   }

   public void addDefinitions()
   {
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

      registerControlSequence(new IfDefEmpty());
      registerControlSequence(new IfDefEmpty("ifcsempty", true));
   }

   public void processOption(String option)
    throws IOException
   {
   }

   protected void preOptions()
     throws IOException
   {
   }
}
