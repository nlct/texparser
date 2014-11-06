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

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

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

   public void addDefinitions(LaTeXParserListener listener)
   {
   }

   public void load(LaTeXParserListener listener, 
      TeXParser parser, KeyValList options)
   throws IOException
   {
      listener.usepackage(parser, null, "xkeyval");
      listener.usepackage(parser, null, "calc");
      listener.usepackage(parser, null, "etoolbox");

      listener.usepackage(parser, null, "amsmath");
      listener.usepackage(parser, null, "amssymb");
      listener.usepackage(parser, null, "natbib");
      listener.usepackage(parser, null, "graphicx");
      listener.usepackage(parser, null, "url");

      KeyValList opts = new KeyValList();
      opts.put("x11names", new Empty());
      listener.usepackage(parser, opts, "xcolor");

      opts = new KeyValList();
      opts.put("algo2e", new Empty());
      opts.put("ruled", new Empty());
      listener.usepackage(parser, opts, "algorithm2e");

      listener.usepackage(parser, null, "hyperref");
      listener.usepackage(parser, null, "nameref");
      addDefinitions(listener);
   }
}
