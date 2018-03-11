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
package com.dickimawbooks.texparserlib.latex.upgreek;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.primitives.Undefined;
import com.dickimawbooks.texparserlib.generic.*;

public class UpGreekSty extends LaTeXSty
{
   public UpGreekSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
    throws IOException
   {
      super(options, "upgreek", listener, loadParentOptions);
   }

   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();

      registerControlSequence(listener.createGreekSymbol("upalpha", 0x03B1));
      registerControlSequence(listener.createGreekSymbol("upbeta", 0x03B2));
      registerControlSequence(listener.createGreekSymbol("upgamma", 0x03B3));
      registerControlSequence(listener.createGreekSymbol("updelta", 0x03B4));
      registerControlSequence(listener.createGreekSymbol("upepsilon", 0x03B5));
      registerControlSequence(listener.createGreekSymbol("upvarepsilon",
         0x03B5));
      registerControlSequence(listener.createGreekSymbol("upzeta", 0x03B6));
      registerControlSequence(listener.createGreekSymbol("upeta", 0x03B7));
      registerControlSequence(listener.createGreekSymbol("uptheta", 0x03B8));
      registerControlSequence(listener.createGreekSymbol("upiota", 0x03B9));
      registerControlSequence(listener.createGreekSymbol("upkappa", 0x03BA));
      registerControlSequence(listener.createGreekSymbol("uplambda", 0x03BB));
      registerControlSequence(listener.createGreekSymbol("upmu", 0x03BC));
      registerControlSequence(listener.createGreekSymbol("upnu", 0x03BD));
      registerControlSequence(listener.createGreekSymbol("upxi", 0x03BE));
      registerControlSequence(listener.createGreekSymbol("upomicron", 0x03BF));
      registerControlSequence(listener.createGreekSymbol("uppi", 0x03C0));
      registerControlSequence(listener.createGreekSymbol("uprho", 0x03C1));
      registerControlSequence(listener.createGreekSymbol("upvarrho", 0x03C1));
      registerControlSequence(listener.createGreekSymbol("upvarsigma", 0x03C2));
      registerControlSequence(listener.createGreekSymbol("upsigma", 0x03C3));
      registerControlSequence(listener.createGreekSymbol("uptau", 0x03C4));
      registerControlSequence(listener.createGreekSymbol("upupsilon", 0x03C5));
      registerControlSequence(listener.createGreekSymbol("upphi", 0x03C6));
      registerControlSequence(listener.createGreekSymbol("upchi", 0x03C7));
      registerControlSequence(listener.createGreekSymbol("uppsi", 0x03C8));
      registerControlSequence(listener.createGreekSymbol("upomega", 0x03C9));
      registerControlSequence(listener.createGreekSymbol("upvartheta", 0x03D1));
      registerControlSequence(listener.createGreekSymbol("upvarphi", 0x03D5));
      registerControlSequence(listener.createGreekSymbol("upvarpi", 0x03D6));

      registerControlSequence(listener.createGreekSymbol("Upalpha", 0x0391));
      registerControlSequence(listener.createGreekSymbol("Upbeta", 0x0392));
      registerControlSequence(listener.createGreekSymbol("Upgamma", 0x0393));
      registerControlSequence(listener.createGreekSymbol("Updelta", 0x0394));
      registerControlSequence(listener.createGreekSymbol("Upepsilon", 0x0395));
      registerControlSequence(listener.createGreekSymbol("Upzeta", 0x0396));
      registerControlSequence(listener.createGreekSymbol("Upeta", 0x0397));
      registerControlSequence(listener.createGreekSymbol("Uptheta", 0x0398));
      registerControlSequence(listener.createGreekSymbol("Upiota", 0x0399));
      registerControlSequence(listener.createGreekSymbol("Upkappa", 0x039A));
      registerControlSequence(listener.createGreekSymbol("Uplambda", 0x039B));
      registerControlSequence(listener.createGreekSymbol("Upmu", 0x039C));
      registerControlSequence(listener.createGreekSymbol("Upnu", 0x039D));
      registerControlSequence(listener.createGreekSymbol("Upxi", 0x039E));
      registerControlSequence(listener.createGreekSymbol("Upomicron", 0x039F));
      registerControlSequence(listener.createGreekSymbol("Uppi", 0x03A0));
      registerControlSequence(listener.createGreekSymbol("Uprho", 0x03A1));
      registerControlSequence(listener.createGreekSymbol("Upsigma", 0x03A3));
      registerControlSequence(listener.createGreekSymbol("Uptau", 0x03A4));
      registerControlSequence(listener.createGreekSymbol("Upupsilon", 0x03A5));
      registerControlSequence(listener.createGreekSymbol("Upphi", 0x03A6));
      registerControlSequence(listener.createGreekSymbol("Upchi", 0x03A7));
      registerControlSequence(listener.createGreekSymbol("Uppsi", 0x03A8));
      registerControlSequence(listener.createGreekSymbol("Upomega", 0x03A9));
   }

}
