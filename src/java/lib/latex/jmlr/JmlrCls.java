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

   public void addDefinitions(LaTeXParserListener listener)
   {
      TeXParser parser = listener.getParser();

      parser.putControlSequence(new StoreDataCs("jmlrworkshop"));
      parser.putControlSequence(new StoreDataCs("jmlryear"));
      parser.putControlSequence(new StoreDataCs("jmlrvolume"));
      parser.putControlSequence(new StoreDataCs("jmlrissue"));
      parser.putControlSequence(new StoreDataCs("jmlrpages"));
      parser.putControlSequence(new StoreDataCs("jmlrsubmitted"));
      parser.putControlSequence(new StoreDataCs("jmlrpublished"));
      parser.putControlSequence(
         new StoreDataCs("jmlrauthors", "@jmlr@authors"));
      parser.putControlSequence(new StoreDataCs("editor"));
      parser.putControlSequence(new StoreDataCs("editors"));

      parser.putControlSequence(
         new StoreDataCs("title", "@shorttitle", "@title"));
      parser.putControlSequence(
         new StoreDataCs("author", "@shortauthor", "@author"));

      parser.putControlSequence(new GenericCommand("editorname",
       null, new TeXObjectList("Editor")));

      parser.putControlSequence(new GenericCommand("editorsname",
       null, new TeXObjectList("Editors")));
   }

   protected void loadPreHyperrefPackages(LaTeXParserListener listener)
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
      opts.put("x11names", new GenericCommand("empty"));
      listener.usepackage(opts, "xcolor");

      opts = new KeyValList();
      opts.put("algo2e", new GenericCommand("empty"));
      opts.put("ruled", new GenericCommand("empty"));
      listener.usepackage(opts, "algorithm2e");

   }

   protected void preOptions(LaTeXParserListener listener)
     throws IOException
   {
      TeXParser parser = listener.getParser();
      loadPreHyperrefPackages(listener);

      ControlSequence cs = parser.getControlSequence("jmlrprehyperref");

      if (cs == null)
      {
         parser.putControlSequence(new GenericCommand("jmlrprehyperref"));
      }
      else
      {
         cs.process(parser);
      }

      listener.usepackage(null, "hyperref");
      listener.usepackage(null, "nameref");

      listener.putControlSequence(new GenericCommand("@jmlrproceedings",
       null, new TeXObjectList("Journal of Machine Learning Research")));
      listener.putControlSequence(new GenericCommand("@jmlrabbrvproceedings",
       null, new TeXObjectList("JMLR")));
      listener.putControlSequence(new JmlrProceedings());

      TeXObjectList def = new TeXObjectList();
      def.add(new TeXCsRef("jmlrproceedings"));
      def.add(listener.createGroup("JMLR"));
      def.add(listener.createGroup("Journal of Machine Learning Research"));
      listener.putControlSequence(new GenericCommand("jmlrnowcp", null, def));

      def = new TeXObjectList();
      def.add(new TeXCsRef("jmlrproceedings"));
      Group grp = listener.createGroup("JMLR ");
      grp.add(new TeXCsRef("&"));
      grp.add(listener.getLetter('C'));
      grp.add(listener.getLetter('P'));
      def.add(grp);
      def.add(listener.createGroup("JMLR: Workshop and Conference Proceedings"));
      listener.putControlSequence(new GenericCommand("jmlrwcp", null, def));
   }

   public void processOption(LaTeXParserListener listener, String option)
     throws IOException
   {
      if (option.equals("nowcp"))
      {
         listener.getControlSequence("jmlrnowcp").process(listener.getParser());
      }
      else if (option.equals("wcp"))
      {
         listener.getControlSequence("jmlrwcp").process(listener.getParser());
      }
   }
}
