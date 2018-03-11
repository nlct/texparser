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
package com.dickimawbooks.texparserlib.latex.amsmath;

import java.util.Hashtable;
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class AmsSymbSty extends LaTeXSty
{
   public AmsSymbSty(KeyValList options, LaTeXParserListener listener, 
      boolean loadParentOptions)
    throws IOException
   {
      this(options, "amssymb", listener, loadParentOptions);
   }

   public AmsSymbSty(KeyValList options, String name, 
      LaTeXParserListener listener, boolean loadParentOptions)
   throws IOException
   {
      super(options, name, listener, loadParentOptions);
   }

   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();

      registerControlSequence(listener.createMathSymbol("smallsetminus", 0x29F5));
      registerControlSequence(listener.createMathSymbol("nsucceq", 0x22E1));
      registerControlSequence(listener.createMathSymbol("npreceq", 0x22E0));
      registerControlSequence(listener.createMathSymbol("Cup", 0x22D3));
      registerControlSequence(listener.createMathSymbol("Cap", 0x22D2));
      registerControlSequence(listener.createMathSymbol("curlywedge", 0x22CF));
      registerControlSequence(listener.createMathSymbol("curlyvee", 0x22CE));
      registerControlSequence(listener.createMathSymbol("rightthreetimes", 0x22CC));
      registerControlSequence(listener.createMathSymbol("leftthreetimes", 0x22CB));
      registerControlSequence(listener.createMathSymbol("rtimes", 0x22CA));
      registerControlSequence(listener.createMathSymbol("ltimes", 0x22C9));
      registerControlSequence(listener.createMathSymbol("divideontimes", 0x22C7));
      registerControlSequence(listener.createMathSymbol("centerdot", 0x22C5));
      registerControlSequence(listener.createMathSymbol("intercal", 0x22BA));
      registerControlSequence(listener.createMathSymbol("nVDash", 0x22AF));
      registerControlSequence(listener.createMathSymbol("nvDash", 0x22AD));
      registerControlSequence(listener.createMathSymbol("nvdash", 0x22AC));
      registerControlSequence(listener.createMathSymbol("Vvdash", 0x22AA));
      registerControlSequence(listener.createMathSymbol("Vdash", 0x22A9));
      registerControlSequence(listener.createMathSymbol("vDash", 0x22A8));
      registerControlSequence(listener.createMathSymbol("boxdot", 0x22A1));
      registerControlSequence(listener.createMathSymbol("boxtimes", 0x22A0));
      registerControlSequence(listener.createMathSymbol("boxminus", 0x229F));
      registerControlSequence(listener.createMathSymbol("boxplus", 0x229E));
      registerControlSequence(listener.createMathSymbol("circleddash", 0x229D));
      registerControlSequence(listener.createMathSymbol("circledast", 0x229B));
      registerControlSequence(listener.createMathSymbol("circledcirc", 0x229A));
      registerControlSequence(listener.createMathSymbol("nsucc", 0x2281));
      registerControlSequence(listener.createMathSymbol("nprec", 0x2280));
      registerControlSequence(listener.createMathSymbol("between", 0x226C));
      registerControlSequence(listener.createMathSymbol("bumpeq", 0x224F));
      registerControlSequence(listener.createMathSymbol("approxeq", 0x224A));
      registerControlSequence(listener.createMathSymbol("ncong", 0x2247));
      registerControlSequence(listener.createMathSymbol("backsim", 0x223D));
      registerControlSequence(listener.createMathSymbol("because", 0x2235));
      registerControlSequence(listener.createMathSymbol("therefore", 0x2234));
      registerControlSequence(listener.createMathSymbol("sphericalangle", 0x2222));
      registerControlSequence(listener.createMathSymbol("measuredangle", 0x2221));
      registerControlSequence(listener.createMathSymbol("dotplus", 0x2214));
      registerControlSequence(listener.createMathSymbol("digamma", 0x1D7CA));
      registerControlSequence(listener.createMathSymbol("blacksquare", 0x220E));
      registerControlSequence(listener.createMathSymbol("nexists", 0x2204));
      registerControlSequence(listener.createMathSymbol("complement", 0x2201));
      registerControlSequence(listener.createMathSymbol("veebar", 0x2A61));
      registerControlSequence(listener.createMathSymbol("doublebarwedge", 0x2A5E));
      registerControlSequence(listener.createMathSymbol("Join", 0x2A1D));
   }
}
