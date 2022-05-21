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
package com.dickimawbooks.texparserlib.latex.fourier;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class FourierSty extends LaTeXSty
{
   public FourierSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
   throws IOException
   {
      super(options, "fourier", listener, loadParentOptions);
   }

   @Override
   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();

      for (int i = 0; i < SYMBOLS.length; i++)
      {
         registerControlSequence(listener.createSymbol(
           (String)SYMBOLS[i][0],
           ((Integer)SYMBOLS[i][1]).intValue()));
      }
   }

   @Override
   protected void preOptions(TeXObjectList stack)
     throws IOException
   {
      getListener().requirepackage(null, "textcomp", false, stack);
   }

   // very limited support

   private static final Object[][] SYMBOLS = new Object[][]
   {
      new Object[]{"danger", Integer.valueOf(0x26A0)},
      new Object[]{"textxswup", Integer.valueOf(0x2694)},
      new Object[]{"noway", Integer.valueOf(0x26D4)},
      new Object[]{"starredbullet", Integer.valueOf(0x2726)},
      new Object[]{"grimace", Integer.valueOf(0x1F61F)},// ?
      new Object[]{"decosix", Integer.valueOf(0x2727)},// ?
      new Object[]{"aldineright", Integer.valueOf(0x2766)},
      new Object[]{"bomb", Integer.valueOf(0x1F4A3)},
      new Object[]{"lefthand", Integer.valueOf(0x261E)},
      new Object[]{"righthand", Integer.valueOf(0x261C)},
      new Object[]{"eurologo", Integer.valueOf(0x20AC)},
   };
}
