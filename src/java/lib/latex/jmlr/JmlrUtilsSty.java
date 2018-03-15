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
import com.dickimawbooks.texparserlib.primitives.NewIf;

public class JmlrUtilsSty extends LaTeXSty
{
   public JmlrUtilsSty(KeyValList options, 
      LaTeXParserListener listener, boolean loadParentOptions)
   throws IOException
   {
      this(options, "jmlrutils", listener, loadParentOptions);
   }

   public JmlrUtilsSty(KeyValList options, String name, 
      LaTeXParserListener listener, boolean loadParentOptions)
   throws IOException
   {
      super(options, name, listener, loadParentOptions);
   }

   public void addDefinitions()
   {
      LaTeXParserListener listener = getListener();
      TeXParser parser = getParser();

      // Only create \iftablecaptiontop if not already defined

      if (parser.getControlSequence("iftablecaptiontop") == null)
      {
         NewIf.createConditional(true, parser, "iftablecaptiontop", true);
      }

      // Cross-referencing

      registerControlSequence(new GenericCommand("@jmlr@reflistsep", null,
        listener.createString(", ")));
      registerControlSequence(new GenericCommand("@jmlr@reflistlastsep", null,
        listener.createString(" and ")));
      registerControlSequence(new GenericCommand("sectionrefname", null,
        listener.createString("Section")));
      registerControlSequence(new GenericCommand("sectionsrefname", null,
        listener.createString("Sections")));
      registerControlSequence(new GenericCommand("equationrefname", null,
        listener.createString("Equation")));
      registerControlSequence(new GenericCommand("equationsrefname", null,
        listener.createString("Equations")));
      registerControlSequence(new GenericCommand("tablerefname", null,
        listener.createString("Table")));
      registerControlSequence(new GenericCommand("tablesrefname", null,
        listener.createString("Tables")));
      registerControlSequence(new GenericCommand("figurerefname", null,
        listener.createString("Figure")));
      registerControlSequence(new GenericCommand("figuresrefname", null,
        listener.createString("Figures")));
      registerControlSequence(new GenericCommand("algorithmrefname", null,
        listener.createString("Algorithm")));
      registerControlSequence(new GenericCommand("algorithmsrefname", null,
        listener.createString("Algorithms")));
      registerControlSequence(new GenericCommand("theoremrefname", null,
        listener.createString("Theorem")));
      registerControlSequence(new GenericCommand("theoremsrefname", null,
        listener.createString("Theorems")));
      registerControlSequence(new GenericCommand("lemmarefname", null,
        listener.createString("Lemma")));
      registerControlSequence(new GenericCommand("lemmasrefname", null,
        listener.createString("Lemmas")));
      registerControlSequence(new GenericCommand("remarkrefname", null,
        listener.createString("Remark")));
      registerControlSequence(new GenericCommand("remarksrefname", null,
        listener.createString("Remarks")));
      registerControlSequence(new GenericCommand("corollaryrefname", null,
        listener.createString("Corollary")));
      registerControlSequence(new GenericCommand("corollarysrefname", null,
        listener.createString("Corollaries")));
      registerControlSequence(new GenericCommand("definitionrefname", null,
        listener.createString("Definition")));
      registerControlSequence(new GenericCommand("definitionsrefname", null,
        listener.createString("Definitions")));
      registerControlSequence(new GenericCommand("conjecturerefname", null,
        listener.createString("Conjecture")));
      registerControlSequence(new GenericCommand("conjecturesrefname", null,
        listener.createString("Conjectures")));
      registerControlSequence(new GenericCommand("axiomrefname", null,
        listener.createString("Axiom")));
      registerControlSequence(new GenericCommand("axiomsrefname", null,
        listener.createString("Axioms")));
      registerControlSequence(new GenericCommand("examplerefname", null,
        listener.createString("Example")));
      registerControlSequence(new GenericCommand("examplesrefname", null,
        listener.createString("Examples")));
      registerControlSequence(new GenericCommand("appendixrefname", null,
        listener.createString("Appendix")));
      registerControlSequence(new GenericCommand("appendixsrefname", null,
        listener.createString("Appendices")));
      registerControlSequence(new GenericCommand("partrefname", null,
        listener.createString("Part")));
      registerControlSequence(new GenericCommand("partsrefname", null,
        listener.createString("Parts")));

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

      // Figures, tables and algorithms

      registerControlSequence(new FloatConts());
      registerControlSequence(new FloatConts("table"));
      registerControlSequence(new FloatConts("figure"));

      registerControlSequence(new AtJmlrIfGraphicxLoaded());

      if (supportSubFloats)
      {
         newsubfloat("figure", "fig");
         newsubfloat("table", "tab");

         NewIf.createConditional(true, parser, "ifjmlrutilssubfloats", true);
      }
      else
      {
         NewIf.createConditional(true, parser, "ifjmlrutilssubfloats", false);
      }

      if (supportMaths)
      {
         registerControlSequence(new JmlrSet());
         registerControlSequence(new MathAccent("orgvec", 8407));
         registerControlSequence(new JmlrVec());

         NewIf.createConditional(true, parser, "ifjmlrutilsmaths", true);
      }
      else
      {
         NewIf.createConditional(true, parser, "ifjmlrutilsmaths", false);
      }

      if (supportTheorems)
      {
         registerControlSequence(new GenericCommand("jmlrBlackBox", null,
           listener.createString("\u220E")));

         registerControlSequence(new GenericCommand(false, "jmlrQED", null,
           new TeXObject[] {new TeXCsRef("hfill"), 
           new TeXCsRef("textup"),
           new TeXCsRef("jmlrBlackBox"),
           new TeXCsRef("par"), new TeXCsRef("bigskip")}));

         registerControlSequence(new Proof());
         registerControlSequence(new GenericCommand("proofname", null,
           listener.createString("Proof")));

         registerControlSequence(new GenericCommand(true, 
           "@theorembodyfont", null,
           new TeXObject[] {new TeXCsRef("normalfont"), 
              new TeXCsRef("itshape")}));

         registerControlSequence(new GenericCommand(true, 
           "@theoremheaderfont", null,
           new TeXObject[] {new TeXCsRef("normalfont"), 
              new TeXCsRef("bfseries")}));

         registerControlSequence(new GenericCommand("@theoremsep"));
         registerControlSequence(new GenericCommand("@theorempostheader"));

         registerControlSequence(new StoreDataCs("theorembodyfont"));
         registerControlSequence(new StoreDataCs("theoremheaderfont"));
         registerControlSequence(new StoreDataCs("theoremsep"));
         registerControlSequence(new StoreDataCs("theorempostheader"));
         registerControlSequence(new JmlrNewTheorem(this));

         newtheorem("theorem", "theorem", "Theorem", null);
         newtheorem("example", "example", "Example", null);
         newtheorem("lemma", "theorem", "Lemma", null);
         newtheorem("proposition", "theorem", "Proposition", null);
         newtheorem("remark", "theorem", "Remark", null);
         newtheorem("corollary", "theorem", "Corollary", null);
         newtheorem("definition", "theorem", "Definition", null);
         newtheorem("conjecture", "theorem", "Conjecture", null);
         newtheorem("axiom", "theorem", "Axiom", null);

         NewIf.createConditional(true, parser, "ifjmlrutilstheorems", true);
      }
      else
      {
         NewIf.createConditional(true, parser, "ifjmlrutilstheorems", false);
      }

   }

   protected void postOptions() throws IOException
   {
      getListener().requirepackage(null, "etoolbox", false);

      if (supportMaths)
      {
         getListener().requirepackage(null, "amsmath", false);
      }

      addDefinitions();
   }

   public void processOption(String option, TeXObject value)
    throws IOException
   {
      if (option.equals("maths"))
      {
         supportMaths=true;
      }
      else if (option.equals("nomaths"))
      {
         supportMaths=false;
      }
      else if (option.equals("theorems"))
      {
         supportTheorems=true;
      }
      else if (option.equals("notheorems"))
      {
         supportTheorems=false;
      }
      else if (option.equals("subfloats"))
      {
         supportSubFloats=true;
      }
      else if (option.equals("nosubfloats"))
      {
         supportSubFloats=false;
      }
   }

   public void newsubfloat(String floatname, String abbr)
   {
      String countername = "sub"+floatname;

      listener.newcounter(countername, null, "@alph");
      listener.addtoreset(countername, floatname);

      registerControlSequence(new SubFloat(floatname));

      String sublabel = String.format("sub%slabel", floatname);

      registerControlSequence(new SubFloatLabel(sublabel));

      TeXObjectList def = new TeXObjectList();
      def.add(listener.getParam(1));
      def.add(new TeXCsRef(sublabel));

      registerControlSequence(new GenericCommand(listener, true, 
        String.format("@%slabel", countername), 1, def));

      registerControlSequence(new JmlrSubFloatRef(
        String.format("sub%sref", abbr), floatname));
   }

   public void newtheorem(String envname, String counter, 
      String title, String outerCounter)
   {
      newtheorem(envname, counter, getListener().createString(title),
        outerCounter);
   }

   public void newtheorem(String envname, String counter, 
      TeXObject title, String outerCounter)
   {
      addTheoremControlSequence(envname, "body@font", 
         "@theorembodyfont");
      addTheoremControlSequence(envname, "header@font", 
         "@theoremheaderfont");
      addTheoremControlSequence(envname, "sep", "@theoremsep");
      addTheoremControlSequence(envname, "postheader", 
         "@theorempostheader");

      if (counter != null 
            && getParser().getSettings().getRegister("c@"+counter) == null)
      {
         getListener().newcounter(counter, outerCounter);
      }

      registerControlSequence(new JmlrTheorem(envname, counter, title));
   }

   protected void addTheoremControlSequence(String envname, String name1, 
    String name2)
   {
      TeXParser parser = getParser();

      ControlSequence cs = parser.getControlSequence(name2);

      TeXObjectList definition = null;

      if (cs instanceof GenericCommand)
      {
         definition = ((GenericCommand)cs).getDefinition();
      }
      else if (cs instanceof Expandable)
      {
         try
         {
            definition = ((Expandable)cs).expandonce(parser);
         }
         catch (IOException e)
         {
            getListener().getTeXApp().warning(getParser(), e.getMessage());
         }
      }

      if (definition == null)
      {
         definition = new TeXObjectList();
         definition.add(cs);
      }

      registerControlSequence(new GenericCommand(
       String.format("jmlr@thm@%s@%s", envname, name1), null, definition));
   }

   private boolean supportTheorems=true, supportMaths=true,
    supportSubFloats=true;
}
