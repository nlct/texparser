/*
    Copyright (C) 2013-2023 Nicola L.C. Talbot
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

public class AmsmathSty extends LaTeXSty
{
   public AmsmathSty(KeyValList options, 
      LaTeXParserListener listener, boolean loadParentOptions)
   throws IOException
   {
      super(options, "amsmath", listener, loadParentOptions);
   }

   @Override
   public void addDefinitions()
   {
      registerControlSequence(new Align());
      registerControlSequence(new Align("align*", false));
      registerControlSequence(new Text());
   }
}
