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
   public JmlrCls(KeyValList options, 
      LaTeXParserListener listener)
   throws IOException
   {
      this(options, "jmlr", listener);
   }

   public JmlrCls(KeyValList options, String name, 
      LaTeXParserListener listener)
   throws IOException
   {
      super(options, name, listener);
   }

   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();

      registerControlSequence(new StoreDataCs("jmlrworkshop"));
      registerControlSequence(new StoreDataCs("jmlryear"));
      registerControlSequence(new StoreDataCs("jmlrvolume"));
      registerControlSequence(new StoreDataCs("jmlrissue"));
      registerControlSequence(new StoreDataCs("jmlrpages"));
      registerControlSequence(new StoreDataCs("jmlrsubmitted"));
      registerControlSequence(new StoreDataCs("jmlrpublished"));
      registerControlSequence(
         new StoreDataCs("jmlrauthors", "@jmlr@authors"));
      registerControlSequence(new StoreDataCs("editor"));
      registerControlSequence(new StoreDataCs("editors"));

      registerControlSequence(
         new StoreDataCs("title", "@shorttitle", "@title"));
      registerControlSequence(
         new StoreDataCs("author", "@shortauthor", "@author"));

      registerControlSequence(new GenericCommand("editorname",
       null, listener.createString("Editor")));

      registerControlSequence(new GenericCommand("editorsname",
       null, listener.createString("Editors")));

      registerControlSequence(new JmlrKeywords());
      registerControlSequence(new JmlrName(this));
      registerControlSequence(new JmlrEmail());
      registerControlSequence(new JmlrAddr());
   }

   protected void loadPreHyperrefPackages()
     throws IOException
   {
      LaTeXParserListener listener = getListener();

      listener.usepackage(null, "xkeyval");
      listener.usepackage(null, "calc");
      listener.usepackage(null, "etoolbox");

      listener.usepackage(null, "amsmath");
      listener.usepackage(null, "amssymb");
      LaTeXSty sty = listener.usepackage(null, "natbib");

      sty.processOption("round");

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

   protected void preOptions()
     throws IOException
   {
      LaTeXParserListener listener = getListener();
      TeXParser parser = listener.getParser();

      loadPreHyperrefPackages();

      ControlSequence cs = parser.getControlSequence("jmlrprehyperref");

      if (cs == null)
      {
         registerControlSequence(new GenericCommand("jmlrprehyperref"));
      }
      else
      {
         cs.process(parser);
      }

      listener.usepackage(null, "hyperref");
      listener.usepackage(null, "nameref");

      registerControlSequence(new GenericCommand("@jmlrproceedings",
       null, listener.createString("Journal of Machine Learning Research")));
      registerControlSequence(new GenericCommand("@jmlrabbrvproceedings",
       null, listener.createString("JMLR")));
      registerControlSequence(new JmlrProceedings());
      registerControlSequence(new ObsoleteFontCs());

      TeXObjectList def = new TeXObjectList();
      def.add(new TeXCsRef("jmlrproceedings"));
      def.add(listener.createGroup("JMLR"));
      def.add(listener.createGroup("Journal of Machine Learning Research"));
      registerControlSequence(new GenericCommand("jmlrnowcp", null, def));

      def = new TeXObjectList();
      def.add(new TeXCsRef("jmlrproceedings"));
      Group grp = listener.createGroup("JMLR ");
      grp.add(new TeXCsRef("&"));
      grp.add(listener.getLetter('C'));
      grp.add(listener.getLetter('P'));
      def.add(grp);
      def.add(listener.createGroup("JMLR: Workshop and Conference Proceedings"));
      registerControlSequence(new GenericCommand("jmlrwcp", null, def));
      registerControlSequence(new AtSecondOfTwo("ifprint"));
   }

   public void processOption(String option)
     throws IOException
   {
      LaTeXParserListener listener = getListener();

      if (option.equals("nowcp"))
      {
         listener.getControlSequence("jmlrnowcp").process(listener.getParser());
      }
      else if (option.equals("wcp"))
      {
         listener.getControlSequence("jmlrwcp").process(listener.getParser());
      }
      else if (option.equals("color"))
      {
         registerControlSequence(new AtSecondOfTwo("ifprint"));
      }
      else if (option.equals("gray"))
      {
         registerControlSequence(new AtFirstOfTwo("ifprint"));
      }
   }

   public void addAuthor(TeXObject author)
   {
      authors.add(author);
   }

   public TeXObjectList getAuthors()
   {
      return authors;
   }

   private TeXObjectList authors = new TeXObjectList();
}
