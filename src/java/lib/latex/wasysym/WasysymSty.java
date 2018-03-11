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
package com.dickimawbooks.texparserlib.latex.wasysym;

import java.util.Hashtable;
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class WasysymSty extends LaTeXSty
{
   public WasysymSty(KeyValList options,  
     LaTeXParserListener listener, boolean loadParentOptions)
   throws IOException
   {
      super(options, "wasysym", listener, loadParentOptions);
   }

   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();

      registerControlSequence(listener.createMathSymbol("Join", 0x2A1D));
      registerControlSequence(listener.createMathSymbol("Box", 0x25A1));
      registerControlSequence(listener.createMathSymbol("Diamond", 0x25C7));
      registerControlSequence(listener.createMathSymbol("leadsto", 0x2933));
      registerControlSequence(listener.createMathSymbol("sqsubset", 0x228F));
      registerControlSequence(listener.createMathSymbol("sqsupset", 0x2290));
      registerControlSequence(listener.createMathSymbol("LHD", 0x25C2));
      registerControlSequence(listener.createMathSymbol("lhd", 0x25C3));
      registerControlSequence(listener.createMathSymbol("RHD", 0x25B8));
      registerControlSequence(listener.createMathSymbol("rhd", 0x25B9));
      registerControlSequence(listener.createMathSymbol("apprle", 0x2272));
      registerControlSequence(listener.createMathSymbol("apprge", 0x2273));
      registerControlSequence(listener.createMathSymbol("wasypropto", 0x221D));
      registerControlSequence(listener.createMathSymbol("ocircle", 0x25CB));
      registerControlSequence(listener.createMathSymbol("oiint", 0x222F));
   }

}
