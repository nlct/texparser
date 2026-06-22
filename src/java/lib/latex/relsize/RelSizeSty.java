/*
    Copyright (C) 2026 Nicola L.C. Talbot
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
package com.dickimawbooks.texparserlib.latex.relsize;

import java.io.IOException;

import java.util.HashMap;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.html.L2HConverter;

public class RelSizeSty extends LaTeXSty
{
   public RelSizeSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
   throws IOException
   {
      super(options, "relsize", listener, loadParentOptions);
   }

   @Override
   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();

      Declaration decl = new RelSize("smaller", -1);
      registerControlSequence(decl);
      registerControlSequence(listener.createTextBlockCommand("textsmaller", decl));

      decl = new RelSize("larger", 1);
      registerControlSequence(decl);
      registerControlSequence(listener.createTextBlockCommand("textlarger", decl));

      decl = new RelSize();
      registerControlSequence(decl);
      registerControlSequence(listener.createTextBlockCommand("textrelsize", decl));

      decl = new RelScale();
      registerControlSequence(decl);
      registerControlSequence(listener.createTextBlockCommand("textrelscale", decl));
   }

   @Override
   protected void postOptions(TeXObjectList stack) throws IOException
   {
      super.postOptions(stack);

      LaTeXParserListener listener = getListener();

      if (listener instanceof L2HConverter)
      {
         L2HConverter l2h = (L2HConverter)listener;

         HashMap<String,String> attrs = new HashMap<String,String>();
         attrs.put("font-size", "smaller");

         l2h.addDefaultStyle("smaller", attrs);

         attrs = new HashMap<String,String>();
         attrs.put("font-size", "larger");

         l2h.addDefaultStyle("larger", attrs);
      }
   }

}
