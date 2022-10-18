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
import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.NewIf;
import com.dickimawbooks.texparserlib.primitives.IfFalse;
import com.dickimawbooks.texparserlib.primitives.IfTrue;
import com.dickimawbooks.texparserlib.primitives.Undefined;
import com.dickimawbooks.texparserlib.generic.TeXParserSetUndefAction;
import com.dickimawbooks.texparserlib.generic.Symbol;
import com.dickimawbooks.texparserlib.generic.ParCs;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.hyperref.HyperTarget;
import com.dickimawbooks.texparserlib.latex.hyperref.HyperLink;
import com.dickimawbooks.texparserlib.latex.mfirstuc.MfirstucSty;

/**
 * A glossary style package.
 */

public class GlossaryStyleSty extends LaTeXSty
{
   public GlossaryStyleSty(String name, LaTeXParserListener listener)
   throws IOException
   {
      super(null, name, listener, false);
      status = STATUS_IMPLEMENTED;
   }

   public GlossaryStyleSty(LaTeXParserListener listener, String tag, int status)
   throws IOException
   {
      super(null, "glossary-"+tag, listener, false);
      this.status = status;
   }

   public int getStatus()
   {
      return status;
   }

   @Override
   public void addDefinitions()
   {
   }

   public static final int STATUS_NOT_LOADED=0;
   public static final int STATUS_PARSED=1;
   public static final int STATUS_IMPLEMENTED=2;

   protected int status = STATUS_NOT_LOADED;
}
