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
package com.dickimawbooks.texparserlib.latex.jmlr;

import java.util.Hashtable;
import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;
import com.dickimawbooks.texparserlib.generic.*;

public class JmlrCls extends LaTeXCls
{
   public JmlrCls()
   {
      this("jmlr");
   }

   public JmlrCls(String name)
   {
      super(name);
   }

   protected void addDefinitions(LaTeXParserListener listener)
   {
   }

   public void load(LaTeXParserListener listener, 
      TeXParser parser, KeyValList options)
   throws IOException
   {
      listener.usepackage(null, "xkeyval");
      listener.usepackage(null, "calc");
      listener.usepackage(null, "etoolbox");

      listener.usepackage(null, "amsmath");
      listener.usepackage(null, "amssymb");
      listener.usepackage(null, "natbib");
      listener.usepackage(null, "graphicx");
      listener.usepackage(null, "url");

      KeyValList opts = new KeyValList();
      opts.put("x11names", new Empty());
      listener.usepackage(opts, "xcolor");

      opts = new KeyValList();
      opts.put("algo2e", new Empty());
      opts.put("ruled", new Empty());
      listener.usepackage(opts, "algorithm2e");

      listener.usepackage(null, "hyperref");
      listener.usepackage(null, "nameref");
      addDefinitions(listener);
   }
}
