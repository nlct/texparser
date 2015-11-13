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
import java.util.Vector;
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

      registerControlSequence(new GenericCommand("sectionrefname",
       null, listener.createString("Section")));

      registerControlSequence(new GenericCommand("sectionsrefname",
       null, listener.createString("Sections")));

      registerControlSequence(new GenericCommand("equationrefname",
       null, listener.createString("Equation")));

      registerControlSequence(new GenericCommand("equationsrefname",
       null, listener.createString("Equations")));

      registerControlSequence(new GenericCommand("tablerefname",
       null, listener.createString("Table")));

      registerControlSequence(new GenericCommand("tablesrefname",
       null, listener.createString("Tables")));

      registerControlSequence(new GenericCommand("figurerefname",
       null, listener.createString("Figure")));

      registerControlSequence(new GenericCommand("figuresrefname",
       null, listener.createString("Figures")));

      registerControlSequence(new GenericCommand("algorithmrefname",
       null, listener.createString("Algorithm")));

      registerControlSequence(new GenericCommand("algorithmsrefname",
       null, listener.createString("Algorithms")));

      registerControlSequence(new GenericCommand("theoremrefname",
       null, listener.createString("Theorem")));

      registerControlSequence(new GenericCommand("theoremsrefname",
       null, listener.createString("Theorems")));

      registerControlSequence(new GenericCommand("lemmarefname",
       null, listener.createString("Lemma")));

      registerControlSequence(new GenericCommand("lemmasrefname",
       null, listener.createString("Lemmas")));

      registerControlSequence(new GenericCommand("remarkrefname",
       null, listener.createString("Remark")));

      registerControlSequence(new GenericCommand("remarksrefname",
       null, listener.createString("Remarks")));

      registerControlSequence(new GenericCommand("corollaryrefname",
       null, listener.createString("Corollary")));

      registerControlSequence(new GenericCommand("corollarysrefname",
       null, listener.createString("Corollaries")));

      registerControlSequence(new GenericCommand("definitionrefname",
       null, listener.createString("Definition")));

      registerControlSequence(new GenericCommand("definitionsrefname",
       null, listener.createString("Definitions")));

      registerControlSequence(new GenericCommand("axiomrefname",
       null, listener.createString("Axiom")));

      registerControlSequence(new GenericCommand("axiomsrefname",
       null, listener.createString("Axioms")));

      registerControlSequence(new GenericCommand("examplerefname",
       null, listener.createString("Example")));

      registerControlSequence(new GenericCommand("examplesrefname",
       null, listener.createString("Examples")));

      registerControlSequence(new GenericCommand("appendixrefname",
       null, listener.createString("Appendix")));

      registerControlSequence(new GenericCommand("appendixsrefname",
       null, listener.createString("Appendices")));

      registerControlSequence(new GenericCommand("partrefname",
       null, listener.createString("Part")));

      registerControlSequence(new GenericCommand("partsrefname",
       null, listener.createString("Parts")));

      registerControlSequence(new GenericCommand("@jmlr@reflistsep",
       null, listener.createString(", ")));

      registerControlSequence(new GenericCommand("@jmlr@reflistlastsep",
       null, listener.createString(" and ")));

      registerControlSequence(new JmlrKeywords());
      registerControlSequence(new JmlrName(this));
      registerControlSequence(new JmlrEmail());
      registerControlSequence(new JmlrAddr());

      registerControlSequence(new JmlrObjectRef());
      registerControlSequence(new JmlrObjectTypeRef("section"));
      registerControlSequence(new JmlrObjectTypeRef("equation",
        listener.createString("("), listener.createString(")")));
      registerControlSequence(new JmlrObjectTypeRef("table"));
      registerControlSequence(new JmlrObjectTypeRef("figure"));
      registerControlSequence(new JmlrObjectTypeRef("algorithm"));
      registerControlSequence(new JmlrObjectTypeRef("theorem"));
      registerControlSequence(new JmlrObjectTypeRef("lemma"));
      registerControlSequence(new JmlrObjectTypeRef("remark"));
      registerControlSequence(new JmlrObjectTypeRef("corollary"));
      registerControlSequence(new JmlrObjectTypeRef("definition"));
      registerControlSequence(new JmlrObjectTypeRef("conjecture"));
      registerControlSequence(new JmlrObjectTypeRef("axiom"));
      registerControlSequence(new JmlrObjectTypeRef("exampleref"));
      registerControlSequence(new JmlrObjectTypeRef("appendix"));
      registerControlSequence(new JmlrObjectTypeRef("part"));
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

      if (sty == null)
      {
         sty = listener.getLoadedPackage("natbib");
      }

      if (sty != null)
      {
         sty.processOption("round");
      }

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

   public void addAuthor(Group author)
   {
      if (authors == null)
      {
         authors = new Vector<Group>();
      }

      authors.add(author);
   }

   public Vector<Group> getAuthors()
   {
      return authors;
   }

   private Vector<Group> authors = null;
}
