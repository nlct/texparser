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
package com.dickimawbooks.texparserlib.latex.bpchem;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

// This is just a rough approximation. Not all macros are
// implemented.
public class BpChemSty extends LaTeXSty
{
   public BpChemSty(KeyValList options, LaTeXParserListener listener, 
      boolean loadParentOptions)
    throws IOException
   {
      super(options, "bpchem", listener, loadParentOptions);
   }

   public void addDefinitions()
   {
      getListener().newcounter("BPCno");
      getListener().newcounter("BPCnoa", "BCPno");
      registerControlSequence(new BPChem());
      registerControlSequence(new Iupac());
      registerControlSequence(new Hnmr());
      registerControlSequence(new Cnmr());
      registerControlSequence(new Cis());
      registerControlSequence(new Trans());
      registerControlSequence(new BpAlpha());
      registerControlSequence(new BpAlpha("talpha"));
      registerControlSequence(new BpBeta());
      registerControlSequence(new BpBeta("tbeta"));
      registerControlSequence(new BpDelta());
      registerControlSequence(new Hapto());
      registerControlSequence(new Dreh());
      registerControlSequence(new CNref());
      registerControlSequence(new CNrefsub());
      registerControlSequence(new CNlabel());
      registerControlSequence(new CNlabelsub());
      registerControlSequence(new CNlabelnoref());
      registerControlSequence(new CNlabelsubnoref());
      registerControlSequence(new TheBPCno());
      registerControlSequence(new TheBPCnoa());
   }

   protected void preOptions() throws IOException
   {
      getListener().requirepackage(null, "xspace", false);
   }

}
