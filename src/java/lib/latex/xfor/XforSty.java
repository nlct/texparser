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
package com.dickimawbooks.texparserlib.latex.xfor;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.LaTeXParserListener;
import com.dickimawbooks.texparserlib.latex.KeyValList;
import com.dickimawbooks.texparserlib.latex.LaTeXSty;
import com.dickimawbooks.texparserlib.primitives.NewIf;

public class XforSty extends LaTeXSty
{
   public XforSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
    throws IOException
   {
      super(options, "xfor", listener, loadParentOptions);
   }

   @Override
   public void addDefinitions()
   {
      NewIf.createConditional(true, getParser(), "if@endfor", false);
   }

}
