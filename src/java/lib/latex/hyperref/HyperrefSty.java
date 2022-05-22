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
package com.dickimawbooks.texparserlib.latex.hyperref;

import java.io.IOException;

import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.latex.etoolbox.CsDef;

public class HyperrefSty extends LaTeXSty
{
   public HyperrefSty(KeyValList options, LaTeXParserListener listener, 
     boolean loadParentOptions)
    throws IOException
   {
      super(options, "hyperref", listener, loadParentOptions);
   }

   @Override
   public void addDefinitions()
   {
      registerControlSequence(new HyperTarget());
      registerControlSequence(new HyperLink());
      registerControlSequence(new HyperRef(this));
      registerControlSequence(new Href(this));
      registerControlSequence(new NoLinkUrl());
      registerControlSequence(new Url(this));
      registerControlSequence(new HyperBaseUrl(this));
      registerControlSequence(new AtFirstOfTwo("texorpdfstring"));
      // automatically implement unicode package option
      registerControlSequence(new AtFirstOfTwo("ifpdfstringunicode"));
      registerControlSequence(new SymbolCs("unichar"));
      // ignore bookmark commands
      registerControlSequence(new GobbleOpt("pdfbookmark", 1, 2));
      registerControlSequence(new GobbleOpt("currentpdfbookmark", 0, 2));
      registerControlSequence(new GobbleOpt("subpdfbookmark", 0, 2));
      registerControlSequence(new GobbleOpt("belowpdfbookmark", 0, 2));
      registerControlSequence(new AtGobble("thispdfpagelabel"));
      registerControlSequence(new LaTeXGenericEnvironment("HoHyper"));
      // make pdfstringdef simply behave like csdef
      registerControlSequence(new CsDef("pdfstringdef"));
   }

   /**
    * Prepends base URL, if supplied.
    * There's not check to determine if the URL is valid.
    * @param url URL string
    * @return full URL with base prepended
    */ 
   public String toFullUrl(String url)
   {
      if (baseUrl == null)
      {
         return url;
      }

      return baseUrl+url;
   }

   public void setBaseUrl(String base)
   {
      baseUrl = base;
   }

   protected String baseUrl = null;
}
