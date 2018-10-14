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
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;
import java.io.EOFException;
import java.io.File;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Vector;
import java.util.Stack;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.file.Path;
import java.awt.Color;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.primitives.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.aux.*;
import com.dickimawbooks.texparserlib.latex.graphics.*;
import com.dickimawbooks.texparserlib.latex.amsmath.*;
import com.dickimawbooks.texparserlib.latex.tcilatex.*;
import com.dickimawbooks.texparserlib.latex.lipsum.*;
import com.dickimawbooks.texparserlib.latex.jmlr.*;
import com.dickimawbooks.texparserlib.latex.etoolbox.*;
import com.dickimawbooks.texparserlib.latex.hyperref.*;
import com.dickimawbooks.texparserlib.latex.natbib.*;
import com.dickimawbooks.texparserlib.latex.inputenc.*;
import com.dickimawbooks.texparserlib.latex.wasysym.*;
import com.dickimawbooks.texparserlib.latex.pifont.*;
import com.dickimawbooks.texparserlib.latex.booktabs.*;
import com.dickimawbooks.texparserlib.latex.textcase.*;
import com.dickimawbooks.texparserlib.latex.shortvrb.*;
import com.dickimawbooks.texparserlib.latex.probsoln.*;
import com.dickimawbooks.texparserlib.latex.bpchem.*;
import com.dickimawbooks.texparserlib.latex.xspace.*;
import com.dickimawbooks.texparserlib.latex.xfor.*;
import com.dickimawbooks.texparserlib.latex.siunitx.*;
import com.dickimawbooks.texparserlib.latex.mhchem.*;
import com.dickimawbooks.texparserlib.latex.stix.*;
import com.dickimawbooks.texparserlib.latex.textcomp.*;
import com.dickimawbooks.texparserlib.latex.mnsymbol.*;
import com.dickimawbooks.texparserlib.latex.fourier.*;
import com.dickimawbooks.texparserlib.latex.fontenc.*;
import com.dickimawbooks.texparserlib.latex.tipa.*;
import com.dickimawbooks.texparserlib.latex.upgreek.*;
import com.dickimawbooks.texparserlib.latex.datatool.*;
import com.dickimawbooks.texparserlib.latex.ifthen.*;
import com.dickimawbooks.texparserlib.latex.color.*;
import com.dickimawbooks.texparserlib.latex.mfirstuc.*;

public abstract class LaTeXParserListener extends DefaultTeXParserListener
{
   public LaTeXParserListener(Writeable writeable)
   {
      this(writeable, null, false);
   }

   public LaTeXParserListener(Writeable writeable, boolean parseAux)
   {
      this(writeable, null, true);
   }

   public LaTeXParserListener(Writeable writeable, Vector<AuxData> auxData)
   {
      this(writeable, auxData, false);
   }

   public LaTeXParserListener(Writeable writeable, Vector<AuxData> auxData, boolean parseAux)
   {
      this(writeable, auxData, parseAux, false);
   }

   public LaTeXParserListener(Writeable writeable, Vector<AuxData> auxData, 
     boolean parseAux, boolean parsePackages)
   {
      super(writeable);
      setAuxData(auxData);
      setParseAuxEnabled(parseAux);
      counters = new Hashtable<String,Vector<String>>();
      indexes = new Hashtable<String,IndexRoot>();
      this.parsePackages = parsePackages;

      footnotes = new TeXObjectList();

      loadedPackages = new Vector<LaTeXFile>();
      verbEnv = new Vector<String>();
   }

   public boolean isParseAuxEnabled()
   {
      return parseAux;
   }

   public void setParseAuxEnabled(boolean parseAux)
   {
      this.parseAux = parseAux;
   }

   public TeXObject getAnchor(String anchorName)
   {
      return null;
   }

   public TeXObject getCitation(TeXObject label)
      throws IOException
   {
      if (auxData == null)
      {
         return createUnknownReference(label);
      }

      return AuxData.getCitation(auxData, getParser(), label);
   }

   public TeXObject getReference(TeXObject label)
      throws IOException
   {
      if (auxData == null)
      {
         return createUnknownReference(label);
      }

      return AuxData.getReference(auxData, getParser(), label);
   }

   public TeXObject getPageReference(TeXObject label)
      throws IOException
   {
      if (auxData == null)
      {
         return createUnknownReference(label);
      }

      return AuxData.getPageReference(auxData, getParser(), label);
   }

   public TeXObject getNameReference(TeXObject label)
      throws IOException
   {
      if (auxData == null)
      {
         return createUnknownReference(label);
      }

      return AuxData.getNameReference(auxData, getParser(), label);
   }

   public TeXObject getHyperReference(TeXObject label)
      throws IOException
   {
      if (auxData == null)
      {
         return createUnknownReference(label);
      }

      return AuxData.getHyperReference(auxData, getParser(), label);
   }

   public TeXObject getLabelForLink(TeXObject link)
     throws IOException
   {
      if (auxData == null)
      {
         return null;
      }

      return AuxData.getLabelForLink(auxData, getParser(), link);
   }

   public void addVerbEnv(String name)
   {
      verbEnv.add(name);
   }

   public boolean isVerbEnv(String name)
   {
      return verbEnv.contains(name);
   }

   public boolean containsVerbatim(TeXObject object)
   {
      if (object instanceof VerbChar)
      {
         return true;
      }
      else if (object instanceof ControlSequence)
      {
         return getParser().isVerbCommand(((ControlSequence)object).getName());
      }
      else if (object instanceof TeXObjectList)
      {
         TeXObjectList list = (TeXObjectList)object;

         for (int i = 0; i < list.size(); i++)
         {
            object = list.get(i);

            if (object instanceof TeXObjectList)
            {
               if (containsVerbatim(object))
               {
                  return true;
               }
            }
            else if (object instanceof ControlSequence)
            {
               ControlSequence cs = (ControlSequence)object;
               String csName = cs.getName();

               if (getParser().isVerbCommand(csName))
               {
                  return true;
               }

               if (csName.equals("begin"))
               {
                  i++;
                  object = null;

                  while (i < list.size())
                  {
                     TeXObject nextObj = list.get(i);
                     i++;

                     if (!(nextObj instanceof Ignoreable))
                     {
                        object = nextObj;
                        break;
                     }
                  }

                  if (object == null)
                  {
                     return false;
                  }

                  if (object instanceof Group)
                  {
                     object = ((Group)object).toList();
                  }
                  else if (object instanceof BgChar)
                  {
                     TeXObjectList grp = new TeXObjectList();
                     i++;

                     while (i < list.size())
                     {
                        object = list.get(i);
                        i++;

                        if (object instanceof EgChar)
                        {
                           break;
                        }

                        grp.add(object);
                     }

                     object = grp;
                  }

                  String envName = object.toString(getParser());

                  if (isVerbEnv(envName))
                  {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   protected void addPredefined()
   {
      super.addPredefined();

      addVerbEnv("verbatim");
      addVerbEnv("verbatim*");
      addVerbEnv("filecontents");
      addVerbEnv("filecontents*");

      parser.putControlSequence(new Begin());
      parser.putControlSequence(new End());

      parser.putControlSequence(new DocumentClass());
      parser.putControlSequence(new UsePackage());

      if (parsePackages)
      {
         parser.putControlSequence(new LoadDocumentClass());
         parser.putControlSequence(
            new LoadDocumentClass("LoadClassWithOptions", true));
         parser.putControlSequence(new UsePackage("RequirePackage", false));
         parser.putControlSequence(
           new UsePackage("RequirePackageWithOptions", true));
         parser.putControlSequence(new PassOptionsToPackage());
         parser.putControlSequence(new PassOptionsToPackage(
            "PassOptionsToClass", "cls"));
         parser.putControlSequence(new ProvidesFile());
         parser.putControlSequence(new ProvidesPackage());
         parser.putControlSequence(new ProvidesPackage("ProvidesClass"));
         parser.putControlSequence(new DeclareOption());
         parser.putControlSequence(new ProcessOptions());
         parser.putControlSequence(new ExecuteOptions());

         parser.putControlSequence(new AddToHook("AtBeginDocument",
           "@begindocumenthook"));
         parser.putControlSequence(new AddToHook("AtEndDocument",
           "@enddocumenthook"));

         parser.putControlSequence(new AtIfPackageLoaded());
         parser.putControlSequence(new AtIfClassLoaded());

         parser.putControlSequence(new PackageError());
         parser.putControlSequence(new PackageError(
          "PackageErrorNoLine", LaTeXSyntaxException.PACKAGE_ERROR));

         parser.putControlSequence(new PackageError(
          "ClassError", LaTeXSyntaxException.CLASS_ERROR));
         parser.putControlSequence(new PackageError(
          "ClassErrorNoLine", LaTeXSyntaxException.CLASS_ERROR));
      }

      parser.putControlSequence(new NewCommand());
      parser.putControlSequence(new NewCommand("renewcommand",
        NewCommand.OVERWRITE_FORCE));
      parser.putControlSequence(new NewCommand("providecommand",
        NewCommand.OVERWRITE_SKIP));

      parser.putControlSequence(new Label());
      parser.putControlSequence(new Ref());
      parser.putControlSequence(new PageRef());
      parser.putControlSequence(new NameRef());
      parser.putControlSequence(new Input());
      parser.putControlSequence(new Input("@input", Input.NOT_FOUND_ACTION_WARN));
      parser.putControlSequence(new InputIfFileExists());
      parser.putControlSequence(new IfFileExists());
      parser.putControlSequence(new MakeAtLetter());
      parser.putControlSequence(new MakeAtOther());
      parser.putControlSequence(new Ignorespaces());
      parser.putControlSequence(new Centerline());
      parser.putControlSequence(new Verb());
      parser.putControlSequence(new Cr("\\"));
      parser.putControlSequence(new Cr("cr"));
      parser.putControlSequence(new TabularNewline());
      parser.putControlSequence(new MultiColumn());
      parser.putControlSequence(new Hline());
      parser.putControlSequence(new Cline());
      parser.putControlSequence(new EnsureMath());
      parser.putControlSequence(new Frac());
      parser.putControlSequence(new GenericCommand("@empty"));
      parser.putControlSequence(new SymbolCs());

      parser.putControlSequence(new AtFor());
      parser.putControlSequence(new AtIfNextChar());
      parser.putControlSequence(new AtFirstOfTwo());
      parser.putControlSequence(new AtSecondOfTwo());
      parser.putControlSequence(new AtFirstOfOne());
      parser.putControlSequence(new AtGobble());
      parser.putControlSequence(new AtGobbleTwo());
      parser.putControlSequence(new AtNameUse());

      parser.putControlSequence(new AtAlph("@Alph", AtAlph.UPPER));
      parser.putControlSequence(new AtAlph("@alph", AtAlph.LOWER));
      parser.putControlSequence(new AtRoman("@Roman", AtRoman.UPPER));
      parser.putControlSequence(new AtRoman("@roman", AtRoman.LOWER));

      parser.putControlSequence(new Verbatim());
      parser.putControlSequence(new Verbatim("verbatim*"));
      parser.putControlSequence(new Tabular());
      parser.putControlSequence(new Tabular("array"));
      parser.putControlSequence(new Bibliography());
      parser.putControlSequence(new BibliographyStyle());
      parser.putControlSequence(new BibItem());
      parser.putControlSequence(new Cite());
      parser.putControlSequence(new AddContentsLine());
      parser.putControlSequence(new Uppercase("MakeUppercase"));
      parser.putControlSequence(new Lowercase("MakeLowercase"));
      parser.putControlSequence(new Protect());
      parser.putControlSequence(new Index());
      parser.putControlSequence(new MakeIndex());
      parser.putControlSequence(new Appendix());

      bibliographySection = new TeXObjectList();
      bibliographySection.add(new TeXCsRef("section"));
      bibliographySection.add(getOther('*'));
      bibliographySection.add(new TeXCsRef("refname"));

      parser.putControlSequence(
        new GenericCommand("refname", null, createString("References")));

      parser.putControlSequence(
        new GenericCommand("contentsname", null, createString("Contents")));

      parser.putControlSequence(
        new GenericCommand("abstractname", null, createString("Abstract")));
      parser.putControlSequence(new AbstractDec());

      parser.putControlSequence(new Today());
      parser.putControlSequence(new StoreDataCs("title"));
      parser.putControlSequence(new StoreDataCs("author"));
      parser.putControlSequence(new StoreDataCs("date"));
      parser.putControlSequence(new GenericCommand("@date", null, new TeXCsRef("today")));

      parser.putControlSequence(
        new GenericCommand("figurename", null, createString("Figure")));
      parser.putControlSequence(
        new GenericCommand("tablename", null, createString("Table")));

      newlength("fboxsep", 3, TeXUnit.PT);
      newlength("fboxrule", 0.4f, TeXUnit.PT);

      parser.putControlSequence(new FrameBox());
      parser.putControlSequence(new MBox());
      parser.putControlSequence(new ParBox());
      parser.putControlSequence(new FrameBox("framebox"));
      parser.putControlSequence(new MBox("makebox"));
      parser.putControlSequence(new MBox("frame",
        FrameBox.BORDER_SOLID,
        new UserDimension(1, FixedUnit.BP), 
        new UserDimension(0, FixedUnit.BP)));

      newlength("tabcolsep", 6, TeXUnit.PT);
      newlength("arraycolsep", 5, TeXUnit.PT);

      newlength("linewidth", 100, new PercentUnit());
      newlength("textwidth", 100, new PercentUnit(PercentUnit.TEXT_WIDTH));
      newlength("textheight", 100, new PercentUnit(PercentUnit.TEXT_HEIGHT));
      newlength("columnwidth", 100, new PercentUnit(PercentUnit.COLUMN_WIDTH));
      newlength("columnheight", 100, new PercentUnit(PercentUnit.COLUMN_HEIGHT));
      newlength("paperwidth", 100, new PercentUnit(PercentUnit.PAPER_WIDTH));
      newlength("paperheight", 100, new PercentUnit(PercentUnit.PAPER_HEIGHT));
      newlength("marginparwidth", 100, new PercentUnit(PercentUnit.MARGIN_WIDTH));

      newtoks(true, "toks@");

      newcounter("part");
      newcounter("section");
      newcounter("subsection", "section");
      newcounter("subsubsection", "subsection");
      newcounter("paragraph", "subsubsection");
      newcounter("subparagraph", "paragraph");

      newcounter("part*");
      newcounter("section*");
      newcounter("subsection*", "section*");
      newcounter("subsubsection*", "subsection*");
      newcounter("paragraph*", "subsubsection*");
      newcounter("subparagraph*", "paragraph*");

      newcounter("figure");
      newcounter("table");
      newcounter("equation");
      newcounter("enumi");
      newcounter("enumii", null, "@alph");
      newcounter("enumiii", null, "@roman");
      newcounter("enumiv", null, "@Alph");

      newcounter("footnote");
      newcounter("mpfootnote");


      parser.getSettings().newcount("@listdepth");
      parser.getSettings().newcount("@enumdepth");
      parser.getSettings().newcount("@itemdepth");

      NewIf.createConditional(true, parser, "if@nmbrlist");

      parser.putControlSequence(new TrivListDec());
      parser.putControlSequence(new ListDec());
      parser.putControlSequence(new EnumerateDec());
      parser.putControlSequence(new ItemizeDec());
      parser.putControlSequence(new DescriptionDec());
      parser.putControlSequence(new GenericCommand(false, "descriptionlabel",
        new TeXObject[] {getParam(1)},
        new TeXObject[]
        {
           new TeXCsRef("hspace"),
           new TeXCsRef("labelsep"),
           new TeXCsRef("normalfont"),
           new TeXCsRef("bfseries"),
           getParam(1)
        }));
      parser.putControlSequence(new ListItem());
      parser.putControlSequence(new DescriptionItem());
      parser.putControlSequence(new UseCounter());

      parser.putControlSequence(
        new GenericCommand(true, "labelenumi", null, 
        new TeXObject[] {new TeXCsRef("theenumi"), getOther('.')}));

      parser.putControlSequence(
        new GenericCommand(true, "labelenumii", null, 
        new TeXObject[]
        {getOther('('), new TeXCsRef("theenumii"), getOther(')')}));

      parser.putControlSequence(
        new GenericCommand(true, "labelenumiii", null, 
        new TeXObject[] {new TeXCsRef("theenumiii"), getOther('.')}));

      parser.putControlSequence(
        new GenericCommand(true, "labelenumiv", null, 
        new TeXObject[] {new TeXCsRef("theenumiv"), getOther('.')}));

      parser.putControlSequence(
        new GenericCommand(true, "labelitemi", null, 
        new TeXObject[] {new TeXCsRef("textbullet")}));

      parser.putControlSequence(
        new GenericCommand(true, "labelitemii", null, 
        new TeXObject[]
        {
           new TeXCsRef("normalfont"),
           new TeXCsRef("bfseries"),
           new TeXCsRef("textendash")
        }));

      parser.putControlSequence(
        new GenericCommand(true, "labelitemiii", null, 
        new TeXObject[] {new TeXCsRef("textasteriskcentered")}));

      parser.putControlSequence(
        new GenericCommand(true, "labelitemiv", null, 
        new TeXObject[] {new TeXCsRef("textperiodcentered")}));

      parser.putControlSequence(
        new GenericCommand("@mpfn", null, createString("footnote")));
      parser.putControlSequence(
        new GenericCommand("thempfn", null, new TeXCsRef("thefootnote")));
      parser.putControlSequence(new Footnote());

      parser.putControlSequence(new MarginPar());
      parser.putControlSequence(new SwitchMarginSide("reversemarginpar", false));
      parser.putControlSequence(new SwitchMarginSide("normalmarginpar", true));

      parser.putControlSequence(new Thanks());
      parser.putControlSequence(new AtFnSymbol());

      parser.putControlSequence(new MathDeclaration("math"));

      MathDeclaration begMathDecl = new MathDeclaration("(");
      parser.putControlSequence(begMathDecl);
      parser.putControlSequence(new EndDeclaration(")", begMathDecl));
      parser.putControlSequence(
         new MathDeclaration("displaymath", TeXSettings.MODE_DISPLAY_MATH));

      MathDeclaration begDispDecl = new MathDeclaration("[", TeXSettings.MODE_DISPLAY_MATH);

      parser.putControlSequence(begDispDecl);
      parser.putControlSequence(new EndDeclaration("]", begDispDecl));
      parser.putControlSequence(
         new MathDeclaration("equation", TeXSettings.MODE_DISPLAY_MATH, true));

      // Math font commands

      addMathFontCommand("mathrm",TeXSettings.MATH_STYLE_RM);
      addMathFontCommand("mathsf",TeXSettings.MATH_STYLE_SF);
      addMathFontCommand("mathtt",TeXSettings.MATH_STYLE_TT);
      addMathFontCommand("mathit",TeXSettings.MATH_STYLE_IT);
      addMathFontCommand("mathbf",TeXSettings.MATH_STYLE_BF);
      addMathFontCommand("mathcal",TeXSettings.MATH_STYLE_CAL);
      addMathFontCommand("mathbb",TeXSettings.MATH_STYLE_BB);
      addMathFontCommand("mathfrak",TeXSettings.MATH_STYLE_FRAK);
      addMathFontCommand("boldsymbol",TeXSettings.MATH_STYLE_BOLDSYMBOL);
      addMathFontCommand("pmb",TeXSettings.MATH_STYLE_PMB);

      // Font declarations
      addFontWeightDeclaration("mdseries", "textmd", TeXSettings.WEIGHT_MD);
      addFontWeightDeclaration("bfseries", "textbf", TeXSettings.WEIGHT_BF);

      addFontFamilyDeclaration("rmfamily", "textrm", TeXSettings.FAMILY_RM);
      addFontFamilyDeclaration("sffamily", "textsf", TeXSettings.FAMILY_SF);
      addFontFamilyDeclaration("ttfamily", "texttt", TeXSettings.FAMILY_TT);

      addFontShapeDeclaration("upshape", "textup", TeXSettings.SHAPE_UP);
      addFontShapeDeclaration("itshape", "textit", TeXSettings.SHAPE_IT);
      addFontShapeDeclaration("slshape", "textsl", TeXSettings.SHAPE_SL);
      addFontShapeDeclaration("scshape", "textsc", TeXSettings.SHAPE_SC);

      addFontSizeDeclaration("normalsize", TeXSettings.SIZE_NORMAL);
      addFontSizeDeclaration("large", TeXSettings.SIZE_LARGE);
      addFontSizeDeclaration("Large", TeXSettings.SIZE_XLARGE);
      addFontSizeDeclaration("LARGE", TeXSettings.SIZE_XXLARGE);
      addFontSizeDeclaration("huge", TeXSettings.SIZE_HUGE);
      addFontSizeDeclaration("Huge", TeXSettings.SIZE_XHUGE);
      addFontSizeDeclaration("HUGE", TeXSettings.SIZE_XXHUGE);
      addFontSizeDeclaration("small", TeXSettings.SIZE_SMALL);
      addFontSizeDeclaration("footnotesize", TeXSettings.SIZE_FOOTNOTE);
      addFontSizeDeclaration("scriptsize", TeXSettings.SIZE_SCRIPT);
      addFontSizeDeclaration("tiny", TeXSettings.SIZE_TINY);

      parser.putControlSequence(
        new GenericCommand(true, "@spaces", null, 
         new TeXObject[]{new TeXCsRef("space"), new TeXCsRef("space"),
          new TeXCsRef("space"), new TeXCsRef("space")}));

      parser.putControlSequence(new GenericError());
      parser.putControlSequence(new DocumentStyle());
   }

   protected void addMathFontCommand(String name, int style)
   {
      parser.putControlSequence(new MathFontCommand(name, style));
   }

   public void registerControlSequence(LaTeXSty sty, ControlSequence cs)
   {
      parser.putControlSequence(cs);
   }

   public void newcommand(byte overwrite, 
     String type, String csName, boolean isShort,
     int numParams, TeXObject defValue, TeXObject definition)
   throws IOException
   {
      ControlSequence cs = getControlSequence(csName);

      if (cs instanceof Undefined)
      {
         if (overwrite == NewCommand.OVERWRITE_FORCE)
         {
            throw new TeXSyntaxException(parser,
             TeXSyntaxException.ERROR_UNDEFINED,
             String.format("%s%s", 
              new String(Character.toChars(parser.getEscChar())), csName));
         }
      }
      else
      {
         if (overwrite == NewCommand.OVERWRITE_FORBID)
         {
            throw new LaTeXSyntaxException(parser,
             LaTeXSyntaxException.ERROR_DEFINED,
             cs.toString(parser));
         }
         else if (overwrite == NewCommand.OVERWRITE_SKIP)
         {
            return;
         }
      }

      addLaTeXCommand(csName, isShort, numParams, defValue, definition);
   }

   public void addLaTeXCommand(String name, 
     boolean isShort, int numParams,
     TeXObject defValue, TeXObject definition)
     throws IOException
   {
      TeXObjectList defList;

      if (definition instanceof TeXObjectList)
      {
         defList = (TeXObjectList)definition;
      }
      else
      {
         defList = new TeXObjectList(1);
         defList.add(definition);
      }

      if (numParams == 0)
      {
         putControlSequence(true,// local
           new LaTeXGenericCommand(isShort, name, defList));
         return;
      }

      char[] syntax = new char[numParams];

      if (defValue == null)
      {
         syntax[0] = LaTeXGenericCommand.SYNTAX_MANDATORY;
      }
      else
      {
         syntax[0] = LaTeXGenericCommand.SYNTAX_OPTIONAL;
      }

      for (int i = 1; i < numParams; i++)
      {
         syntax[i] = LaTeXGenericCommand.SYNTAX_MANDATORY;
      }

      if (defValue == null)
      {
         putControlSequence(true,// local
           new LaTeXGenericCommand(isShort, name, syntax, defList));
      }
      else
      {
         putControlSequence(true,// local
           new LaTeXGenericCommand(isShort, name, syntax, defList,
             new TeXObject[]{defValue}));
      }
   }

   private void addFontWeightDeclaration(
       String declName, String textblockName, int weight)
   {
      Declaration decl = getFontWeightDeclaration(declName, weight);
      parser.putControlSequence(decl);
      parser.putControlSequence(new TextBlockCommand(textblockName, decl));
   }

   private void addFontShapeDeclaration(
       String declName, String textblockName, int shape)
   {
      Declaration decl = getFontShapeDeclaration(declName, shape);
      parser.putControlSequence(decl);
      parser.putControlSequence(new TextBlockCommand(textblockName, decl));
   }

   private void addFontSizeDeclaration(String name, int size)
   {
      parser.putControlSequence(getFontSizeDeclaration(name, size));
   }

   private void addFontFamilyDeclaration(
       String declName, String textblockName, int family)
   {
      Declaration decl =  getFontFamilyDeclaration(declName, family);
      parser.putControlSequence(decl);
      parser.putControlSequence(new TextBlockCommand(textblockName, decl));
   }

   public ControlSequence getTeXFontFamilyDeclaration(
      String name, int family)
   {
      ControlSequence decl = super.getTeXFontFamilyDeclaration(name, family);

      String newName = decl.getName()+"family";

      ControlSequence newDecl = getControlSequence(newName);

      return newDecl == null ? decl : new Obsolete(decl, newDecl);
   }

   public ControlSequence getTeXFontWeightDeclaration(
      String name, int weight)
   {
      ControlSequence decl = super.getTeXFontWeightDeclaration(name, weight);

      String newName = decl.getName()+"series";

      ControlSequence newDecl = getControlSequence(newName);

      return newDecl == null ? decl : new Obsolete(decl, newDecl);
   }

   public ControlSequence getTeXFontShapeDeclaration(
      String name, int shape)
   {
      if (name.equals("em"))
      {
         Declaration decl = getFontShapeDeclaration("em", TeXSettings.SHAPE_EM);
         putControlSequence(new TextBlockCommand("emph", decl));
         return decl;
      }

      ControlSequence decl = super.getTeXFontShapeDeclaration(name, shape);

      String newName = decl.getName()+"shape";

      ControlSequence newDecl = getControlSequence(newName);

      return newDecl == null ? decl : new Obsolete(decl, newDecl);
   }

   public FontWeightDeclaration getFontWeightDeclaration(String name, int weight)
   {
      return new FontWeightDeclaration(name, weight);
   }

   public FontSizeDeclaration getFontSizeDeclaration(String name, int size)
   {
      return new FontSizeDeclaration(name, size);
   }

   public FontShapeDeclaration getFontShapeDeclaration(String name, int shape)
   {
      return new FontShapeDeclaration(name, shape);
   }

   public FontFamilyDeclaration getFontFamilyDeclaration(String name, int family)
   {
      return new FontFamilyDeclaration(name, family);
   }

   public TeXObject createUnknownReference(String label)
   {
      return new UnknownReference(this, label);
   }

   public TeXObject createUnknownReference(TeXObject label)
   {
      return new UnknownReference(this, label);
   }

   public TeXCellAlignList createTeXCellAlignList(TeXObject colSpecs)
     throws IOException
   {
      return new TeXCellAlignList(getParser(), colSpecs);
   }

   public boolean isInDocEnv()
   {
      return docEnvFound;
   }

   public void setIsInDocEnv(boolean inDocEnv)
   {
      this.docEnvFound = inDocEnv;
   }

   public void beginDocument()
     throws IOException
   {
      if (isInDocEnv())
      {
         throw new LaTeXSyntaxException(
            parser,
            LaTeXSyntaxException.ERROR_MULTI_BEGIN_DOC);
      }

      getParser().getSettings().setCharMapMode(TeXSettings.CHAR_MAP_OFF);

      setIsInDocEnv(true);

      if (isParseAuxEnabled())
      {
         File auxFile = getAuxFile();

         if (auxFile != null && auxFile.exists())
         {
            AuxParser auxListener = new AuxParser(getTeXApp(), getCharSet());
            auxListener.parseAuxFile(auxFile);
            auxData = auxListener.getAuxData();
         }
      }

      ControlSequence cs = parser.getControlSequence(
        "@begindocumenthook");

      if (cs != null)
      {
         cs.process(parser);
      }
   }

   public void endDocument()
     throws IOException
   {
      if (!isInDocEnv())
      {
         throw new LaTeXSyntaxException(
            parser,
            LaTeXSyntaxException.ERROR_NO_BEGIN_DOC);
      }

      processFootnotes();

      ControlSequence cs = parser.getControlSequence(
        "@enddocumenthook");

      if (cs != null)
      {
         try
         {
            cs.process(parser);
         }
         catch (IOException e)
         {
            getTeXApp().error(e);
         }
      }

      throw new EOFException();
   }

   public void processFootnotes()
   throws IOException
   {
      if (footnotes.size() > 0)
      {
         doFootnoteRule();

         while (footnotes.size() > 0)
         {
            footnotes.pop().process(getParser());
            getPar().process(getParser());
         }
      }

   }

   public void doFootnoteRule() throws IOException
   {
   }

   public LaTeXCls getDocumentClass()
   {
      return docCls;
   }

   public KeyValList getDocumentClassOptions()
   {
      return docCls == null ? null : docCls.getOptions();
   }

   public boolean isClassLoaded(String name)
   {
      if (docCls == null)
      {
         return false;
      }

      if (docCls.getName().equals(name))
      {
         return true;
      }

      if (loadedClasses == null)
      {
         return false;
      }

      // may have been loaded by \LoadClass

      for (LaTeXCls cls : loadedClasses)
      {
         if (cls.getName().equals(name))
         {
            return true;
         }
      }

      return false;
   }

   public void loadclass(KeyValList options,
     String clsName, boolean loadParentOptions)
     throws IOException
   {
      if (docCls == null)
      {
         documentclass(options, clsName, loadParentOptions);
         return;
      }

      LaTeXCls cls = getLaTeXCls(options, clsName, loadParentOptions);

      addFileReference(cls);

      if (loadedClasses == null)
      {
         loadedClasses = new Vector<LaTeXCls>();
      }

      loadedClasses.add(cls);

      if (cls instanceof UnknownCls)
      {
         parsePackageFile(cls);
      }
      else
      {
         cls.processOptions();
      }
   }

   public void documentclass(KeyValList options,
     String clsName, boolean loadParentOptions)
     throws IOException
   {
      if (docCls != null)
      {
         throw new LaTeXSyntaxException(
            parser,
            LaTeXSyntaxException.ERROR_MULTI_CLS);
      }

      docCls = getLaTeXCls(options, clsName, loadParentOptions);

      addFileReference(docCls);

      if (docCls instanceof UnknownCls)
      {
         parsePackageFile(docCls);
      }
      else
      {
         docCls.processOptions();
      }
   }

   public LaTeXCls getLaTeXCls(KeyValList options, String clsName, 
     boolean loadParentOptions)
    throws IOException
   {
      if (clsName.equals("jmlr"))
      {
         return new JmlrCls(options, this, loadParentOptions);
      }

      if (clsName.equals("jmlrbook"))
      {
         return new JmlrBookCls(options, this, loadParentOptions);
      }

      return new UnknownCls(options, clsName, this, loadParentOptions);
   }

   public void removePackage(LaTeXSty sty)
   {
      removeFileReference(sty);
      loadedPackages.remove(sty);
   }

   public LaTeXSty requirepackage(String name)
   throws IOException
   {
      return requirepackage(null, name, false);
   }

   // returns file if already loaded
   public LaTeXSty requirepackage(KeyValList options, 
     String name, boolean loadParentOptions)
   throws IOException
   {
      LaTeXSty sty = getLoadedPackage(name);

      if (sty != null)
      {
         return sty;
      }

      sty = getLaTeXSty(options, name, loadParentOptions);

      addFileReference(sty);
      loadedPackages.add(sty);

      if (sty instanceof UnknownSty)
      {
         parsePackageFile(sty);
      }
      else
      {
         sty.processOptions();
      }

      return sty;
   }

   public void usepackage(LaTeXSty sty)
   {
      if (isStyLoaded(sty.getName()))
      {
         removePackage(sty);
      }

      if (sty instanceof FontEncSty)
      {
         fontEncSty = (FontEncSty)sty;
      }

      addFileReference(sty);
      loadedPackages.add(sty);
   }

   public boolean isParsePackageSupportOn()
   {
      return parsePackages;
   }

   public void setParsePackageSupport(boolean on)
   {
      parsePackages = on;
   }

   public void parsePackageFile(LaTeXSty sty) throws IOException
   {
      // If not found by kpsewhich then possibly a custom package/class
      // which might be simple enough to parse.
      // Otherwise ignore unknown class/packages

      if (parsePackages && !sty.wasFoundByKpsewhich())
      {
         sty.parseFile();
      }
   }

   // returns null if already loaded
   public LaTeXSty usepackage(KeyValList options, String styName, 
     boolean loadParentOptions)
   throws IOException
   {
      if (!isStyLoaded(styName))
      {
         LaTeXSty sty = getLaTeXSty(options, styName, loadParentOptions);

         addFileReference(sty);
         loadedPackages.add(sty);

         if (sty instanceof UnknownSty)
         {
            parsePackageFile(sty);
         }
         else
         {
            sty.processOptions();
         }

         return sty;
      }

      return null;
   }

   public LaTeXSty getLoadedPackage(String styName)
   {
      if (loadedPackages == null)
      {
         return null;
      }

      for (LaTeXFile lfile : loadedPackages)
      {
         if (lfile instanceof LaTeXSty)
         {
            LaTeXSty sty = (LaTeXSty)lfile;

            if (sty.getName().equals(styName))
            {
               return sty;
            }
         }
      }

      return null;
   }

   public FontEncSty getFontEncSty()
   {
      return fontEncSty;
   }

   protected LaTeXSty getLaTeXSty(KeyValList options, String styName, 
      boolean loadParentOptions)
   throws IOException
   {
      if (styName.equals("graphics")
        || styName.equals("graphicx")
        || styName.equals("epsfig"))
      {
         if (styName.equals("epsfig")
           && !isStyLoaded("graphicx"))
         {
            LaTeXFile lfile = new LaTeXFile(parser, null, "graphicx", "sty");

            addFileReference(lfile);
            loadedPackages.add(lfile);
         }

         return new GraphicsSty(options, styName, this, loadParentOptions);
      }

      if (styName.equals("amsmath"))
      {
         return new AmsmathSty(options, this, loadParentOptions);
      }

      if (styName.equals("amssymb"))
      {
         return new AmsSymbSty(options, this, loadParentOptions);
      }

      if (styName.equals("lipsum"))
      {
         return new LipsumSty(options, this, loadParentOptions);
      }

      if (styName.equals("etoolbox"))
      {
         return new EtoolboxSty(options, this, loadParentOptions);
      }

      if (styName.equals("hyperref"))
      {
         return new HyperrefSty(options, this, loadParentOptions);
      }

      if (styName.equals("inputenc"))
      {
         return new InputEncSty(options, this, loadParentOptions);
      }

      if (styName.equals("fontenc"))
      {
         fontEncSty = new FontEncSty(options, this, loadParentOptions);
         return fontEncSty;
      }

      if (styName.equals("natbib"))
      {
         return new NatbibSty(options, this, loadParentOptions);
      }

      if (styName.equals("wasysym"))
      {
         return new WasysymSty(options, this, loadParentOptions);
      }

      if (styName.equals("pifont"))
      {
         return new PifontSty(options, this, loadParentOptions);
      }

      if (styName.equals("booktabs"))
      {
         return new BooktabsSty(options, this, loadParentOptions);
      }

      if (styName.equals("textcase"))
      {
         return new TextCaseSty(options, this, loadParentOptions);
      }

      if (styName.equals("shortvrb"))
      {
         return new ShortVrbSty(options, this, loadParentOptions);
      }

      if (styName.equals("doc"))
      {
         return new DocSty(options, this, loadParentOptions);
      }

      if (styName.equals("probsoln"))
      {
         return new ProbSolnSty(options, this, loadParentOptions);
      }

      if (styName.equals("bpchem"))
      {
         return new BpChemSty(options, this, loadParentOptions);
      }

      if (styName.equals("siunitx"))
      {
         return new SIunitxSty(options, this, loadParentOptions);
      }

      if (styName.equals("mhchem"))
      {
         return new MhchemSty(options, this, loadParentOptions);
      }

      if (styName.equals("stix"))
      {
         return new StixSty(options, this, loadParentOptions);
      }

      if (styName.toLowerCase().equals("mnsymbol"))
      {
         return new MnSymbolSty(options, this, loadParentOptions);
      }

      if (styName.equals("textcomp"))
      {
         return new TextCompSty(options, this, loadParentOptions);
      }

      if (styName.equals("fourier"))
      {
         return new FourierSty(options, this, loadParentOptions);
      }

      if (styName.equals("upgreek"))
      {
         return new UpGreekSty(options, this, loadParentOptions);
      }

      if (styName.equals("tipa"))
      {
         return new TipaSty(options, this, loadParentOptions);
      }

      if (styName.equals("datatool"))
      {
         return new DataToolSty(options, this, loadParentOptions);
      }

      if (styName.equals("datatool-base"))
      {
         return new DataToolBaseSty(options, this, loadParentOptions);
      }

      if (styName.equals("mfirstuc"))
      {
         return new MfirstucSty(options, this, loadParentOptions);
      }

      if (styName.equals("mfirstuc-english"))
      {
         return new MfirstucEnglishSty(options, this, loadParentOptions);
      }

      if (styName.equals("ifthen"))
      {
         return new IfThenSty(options, this, loadParentOptions);
      }

      if (styName.equals("xspace"))
      {
         return new XspaceSty(options, this, loadParentOptions);
      }

      if (styName.equals("xfor"))
      {
         return new XforSty(options, this, loadParentOptions);
      }

      if (styName.equals("color") || styName.equals("xcolor"))
      {
         return new ColorSty(options, styName, this, loadParentOptions);
      }

      if (styName.equals("jmlrutils"))
      {
         return new JmlrUtilsSty(options, this, loadParentOptions);
      }

      if (styName.equals("jmlr2e"))
      {
         return new Jmlr2eSty(options, this, loadParentOptions);
      }

      return new UnknownSty(options, styName, this, loadParentOptions);
   }

   public abstract void substituting( 
    String original, String replacement)
     throws IOException;

   public abstract void includegraphics( 
     KeyValList options, String imgName)
     throws IOException;

   public boolean isStyLoaded(String name)
   {
      for (LaTeXFile f : loadedPackages)
      {
         if (f.getName().equals(name))
         {
            return true;
         }
      }

      return false;
   }

   public Vector<LaTeXFile> getLoadedPackages()
   {
      return loadedPackages;
   }

   public boolean input(TeXPath path)
     throws IOException
   {
      if (path.toString().endsWith("tcilatex.tex"))
      {
         usepackage(null, "amsmath", false);
         usepackage(null, "graphicx", false);
         addSpecialListener(new SWSpecialListener());

         parser.putControlSequence(new SWFrame());
         parser.putControlSequence(new Qcb());
         parser.putControlSequence(new BF());
         parser.putControlSequence(new NEG());
         parser.putControlSequence(new QATOP());
         parser.putControlSequence(new QTATOP());
         parser.putControlSequence(new QDATOP());
         parser.putControlSequence(new QABOVE());
         parser.putControlSequence(new QTABOVE());
         parser.putControlSequence(new QDABOVE());
         parser.putControlSequence(new QOVERD());
         parser.putControlSequence(new QTOVERD());
         parser.putControlSequence(new QDOVERD());
         parser.putControlSequence(new QATOPD());
         parser.putControlSequence(new QTATOPD());
         parser.putControlSequence(new QDATOPD());

         return true;
      }

      return super.input(path);
   }

   public boolean bibliography(TeXPath[] bibPaths, TeXPath bblPath)
    throws IOException
   {
      return (bblPath.exists() ? super.input(bblPath) : false);
   }

   public Charset getCharSet()
   {
      Charset charset = null;

      if (inputEncoding != null && !inputEncoding.equals("utf8"))
      {
         try
         {
            charset = InputEncSty.getCharSet(inputEncoding);
         }
         catch (IllegalCharsetNameException e)
         {
            getTeXApp().error(e);
            charset = null;
         }
      }

      return charset;
   }

   public void setGraphicsPath(TeXObjectList paths)
     throws IOException
   {
      graphicsPath = paths;
   }

   public TeXObjectList getGraphicsPath()
   {
      return graphicsPath;
   }

   public String getInputEncoding()
   {
      return inputEncoding;
   }

   public void setInputEncoding(String enc)
   {
      inputEncoding = enc;
   }

   protected File getImageFile(String[] grpaths, TeXPath path)
    throws IOException,InterruptedException
   {
      if (grpaths == null)
      {
         File file = path.getFile();

         if (file.exists())
         {
            return file;
         }

      }
      else
      {
         Path basePath = path.getBaseDir();

         for (int i = 0; i < grpaths.length; i++)
         {
            Path subPath = 
            (new File(File.separatorChar == '/' ?
              grpaths[i] : 
              grpaths[i].replaceAll("/", File.separator)
            ).toPath()).resolve(path.getRelative());

            File file = (basePath == null ?  subPath :
              basePath.resolve(subPath)).toFile();

            if (file.exists())
            {
               return file;
            }
         }
      }

      String name = getTeXApp().kpsewhich(path.getFileName().toString());

      if (name != null && !name.isEmpty())
      {
         return new File(name);
      }

      return null;
   }

   public String[] getGraphicsPaths()
     throws IOException
   {
      TeXObjectList graphicsPath = getGraphicsPath();

      String[] grpaths = null;

      if (graphicsPath != null && graphicsPath.size() > 0)
      {
         int n = graphicsPath.size();

         grpaths = new String[n];

         for (int i = 0; i < n; i++)
         {
            TeXObject object = graphicsPath.get(i);

            TeXObjectList expanded = null;

            if (object instanceof Expandable)
            {
               expanded = ((Expandable)object).expandfully(parser);
            }

            if (expanded != null)
            {
               grpaths[i] = expanded.toString(parser);
            }
            else
            {
               grpaths[i] = object.toString(parser);
            }
         }
      }

      return grpaths;
   }

   public File getImage(String[] grpaths, String imgName)
     throws IOException
   {
      try
      {
         if (imgName.contains("."))
         {
            TeXPath path = new TeXPath(parser, imgName);

            return getImageFile(grpaths, path);
         }
         else
         {
            for (int i = 0; i < IMAGE_EXT.length; i++)
            {
                String name = imgName+"."+IMAGE_EXT[i];

                TeXPath path = new TeXPath(parser, name);

                File file = getImageFile(grpaths, path);

                if (file != null)
                {
                   return file;
                }
            }
         }
      }
      catch (InterruptedException e)
      {
         getTeXApp().error(e);
      }

      return null;
   }

   public void cr(boolean isStar, TeXObject optArg)
     throws IOException
   {
      TeXSettings settings = getParser().getSettings();

      if (settings.getAlignMode() == TeXSettings.ALIGN_MODE_TRUE)
      {
         settings.startRow();
      }
   }

   public AlignRow createAlignRow(TeXObjectList stack)
     throws IOException
   {
      return new AlignRow(getParser(), stack);
   }

   public void setAuxData(Vector<AuxData> auxData)
   {
      this.auxData = auxData;
   }

   public Vector<AuxData> getAuxData()
   {
      return auxData;
   }

   public File getAuxFile()
   {
      return getAuxFile("aux");
   }

   public File getBblFile()
   {
      return getAuxFile("bbl");
   }

   public File getAuxFile(String ext)
   {
      if (getParser() == null) return null;

      File dir = getParser().getCurrentParentFile();

      if (dir == null) return null;

      String jobname = parser.getJobname();

      return new File(dir, jobname+"."+ext);
   }

   public void newcounter(String name)
   {
      newcounter(name, null);
   }

   public void newcounter(String name, String parent)
   {
      newcounter(name, parent, "number");
   }

   public void newcounter(String name, String parent, String format)
   {
      // counters are global
      parser.getSettings().newcount(false, "c@"+name);

      if (parent == null)
      {
         parser.putControlSequence(new GenericCommand(true, "the"+name, null,
             new TeXObject[] {new TeXCsRef(format), new TeXCsRef("c@"+name)}));

      }
      else
      {
         parser.putControlSequence(new GenericCommand(true, "the"+name, null,
             new TeXObject[] {
               new TeXCsRef("the"+parent),
               getOther('.'),
               new TeXCsRef("number"),
               new TeXCsRef("c@"+name)}));
         addtoreset(name, parent);
      }
   }

   public void addtoreset(String name, String parent)
   {
      Vector<String> dependents = counters.get(parent);

      if (dependents == null)
      {
         dependents = new Vector<String>();
         counters.put(parent, dependents);
      }

      dependents.add(name);
   }

   public int getcountervalue(String name)
    throws TeXSyntaxException,LaTeXSyntaxException
   {
      NumericRegister reg = parser.getSettings().getNumericRegister("c@"+name);

      if (reg == null)
      {
         throw new LaTeXSyntaxException(parser, 
            LaTeXSyntaxException.ERROR_UNDEFINED_COUNTER, name);
      }

      return reg.number(parser);
   }

   public void addtocounter(String name, Numerical value)
     throws TeXSyntaxException
   {
      parser.getSettings().globalAdvanceRegister("c@"+name, value);
   }

   public void setcounter(String name, Numerical value)
     throws TeXSyntaxException
   {
      parser.getSettings().globalSetRegister("c@"+name, value);
   }

   public void resetcounter(String name)
     throws TeXSyntaxException
   {
      setcounter(name, ZERO);
   }

   public void stepcounter(String name)
     throws TeXSyntaxException
   {
      addtocounter(name, ONE);

      Vector<String> dependents = counters.get(name);

      if (dependents != null)
      {
         for (String dep : dependents)
         {
            resetcounter(dep);
         }
      }
   }

   public TeXObjectList getBibliographySection()
   {
      return bibliographySection;
   }

   public void setBibliographySection(TeXObjectList object)
   {
      bibliographySection = object;
   }

   public void addToBibliographySection(TeXObject object)
   {
      bibliographySection.add(object);
   }

   public void addFootnote(TeXObject footnote)
   {
      footnotes.add(footnote);
   }

   public boolean isMarginRight()
   {
      return marginright;
   }

   public void setMarginRight(boolean isRight)
   {
      marginright = isRight;
   }

   public void marginpar(TeXObject leftText, TeXObject rightText)
     throws IOException
   {
   }

   public TeXObjectList getAuthor()
   {
      ControlSequence cs = getControlSequence("@author");

      if (cs instanceof GenericCommand)
      {
         return ((GenericCommand)cs).getDefinition();
      }

      return null;
   }

   // null argument indicates default index (mainIndex)
   // returns null if indexing is disabled or if index doesn't exist
   public IndexRoot getIndexRoot(String ref)
   {
      if (!isIndexingEnabled()) return null;

      return indexes.get(ref == null ? mainIndex : ref);
   }

   public IndexLocation createIndexLocation(String indexLabel)
    throws IOException
   {
      return new IndexLocation(new TeXCsRef("thepage"));
   }

   public void index(String ref, TeXObject arg) throws IOException
   {
      IndexRoot indexRoot = getIndexRoot(ref);

      if (indexRoot != null)
      {
         indexRoot.addEntry(getParser(), arg,
            createIndexLocation(ref == null ? mainIndex : ref));
      }
   }

   public float emToPt(float emValue)
   {
      getTeXApp().warning(getParser(),
        "Can't convert from em to pt, no font information loaded");

      // approximate

      float base = 10f;

      if (docCls == null)
      {
         KeyValList opts = docCls.getOptions();

         if (opts != null)
         {
            for (Iterator<String> it = opts.keySet().iterator(); it.hasNext();)
            {
               Matcher m = PTSIZE_PATTERN.matcher(it.next());

               if (m.matches())
               {
                  try
                  {
                     base = (float)Integer.parseInt(m.group(1));
                     break;
                  }
                  catch (NumberFormatException e)
                  {// this won't happen
                  }
               }
            }
         }
      }

      // TODO take into account font size change

      return base*0.95f*emValue;
   }

   public float exToPt(float exValue)
   {
      getTeXApp().warning(getParser(),
        "Can't convert from ex to pt, no font information loaded");

      // approximate!!!

      float base = 10f;

      if (docCls == null)
      {
         KeyValList opts = docCls.getOptions();

         if (opts != null)
         {
            for (Iterator<String> it = opts.keySet().iterator(); it.hasNext();)
            {
               Matcher m = PTSIZE_PATTERN.matcher(it.next());

               if (m.matches())
               {
                  try
                  {
                     base = (float)Integer.parseInt(m.group(1));
                     break;
                  }
                  catch (NumberFormatException e)
                  {// this won't happen
                  }
               }
            }
         }
      }

      // TODO take into account font size change

      return base*0.44f*exValue;
   }

   public boolean isIndexingEnabled()
   {
      return indexingEnabled;
   }

   public void enableIndexing(boolean enable)
   {
      enableIndexing(mainIndex, enable);
   }

   public void enableIndexing(String indexLabel, boolean enable)
   {
      indexingEnabled = enable;

      if (indexingEnabled)
      {
         IndexRoot index = getIndexRoot(indexLabel);

         if (index == null)
         {
            indexes.put(
              indexLabel == null ? mainIndex : indexLabel,
              index);
         }
      }
   }

   public void setMainIndexLabel(String label)
   {
      mainIndex = label;
   }

   public void startList(TrivListDec trivlist) throws IOException
   {
      trivListStack.push(trivlist);
   }

   public void endList(TrivListDec trivlist) throws IOException
   {
      trivListStack.pop();
   }

   public TrivListDec peekTrivListStack()
   {
      return trivListStack.peek();
   }

   public void startColor(Color color, boolean isForeground)
     throws IOException
   {
   }

   public void endColor(boolean isForeground)
     throws IOException
   {
   }

   public void startFrameBox(FrameBox fbox)
    throws IOException
   {
   }

   public void endFrameBox(FrameBox fbox)
    throws IOException
   {
   }

   public void startTheorem(String name) throws IOException
   {
      getPar().process(parser);
   }

   public void endTheorem(String name) throws IOException
   {
      getPar().process(parser);
   }

   public void setCurrentSty(LaTeXFile sty, String ext)
   {
      if (currentSty == null)
      {
         if (sty == null)
         {
            currentExt = null;
            return;
         }

         currentSty = new HashMap<String,LaTeXFile>();
      }

      if (sty == null)
      {
         currentSty.remove(ext);
         currentExt = null;
      }
      else
      {
         currentSty.put(ext, sty);
         currentExt = ext;

      }
   }

   public LaTeXFile getCurrentSty(String ext)
   {
      return currentSty == null ? null : currentSty.get(ext);
   }

   public String getCurrentExtension()
   {
      return currentExt;
   }

   public LaTeXFile getCurrentSty() throws IOException
   {
      String ext = getCurrentExtension();

      if (ext == null)
      {
         ControlSequence cs = parser.getControlSequence("@currext");

         if (cs instanceof Expandable)
         {
            TeXObjectList expanded;

            expanded = ((Expandable)cs).expandfully(parser);

            if (expanded != null)
            {
               ext = expanded.toString(parser);
            }
         }

         if (ext == null)
         {
            ext = getCurrentExtension();

            if (ext == null)
            {
               getTeXApp().warning(parser, getTeXApp().getMessage(
                    TeXSyntaxException.ERROR_UNEXPANDABLE, "@currext"));

               ext = "sty";
            }
         }
      }

      LaTeXFile sty = getCurrentSty(ext);

      if (sty == null)
      {
         ControlSequence cs = parser.getControlSequence("@currname");
         String name=null;

         if (cs instanceof Expandable)
         {
            TeXObjectList expanded;

            expanded = ((Expandable)cs).expandfully(parser);

            if (expanded != null)
            {
               name = expanded.toString(parser);
            }
         }

         if (name == null)
         {
            throw new LaTeXSyntaxException(parser, 
              getTeXApp().getMessage(
                 TeXSyntaxException.ERROR_UNEXPANDABLE, "@currname"));
         }

         sty = getLoadedPackage(name);

         if (sty == null)
         {
            throw new LaTeXSyntaxException(parser, 
              getTeXApp().getMessage(
                 LaTeXSyntaxException.ERROR_PACKAGE_NOT_LOADED, name));
         }
      }

      return sty;
   }

   public void passOptionsTo(String name, KeyValList options)
   {
      if (passOptions == null)
      {
         passOptions = new HashMap<String,KeyValList>();
      }
      else
      {
         KeyValList value = passOptions.get(name);

         if (value != null)
         {
            value.putAll(options);
            return;
         }
      }

      passOptions.put(name, options);
   }

   public KeyValList getPassedOptions(String name)
   {
      if (passOptions == null)
      {
         return null;
      }

      return passOptions.get(name);
   }

   private Vector<String> verbEnv;

   protected Vector<LaTeXFile> loadedPackages;
   protected Vector<LaTeXCls> loadedClasses;

   private Vector<AuxData> auxData;

   private Hashtable<String,Vector<String>> counters;

   protected LaTeXCls docCls;

   private HashMap<String,LaTeXFile> currentSty = null;

   private String currentExt = null;

   private HashMap<String,KeyValList> passOptions=null;

   private boolean parsePackages = false;

   private TeXObjectList graphicsPath = null;

   private boolean docEnvFound = false;

   private String inputEncoding = null;

   private FontEncSty fontEncSty = null;

   private boolean parseAux = false;

   private TeXObjectList bibliographySection;

   private TeXObjectList footnotes;

   private boolean marginright=true;

   private Hashtable<String,IndexRoot> indexes;

   private String mainIndex = "main";

   private boolean indexingEnabled = false;

   private Stack<TrivListDec> trivListStack = new Stack<TrivListDec>();

   public static final UserNumber ZERO = new UserNumber(0);
   public static final UserNumber ONE = new UserNumber(1);
   public static final UserNumber MINUS_ONE = new UserNumber(-1);

   public static final String[] IMAGE_EXT = new String[]
   {
      "pdf", "PDF", "png", "PNG", "jpg", "JPG", "jpeg", "JPEG",
      "eps", "EPS", "ps", "PS", "gif", "GIF"
   };

   public static final Pattern PTSIZE_PATTERN = Pattern.compile("(\\d+)pt");
}
